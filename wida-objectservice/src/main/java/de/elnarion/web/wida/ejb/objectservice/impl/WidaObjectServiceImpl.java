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
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.BulkUpdateObjectIdAndChangeToken;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.FailedToDeleteData;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.RenditionData;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNotSupportedException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.spi.Holder;

import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.ejb.objectservice.WidaObjectService;
import de.elnarion.web.wida.ejb.storageservice.WidaStorageService;
import de.elnarion.web.wida.metadataservice.WidaContentMetaDataService;
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

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#createDocument(org.apache.chemistry.opencmis.commons.data.Properties, java.lang.String, org.apache.chemistry.opencmis.commons.data.ContentStream, org.apache.chemistry.opencmis.commons.enums.VersioningState, java.util.List, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.Acl)
	 */
	@Override
	public String createDocument(Properties properties, String folderId, ContentStream contentStream,
			VersioningState versioningState, List<String> policies, Acl addAces, Acl removeAces) {
		// ignore the following parameters:
		// - policies -> policies not supported
		// - addAces -> ACLs not supported
		// - removeAces -> ACLs not supported

		// check versioning state
        if (VersioningState.NONE != versioningState) {
            throw new CmisConstraintException("Versioning not supported!",WidaErrorConstants.NOT_SUPPORTED_VERSIONING);
        }

        // get parent File
        Folder folder = widaContentMetaDataService.getFolder(folderId);
        if (folder==null) {
            throw new CmisObjectNotFoundException("Parent is not a folder!", WidaErrorConstants.NOT_FOUND_OBJECT);
        }		
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#createDocumentFromSource(java.lang.String, org.apache.chemistry.opencmis.commons.data.Properties, java.lang.String, org.apache.chemistry.opencmis.commons.enums.VersioningState, java.util.List, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.Acl)
	 */
	@Override
	public String createDocumentFromSource(String sourceId, Properties properties, String folderId,
			VersioningState versioningState, List<String> policies, Acl addAces, Acl removeAces) {
		// ignore the following parameters:
		// - versioningState -> Versioning not supported
		// - policies -> policies not supported
		// - addAces -> ACLs not supported
		// - removeAces -> ACLs not supported
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#createFolder(org.apache.chemistry.opencmis.commons.data.Properties, java.lang.String, java.util.List, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.Acl)
	 */
	@Override
	public String createFolder(Properties properties, String folderId, List<String> policies, Acl addAces,
			Acl removeAces) {
		// ignore the following parameters:
		// - policies -> policies not supported
		// - addAces -> ACLs not supported
		// - removeAces -> ACLs not supported
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#createRelationship(org.apache.chemistry.opencmis.commons.data.Properties, java.util.List, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.Acl)
	 */
	@Override
	public String createRelationship(Properties properties, List<String> policies, Acl addAces, Acl removeAces) {
		throw new CmisNotSupportedException("Relationships are not supported by this repository",
				WidaErrorConstants.NOT_SUPPORTED_RELATIONSHIPS);
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#createPolicy(org.apache.chemistry.opencmis.commons.data.Properties, java.lang.String, java.util.List, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.Acl)
	 */
	@Override
	public String createPolicy(Properties properties, String folderId, List<String> policies, Acl addAces,
			Acl removeAces) {
		throw new CmisNotSupportedException("Policies are not supported by this repository",
				WidaErrorConstants.NOT_SUPPORTED_POLICIES);
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#createItem(org.apache.chemistry.opencmis.commons.data.Properties, java.lang.String, java.util.List, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.Acl)
	 */
	@Override
	public String createItem(Properties properties, String folderId, List<String> policies, Acl addAces,
			Acl removeAces) {
		throw new CmisNotSupportedException("Simple items are not supported by this repository",
				WidaErrorConstants.NOT_SUPPORTED_ITEMS);
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#getObject(java.lang.String, java.lang.String, java.lang.Boolean, org.apache.chemistry.opencmis.commons.enums.IncludeRelationships, java.lang.String, java.lang.Boolean, java.lang.Boolean)
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

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#getObjectByPath(java.lang.String, java.lang.String, java.lang.Boolean, org.apache.chemistry.opencmis.commons.enums.IncludeRelationships, java.lang.String, java.lang.Boolean, java.lang.Boolean)
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

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#getContentStream(java.lang.String, java.lang.String, java.math.BigInteger, java.math.BigInteger)
	 */
	@Override
	public ContentStream getContentStream(String objectId, String streamId, BigInteger offset, BigInteger length) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#getRenditions(java.lang.String, java.lang.String, java.math.BigInteger, java.math.BigInteger)
	 */
	@Override
	public List<RenditionData> getRenditions(String objectId, String renditionFilter, BigInteger maxItems,
			BigInteger skipCount) {
		throw new CmisNotSupportedException("Renditions are not supported by this repository",
				WidaErrorConstants.NOT_SUPPORTED_RENDITIONS);
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#updateProperties(org.apache.chemistry.opencmis.commons.spi.Holder, org.apache.chemistry.opencmis.commons.spi.Holder, org.apache.chemistry.opencmis.commons.data.Properties)
	 */
	@Override
	public void updateProperties(Holder<String> objectId, Holder<String> changeToken, Properties properties) {
		// ignore change token because it is not supported
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#bulkUpdateProperties(java.util.List, org.apache.chemistry.opencmis.commons.data.Properties, java.util.List, java.util.List)
	 */
	@Override
	public List<BulkUpdateObjectIdAndChangeToken> bulkUpdateProperties(
			List<BulkUpdateObjectIdAndChangeToken> objectIdAndChangeToken, Properties properties,
			List<String> addSecondaryTypeIds, List<String> removeSecondaryTypeIds) {
		throw new CmisNotSupportedException("Bulk updates are not supported by this repository",
				WidaErrorConstants.NOT_SUPPORTED_BULK_UPDATES);
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#moveObject(org.apache.chemistry.opencmis.commons.spi.Holder, java.lang.String, java.lang.String)
	 */
	@Override
	public void moveObject(Holder<String> objectId, String targetFolderId, String sourceFolderId) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#deleteObject(java.lang.String, java.lang.Boolean)
	 */
	@Override
	public void deleteObject(String objectId, Boolean allVersions) {
		// ignore allVersions because versionining is not supported
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#deleteTree(java.lang.String, java.lang.Boolean, org.apache.chemistry.opencmis.commons.enums.UnfileObject, java.lang.Boolean)
	 */
	@Override
	public FailedToDeleteData deleteTree(String folderId, Boolean allVersions, UnfileObject unfileObjects,
			Boolean continueOnFailure) {
		// ignore allVersions because Versioning is not supported
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#setContentStream(org.apache.chemistry.opencmis.commons.spi.Holder, java.lang.Boolean, org.apache.chemistry.opencmis.commons.spi.Holder, org.apache.chemistry.opencmis.commons.data.ContentStream)
	 */
	@Override
	public void setContentStream(Holder<String> objectId, Boolean overwriteFlag, Holder<String> changeToken,
			ContentStream contentStream) {
		throw new CmisNotSupportedException("It is not allowed to add a content stream after document creation.",
				WidaErrorConstants.NOT_SUPPORTED_ADD_CONTENT_STREAM_AFTER_DOCUMENT_CREATION);
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#appendContentStream(org.apache.chemistry.opencmis.commons.spi.Holder, org.apache.chemistry.opencmis.commons.spi.Holder, org.apache.chemistry.opencmis.commons.data.ContentStream, boolean)
	 */
	@Override
	public void appendContentStream(Holder<String> objectId, Holder<String> changeToken, ContentStream contentStream,
			boolean isLastChunk) {
		throw new CmisNotSupportedException("It is not allowed to append a content stream after document creation.",
				WidaErrorConstants.NOT_SUPPORTED_APPEND_CONTENT_STREAM_AFTER_DOCUMENT_CREATION);
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.objectservice.WidaObjectService#deleteContentStream(org.apache.chemistry.opencmis.commons.spi.Holder, org.apache.chemistry.opencmis.commons.spi.Holder)
	 */
	@Override
	public void deleteContentStream(Holder<String> objectId, Holder<String> changeToken) {
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
