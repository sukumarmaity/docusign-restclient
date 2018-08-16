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
package uk.co.techblue.docusign.resteasy.providers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.mail.internet.ContentDisposition;
import javax.mail.internet.ParseException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.james.mime4j.field.FieldName;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.ProviderHelper;

import uk.co.techblue.docusign.client.dto.DocumentFile;

@Provider
@Produces("*/*")
@Consumes("*/*")
public class DocumentFileProvider implements MessageBodyReader<DocumentFile> {

    private static final String PREFIX = "pfx";

    private static final String SUFFIX = "sfx";

    private static final String PARAM_FILENAME = "filename";
    private static final String PARAM_DOCUMENT_ID = "documentId";

    private final String downloadDirectory = null; // by default temp dir, consider allowing it to be defined at runtime

    private final static Logger logger = Logger.getLogger(DocumentFileProvider.class);

    @Override
    public boolean isReadable(final Class<?> type, final Type genericType,
        final Annotation[] annotations, final MediaType mediaType) {
        return DocumentFile.class == type;
    }

    @Override
    public DocumentFile readFrom(final Class<DocumentFile> type, final Type genericType,
        final Annotation[] annotations, final MediaType mediaType,
        final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream)
            throws IOException {
        File downloadedFile = null;
        final String dispositionHeader = httpHeaders
            .getFirst(FieldName.CONTENT_DISPOSITION);
        ContentDisposition contentDisposition = null;
        try {
            contentDisposition = getContentDisposition(dispositionHeader);
        } catch (final ParseException pe) {
            throw new IOException(
                "Error occurred while parsing header. "
                    + FieldName.CONTENT_DISPOSITION + " : "
                    + dispositionHeader, pe);
        }
        final DocumentFile documentFile = new DocumentFile();
        String prefix = null;
        String suffix = null;
        if (contentDisposition != null) {
            setDocumentAttributes(contentDisposition, documentFile);
            suffix = getFileSuffix(contentDisposition, mediaType);
            prefix = getFilePrefix(contentDisposition);
        } else {
            logger.warn("Content Disposition header not found in response. All the attributes DocumentFile instance won't be populated.");
            suffix = getFileSuffix(mediaType);
        }
        if (StringUtils.isBlank(prefix)) {
            prefix = PREFIX;
        }
        if (StringUtils.isBlank(suffix)) {
            suffix = SUFFIX;
        }
        if (downloadDirectory != null) {
            try {
                downloadedFile = File.createTempFile(prefix, suffix, new File(
                    downloadDirectory));
            } catch (final IOException ex) {
                // could make this configurable, so we fail on fault rather than
                // default.
                logger.error("Could not bind to specified download directory "
                    + downloadDirectory + " so will use temp dir.");
            }
        }

        if (downloadedFile == null) {
            downloadedFile = File.createTempFile(prefix, suffix);
        }

        final OutputStream output = new BufferedOutputStream(new FileOutputStream(
            downloadedFile));

        try {
            ProviderHelper.writeTo(entityStream, output);
        } finally {
            output.close();
        }
        documentFile.setDocFile(downloadedFile);

        return documentFile;
    }

    private String getFilePrefix(final ContentDisposition contentDisposition) {
        final String fileName = contentDisposition.getParameter(PARAM_FILENAME);
        return StringUtils.substringBeforeLast(fileName, ".");
    }

    private void setDocumentAttributes(final ContentDisposition contentDisposition,
        final DocumentFile documentFile) {
        documentFile.setDocumentId(contentDisposition
            .getParameter(PARAM_DOCUMENT_ID));
        documentFile.setName(contentDisposition.getParameter(PARAM_FILENAME));
    }

    private String getFileSuffix(final ContentDisposition contentDisposition,
        final MediaType mediaType) {
        final String fileName = contentDisposition.getParameter(PARAM_FILENAME);
        return getFileSuffix(mediaType, fileName);
    }

    private String getFileSuffix(final MediaType mediaType) {
        return getFileSuffix(mediaType, null);
    }

    private String getFileSuffix(final MediaType mediaType, final String fileName) {
        String suffix = StringUtils.substringAfterLast(fileName, ".");
        final String mediaSubtype = StringUtils.defaultString(mediaType.getSubtype());
        final boolean suffixBlank = StringUtils.isBlank(suffix);
        final boolean mediaSubtypeBlank = StringUtils.isBlank(mediaSubtype);
        if (!suffixBlank && mediaSubtype.equalsIgnoreCase(suffix)) {
            suffix = "." + suffix;
        } else if (!suffixBlank && mediaSubtypeBlank) {
            suffix = "." + suffix;
        } else if (suffixBlank && !mediaSubtypeBlank) {
            suffix = "." + mediaSubtype;
        } else if (!suffixBlank && !mediaSubtypeBlank) {
            suffix = "." + suffix + "." + mediaSubtype;
        } else {
            suffix = ".pdf";
        }
        return suffix;
    }

    private ContentDisposition getContentDisposition(final String dispositionHeader)
        throws ParseException {
        if (StringUtils.isBlank(dispositionHeader)) {
            return null;
        }
        return new ContentDisposition(dispositionHeader);
    }

    public boolean isWriteable(final Class<?> type, final Type genericType,
        final Annotation[] annotations, final MediaType mediaType) {
        return File.class.isAssignableFrom(type); // catch subtypes
    }

    public long getSize(final File o, final Class<?> type, final Type genericType,
        final Annotation[] annotations, final MediaType mediaType) {
        return o.length();
    }

    public void writeTo(final File uploadFile, final Class<?> type, final Type genericType,
        final Annotation[] annotations, final MediaType mediaType,
        final MultivaluedMap<String, Object> httpHeaders,
        final OutputStream entityStream) throws IOException {
        final InputStream inputStream = new BufferedInputStream(new FileInputStream(
            uploadFile));

        try {
            ProviderHelper.writeTo(inputStream, entityStream);
        } finally {
            inputStream.close();
        }
    }

}
