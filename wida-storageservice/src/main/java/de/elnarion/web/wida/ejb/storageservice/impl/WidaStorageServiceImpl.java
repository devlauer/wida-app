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
package de.elnarion.web.wida.ejb.storageservice.impl;

import javax.annotation.Resource;
import javax.ejb.Stateless;

import org.xadisk.connector.outbound.XADiskConnectionFactory;

import de.elnarion.web.wida.ejb.storageservice.WidaStorageService;

/**
 * The Class WidaStorageServiceImpl.
 */
@Stateless
public class WidaStorageServiceImpl implements WidaStorageService {

	/** The xa connection factory. */
	@Resource(lookup = "java:/XADiskCF", type = org.xadisk.connector.outbound.XADiskConnectionFactory.class)
	private XADiskConnectionFactory xaConnectionFactory;


	/**
	 * Instantiates a new wida storage service impl.
	 */
	public WidaStorageServiceImpl() {
	}



	
	

	
}
