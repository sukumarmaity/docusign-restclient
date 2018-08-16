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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import uk.co.techblue.docusign.client.Resource;
import uk.co.techblue.docusign.client.dto.CorrectionViewRequest;
import uk.co.techblue.docusign.client.dto.EnvelopeSenderViewRequest;
import uk.co.techblue.docusign.client.dto.recipients.RecipientViewRequest;
import uk.co.techblue.docusign.client.utils.DocuSignConstants;

/**
 * The Interface ConsoleViewResource.
 *
 */
@Path(DocuSignConstants.RESOURCE_CONTEXT_PATH)
public interface ConsoleViewResource extends Resource {

    /**
     * Gets the envelope sender view.
     *
     * @param envelopeId the envelope id
     * @param envelopeSenderViewRequest the envelope sender view request
     * @return the envelope sender view
     */
    @POST
    @Path("envelopes/{envelopeId}/views/sender")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getEnvelopeSenderView(@PathParam("envelopeId") String envelopeId, EnvelopeSenderViewRequest envelopeSenderViewRequest);

    /**
     * Gets the envelope recipient view.
     *
     * @param envelopeId the envelope id
     * @param recipientViewRequest the recipient view request
     * @return the envelope recipient view
     */
    @POST
    @Path("envelopes/{envelopeId}/views/recipient")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getEnvelopeRecipientView(@PathParam("envelopeId") String envelopeId, RecipientViewRequest recipientViewRequest);

    /**
     * Gets the envelope correction view.
     *
     * @param envelopeId the envelope id
     * @param correctionViewRequest the correction view request
     * @return the envelope correction view
     */
    @POST
    @Path("envelopes/{envelopeId}/views/correct")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getEnvelopeCorrectionView(@PathParam("envelopeId") String envelopeId, CorrectionViewRequest correctionViewRequest);

    /**
     * Gets the authentication view.
     *
     * @return the authentication view
     */
    @GET
    @Path("views/authentication")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes()
    public Response getAuthenticationView();
}
