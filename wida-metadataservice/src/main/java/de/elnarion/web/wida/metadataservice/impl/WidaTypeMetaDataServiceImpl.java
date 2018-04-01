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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
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
import org.apache.chemistry.opencmis.server.support.TypeDefinitionFactory;

import de.elnarion.web.wida.common.WidaConstants;
import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.metadataservice.WidaTypeMetaDataService;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionBase;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeBase;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeBase_;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeDocument;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeFolder;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeSecondary;

/**
 * The Class WidaTypeServiceImpl.
 */
@Stateless
public class WidaTypeMetaDataServiceImpl implements WidaTypeMetaDataService, WidaErrorConstants, WidaConstants {

	/** The Constant DEFAULT_NAMESPACE. */
	private static final String DEFAULT_NAMESPACE = "http://elnarion.de/wida/types";

	/** The type definition factory. */
	private TypeDefinitionFactory typeDefinitionFactory;
	
	/** The structure manager. */
	@EJB
	private WidaContentMetaDataStructureManager structureManager;
	
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
	public TypeDefinitionContainer getTypeById(String typeId) {
		TypeBase typeBase = getTypeBaseById(typeId);
		if (typeBase != null) {
			return TypeHelper.convertTypeBaseToTypeContainer(typeBase);
		}
		return null;
	}

	/**
	 * Gets the type base by id.
	 *
	 * @param typeId
	 *            the type id
	 * @return the type base by id
	 */
	private TypeBase getTypeBaseById(String typeId) {
		TypeBase typeBase = entityManager.find(TypeBase.class, typeId);
		return typeBase;
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
	public TypeDefinition getTypeByQueryName(String typeQueryName) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TypeBase> query = cb.createQuery(TypeBase.class);
		Root<TypeBase> rootCriteria = query.from(TypeBase.class);
		query.where(cb.equal(rootCriteria.get(TypeBase_.queryName), typeQueryName));
		TypeBase result = entityManager.createQuery(query).getSingleResult();
		return result;
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
	public Collection<TypeDefinitionContainer> getTypeDefinitionList() {
		List<TypeBase> allTypes = getTypeDefinitionsFromDb();
		if (allTypes != null) {
			List<TypeDefinitionContainer> resultList = new ArrayList<>();
			for (TypeBase baseType : allTypes) {
				TypeDefinitionContainer container = TypeHelper.convertTypeBaseToTypeContainer(baseType);
				resultList.add(container);
			}
			return resultList;
		}
		return null;
	}

	private List<TypeBase> getTypeDefinitionsFromDb() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TypeBase> query = cb.createQuery(TypeBase.class);
		Root<TypeBase> rootCriteria = query.from(TypeBase.class);
		CriteriaQuery<TypeBase> all = query.select(rootCriteria);
		List<TypeBase> allTypes = entityManager.createQuery(all).getResultList();
		return allTypes;
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
		if (documentRoot != null)
		{
			rootTypesList.add(documentRoot);
		}
		else
		{
			// add the missing base document type
			MutableDocumentTypeDefinition documentType = typeDefinitionFactory
					.createBaseDocumentTypeDefinition(CmisVersion.CMIS_1_1);
			addTypeDefinition(documentType, false);
			rootTypesList.add(getTypeById(OBJECT_BASE_TYPE_DOCUMENT));
		}
			
		TypeDefinitionContainer folderRoot = getTypeById(OBJECT_BASE_TYPE_FOLDER);
		if (folderRoot != null)
		{
			rootTypesList.add(folderRoot);
		}
		else
		{
			// add the missing base folder type
			MutableFolderTypeDefinition folderType = typeDefinitionFactory
					.createBaseFolderTypeDefinition(CmisVersion.CMIS_1_1);

			addTypeDefinition(folderType, false);
			rootTypesList.add(getTypeById(OBJECT_BASE_TYPE_FOLDER));
		}
		TypeDefinitionContainer secondaryRoot = getTypeById(OBJECT_BASE_TYPE_SECONDARY);
		if (secondaryRoot != null)
		{
			rootTypesList.add(secondaryRoot);			
		}
		else
		{
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
	public void addTypeDefinition(TypeDefinition paramNewTypeDefinition, boolean paramAddInheritedProperties) {
		// ignore paramAddInheritedProperties because CMIS-API allows only inheritance for type creation
		
		if (paramNewTypeDefinition == null || paramNewTypeDefinition.getId() == null)
			throw new CmisInvalidArgumentException("An empty type or a type without an id can not be added.",
					TYPE_EMPTY_NOT_ALLOWED);
		checkPropertyDefinitions(paramNewTypeDefinition);
		TypeBase origRepoTypeDefinition = null;
		TypeBase newRepoTypeDefinition = null;
		TypeBase parentTypeDefinition = null;
		// check if the type definition is not already defined
		origRepoTypeDefinition = entityManager.find(TypeBase.class, paramNewTypeDefinition.getId(),
				LockModeType.OPTIMISTIC);
		if (paramNewTypeDefinition.getParentTypeId() != null)
			parentTypeDefinition = entityManager.find(TypeBase.class, paramNewTypeDefinition.getParentTypeId(),
					LockModeType.OPTIMISTIC);
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
		// TODO create table and view for property values!!!
		structureManager.updateDatabaseStructure(newRepoTypeDefinition);
		entityManager.persist(newRepoTypeDefinition);
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
	public void updateTypeDefinition(TypeDefinition paramTypeDefinition) {
		if (paramTypeDefinition == null)
			throw new CmisInvalidArgumentException("Empty type not allowed.", TYPE_EMPTY_NOT_ALLOWED);
		TypeBase origValue = getTypeBaseById(paramTypeDefinition.getId());
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

		// TODO Update table, view and column definitions

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
	public void deleteTypeDefinition(String paramTypeId) {
		TypeBase repoValue = getTypeBaseById(paramTypeId);
		if (repoValue == null)
			throw new CmisInvalidArgumentException("The type to remove does not exist.", TYPE_DOES_NOT_EXIST);
		if (repoValue.getId().equals(repoValue.getBaseTypeId().name()))
			throw new CmisConstraintException("It is not allowed to remove the base types.",
					TYPE_BASE_CANNOT_BE_REMOVED);
		Set<TypeBase> children = repoValue.getChildren();
		if(children!=null)
			for(TypeBase child:children)
			{
				deleteTypeDefinition(child.getId());
			}
		// TODO drop tables and view for this type!!
		entityManager.remove(repoValue);
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

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.metadataservice.WidaTypeMetaDataService#getPropertyDefinition(java.lang.String)
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
	public void init()
	{
			typeDefinitionFactory = TypeDefinitionFactory.newInstance();
			typeDefinitionFactory = TypeDefinitionFactory.newInstance();
			typeDefinitionFactory.setDefaultNamespace(DEFAULT_NAMESPACE);
			typeDefinitionFactory.setDefaultControllableAcl(false);
			typeDefinitionFactory.setDefaultControllablePolicy(false);
			typeDefinitionFactory.setDefaultQueryable(false);
			typeDefinitionFactory.setDefaultFulltextIndexed(false);
			typeDefinitionFactory
					.setDefaultTypeMutability(typeDefinitionFactory.createTypeMutability(true, false, false));
		
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
	public Map<String, TypeDefinition> getTypeDefinitionMap() {
		List<TypeBase> allTypes = getTypeDefinitionsFromDb();
		if (allTypes != null) {
			Map<String, TypeDefinition> resultMap = new HashMap<>();
			for (TypeBase baseType : allTypes) {
				resultMap.put(baseType.getId(), baseType);
			}
			return resultMap;
		}
		return null;
	}

	@Override
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public TypeBase getInternalTypeDefinition(String paramTypeId) {
		TypeBase typeBase = getTypeBaseById(paramTypeId);
		return typeBase;
	}
} 
