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
package de.elnarion.web.wida.metadataservice;

import java.util.Map;

import javax.ejb.Local;

import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.PropertyData;

import de.elnarion.web.wida.metadataservice.domain.contentmetadata.Document;
import de.elnarion.web.wida.metadataservice.domain.contentmetadata.Folder;

/**
 * The Interface WidaContentMetaDataService.
 */
@Local
public interface WidaContentMetaDataService {

	/**
	 * Gets the root folder for this repository.
	 *
	 * @return Folder - the root folder
	 */
	public Folder getRootFolder();

	/**
	 * Gets the folder.
	 *
	 * @param folderId
	 *            the folder id
	 * @return Folder - the folder
	 */
	public Folder getFolder(String folderId);

	/**
	 * Creates the document.
	 *
	 * @param document
	 *            the document
	 * @param properties
	 *            the properties
	 * @return the string - the object id of the new document
	 */
	public String createDocument(Document document,  Properties properties);

	/**
	 * Gets the document.
	 *
	 * @param sourceId
	 *            the source id
	 * @return Document - the document
	 */
	public Document getDocument(String sourceId);

	/**
	 * Creates the folder.
	 *
	 * @param folder
	 *            the folder
	 * @param properties
	 *            the properties
	 * @return the string
	 */
	public String createFolder(Folder folder, Properties properties);
}
