/*******************************************************************************
 * Copyright 2012 Technology Blueprint Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package uk.co.techblue.docusign.client;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.techblue.docusign.client.credential.DocuSignCredentials;
import uk.co.techblue.docusign.client.utils.DocuSignUtils;
import uk.co.techblue.docusign.resteasy.providers.DocumentFileProvider;
import uk.co.techblue.docusign.route.DynamicProxyRoutePlanner;

/**
 * The HTTP client can be configured adding in the classpath the following properties file:
 * uk.co.techblue.docusign.client.DocuSignClient.properties The configuration allows to set the timeout and the maximum number
 * of connections per route: docusign.connection.timeout=20000 docusign.max.per.route=50 It can also configure a proxy:
 * docusign.https.proxyHost=<IP address or hostname of the proxy> docusign.https.proxyPort=listening port of the proxy
 */
public class DocuSignClient {

    private final static String PROXY_HOST_PROPERTY = "docusign.https.proxyHost";
    private final static String PROXY_PORT_PROPERTY = "docusign.https.proxyPort";
    private final static String CONNECTION_TIMEOUT = "docusign.connection.timeout";
    private static final String CONNECTION_DEFAULT_MAX_PER_ROUTE = "docusign.max.per.route";
    private final static String CONNECTION_URL = "docusign.connection.port.redirect";
    private static HttpClientConfiguration httpClientConfiguration;
    private final static Logger logger = LoggerFactory.getLogger(DocuSignClient.class);

    private static HttpClient client = null;

    static {
        initializeProviderFactory();
        httpClientConfiguration = new HttpClientConfiguration();
    }

    private static void initializeProviderFactory() {
        try {
            final ResteasyProviderFactory providerFactory = ResteasyProviderFactory.getInstance();
            registerResteasyProvider(providerFactory, DocumentFileProvider.class);
            RegisterBuiltin.register(providerFactory);
        } catch (final Exception e) {
            logger.error("Error occurred while registering custom resteasy providers", e);
        }
    }

    private static void registerResteasyProvider(final ResteasyProviderFactory providerFactory, final Class<?> providerClass) {
        final boolean registered = false; // EXARI: providerFactory.getClasses().getInstance(providerClass) != null;
        if (!registered) {
            providerFactory.registerProvider(providerClass);
            logger.info("Registered custom Provider with Resteasy:" + providerClass.getName());
        } else {
            logger.info("Provider is already registered with Resteasy. Ignoring registration request:" + providerClass.getName());
        }
    }

    @SuppressWarnings("unused")
    private static void initializeAutoScanProviderFactory() {
        try {
            final ResteasyProviderFactory providerFactory = ResteasyProviderFactory.getInstance();
            final Iterable<Class<?>> providerClasses = DocuSignUtils.getClasses("uk.co.techblue.docusign.resteasy.providers");
            for (final Class<?> provider : providerClasses) {
                if (provider.isAnnotationPresent(Provider.class)) {
                    providerFactory.registerProvider(provider);
                }
            }
            RegisterBuiltin.register(providerFactory);
        } catch (final ClassNotFoundException cnfe) {
            logger.error(
                "Error occurred while registering custom resteasy providers", cnfe);
        } catch (final IOException ioe) {
            logger.error(
                "Error occurred while registering custom resteasy providers", ioe);
        } catch (final Exception e) {
            logger.error(
                "Error occurred while registering custom resteasy providers", e);
        }
    }

    private static class HttpClientConfiguration {
        private ResourceBundle docusignClientBundle = null;

        private HttpClientConfiguration() {
            try {
                docusignClientBundle = ResourceBundle.getBundle(DocuSignClient.class.getCanonicalName());
            } catch (final MissingResourceException mre) {
                /* Ignore */
            }
        }

        private String getString(final String key, final String defaultValue) {
            String value = defaultValue;
            try {
                if (docusignClientBundle != null) {
                    value = docusignClientBundle.getString(key);
                }
            } catch (final MissingResourceException mre) {
                /* Ignore */
            }

            return value;
        }

        private int getInteger(final String key, final int defaultValue) {
            int value = defaultValue;
            try {
                if (docusignClientBundle != null) {
                    final String valueString = docusignClientBundle.getString(key);
                    value = Integer.parseInt(valueString);
                }
            } catch (final MissingResourceException mre) {
                /* Ignore */
            } catch (final NumberFormatException nfe) {
                /* Ignore */
                if (nfe.getMessage() != null) {
                    logger.debug(nfe.getMessage());
                }
            }

            return value;
        }

        private int getDefaultMaxPerRoute() {
            return getInteger(CONNECTION_DEFAULT_MAX_PER_ROUTE, 50);
        }

        private int getTimeout() {
            return getInteger(CONNECTION_TIMEOUT, 20000);
        }

        private int getProxyPort() {
            return getInteger(PROXY_PORT_PROPERTY, 0);
        }

        private String getProxyHost() {
            return getString(PROXY_HOST_PROPERTY, null);
        }

        private String getConnectionPortRedirect() {
            return getString(CONNECTION_URL, null);
        }
    }

    private static HttpClient getHttpClient() {
        if (client == null) {
            synchronized (DocuSignClient.class) {
                if (client == null) {
                    final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

                    final int maxPerRoute = httpClientConfiguration.getDefaultMaxPerRoute();
                    cm.setDefaultMaxPerRoute(maxPerRoute);
                    cm.setMaxTotal(maxPerRoute);

                    final int timeout = httpClientConfiguration.getTimeout();
                    final String proxyHost = httpClientConfiguration.getProxyHost();

                    final RequestConfig.Builder configBuilder = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout);
                    // Configure proxy info if necessary and defined
                    if (StringUtils.isNotBlank(proxyHost)) {
                        // Configure the host and port
                        final int port = httpClientConfiguration.getProxyPort();
                        final HttpHost proxy = new HttpHost(proxyHost, port);
                        configBuilder.setProxy(proxy);
                    }
                    final RequestConfig config = configBuilder.build();
                    client = HttpClientBuilder.create().setConnectionManager(cm).setDefaultRequestConfig(config).build();
                }
            }
        }

        return client;
    }

    /**
     * Gets the client service.
     * 
     * @param <T> the generic type
     * @param clazz the clazz
     * @param serverUri the server uri
     * @return the client service
     */
    public static <T> T getClientService(final Class<T> clazz, final String serverUri, final DocuSignCredentials credentials) {
        logger.info("Generating REST resource proxy for: " + clazz.getName());
        final String proxyHost = System.getProperty("proxy.host");
        final int proxyPort = NumberUtils.toInt(System.getProperty("proxy.port"));
        DynamicProxyRoutePlanner routePlanner = null;
        if (StringUtils.isNotBlank(proxyHost)) {
            final HttpHost httpProxy = new HttpHost(proxyHost, proxyPort);
            routePlanner = new DynamicProxyRoutePlanner(httpProxy);
        }

        final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setRoutePlanner(routePlanner);

        final HttpClient httpclient =
            HttpClients.custom().setRoutePlanner(routePlanner).setDefaultHeaders(credentials.getHeader()).build();

        final ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpclient);
        final ResteasyClientBuilder resteasyClientBuilder = new ResteasyClientBuilder().connectionPoolSize(20);
        final ResteasyClient client = resteasyClientBuilder.httpEngine(engine).build();
        final ResteasyWebTarget target = client.target(serverUri);
        return target.proxy(clazz);
    }
}
