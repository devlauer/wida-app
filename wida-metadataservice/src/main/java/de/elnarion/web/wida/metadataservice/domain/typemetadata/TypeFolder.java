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

import org.apache.chemistry.opencmis.commons.definitions.FolderTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.MutableFolderTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNotSupportedException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;

import de.elnarion.web.wida.common.WidaErrorConstants;

/**
 * The Class TypeFolder.
 */
@Entity
@DiscriminatorValue("type_folder")
public class TypeFolder extends TypeBase implements MutableFolderTypeDefinition {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2544450315750384720L;

	// non cmis properties

	/** The parent. */
	private TypeFolder parent;

	/** The children. */
	private Set<TypeFolder> children = new TreeSet<TypeFolder>();

	/**
	 * Instantiates a new folder type.
	 */
	public TypeFolder() {
		init();
	}

	/**
	 * Instantiates a new type folder.
	 *
	 * @param paramType
	 *            the param type
	 */
	public TypeFolder(FolderTypeDefinition paramType) {
		super(paramType);
		init();
	}

	/**
	 * Inits the object.
	 */
	private void init() {
		setBaseTypeId(BaseTypeId.CMIS_FOLDER);
	}

	/**
	 * Gets the parent.
	 *
	 * @return TypeBase - the parent
	 */
	public TypeFolder getParent() {
		return parent;
	}

	/**
	 * Gets the children.
	 *
	 * @return List - the children
	 */
	@SuppressWarnings("unchecked")
	@OneToMany(mappedBy = "parent", targetEntity=TypeBase.class, fetch = FetchType.LAZY)
	public Set<TypeFolder> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children
	 *            the children
	 */
	public void setChildren(Set<TypeFolder> children) {
		this.children = children;
	}

	@Override
	public void updateTypeAttributes(TypeDefinition paramType) {
		if(!(paramType instanceof FolderTypeDefinition))
			 throw new CmisRuntimeException("Wrong type used to update folder type", WidaErrorConstants.RUNTIME_EXCEPTION_WRONG_TYPE_FOR_UPDATE_USED);
		FolderTypeDefinition folderType = (FolderTypeDefinition)paramType;
		
		if (!folderType.isFileable())
			throw new CmisNotSupportedException("Non fileable folder types are not supported in this repository",
					WidaErrorConstants.NOT_SUPPORTED_NON_FILEABLE_FOLDER_TYPE);

		updateBaseTypeAttributes(folderType);
	}

}
