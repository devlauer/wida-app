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
package de.elnarion.web.wida.common.ejbhelper;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * The Class PropertiesResourceProducer.
 */
public class PropertiesResourceProducer {

	/**
	 * Instantiates a new properties resource producer.
	 */
	public PropertiesResourceProducer() {
	}

	/**
	 * Load properties.
	 *
	 * @param paramInjectionPoint
	 *            the param injection point
	 * @return the properties
	 */
	@Produces
    @PropertiesResource
    Properties loadProperties(InjectionPoint paramInjectionPoint) {
        PropertiesResource annotation = paramInjectionPoint.getAnnotated().getAnnotation(
                PropertiesResource.class);
        String fileName = annotation.name();
        // if no specific file is requested return a empty properties object
        if(fileName!=null&&fileName.equals(PropertiesResource.UNKOWN))
        	return new Properties();
        Properties props = null;
        // Load the properties from file
        URL url = null;
        url = Thread.currentThread().getContextClassLoader()
                .getResource(fileName);
        if (url != null) {
            props = new Properties();
            try {
                props.load(url.openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return props;
    }	
}
