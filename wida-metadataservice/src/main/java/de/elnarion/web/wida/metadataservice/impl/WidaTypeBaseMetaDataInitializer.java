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
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import de.elnarion.web.wida.metadataservice.WidaTypeMetaDataService;

/**
 * The Class WidaTypeBaseMetaDataInitializer is just a small helper ejb which is
 * started as singleton to get the root types of this repository initialized.
 */
@Singleton
@Startup
public class WidaTypeBaseMetaDataInitializer {

	/** The type meta data service. */
	@EJB
	private WidaTypeMetaDataService typeMetaDataService;

	/**
	 * Gets the type meta data service.
	 *
	 * @return WidaTypeMetaDataService - the type meta data service
	 */
	public WidaTypeMetaDataService getTypeMetaDataService() {
		return typeMetaDataService;
	}

	/**
	 * Sets the type meta data service.
	 *
	 * @param typeMetaDataService
	 *            the type meta data service
	 */
	public void setTypeMetaDataService(WidaTypeMetaDataService typeMetaDataService) {
		this.typeMetaDataService = typeMetaDataService;
	}

	/**
	 * Initialize all base types in the database if they are not there.
	 */
	@PostConstruct
	public void initialize() {

		// request all root types - if they are not defined so far they will get
		// initialized right now by the WidaTypeMetaDataServiceImpl
		typeMetaDataService.getRootTypes();
	}

}
