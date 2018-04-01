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

import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.server.support.TypeManager;

import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeBase;

/**
 * The Interface WidaTypeService.
 */
@Local
public interface WidaTypeMetaDataService extends TypeManager{
	
	/**
	 * Gets the property definition for the given id.
	 *
	 * @param paramPropertyId
	 *            the param property id
	 * @return PropertyDefinition - the property definition
	 */
	public PropertyDefinition<?> getPropertyDefinition(String paramPropertyId);
	
	/**
	 * Gets the type definition map.
	 *
	 * @return Map - the type definition map
	 */
	public Map<String, TypeDefinition> getTypeDefinitionMap();
	
	
	/**
	 * Gets the internal type definition.
	 *
	 * @param paramTypeId
	 *            the param type id
	 * @return the internal type definition
	 */
	public TypeBase getInternalTypeDefinition(String paramTypeId);

}
