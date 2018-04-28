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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Transient;

import de.elnarion.web.wida.metadataservice.WidaMetaDataConstants;

/**
 * The Class Document.
 */
@Entity
@DiscriminatorValue("document")
@SecondaryTable(name = WidaMetaDataConstants.METADATA_CONTENT_DOCUMENT_TABLE,  pkJoinColumns = {
		@PrimaryKeyJoinColumn(name = WidaMetaDataConstants.METADATA_ID_COLUMN_NAME, referencedColumnName = WidaMetaDataConstants.METADATA_ID_COLUMN_NAME) })
public class Document extends BaseItem {

	/**
	 * Instantiates a new document.
	 */
	public Document() {
	}

	// cmis properties

	/** The is immutable. */
	private boolean isImmutable = true;

	/** The is la version. */
	private boolean isLastVersion = true;

	/** The is major version. */
	private boolean isMajorVersion = true;

	/** The is la major version. */
	private boolean isLastMajorVersion = true;

	/** The version label. */
	private String versionLabel = "1.0";

	/** The version series id. */
	@SuppressWarnings("unused")
	private String versionSeriesId;

	/** The is version series checked out. */
	private boolean isVersionSeriesCheckedOut = false;

	/** The version series checked out by. */
	private String versionSeriesCheckedOutBy;

	/** The version series checked out id. */
	private String versionSeriesCheckedOutId = null;

	/** The checkin comment. */
	private String checkinComment;

	/** The content stream length. */
	private BigInteger contentStreamLength;

	/** The content stream mime type. */
	private String contentStreamMimeType;

	/** The content stream file name. */
	private String contentStreamFileName;

	/** The content stream id. */
	private String contentStreamId;

	// no cmis property

	/** The storage id. */
	private String storageId;

	/**
	 * Checks if is immutable.
	 *
	 * @return true, if is immutable
	 */
	@Transient
	public boolean isImmutable() {
		return isImmutable;
	}

	/**
	 * Sets the immutable.
	 *
	 * @param isImmutable
	 *            the is immutable
	 */
	public void setImmutable(boolean isImmutable) {
		this.isImmutable = isImmutable;
	}

	/**
	 * Checks if is last version.
	 *
	 * @return true, if is last version
	 */
	@Transient
	public boolean isLastVersion() {
		return isLastVersion;
	}

	/**
	 * Sets the last version.
	 *
	 * @param isLastVersion
	 *            the is last version
	 */
	public void setLastVersion(boolean isLastVersion) {
		this.isLastVersion = isLastVersion;
	}

	/**
	 * Checks if is major version.
	 *
	 * @return true, if is major version
	 */
	@Transient
	public boolean isMajorVersion() {
		return isMajorVersion;
	}

	/**
	 * Sets the major version.
	 *
	 * @param isMajorVersion
	 *            the is major version
	 */
	public void setMajorVersion(boolean isMajorVersion) {
		this.isMajorVersion = isMajorVersion;
	}

	/**
	 * Checks if is last major version.
	 *
	 * @return true, if is last major version
	 */
	@Transient
	public boolean isLastMajorVersion() {
		return isLastMajorVersion;
	}

	/**
	 * Sets the last major version.
	 *
	 * @param isLastMajorVersion
	 *            the is last major version
	 */
	public void setLastMajorVersion(boolean isLastMajorVersion) {
		this.isLastMajorVersion = isLastMajorVersion;
	}

	/**
	 * Gets the version label.
	 *
	 * @return String - the version label
	 */
	@Transient
	public String getVersionLabel() {
		return versionLabel;
	}

	/**
	 * Sets the version label.
	 *
	 * @param versionLabel
	 *            the version label
	 */
	public void setVersionLabel(String versionLabel) {
		this.versionLabel = versionLabel;
	}

	/**
	 * Gets the version series id.
	 *
	 * @return String - the version series id
	 */
	@Transient
	public String getVersionSeriesId() {
		// as long as the repository does not support versioning the version series id
		// equals the object id
		return getObjectId();
	}

	/**
	 * Sets the version series id.
	 *
	 * @param versionSeriesId
	 *            the version series id
	 */
	public void setVersionSeriesId(String versionSeriesId) {
		// do nothing (look above in the getter method)
	}

	/**
	 * Checks if is version series checked out.
	 *
	 * @return true, if is version series checked out
	 */
	@Transient
	public boolean isVersionSeriesCheckedOut() {
		return isVersionSeriesCheckedOut;
	}

	/**
	 * Sets the version series checked out.
	 *
	 * @param isVersionSeriesCheckedOut
	 *            the is version series checked out
	 */
	public void setVersionSeriesCheckedOut(boolean isVersionSeriesCheckedOut) {
		this.isVersionSeriesCheckedOut = isVersionSeriesCheckedOut;
	}

	/**
	 * Gets the version series checked out by.
	 *
	 * @return String - the version series checked out by
	 */
	@Transient
	public String getVersionSeriesCheckedOutBy() {
		return versionSeriesCheckedOutBy;
	}

	/**
	 * Sets the version series checked out by.
	 *
	 * @param versionSeriesCheckedOutBy
	 *            the version series checked out by
	 */
	public void setVersionSeriesCheckedOutBy(String versionSeriesCheckedOutBy) {
		this.versionSeriesCheckedOutBy = versionSeriesCheckedOutBy;
	}

	/**
	 * Gets the version series checked out id.
	 *
	 * @return String - the version series checked out id
	 */
	@Transient
	public String getVersionSeriesCheckedOutId() {
		return versionSeriesCheckedOutId;
	}

	/**
	 * Sets the version series checked out id.
	 *
	 * @param versionSeriesCheckedOutId
	 *            the version series checked out id
	 */
	public void setVersionSeriesCheckedOutId(String versionSeriesCheckedOutId) {
		this.versionSeriesCheckedOutId = versionSeriesCheckedOutId;
	}

	/**
	 * Gets the checkin comment.
	 *
	 * @return String - the checkin comment
	 */
	@Transient
	public String getCheckinComment() {
		return checkinComment;
	}

	/**
	 * Sets the checkin comment.
	 *
	 * @param checkinComment
	 *            the checkin comment
	 */
	public void setCheckinComment(String checkinComment) {
		this.checkinComment = checkinComment;
	}

	/**
	 * Gets the content stream length.
	 *
	 * @return BigInteger - the content stream length
	 */
	@Column(name = "CONTENT_STREAM_LENGTH", table = WidaMetaDataConstants.METADATA_CONTENT_DOCUMENT_TABLE, nullable = false)
	public BigInteger getContentStreamLength() {
		return contentStreamLength;
	}

	/**
	 * Sets the content stream length.
	 *
	 * @param contentStreamLength
	 *            the content stream length
	 */
	public void setContentStreamLength(BigInteger contentStreamLength) {
		this.contentStreamLength = contentStreamLength;
	}

	/**
	 * Gets the content stream mime type.
	 *
	 * @return String - the content stream mime type
	 */
	@Column(name = "CONTENT_STREAM_MIME_TYPE", table = WidaMetaDataConstants.METADATA_CONTENT_DOCUMENT_TABLE, length = 128, nullable = false)
	public String getContentStreamMimeType() {
		return contentStreamMimeType;
	}

	/**
	 * Sets the content stream mime type.
	 *
	 * @param contentStreamMimeType
	 *            the content stream mime type
	 */
	public void setContentStreamMimeType(String contentStreamMimeType) {
		this.contentStreamMimeType = contentStreamMimeType;
	}

	/**
	 * Gets the content stream file name.
	 *
	 * @return String - the content stream file name
	 */
	@Column(name = "CONTENT_STREAM_FILE_NAME", table = WidaMetaDataConstants.METADATA_CONTENT_DOCUMENT_TABLE, length = 256, nullable = false)
	public String getContentStreamFileName() {
		return contentStreamFileName;
	}

	/**
	 * Sets the content stream file name.
	 *
	 * @param contentStreamFileName
	 *            the content stream file name
	 */
	public void setContentStreamFileName(String contentStreamFileName) {
		this.contentStreamFileName = contentStreamFileName;
	}

	/**
	 * Gets the content stream id.
	 *
	 * @return String - the content stream id
	 */
	@Column(name = "CONTENT_STREAM_ID", table = WidaMetaDataConstants.METADATA_CONTENT_DOCUMENT_TABLE, nullable = false, unique = true)
	public String getContentStreamId() {
		return contentStreamId;
	}

	/**
	 * Sets the content stream id.
	 *
	 * @param contentStreamId
	 *            the content stream id
	 */
	public void setContentStreamId(String contentStreamId) {
		this.contentStreamId = contentStreamId;
	}

	/**
	 * Gets the storage id.
	 *
	 * @return String - the storage id
	 */
	@Column(name = "STORAGE_ID", table = WidaMetaDataConstants.METADATA_CONTENT_DOCUMENT_TABLE, length = 400)
	public String getStorageId() {
		return storageId;
	}

	/**
	 * Sets the storage id.
	 *
	 * @param storageId
	 *            the id to retrieve this object from the storage service
	 */
	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}

	/**
	 * Inits the UUID for content stream id.
	 */
	@PrePersist
	public void initUUIDs()
	{
		if(contentStreamId==null)
			contentStreamId = UUID.randomUUID().toString();
	}
	

	public Document clone() {
		Document document = new Document();
		document.setBaseTypeId(this.getBaseTypeId());
		document.setCheckinComment(this.getCheckinComment());
		document.setContentStreamFileName(this.getContentStreamFileName());
		document.setContentStreamId(this.getContentStreamId());
		document.setContentStreamLength(this.getContentStreamLength());
		document.setContentStreamMimeType(this.getContentStreamMimeType());
		document.setCreatedBy(this.getCreatedBy());
		document.setCreationDate(this.getCreationDate());
		document.setImmutable(this.isImmutable());
		document.setLastMajorVersion(this.isLastMajorVersion());
		document.setLastModificationDate(this.getLastModificationDate());
		document.setLastModifiedBy(this.getLastModifiedBy());
		document.setLastVersion(this.isLastVersion());
		document.setMajorVersion(this.isMajorVersion());
		document.setName(this.getName());
		document.setObjectTypeId(this.getObjectTypeId());
		document.setParent(this.getParent());
		Map<String, Object> properties = new HashMap<>();
		properties.putAll(this.getProperties());
		document.setProperties(properties);
		Set<String> secondaryTypeIds = new TreeSet<>();
		secondaryTypeIds.addAll(this.getSecondaryTypeIds());
		document.setSecondaryTypeIds(secondaryTypeIds);
		document.setStorageId(this.getStorageId());
		document.setVersionLabel(this.getVersionLabel());
		document.setVersionSeriesCheckedOut(this.isVersionSeriesCheckedOut);
		document.setVersionSeriesCheckedOutBy(this.getVersionSeriesCheckedOutBy());
		document.setVersionSeriesCheckedOutId(this.getVersionSeriesCheckedOutId());
		document.setVersionSeriesId(this.getVersionSeriesId());
		document.setObjectId(this.getObjectId());
		document.setId(this.getId());
		return document;
		
	}
}
