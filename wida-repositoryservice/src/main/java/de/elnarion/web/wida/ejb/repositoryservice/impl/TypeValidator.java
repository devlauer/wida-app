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
package de.elnarion.web.wida.ejb.repositoryservice.impl;

import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;

import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeBase;

/**
 * The Class TypeCreationValidator.
 */
public class TypeValidator {

	/**
	 * Instantiates a new type creation validator.
	 */
	public TypeValidator() {
	}

	/**
	 * Validate creation of type.
	 *
	 * @param paramTypeToValidate
	 *            the param type to validate
	 * @param paramParentDefinition
	 *            the param parent definition
	 */
	public void validateCreationOfType(TypeDefinition paramTypeToValidate, TypeBase paramParentDefinition)
	{
		// TODO implement see 2.1.10.1 General Constraints on Metadata Changes
		// http://docs.oasis-open.org/cmis/CMIS/v1.1/errata01/os/CMIS-v1.1-errata01-os-complete.html#x1-7400010
	}
	
	/**
	 * Validate update of type.
	 *
	 * @param paramOriginalType
	 *            the param original type
	 * @param paramTargetDefinition
	 *            the param target definition
	 */
	public void validateUpdateOfType(TypeBase paramOriginalType, TypeDefinition paramTargetDefinition)
	{
		// TODO implement see 2.1.10.1 General Constraints on Metadata Changes
		// http://docs.oasis-open.org/cmis/CMIS/v1.1/errata01/os/CMIS-v1.1-errata01-os-complete.html#x1-7400010
	}
}
