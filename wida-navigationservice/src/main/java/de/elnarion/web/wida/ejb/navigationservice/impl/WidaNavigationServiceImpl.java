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
package de.elnarion.web.wida.ejb.navigationservice.impl;

import java.math.BigInteger;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderContainer;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;
import org.apache.chemistry.opencmis.commons.data.ObjectList;
import org.apache.chemistry.opencmis.commons.data.ObjectParentData;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNotSupportedException;

import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.ejb.navigationservice.WidaNavigationService;

/**
 * The Class WidaNavigationServiceImpl implements methods to traverse the folder
 * hierarchy in this repository, and to locate documents that are checked out..
 */
@Stateless
public class WidaNavigationServiceImpl implements WidaNavigationService {

	@Override
	public ObjectInFolderList getChildren(String folderId, String filter, String orderBy,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount) {

		if (orderBy != null && orderBy.length() > 0)
			throw new CmisInvalidArgumentException(
					"This repository does not support ordering capability. The ordering parameter is invalid.",
					WidaErrorConstants.INVALID_ARGUMENT_ORDER_BY);

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ObjectInFolderContainer> getDescendants(String folderId, BigInteger depth, String filter,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ObjectInFolderContainer> getFolderTree(String folderId, BigInteger depth, String filter,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectData getFolderParent(String folderId, String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ObjectParentData> getObjectParents(String objectId, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, Boolean includeRelativePathSegment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectList getCheckedOutDocs(String folderId, String filter, String orderBy, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, BigInteger maxItems,
			BigInteger skipCount) {
		throw new CmisNotSupportedException("Versioning is not supported by this repository !",
				WidaErrorConstants.NOT_SUPPORTED_VERSIONING);
	}

}
