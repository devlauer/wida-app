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

import de.elnarion.web.wida.common.WidaConstants;
import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.metadataservice.WidaMetaDataConstants;
import de.elnarion.web.wida.metadataservice.WidaTypeMetaDataService;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.*;
import org.apache.chemistry.opencmis.commons.definitions.*;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.chemistry.opencmis.commons.enums.ContentStreamAllowed;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNotSupportedException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.TypeDefinitionContainerImpl;
import org.apache.chemistry.opencmis.server.support.TypeDefinitionFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.*;

/**
 * The Class WidaTypeServiceImpl.
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class WidaTypeMetaDataServiceImpl implements WidaTypeMetaDataService, WidaErrorConstants, WidaConstants {

    /**
     * The Constant DEFAULT_NAMESPACE.
     */
    private static final String DEFAULT_NAMESPACE = "https://elnarion.de/wida/types";

    /**
     * The type definition factory.
     */
    private TypeDefinitionFactory typeDefinitionFactory;

    /**
     * The structure manager.
     */
    @EJB
    private WidaContentMetaDataStructureManager structureManager;

    private VersionLock versionLock = new VersionLock(WidaMetaDataConstants.METADATA_VERSION_LOCK_KEY);

    private Map<String, TypeBase> typedefinitions = new HashMap<>();
    private Map<String, PropertyDefinitionBase<?>> propertydefinitions = new HashMap<>();

    /**
     * The wida datasource.
     */
    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.chemistry.opencmis.server.support.TypeManager#getTypeById(java.
     * lang.String)
     */
    @Resource(lookup = "jdbc/wida_xads", type = DataSource.class)
    private DataSource widaDatasource;

    /**
     * The entity manager.
     */
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
        checkAndRenewTypeDefinitions(false);
        TypeBase typeBase = typedefinitions.get(typeId);
        if (typeBase != null) {
            return convertTypeBaseToTypeContainer(typeBase);
        }
        return null;
    }

    /**
     * Converts the internal type structure into TypeContainer objects
     *
     * @param typeBase the type base
     * @return the type definition container
     */
    private TypeDefinitionContainer convertTypeBaseToTypeContainer(TypeBase typeBase) {
        TypeDefinitionContainerImpl typeContainer = new TypeDefinitionContainerImpl();
        typeContainer.setTypeDefinition(typeDefinitionFactory.copy(typeBase, true));
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
        for (TypeBase typeBase : typeBaseList) {
            newDefinitionsMap.put(typeBase.getId(), typeBase);
            List<PropertyDefinitionBase<?>> internalPropertyDefinitions = typeBase.getPropertyDefinitionsList();
            for (PropertyDefinitionBase<?> propertyDefinition : internalPropertyDefinitions) {
                newPropertyDefinitions.put(propertyDefinition.getId(), propertyDefinition);
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
        checkAndRenewTypeDefinitions(false);
        Collection<TypeBase> typedefinitionList = typedefinitions.values();
        for (TypeBase typeBase : typedefinitionList)
            if (typeQueryName.equals(typeBase.getQueryName()))
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
        checkAndRenewTypeDefinitions(false);
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
            // set repository specific information
            documentType.setContentStreamAllowed(ContentStreamAllowed.REQUIRED);
            documentType.setIsVersionable(false);
            documentType.setIsFileable(true);
            documentType.setIsQueryable(true);

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
    @Lock(LockType.WRITE)
    public void addTypeDefinition(TypeDefinition paramNewTypeDefinition, boolean paramAddInheritedProperties) {
        checkAndRenewTypeDefinitions(true);
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

        // base types share their property definitions so add existing instead of given
        // ones
        if (newRepoTypeDefinition.getId().equals(newRepoTypeDefinition.getBaseTypeId().value())) {
            List<PropertyDefinitionBase<?>> properties = newRepoTypeDefinition.getPropertyDefinitionsList();
            List<PropertyDefinitionBase<?>> propertiesToReplace = new ArrayList<>();
            for (PropertyDefinitionBase<?> newPropDef : properties) {
                PropertyDefinitionBase<?> repoProp = null;
                try {
                    repoProp = entityManager.find(PropertyDefinitionBase.class, newPropDef.getId());
                } catch (NoResultException e) {
                    // do nothing
                }
                if (repoProp != null) {
                    propertiesToReplace.add(repoProp);
                } else {
                    propertiesToReplace.add(newPropDef);
                }
            }
            newRepoTypeDefinition.removeAllPropertyDefinitions();
            newRepoTypeDefinition.setPropertyDefinitionsList(propertiesToReplace);
        }

        structureManager.updateDatabaseStructure(newRepoTypeDefinition, true);
        entityManager.persist(newRepoTypeDefinition);
        releaseWriteVersionLock(true, true);
        entityManager.flush();
        reReadTypesMap();
    }

    private void releaseWriteVersionLock(boolean increaseVersion, boolean writeMode) {
        VersionLock versionLockFromDb = getFromDb(writeMode);
        if (increaseVersion)
            versionLockFromDb.setVersion(versionLockFromDb.getVersion() + 1);
        entityManager.persist(versionLockFromDb);
        entityManager.flush();
        synchronized (versionLock) {
            versionLock = versionLockFromDb;
        }
    }

    private boolean checkVersionLockRenewed(boolean lockwriteMode) {
        synchronized (versionLock) {
            VersionLock versionFromDb = null;
            try {
                versionFromDb = getFromDb(lockwriteMode);
                if (versionFromDb != null && versionLock != null) {
                    if (versionLock.getVersion() < versionFromDb.getVersion()) {
                        return true;
                    } else
                        return false;
                }
            } catch (NoResultException e) {
                // do nothing new version lock is already created in the code beneath
            }
            if (versionFromDb == null)
                versionLock = new VersionLock(WidaMetaDataConstants.METADATA_VERSION_LOCK_KEY);
            else
                versionLock.setVersion(versionFromDb.getVersion());
            entityManager.persist(versionLock);
            entityManager.flush();
            return false;
        }
    }

    private VersionLock getFromDb(boolean lockwriteMode) {
        VersionLock versionFromDb = entityManager.find(VersionLock.class,
                WidaMetaDataConstants.METADATA_VERSION_LOCK_KEY,
                lockwriteMode ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
        return versionFromDb;
    }

    /**
     * Check property definitions.
     *
     * @param paramTypeDefinition the type definition
     */
    private void checkPropertyDefinitions(TypeDefinition paramTypeDefinition) {
        Map<String, PropertyDefinition<?>> propertyDefinitionsMap = paramTypeDefinition.getPropertyDefinitions();
        Set<String> propertyDefinitionKeys = propertyDefinitionsMap.keySet();
        for (String propertyDefinitionKey : propertyDefinitionKeys) {
            // check property definition existance only for non base types because base
            // types share their property definitions
            if (!paramTypeDefinition.getBaseTypeId().value().equals(paramTypeDefinition.getId())) {
                PropertyDefinition<?> savedRepoPropDef = getPropertyDefinition(propertyDefinitionKey);
                if (savedRepoPropDef != null)
                    throw new CmisInvalidArgumentException("It is not allowed to redefine a property. The property "
                            + propertyDefinitionKey + " is already defined!", PROPERTY_ALREADY_DEFINED);
            }
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
        checkAndRenewTypeDefinitions(true);
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
        structureManager.updateDatabaseStructure(origValue, true);
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
        checkAndRenewTypeDefinitions(true);
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
        releaseWriteVersionLock(true, true);
        typedefinitions.remove(paramTypeId);
    }

    private void checkAndRenewTypeDefinitions(boolean lockWriteMode) {
        if (checkVersionLockRenewed(lockWriteMode)) {
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
     * @param paramWidaDatasource the wida datasource
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
     * @param paramEntityManager the entity manager
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
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Lock(LockType.READ)
    public PropertyDefinition<?> getPropertyDefinition(String paramPropertyId) {
        if (checkVersionLockRenewed(false))
            reReadTypesMap();
        PropertyDefinition<?> propDef = propertydefinitions.get(paramPropertyId);
        if (propDef != null) {
            return typeDefinitionFactory.copy(propDef);
        }
        return null;
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
        // be sure version lock is set in database and bean
        checkVersionLockRenewed(false);
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
     * @param structureManager the structure manager
     */
    public void setStructureManager(WidaContentMetaDataStructureManager structureManager) {
        this.structureManager = structureManager;
    }

    @Override
    @Transactional
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Lock(LockType.READ)
    public Map<String, TypeDefinition> getTypeDefinitionMap() {
        checkAndRenewTypeDefinitions(false);
        Map<String, TypeDefinition> resultMap = new HashMap<>();
        resultMap.putAll(typedefinitions);
        return resultMap;
    }

    @Override
    @Transactional
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Lock(LockType.READ)
    public TypeBase getInternalTypeDefinition(String paramTypeId) {
        checkAndRenewTypeDefinitions(false);
        return typedefinitions.get(paramTypeId);
    }
}
