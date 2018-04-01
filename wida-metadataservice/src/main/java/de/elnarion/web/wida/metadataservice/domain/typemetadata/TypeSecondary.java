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
package de.elnarion.web.wida.metadataservice.domain.typemetadata;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.apache.chemistry.opencmis.commons.definitions.MutableSecondaryTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.SecondaryTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNotSupportedException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;

import de.elnarion.web.wida.common.WidaErrorConstants;


/**
 * The Class TypeSecondary.
 */
@Entity
@DiscriminatorValue("type_secondary")
public class TypeSecondary extends TypeBase implements MutableSecondaryTypeDefinition{


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2133951212231515371L;

	// non cmis properties

	/** The parent. */
	private TypeSecondary parent;	
	
	/** The children. */
	private Set<TypeSecondary> children = new TreeSet<TypeSecondary>();
	
	/**
	 * Instantiates a new secondary type .
	 */
	public TypeSecondary() {
		init();
	}
	
	/**
	 * Instantiates a new type secondary.
	 *
	 * @param paramSecondaryTypeDefinition
	 *            the param secondary type definition
	 */
	public TypeSecondary(SecondaryTypeDefinition paramSecondaryTypeDefinition)
	{
		super(paramSecondaryTypeDefinition);
	}

	private void init() {
		setBaseTypeId(BaseTypeId.CMIS_SECONDARY);
	}
	
	/**
	 * Gets the parent.
	 *
	 * @return TypeBase - the parent
	 */
	@SuppressWarnings("unchecked")
	public TypeSecondary getParent() {
		return parent;
	}


	/**
	 * Gets the children.
	 *
	 * @return List - the children
	 */
	@SuppressWarnings("unchecked")
	@OneToMany(mappedBy="parent", targetEntity=TypeBase.class, fetch=FetchType.LAZY)
	public Set<TypeSecondary> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children
	 *            the children
	 */
	public void setChildren(Set<TypeSecondary> children) {
		this.children = children;
	}
	
	@Override
	public void updateTypeAttributes(TypeDefinition paramType) {
		if(!(paramType instanceof SecondaryTypeDefinition))
			 throw new CmisRuntimeException("Wrong type used to update folder type", WidaErrorConstants.RUNTIME_EXCEPTION_WRONG_TYPE_FOR_UPDATE_USED);
		SecondaryTypeDefinition secondaryType = (SecondaryTypeDefinition)paramType;		
		if (secondaryType.isFileable())
			throw new CmisNotSupportedException("Fileable secondary types are not supported in this repository",
					WidaErrorConstants.NOT_SUPPORTED_FILEABLE_SECONDARY_TYPE);
		updateBaseTypeAttributes(secondaryType);
	}

}
