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
package de.elnarion.web.wida.ejb.navigationservice;

import java.math.BigInteger;
import java.util.List;

import javax.ejb.Local;

import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderContainer;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;
import org.apache.chemistry.opencmis.commons.data.ObjectList;
import org.apache.chemistry.opencmis.commons.data.ObjectParentData;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;

/**
 * The Interface WidaNavigationService provides methods to traverse the folder
 * hierarchy in this repository, and to locate documents that are checked out.
 */
@Local
public interface WidaNavigationService {

	/**
	 * Gets the list of child objects contained in the speciﬁed folder.
	 *
	 * @param folderId
	 *            The identiﬁer for the folder.
	 * @param filter
	 *            Value indicating which properties for objects MUST be returned.
	 *            This ﬁlter is a list of property query names and NOT a list of
	 *            property ids. The query names of secondary type properties MUST
	 *            follow the pattern
	 *            &lt;secondaryTypeQueryName&gt;.&lt;propertyQueryName&gt;.
	 * @param orderBy
	 *            Used to deﬁne the order of the list of objects returned by
	 *            getChildren and getCheckedOutDocs. If the optional capability
	 *            capabilityOrderBy is "none" and this parameter is set, the
	 *            repository SHOULD return an invalidArgument error.
	 * @param includeAllowableActions
	 *            If TRUE, then the Repository MUST return the available actions for
	 *            each object in the result set.
	 * @param renditionFilter
	 *            The Repository MUST return the set of renditions whose kind
	 *            matches this ﬁlter. See section below for the ﬁlter grammar.
	 *            Defaults to "cmis:none".
	 * @param includeRelationships
	 *            Enum includeRelationships Value indicating what relationships in
	 *            which the objects returned participate MUST be returned, if any.
	 *            Values are:
	 * 
	 *            none No relationships MUST be returned. (Default) source Only
	 *            relationships in which the objects returned are the source MUST be
	 *            returned. target Only relationships in which the objects returned
	 *            are the target MUST be returned. both Relationships in which the
	 *            objects returned are the source or the target MUST be returned.
	 * @param includePathSegment
	 *            the include path segment
	 * @param maxItems
	 *            This is the maximum number of items to return in a response. The
	 *            repository MUST NOT exceed this maximum.
	 * @param skipCount
	 *            the skip count
	 * @return ObjectInFolderList - the children
	 */
	public ObjectInFolderList getChildren(String folderId, String filter, String orderBy,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount);

	/**
	 * Gets the set of descendant objects contained in the speciﬁed folder or any of
	 * its child-folders. The order in which results are returned is
	 * respository-speciﬁc.
	 * 
	 *
	 * @param folderId
	 *            The identiﬁer for the folder.
	 * @param depth
	 *            The number of levels of depth in the folder hierarchy from which
	 *            to return results. Valid values are: 1 Return only objects that
	 *            are children of the folder; Integer value greater than 1 Return
	 *            only objects that are children of the folder and descendants up to
	 *            value levels deep; -1 Return ALL descendant objects at all depth
	 *            levels in the CMIS hierarchy. The default value is repository
	 *            speciﬁc and SHOULD be at least 2 or -1.
	 * @param filter
	 *            Value indicating which properties for objects MUST be returned.
	 *            This ﬁlter is a list of property query names and NOT a list of
	 *            property ids. The query names of secondary type properties MUST
	 *            follow the pattern
	 *            &lt;secondaryTypeQueryName&gt;.&lt;propertyQueryName&gt;.
	 * @param includeAllowableActions
	 *            If TRUE, then the Repository MUST return the available actions for
	 *            each object in the result set.
	 * @param renditionFilter
	 *            The Repository MUST return the set of renditions whose kind
	 *            matches this ﬁlter. See section below for the ﬁlter grammar.
	 *            Defaults to "cmis:none".
	 * @param includeRelationships
	 *            Enum includeRelationships Value indicating what relationships in
	 *            which the objects returned participate MUST be returned, if any.
	 *            Values are:
	 * 
	 *            none No relationships MUST be returned. (Default) source Only
	 *            relationships in which the objects returned are the source MUST be
	 *            returned. target Only relationships in which the objects returned
	 *            are the target MUST be returned. both Relationships in which the
	 *            objects returned are the source or the target MUST be returned.
	 * @param includePathSegment
	 *            If TRUE, returns a PathSegment for each child object for use in
	 *            constructing that object’s path.
	 * @return List - A tree of the child objects for the speciﬁed folder.
	 */
	public List<ObjectInFolderContainer> getDescendants(String folderId, BigInteger depth, String filter,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment);

	/**
	 * Gets the folder tree.
	 *
	 * @param folderId
	 *            The identiﬁer for the folder.
	 * @param depth
	 *            The number of levels of depth in the folder hierarchy from which
	 *            to return results. Valid values are: 1 Return only objects that
	 *            are children of the folder; Integer value greater than 1 Return
	 *            only objects that are children of the folder and descendants up to
	 *            value levels deep; -1 Return ALL descendant objects at all depth
	 *            levels in the CMIS hierarchy. The default value is repository
	 *            speciﬁc and SHOULD be at least 2 or -1.
	 * @param filter
	 *            Value indicating which properties for objects MUST be returned.
	 *            This ﬁlter is a list of property query names and NOT a list of
	 *            property ids. The query names of secondary type properties MUST
	 *            follow the pattern
	 *            &lt;secondaryTypeQueryName&gt;.&lt;propertyQueryName&gt;.
	 * @param includeAllowableActions
	 *            If TRUE, then the Repository MUST return the available actions for
	 *            each object in the result set.
	 * @param renditionFilter
	 *            The Repository MUST return the set of renditions whose kind
	 *            matches this ﬁlter. See section below for the ﬁlter grammar.
	 *            Defaults to "cmis:none".
	 * @param includeRelationships
	 *            Enum includeRelationships Value indicating what relationships in
	 *            which the objects returned participate MUST be returned, if any.
	 *            Values are:
	 * 
	 *            none No relationships MUST be returned. (Default) source Only
	 *            relationships in which the objects returned are the source MUST be
	 *            returned. target Only relationships in which the objects returned
	 *            are the target MUST be returned. both Relationships in which the
	 *            objects returned are the source or the target MUST be returned.
	 * @param includePathSegment
	 *            If TRUE, returns a PathSegment for each child object for use in
	 *            constructing that object’s path.
	 * @return List - A tree of the child objects for the speciﬁed folder.
	 */
	public List<ObjectInFolderContainer> getFolderTree(String folderId, BigInteger depth, String filter,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment);

	/**
	 * Gets the parent folder object for the speciﬁed folder object.
	 *
	 * @param folderId
	 *            The identiﬁer for the folder.
	 * @param filter
	 *            Value indicating which properties for objects MUST be returned.
	 *            This ﬁlter is a list of property query names and NOT a list of
	 *            property ids. The query names of secondary type properties MUST
	 *            follow the pattern
	 *            &lt;secondaryTypeQueryName&gt;.&lt;propertyQueryName&gt;.
	 * @return ObjectData - The parent folder object of the speciﬁed folder.
	 */
	public ObjectData getFolderParent(String folderId, String filter);

	/**
	 * Gets the parent folder(s) for the speciﬁed ﬁleable object.
	 *
	 * @param objectId
	 *            The identiﬁer for the object.
	 * @param filter
	 *            Value indicating which properties for objects MUST be returned.
	 *            This ﬁlter is a list of property query names and NOT a list of
	 *            property ids. The query names of secondary type properties MUST
	 *            follow the pattern
	 *            &lt;secondaryTypeQueryName&gt;.&lt;propertyQueryName&gt;.
	 * @param includeAllowableActions
	 *            If TRUE, then the Repository MUST return the available actions for
	 *            each object in the result set.
	 * @param renditionFilter
	 *            The Repository MUST return the set of renditions whose kind
	 *            matches this ﬁlter. See section below for the ﬁlter grammar.
	 *            Defaults to "cmis:none".
	 * @param includeRelationships
	 *            Enum includeRelationships Value indicating what relationships in
	 *            which the objects returned participate MUST be returned, if any.
	 *            Values are:
	 * 
	 *            none No relationships MUST be returned. (Default) source Only
	 *            relationships in which the objects returned are the source MUST be
	 *            returned. target Only relationships in which the objects returned
	 *            are the target MUST be returned. both Relationships in which the
	 *            objects returned are the source or the target MUST be returned.
	 * @param includeRelativePathSegment
	 *            If TRUE, returns a relativePathSegment for each parent object for
	 *            use in constructing that object’s paths.
	 * @return List - list of the parent folder(s) of the speciﬁed objects. Empty
	 *         for the root folder and unﬁled objects.
	 */
	public List<ObjectParentData> getObjectParents(String objectId, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, Boolean includeRelativePathSegment);

	/**
	 * Gets the list of documents that are checked out that the user has access to.
	 *
	 * @param folderId
	 *            The identiﬁer for the folder. If speciﬁed, the repository MUST
	 *            only return checked out documents that are child-objects of the
	 *            speciﬁed folder. If not speciﬁed, the repository MUST return
	 *            checked out documents from anywhere in the repository hierarchy.
	 * @param filter
	 *            Value indicating which properties for objects MUST be returned.
	 *            This ﬁlter is a list of property query names and NOT a list of
	 *            property ids. The query names of secondary type properties MUST
	 *            follow the pattern
	 *            &lt;secondaryTypeQueryName&gt;.&lt;propertyQueryName&gt;.
	 * @param orderBy
	 *            Used to deﬁne the order of the list of objects returned by
	 *            getChildren and getCheckedOutDocs. If the optional capability
	 *            capabilityOrderBy is "none" and this parameter is set, the
	 *            repository SHOULD return an invalidArgument error.
	 * @param includeAllowableActions
	 *            If TRUE, then the Repository MUST return the available actions for
	 *            each object in the result set.
	 * @param renditionFilter
	 *            The Repository MUST return the set of renditions whose kind
	 *            matches this ﬁlter. See section below for the ﬁlter grammar.
	 *            Defaults to "cmis:none".
	 * @param includeRelationships
	 *            Enum includeRelationships Value indicating what relationships in
	 *            which the objects returned participate MUST be returned, if any.
	 *            Values are:
	 * 
	 *            none No relationships MUST be returned. (Default) source Only
	 *            relationships in which the objects returned are the source MUST be
	 *            returned. target Only relationships in which the objects returned
	 *            are the target MUST be returned. both Relationships in which the
	 *            objects returned are the source or the target MUST be returned.
	 * @param maxItems
	 *            This is the maximum number of items to return in a response. The
	 *            repository MUST NOT exceed this maximum.
	 * @param skipCount
	 *            This is the number of potential results that the repository MUST
	 *            skip/page over before returning any results.
	 * @return ObjectList - A list of checked out documents.
	 */
	public ObjectList getCheckedOutDocs(String folderId, String filter, String orderBy, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, BigInteger maxItems,
			BigInteger skipCount);

}
