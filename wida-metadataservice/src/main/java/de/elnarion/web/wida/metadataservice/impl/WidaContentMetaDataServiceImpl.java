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
package de.elnarion.web.wida.metadataservice.impl;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import de.elnarion.web.wida.common.WidaConstants;
import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.metadataservice.WidaContentMetaDataService;
import de.elnarion.web.wida.metadataservice.WidaMetaDataConstants;
import de.elnarion.web.wida.metadataservice.WidaTypeMetaDataService;
import de.elnarion.web.wida.metadataservice.domain.contentmetadata.Folder;
import de.elnarion.web.wida.metadataservice.domain.contentmetadata.Folder_;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeBase;

/**
 * The Class WidaContentMetadataServiceImpl.
 */
@Stateless
public class WidaContentMetaDataServiceImpl implements WidaContentMetaDataService, WidaErrorConstants, WidaConstants {

	/** The wida datasource. */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.chemistry.opencmis.server.support.TypeManager#getTypeById(java.
	 * lang.String)
	 */
	@Resource(lookup = "jdbc/wida_xads", type = DataSource.class)
	private DataSource widaDatasource;

	/** The entity manager. */
	@PersistenceContext(unitName = "wida_ctx")
	private EntityManager entityManager;
	
	/** The structure manager. */
	@EJB
	private WidaContentMetaDataStructureManager structureManager;
	
	/** The type service. */
	@EJB
	private WidaTypeMetaDataService typeService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.metadataservice.WidaContentMetaDataService#getRootFolder
	 * ()
	 */
	@Override
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Folder getRootFolder() {
		Folder rootFolder = getFolder(WidaMetaDataConstants.METADATA_ROOTFOLDER_OBJECT_ID);
		if (rootFolder == null) {
			rootFolder = createRootFolder();
		}
		return rootFolder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elnarion.web.wida.metadataservice.WidaContentMetaDataService#getFolder(
	 * java.lang.String)
	 */
	@Override
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Folder getFolder(String paramObjectId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Folder> query = cb.createQuery(Folder.class);
		Root<Folder> rootCriteria = query.from(Folder.class);
		query.where(cb.equal(rootCriteria.get(Folder_.objectId), paramObjectId));
		Folder folder = null;
		try
		{
			folder = entityManager.createQuery(query).getSingleResult();
		}
		catch(NoResultException e)
		{
			return null;
		}
		if(folder.getBaseTypeId()!=folder.getObjectTypeId())
		{
			String objectTypeId = folder.getObjectTypeId();
			Long objectId = folder.getId();
			Set<String> secondaryIds = folder.getSecondaryTypeIds();
			folder.setProperties(getNonBaseTypeProperties(objectId, objectTypeId,secondaryIds));
		}
		return folder;
	}

	private Map<String, Object> getNonBaseTypeProperties(Long objectId, String objectTypeId, Set<String> secondaryIds) {
		TypeBase type = typeService.getInternalTypeDefinition(objectTypeId);
		Map<String, Object> resultProperties = new HashMap<>();
		Map<String, Object> typeProperties = structureManager.fetchPropertiesForId(type, objectId);
		if(typeProperties!=null)
			resultProperties.putAll(typeProperties);
		if(secondaryIds!=null)
		{
			for(String secondaryObjectTypeId : secondaryIds)
			{
				TypeBase secondaryType = typeService.getInternalTypeDefinition(secondaryObjectTypeId);
				Map<String, Object> secondaryTypeProperties = structureManager.fetchPropertiesForId(secondaryType, objectId);
				if(secondaryTypeProperties!=null)
					resultProperties.putAll(typeProperties);
			}
		}
		return resultProperties;
	}

	/**
	 * Creates the root folder.
	 *
	 * @return the folder
	 */
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private Folder createRootFolder() {
		Folder rootFolder = new Folder();
		rootFolder.setObjectId(WidaMetaDataConstants.METADATA_ROOTFOLDER_OBJECT_ID);
		rootFolder.setBaseTypeId(OBJECT_BASE_TYPE_FOLDER);
		rootFolder.setCreatedBy(SYSTEM_USER);
		rootFolder.setCreationDate((GregorianCalendar) GregorianCalendar.getInstance());
		rootFolder.setLastModificationDate(rootFolder.getCreationDate());
		rootFolder.setLastModifiedBy(SYSTEM_USER);
		rootFolder.setName(ROOT_FOLDER_NAME);
		rootFolder.setObjectTypeId(OBJECT_BASE_TYPE_FOLDER);
		rootFolder.setParent(null);
		entityManager.persist(rootFolder);
		return rootFolder;
	}

	/**
	 * Gets the wida datasource.
	 *
	 * @return DataSource - the wida datasource
	 */
	public DataSource getWidaDatasource() {
		return widaDatasource;
	}

	/**
	 * Sets the wida datasource.
	 *
	 * @param widaDatasource
	 *            the wida datasource
	 */
	public void setWidaDatasource(DataSource widaDatasource) {
		this.widaDatasource = widaDatasource;
	}

	/**
	 * Gets the entity manager.
	 *
	 * @return EntityManager - the entity manager
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Sets the entity manager.
	 *
	 * @param entityManager
	 *            the entity manager
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * Gets the structure manager.
	 *
	 * @return WidaContentMetaDataStructureManager - the structure manager
	 */
	public WidaContentMetaDataStructureManager getStructureManager() {
		return structureManager;
	}

	/**
	 * Sets the structure manager.
	 *
	 * @param structureManager
	 *            the structure manager
	 */
	public void setStructureManager(WidaContentMetaDataStructureManager structureManager) {
		this.structureManager = structureManager;
	}

	/**
	 * Gets the type service.
	 *
	 * @return WidaTypeMetaDataService - the type service
	 */
	public WidaTypeMetaDataService getTypeService() {
		return typeService;
	}

	/**
	 * Sets the type service.
	 *
	 * @param typeService
	 *            the type service
	 */
	public void setTypeService(WidaTypeMetaDataService typeService) {
		this.typeService = typeService;
	}

}
