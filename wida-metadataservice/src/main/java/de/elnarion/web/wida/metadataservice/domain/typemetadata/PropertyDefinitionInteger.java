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

import java.math.BigInteger;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

import org.apache.chemistry.opencmis.commons.definitions.MutablePropertyIntegerDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyIntegerDefinition;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;

import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.metadataservice.WidaMetaDataConstants;

/**
 * The Class PropertyDefinitionInteger.
 */
@Entity
@DiscriminatorValue("integer")
@SecondaryTable(name = WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE
		+ "_INTEGER",  pkJoinColumns = {
				@PrimaryKeyJoinColumn(name = WidaMetaDataConstants.METADATA_PK_PREFIX
						+ WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE
						+ "_INTEGER_ID", referencedColumnName = WidaMetaDataConstants.METADATA_ID_COLUMN_NAME) })
public class PropertyDefinitionInteger extends PropertyDefinitionBase<BigInteger>
		implements MutablePropertyIntegerDefinition {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8584839413188246373L;

	/** The min value. */
	private BigInteger minValue;

	/** The max value. */
	private BigInteger maxValue;

	/**
	 * Instantiates a new property definition integer.
	 */
	public PropertyDefinitionInteger() {
		super();
		init();
	}

	/**
	 * Instantiates a new property definition integer.
	 *
	 * @param paramPropertyDefinition
	 *            the param property definition
	 */
	public PropertyDefinitionInteger(PropertyIntegerDefinition paramPropertyDefinition) {
		super(paramPropertyDefinition);
		init();
	}

	private void init() {
		setPropertyType(PropertyType.INTEGER);
	}

	/**
	 * Gets the min value.
	 *
	 * @return BigInteger - the min value
	 */
	@Column(name = "MIN_VALUE", table = WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE + "_INTEGER")
	public BigInteger getMinValue() {
		return minValue;
	}

	/**
	 * Sets the min value.
	 *
	 * @param minValue
	 *            the min value
	 */
	public void setMinValue(BigInteger minValue) {
		this.minValue = minValue;
	}

	/**
	 * Gets the max value.
	 *
	 * @return BigInteger - the max value
	 */
	@Column(name = "MAX_VALUE", table = WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE + "_INTEGER")
	public BigInteger getMaxValue() {
		return maxValue;
	}

	/**
	 * Sets the max value.
	 *
	 * @param maxValue
	 *            the max value
	 */
	public void setMaxValue(BigInteger maxValue) {
		this.maxValue = maxValue;
	}

	@Override
	@ElementCollection
	@CollectionTable(name = WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE
			+ "_INTEGER_DEFAULT", joinColumns = @JoinColumn(name = "PROP_DEF_ID"))
	@Column(name = "DEFAULT_VALUE")
	public List<BigInteger> getDefaultValue() {
		return super.getDefaultValue();
	}

	@Override
	public void setDefaultValue(List<BigInteger> defaultValue) {
		super.setDefaultValue(defaultValue);
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionBase#updatePropertyDefinitionAttributes(org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition)
	 */
	@Override
	public void updatePropertyDefinitionAttributes(PropertyDefinition<BigInteger> paramPropertyDefinition) {
		if(!(paramPropertyDefinition instanceof PropertyIntegerDefinition))
			 throw new CmisRuntimeException("Wrong type used to update property type", WidaErrorConstants.RUNTIME_EXCEPTION_WRONG_TYPE_FOR_UPDATE_USED);		
		
		updatePropertyDefinitionBase(paramPropertyDefinition);
		setMinValue(((PropertyIntegerDefinition)paramPropertyDefinition).getMinValue());
		setMaxValue(((PropertyIntegerDefinition)paramPropertyDefinition).getMaxValue());
	}

}
