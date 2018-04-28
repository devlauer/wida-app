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

import org.apache.chemistry.opencmis.commons.exceptions.CmisNameConstraintViolationException;

import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.metadataservice.WidaMetaDataConstants;

/**
 * The Class TypeHelper is a helper class which provides several static helper
 * methods for types.
 */
public class TypeHelper implements WidaErrorConstants {

	/**
	 * Convert type id to table or column name.
	 *
	 * @param paramTypeId
	 *            the param type id
	 * @return the string
	 */
	public static String convertTypeIdToTableOrColumnName(String paramTypeId) {
		// remove all non alphanumeric
		if (paramTypeId != null) {
			paramTypeId = paramTypeId.toLowerCase().replaceAll("[^A-Za-z0-9]", "");
			if (paramTypeId.length() > WidaMetaDataConstants.MAX_NAME_LENGTH) {
				throw new CmisNameConstraintViolationException(
						"The alphanumeric part of the id should not be bigger than 30 characters!",
						WidaErrorConstants.CONSTRAINT_NAME_TOO_BIG);
			}
			paramTypeId = paramTypeId.toUpperCase();
		}
		return paramTypeId;
	}


}
