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
package de.elnarion.web.wida.ejb.objectservice;

import java.math.BigInteger;
import java.util.List;

import javax.ejb.Local;

import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.BulkUpdateObjectIdAndChangeToken;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.FailedToDeleteData;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.RenditionData;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.spi.Holder;

/**
 * The Interface WidaObjectService encapsulates id-based CRUD (Create, Retrieve,
 * Update, Delete) operations on objects in this repository.
 */
@Local
public interface WidaObjectService {

	/**
	 * Creates a document object of the speciﬁed type (given by the
	 * cmis:objectTypeId property) in the speciﬁed location.
	 *
	 * @param userName
	 *            the user name
	 * @param properties
	 *            The property values that MUST be applied to the newly-created
	 *            document object.
	 * @param folderId
	 *            If speciﬁed, the identiﬁer for the folder that MUST be the parent
	 *            folder for the newly-created document object. This parameter MUST
	 *            be speciﬁed if the repository does NOT support the optional
	 *            "unﬁling" capability.
	 * @param contentStream
	 *            The content stream that MUST be stored for the newly-created
	 *            document object. The method of passing the contentStream to the
	 *            server and the encoding mechanism will be speciﬁed by each speciﬁc
	 *            binding. MUST be required if the type requires it.
	 * @param versioningState
	 *            none - The document MUST be created as a non-versionable document.
	 * @param policies
	 *            A list of policy ids that MUST be applied to the newly-created
	 *            document object.
	 * @param addAces
	 *            A list of ACEs that MUST be added to the newly-created document
	 *            object, either using the ACL from folderId if speciﬁed, or being
	 *            applied if no folderId is speciﬁed.
	 * @param removeAces
	 *            A list of ACEs that MUST be removed from the newly-created
	 *            document object, either using the ACL from folderId if speciﬁed,
	 *            or being ignored if no folderId is speciﬁed.
	 * @return The id of the newly-created document.
	 */
	public String createDocument(String userName,Properties properties, String folderId, ContentStream contentStream,
			VersioningState versioningState, List<String> policies, Acl addAces, Acl removeAces);

	/**
	 * Creates a document object as a copy of the given source document in the
	 * speciﬁed location.
	 *
	 * @param userName
	 *            the user name
	 * @param sourceId
	 *            The identiﬁer for the source document.
	 * @param properties
	 *            The property values that MUST be applied to the object. This list
	 *            of properties SHOULD only contain properties whose values diﬀer
	 *            from the source document.
	 * @param folderId
	 *            If speciﬁed, the identiﬁer for the folder that MUST be the parent
	 *            folder for the newly-created document object.
	 * @param versioningState
	 *            none - The document MUST be created as a non-versionable document.
	 * @param policies
	 *            A list of policy ids that MUST be applied to the newly-created
	 *            document object.
	 * @param addAces
	 *            A list of ACEs that MUST be added to the newly-created document
	 *            object, either using the ACL from folderId if speciﬁed, or being
	 *            applied if no folderId is speciﬁed.
	 * @param removeAces
	 *            A list of ACEs that MUST be removed from the newly-created
	 *            document object, either using the ACL from folderId if speciﬁed,
	 *            or being ignored if no folderId is speciﬁed.
	 * @return The id of the newly-created document.
	 */
	public String createDocumentFromSource(String userName,String sourceId, Properties properties, String folderId,
			VersioningState versioningState, List<String> policies, Acl addAces, Acl removeAces);

	/**
	 * Creates a folder object of the speciﬁed type in the speciﬁed location.
	 *
	 * @param userName
	 *            the user name
	 * @param properties
	 *            The property values that MUST be applied to the newly-created
	 *            folder object.
	 * @param folderId
	 *            The identiﬁer for the folder that MUST be the parent folder for
	 *            the newly-created folder object.
	 * @param policies
	 *            A list of policy ids that MUST be applied to the newly-created
	 *            folder object.
	 * @param addAces
	 *            A list of ACEs that MUST be added to the newly-created folder
	 *            object, either using the ACL from folderId if speciﬁed, or being
	 *            applied if no folderId is speciﬁed.
	 * @param removeAces
	 *            A list of ACEs that MUST be removed from the newly-created folder
	 *            object, either using the ACL from folderId if speciﬁed, or being
	 *            ignored if no folderId is speciﬁed.
	 * @return The id of the newly-created folder.
	 */
	public String createFolder(String userName,Properties properties, String folderId, List<String> policies, Acl addAces,
			Acl removeAces);

	/**
	 * Creates a relationship object of the speciﬁed type.
	 *
	 * @param userName
	 *            the user name
	 * @param properties
	 *            The property values that MUST be applied to the newly-created
	 *            relationship object.
	 * @param policies
	 *            A list of policy ids that MUST be applied to the newly-created
	 *            relationship object.
	 * @param addAces
	 *            A list of ACEs that MUST be added to the newly-created
	 *            relationship object, either using the ACL from folderId if
	 *            speciﬁed, or being applied if no folderId is speciﬁed.
	 * @param removeAces
	 *            A list of ACEs that MUST be removed from the newly-created
	 *            relationship object, either using the ACL from folderId if
	 *            speciﬁed, or being ignored if no folderId is speciﬁed.
	 * @return The id of the newly-created relationship.
	 */
	public String createRelationship(String userName,Properties properties, List<String> policies, Acl addAces, Acl removeAces);

	/**
	 * Creates a policy object of the speciﬁed type.
	 *
	 * @param userName
	 *            the user name
	 * @param properties
	 *            The property values that MUST be applied to the newly-created
	 *            policy object.
	 * @param folderId
	 *            If speciﬁed, the identiﬁer for the folder that MUST be the parent
	 *            folder for the newly-created policy object.
	 * @param policies
	 *            A list of policy ids that MUST be applied to the newly-created
	 *            relationship object.
	 * @param addAces
	 *            A list of ACEs that MUST be added to the newly-created
	 *            relationship object, either using the ACL from folderId if
	 *            speciﬁed, or being applied if no folderId is speciﬁed.
	 * @param removeAces
	 *            A list of ACEs that MUST be removed from the newly-created
	 *            relationship object, either using the ACL from folderId if
	 *            speciﬁed, or being ignored if no folderId is speciﬁed.
	 * @return The id of the newly-created policy.
	 */
	public String createPolicy(String userName,Properties properties, String folderId, List<String> policies, Acl addAces,
			Acl removeAces);

	/**
	 * Creates an item object of the speciﬁed type.
	 *
	 * @param userName
	 *            the user name
	 * @param properties
	 *            The property values that MUST be applied to the newly-created item
	 *            object.
	 * @param folderId
	 *            If speciﬁed, the identiﬁer for the folder that MUST be the parent
	 *            folder for the newly-created item object.
	 * @param policies
	 *            A list of policy ids that MUST be applied to the newly-created
	 *            relationship object.
	 * @param addAces
	 *            A list of ACEs that MUST be added to the newly-created
	 *            relationship object, either using the ACL from folderId if
	 *            speciﬁed, or being applied if no folderId is speciﬁed.
	 * @param removeAces
	 *            A list of ACEs that MUST be removed from the newly-created
	 *            relationship object, either using the ACL from folderId if
	 *            speciﬁed, or being ignored if no folderId is speciﬁed.
	 * @return The id of the newly-created item.
	 */
	public String createItem(String userName,Properties properties, String folderId, List<String> policies, Acl addAces,
			Acl removeAces);

	/**
	 * Gets the speciﬁed information for the object.
	 *
	 * @param objectId
	 *            The identiﬁer for the object.
	 * @param filter
	 *            Value indicating which properties for objects MUST be returned.
	 *            This ﬁlter is a list of property query names and NOT a list of
	 *            property ids. The query names of secondary type properties MUST
	 *            follow the pattern
	 *            &lt;secondaryTypeQueryName&gt;.&lt;propertyQueryName&gt;. * @param
	 *            includeAllowableActions the include allowable actions
	 * @param includeAllowableActions
	 *            If TRUE, then the Repository MUST return the available actions for
	 *            each object in the result set.
	 * @param includeRelationships
	 *            none - No relationships MUST be returned.
	 * @param renditionFilter
	 *            The Repository MUST return the set of renditions whose kind
	 *            matches this ﬁlter.
	 * @param includePolicyIds
	 *            If TRUE, then the Repository MUST return the Ids of the policies
	 *            applied to the object.
	 * @param includeAcl
	 *            If TRUE, then the repository MUST return the ACLs for each object
	 *            in the result set.
	 * @return {@link ObjectData} - the object
	 */
	public ObjectData getObject(String objectId, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
			Boolean includeAcl);

	/**
	 * Gets the speciﬁed information for the object.
	 *
	 * @param path
	 *            The path to the object.
	 * @param filter
	 *            the filter
	 * @param includeAllowableActions
	 *            If TRUE, then the Repository MUST return the available actions for
	 *            each object in the result set.
	 * @param includeRelationships
	 *            none - No relationships MUST be returned.
	 * @param renditionFilter
	 *            The Repository MUST return the set of renditions whose kind
	 *            matches this ﬁlter.
	 * @param includePolicyIds
	 *            If TRUE, then the Repository MUST return the Ids of the policies
	 *            applied to the object.
	 * @param includeAcl
	 *            If TRUE, then the repository MUST return the ACLs for each object
	 *            in the result set.
	 * @return ObjectData - the object by path
	 */
	public ObjectData getObjectByPath(String path, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
			Boolean includeAcl);

	/**
	 * Gets the content stream for the speciﬁed document object, or gets a rendition
	 * stream for a speciﬁed rendition of a document or folder object.
	 *
	 * @param objectId
	 *            The identiﬁer for the object.
	 * @param streamId
	 *            The identiﬁer for the rendition stream, when used to get a
	 *            rendition stream. For documents, if not provided then this method
	 *            returns the content stream. For folders, it MUST be provided.
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @return ContentStream - The speciﬁed content stream or rendition stream for
	 *         the object.
	 */
	public ContentStream getContentStream(String objectId, String streamId, BigInteger offset, BigInteger length);

	/**
	 * Gets the list of associated renditions for the speciﬁed object. Only
	 * rendition attributes are returned, not rendition stream.
	 *
	 * @param objectId
	 *            The identiﬁer for the object.
	 * @param renditionFilter
	 *            The Repository MUST return the set of renditions whose kind
	 *            matches this ﬁlter.
	 * @param maxItems
	 *            the max items
	 * @param skipCount
	 *            the skip count
	 * @return List - The set of renditions.
	 */
	public List<RenditionData> getRenditions(String objectId, String renditionFilter, BigInteger maxItems,
			BigInteger skipCount);

	/**
	 * Updates properties and secondary types of the speciﬁed object.
	 *
	 * @param userName
	 *            the user name
	 * @param objectId
	 *            The identiﬁer for the object.
	 * @param changeToken
	 *            The CMIS base object-type deﬁnitions include an opaque string
	 *            cmis:changeToken property that a repository MAY use for optimistic
	 *            locking and/or concurrency checking to ensure that user updates do
	 *            not conﬂict.
	 * 
	 *            If a repository provides a value for the cmis:changeToken property
	 *            for an object, then all invocations of the "update" methods on
	 *            that object (updateProperties, bulkUpdateProperties,
	 *            setContentStream, appendContentStream, deleteContentStream, etc.)
	 *            MUST provide the value of the cmis:changeToken property as an
	 *            input parameter, and the repository MUST throw an
	 *            updateConflictException if the value speciﬁed for the change token
	 *            does NOT match the change token value for the object being
	 *            updated.
	 * @param properties
	 *            The updated property values that MUST be applied to the object.
	 */
	public void updateProperties(String userName,Holder<String> objectId, Holder<String> changeToken, Properties properties);

	/**
	 * Updates properties and secondary types of one or more objects. Only
	 * properties whose values are different than the original value of the object
	 * SHOULD be provided. This service is not atomic. If the update fails, some
	 * objects might have been updated and others might not have been updated. This
	 * service MUST NOT throw an exception if the update of an object fails. If an
	 * update fails, the object id of this particular object MUST be omitted from
	 * the result.
	 *
	 * @param userName
	 *            the user name
	 * @param objectIdAndChangeToken
	 *            The identiﬁers of the objects to be updated and their change
	 *            tokens. Invalid object ids, for example ids of objects that don’t
	 *            exist, MUST be ignored. Change tokens are optional.
	 * @param properties
	 *            The updated property values that MUST be applied to the objects.
	 * @param addSecondaryTypeIds
	 *            A list of secondary type ids that SHOULD be added to the objects.
	 * @param removeSecondaryTypeIds
	 *            A list of secondary type ids that SHOULD be removed from the
	 *            objects. Secondary type ids in this list that are not attached to
	 *            an object SHOULD be ignored.
	 * @return List of a triple for each updated object composed of:
	 * 
	 *         The original object id. MUST always be set. The new object id if the
	 *         update triggered a new version. MUST NOT be set if no new version has
	 *         been created. The new change token of the object. MUST be set if the
	 *         repository supports change tokens.
	 */
	public List<BulkUpdateObjectIdAndChangeToken> bulkUpdateProperties(String userName,
			List<BulkUpdateObjectIdAndChangeToken> objectIdAndChangeToken, Properties properties,
			List<String> addSecondaryTypeIds, List<String> removeSecondaryTypeIds);

	/**
	 * Moves the speciﬁed ﬁle-able object from one folder to another.
	 *
	 * @param userName
	 *            the user name
	 * @param objectId
	 *            The identiﬁer for the object.
	 * @param targetFolderId
	 *            The folder into which the object is to be moved.
	 * @param sourceFolderId
	 *            The folder from which the object is to be moved.
	 */
	public void moveObject(String userName,Holder<String> objectId, String targetFolderId, String sourceFolderId);

	/**
	 * Deletes the speciﬁed object.
	 *
	 * @param userName
	 *            the user name
	 * @param objectId
	 *            The identiﬁer for the object.
	 * @param allVersions
	 *            If TRUE (default), then delete all versions of the document. If
	 *            FALSE, delete only the document object speciﬁed. The repository
	 *            MUST ignore the value of this parameter when this service is
	 *            invoke on a non-document object or non-versionable document
	 *            object.
	 */
	public void deleteObject(String userName,String objectId, Boolean allVersions);

	/**
	 * Deletes the speciﬁed folder object and all of its child- and
	 * descendant-objects.
	 * 
	 * A repository MAY attempt to delete child- and descendant-objects of the
	 * speciﬁed folder in any order. Any child- or descendant-object that the
	 * repository cannot delete MUST persist in a valid state in the CMIS domain
	 * model. This service is not atomic. However, if deletesinglefiled is chosen
	 * and some objects fail to delete, then single-ﬁled objects are either deleted
	 * or kept, never just unﬁled. This is so that a user can call this command
	 * again to recover from the error by using the same tree.
	 *
	 * @param userName
	 *            the user name
	 * @param folderId
	 *            The identiﬁer of the folder to be deleted.
	 * @param allVersions
	 *            If TRUE (default), then delete all versions of all documents. If
	 *            FALSE, delete only the document versions referenced in the tree.
	 *            The repository MUST ignore the value of this parameter when this
	 *            service is invoked on any non-document objects or non-versionable
	 *            document objects.
	 * @param unfileObjects
	 *            An enumeration specifying how the repository MUST process ﬁle-able
	 *            child- or descendant-objects. Valid values are:
	 * 
	 *            unﬁle Unﬁle all ﬁleable objects.
	 * 
	 *            deletesingleﬁled Delete all
	 * 
	 *            ﬁleable non-folder objects whose only parent-folders are in the
	 *            current folder tree. Unﬁle all other ﬁleable non-folder objects
	 *            from the current folder tree.
	 * 
	 *            delete (default) Delete all ﬁleable objects.
	 * @param continueOnFailure
	 *            If TRUE, then the repository SHOULD continue attempting to perform
	 *            this operation even if deletion of a child- or descendant-object
	 *            in the speciﬁed folder cannot be deleted.
	 * 
	 *            If FALSE (default), then the repository SHOULD abort this method
	 *            when it fails to delete a single child object or descendant
	 *            object.
	 * @return A list of identiﬁers of objects in the folder tree that were not
	 *         deleted.
	 */
	public FailedToDeleteData deleteTree(String userName,String folderId, Boolean allVersions, UnfileObject unfileObjects,
			Boolean continueOnFailure);

	/**
	 * Sets the content stream for the speciﬁed document object.
	 *
	 * @param userName
	 *            the user name
	 * @param objectId
	 *            The identiﬁer for the document object.
	 * @param overwriteFlag
	 *            If TRUE (default), then the repository MUST replace the existing
	 *            content stream for the object (if any) with the input
	 *            contentStream.
	 * 
	 *            If FALSE, then the repository MUST only set the input
	 *            contentStream for the object if the object currently does not have
	 *            a content stream.
	 * @param changeToken
	 *            The CMIS base object-type deﬁnitions include an opaque string
	 *            cmis:changeToken property that a repository MAY use for optimistic
	 *            locking and/or concurrency checking to ensure that user updates do
	 *            not conﬂict.
	 * 
	 *            If a repository provides a value for the cmis:changeToken property
	 *            for an object, then all invocations of the "update" methods on
	 *            that object (updateProperties, bulkUpdateProperties,
	 *            setContentStream, appendContentStream, deleteContentStream, etc.)
	 *            MUST provide the value of the cmis:changeToken property as an
	 *            input parameter, and the repository MUST throw an
	 *            updateConflictException if the value speciﬁed for the change token
	 *            does NOT match the change token value for the object being
	 *            updated.
	 * @param contentStream
	 *            The content stream
	 */
	public void setContentStream(String userName,Holder<String> objectId, Boolean overwriteFlag, Holder<String> changeToken,
			ContentStream contentStream);

	/**
	 * Appends to the content stream for the speciﬁed document object.
	 *
	 * @param userName
	 *            the user name
	 * @param objectId
	 *            The identiﬁer for the document object.
	 * @param changeToken
	 *            The CMIS base object-type deﬁnitions include an opaque string
	 *            cmis:changeToken property that a repository MAY use for optimistic
	 *            locking and/or concurrency checking to ensure that user updates do
	 *            not conﬂict.
	 * 
	 *            If a repository provides a value for the cmis:changeToken property
	 *            for an object, then all invocations of the "update" methods on
	 *            that object (updateProperties, bulkUpdateProperties,
	 *            setContentStream, appendContentStream, deleteContentStream, etc.)
	 *            MUST provide the value of the cmis:changeToken property as an
	 *            input parameter, and the repository MUST throw an
	 *            updateConflictException if the value speciﬁed for the change token
	 *            does NOT match the change token value for the object being
	 *            updated.
	 * @param contentStream
	 *            The content stream that should be appended to the existing
	 *            content.
	 * @param isLastChunk
	 *            If TRUE, then this is the last chunk of the content and the client
	 *            does not intend to send another chunk. If FALSE (default), then
	 *            the repository should except another chunk from the client.
	 * 
	 *            Clients SHOULD always set this parameter but repositories SHOULD
	 *            be prepared that clients don’t provide it.
	 * 
	 *            Repositories may use this ﬂag to trigger some sort of content
	 *            processing. For example, only if isLastChunk is TRUE the
	 *            repsoitory could generate renditions of the content.
	 */
	public void appendContentStream(String userName,Holder<String> objectId, Holder<String> changeToken, ContentStream contentStream,
			boolean isLastChunk);

	/**
	 * Delete content stream.
	 *
	 * @param userName
	 *            the user name
	 * @param objectId
	 *            The identiﬁer for the document object.
	 * @param changeToken
	 *            The CMIS base object-type deﬁnitions include an opaque string
	 *            cmis:changeToken property that a repository MAY use for optimistic
	 *            locking and/or concurrency checking to ensure that user updates do
	 *            not conﬂict.
	 * 
	 *            If a repository provides a value for the cmis:changeToken property
	 *            for an object, then all invocations of the "update" methods on
	 *            that object (updateProperties, bulkUpdateProperties,
	 *            setContentStream, appendContentStream, deleteContentStream, etc.)
	 *            MUST provide the value of the cmis:changeToken property as an
	 *            input parameter, and the repository MUST throw an
	 *            updateConflictException if the value speciﬁed for the change token
	 *            does NOT match the change token value for the object being
	 *            updated.
	 */
	public void deleteContentStream(String userName,Holder<String> objectId, Holder<String> changeToken);

}
