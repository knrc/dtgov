/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.dtgov.ui.server.api;

import java.lang.reflect.Constructor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.configuration.Configuration;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.auth.AuthenticationProvider;

/**
 * The class used whenever an S-RAMP Atom API request for data needs to be made.
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
public class SrampApiClientAccessor {

    @Inject
    private DtgovUIConfig config;

    private transient SrampAtomApiClient client;

	/**
	 * C'tor.
	 */
	public SrampApiClientAccessor() {
		String endpoint = (String) config.getConfiguration().getProperty("dtgov-ui.atom-api.endpoint");
        boolean validating = "true".equals(config.getConfiguration().getProperty("dtgov-ui.atom-api.validating"));
        AuthenticationProvider authProvider = null;
        String authProviderClass = (String) config.getConfiguration().getProperty("dtgov-ui.atom-api.authentication.provider");
        try {
            if (authProviderClass != null && authProviderClass.trim().length() > 0) {
                Class<?> c = Class.forName(authProviderClass);
                Constructor<?> constructor = null;
                try {
                    constructor = c.getConstructor(Configuration.class);
                    authProvider = (AuthenticationProvider) constructor.newInstance(config.getConfiguration());
                } catch (NoSuchMethodException e) {}
                try {
                    constructor = c.getConstructor();
                    authProvider = (AuthenticationProvider) constructor.newInstance();
                } catch (NoSuchMethodException e) {}
            }
            client = new SrampAtomApiClient(endpoint, authProvider, validating);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}

	/**
	 * @return the atom api client
	 */
	public SrampAtomApiClient getClient() {
	    return client;
	}

}
