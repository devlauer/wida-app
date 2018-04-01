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
package de.elnarion.web.wida.ejb.repositoryservice.impl;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.CapabilityAcl;
import org.apache.chemistry.opencmis.commons.enums.CapabilityChanges;
import org.apache.chemistry.opencmis.commons.enums.CapabilityContentStreamUpdates;
import org.apache.chemistry.opencmis.commons.enums.CapabilityJoin;
import org.apache.chemistry.opencmis.commons.enums.CapabilityOrderBy;
import org.apache.chemistry.opencmis.commons.enums.CapabilityQuery;
import org.apache.chemistry.opencmis.commons.enums.CapabilityRenditions;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNotSupportedException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CreatablePropertyTypesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.NewTypeSettableAttributesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.RepositoryCapabilitiesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.RepositoryInfoImpl;
import org.apache.chemistry.opencmis.server.support.TypeDefinitionFactory;

import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.common.ejbhelper.PropertiesResource;
import de.elnarion.web.wida.ejb.repositoryservice.WidaRepositoryService;
import de.elnarion.web.wida.metadataservice.WidaMetaDataConstants;
import de.elnarion.web.wida.metadataservice.WidaTypeMetaDataService;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeBase;

/**
 * The Class WidaRepositoryServiceImpl implements methods to discover
 * information about the repository, including information about the repository
 * and the object-types deﬁned for the repository. Furthermore, it provides
 * operations to create, modify and delete object-type deﬁnitions if that is
 * supported by the repository. *
 * 
 * @author dev.lauer@elnarion.de
 */
@Stateless
public class WidaRepositoryServiceImpl implements WidaRepositoryService {

	/** The meta data service. */
	@Inject
	private WidaTypeMetaDataService metaDataService;


	/** The repo properties. */
	@Inject
	@PropertiesResource(name = "reposervice.properties")
	private Properties repoProperties = new Properties();
	
	private TypeDefinitionFactory typeDefinitionFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.elnarion.web.wida.ejb.repositoryservice.WidaRepositoryService#
	 * getRepositoryInfos()
	 */
	@Override
	public List<RepositoryInfo> getRepositoryInfos(CmisVersion paramCmisVersion) {
		// very basic repository info set up
		RepositoryInfoImpl repositoryInfo = new RepositoryInfoImpl();
		repositoryInfo.setId("-default-");
		repositoryInfo.setName("Wida");
		repositoryInfo.setDescription(repoProperties.getProperty("application.description",
				"CMIS-based Content Repository"));
		repositoryInfo.setCmisVersionSupported(paramCmisVersion.value());
		repositoryInfo.setProductName(repoProperties.getProperty("application.product_name", "Document Repository"));
		repositoryInfo.setProductVersion(repoProperties.getProperty("application.version", "x.x.x"));
		repositoryInfo.setVendorName(repoProperties.getProperty("application.vendor_name", "Dev.Lauer"));

		repositoryInfo.setRootFolder(WidaMetaDataConstants.METADATA_ROOTFOLDER_OBJECT_ID);
		// No web interface so no thin client uri to set
		//repositoryInfo.setThinClientUri("");

		RepositoryCapabilitiesImpl capabilities = new RepositoryCapabilitiesImpl();
		capabilities.setAllVersionsSearchable(false);
		capabilities.setSupportsMultifiling(false);
		capabilities.setSupportsUnfiling(false);
		capabilities.setSupportsVersionSpecificFiling(false);
		capabilities.setIsPwcSearchable(false);
		capabilities.setIsPwcUpdatable(false);
		capabilities.setCapabilityAcl(CapabilityAcl.NONE);
		capabilities.setCapabilityChanges(CapabilityChanges.NONE);
		capabilities.setCapabilityContentStreamUpdates(CapabilityContentStreamUpdates.NONE);
		capabilities.setCapabilityJoin(CapabilityJoin.NONE);
		capabilities.setCapabilityOrderBy(CapabilityOrderBy.NONE);
		capabilities.setCapabilityQuery(CapabilityQuery.METADATAONLY);
		capabilities.setCapabilityRendition(CapabilityRenditions.NONE);
		capabilities.setSupportsGetDescendants(true);
		capabilities.setSupportsGetFolderTree(true);

		if (paramCmisVersion != CmisVersion.CMIS_1_0) {
            NewTypeSettableAttributesImpl typeSetAttributes = new NewTypeSettableAttributesImpl();
            typeSetAttributes.setCanSetControllableAcl(false);
            typeSetAttributes.setCanSetControllablePolicy(false);
            typeSetAttributes.setCanSetCreatable(false);
            typeSetAttributes.setCanSetDescription(false);
            typeSetAttributes.setCanSetDisplayName(false);
            typeSetAttributes.setCanSetFileable(false);
            typeSetAttributes.setCanSetFulltextIndexed(false);
            typeSetAttributes.setCanSetId(false);
            typeSetAttributes.setCanSetIncludedInSupertypeQuery(false);
            typeSetAttributes.setCanSetLocalName(false);
            typeSetAttributes.setCanSetLocalNamespace(false);
            typeSetAttributes.setCanSetQueryable(false);
            typeSetAttributes.setCanSetQueryName(false);
            capabilities.setNewTypeSettableAttributes(typeSetAttributes);
            CreatablePropertyTypesImpl creatablePropertyTypes = new CreatablePropertyTypesImpl();
            capabilities.setCreatablePropertyTypes(creatablePropertyTypes);
        }		
		return Collections.singletonList((RepositoryInfo) repositoryInfo);
	}

	/**
	 * Gets the metadata service.
	 *
	 * @return {@link WidaTypeMetaDataService} - the metadata service
	 */
	public WidaTypeMetaDataService getMetaDataService() {
		return metaDataService;
	}

	/**
	 * Sets the metadata service.
	 *
	 * @param metadataService
	 *            the metadataservice service 
	 */
	public void setMetaDataService(WidaTypeMetaDataService metadataService) {
		this.metaDataService = metadataService;
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.repositoryservice.WidaRepositoryService#getTypeChildren(java.lang.String, java.lang.Boolean, java.math.BigInteger, java.math.BigInteger)
	 */
	@Override
	public TypeDefinitionList getTypeChildren(String typeId, Boolean includePropertyDefinitions, BigInteger maxItems,
			BigInteger skipCount, CmisVersion cmisVersion) {
		Map<String, TypeDefinition> typeDefinitions= metaDataService.getTypeDefinitionMap();
		TypeDefinitionList resultList = typeDefinitionFactory.createTypeDefinitionList(typeDefinitions, typeId, includePropertyDefinitions,
                maxItems, skipCount, cmisVersion);
		return resultList;
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.repositoryservice.WidaRepositoryService#getTypeDefinition(java.lang.String)
	 */
	@Override
	public TypeDefinition getTypeDefinition(String typeId, CmisVersion cmisVersion) {
		TypeDefinitionContainer typeDefinition = metaDataService.getTypeById(typeId);
		if(typeDefinition!=null)
		{
			return typeDefinitionFactory.copy(typeDefinition.getTypeDefinition(), true, cmisVersion);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.repositoryservice.WidaRepositoryService#createType(org.apache.chemistry.opencmis.commons.definitions.TypeDefinition)
	 */
	@Override
	public TypeDefinition createType(TypeDefinition type, CmisVersion cmisVersion) {
		// null value not allowed
		if(type==null)
			throw new CmisConstraintException("It is not possible to create an empty TypeDefinition.", WidaErrorConstants.CONSTRAINT_CREATE_TYPE_WITH_ARGUMENT_EMPTY);
		TypeDefinitionContainer typeDefinitionContainerCheck = metaDataService.getTypeById(type.getId());
		// creating non existent type not allowed
		if(typeDefinitionContainerCheck!=null&&typeDefinitionContainerCheck.getTypeDefinition()!=null)
			throw new CmisNotSupportedException("It is not supported to create an existant TypeDefinition", WidaErrorConstants.NOT_SUPPORTED_CREATING_EXISTANT_TYPE);
		// null parent id only allowed for base types (id==basetypeid)
		if(type.getParentTypeId()==null&&!type.getBaseTypeId().value().equals(type.getId()))
			throw new CmisConstraintException("An empty parent is only allowed for base types.", WidaErrorConstants.TYPE_PARENT_EMPTY_NOT_ALLOWED);

		// check if parent type allows creating subtypes
		TypeBase parentType = metaDataService.getInternalTypeDefinition(type.getParentTypeId());
		if(parentType==null)
			throw new CmisConstraintException("The parent type id does not exist.",WidaErrorConstants.TYPE_PARENT_DOES_NOT_EXIST);
		
		if(!parentType.getTypeMutability().canCreate())
			throw new CmisConstraintException("You can not create a child of an immutable type definition!", WidaErrorConstants.CONSTRAINT_CREATE_CHILD_OF_IMMUTABLE_TYPE);
		TypeValidator validator = new TypeValidator();
		// validate type if not ok throw cmisexception
		validator.validateCreationOfType(type, parentType);
		// Basic type validation is done now update the type
		// type inheritance is always true because it can not be specified
		metaDataService.addTypeDefinition(type, true);
		// return the requested object because no change is done to the ids of all types 
		// and to save the order of the properties -> part of cmis spec
		// use a copy from type definition factory to ensure version compliance
		return typeDefinitionFactory.copy(type,true,cmisVersion);
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.repositoryservice.WidaRepositoryService#updateType(org.apache.chemistry.opencmis.commons.definitions.TypeDefinition)
	 */
	@Override
	public TypeDefinition updateType(TypeDefinition type, CmisVersion cmisVersion) {
		// null value not allowed
		if(type==null)
			throw new CmisConstraintException("It is not possible to update an empty TypeDefinition.", WidaErrorConstants.CONSTRAINT_UPDATE_TYPE_WITH_ARGUMENT_EMPTY);
		TypeBase typeInternal = metaDataService.getInternalTypeDefinition(type.getId());
		// updating non existent type not allowed
		if(typeInternal==null)
			throw new CmisNotSupportedException("It is not supported to update an non existant TypeDefinition", WidaErrorConstants.NOT_SUPPORTED_UPDATING_NON_EXISTANT_TYPE);
		
		if(!typeInternal.getTypeMutability().canUpdate())
			throw new CmisConstraintException("You can not update an immutable type definition!", WidaErrorConstants.CONSTRAINT_UPDATE_IMMUTABLE_TYPE);
		TypeValidator validator = new TypeValidator();
		// Do update validation
		validator.validateUpdateOfType(typeInternal,type);
		// After basic checks for validation update type
		metaDataService.updateTypeDefinition(type);
		// return the requested object because no change is done to the ids of all types 
		// and to save the order of the properties -> part of cmis spec
		// use a copy from type definition factory to ensure version compliance
		return typeDefinitionFactory.copy(type,true,cmisVersion);
	}

	/* (non-Javadoc)
	 * @see de.elnarion.web.wida.ejb.repositoryservice.WidaRepositoryService#deleteType(java.lang.String)
	 */
	@Override
	public void deleteType(String typeId) {
		throw new CmisNotSupportedException("It is not allowed to remove an existing type definition", WidaErrorConstants.NOT_SUPPORTED_TYPE_REMOVAL);
	}


	/**
	 * Inits the bean.
	 */
	@PostConstruct
	public void init()
	{
		// set up TypeDefinitionFactory
        typeDefinitionFactory = TypeDefinitionFactory.newInstance();
        typeDefinitionFactory.setDefaultNamespace(WidaMetaDataConstants.METADATA_DEFAULT_NAMESPACE);
        typeDefinitionFactory.setDefaultControllableAcl(false);
        typeDefinitionFactory.setDefaultControllablePolicy(false);
        typeDefinitionFactory.setDefaultQueryable(false);
        typeDefinitionFactory.setDefaultFulltextIndexed(false);
        typeDefinitionFactory.setDefaultTypeMutability(typeDefinitionFactory.createTypeMutability(true, true, false));
	}
	
}
