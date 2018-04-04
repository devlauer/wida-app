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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

import org.apache.chemistry.opencmis.commons.definitions.DocumentTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.MutableDocumentTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.ContentStreamAllowed;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNotSupportedException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;

import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.metadataservice.WidaMetaDataConstants;

/**
 * The Class TypeDocument.
 */
@Entity
@DiscriminatorValue("type_document")
@SecondaryTable(name = WidaMetaDataConstants.METADATA_TYPE_TYPE_DOCUMENT_DEFINITION_TABLE, schema = WidaMetaDataConstants.METADATA_DB_SCHEMA, pkJoinColumns = {
		@PrimaryKeyJoinColumn(name = WidaMetaDataConstants.METADATA_PK_PREFIX
				+ WidaMetaDataConstants.METADATA_TYPE_TYPE_DOCUMENT_DEFINITION_TABLE
				+ "_id", referencedColumnName = "id") })
public class TypeDocument extends TypeBase implements MutableDocumentTypeDefinition {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	// cmis properties

	/** The versionable. */
	private Boolean versionable;

	/** The content stream allowed. */
	private ContentStreamAllowed contentStreamAllowed;

	// non cmis properties

	/** The parent. */
	private TypeDocument parent;

	/** The children. */
	private Set<TypeDocument> children = new TreeSet<TypeDocument>();

	/**
	 * Instantiates a new document type .
	 */
	public TypeDocument() {
		super();
		init();

	}

	/**
	 * Instantiates a new type document.
	 *
	 * @param paramType
	 *            the param type
	 */
	public TypeDocument(DocumentTypeDefinition paramType) {
		super(paramType);
		init();
	}

	/**
	 * Inits the object.
	 */
	private void init() {
		setBaseTypeId(BaseTypeId.CMIS_DOCUMENT);
	}

	/**
	 * Gets the versionable.
	 *
	 * @return Boolean - the versionable
	 */
	@Column(name = "versionable", table = WidaMetaDataConstants.METADATA_TYPE_TYPE_DOCUMENT_DEFINITION_TABLE, nullable = false)
	public Boolean isVersionable() {
		return versionable;
	}

	/**
	 * Sets the versionable.
	 *
	 * @param versionable
	 *            the versionable
	 */
	public void setVersionable(Boolean versionable) {
		setIsVersionable(versionable);
	}

	/**
	 * Sets the versionable.
	 *
	 * @param versionable
	 *            the versionable
	 */
	public void setIsVersionable(Boolean versionable) {
		this.versionable = versionable;
	}

	/**
	 * Gets the content stream allowed.
	 *
	 * @return ContentStreamAllowed - the content stream allowed
	 */
	@Column(name = "contentstream_allowed", table = WidaMetaDataConstants.METADATA_TYPE_TYPE_DOCUMENT_DEFINITION_TABLE, nullable = false)
	@Enumerated(EnumType.STRING)
	public ContentStreamAllowed getContentStreamAllowed() {
		return contentStreamAllowed;
	}

	/**
	 * Sets the content stream allowed.
	 *
	 * @param contentStreamAllowed
	 *            the content stream allowed
	 */
	public void setContentStreamAllowed(ContentStreamAllowed contentStreamAllowed) {
		this.contentStreamAllowed = contentStreamAllowed;
	}

	/**
	 * Gets the parent.
	 *
	 * @return TypeBase - the parent
	 */
	public TypeDocument getParent() {
		return parent;
	}

	/**
	 * Gets the children.
	 *
	 * @return List - the children
	 */
	@SuppressWarnings("unchecked")
	@OneToMany(mappedBy = "parent", targetEntity = TypeBase.class, fetch = FetchType.LAZY)
	public Set<TypeDocument> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children
	 *            the children
	 */
	public void setChildren(Set<TypeDocument> children) {
		this.children = children;
	}

	@Override
	public void updateTypeAttributes(TypeDefinition paramType) {
		if(!(paramType instanceof DocumentTypeDefinition))
			 throw new CmisRuntimeException("Wrong type used to update document type", WidaErrorConstants.RUNTIME_EXCEPTION_WRONG_TYPE_FOR_UPDATE_USED);
		DocumentTypeDefinition documentType = (DocumentTypeDefinition)paramType;
		if (!documentType.isFileable())
			throw new CmisNotSupportedException("Non fileable document types are not supported in this repository",
					WidaErrorConstants.NOT_SUPPORTED_NON_FILEABLE_DOCUMENT_TYPE);
		updateBaseTypeAttributes(documentType);
		if (documentType.isVersionable())
			throw new CmisNotSupportedException("Versionable is not supported by this repository",
					WidaErrorConstants.NOT_SUPPORTED_VERSIONABLE);
		setIsVersionable(false);
		if (documentType.getContentStreamAllowed() == null
				|| !documentType.getContentStreamAllowed().equals(ContentStreamAllowed.REQUIRED))
			throw new CmisConstraintException(
					"A document must always have a contentstream. Contentstream is always required!",
					WidaErrorConstants.TYPE_ATTRIBUTE_CONTENT_STREAM_OPTIONAL_NOT_ALLOWED);
		setContentStreamAllowed(ContentStreamAllowed.REQUIRED);
	}

}
