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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.chemistry.opencmis.commons.definitions.Choice;
import org.apache.chemistry.opencmis.commons.definitions.MutablePropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.Cardinality;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AbstractExtensionData;

import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.metadataservice.WidaMetaDataConstants;
import de.elnarion.web.wida.metadataservice.impl.TypeHelper;

/**
 * The Class PropertyDefinitionBase.
 *
 * @param <T>
 *            the generic type
 */
@Entity
@Table(name = WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE,  indexes = {
		@Index(name = WidaMetaDataConstants.METADATA_INDEX_PREFIX
				+ WidaMetaDataConstants.METADATA_TYPE_PROPERTYDEFINITION_TABLE
				+ "_QUERY_NAME", columnList = "QUERY_NAME") }, uniqueConstraints = {
						@UniqueConstraint(columnNames = { "QUERY_NAME" }) })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class PropertyDefinitionBase<T> extends AbstractExtensionData
		implements MutablePropertyDefinition<T> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4405047409016134190L;

	/** The id. */
	private String id;

	/** The property type. */
	private PropertyType propertyType;

	/** The cardinality. */
	private Cardinality cardinality;

	/** The updatability. */
	private Updatability updatability;

	/** The inherited. */
	private Boolean inherited;

	/** The required. */
	private Boolean required;

	/** The queryable. */
	private Boolean queryable;

	/** The orderable. */
	private Boolean orderable;

	/** The default value. */
	private List<T> defaultValue;

	/** The local name. */
	// optional
	private String localName;

	/** The local namespace. */
	private String localNamespace;

	/** The query name. */
	private String queryName;

	/** The display name. */
	private String displayName;

	/** The description. */
	private String description;

	/** The choices. */
	private List<Choice<T>> choices;

	/** The open choice. */
	private Boolean openChoice;

	// non cmis attributes;

	/** The column name. */
	private String columnName;

	/**
	 * Instantiates a new property definition base.
	 */
	public PropertyDefinitionBase() {

	}

	/**
	 * Instantiates a new property definition base.
	 *
	 * @param paramPropertyDefinition
	 *            the param property definition
	 */
	public PropertyDefinitionBase(PropertyDefinition<T> paramPropertyDefinition) {
		updatePropertyDefinitionAttributes(paramPropertyDefinition);
	}

	/**
	 * Gets the id.
	 *
	 * @return String - the id
	 */
	@Id
	@Column(name = WidaMetaDataConstants.METADATA_ID_COLUMN_NAME, length = 50, unique = true, nullable = false)
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the property type.
	 *
	 * @return PropertyType - the property type
	 */
	@Column(name = "PROPERTY_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	public PropertyType getPropertyType() {
		return propertyType;
	}

	/**
	 * Sets the property type.
	 *
	 * @param propertyType
	 *            the property type
	 */
	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
	}

	/**
	 * Gets the cardinality.
	 *
	 * @return Cardinality - the cardinality
	 */
	@Column(name = "CARDINALITY", nullable = false)
	@Enumerated(EnumType.STRING)
	public Cardinality getCardinality() {
		return cardinality;
	}

	/**
	 * Sets the cardinality.
	 *
	 * @param cardinality
	 *            the cardinality
	 */
	public void setCardinality(Cardinality cardinality) {
		this.cardinality = cardinality;
	}

	/**
	 * Gets the updatability.
	 *
	 * @return Updatability - the updatability
	 */
	@Column(name = "UPDATABILITY", nullable = false)
	@Enumerated(EnumType.STRING)
	public Updatability getUpdatability() {
		return updatability;
	}

	/**
	 * Sets the updatability.
	 *
	 * @param updatability
	 *            the updatability
	 */
	public void setUpdatability(Updatability updatability) {
		this.updatability = updatability;
	}

	/**
	 * Gets the inherited.
	 *
	 * @return Boolean - the inherited
	 */
	@Transient
	public Boolean isInherited() {
		return inherited;
	}

	/**
	 * Sets the inherited.
	 *
	 * @param inherited
	 *            the inherited
	 */
	public void setInherited(Boolean inherited) {
		setIsInherited(inherited);
	}

	/**
	 * Sets the inherited.
	 *
	 * @param inherited
	 *            the inherited
	 */
	public void setIsInherited(Boolean inherited) {
		this.inherited = inherited;
	}

	/**
	 * Gets the required.
	 *
	 * @return Boolean - the required
	 */
	@Column(name = "REQUIRED", nullable = false)
	public Boolean isRequired() {
		return required;
	}

	/**
	 * Sets the required.
	 *
	 * @param required
	 *            the required
	 */
	public void setRequired(Boolean required) {
		setIsRequired(required);
	}

	/**
	 * Sets the required.
	 *
	 * @param required
	 *            the required
	 */
	public void setIsRequired(Boolean required) {
		this.required = required;
	}

	/**
	 * Gets the queryable.
	 *
	 * @return Boolean - the queryable
	 */
	@Column(name = "QUERYABLE", nullable = false)
	public Boolean isQueryable() {
		return queryable;
	}

	/**
	 * Sets the queryable.
	 *
	 * @param queryable
	 *            the queryable
	 */
	public void setQueryable(Boolean queryable) {
		setIsQueryable(queryable);
	}

	/**
	 * Sets the queryable.
	 *
	 * @param queryable
	 *            the queryable
	 */
	public void setIsQueryable(Boolean queryable) {
		this.queryable = queryable;
	}

	/**
	 * Gets the orderable.
	 *
	 * @return Boolean - the orderable
	 */
	@Column(name = "ORDERABLE", nullable = false)
	public Boolean isOrderable() {
		return orderable;
	}

	/**
	 * Sets the orderable.
	 *
	 * @param orderable
	 *            the orderable
	 */
	public void setOrderable(Boolean orderable) {
		setIsOrderable(orderable);
	}

	/**
	 * Sets the orderable.
	 *
	 * @param orderable
	 *            the orderable
	 */
	public void setIsOrderable(Boolean orderable) {
		this.orderable = orderable;
	}

	/**
	 * Gets the default value.
	 *
	 * @return T - the default value
	 */
	@Transient
	public List<T> getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default value.
	 *
	 * @param defaultValue
	 *            the default value
	 */
	public void setDefaultValue(List<T> defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Gets the local name.
	 *
	 * @return String - the local name
	 */
	@Column(name = "LOCAL_NAME", nullable = true, length = 50)
	public String getLocalName() {
		return localName;
	}

	/**
	 * Sets the local name.
	 *
	 * @param localName
	 *            the local name
	 */
	public void setLocalName(String localName) {
		this.localName = localName;
	}

	/**
	 * Gets the local namespace.
	 *
	 * @return String - the local namespace
	 */
	@Column(name = "LOCAL_NAMESPACE", nullable = true, length = 100)
	public String getLocalNamespace() {
		return localNamespace;
	}

	/**
	 * Sets the local namespace.
	 *
	 * @param localNamespace
	 *            the local namespace
	 */
	public void setLocalNamespace(String localNamespace) {
		this.localNamespace = localNamespace;
	}

	/**
	 * Gets the query name.
	 *
	 * @return String - the query name
	 */
	@Column(name = "QUERY_NAME", nullable = true, length = 50)
	public String getQueryName() {
		return queryName;
	}

	/**
	 * Sets the query name.
	 *
	 * @param queryName
	 *            the query name
	 */
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	/**
	 * Gets the display name.
	 *
	 * @return String - the display name
	 */
	@Column(name = "DISPLAY_NAME", nullable = true, length = 50)
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Sets the display name.
	 *
	 * @param displayName
	 *            the display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Gets the description.
	 *
	 * @return String - the description
	 */
	@Column(name = "DESCRIPTION", nullable = true, length = 200)
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description
	 *            the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the column name.
	 *
	 * @return String - the column name
	 */
	@Column(name = "COLUMN_NAME", nullable = false, length = WidaMetaDataConstants.MAX_NAME_LENGTH)
	public String getColumnName() {
		if(columnName==null)
		{
			columnName=TypeHelper.convertTypeIdToTableOrColumnName(id);
		}
		return columnName;
	}

	/**
	 * Sets the column name.
	 *
	 * @param columnName
	 *            the column name
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition#
	 * isOpenChoice()
	 */
	@Override
	@Column(name = "OPENCHOICE")
	public Boolean isOpenChoice() {
		return openChoice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition#
	 * getChoices()
	 */
	@Override
	@Transient
	public List<Choice<T>> getChoices() {
		return choices;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.chemistry.opencmis.commons.definitions.MutablePropertyDefinition#
	 * setChoices(java.util.List)
	 */
	@Override
	public void setChoices(List<Choice<T>> choiceList) {
		if (choiceList == null) {
			choices = new ArrayList<Choice<T>>();
		} else {
			choices = choiceList;
		}

	}

	/**
	 * Sets the open choice.
	 *
	 * @param isOpenChoice
	 *            the is open choice
	 */
	public void setOpenChoice(Boolean isOpenChoice) {
		setIsOpenChoice(isOpenChoice);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.chemistry.opencmis.commons.definitions.MutablePropertyDefinition#
	 * setIsOpenChoice(java.lang.Boolean)
	 */
	@Override
	public void setIsOpenChoice(Boolean isOpenChoice) {
		openChoice = isOpenChoice;
	}

	/**
	 * Update property definition attributes.
	 *
	 * @param paramPropertyDefinition
	 *            the param property definition
	 */
	public abstract void updatePropertyDefinitionAttributes(PropertyDefinition<T> paramPropertyDefinition);

	protected void updatePropertyDefinitionBase(PropertyDefinition<T> paramFromPropDef) {
		if (getCardinality() != null && !getCardinality().equals(paramFromPropDef.getCardinality()))
			throw new CmisConstraintException(
					"The switch from a single to a multi value property or vice versa is not allowed. Id is "
							+ paramFromPropDef.getId(),
					WidaErrorConstants.PROPERTY_CARDINALITY_SWITCH_NOT_ALLOWED);
		setCardinality(paramFromPropDef.getCardinality());
		if (paramFromPropDef.getChoices() != null && paramFromPropDef.getChoices().size() > 0)
			throw new CmisConstraintException(
					"Choices are not allowed for property definitions in this repository. Id is "
							+ paramFromPropDef.getId(),
					WidaErrorConstants.PROPERTY_CHOICES_NOT_ALLOWED);
		setChoices(new ArrayList<>());
		setDefaultValue(paramFromPropDef.getDefaultValue());
		setDescription(paramFromPropDef.getDescription());
		setDisplayName(paramFromPropDef.getDisplayName());
		setId(paramFromPropDef.getId());
		// the attribute inherited is not set because it is only set when the whole type
		// is retrieved and property
		// definitions of the parent are added.

		// because choices are forbidden (look above) this property is always true
		setIsOpenChoice(true);
		setIsOrderable(paramFromPropDef.isOrderable());
		setIsQueryable(paramFromPropDef.isQueryable());
		if (isRequired() != null && !isRequired().equals(paramFromPropDef.isRequired()))
			throw new CmisConstraintException(
					"It is not allowed to switch from required to non required or vice versa for properties.Id is "
							+ paramFromPropDef.getId(),
					WidaErrorConstants.PROPERTY_SWITCH_REQUIRED_NOT_ALLOWED);
		setIsRequired(paramFromPropDef.isRequired());
		setLocalName(paramFromPropDef.getLocalName());
		setLocalNamespace(paramFromPropDef.getLocalNamespace());
		setQueryName(paramFromPropDef.getQueryName());
		if (paramFromPropDef.getUpdatability().equals(Updatability.WHENCHECKEDOUT))
			throw new CmisConstraintException(
					"This repository does not support versioning, so an updatability with whencheckedout is not allowed. Id is "
							+ paramFromPropDef.getId(),
					WidaErrorConstants.PROPERTY_UPDATABILITY_WHENCHECKEDOUT_NOT_ALLOWED);
		setUpdatability(paramFromPropDef.getUpdatability());

	}

}
