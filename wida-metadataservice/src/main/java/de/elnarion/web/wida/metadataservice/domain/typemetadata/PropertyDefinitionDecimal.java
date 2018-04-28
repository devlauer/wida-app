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

import java.math.BigDecimal;
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

import org.apache.chemistry.opencmis.commons.definitions.MutablePropertyDecimalDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDecimalDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.DecimalPrecision;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;

import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.metadataservice.WidaMetaDataConstants;

/**
 * The Class PropertyDefinitionDecimal.
 */
@Entity
@DiscriminatorValue("decimal")
@SecondaryTable(name = WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE+"_DECIMAL",  pkJoinColumns = {
		@PrimaryKeyJoinColumn(name = WidaMetaDataConstants.METADATA_PK_PREFIX+WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE+"_DECIMAL_ID", referencedColumnName = WidaMetaDataConstants.METADATA_ID_COLUMN_NAME) })
public class PropertyDefinitionDecimal extends PropertyDefinitionBase<BigDecimal>
		implements MutablePropertyDecimalDefinition {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -9198179808433535234L;

	/** The precision. */
	private DecimalPrecision precision;

	/** The min value. */
	private BigDecimal minValue;

	/** The max value. */
	private BigDecimal maxValue;

	/**
	 * Instantiates a new property definition decimal.
	 */
	public PropertyDefinitionDecimal() {
		super();
		init();
	}

	/**
	 * Instantiates a new property definition decimal.
	 *
	 * @param paramPropertyDefimition
	 *            the param property defimition
	 */
	public PropertyDefinitionDecimal(PropertyDecimalDefinition paramPropertyDefimition) {
		super(paramPropertyDefimition);
		init();
	}

	/**
	 * Inits the.
	 */
	private void init() {
		setPropertyType(PropertyType.DECIMAL);
	}

	/**
	 * Gets the precision.
	 *
	 * @return DecimalPrecision - the precision
	 */
	@Column(name = "PRECISION",table = WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE+"_DECIMAL")
	@Enumerated(EnumType.STRING)
	public DecimalPrecision getPrecision() {
		return precision;
	}

	/**
	 * Sets the precision.
	 *
	 * @param precision
	 *            the precision
	 */
	public void setPrecision(DecimalPrecision precision) {
		this.precision = precision;
	}

	/**
	 * Gets the min value.
	 *
	 * @return BigDecimal - the min value
	 */
	@Column(name = "MIN_VALUE",table = WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE+"_DECIMAL")
	public BigDecimal getMinValue() {
		return minValue;
	}

	/**
	 * Sets the min value.
	 *
	 * @param minValue
	 *            the min value
	 */
	public void setMinValue(BigDecimal minValue) {
		this.minValue = minValue;
	}

	/**
	 * Gets the max value.
	 *
	 * @return BigInteger - the max value
	 */
	@Column(name = "MAX_VALUE", table = WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE+"_DECIMAL")
	public BigDecimal getMaxValue() {
		return maxValue;
	}

	/**
	 * Sets the max value.
	 *
	 * @param maxValue
	 *            the max value
	 */
	public void setMaxValue(BigDecimal maxValue) {
		this.maxValue = maxValue;
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionBase#getDefaultValue()
	 */
	@Override
	@ElementCollection
	@CollectionTable( name = WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE+"_DECIMAL_DEFAULT", joinColumns = @JoinColumn(name = "PROP_DEF_ID"))
	@Column(name = "DEFAULT_VALUE")
	public List<BigDecimal> getDefaultValue() {
		return super.getDefaultValue();
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionBase#setDefaultValue(java.util.List)
	 */
	@Override
	public void setDefaultValue(List<BigDecimal> defaultValue) {
		super.setDefaultValue(defaultValue);
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionBase#updatePropertyDefinitionAttributes(org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition)
	 */
	@Override
	public void updatePropertyDefinitionAttributes(PropertyDefinition<BigDecimal> paramPropertyDefinition) {
		if(!(paramPropertyDefinition instanceof PropertyDecimalDefinition))
			 throw new CmisRuntimeException("Wrong type used to update property type", WidaErrorConstants.RUNTIME_EXCEPTION_WRONG_TYPE_FOR_UPDATE_USED);		
		
		updatePropertyDefinitionBase(paramPropertyDefinition);
		setPrecision(((PropertyDecimalDefinition)paramPropertyDefinition).getPrecision());
		setMinValue(((PropertyDecimalDefinition)paramPropertyDefinition).getMinValue());
		setMaxValue(((PropertyDecimalDefinition)paramPropertyDefinition).getMaxValue());
	}
}
