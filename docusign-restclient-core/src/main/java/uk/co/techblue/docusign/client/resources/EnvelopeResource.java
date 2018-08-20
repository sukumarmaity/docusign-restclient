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
package uk.co.techblue.docusign.client.resources;

import java.io.File;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.james.mime4j.field.FieldName;
import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import uk.co.techblue.docusign.client.Resource;
import uk.co.techblue.docusign.client.dto.EnvelopeStatusQueryForm;
import uk.co.techblue.docusign.client.dto.StatusChangeRequest;
import uk.co.techblue.docusign.client.dto.VoidEnvelopeRequest;
import uk.co.techblue.docusign.client.dto.recipients.Signers;
import uk.co.techblue.docusign.client.utils.DocuSignConstants;

/**
 * The Interface EnvelopeResource.
 */
@Path(DocuSignConstants.RESOURCE_CONTEXT_PATH)
public interface EnvelopeResource extends Resource {

    /**
     * Response.
     *
     * @param envelopeId the envelope id
     * @param VoidEnvelopeRequest the void envelope request
     * @return the response
     */
    @PUT
    @Path("envelopes/{envelopeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response voidEnvelope(@PathParam("envelopeId") String envelopeId, VoidEnvelopeRequest VoidEnvelopeRequest);

    /**
     * Response.
     *
     * @param envelopeId the envelope id
     * @param statusChangeRequest the status change request
     * @return the response
     */
    @PUT
    @Path("envelopes/{envelopeId}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeStatus(@PathParam("envelopeId") String envelopeId, StatusChangeRequest statusChangeRequest);

    /**
     * Response.
     *
     * @param contentDisposition the content disposition
     * @param envelopeId the envelope id
     * @param documentId the document id
     * @param docFile the doc file
     * @return the response
     */
    @PUT
    @Path("envelopes/{envelopeId}/documents/{documentId}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response addDocumentToDraftEnvelope(@HeaderParam(FieldName.CONTENT_DISPOSITION) String contentDisposition, @PathParam("envelopeId") String envelopeId,
        @PathParam("documentId") String documentId, File docFile);

    /**
     * Gets the audit events.
     *
     * @param envelopeId the envelope id
     * @return the audit events
     */
    @GET
    @Path("envelopes/{envelopeId}/audit_events")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuditEvents(@PathParam("envelopeId") String envelopeId);

    /**
     * Gets the envelope.
     *
     * @param envelopeId the envelope id
     * @return the envelope
     */
    @GET
    @Path("envelopes/{envelopeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEnvelope(@PathParam("envelopeId") String envelopeId);

    /**
     * Response.
     *
     * @param formDataOutput the form data output
     * @return the response
     */
    @POST
    @Path("folders/draft")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response saveToDrafts(MultipartFormDataOutput formDataOutput);

    /**
     * Gets the envelope status.
     *
     * @param statusQueryForm the status query form
     * @return the envelope status
     */
    @GET
    @Path("envelopes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEnvelopeStatus(@Form EnvelopeStatusQueryForm statusQueryForm);

    /**
     * Gets the certificate.
     *
     * @param envelopeId the envelope id
     * @param watermark the watermark
     * @param certificate the certificate
     * @return the certificate
     */
    @GET
    @Path("envelopes/{envelopeId}/documents/certificate")
    public Response getCertificate(@PathParam("envelopeId") String envelopeId, @QueryParam("watermark") Boolean watermark, @QueryParam("certificate") Boolean certificate);

    /**
     * Gets the documents combined.
     *
     * @param envelopeId the envelope id
     * @return the documents combined
     */
    @GET
    @Path("envelopes/{envelopeId}/documents/combined")
    public Response getDocumentsCombined(@PathParam("envelopeId") String envelopeId);

    /**
     * Gets the documents info.
     *
     * @param envelopeId the envelope id
     * @return the documents info
     */
    @GET
    @Path("envelopes/{envelopeId}/documents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDocumentsInfo(@PathParam("envelopeId") String envelopeId);

    /**
     * Gets the document.
     *
     * @param envelopeId the envelope id
     * @param documentId the document id
     * @return the document
     */
    @GET
    @Path("/envelopes/{envelopeId}/documents/{documentId}")
    public Response getDocument(@PathParam("envelopeId") String envelopeId, @PathParam("documentId") String documentId);

    /**
     * Gets the custom fields.
     *
     * @param envelopeId the envelope id
     * @return the custom fields
     */
    @GET
    @Path("/envelopes/{envelopeId}/custom_fields")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomFields(@PathParam("envelopeId") String envelopeId);

    /**
     * Gets the notification info.
     *
     * @param envelopeId the envelope id
     * @return the notification info
     */
    @GET
    @Path("/envelopes/{envelopeId}/notification")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotificationInfo(@PathParam("envelopeId") String envelopeId);

    /**
     * Gets the recipient status.
     *
     * @param envelopeId the envelope id
     * @param includeTabs the include tabs
     * @param includeExtended the include extended
     * @return the recipient status
     */
    @GET
    @Path("/envelopes/{envelopeId}/recipients")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecipientStatus(@PathParam("envelopeId") String envelopeId, @QueryParam("include_tabs") Boolean includeTabs,
        @QueryParam("include_extended") Boolean includeExtended);

    /**
     * Response.
     *
     * @author : Amit Choudhary Created At: Aug 16, 2018, 1:26:18 PM
     * @param envelopeId the envelope id
     * @param resendEnvelope the resend envelope
     * @param signersList the signers list
     * @return the response
     */
    @PUT
    @Path("/envelopes/{envelopeId}/recipients")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resendEnvelope(@PathParam("envelopeId") String envelopeId, @QueryParam("resend_envelope") final boolean resendEnvelope, Signers signersList);

}
