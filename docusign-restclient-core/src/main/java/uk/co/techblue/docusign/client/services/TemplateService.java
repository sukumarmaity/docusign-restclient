/*******************************************************************************
 * Copyright 2018, Techblue Software Pvt Ltd. All Rights Reserved.
 * No part of this content may be used without Techblue's express consent.
 ******************************************************************************/
package uk.co.techblue.docusign.client.services;

import javax.ws.rs.core.Response;

import uk.co.techblue.docusign.client.BaseService;
import uk.co.techblue.docusign.client.credential.DocuSignCredentials;
import uk.co.techblue.docusign.client.dto.Template;
import uk.co.techblue.docusign.client.dto.TemplateInfo;
import uk.co.techblue.docusign.client.dto.user.LoginAccount;
import uk.co.techblue.docusign.client.exception.ServiceInitException;
import uk.co.techblue.docusign.client.exception.TemplateException;
import uk.co.techblue.docusign.client.resources.TemplateResource;

/**
 * The Class TemplateService.
 */
public class TemplateService extends BaseService<TemplateResource> {

    /**
     * Instantiates a new template service.
     * 
     * @param serverUri the server uri
     * @param credentials the credentials
     * @throws ServiceInitException the service init exception
     */
    public TemplateService(String serverUri, DocuSignCredentials credentials) throws ServiceInitException {
        super(serverUri, credentials);
    }

    /**
     * Instantiates a new template service.
     * 
     * @param serverUri the server uri
     * @param credentials the credentials
     * @param loginAccount the login account
     * @throws ServiceInitException the service init exception
     */
    public TemplateService(String serverUri, DocuSignCredentials credentials, LoginAccount loginAccount)
        throws ServiceInitException {
        super(loginAccount, credentials);
    }

    /**
     * Retrieve templates.
     * 
     * @param credentials the credentials
     * @param accountId the account id
     * @return the template
     * @throws TemplateException the template exception
     */
    public TemplateInfo retrieveTemplates() throws TemplateException {
        Response clientResponse = resourceProxy.retrieveTemplates();
        return parseEntityFromResponse(clientResponse, TemplateInfo.class, TemplateException.class);
    }

    /**
     * Template.
     *
     * @param templateId the template id
     * @return the template
     * @throws TemplateException the template exception
     */
    public Template retrieveTemplate(String templateId) throws TemplateException {
        Response clientResponse = resourceProxy.retrieveTemplate(templateId);
        return parseEntityFromResponse(clientResponse, Template.class, TemplateException.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.techblue.docusign.client.Service#getResourceClass()
     */
    @Override
    protected Class<TemplateResource> getResourceClass() {
        return TemplateResource.class;
    }

}
