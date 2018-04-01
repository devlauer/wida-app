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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.chemistry.opencmis.commons.definitions.MutableTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyBooleanDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDateTimeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDecimalDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyHtmlDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyIdDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyIntegerDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyStringDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyUriDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeMutability;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNotSupportedException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AbstractTypeDefinition;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.TypeMutabilityImpl;

import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.metadataservice.WidaMetaDataConstants;
import de.elnarion.web.wida.metadataservice.impl.TypeHelper;

/**
 * The Class TypeBase.
 *
 */
@Entity
@Table(name = WidaMetaDataConstants.METADATA_TYPE_TYPEBASE_DEFINITION_TABLE, schema = WidaMetaDataConstants.METADATA_DB_SCHEMA, indexes = {
		@Index(name = WidaMetaDataConstants.METADATA_INDEX_PREFIX
				+ WidaMetaDataConstants.METADATA_TYPE_TYPEBASE_DEFINITION_TABLE + "_id", columnList = "parent_id"),
		@Index(name = WidaMetaDataConstants.METADATA_INDEX_PREFIX
				+ WidaMetaDataConstants.METADATA_TYPE_TYPEBASE_DEFINITION_TABLE
				+ "_query_name", columnList = "query_name") }, uniqueConstraints = {
						@UniqueConstraint(columnNames = { "query_name" }) })
@DiscriminatorColumn(name = "discriminator")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class TypeBase extends AbstractTypeDefinition
		implements MutableTypeDefinition {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4607317183083936217L;


	/** The type mutability create. */
	private Boolean typeMutabilityCreate = true;

	/** The type mutability update. */
	private Boolean typeMutabilityUpdate = false;

	/** The type mutabiltiy delete. */
	private Boolean typeMutabiltiyDelete = false;

	// non cmis properties

	/** The inherit properties. */
	private Boolean inheritProperties;

	/** The secondary types. */
	private Set<TypeSecondary> secondaryTypes = new TreeSet<TypeSecondary>();

	/** The property definitions. */
	private List<PropertyDefinitionBase<?>> propertyDefinitionsList = new ArrayList<PropertyDefinitionBase<?>>();

	/** The tablename. */
	private String tablename;

	/**
	 * Instantiates a new type base.
	 */
	public TypeBase() {

	}

	/**
	 * Instantiates a new type base.
	 *
	 * @param paramInitTypeDefinition
	 *            the param init type definition
	 */
	public TypeBase(TypeDefinition paramInitTypeDefinition) {
		// in this repo properties are always inherited, there is no parameter in cmis to
		// not inherit properties on creation (inherited properties are never included).
		// and because this repository allows type creation only through cmis-api there are 
		// never not inherited properties -> always true
		setInheritProperties(true);
		updateTypeAttributes(paramInitTypeDefinition);
	}

	/**
	 * Update type attributes.
	 *
	 * @param paramType
	 *            the param type
	 */
	public abstract void updateTypeAttributes(TypeDefinition paramType);

	/**
	 * Update the attributes of this type with the attributes of the given type
	 * definition.
	 *
	 * @param paramTypeDefinition
	 *            the param type definition
	 */
	protected void updateBaseTypeAttributes(TypeDefinition paramTypeDefinition) {
		if (paramTypeDefinition.getId() == null)
			throw new CmisInvalidArgumentException("An id with a null value is not allowed",
					WidaErrorConstants.TYPE_ATTRIBUTE_ID_NULL_NOT_ALLOWED);
		else
			setId(paramTypeDefinition.getId());
		setDescription(paramTypeDefinition.getDescription());
		if (paramTypeDefinition.getDisplayName() != null)
			setDisplayName(paramTypeDefinition.getDisplayName());
		else
			setDisplayName(getId());
		if (paramTypeDefinition.isControllableAcl())
			throw new CmisNotSupportedException("Controllable Acl is not supported in this repository",
					WidaErrorConstants.NOT_SUPPORTED_CONTROLLABLE_ACL);
		setIsControllableAcl(false);
		if (paramTypeDefinition.isControllablePolicy())
			throw new CmisNotSupportedException("Controllable Policy is not allowed in this repository",
					WidaErrorConstants.NOT_SUPPORTED_CONTROLLABLE_POLICY);
		setIsControllablePolicy(false);
		setIsCreatable(paramTypeDefinition.isCreatable());
		setIsFileable(paramTypeDefinition.isFileable());
		if (paramTypeDefinition.isFulltextIndexed())
			throw new CmisNotSupportedException("A fulltext index is not supported by this repository",
					WidaErrorConstants.NOT_SUPPORTED_FULLTEXT_INDEX);
		setIsFulltextIndexed(false);
		if (paramTypeDefinition.isIncludedInSupertypeQuery() == null)
			setIsIncludedInSupertypeQuery(true);
		else
			setIsIncludedInSupertypeQuery(paramTypeDefinition.isIncludedInSupertypeQuery());
		if (paramTypeDefinition.isQueryable() == null)
			setIsQueryable(true);
		else
			setIsQueryable(paramTypeDefinition.isQueryable());
		if (paramTypeDefinition.getLocalName() == null)
			setLocalName(getId());
		else
			setLocalName(paramTypeDefinition.getLocalName());
		setLocalNamespace(paramTypeDefinition.getLocalNamespace());
		setParentTypeId(paramTypeDefinition.getParentTypeId());
		if (paramTypeDefinition.getQueryName() == null)
			setQueryName(getId());
		else
			setQueryName(paramTypeDefinition.getQueryName());
		if (paramTypeDefinition.getTypeMutability() == null) {
			TypeMutabilityImpl typeMutability = new TypeMutabilityImpl();
			typeMutability.setCanCreate(true);
			typeMutability.setCanUpdate(true);
			typeMutability.setCanDelete(true);
			setTypeMutability(typeMutability);
		} else {
			setTypeMutability(paramTypeDefinition.getTypeMutability());
		}
		setTablename(TypeHelper.convertTypeIdToTableOrColumnName(getId()));
		updatePropertyDefinitions(paramTypeDefinition);
	}

	/**
	 * Update property definitions from parameter to this implementation.
	 *
	 * @param paramTypeDefinition
	 *            the param type definition
	 */
	private void updatePropertyDefinitions(TypeDefinition paramTypeDefinition) {
		Map<String, PropertyDefinition<?>> newPropertyDefinitionMap = paramTypeDefinition.getPropertyDefinitions();
		Map<String, PropertyDefinition<?>> oldPropertyDefinitionMap = getPropertyDefinitions(false);
		Set<String> newKeys = newPropertyDefinitionMap.keySet();
		Set<String> oldKeysCheckSet = oldPropertyDefinitionMap.keySet();
		for (String newKey : newKeys) {
			PropertyDefinitionBase<?> repoPropDef = null;
			// check if the property definition is already in this object,
			// if the property definition is not in this object,
			// create a new specific definition.
			PropertyDefinition<?> oldPropDef = oldPropertyDefinitionMap.get(newKey);
			PropertyDefinition<?> newPropDef = newPropertyDefinitionMap.get(newKey);
			if (oldPropDef != null) {
				oldKeysCheckSet.remove(newKey);
				repoPropDef = (PropertyDefinitionBase<?>) oldPropDef;
				if (newPropDef instanceof PropertyBooleanDefinition
						&& repoPropDef instanceof PropertyDefinitionBoolean) {
					((PropertyDefinitionBoolean) repoPropDef)
							.updatePropertyDefinitionAttributes((PropertyBooleanDefinition) newPropDef);
				} else if (newPropDef instanceof PropertyDateTimeDefinition
						&& repoPropDef instanceof PropertyDefinitionDateTime) {
					((PropertyDefinitionDateTime) repoPropDef)
							.updatePropertyDefinitionAttributes((PropertyDateTimeDefinition) newPropDef);
				} else if (newPropDef instanceof PropertyDecimalDefinition
						&& repoPropDef instanceof PropertyDefinitionDecimal) {
					((PropertyDefinitionDecimal) repoPropDef)
							.updatePropertyDefinitionAttributes((PropertyDecimalDefinition) newPropDef);
				} else if (newPropDef instanceof PropertyHtmlDefinition
						&& repoPropDef instanceof PropertyDefinitionHTML) {
					((PropertyDefinitionHTML) repoPropDef)
							.updatePropertyDefinitionAttributes((PropertyHtmlDefinition) newPropDef);
				} else if (newPropDef instanceof PropertyIdDefinition && repoPropDef instanceof PropertyDefinitionId) {
					((PropertyDefinitionId) repoPropDef)
							.updatePropertyDefinitionAttributes((PropertyIdDefinition) newPropDef);
				} else if (newPropDef instanceof PropertyIntegerDefinition
						&& repoPropDef instanceof PropertyDefinitionInteger) {
					((PropertyDefinitionInteger) repoPropDef)
							.updatePropertyDefinitionAttributes((PropertyIntegerDefinition) newPropDef);
				} else if (newPropDef instanceof PropertyStringDefinition
						&& repoPropDef instanceof PropertyDefinitionString) {
					((PropertyDefinitionString) repoPropDef)
							.updatePropertyDefinitionAttributes((PropertyStringDefinition) newPropDef);
				} else if (newPropDef instanceof PropertyUriDefinition
						&& repoPropDef instanceof PropertyDefinitionURI) {
					((PropertyDefinitionURI) repoPropDef)
							.updatePropertyDefinitionAttributes((PropertyUriDefinition) newPropDef);
				} else {
					throw new CmisConstraintException(
							"Property definition type does not match the original property definition type or the property definition type is unknown. PropertyDefinitionClass is "
									+ newPropDef.getClass().getName() + ", Id is " + newPropDef.getId(),
							WidaErrorConstants.PROPERTY_UNKOWN_DEFINITION_TYPE);
				}
			} else {
				repoPropDef = createRepoPropDefByPropDef(newPropDef);
				addPropertyDefinition(repoPropDef);
			}
		}
		if (oldKeysCheckSet != null && oldKeysCheckSet.size() > 0)
			throw new CmisConstraintException("It is not allowed to remove property definitions from an existing type!",
					WidaErrorConstants.PROPERTY_DEFINITION_TYPE_REMOVAL_NOT_ALLOWED);
	}

	/**
	 * Creates the repo prop def by prop def.
	 *
	 * @param newPropDef
	 *            the new prop def
	 * @return the property definition base<?,? extends property definition<?>>
	 */
	private PropertyDefinitionBase<?> createRepoPropDefByPropDef(
			PropertyDefinition<?> newPropDef) {
		PropertyDefinitionBase<?> repoPropDef = null;
		if (newPropDef instanceof PropertyBooleanDefinition) {
			repoPropDef = new PropertyDefinitionBoolean((PropertyBooleanDefinition) newPropDef);
		} else if (newPropDef instanceof PropertyDateTimeDefinition) {
			repoPropDef = new PropertyDefinitionDateTime((PropertyDateTimeDefinition) newPropDef);
		} else if (newPropDef instanceof PropertyDecimalDefinition) {
			repoPropDef = new PropertyDefinitionDecimal((PropertyDecimalDefinition) newPropDef);
		} else if (newPropDef instanceof PropertyHtmlDefinition) {
			repoPropDef = new PropertyDefinitionHTML((PropertyHtmlDefinition) newPropDef);
		} else if (newPropDef instanceof PropertyIdDefinition) {
			repoPropDef = new PropertyDefinitionId((PropertyIdDefinition) newPropDef);
		} else if (newPropDef instanceof PropertyIntegerDefinition) {
			repoPropDef = new PropertyDefinitionInteger((PropertyIntegerDefinition) newPropDef);
		} else if (newPropDef instanceof PropertyStringDefinition) {
			repoPropDef = new PropertyDefinitionString((PropertyStringDefinition) newPropDef);
		} else if (newPropDef instanceof PropertyUriDefinition) {
			repoPropDef = new PropertyDefinitionURI((PropertyUriDefinition) newPropDef);
		} else {
			throw new CmisConstraintException("Unsupported property definition " + newPropDef.getClass().getName(),
					WidaErrorConstants.PROPERTY_UNKOWN_DEFINITION_TYPE);
		}
		return repoPropDef;
	}

	/**
	 * Gets the id.
	 *
	 * @return String - the id
	 */
	@Id
	@Column(name = "id", length = 50, unique = true, nullable = false)
	public String getId() {
		return super.getId();
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the id
	 */
	public void setId(String id) {
		super.setId(id);
	}

	/**
	 * Gets the base id.
	 *
	 * @return BaseTypeId - the base id
	 */
	@Transient
	public BaseTypeId getBaseTypeId() {
		return super.getBaseTypeId();
	}

	/**
	 * Sets the base id.
	 *
	 * @param baseTypeId
	 *            the base id
	 */
	public void setBaseTypeId(BaseTypeId baseTypeId) {
		super.setBaseTypeId(baseTypeId);
	}

	/**
	 * Gets the parent id.
	 *
	 * @return String - the parent id
	 */
	@Column(name = "parent_id", nullable = true, length = 50)
	public String getParentTypeId() {
		return super.getParentTypeId();
	}

	/**
	 * Gets the children.
	 *
	 * @param <T>
	 *            the generic type
	 * @return the children
	 */
	@Transient
	public abstract <T extends TypeBase> Set<T> getChildren();

	/**
	 * Sets the parent id.
	 *
	 * @param parentTypeId
	 *            the parent id
	 */
	public void setParentTypeId(String parentTypeId) {
		super.setParentTypeId(parentTypeId);
	}

	/**
	 * Gets the creatable.
	 *
	 * @return Boolean - the creatable
	 */
	@Column(name = "creatable", nullable = false)
	public Boolean isCreatable() {
		return super.isCreatable();
	}

	/**
	 * Sets the creatable.
	 *
	 * @param creatable
	 *            the creatable
	 */
	public void setCreatable(Boolean creatable) {
		setIsCreatable(creatable);
	}

	/**
	 * Sets the creatable.
	 *
	 * @param creatable
	 *            the creatable
	 */
	public void setIsCreatable(Boolean creatable) {
		super.setIsCreatable(creatable);
	}

	/**
	 * Gets the fileable.
	 *
	 * @return Boolean - the fileable
	 */
	@Column(name = "fileable", nullable = false)
	public Boolean isFileable() {
		return super.isFileable();
	}

	/**
	 * Sets the fileable.
	 *
	 * @param fileable
	 *            the fileable
	 */
	public void setFileable(Boolean fileable) {
		setIsFileable(fileable);
	}

	/**
	 * Sets the fileable.
	 *
	 * @param fileable
	 *            the fileable
	 */
	public void setIsFileable(Boolean fileable) {
		super.setIsFileable(fileable);
	}

	/**
	 * Gets the queryable.
	 *
	 * @return Boolean - the queryable
	 */
	@Column(name = "queryable", nullable = false)
	public Boolean isQueryable() {
		return super.isQueryable();
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
		super.setIsQueryable(queryable);
	}

	/**
	 * Gets the controllable policy.
	 *
	 * @return Boolean - the controllable policy
	 */
	@Column(name = "controllable_policy", nullable = false)
	public Boolean isControllablePolicy() {
		return super.isControllablePolicy();
	}

	/**
	 * Sets the controllable policy.
	 *
	 * @param controllablePolicy
	 *            the controllable policy
	 */
	public void setControllablePolicy(Boolean controllablePolicy) {
		setIsControllablePolicy(controllablePolicy);
	}

	/**
	 * Sets the controllable policy.
	 *
	 * @param controllablePolicy
	 *            the controllable policy
	 */
	public void setIsControllablePolicy(Boolean controllablePolicy) {
		super.setIsControllablePolicy(controllablePolicy);
	}

	/**
	 * Gets the controllable ACL.
	 *
	 * @return Boolean - the controllable ACL
	 */
	@Column(name = "controllable_acl", nullable = false)
	public Boolean isControllableAcl() {
		return super.isControllableAcl();
	}

	/**
	 * Sets the controllable acl.
	 *
	 * @param controllableAcl
	 *            the controllable acl
	 */
	public void setControllableAcl(Boolean controllableAcl) {
		setIsControllableAcl(controllableAcl);
	}

	/**
	 * Sets the controllable ACL.
	 *
	 * @param controllableAcl
	 *            the controllable ACL
	 */
	public void setIsControllableAcl(Boolean controllableAcl) {
		super.setIsControllableAcl(controllableAcl);
	}

	/**
	 * Gets the fulltext indexed.
	 *
	 * @return Boolean - the fulltext indexed
	 */
	@Column(name = "fulltext_indexed", nullable = false)
	public Boolean isFulltextIndexed() {
		return super.isFulltextIndexed();
	}

	/**
	 * Sets the fulltext indexed.
	 *
	 * @param fulltextIndexed
	 *            the fulltext indexed
	 */
	public void setFulltextIndexed(Boolean fulltextIndexed) {
		setIsFulltextIndexed(fulltextIndexed);
	}

	/**
	 * Sets the fulltext indexed.
	 *
	 * @param fulltextIndexed
	 *            the fulltext indexed
	 */
	public void setIsFulltextIndexed(Boolean fulltextIndexed) {
		super.setIsFulltextIndexed(fulltextIndexed);
	}

	/**
	 * Gets the included in super type query.
	 *
	 * @return Boolean - the included in super type query
	 */
	@Column(name = "included_in_super_type_query", nullable = false)
	public Boolean isIncludedInSupertypeQuery() {
		return super.isIncludedInSupertypeQuery();
	}

	/**
	 * Sets the included in supertype query.
	 *
	 * @param includedInSuperTypeQuery
	 *            the included in super type query
	 */
	public void setIncludedInSupertypeQuery(Boolean includedInSuperTypeQuery) {
		setIsIncludedInSupertypeQuery(includedInSuperTypeQuery);
	}

	/**
	 * Sets the included in super type query.
	 *
	 * @param includedInSuperTypeQuery
	 *            the included in super type query
	 */
	public void setIsIncludedInSupertypeQuery(Boolean includedInSuperTypeQuery) {
		super.setIsIncludedInSupertypeQuery(includedInSuperTypeQuery);
	}

	/**
	 * Gets the local name.
	 *
	 * @return String - the local name
	 */
	@Column(name = "local_name", nullable = true, length = 50)
	public String getLocalName() {
		return super.getLocalName();
	}

	/**
	 * Sets the local name.
	 *
	 * @param localName
	 *            the local name
	 */
	public void setLocalName(String localName) {
		super.setLocalName(localName);
	}

	/**
	 * Gets the local namespace.
	 *
	 * @return String - the local namespace
	 */
	@Column(name = "local_namespace", nullable = true, length = 100)
	public String getLocalNamespace() {
		return super.getLocalNamespace();
	}

	/**
	 * Sets the local namespace.
	 *
	 * @param localNamespace
	 *            the local namespace
	 */
	public void setLocalNamespace(String localNamespace) {
		super.setLocalNamespace(localNamespace);
	}

	/**
	 * Gets the query name.
	 *
	 * @return String - the query name
	 */
	@Column(name = "query_name", nullable = true, length = 50)
	public String getQueryName() {
		return super.getQueryName();
	}

	/**
	 * Sets the query name.
	 *
	 * @param queryName
	 *            the query name
	 */
	public void setQueryName(String queryName) {
		super.setQueryName(queryName);
	}

	/**
	 * Gets the display name.
	 *
	 * @return String - the display name
	 */
	@Column(name = "display_name", nullable = true, length = 50)
	public String getDisplayName() {
		return super.getDisplayName();
	}

	/**
	 * Sets the display name.
	 *
	 * @param displayName
	 *            the display name
	 */
	public void setDisplayName(String displayName) {
		super.setDisplayName(displayName);
	}

	/**
	 * Gets the description.
	 *
	 * @return String - the description
	 */
	@Column(name = "description", nullable = true, length = 200)
	public String getDescription() {
		return super.getDescription();
	}

	/**
	 * Sets the description.
	 *
	 * @param description
	 *            the description
	 */
	public void setDescription(String description) {
		super.setDescription(description);
	}

	/**
	 * Gets the type mutability create.
	 *
	 * @return Boolean - the type mutability create
	 */
	@Column(name = "tm_create", nullable = true)
	public Boolean getTypeMutabilityCreate() {
		return typeMutabilityCreate;
	}

	/**
	 * Sets the type mutability create.
	 *
	 * @param typeMutabilityCreate
	 *            the type mutability create
	 */
	public void setTypeMutabilityCreate(Boolean typeMutabilityCreate) {
		this.typeMutabilityCreate = typeMutabilityCreate;
	}

	/**
	 * Gets the type mutability update.
	 *
	 * @return Boolean - the type mutability update
	 */
	@Column(name = "tm_update", nullable = true)
	public Boolean getTypeMutabilityUpdate() {
		return typeMutabilityUpdate;
	}

	/**
	 * Sets the type mutability update.
	 *
	 * @param typeMutabilityUpdate
	 *            the type mutability update
	 */
	public void setTypeMutabilityUpdate(Boolean typeMutabilityUpdate) {
		this.typeMutabilityUpdate = typeMutabilityUpdate;
	}

	/**
	 * Gets the type mutabiltiy delete.
	 *
	 * @return Boolean - the type mutabiltiy delete
	 */
	@Column(name = "tm_delete", nullable = true)
	public Boolean getTypeMutabiltiyDelete() {
		return typeMutabiltiyDelete;
	}

	/**
	 * Sets the type mutabiltiy delete.
	 *
	 * @param typeMutabiltiyDelete
	 *            the type mutabiltiy delete
	 */
	public void setTypeMutabiltiyDelete(Boolean typeMutabiltiyDelete) {
		this.typeMutabiltiyDelete = typeMutabiltiyDelete;
	}

	/**
	 * Gets the secondary types.
	 *
	 * @return List - the secondary types
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = WidaMetaDataConstants.METADATA_TYPE_TYPE_TO_SECONDARY_TYPE_TABLE, schema = WidaMetaDataConstants.METADATA_DB_SCHEMA, joinColumns = @JoinColumn(name = "id"), inverseJoinColumns = @JoinColumn(name = "secondary_type_id"))
	public Set<TypeSecondary> getSecondaryTypes() {
		return secondaryTypes;
	}

	/**
	 * Sets the secondary types.
	 *
	 * @param secondaryTypes
	 *            the secondary types
	 */
	public void setSecondaryTypes(Set<TypeSecondary> secondaryTypes) {
		this.secondaryTypes = secondaryTypes;
	}

	/**
	 * Gets the property definitions.
	 *
	 * @return List - the property definitions
	 */
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = WidaMetaDataConstants.METADATA_TYPE_TYPE_TO_PROPERTY_TYPE_TABLE, schema = WidaMetaDataConstants.METADATA_DB_SCHEMA, joinColumns = @JoinColumn(name = "id"), inverseJoinColumns = @JoinColumn(name = "prop_def_id"))
	public List<PropertyDefinitionBase<?>> getPropertyDefinitionsList() {
		return propertyDefinitionsList;
	}

	/**
	 * Sets the property definitions.
	 *
	 * @param propertyDefinitions
	 *            the property definitions
	 */
	public void setPropertyDefinitionsList(
			List<PropertyDefinitionBase<?>> propertyDefinitions) {
		this.propertyDefinitionsList = propertyDefinitions;
	}

	/**
	 * Gets the tablename.
	 *
	 * @return String - the tablename
	 */
	@Column(name = "tablename", nullable = false, length = WidaMetaDataConstants.MAX_NAME_LENGTH)
	public String getTablename() {
		if (tablename == null)
			tablename = WidaMetaDataConstants.METADATA_CONTENT_TABLE_PREFIX
					+ TypeHelper.convertTypeIdToTableOrColumnName(getId());
		return tablename;
	}

	/**
	 * Sets the tablename.
	 *
	 * @param tablename
	 *            the tablename
	 */
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.chemistry.opencmis.commons.definitions.TypeDefinition#
	 * getPropertyDefinitions()
	 */
	@Transient
	public Map<String, PropertyDefinition<?>> getPropertyDefinitions() {
		return getPropertyDefinitions(true);
	}

	/**
	 * Gets the property definitions.
	 *
	 * @param paramAddInherited
	 *            the param add inherited
	 * @return Map - the property definitions
	 */
	public Map<String, PropertyDefinition<?>> getPropertyDefinitions(boolean paramAddInherited) {
		Map<String, PropertyDefinition<?>> resultMap = new LinkedHashMap<>();
		// do not use the internal attribute because inherited properties are missing
		// there.
		List<PropertyDefinitionBase<?>> internalList = null;
		if (paramAddInherited) {
			internalList = getPropertyDefinitionsListWithInherited();
		} else {
			internalList = getPropertyDefinitionsList();
		}
		if (internalList != null) {
			for (PropertyDefinitionBase<?> propDef : internalList) {
				resultMap.put(propDef.getId(), propDef);
			}
		}
		return resultMap;
	}

	/**
	 * Gets the parent.
	 *
	 * @param <T>
	 *            the generic type
	 * @return TypeBase - the parent
	 */
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "parent_id", insertable = false, updatable = false)
	public abstract <T extends TypeBase> T getParent();

	/**
	 * Sets the parent.
	 *
	 * @param <T>
	 *            the generic type
	 * @param paramParent
	 *            the param parent
	 */
	public <T extends TypeBase> void  setParent(T paramParent) {
		// object association is not used only parent_id so set parent_id
		if (paramParent != null)
			setParentTypeId(paramParent.getId());
	}

	/**
	 * Gets the property definitions list with inherited.
	 *
	 * @return List - the property definitions list with inherited
	 */
	@Transient
	private List<PropertyDefinitionBase<?>> getPropertyDefinitionsListWithInherited() {
		List<PropertyDefinitionBase<?>> completeList = new ArrayList<>();
		completeList.addAll(propertyDefinitionsList);
		if (isInheritProperties()) {
			List<PropertyDefinitionBase<?>> parentList = getParent()
					.getPropertyDefinitionsListWithInherited();
			completeList.addAll(parentList);
		}
		return completeList;
	}

	/**
	 * Sets the property definitions.
	 *
	 * @param newPropertyDefinitions
	 *            the new property definitions
	 */
	public void setPropertyDefinitions(Map<String, PropertyDefinition<?>> newPropertyDefinitions) {
		propertyDefinitionsList = new ArrayList<>();
		if (newPropertyDefinitions != null) {
			Collection<PropertyDefinition<?>> propertyDefinitions = newPropertyDefinitions.values();
			for (PropertyDefinition<?> propDef : propertyDefinitions) {
				addPropertyDefinition(propDef);
			}
		}
	}

	/**
	 * Adds the property definition.
	 *
	 * @param paramPropertyDefinition
	 *            the param property definition
	 */
	public void addPropertyDefinition(PropertyDefinition<?> paramPropertyDefinition) {
		if (paramPropertyDefinition != null) {
			if (paramPropertyDefinition instanceof PropertyDefinitionBase) {
				propertyDefinitionsList
						.add((PropertyDefinitionBase<?>) paramPropertyDefinition);
			} else {
				PropertyDefinitionBase<?> repoVersion = createRepoPropDefByPropDef(
						paramPropertyDefinition);
				propertyDefinitionsList.add(repoVersion);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.chemistry.opencmis.commons.definitions.TypeDefinition#
	 * getTypeMutability()
	 */
	@Transient
	public TypeMutability getTypeMutability() {
		TypeMutabilityImpl typeMutability = new TypeMutabilityImpl();
		typeMutability.setCanCreate(getTypeMutabilityCreate());
		typeMutability.setCanDelete(getTypeMutabiltiyDelete());
		typeMutability.setCanUpdate(getTypeMutabilityUpdate());
		return typeMutability;
	}

	/**
	 * Sets the type mutability.
	 *
	 * @param paramTypeMutability
	 *            the param type mutability
	 */
	public void setTypeMutability(TypeMutability paramTypeMutability) {
		if (paramTypeMutability != null) {
			setTypeMutabilityCreate(paramTypeMutability.canCreate());
			setTypeMutabilityUpdate(paramTypeMutability.canUpdate());
			setTypeMutabiltiyDelete(paramTypeMutability.canDelete());
		}
		super.setTypeMutability(paramTypeMutability);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.chemistry.opencmis.commons.definitions.MutableTypeDefinition#
	 * removePropertyDefinition(java.lang.String)
	 */
	@Override
	public void removePropertyDefinition(String propertyId) {
		if (propertyId == null) {
			return;
		}
		if (propertyDefinitionsList != null) {
			PropertyDefinitionBase<?> repoPropDef = null;
			for (PropertyDefinitionBase<?> propDef : propertyDefinitionsList) {
				if (propDef.getId().equals(propertyId)) {
					repoPropDef = propDef;
				}
			}
			if (repoPropDef != null)
				propertyDefinitionsList.remove(repoPropDef);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.chemistry.opencmis.commons.definitions.MutableTypeDefinition#
	 * removeAllPropertyDefinitions()
	 */
	@Override
	public void removeAllPropertyDefinitions() {
		propertyDefinitionsList = new ArrayList<>();
	}

	/**
	 * Checks if is inherit properties.
	 *
	 * @return the boolean
	 */
	@Column(name = "inherit_properties")
	public Boolean isInheritProperties() {
		return inheritProperties;
	}

	/**
	 * Sets the checks if is inherit properties.
	 *
	 * @param inheritProperties
	 *            the inherit properties
	 */
	public void setInheritProperties(Boolean inheritProperties) {
		this.inheritProperties = inheritProperties;
	}

}
