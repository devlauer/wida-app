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
package de.elnarion.web.wida.server;

import java.math.BigInteger;
import java.util.List;

import javax.enterprise.inject.spi.CDI;

import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.data.BulkUpdateObjectIdAndChangeToken;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.FailedToDeleteData;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderContainer;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;
import org.apache.chemistry.opencmis.commons.data.ObjectList;
import org.apache.chemistry.opencmis.commons.data.ObjectParentData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.RenditionData;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.RelationshipDirection;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.chemistry.opencmis.server.support.wrapper.CallContextAwareCmisService;

import de.elnarion.web.wida.ejb.discoveryservice.WidaDiscoveryService;
import de.elnarion.web.wida.ejb.navigationservice.WidaNavigationService;
import de.elnarion.web.wida.ejb.objectservice.WidaObjectService;
import de.elnarion.web.wida.ejb.repositoryservice.WidaRepositoryService;

/**
 * CMIS Service Implementation.
 */
public class WidaCmisService extends AbstractCmisService implements CallContextAwareCmisService {

	/** The repo service. */
	private WidaRepositoryService repoService;

	/** The object service. */
	private WidaObjectService objectService;

	/** The discovery service. */
	private WidaDiscoveryService discoveryService;

	/** The navigation service. */
	private WidaNavigationService navigationService;

	/**
	 * Instantiates a new wida cmis service.
	 */
	public WidaCmisService() {
		repoService = CDI.current().select(WidaRepositoryService.class).get();
		objectService = CDI.current().select(WidaObjectService.class).get();
		discoveryService = CDI.current().select(WidaDiscoveryService.class).get();
		navigationService = CDI.current().select(WidaNavigationService.class).get();
		;
	}

	/** The context. */
	private CallContext context;

	// --- Call Context ---

	/**
	 * Sets the call context.
	 * 
	 * This method should only be called by the service factory.
	 *
	 * @param context
	 *            the context
	 */
	public void setCallContext(CallContext context) {
		this.context = context;
	}

	/**
	 * Gets the call context.
	 *
	 * @return CallContext - the call context
	 */
	public CallContext getCallContext() {
		return context;
	}

	// --- CMIS Operations ---

	// --- repository services ---

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#
	 * getRepositoryInfos(org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extension) {
		return repoService.getRepositoryInfos(context.getCmisVersion());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#
	 * getTypeChildren(java.lang.String, java.lang.String, java.lang.Boolean,
	 * java.math.BigInteger, java.math.BigInteger,
	 * org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public TypeDefinitionList getTypeChildren(String repositoryId, String typeId, Boolean includePropertyDefinitions,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return repoService.getTypeChildren(typeId, includePropertyDefinitions, maxItems, skipCount,context.getCmisVersion());
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getTypeDescendants(java.lang.String, java.lang.String, java.math.BigInteger, java.lang.Boolean, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public List<TypeDefinitionContainer> getTypeDescendants(String repositoryId, String typeId, BigInteger depth,
			Boolean includePropertyDefinitions, ExtensionsData extension) {
		// parent implementation is sufficient
		return super.getTypeDescendants(repositoryId, typeId, depth, includePropertyDefinitions, extension);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#
	 * getTypeDefinition(java.lang.String, java.lang.String,
	 * org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public TypeDefinition getTypeDefinition(String repositoryId, String typeId, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return repoService.getTypeDefinition(typeId,context.getCmisVersion());
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#createType(java.lang.String, org.apache.chemistry.opencmis.commons.definitions.TypeDefinition, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public TypeDefinition createType(String repositoryId, TypeDefinition type, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return repoService.createType(type,context.getCmisVersion());
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#updateType(java.lang.String, org.apache.chemistry.opencmis.commons.definitions.TypeDefinition, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public TypeDefinition updateType(String repositoryId, TypeDefinition type, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return repoService.updateType(type, context.getCmisVersion());
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#deleteType(java.lang.String, java.lang.String, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public void deleteType(String repositoryId, String typeId, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		repoService.deleteType(typeId);
	}

	// -- navigation services --

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#
	 * getChildren(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.Boolean,
	 * org.apache.chemistry.opencmis.commons.enums.IncludeRelationships,
	 * java.lang.String, java.lang.Boolean, java.math.BigInteger,
	 * java.math.BigInteger,
	 * org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public ObjectInFolderList getChildren(String repositoryId, String folderId, String filter, String orderBy,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return navigationService.getChildren(folderId, filter, orderBy, includeAllowableActions,includeRelationships,renditionFilter, includePathSegment,
				maxItems, skipCount);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getDescendants(java.lang.String, java.lang.String, java.math.BigInteger, java.lang.String, java.lang.Boolean, org.apache.chemistry.opencmis.commons.enums.IncludeRelationships, java.lang.String, java.lang.Boolean, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public List<ObjectInFolderContainer> getDescendants(String repositoryId, String folderId, BigInteger depth,
			String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
			String renditionFilter, Boolean includePathSegment, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return navigationService.getDescendants(folderId, depth, filter, includeAllowableActions,includeRelationships,renditionFilter, includePathSegment);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getFolderTree(java.lang.String, java.lang.String, java.math.BigInteger, java.lang.String, java.lang.Boolean, org.apache.chemistry.opencmis.commons.enums.IncludeRelationships, java.lang.String, java.lang.Boolean, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public List<ObjectInFolderContainer> getFolderTree(String repositoryId, String folderId, BigInteger depth,
			String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
			String renditionFilter, Boolean includePathSegment, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return navigationService.getFolderTree(folderId, depth, filter, includeAllowableActions,includeRelationships,renditionFilter, includePathSegment);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getFolderParent(java.lang.String, java.lang.String, java.lang.String, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public ObjectData getFolderParent(String repositoryId, String folderId, String filter, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return navigationService.getFolderParent(folderId, filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#
	 * getObjectParents(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Boolean,
	 * org.apache.chemistry.opencmis.commons.enums.IncludeRelationships,
	 * java.lang.String, java.lang.Boolean,
	 * org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public List<ObjectParentData> getObjectParents(String repositoryId, String objectId, String filter,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includeRelativePathSegment, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return navigationService.getObjectParents(objectId, filter, includeAllowableActions,includeRelationships,renditionFilter,
				includeRelativePathSegment);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getCheckedOutDocs(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean, org.apache.chemistry.opencmis.commons.enums.IncludeRelationships, java.lang.String, java.math.BigInteger, java.math.BigInteger, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public ObjectList getCheckedOutDocs(String repositoryId, String folderId, String filter, String orderBy,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return navigationService.getCheckedOutDocs(folderId, filter, orderBy, includeAllowableActions,includeRelationships,renditionFilter, maxItems,
				skipCount);
	}

	// -- object services --

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#createDocument(java.lang.String, org.apache.chemistry.opencmis.commons.data.Properties, java.lang.String, org.apache.chemistry.opencmis.commons.data.ContentStream, org.apache.chemistry.opencmis.commons.enums.VersioningState, java.util.List, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public String createDocument(String repositoryId, Properties properties, String folderId,
			ContentStream contentStream, VersioningState versioningState, List<String> policies, Acl addAces,
			Acl removeAces, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return objectService.createDocument(properties, folderId, contentStream, versioningState, policies, addAces,
				removeAces);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#createDocumentFromSource(java.lang.String, java.lang.String, org.apache.chemistry.opencmis.commons.data.Properties, java.lang.String, org.apache.chemistry.opencmis.commons.enums.VersioningState, java.util.List, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public String createDocumentFromSource(String repositoryId, String sourceId, Properties properties, String folderId,
			VersioningState versioningState, List<String> policies, Acl addAces, Acl removeAces,
			ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return objectService.createDocumentFromSource(sourceId, properties, folderId, versioningState, policies,
				addAces, removeAces);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#createFolder(java.lang.String, org.apache.chemistry.opencmis.commons.data.Properties, java.lang.String, java.util.List, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public String createFolder(String repositoryId, Properties properties, String folderId, List<String> policies,
			Acl addAces, Acl removeAces, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return objectService.createFolder(properties, folderId, policies, addAces, removeAces);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#createRelationship(java.lang.String, org.apache.chemistry.opencmis.commons.data.Properties, java.util.List, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public String createRelationship(String repositoryId, Properties properties, List<String> policies, Acl addAces,
			Acl removeAces, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return objectService.createRelationship(properties, policies, addAces, removeAces);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#createPolicy(java.lang.String, org.apache.chemistry.opencmis.commons.data.Properties, java.lang.String, java.util.List, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public String createPolicy(String repositoryId, Properties properties, String folderId, List<String> policies,
			Acl addAces, Acl removeAces, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return objectService.createPolicy(properties, folderId, policies, addAces, removeAces);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#createItem(java.lang.String, org.apache.chemistry.opencmis.commons.data.Properties, java.lang.String, java.util.List, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public String createItem(String repositoryId, Properties properties, String folderId, List<String> policies,
			Acl addAces, Acl removeAces, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return objectService.createItem(properties, folderId, policies, addAces, removeAces);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getAllowableActions(java.lang.String, java.lang.String, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public AllowableActions getAllowableActions(String repositoryId, String objectId, ExtensionsData extension) {
		// parent implementation is sufficient
		return super.getAllowableActions(repositoryId, objectId, extension);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#
	 * getObject(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Boolean,
	 * org.apache.chemistry.opencmis.commons.enums.IncludeRelationships,
	 * java.lang.String, java.lang.Boolean, java.lang.Boolean,
	 * org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public ObjectData getObject(String repositoryId, String objectId, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
			Boolean includeAcl, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return objectService.getObject(objectId, filter, includeAllowableActions, includeRelationships, renditionFilter,
				includePolicyIds, includeAcl);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getProperties(java.lang.String, java.lang.String, java.lang.String, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public Properties getProperties(String repositoryId, String objectId, String filter, ExtensionsData extension) {
		// parent implementation is sufficient
		return super.getProperties(repositoryId, objectId, filter, extension);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getObjectByPath(java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean, org.apache.chemistry.opencmis.commons.enums.IncludeRelationships, java.lang.String, java.lang.Boolean, java.lang.Boolean, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public ObjectData getObjectByPath(String repositoryId, String path, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
			Boolean includeAcl, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return objectService.getObjectByPath(path, filter, includeAllowableActions, includeRelationships,
				renditionFilter, includePolicyIds, includeAcl);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getContentStream(java.lang.String, java.lang.String, java.lang.String, java.math.BigInteger, java.math.BigInteger, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public ContentStream getContentStream(String repositoryId, String objectId, String streamId, BigInteger offset,
			BigInteger length, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return objectService.getContentStream(objectId, streamId, offset, length);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getRenditions(java.lang.String, java.lang.String, java.lang.String, java.math.BigInteger, java.math.BigInteger, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public List<RenditionData> getRenditions(String repositoryId, String objectId, String renditionFilter,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return objectService.getRenditions(objectId, renditionFilter, maxItems, skipCount);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#updateProperties(java.lang.String, org.apache.chemistry.opencmis.commons.spi.Holder, org.apache.chemistry.opencmis.commons.spi.Holder, org.apache.chemistry.opencmis.commons.data.Properties, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public void updateProperties(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
			Properties properties, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		objectService.updateProperties(objectId, changeToken, properties);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#bulkUpdateProperties(java.lang.String, java.util.List, org.apache.chemistry.opencmis.commons.data.Properties, java.util.List, java.util.List, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public List<BulkUpdateObjectIdAndChangeToken> bulkUpdateProperties(String repositoryId,
			List<BulkUpdateObjectIdAndChangeToken> objectIdAndChangeToken, Properties properties,
			List<String> addSecondaryTypeIds, List<String> removeSecondaryTypeIds, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return objectService.bulkUpdateProperties(objectIdAndChangeToken, properties, addSecondaryTypeIds,
				removeSecondaryTypeIds);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#moveObject(java.lang.String, org.apache.chemistry.opencmis.commons.spi.Holder, java.lang.String, java.lang.String, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public void moveObject(String repositoryId, Holder<String> objectId, String targetFolderId, String sourceFolderId,
			ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		objectService.moveObject(objectId, targetFolderId, sourceFolderId);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#deleteObject(java.lang.String, java.lang.String, java.lang.Boolean, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public void deleteObject(String repositoryId, String objectId, Boolean allVersions, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		objectService.deleteObject(objectId, allVersions);
	}
	

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#deleteTree(java.lang.String, java.lang.String, java.lang.Boolean, org.apache.chemistry.opencmis.commons.enums.UnfileObject, java.lang.Boolean, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public FailedToDeleteData deleteTree(String repositoryId, String folderId, Boolean allVersions,
			UnfileObject unfileObjects, Boolean continueOnFailure, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return objectService.deleteTree(folderId, allVersions, unfileObjects, continueOnFailure);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#setContentStream(java.lang.String, org.apache.chemistry.opencmis.commons.spi.Holder, java.lang.Boolean, org.apache.chemistry.opencmis.commons.spi.Holder, org.apache.chemistry.opencmis.commons.data.ContentStream, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public void setContentStream(String repositoryId, Holder<String> objectId, Boolean overwriteFlag,
			Holder<String> changeToken, ContentStream contentStream, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		objectService.setContentStream(objectId, overwriteFlag, changeToken, contentStream);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#appendContentStream(java.lang.String, org.apache.chemistry.opencmis.commons.spi.Holder, org.apache.chemistry.opencmis.commons.spi.Holder, org.apache.chemistry.opencmis.commons.data.ContentStream, boolean, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public void appendContentStream(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
			ContentStream contentStream, boolean isLastChunk, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		objectService.appendContentStream(objectId, changeToken, contentStream, isLastChunk);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#deleteContentStream(java.lang.String, org.apache.chemistry.opencmis.commons.spi.Holder, org.apache.chemistry.opencmis.commons.spi.Holder, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public void deleteContentStream(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
			ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		objectService.deleteContentStream(objectId, changeToken);
	}

	// -- multi filing services -- not supported!!

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#addObjectToFolder(java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public void addObjectToFolder(String repositoryId, String objectId, String folderId, Boolean allVersions,
			ExtensionsData extension) {
		// not supported let parent throw exception
		super.addObjectToFolder(repositoryId, objectId, folderId, allVersions, extension);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#removeObjectFromFolder(java.lang.String, java.lang.String, java.lang.String, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public void removeObjectFromFolder(String repositoryId, String objectId, String folderId,
			ExtensionsData extension) {
		// not supported let parent throw exception
		super.removeObjectFromFolder(repositoryId, objectId, folderId, extension);
	}

	// -- discovery services --

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#query(java.lang.String, java.lang.String, java.lang.Boolean, java.lang.Boolean, org.apache.chemistry.opencmis.commons.enums.IncludeRelationships, java.lang.String, java.math.BigInteger, java.math.BigInteger, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public ObjectList query(String repositoryId, String statement, Boolean searchAllVersions,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return discoveryService.query(statement, searchAllVersions, includeAllowableActions, includeRelationships,
				renditionFilter, maxItems, skipCount);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getContentChanges(java.lang.String, org.apache.chemistry.opencmis.commons.spi.Holder, java.lang.Boolean, java.lang.String, java.lang.Boolean, java.lang.Boolean, java.math.BigInteger, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public ObjectList getContentChanges(String repositoryId, Holder<String> changeLogToken, Boolean includeProperties,
			String filter, Boolean includePolicyIds, Boolean includeAcl, BigInteger maxItems,
			ExtensionsData extension) {
		// ignore repositoryId because only one repository is supported; ignore extension because not used
		return discoveryService.getContentChanges(changeLogToken, includeProperties, filter, includePolicyIds, includeAcl,
				maxItems);
	}
	
	// -- versioning services -- not supported
	
	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#checkOut(java.lang.String, org.apache.chemistry.opencmis.commons.spi.Holder, org.apache.chemistry.opencmis.commons.data.ExtensionsData, org.apache.chemistry.opencmis.commons.spi.Holder)
	 */
	@Override
	public void checkOut(String repositoryId, Holder<String> objectId, ExtensionsData extension,
			Holder<Boolean> contentCopied) {
		// not supported let parent throw exception
		super.checkOut(repositoryId, objectId, extension, contentCopied);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#cancelCheckOut(java.lang.String, java.lang.String, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public void cancelCheckOut(String repositoryId, String objectId, ExtensionsData extension) {
		// not supported let parent throw exception
		super.cancelCheckOut(repositoryId, objectId, extension);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#checkIn(java.lang.String, org.apache.chemistry.opencmis.commons.spi.Holder, java.lang.Boolean, org.apache.chemistry.opencmis.commons.data.Properties, org.apache.chemistry.opencmis.commons.data.ContentStream, java.lang.String, java.util.List, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public void checkIn(String repositoryId, Holder<String> objectId, Boolean major, Properties properties,
			ContentStream contentStream, String checkinComment, List<String> policies, Acl addAces, Acl removeAces,
			ExtensionsData extension) {
		// not supported let parent throw exception
		super.checkIn(repositoryId, objectId, major, properties, contentStream, checkinComment, policies, addAces, removeAces,
				extension);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getObjectOfLatestVersion(java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean, java.lang.String, java.lang.Boolean, org.apache.chemistry.opencmis.commons.enums.IncludeRelationships, java.lang.String, java.lang.Boolean, java.lang.Boolean, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public ObjectData getObjectOfLatestVersion(String repositoryId, String objectId, String versionSeriesId,
			Boolean major, String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
			String renditionFilter, Boolean includePolicyIds, Boolean includeAcl, ExtensionsData extension) {
		// not supported let parent throw exception
		return super.getObjectOfLatestVersion(repositoryId, objectId, versionSeriesId, major, filter, includeAllowableActions,
				includeRelationships, renditionFilter, includePolicyIds, includeAcl, extension);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getPropertiesOfLatestVersion(java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean, java.lang.String, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public Properties getPropertiesOfLatestVersion(String repositoryId, String objectId, String versionSeriesId,
			Boolean major, String filter, ExtensionsData extension) {
		// not supported let parent throw exception
		return super.getPropertiesOfLatestVersion(repositoryId, objectId, versionSeriesId, major, filter, extension);
	}

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getAllVersions(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public List<ObjectData> getAllVersions(String repositoryId, String objectId, String versionSeriesId, String filter,
			Boolean includeAllowableActions, ExtensionsData extension) {
		// not supported let parent throw exception
		return super.getAllVersions(repositoryId, objectId, versionSeriesId, filter, includeAllowableActions, extension);
	}
	
	// -- relationship services -- not supported
	
	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getObjectRelationships(java.lang.String, java.lang.String, java.lang.Boolean, org.apache.chemistry.opencmis.commons.enums.RelationshipDirection, java.lang.String, java.lang.String, java.lang.Boolean, java.math.BigInteger, java.math.BigInteger, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public ObjectList getObjectRelationships(String repositoryId, String objectId, Boolean includeSubRelationshipTypes,
			RelationshipDirection relationshipDirection, String typeId, String filter, Boolean includeAllowableActions,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		// not supported let parent throw exception
		return super.getObjectRelationships(repositoryId, objectId, includeSubRelationshipTypes, relationshipDirection, typeId,
				filter, includeAllowableActions, maxItems, skipCount, extension);
	}
	
	// -- policy services -- not supported
	
	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#applyPolicy(java.lang.String, java.lang.String, java.lang.String, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public void applyPolicy(String repositoryId, String policyId, String objectId, ExtensionsData extension) {
		// not supported let parent throw exception
		super.applyPolicy(repositoryId, policyId, objectId, extension);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#removePolicy(java.lang.String, java.lang.String, java.lang.String, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public void removePolicy(String repositoryId, String policyId, String objectId, ExtensionsData extension) {
		// not supported let parent throw exception
		super.removePolicy(repositoryId, policyId, objectId, extension);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getAppliedPolicies(java.lang.String, java.lang.String, java.lang.String, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public List<ObjectData> getAppliedPolicies(String repositoryId, String objectId, String filter,
			ExtensionsData extension) {
		// not supported let parent throw exception
		return super.getAppliedPolicies(repositoryId, objectId, filter, extension);
	}
	
	// -- acl services -- not supported

	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#applyAcl(java.lang.String, java.lang.String, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.enums.AclPropagation, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public Acl applyAcl(String repositoryId, String objectId, Acl addAces, Acl removeAces,
			AclPropagation aclPropagation, ExtensionsData extension) {
		// not supported let parent throw exception
		return super.applyAcl(repositoryId, objectId, addAces, removeAces, aclPropagation, extension);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#applyAcl(java.lang.String, java.lang.String, org.apache.chemistry.opencmis.commons.data.Acl, org.apache.chemistry.opencmis.commons.enums.AclPropagation)
	 */
	@Override
	public Acl applyAcl(String repositoryId, String objectId, Acl aces, AclPropagation aclPropagation) {
		// not supported let parent throw exception
		return super.applyAcl(repositoryId, objectId, aces, aclPropagation);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService#getAcl(java.lang.String, java.lang.String, java.lang.Boolean, org.apache.chemistry.opencmis.commons.data.ExtensionsData)
	 */
	@Override
	public Acl getAcl(String repositoryId, String objectId, Boolean onlyBasicPermissions, ExtensionsData extension) {
		// not supported let parent throw exception
		return super.getAcl(repositoryId, objectId, onlyBasicPermissions, extension);
	}
	
	
}
