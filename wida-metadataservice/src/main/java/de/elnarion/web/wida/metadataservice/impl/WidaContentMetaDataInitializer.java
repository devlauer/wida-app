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
package de.elnarion.web.wida.metadataservice.impl;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import de.elnarion.web.wida.metadataservice.WidaContentMetaDataService;

/**
 * The Class WidaContentMetaDataInitializer is a small little helper ejb which is
 * started as singleton to get the root folder of this repository initialized.
 */
@Singleton
@Startup
@DependsOn("WidaTypeBaseMetaDataInitializer")
public class WidaContentMetaDataInitializer {

	/** The content meta data service. */
	@EJB
	private WidaContentMetaDataService contentMetaDataService;
	
	
	/**
	 * Inits the root folder.
	 */
	@PostConstruct
	public void initRootFolder()
	{
		// if the root folder is not initialized so far this call will initialize it
		contentMetaDataService.getRootFolder();
	}


	/**
	 * Gets the content meta data service.
	 *
	 * @return WidaContentMetaDataService - the content meta data service
	 */
	public WidaContentMetaDataService getContentMetaDataService() {
		return contentMetaDataService;
	}


	/**
	 * Sets the content meta data service.
	 *
	 * @param contentMetaDataService
	 *            the content meta data service
	 */
	public void setContentMetaDataService(WidaContentMetaDataService contentMetaDataService) {
		this.contentMetaDataService = contentMetaDataService;
	}

}
