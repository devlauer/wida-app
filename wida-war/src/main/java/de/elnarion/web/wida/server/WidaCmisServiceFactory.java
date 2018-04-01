/*******************************************************************************
 * Copyright 2018 dev.lauer@elnarion.de
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.elnarion.web.wida.server;

import java.math.BigInteger;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.impl.server.AbstractServiceFactory;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.server.support.wrapper.ConformanceCmisServiceWrapper;

/**
 * CMIS Service Factory.
 */
public class WidaCmisServiceFactory extends AbstractServiceFactory {

    /** Default maxItems value for getTypeChildren()}. */
    private static final BigInteger DEFAULT_MAX_ITEMS_TYPES = BigInteger.valueOf(1000);

    /** Default depth value for getTypeDescendants(). */
    private static final BigInteger DEFAULT_DEPTH_TYPES = BigInteger.valueOf(-1);

    /**
     * Default maxItems value for getChildren() and other methods returning
     * lists of objects.
     */
    private static final BigInteger DEFAULT_MAX_ITEMS_OBJECTS = BigInteger.valueOf(100000);

    /** Default depth value for getDescendants(). */
    private static final BigInteger DEFAULT_DEPTH_OBJECTS = BigInteger.valueOf(10);

    @Override
    public void init(Map<String, String> parameters) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public CmisService getService(CallContext context) {
        // get the user name and password that the CallContextHandler has determined
        // - if the user is null, this is either an anonymous request or the CallContextHandler configuration is wrong
        // - the password may be null depending on the authentication method
        String user = context.getUsername();
        String password = context.getPassword();

        // if the authentication fails, throw a CmisPermissionDeniedException

        // create a new service object
        // (can also be pooled or stored in a ThreadLocal)
        WidaCmisService service = new WidaCmisService();

        // add the conformance CMIS service wrapper
        // (The wrapper catches invalid CMIS requests and sets default values
        // for parameters that have not been provided by the client.)
        ConformanceCmisServiceWrapper wrapperService = 
                new ConformanceCmisServiceWrapper(service, DEFAULT_MAX_ITEMS_TYPES, DEFAULT_DEPTH_TYPES, 
                        DEFAULT_MAX_ITEMS_OBJECTS, DEFAULT_DEPTH_OBJECTS);

        // hand over the call context to the service object
        wrapperService.setCallContext(context);

        return wrapperService;
    }

}
