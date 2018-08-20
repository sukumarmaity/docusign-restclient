/*******************************************************************************
 * Copyright 2018, Techblue Software Pvt Ltd. All Rights Reserved.
 * No part of this content may be used without Techblue's express consent.
 ******************************************************************************/
package uk.co.techblue.docusign.route;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.HttpContext;

/**
 * The Class DynamicProxyRoutePlanner.
 *
 * @author <a href="mailto:amit.choudhary@techblue.co.uk">Amit Choudhary</a>
 */
public class DynamicProxyRoutePlanner implements HttpRoutePlanner {

    /** The default proxy route planner. */
    private DefaultProxyRoutePlanner defaultProxyRoutePlanner = null;

    /**
     * Instantiates a new dynamic proxy route planner.
     *
     * @param httpProxy the http proxy
     */
    public DynamicProxyRoutePlanner(HttpHost host) {
        defaultProxyRoutePlanner = new DefaultProxyRoutePlanner(host);
    }

    /**
     * Gets the default proxy route planner.
     *
     * @return the default proxy route planner
     */
    public DefaultProxyRoutePlanner getDefaultProxyRoutePlanner() {
        return defaultProxyRoutePlanner;
    }

    /**
     * Sets the default proxy route planner.
     *
     * @param defaultProxyRoutePlanner the new default proxy route planner
     */
    public void setDefaultProxyRoutePlanner(DefaultProxyRoutePlanner defaultProxyRoutePlanner) {
        this.defaultProxyRoutePlanner = defaultProxyRoutePlanner;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.http.conn.routing.HttpRoutePlanner#determineRoute(org.apache.http.HttpHost, org.apache.http.HttpRequest,
     * org.apache.http.protocol.HttpContext)
     */
    @Override
    public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        return defaultProxyRoutePlanner.determineRoute(target, request, context);
    }

}
