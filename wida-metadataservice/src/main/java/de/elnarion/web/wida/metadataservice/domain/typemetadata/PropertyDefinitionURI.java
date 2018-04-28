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
package de.elnarion.web.wida.metadataservice.domain.typemetadata;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

import org.apache.chemistry.opencmis.commons.definitions.MutablePropertyUriDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyUriDefinition;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;

import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.metadataservice.WidaMetaDataConstants;

/**
 * The Class PropertyDefinitionURI.
 */
@Entity
@DiscriminatorValue("uri")
@SecondaryTable(name = WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE
		+ "_URI",  pkJoinColumns = {
				@PrimaryKeyJoinColumn(name = WidaMetaDataConstants.METADATA_PK_PREFIX
						+ WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE
						+ "_URI_ID", referencedColumnName = WidaMetaDataConstants.METADATA_ID_COLUMN_NAME) })
public class PropertyDefinitionURI extends PropertyDefinitionBase<String>
		implements MutablePropertyUriDefinition {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2336342589233035363L;

	/**
	 * Instantiates a new property definition URI.
	 */
	public PropertyDefinitionURI() {
		super();
		init();
	}

	/**
	 * Instantiates a new property definition URI.
	 *
	 * @param paramPropertyDefinition
	 *            the param property definition
	 */
	public PropertyDefinitionURI(PropertyUriDefinition paramPropertyDefinition) {
		super(paramPropertyDefinition);
		init();
	}

	private void init() {
		setPropertyType(PropertyType.URI);
	}

	@Override
	@ElementCollection
	@CollectionTable( name = WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE
			+ "_URI_DEFAULT", joinColumns = @JoinColumn(name = "PROP_DEF_ID"))
	@Column(name = "DEFAULT_VALUE")
	public List<String> getDefaultValue() {
		return super.getDefaultValue();
	}

	@Override
	public void setDefaultValue(List<String> defaultValue) {
		super.setDefaultValue(defaultValue);
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionBase#updatePropertyDefinitionAttributes(org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition)
	 */
	@Override
	public void updatePropertyDefinitionAttributes(PropertyDefinition<String> paramPropertyDefinition) {
		if(!(paramPropertyDefinition instanceof PropertyUriDefinition))
			 throw new CmisRuntimeException("Wrong type used to update property type", WidaErrorConstants.RUNTIME_EXCEPTION_WRONG_TYPE_FOR_UPDATE_USED);		
		
		updatePropertyDefinitionBase(paramPropertyDefinition);
		// Nothing to do no type specific attributes so far
	}
}
