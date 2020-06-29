/*******************************************************************************
 * Copyright 2018, Techblue Software Pvt Ltd. All Rights Reserved.
 * No part of this content may be used without Techblue's express consent.
 ******************************************************************************/
package uk.co.techblue.docusign.client.services;

import java.util.ArrayList;
import java.util.List;

import javax.activation.FileDataSource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.james.mime4j.field.FieldName;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.OutputPart;

import uk.co.techblue.docusign.client.BaseService;
import uk.co.techblue.docusign.client.credential.DocuSignCredentials;
import uk.co.techblue.docusign.client.dto.AuditEventsResponse;
import uk.co.techblue.docusign.client.dto.CustomFields;
import uk.co.techblue.docusign.client.dto.Document;
import uk.co.techblue.docusign.client.dto.DocumentFile;
import uk.co.techblue.docusign.client.dto.DocumentInfo;
import uk.co.techblue.docusign.client.dto.Envelope;
import uk.co.techblue.docusign.client.dto.EnvelopeDetailInfo;
import uk.co.techblue.docusign.client.dto.EnvelopeDocumentInfo;
import uk.co.techblue.docusign.client.dto.EnvelopeInfo;
import uk.co.techblue.docusign.client.dto.EnvelopeNotificationInfo;
import uk.co.techblue.docusign.client.dto.EnvelopeStatusQueryForm;
import uk.co.techblue.docusign.client.dto.EnvelopeStatusResponse;
import uk.co.techblue.docusign.client.dto.StatusChangeRequest;
import uk.co.techblue.docusign.client.dto.VoidEnvelopeRequest;
import uk.co.techblue.docusign.client.dto.recipients.RecipientStatusCollection;
import uk.co.techblue.docusign.client.dto.recipients.RecipientUpdateResults;
import uk.co.techblue.docusign.client.dto.recipients.Signers;
import uk.co.techblue.docusign.client.dto.user.LoginAccount;
import uk.co.techblue.docusign.client.envelope.attributes.Status;
import uk.co.techblue.docusign.client.exception.EnvelopeException;
import uk.co.techblue.docusign.client.exception.ServiceInitException;
import uk.co.techblue.docusign.client.exception.SignatureRequestException;
import uk.co.techblue.docusign.client.resources.EnvelopeResource;
import uk.co.techblue.docusign.client.utils.DocuSignUtils;

/**
 * The Class Envelope Service.
 */
public class EnvelopeService extends BaseService<EnvelopeResource> {

    /**
     * Instantiates a new envelope service.
     * 
     * @param serverUri the server uri
     * @param credentials the credentials
     * @throws ServiceInitException the service init exception
     */
    public EnvelopeService(final String serverUri, final DocuSignCredentials credentials)
        throws ServiceInitException {
        super(serverUri, credentials);
    }

    /**
     * Instantiates a new envelope service.
     * 
     * @param credentials the credentials
     * @param loginAccount the login account
     * @throws ServiceInitException the service init exception
     */
    public EnvelopeService(final DocuSignCredentials credentials,
        final LoginAccount loginAccount) throws ServiceInitException {
        super(loginAccount, credentials);
    }

    /**
     * Send from drafts.
     * 
     * @param credentials the credentials
     * @param accountId the account id
     * @param envelopeId the envelope id
     * @param statusChangeReason the status change reason
     * @throws EnvelopeException the envelope exception
     */
    public void sendFromDrafts(final String envelopeId, final String statusChangeReason)
        throws EnvelopeException {
        final StatusChangeRequest statusChangeRequest = new StatusChangeRequest();
        statusChangeRequest.setStatus(Status.sent);
        statusChangeRequest.setStatusReason(statusChangeReason);
        final Response clientResponse = resourceProxy.changeStatus(envelopeId, statusChangeRequest);
        validateResponseAndReleaseConnection(clientResponse, EnvelopeException.class);
    }

    /**
     * Change envelope status.
     * 
     * @param credentials the credentials
     * @param envelopeId the envelope id
     * @param statusChangeRequest the status change request
     * @throws EnvelopeException the envelope exception
     */
    public void changeStatus(final String envelopeId,
        final StatusChangeRequest statusChangeRequest) throws EnvelopeException {
        final Response clientResponse = resourceProxy.changeStatus(envelopeId, statusChangeRequest);
        validateResponseAndReleaseConnection(clientResponse, EnvelopeException.class);
    }

    /**
     * Change envelope status.
     * 
     * @param envelopeId the envelope id
     * @throws EnvelopeException the envelope exception
     */
    public void voidEnvelope(final String envelopeId,
        final VoidEnvelopeRequest voidEnvelopeRequest) throws EnvelopeException {
        final Response clientResponse = resourceProxy.voidEnvelope(envelopeId, voidEnvelopeRequest);
        validateResponseAndReleaseConnection(clientResponse, EnvelopeException.class);
    }

    /**
     * Change envelope status.
     * 
     * @param envelopeId the envelope id
     * @throws EnvelopeException the envelope exception
     */
    public AuditEventsResponse getAuditEvents(final String envelopeId) throws EnvelopeException {

        final Response clientResponse = resourceProxy.getAuditEvents(envelopeId);

        return parseEntityFromResponse(clientResponse, AuditEventsResponse.class, EnvelopeException.class);
    }

    /**
     * Adds a document to an existing envelope.
     * 
     * @param envelopeId the envelope id
     * @param document the document
     * @throws EnvelopeException the envelope exception
     */
    public void addDocumentToDraftEnvelope(final String envelopeId, final Document document)
        throws EnvelopeException {
        final FileDataSource fileDataSource = new FileDataSource(document.getPath());
        final String contentDisposition = DocuSignUtils
            .getContentDispositionHeader(document);
        Response clientResponse;
        clientResponse = resourceProxy.addDocumentToDraftEnvelope(contentDisposition, envelopeId, document.getDocumentId()
            .toString(), fileDataSource.getFile());
        validateResponseAndReleaseConnection(clientResponse, EnvelopeException.class);
    }

    /**
     * Gets the envelope.
     * 
     * @param envelopeId the envelope id
     * @return the envelope
     * @throws EnvelopeException the envelope exception
     */
    public EnvelopeDetailInfo getEnvelope(final String envelopeId) throws EnvelopeException {
        final Response clientResponse = resourceProxy.getEnvelope(envelopeId);
        return parseEntityFromResponse(clientResponse, EnvelopeDetailInfo.class, EnvelopeException.class);
    }

    /**
     * Save to drafts.
     * 
     * @param envelope the envelope
     * @return the string
     * @throws SignatureRequestException the signature request exception
     */
    public String saveToDrafts(final Envelope envelope) throws SignatureRequestException {
        final MultipartFormDataOutput dataOut = generateMultipartFormDataOutput(envelope);
        final Response clientResponse = resourceProxy.saveToDrafts(dataOut);
        return parseEntityFromResponse(clientResponse, String.class, SignatureRequestException.class);
        // return parseEntityFromResponse(clientResponse, String.class, SignatureRequestException.class).get("envelopeId");
    }

    /**
     * Generates multipart form data output.
     * 
     * @param envelope the envelope
     * @return the multipart form data output
     */
    private MultipartFormDataOutput generateMultipartFormDataOutput(final Envelope envelope) {
        final MultipartFormDataOutput dataOut = new MultipartFormDataOutput();
        dataOut.addFormData("request-type", "Save envelope as draft", MediaType.APPLICATION_JSON_TYPE);
        dataOut.addFormData("envelope_definition", envelope, MediaType.APPLICATION_JSON_TYPE);
        // metadataPart.getHeaders().putSingle(FieldName.CONTENT_DISPOSITION, "form-data");
        for (final DocumentInfo document : envelope.getDocuments()) {
            final FileDataSource dataSource = new FileDataSource(document.getPath());
            final OutputPart filePart = dataOut.addFormData(document.getName(), dataSource, MediaType.valueOf(dataSource.getContentType()));
            filePart.getHeaders().putSingle(FieldName.CONTENT_DISPOSITION, DocuSignUtils.getContentDispositionHeader(document));
        }
        return dataOut;
    }

    /**
     * Gets the envelope status changes.
     * 
     * @param statusQueryForm the status query form
     * @return the envelope status changes
     * @throws EnvelopeException the envelope exception
     */
    public EnvelopeStatusResponse getEnvelopeStatus(
        final EnvelopeStatusQueryForm statusQueryForm) throws EnvelopeException {
        final Response clientResponse = resourceProxy.getEnvelopeStatus(statusQueryForm);
        return parseEntityFromResponse(clientResponse, EnvelopeStatusResponse.class, EnvelopeException.class);
    }

    /**
     * Gets the envelope status changes.
     * 
     * @param statusQueryForm the status query form
     * @return the envelope status changes
     * @throws EnvelopeException the envelope exception
     */
    public EnvelopeInfo getEnvelopeStatus(final String envelopeId)
        throws EnvelopeException {
        final List<String> envelopeIds = new ArrayList<String>();
        envelopeIds.add(envelopeId);
        final EnvelopeStatusQueryForm statusQueryForm = new EnvelopeStatusQueryForm();
        statusQueryForm.setEnvelopeIds(envelopeIds);
        final Response clientResponse = resourceProxy
            .getEnvelopeStatus(statusQueryForm);
        final EnvelopeStatusResponse statusResponse = parseEntityFromResponse(clientResponse, EnvelopeStatusResponse.class, EnvelopeException.class);
        if (statusResponse != null && statusResponse.getEnvelopes() != null
            && statusResponse.getEnvelopes().size() > 0) {
            return statusResponse.getEnvelopes().get(0);
        }
        return null;
    }

    /**
     * This retrieves a PDF containing the combined content of all documents and the certificate.
     * 
     * @param envelopeId the envelope id
     * @return the certificate
     * @throws EnvelopeException the envelope exception
     */
    public DocumentFile getCertificate(final String envelopeId) throws EnvelopeException {
        final Response clientResponse = resourceProxy.getCertificate(envelopeId, null, null);
        return parseEntityFromResponse(clientResponse, DocumentFile.class, EnvelopeException.class);
    }

    /**
     * This retrieves a PDF containing the combined content of all documents and the certificate.
     * 
     * 
     * @param envelopeId the envelope id
     * @param watermark the watermark. If the account has the watermark feature enabled and the envelope is not complete, the
     *        watermark for the account is added to the PDF documents. This option can remove the watermark.
     * 
     * @param certificate the certificate. This option can remove the envelope signing certificate from the download.
     * 
     * @return the certificate
     * @throws EnvelopeException the envelope exception
     */
    public DocumentFile getCertificate(final String envelopeId, final Boolean watermark, final Boolean certificate) throws EnvelopeException {
        final Response clientResponse = resourceProxy.getCertificate(envelopeId, watermark, certificate);
        return parseEntityFromResponse(clientResponse, DocumentFile.class, EnvelopeException.class);
    }

    /**
     * Gets the documents combined.
     * 
     * @param envelopeId the envelope id
     * @return the documents combined
     * @throws EnvelopeException the envelope exception
     */
    public DocumentFile getDocumentsCombined(final String envelopeId) throws EnvelopeException {
        final Response clientResponse = resourceProxy.getDocumentsCombined(envelopeId);
        return parseEntityFromResponse(clientResponse, DocumentFile.class, EnvelopeException.class);
    }

    /**
     * Gets the documents info.
     * 
     * @param envelopeId the envelope id
     * @return the documents info
     * @throws EnvelopeException the envelope exception
     */
    public EnvelopeDocumentInfo getDocumentsInfo(final String envelopeId) throws EnvelopeException {
        final Response clientResponse = resourceProxy.getDocumentsInfo(envelopeId);
        return parseEntityFromResponse(clientResponse, EnvelopeDocumentInfo.class, EnvelopeException.class);
    }

    /**
     * Gets the document.
     * 
     * @param envelopeId the envelope id
     * @param documentId the document id
     * @return the document
     * @throws EnvelopeException the envelope exception
     */
    public DocumentFile getDocument(final String envelopeId, final String documentId) throws EnvelopeException {
        final Response clientResponse = resourceProxy.getDocument(envelopeId, documentId);
        return parseEntityFromResponse(clientResponse, DocumentFile.class, EnvelopeException.class);
    }

    /**
     * Gets information about the custom fields included in the envelope.
     * 
     * @param envelopeId the envelope id
     * @return the custom fields
     * @throws EnvelopeException the envelope exception
     */
    public CustomFields getCustomFields(final String envelopeId) throws EnvelopeException {
        final Response clientResponse = resourceProxy.getCustomFields(envelopeId);
        return parseEntityFromResponse(clientResponse, CustomFields.class, EnvelopeException.class);
    }

    /**
     * Gets the the reminder and expiration information for the envelope.
     * 
     * 
     * @param envelopeId the envelope id
     * @return the notification info
     * @throws EnvelopeException the envelope exception
     */
    public EnvelopeNotificationInfo getNotificationInfo(final String envelopeId) throws EnvelopeException {
        final Response clientResponse = resourceProxy.getNotificationInfo(envelopeId);
        return parseEntityFromResponse(clientResponse, EnvelopeNotificationInfo.class, EnvelopeException.class);
    }

    /**
     * Gets the status for all recipients of a single envelope.
     * 
     * @param envelopeId the envelope id
     * @return the recipient status
     * @throws EnvelopeException the envelope exception
     */
    public RecipientStatusCollection getRecipientStatus(final String envelopeId) throws EnvelopeException {
        final Response clientResponse = resourceProxy.getRecipientStatus(envelopeId, null, null);
        return parseEntityFromResponse(clientResponse, RecipientStatusCollection.class, EnvelopeException.class);
    }

    /**
     * Gets the he status for all recipients of a single envelope.
     * 
     * @param envelopeId the envelope id
     * @param includeTabs If set to true, the tabs associated with the recipient are returned.
     * @param includeExtended If set to true, the extended properties are returned
     * 
     * @return the recipient status
     * @throws EnvelopeException the envelope exception
     */
    public RecipientStatusCollection getRecipientStatus(final String envelopeId, final Boolean includeTabs, final Boolean includeExtended)
        throws EnvelopeException {
        final Response clientResponse = resourceProxy.getRecipientStatus(envelopeId, includeTabs, includeExtended);
        return parseEntityFromResponse(clientResponse, RecipientStatusCollection.class, EnvelopeException.class);
    }

    /**
     * Modify or Correct and Resend Recipient Information.
     * 
     * @param envelopeId the envelope id
     * @param signersList the list of signers
     * @throws EnvelopeException the envelope exception
     */
    public RecipientUpdateResults resendEnvelope(final String envelopeId, final boolean resendEnvelope, final Signers signersList) throws EnvelopeException {
        final Response clientResponse = resourceProxy.resendEnvelope(envelopeId, resendEnvelope, signersList);
        return parseEntityFromResponse(clientResponse, RecipientUpdateResults.class, EnvelopeException.class);
    }
    
    /**
     * Gets the recipient signature.
     *
     * @param envelopeId the envelope id
     * @param recipientId the recipient id
     * @return the recipient signature
     * @throws EnvelopeException the envelope exception
     */
    public String getRecipientSignature(final String envelopeId, final String recipientId) throws EnvelopeException {
        final Response clientResponse = resourceProxy.getRecipientSignature(envelopeId, recipientId);
        return parseEntityFromResponse(clientResponse, String.class, EnvelopeException.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.techblue.docusign.client.services.BaseService#getResourceClass()
     */
    @Override
    protected Class<EnvelopeResource> getResourceClass() {
        return EnvelopeResource.class;
    }
}
