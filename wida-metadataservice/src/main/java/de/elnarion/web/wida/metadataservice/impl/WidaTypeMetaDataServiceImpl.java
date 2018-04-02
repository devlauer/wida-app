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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.apache.chemistry.opencmis.commons.definitions.DocumentTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.FolderTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.MutableDocumentTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.MutableFolderTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.MutableSecondaryTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.SecondaryTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNotSupportedException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.TypeDefinitionContainerImpl;
import org.apache.chemistry.opencmis.server.support.TypeDefinitionFactory;

import de.elnarion.web.wida.common.WidaConstants;
import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.metadataservice.WidaMetaDataConstants;
import de.elnarion.web.wida.metadataservice.WidaTypeMetaDataService;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionBase;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeBase;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeDocument;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeFolder;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeSecondary;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.VersionLock;

/**
 * The Class WidaTypeServiceImpl.
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class WidaTypeMetaDataServiceImpl implements WidaTypeMetaDataService, WidaErrorConstants, WidaConstants {

	/** The Constant DEFAULT_NAMESPACE. */
	private static final String DEFAULT_NAMESPACE = "http://elnarion.de/wida/types";

	/** The type definition factory. */
	private TypeDefinitionFactory typeDefinitionFactory;

	/** The structure manager. */
	@EJB
	private WidaContentMetaDataStructureManager structureManager;
	
	private VersionLock versionLock;

	private Map<String, TypeBase> typedefinitions = new HashMap<>();
	private Map<String, PropertyDefinitionBase<?>> propertydefinitions = new HashMap<>();

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.chemistry.opencmis.server.support.TypeManager#getTypeById(java.
	 * lang.String)
	 */
	@Override
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Lock(LockType.READ)
	public TypeDefinitionContainer getTypeById(String typeId) {
		checkAndRenewTypeDefinitions();
		TypeBase typeBase = typedefinitions.get(typeId);
		if (typeBase != null) {
			return convertTypeBaseToTypeContainer(typeBase);
		}
		return null;
	}

	/**
	 * Converts the internal type structure into TypeContainer objects
	 *
	 * @param typeBase
	 *            the type base
	 * @return the type definition container
	 */
	private TypeDefinitionContainer convertTypeBaseToTypeContainer(TypeBase typeBase) {
		TypeDefinitionContainerImpl typeContainer = new TypeDefinitionContainerImpl();
		typeContainer.setTypeDefinition(typeDefinitionFactory.copy(typeBase,true));
		Set<TypeBase> children = typeBase.getChildren();
		List<TypeDefinitionContainer> typeContainerList = new ArrayList<TypeDefinitionContainer>();
		typeContainer.setChildren(typeContainerList);
		if (children != null) {
			for (TypeBase child : children) {
				TypeDefinitionContainer childContainer = convertTypeBaseToTypeContainer(child);
				typeContainerList.add(childContainer);
			}
		}
		return typeContainer;
	}

	/**
	 * Re read type definition map from db.
	 */
	private void reReadTypesMap() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<TypeBase> cq = cb.createQuery(TypeBase.class);
        Root<TypeBase> rootEntry = cq.from(TypeBase.class);
        CriteriaQuery<TypeBase> all = cq.select(rootEntry);
        TypedQuery<TypeBase> allQuery = getEntityManager().createQuery(all);
		List<TypeBase> typeBaseList = allQuery.getResultList();
		Map<String, TypeBase> newDefinitionsMap = new HashMap<>();
		Map<String, PropertyDefinitionBase<?>> newPropertyDefinitions = new HashMap<>();
		for(TypeBase typeBase:typeBaseList)
		{
			newDefinitionsMap.put(typeBase.getId(),typeBase);
			List<PropertyDefinitionBase<?>> internalPropertyDefinitions = typeBase.getPropertyDefinitionsList();
			for(PropertyDefinitionBase<?> propertyDefinition:internalPropertyDefinitions)
			{
				newPropertyDefinitions.put(propertyDefinition.getId(),propertyDefinition);
			}
		}
		synchronized (typedefinitions) {
			typedefinitions.clear();
			typedefinitions.putAll(newDefinitionsMap);
		}
		synchronized (propertydefinitions) {
			propertydefinitions.clear();
			propertydefinitions.putAll(newPropertyDefinitions);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.chemistry.opencmis.server.support.TypeManager#getTypeByQueryName(
	 * java.lang.String)
	 */
	@Override
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Lock(LockType.READ)
	public TypeDefinition getTypeByQueryName(String typeQueryName) {
		checkAndRenewTypeDefinitions();
		Collection<TypeBase> typedefinitionList = typedefinitions.values();
		for(TypeBase typeBase:typedefinitionList)
			if(typeQueryName.equals(typeBase.getQueryName()))
				return typeBase;
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.chemistry.opencmis.server.support.TypeManager#
	 * getTypeDefinitionList()
	 */
	@Override
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Lock(LockType.READ)
	public Collection<TypeDefinitionContainer> getTypeDefinitionList() {
		checkAndRenewTypeDefinitions();
		Collection<TypeBase> allTypes = typedefinitions.values();
		if (allTypes != null) {
			List<TypeDefinitionContainer> resultList = new ArrayList<>();
			for (TypeBase baseType : allTypes) {
				TypeDefinitionContainer container = convertTypeBaseToTypeContainer(baseType);
				resultList.add(container);
			}
			return resultList;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.chemistry.opencmis.server.support.TypeManager#getRootTypes()
	 */
	@Override
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<TypeDefinitionContainer> getRootTypes() {
		List<TypeDefinitionContainer> rootTypesList = new ArrayList<>();
		TypeDefinitionContainer documentRoot = getTypeById(OBJECT_BASE_TYPE_DOCUMENT);
		if (documentRoot != null) {
			rootTypesList.add(documentRoot);
		} else {
			// add the missing base document type
			MutableDocumentTypeDefinition documentType = typeDefinitionFactory
					.createBaseDocumentTypeDefinition(CmisVersion.CMIS_1_1);
			addTypeDefinition(documentType, false);
			rootTypesList.add(getTypeById(OBJECT_BASE_TYPE_DOCUMENT));
		}

		TypeDefinitionContainer folderRoot = getTypeById(OBJECT_BASE_TYPE_FOLDER);
		if (folderRoot != null) {
			rootTypesList.add(folderRoot);
		} else {
			// add the missing base folder type
			MutableFolderTypeDefinition folderType = typeDefinitionFactory
					.createBaseFolderTypeDefinition(CmisVersion.CMIS_1_1);

			addTypeDefinition(folderType, false);
			rootTypesList.add(getTypeById(OBJECT_BASE_TYPE_FOLDER));
		}
		TypeDefinitionContainer secondaryRoot = getTypeById(OBJECT_BASE_TYPE_SECONDARY);
		if (secondaryRoot != null) {
			rootTypesList.add(secondaryRoot);
		} else {
			// add the missing base secondary type
			MutableSecondaryTypeDefinition secondaryType = typeDefinitionFactory
					.createBaseSecondaryTypeDefinition(CmisVersion.CMIS_1_1);
			addTypeDefinition(secondaryType, false);
			rootTypesList.add(getTypeById(OBJECT_BASE_TYPE_SECONDARY));
		}
		return rootTypesList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.chemistry.opencmis.server.support.TypeManager#
	 * getPropertyIdForQueryName(org.apache.chemistry.opencmis.commons.definitions.
	 * TypeDefinition, java.lang.String)
	 */
	@Override
	public String getPropertyIdForQueryName(TypeDefinition typeDefinition, String propQueryName) {
		return typeDefinition.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.chemistry.opencmis.server.support.TypeManager#addTypeDefinition(
	 * org.apache.chemistry.opencmis.commons.definitions.TypeDefinition, boolean)
	 */
	@Override
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Lock(LockType.READ)
	public void addTypeDefinition(TypeDefinition paramNewTypeDefinition, boolean paramAddInheritedProperties) {
		checkAndRenewTypeDefinitions();
		aquireWriteVersionLock();
		// ignore paramAddInheritedProperties because CMIS-API allows only inheritance
		// for type creation

		if (paramNewTypeDefinition == null || paramNewTypeDefinition.getId() == null)
			throw new CmisInvalidArgumentException("An empty type or a type without an id can not be added.",
					TYPE_EMPTY_NOT_ALLOWED);
		checkPropertyDefinitions(paramNewTypeDefinition);
		TypeBase origRepoTypeDefinition = null;
		TypeBase newRepoTypeDefinition = null;
		TypeBase parentTypeDefinition = null;
		// check if the type definition is not already defined
		origRepoTypeDefinition = typedefinitions.get(paramNewTypeDefinition.getId());
		if (paramNewTypeDefinition.getParentTypeId() != null)
			parentTypeDefinition = typedefinitions.get(paramNewTypeDefinition.getParentTypeId());
		if (origRepoTypeDefinition != null)
			throw new CmisConstraintException("The given type definition already exists. No creation possible",
					TYPE_DEFINITION_ALREADY_EXISTS);
		if (paramNewTypeDefinition.getParentTypeId() != null && parentTypeDefinition == null)
			throw new CmisInvalidArgumentException("The given parent type id does not exist for this base type.",
					TYPE_PARENT_DOES_NOT_EXIST);
		if (paramNewTypeDefinition.getParentTypeId() != null && parentTypeDefinition != null
				&& !parentTypeDefinition.getTypeMutabilityCreate())
			throw new CmisConstraintException("It is not allowed to create a child of this parent type.",
					TYPE_CREATION_NOT_ALLOWED_FOR_THIS_PARENT);
		if (paramNewTypeDefinition instanceof DocumentTypeDefinition) {
			newRepoTypeDefinition = new TypeDocument((DocumentTypeDefinition) paramNewTypeDefinition);
		} else if (paramNewTypeDefinition instanceof FolderTypeDefinition) {
			newRepoTypeDefinition = new TypeFolder((FolderTypeDefinition) paramNewTypeDefinition);
		} else if (paramNewTypeDefinition instanceof SecondaryTypeDefinition) {
			newRepoTypeDefinition = new TypeSecondary((SecondaryTypeDefinition) paramNewTypeDefinition);
		} else {
			throw new CmisConstraintException(
					"The type to add is not one of the allowed types document, folder or secondary. The type is "
							+ paramNewTypeDefinition.getClass().getName(),
					TYPE_NOT_CHILD_OF_ALLOWED_BASIC_TYPES);
		}
		structureManager.updateDatabaseStructure(newRepoTypeDefinition,true);
		entityManager.persist(newRepoTypeDefinition);
		releaseWriteVersionLock(true);
		typedefinitions.put(newRepoTypeDefinition.getId(), newRepoTypeDefinition);
	}

	private void releaseWriteVersionLock(boolean increaseVersion) {
		if (increaseVersion)
			versionLock.setVersion(versionLock.getVersion() + 1);
		entityManager.persist(versionLock);
	}

	private boolean checkVersionLockRenewed() {
		VersionLock versionFromDb = entityManager.find(VersionLock.class,
				WidaMetaDataConstants.METADATA_VERSION_LOCK_KEY,LockModeType.OPTIMISTIC);
		if (versionLock.getVersion() < versionFromDb.getVersion())
			return true;
		else
			return false;
	}

	private void aquireWriteVersionLock() {
		// aquire write lock for version and release it after update with a new version
		// in it
		VersionLock currentVersionLock = entityManager.find(VersionLock.class,
				WidaMetaDataConstants.METADATA_VERSION_LOCK_KEY, LockModeType.PESSIMISTIC_WRITE);
		if (currentVersionLock == null) {
			currentVersionLock = new VersionLock();
			currentVersionLock.setLocktype(WidaMetaDataConstants.METADATA_VERSION_LOCK_KEY);
			entityManager.persist(currentVersionLock);
			currentVersionLock = entityManager.find(VersionLock.class, WidaMetaDataConstants.METADATA_VERSION_LOCK_KEY,
					LockModeType.PESSIMISTIC_WRITE);
		}
		this.versionLock = currentVersionLock;
	}

	/**
	 * Check property definitions.
	 *
	 * @param paramTypeDefinition
	 *            the type definition
	 */
	private void checkPropertyDefinitions(TypeDefinition paramTypeDefinition) {
		Map<String, PropertyDefinition<?>> propertyDefinitionsMap = paramTypeDefinition.getPropertyDefinitions();
		Set<String> propertyDefinitionKeys = propertyDefinitionsMap.keySet();
		for (String propertyDefinitionKey : propertyDefinitionKeys) {
			PropertyDefinition<?> savedRepoPropDef = getPropertyDefinition(propertyDefinitionKey);
			if (savedRepoPropDef != null)
				throw new CmisInvalidArgumentException("It is not allowed to redefine a property. The property "
						+ propertyDefinitionKey + " is already defined!", PROPERTY_ALREADY_DEFINED);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.chemistry.opencmis.server.support.TypeManager#updateTypeDefinition
	 * (org.apache.chemistry.opencmis.commons.definitions.TypeDefinition)
	 */
	@Override
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Lock(LockType.WRITE)
	public void updateTypeDefinition(TypeDefinition paramTypeDefinition) {
		checkAndRenewTypeDefinitions();
		aquireWriteVersionLock();
		if (paramTypeDefinition == null)
			throw new CmisInvalidArgumentException("Empty type not allowed.", TYPE_EMPTY_NOT_ALLOWED);
		TypeBase origValue = typedefinitions.get(paramTypeDefinition.getId());
		if (origValue == null)
			throw new CmisConstraintException("Type to modify does not exist.", TYPE_DOES_NOT_EXIST);
		if (paramTypeDefinition instanceof DocumentTypeDefinition) {
			DocumentTypeDefinition documentTypeDefinition = (DocumentTypeDefinition) paramTypeDefinition;
			TypeDocument origType = null;
			if (origValue instanceof TypeDocument) {
				origType = (TypeDocument) origValue;
			} else {
				throw new CmisNotSupportedException(
						"Modification of one base type to another base type is not supported.",
						TYPE_ATTRIBUTE_BASE_TYPE_MODIFICATION_NOT_ALLOWED);
			}
			origType.updateTypeAttributes(documentTypeDefinition);
		} else if (paramTypeDefinition instanceof FolderTypeDefinition) {
			FolderTypeDefinition folderTypeDefinition = (FolderTypeDefinition) paramTypeDefinition;
			TypeFolder origType = null;
			if (origValue instanceof TypeFolder) {
				origType = (TypeFolder) origValue;
			} else {
				throw new CmisNotSupportedException(
						"Modification of one base type to another base type is not supported.",
						TYPE_ATTRIBUTE_BASE_TYPE_MODIFICATION_NOT_ALLOWED);
			}
			origType.updateTypeAttributes(folderTypeDefinition);
		} else if (paramTypeDefinition instanceof SecondaryTypeDefinition) {
			SecondaryTypeDefinition secondaryTypeDefinition = (SecondaryTypeDefinition) paramTypeDefinition;
			TypeSecondary origType = null;
			if (origValue instanceof TypeSecondary) {
				origType = (TypeSecondary) origValue;
			} else {
				throw new CmisNotSupportedException(
						"Modification of one base type to another base type is not supported.",
						TYPE_ATTRIBUTE_BASE_TYPE_MODIFICATION_NOT_ALLOWED);
			}
			origType.updateTypeAttributes(secondaryTypeDefinition);
		}
		structureManager.updateDatabaseStructure(origValue,true);
		entityManager.persist(origValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.chemistry.opencmis.server.support.TypeManager#deleteTypeDefinition
	 * (java.lang.String)
	 */
	@Override
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Lock(LockType.WRITE)
	public void deleteTypeDefinition(String paramTypeId) {
		checkAndRenewTypeDefinitions();
		aquireWriteVersionLock();
		TypeBase repoValue = typedefinitions.get(paramTypeId);
		if (repoValue == null)
			throw new CmisInvalidArgumentException("The type to remove does not exist.", TYPE_DOES_NOT_EXIST);
		if (repoValue.getId().equals(repoValue.getBaseTypeId().name()))
			throw new CmisConstraintException("It is not allowed to remove the base types.",
					TYPE_BASE_CANNOT_BE_REMOVED);
		Set<TypeBase> children = repoValue.getChildren();
		if (children != null)
			for (TypeBase child : children) {
				deleteTypeDefinition(child.getId());
			}
		structureManager.dropTables(repoValue);
		entityManager.remove(repoValue);
		releaseWriteVersionLock(true);
		typedefinitions.remove(paramTypeId);
	}



	private void checkAndRenewTypeDefinitions() {
		if(checkVersionLockRenewed())
		{
			reReadTypesMap();
			structureManager.reReadModel();
		}
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
	 * @param paramWidaDatasource
	 *            the wida datasource
	 */
	public void setWidaDatasource(DataSource paramWidaDatasource) {
		this.widaDatasource = paramWidaDatasource;
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
	 * @param paramEntityManager
	 *            the entity manager
	 */
	public void setEntityManager(EntityManager paramEntityManager) {
		this.entityManager = paramEntityManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.elnarion.web.wida.metadataservice.WidaTypeMetaDataService#
	 * getPropertyDefinition(java.lang.String)
	 */
	@Override
	@Transactional()
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public PropertyDefinition<?> getPropertyDefinition(String paramPropertyId) {
		return entityManager.find(PropertyDefinitionBase.class, paramPropertyId);
	}

	/**
	 * Gets the type definition factory.
	 *
	 * @return TypeDefinitionFactory - the type definition factory
	 */
	public TypeDefinitionFactory getTypeDefinitionFactory() {
		return typeDefinitionFactory;
	}

	/**
	 * Inits the ejb bean.
	 */
	@PostConstruct
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void init() {
		typeDefinitionFactory = TypeDefinitionFactory.newInstance();
		typeDefinitionFactory = TypeDefinitionFactory.newInstance();
		typeDefinitionFactory.setDefaultNamespace(DEFAULT_NAMESPACE);
		typeDefinitionFactory.setDefaultControllableAcl(false);
		typeDefinitionFactory.setDefaultControllablePolicy(false);
		typeDefinitionFactory.setDefaultQueryable(false);
		typeDefinitionFactory.setDefaultFulltextIndexed(false);
		typeDefinitionFactory.setDefaultTypeMutability(typeDefinitionFactory.createTypeMutability(true, false, false));
		// request all root types - if they are not defined so far they will get
		// initialized right now by the WidaTypeMetaDataServiceImpl
		getRootTypes();
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

	@Override
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Lock(LockType.READ)
	public Map<String, TypeDefinition> getTypeDefinitionMap() {
		checkAndRenewTypeDefinitions();
		Map<String, TypeDefinition> resultMap = new HashMap<>();
		resultMap.putAll(typedefinitions);
		return resultMap;
	}

	@Override
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Lock(LockType.READ)
	public TypeBase getInternalTypeDefinition(String paramTypeId) {
		checkAndRenewTypeDefinitions();
		return typedefinitions.get(paramTypeId);
	}
}
