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
package de.elnarion.web.wida.ejb.repositoryservice;

import java.math.BigInteger;
import java.util.List;

import javax.ejb.Local;

import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;

/**
 * The Interface WidaRepositoryService provides methods to discover information
 * about the repository, including information about the repository and the
 * object-types deﬁned for the repository. Furthermore, it provides operations
 * to create, modify and delete object-type deﬁnitions if that is supported by
 * the repository.
 *
 * @author dev.lauer@elnarion.de
 */
@Local
public interface WidaRepositoryService {

	/**
	 * Gets the repository infos.
	 * @param cmisVersion 
	 *
	 * @return {@link List}&lt;{@link RepositoryInfo}&gt;
	 */
	public List<RepositoryInfo> getRepositoryInfos(CmisVersion cmisVersion);

	/**
	 * Returns the list of object-types deﬁned for the repository that are children
	 * of the speciﬁed type.
	 *
	 * @param typeId
	 *            The typeId of an object-type speciﬁed in the repository.
	 * @param includePropertyDefinitions
	 *            If TRUE, then the repository MUST return the property deﬁnitions
	 *            for each object-type. If FALSE (default), the repository MUST
	 *            return only the attributes for each object-type.
	 * @param maxItems
	 *            This is the maximum number of items to return in a response. The
	 *            repository MUST NOT exceed this maximum. Default is
	 *            repository-speciﬁc.
	 * @param skipCount
	 *            This is the number of potential results that the repository MUST
	 *            skip/page over before returning any results. Defaults to 0.
	 * @param cmisVersion
	 *            the cmis version
	 * @return TypeDefinitionList - The list of child object-types deﬁned for the
	 *         given typeId.
	 */
	public TypeDefinitionList getTypeChildren(String typeId, Boolean includePropertyDefinitions, BigInteger maxItems,
			BigInteger skipCount,CmisVersion cmisVersion);

	/**
	 * Gets the deﬁnition of the speciﬁed object-type.
	 *
	 * @param typeId
	 *            The typeId of an object-type speciﬁed in the repository.
	 * @param cmisVersion
	 *            the cmis version
	 * @return TypeDefinition - Object-type including all property deﬁnitions.
	 */
	public TypeDefinition getTypeDefinition(String typeId, CmisVersion cmisVersion);

	/**
	 * Creates a new type deﬁnition that is a subtype of an existing speciﬁed parent
	 * type. Only properties that are new to this type (not inherited) are passed to
	 * this service.
	 *
	 * @param type
	 *            A fully populated type deﬁnition including all new property
	 *            deﬁnitions.
	 * @param cmisVersion
	 *            the cmis version
	 * @return The newly created object-type including all property deﬁnitions.
	 */
	public TypeDefinition createType(TypeDefinition type, CmisVersion cmisVersion);

	/**
	 * Updates a type deﬁnition. Notes: If you add an optional property to a type in
	 * error. There is no way to remove it/correct it - without deleting the type.
	 *
	 * @param type
	 *            A type deﬁnition object with the property deﬁnitions that are to
	 *            change. Repositories MUST ignore all ﬁelds in the type deﬁnition
	 *            except for the type id and the list of properties.
	 * 
	 *            Properties that are not changing MUST NOT be included, including
	 *            any inherited property deﬁnitions. For the properties that are
	 *            being included, an entire copy of the property deﬁnition should be
	 *            present (with the exception of the choice values – see special
	 *            note), even values that are not changing.
	 * @param cmisVersion
	 *            the cmis version
	 * @return The updated object-type including all property deﬁnitions.
	 */
	public TypeDefinition updateType(TypeDefinition type,CmisVersion cmisVersion);

	/**
	 * Deletes a type deﬁnition. Notes: If there are object instances present of the
	 * type being deleted then this operation MUST fail.
	 *
	 * @param typeId
	 *            The typeId of an object-type speciﬁed in the repository.
	 */
	public void deleteType(String typeId);
}
