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
package de.elnarion.web.wida.ejb.objectservice.impl;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.BulkUpdateObjectIdAndChangeToken;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.FailedToDeleteData;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.data.RenditionData;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNotSupportedException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.spi.Holder;

import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.ejb.objectservice.WidaObjectService;
import de.elnarion.web.wida.ejb.storageservice.WidaStorageService;
import de.elnarion.web.wida.ejb.storageservice.WidaStorageServiceException;
import de.elnarion.web.wida.metadataservice.WidaContentMetaDataService;
import de.elnarion.web.wida.metadataservice.domain.contentmetadata.Document;
import de.elnarion.web.wida.metadataservice.domain.contentmetadata.Folder;

/**
 * The class implements id-based CRUD (Create, Retrieve, Update, Delete)
 * operations on objects in this repository.
 */
@Stateless
public class WidaObjectServiceImpl implements WidaObjectService {

	/** The wida content service. */
	@EJB
	private WidaStorageService widaStorageService;

	/** The wida content meta data service. */
	@EJB
	private WidaContentMetaDataService widaContentMetaDataService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.ejb.objectservice.WidaObjectService#createDocument(org.
	 * apache.chemistry.opencmis.commons.data.Properties, java.lang.String,
	 * org.apache.chemistry.opencmis.commons.data.ContentStream,
	 * org.apache.chemistry.opencmis.commons.enums.VersioningState, java.util.List,
	 * org.apache.chemistry.opencmis.commons.data.Acl,
	 * org.apache.chemistry.opencmis.commons.data.Acl)
	 */
	@Override
	public String createDocument(String userName, Properties properties, String folderId, ContentStream contentStream,
			VersioningState versioningState, List<String> policies, Acl addAces, Acl removeAces) {
		// ignore the following parameters:
		// - policies -> policies not supported
		// - addAces -> ACLs not supported
		// - removeAces -> ACLs not supported

		// check versioning state
		if (VersioningState.NONE != versioningState) {
			throw new CmisConstraintException("Versioning not supported!", WidaErrorConstants.NOT_SUPPORTED_VERSIONING);
		}

		// get parent File
		Folder folder = widaContentMetaDataService.getFolder(folderId);
		if (folder == null) {
			throw new CmisObjectNotFoundException("Parent is not a folder!", WidaErrorConstants.NOT_FOUND_OBJECT);
		}

		// set general document properties
		Document document = new Document();
		if (contentStream.getFileName() == null)
			throw new CmisInvalidArgumentException("Content stream is missing a filename",
					WidaErrorConstants.INVALID_ARGUMENT_MISSING_FILENAME_FOR_CONTENT_STREAM);
		document.setContentStreamFileName(contentStream.getFileName());
		// length can be null so accept all values
		document.setContentStreamLength(contentStream.getBigLength());
		if (contentStream.getMimeType() == null)
			throw new CmisInvalidArgumentException("Mimetype for content stream is missing",
					WidaErrorConstants.INVALID_ARGUMENT_MISSING_MIMETYPE_FOR_CONTENT_STREAM);
		document.setContentStreamMimeType(contentStream.getMimeType());
		// user is part of callcontext so no check is needed
		document.setCreatedBy(userName);
		document.setCreationDate((GregorianCalendar) GregorianCalendar.getInstance());
		document.setImmutable(true);
		document.setLastMajorVersion(true);
		document.setLastModificationDate(document.getCreationDate());
		// user is part of callcontext so no check is needed
		document.setLastModifiedBy(userName);
		document.setLastVersion(true);
		document.setMajorVersion(true);
		document.setParent(folder);
		document.setVersionLabel(null);
		document.setVersionSeriesCheckedOut(false);
		document.setVersionSeriesCheckedOutBy(null);
		document.setVersionSeriesCheckedOutId(null);
		document.setVersionSeriesId(null);

		Map<String, PropertyData<?>> propertyMap = properties.getProperties();

		if (propertyMap.get(PropertyIds.NAME) != null) {
			PropertyData<?> propertyData = propertyMap.get(PropertyIds.NAME);
			document.setName((String) propertyData.getFirstValue());
		} else if (propertyMap.get(PropertyIds.OBJECT_TYPE_ID) != null) {
			PropertyData<?> propertyData = propertyMap.get(PropertyIds.OBJECT_TYPE_ID);
			document.setObjectTypeId((String) propertyData.getFirstValue());
		} else if (propertyMap.get(PropertyIds.SECONDARY_OBJECT_TYPE_IDS) != null) {
			PropertyData<?> secondaryIds = propertyMap.get(PropertyIds.SECONDARY_OBJECT_TYPE_IDS);
			if (secondaryIds != null) {
				@SuppressWarnings("unchecked")
				List<String> values = (List<String>) secondaryIds.getValues();
				if (values != null && values.size() > 0) {
					Set<String> secondarySet = new TreeSet<>();
					secondarySet.addAll(values);
					document.setSecondaryTypeIds(secondarySet);
				}
			}
		}

		String storageid = null;
		try {
			storageid = widaStorageService.saveFile(contentStream.getStream(), contentStream.getBigLength());
		} catch (WidaStorageServiceException e) {
			throw new CmisRuntimeException(e.getMessage(), WidaErrorConstants.RUNTIME_EXCEPTION_STORAGE_WRITE_EXCEPTION,
					e);
		}
		document.setStorageId(storageid);
		String objectId = widaContentMetaDataService.createDocument(document, properties);
		return objectId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#
	 * createDocumentFromSource(java.lang.String,
	 * org.apache.chemistry.opencmis.commons.data.Properties, java.lang.String,
	 * org.apache.chemistry.opencmis.commons.enums.VersioningState, java.util.List,
	 * org.apache.chemistry.opencmis.commons.data.Acl,
	 * org.apache.chemistry.opencmis.commons.data.Acl)
	 */
	@Override
	public String createDocumentFromSource(String userName, String sourceId, Properties properties, String folderId,
			VersioningState versioningState, List<String> policies, Acl addAces, Acl removeAces) {
		// ignore the following parameters:
		// - policies -> policies not supported
		// - addAces -> ACLs not supported
		// - removeAces -> ACLs not supported
		
		// check versioning state
		if (VersioningState.NONE != versioningState) {
			throw new CmisConstraintException("Versioning not supported!", WidaErrorConstants.NOT_SUPPORTED_VERSIONING);
		}

		// get parent File
		Folder folder = widaContentMetaDataService.getFolder(folderId);
		if (folder == null) {
			throw new CmisObjectNotFoundException("Parent is not a folder!", WidaErrorConstants.NOT_FOUND_OBJECT);
		}

		Document sourceDocument = widaContentMetaDataService.getDocument(sourceId);
		Document targetDocument = sourceDocument.clone();
		targetDocument.setId(null);
		targetDocument.setObjectId(null);
		targetDocument.setParent(folder);
		targetDocument.setProperties(null);
		String objectId = widaContentMetaDataService.createDocument(targetDocument, properties);
		return objectId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.ejb.objectservice.WidaObjectService#createFolder(org.
	 * apache.chemistry.opencmis.commons.data.Properties, java.lang.String,
	 * java.util.List, org.apache.chemistry.opencmis.commons.data.Acl,
	 * org.apache.chemistry.opencmis.commons.data.Acl)
	 */
	@Override
	public String createFolder(String userName, Properties properties, String folderId, List<String> policies,
			Acl addAces, Acl removeAces) {
		// ignore the following parameters:
		// - policies -> policies not supported
		// - addAces -> ACLs not supported
		// - removeAces -> ACLs not supported

		// get parent File
		Folder parentfolder = widaContentMetaDataService.getFolder(folderId);
		if (parentfolder == null) {
			throw new CmisObjectNotFoundException("Parent is not a folder!", WidaErrorConstants.NOT_FOUND_OBJECT);
		}

		// set general folder properties
		Folder folder = new Folder();
		// user is part of callcontext so no check is needed
		folder.setCreatedBy(userName);
		folder.setCreationDate((GregorianCalendar) GregorianCalendar.getInstance());
		folder.setLastModificationDate(folder.getCreationDate());
		// user is part of callcontext so no check is needed
		folder.setLastModifiedBy(userName);
		folder.setParent(folder);

		Map<String, PropertyData<?>> propertyMap = properties.getProperties();

		if (propertyMap.get(PropertyIds.NAME) != null) {
			PropertyData<?> propertyData = propertyMap.get(PropertyIds.NAME);
			folder.setName((String) propertyData.getFirstValue());
		} else if (propertyMap.get(PropertyIds.OBJECT_TYPE_ID) != null) {
			PropertyData<?> propertyData = propertyMap.get(PropertyIds.OBJECT_TYPE_ID);
			folder.setObjectTypeId((String) propertyData.getFirstValue());
		} else if (propertyMap.get(PropertyIds.SECONDARY_OBJECT_TYPE_IDS) != null) {
			PropertyData<?> secondaryIds = propertyMap.get(PropertyIds.SECONDARY_OBJECT_TYPE_IDS);
			if (secondaryIds != null) {
				@SuppressWarnings("unchecked")
				List<String> values = (List<String>) secondaryIds.getValues();
				if (values != null && values.size() > 0) {
					Set<String> secondarySet = new TreeSet<>();
					secondarySet.addAll(values);
					folder.setSecondaryTypeIds(secondarySet);
				}
			}
		}

		String objectId = widaContentMetaDataService.createFolder(folder, properties);
		return objectId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.ejb.objectservice.WidaObjectService#createRelationship(
	 * org.apache.chemistry.opencmis.commons.data.Properties, java.util.List,
	 * org.apache.chemistry.opencmis.commons.data.Acl,
	 * org.apache.chemistry.opencmis.commons.data.Acl)
	 */
	@Override
	public String createRelationship(String userName, Properties properties, List<String> policies, Acl addAces,
			Acl removeAces) {
		throw new CmisNotSupportedException("Relationships are not supported by this repository",
				WidaErrorConstants.NOT_SUPPORTED_RELATIONSHIPS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.ejb.objectservice.WidaObjectService#createPolicy(org.
	 * apache.chemistry.opencmis.commons.data.Properties, java.lang.String,
	 * java.util.List, org.apache.chemistry.opencmis.commons.data.Acl,
	 * org.apache.chemistry.opencmis.commons.data.Acl)
	 */
	@Override
	public String createPolicy(String userName, Properties properties, String folderId, List<String> policies,
			Acl addAces, Acl removeAces) {
		throw new CmisNotSupportedException("Policies are not supported by this repository",
				WidaErrorConstants.NOT_SUPPORTED_POLICIES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#createItem(org.
	 * apache.chemistry.opencmis.commons.data.Properties, java.lang.String,
	 * java.util.List, org.apache.chemistry.opencmis.commons.data.Acl,
	 * org.apache.chemistry.opencmis.commons.data.Acl)
	 */
	@Override
	public String createItem(String userName, Properties properties, String folderId, List<String> policies,
			Acl addAces, Acl removeAces) {
		throw new CmisNotSupportedException("Simple items are not supported by this repository",
				WidaErrorConstants.NOT_SUPPORTED_ITEMS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.ejb.objectservice.WidaObjectService#getObject(java.lang.
	 * String, java.lang.String, java.lang.Boolean,
	 * org.apache.chemistry.opencmis.commons.enums.IncludeRelationships,
	 * java.lang.String, java.lang.Boolean, java.lang.Boolean)
	 */
	@Override
	public ObjectData getObject(String objectId, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
			Boolean includeAcl) {
		// ignore the following parameters:
		// - includeAllowableActions -> not supported
		// - includeRelationships -> Relationships are not supported
		// - renditionFilter -> Rendition not supported
		// - includePolicyIds -> policies not supported
		// - includeAcl -> ACLs not supported
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.ejb.objectservice.WidaObjectService#getObjectByPath(java
	 * .lang.String, java.lang.String, java.lang.Boolean,
	 * org.apache.chemistry.opencmis.commons.enums.IncludeRelationships,
	 * java.lang.String, java.lang.Boolean, java.lang.Boolean)
	 */
	@Override
	public ObjectData getObjectByPath(String path, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
			Boolean includeAcl) {
		// ignore the following parameters:
		// - includeAllowableActions -> not supported
		// - includeRelationships -> Relationships are not supported
		// - renditionFilter -> Rendition not supported
		// - includePolicyIds -> policies not supported
		// - includeAcl -> ACLs not supported
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.ejb.objectservice.WidaObjectService#getContentStream(
	 * java.lang.String, java.lang.String, java.math.BigInteger,
	 * java.math.BigInteger)
	 */
	@Override
	public ContentStream getContentStream(String objectId, String streamId, BigInteger offset, BigInteger length) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.ejb.objectservice.WidaObjectService#getRenditions(java.
	 * lang.String, java.lang.String, java.math.BigInteger, java.math.BigInteger)
	 */
	@Override
	public List<RenditionData> getRenditions(String objectId, String renditionFilter, BigInteger maxItems,
			BigInteger skipCount) {
		throw new CmisNotSupportedException("Renditions are not supported by this repository",
				WidaErrorConstants.NOT_SUPPORTED_RENDITIONS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.ejb.objectservice.WidaObjectService#updateProperties(org
	 * .apache.chemistry.opencmis.commons.spi.Holder,
	 * org.apache.chemistry.opencmis.commons.spi.Holder,
	 * org.apache.chemistry.opencmis.commons.data.Properties)
	 */
	@Override
	public void updateProperties(String userName, Holder<String> objectId, Holder<String> changeToken,
			Properties properties) {
		// ignore change token because it is not supported
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.ejb.objectservice.WidaObjectService#bulkUpdateProperties
	 * (java.util.List, org.apache.chemistry.opencmis.commons.data.Properties,
	 * java.util.List, java.util.List)
	 */
	@Override
	public List<BulkUpdateObjectIdAndChangeToken> bulkUpdateProperties(String userName,
			List<BulkUpdateObjectIdAndChangeToken> objectIdAndChangeToken, Properties properties,
			List<String> addSecondaryTypeIds, List<String> removeSecondaryTypeIds) {
		throw new CmisNotSupportedException("Bulk updates are not supported by this repository",
				WidaErrorConstants.NOT_SUPPORTED_BULK_UPDATES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#moveObject(org.
	 * apache.chemistry.opencmis.commons.spi.Holder, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void moveObject(String userName, Holder<String> objectId, String targetFolderId, String sourceFolderId) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.ejb.objectservice.WidaObjectService#deleteObject(java.
	 * lang.String, java.lang.Boolean)
	 */
	@Override
	public void deleteObject(String userName, String objectId, Boolean allVersions) {
		// ignore allVersions because versionining is not supported
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.ejb.objectservice.WidaObjectService#deleteTree(java.lang
	 * .String, java.lang.Boolean,
	 * org.apache.chemistry.opencmis.commons.enums.UnfileObject, java.lang.Boolean)
	 */
	@Override
	public FailedToDeleteData deleteTree(String userName, String folderId, Boolean allVersions,
			UnfileObject unfileObjects, Boolean continueOnFailure) {
		// ignore allVersions because Versioning is not supported
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.ejb.objectservice.WidaObjectService#setContentStream(org
	 * .apache.chemistry.opencmis.commons.spi.Holder, java.lang.Boolean,
	 * org.apache.chemistry.opencmis.commons.spi.Holder,
	 * org.apache.chemistry.opencmis.commons.data.ContentStream)
	 */
	@Override
	public void setContentStream(String userName, Holder<String> objectId, Boolean overwriteFlag,
			Holder<String> changeToken, ContentStream contentStream) {
		throw new CmisNotSupportedException("It is not allowed to add a content stream after document creation.",
				WidaErrorConstants.NOT_SUPPORTED_ADD_CONTENT_STREAM_AFTER_DOCUMENT_CREATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.ejb.objectservice.WidaObjectService#appendContentStream(
	 * org.apache.chemistry.opencmis.commons.spi.Holder,
	 * org.apache.chemistry.opencmis.commons.spi.Holder,
	 * org.apache.chemistry.opencmis.commons.data.ContentStream, boolean)
	 */
	@Override
	public void appendContentStream(String userName, Holder<String> objectId, Holder<String> changeToken,
			ContentStream contentStream, boolean isLastChunk) {
		throw new CmisNotSupportedException("It is not allowed to append a content stream after document creation.",
				WidaErrorConstants.NOT_SUPPORTED_APPEND_CONTENT_STREAM_AFTER_DOCUMENT_CREATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.ejb.objectservice.WidaObjectService#deleteContentStream(
	 * org.apache.chemistry.opencmis.commons.spi.Holder,
	 * org.apache.chemistry.opencmis.commons.spi.Holder)
	 */
	@Override
	public void deleteContentStream(String userName, Holder<String> objectId, Holder<String> changeToken) {
		throw new CmisNotSupportedException("It is not allowed to delete only a content stream.",
				WidaErrorConstants.NOT_SUPPORTED_DELETE_CONTENT_STREAM);
	}

	/**
	 * Gets the wida content meta data service.
	 *
	 * @return WidaContentMetaDataService - the wida content meta data service
	 */
	public WidaContentMetaDataService getWidaContentMetaDataService() {
		return widaContentMetaDataService;
	}

	/**
	 * Sets the wida content meta data service.
	 *
	 * @param widaContentMetaDataService
	 *            the wida content meta data service
	 */
	public void setWidaContentMetaDataService(WidaContentMetaDataService widaContentMetaDataService) {
		this.widaContentMetaDataService = widaContentMetaDataService;
	}

}
