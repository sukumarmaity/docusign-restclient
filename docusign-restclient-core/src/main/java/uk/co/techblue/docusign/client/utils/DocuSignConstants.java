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
package uk.co.techblue.docusign.client.utils;

/**
 * The Interface DocuSignConstants.
 * 
 * @author <a href="mailto:dheeraj.arora@techblue.co.uk">Dheeraj Arora</a>
 */
public interface DocuSignConstants {

    /** The header param authorization. */
    String HEADER_PARAM_AUTHORIZATION = "Authorization";

    /** The header param act as user. */
    String HEADER_PARAM_ACT_AS_USER = "X-DocuSign-Act-As-User";

    /** The resource context path. */
    String RESOURCE_CONTEXT_PATH = "/";

    /** The header param authentication. */
    String HEADER_PARAM_AUTHENTICATION = "X-DocuSign-Authentication";
}
