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
package de.elnarion.web.wida.metadataservice.domain.contentmetadata;

import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.elnarion.web.wida.metadataservice.WidaMetaDataConstants;

/**
 * The Class BaseItem.
 */
@Entity
@Table(name = WidaMetaDataConstants.METADATA_CONTENT_ITEM_TABLE, schema = WidaMetaDataConstants.METADATA_DB_SCHEMA, indexes = {
		@Index(name = WidaMetaDataConstants.METADATA_INDEX_PREFIX+WidaMetaDataConstants.METADATA_CONTENT_ITEM_TABLE+"_object_id", columnList = "object_id", unique = true),
		@Index(name = WidaMetaDataConstants.METADATA_INDEX_PREFIX+WidaMetaDataConstants.METADATA_CONTENT_ITEM_TABLE+"_name", columnList = "name"),
		@Index(name = WidaMetaDataConstants.METADATA_INDEX_PREFIX+WidaMetaDataConstants.METADATA_CONTENT_ITEM_TABLE+"_discriminator", columnList = "discriminator"),
		@Index(name = WidaMetaDataConstants.METADATA_INDEX_PREFIX+WidaMetaDataConstants.METADATA_CONTENT_ITEM_TABLE+"_parent_id", columnList = "parent_id") })
@DiscriminatorColumn(name = "discriminator")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class BaseItem {

	// cmis properties

	/** The object id. */
	private String objectId;

	/** The name. */
	private String name;

	/** The base type id. */
	private String baseTypeId;

	/** The object type id. */
	private String objectTypeId;

	/** The created by. */
	private String createdBy;

	/** The creation date. */
	private GregorianCalendar creationDate;

	/** The last modified by. */
	private String lastModifiedBy;

	/** The last modification date. */
	private GregorianCalendar lastModificationDate;

	// non cmis properties

	/** The id. */
	private Long id;

	/** The parent. */
	private Folder parent;
	
	/** The secondary type ids. */
	private Set<String> secondaryTypeIds;
	
	/** The properties. */
	private Map<String, Object> properties;

	/**
	 * Gets the name.
	 *
	 * @return String - the name
	 */
	@Column(name = "name", length = 256, nullable = false)
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the object id.
	 *
	 * @return String - the object id
	 */
	@Column(name = "object_id", length = 144, nullable = false)
	public String getObjectId() {
		return objectId;
	}

	/**
	 * Sets the object id.
	 *
	 * @param objectId
	 *            the object id
	 */
	public void setId(String objectId) {
		this.setObjectId(objectId);
	}

	/**
	 * Gets the base type id.
	 *
	 * @return String - the base type id
	 */
	@Column(name = "base_type_id", length = 50, nullable = false)
	public String getBaseTypeId() {
		return baseTypeId;
	}

	/**
	 * Sets the base type id.
	 *
	 * @param baseTypeId
	 *            the base type id
	 */
	public void setBaseTypeId(String baseTypeId) {
		this.baseTypeId = baseTypeId;
	}

	/**
	 * Gets the object type id.
	 *
	 * @return String - the object type id
	 */
	@Column(name = "object_type_id", length = 50, nullable = false)
	public String getObjectTypeId() {
		return objectTypeId;
	}

	/**
	 * Sets the object type id.
	 *
	 * @param objectTypeId
	 *            the object type id
	 */
	public void setObjectTypeId(String objectTypeId) {
		this.objectTypeId = objectTypeId;
	}

	/**
	 * Gets the created by.
	 *
	 * @return String - the created by
	 */
	@Column(name = "created_by", length = 256, nullable = false)
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Sets the created by.
	 *
	 * @param createdBy
	 *            the created by
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Gets the creation date.
	 *
	 * @return Calendar - the creation date
	 */
	@Column(name = "creation_date", nullable = false)
	public GregorianCalendar getCreationDate() {
		return creationDate;
	}

	/**
	 * Sets the creation date.
	 *
	 * @param creationDate
	 *            the creation date
	 */
	public void setCreationDate(GregorianCalendar creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Gets the last modified by.
	 *
	 * @return String - the last modified by
	 */
	@Column(name = "last_modified_by", length = 256, nullable = false)
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	/**
	 * Sets the last modified by.
	 *
	 * @param lastModifiedBy
	 *            the last modified by
	 */
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	/**
	 * Gets the last modification date.
	 *
	 * @return Calendar - the last modification date
	 */
	@Column(name = "last_modification_date", nullable = false)
	public GregorianCalendar getLastModificationDate() {
		return lastModificationDate;
	}

	/**
	 * Sets the last modification date.
	 *
	 * @param lastModificationDate
	 *            the last modification date
	 */
	public void setLastModificationDate(GregorianCalendar lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	/**
	 * Gets the parent.
	 *
	 * @return Folder - the parent
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	public Folder getParent() {
		return parent;
	}

	/**
	 * Sets the parent.
	 *
	 * @param parent
	 *            the parent
	 */
	public void setParent(Folder parent) {
		this.parent = parent;
	}

	/**
	 * Gets the id.
	 *
	 * @return Long - the id
	 */
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Sets the object id.
	 *
	 * @param objectId
	 *            the object id
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	/**
	 * Inits the object id.
	 */
	@PrePersist
	public void initObjectId()
	{
		if(objectId==null)
			objectId = UUID.randomUUID().toString();
	}

	/**
	 * Gets the secondary type ids.
	 *
	 * @return Set - the secondary type ids
	 */
	@ElementCollection
	@CollectionTable(schema = "wida", name = "cm_item_secondary_types", joinColumns = @JoinColumn(name = "item_id"))
	@Column(name = "secondary_type_id")
	public Set<String> getSecondaryTypeIds() {
		return secondaryTypeIds;
	}

	/**
	 * Sets the secondary type ids.
	 *
	 * @param secondaryTypeIds
	 *            the secondary type ids
	 */
	public void setSecondaryTypeIds(Set<String> secondaryTypeIds) {
		this.secondaryTypeIds = secondaryTypeIds;
	}

	/**
	 * Gets the properties.
	 *
	 * @return Map - the properties
	 */
	@Transient
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * Sets the additional properties.
	 *
	 * @param properties
	 *            the additional properties
	 */
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
}
