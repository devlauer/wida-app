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
package de.elnarion.web.wida.ejb.storageservice;

import java.io.InputStream;
import java.math.BigInteger;

/**
 * The Interface WidaContentService.
 */
public interface WidaStorageService {

	/**
	 * Saves file the file to the configured storage path and return an storageid.
	 *
	 * @param stream
	 *            the stream to save
	 * @param size
	 *            the size in bytes
	 * @return the string the storageid
	 * @throws WidaStorageServiceException
	 *             the wida storage service exception
	 */
	String saveFile(InputStream stream,BigInteger size)throws WidaStorageServiceException;



	
}
