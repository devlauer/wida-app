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

import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

import org.apache.chemistry.opencmis.commons.definitions.MutablePropertyDateTimeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDateTimeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.DateTimeResolution;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;

import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.metadataservice.WidaMetaDataConstants;

/**
 * The Class PropertyDefinitionDateTime.
 */
@Entity
@DiscriminatorValue("datetime")
@SecondaryTable(name = WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE
		+ "_datetime", schema = WidaMetaDataConstants.METADATA_DB_SCHEMA, pkJoinColumns = {
				@PrimaryKeyJoinColumn(name = WidaMetaDataConstants.METADATA_PK_PREFIX
						+ WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE
						+ "_datetime_id", referencedColumnName = "id") })
public class PropertyDefinitionDateTime extends PropertyDefinitionBase<GregorianCalendar>
		implements MutablePropertyDateTimeDefinition {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5910673620268588136L;

	/** The resolution. */
	private DateTimeResolution dateTimeResolution = DateTimeResolution.TIME;

	/**
	 * Instantiates a new property definition date time.
	 */
	public PropertyDefinitionDateTime() {
		super();
		init();
	}

	/**
	 * Instantiates a new property definition date time.
	 *
	 * @param paramPropertyDefinition
	 *            the param property definition
	 */
	public PropertyDefinitionDateTime(PropertyDateTimeDefinition paramPropertyDefinition) {
		super(paramPropertyDefinition);
		init();
	}

	private void init() {
		setPropertyType(PropertyType.DATETIME);
	}

	/**
	 * Gets the resolution.
	 *
	 * @return DateTimeResolution - the resolution
	 */
	@Column(name = "resolution", table = WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE + "_datetime")
	@Enumerated(EnumType.STRING)
	public DateTimeResolution getDateTimeResolution() {
		return dateTimeResolution;
	}

	/**
	 * Sets the resolution.
	 *
	 * @param resolution
	 *            the resolution
	 */
	public void setDateTimeResolution(DateTimeResolution resolution) {
		this.dateTimeResolution = resolution;
	}

	@Override
	@ElementCollection
	@CollectionTable(schema = WidaMetaDataConstants.METADATA_DB_SCHEMA, name = WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE
			+ "_datetime_default", joinColumns = @JoinColumn(name = "prop_def_id"))
	@Column(name = "default_value")
	public List<GregorianCalendar> getDefaultValue() {
		return super.getDefaultValue();
	}

	@Override
	public void setDefaultValue(List<GregorianCalendar> defaultValue) {
		super.setDefaultValue(defaultValue);
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionBase#updatePropertyDefinitionAttributes(org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition)
	 */
	@Override
	public void updatePropertyDefinitionAttributes(PropertyDefinition<GregorianCalendar> paramPropertyDefinition) {
		if(!(paramPropertyDefinition instanceof PropertyDateTimeDefinition))
			 throw new CmisRuntimeException("Wrong type used to update property type", WidaErrorConstants.RUNTIME_EXCEPTION_WRONG_TYPE_FOR_UPDATE_USED);		
		
		updatePropertyDefinitionBase(paramPropertyDefinition);
		setDateTimeResolution(((PropertyDateTimeDefinition)paramPropertyDefinition).getDateTimeResolution());
	}

}
