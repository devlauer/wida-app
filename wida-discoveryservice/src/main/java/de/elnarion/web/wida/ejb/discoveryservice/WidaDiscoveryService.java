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
package de.elnarion.web.wida.ejb.discoveryservice;

import java.math.BigInteger;

import javax.ejb.Local;

import org.apache.chemistry.opencmis.commons.data.ObjectList;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.spi.Holder;

/**
 * The Interface WidaDiscoveryService provides methods to search for query-able
 * objects within the repository.
 */
@Local
public interface WidaDiscoveryService {

	/**
	 * Executes a CMIS query statement against the contents of the repository.
	 *
	 * @param statement
	 *            CMIS query to be executed.
	 * @param searchAllVersions
	 *            If TRUE, then the repository MUST include latest and non-latest
	 *            versions of document objects in the query search scope.
	 * 
	 *            If FALSE (default), then the repository MUST only include latest
	 *            versions of documents in the query search scope.
	 * 
	 *            If the repository does not support the optional
	 *            capabilityAllVersionsSearchable capability, then this parameter
	 *            value MUST be set to FALSE.
	 * @param includeAllowableActions
	 *            For query statements where the SELECT clause contains properties
	 *            from only one virtual table reference (i.e. referenced
	 *            object-type), any value for this parameter may be used. If the
	 *            SELECT clause contains properties from more than one table, then
	 *            the value of this parameter MUST be "FALSE". Defaults to FALSE.
	 * @param includeRelationships
	 *            For query statements where the SELECT clause contains properties
	 *            from only one virtual table reference (i.e. referenced
	 *            object-type), any value for this enum may be used. If the SELECT
	 *            clause contains properties from more than one table, then the
	 *            value of this parameter MUST be none. Defaults to none.
	 * @param renditionFilter
	 *            If the SELECT clause contains properties from more than one table,
	 *            then the value of this parameter MUST not be set.
	 * @param maxItems
	 *            This is the maximum number of items to return in a response. The
	 *            repository MUST NOT exceed this maximum. Default is
	 *            repository-speciﬁc.
	 * @param skipCount
	 *            This is the number of potential results that the repository MUST
	 *            skip/page over before returning any results. Defaults to 0.
	 * @return The set of results for the query.
	 */
	public ObjectList query(String statement, Boolean searchAllVersions, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, BigInteger maxItems,
			BigInteger skipCount);

	/**
	 * Gets a list of content changes. This service is intended to be used by search
	 * crawlers or other applications that need to eﬃciently understand what has
	 * changed in the repository.
	 * 
	 * The content stream is NOT returned for any change event.
	 * 
	 * The latest change log token for a repository can be acquired via the
	 * getRepositoryInfo service.
	 * 
	 * If a change log token is speciﬁed and the repository knows the corresponding
	 * change event, then the repository MUST return this change event as the ﬁrst
	 * entry in the result set. A client can then compare the ﬁrst change event of
	 * the call with the last change event of the previous call. If they match, the
	 * client knows that it didn’t miss a change event. If they don’t match, the
	 * repository truncated the change log between this call and previous call. That
	 * is, the client missed one or more change events and may have to re-crawl the
	 * repository.
	 *
	 * @param changeLogToken
	 *            If speciﬁed, then the repository MUST return the change event
	 *            corresponding to the value of the speciﬁed change log token as the
	 *            ﬁrst result in the output.
	 * 
	 *            If not speciﬁed, then the repository MUST return the ﬁrst change
	 *            event recorded in the change log.
	 * @param includeProperties
	 *            If TRUE, then the repository MUST include the updated property
	 *            values for "updated" change events if the repository supports
	 *            returning property values as speciﬁed by capbilityChanges.
	 * 
	 *            If FALSE (default), then the repository MUST NOT include the
	 *            updated property values for "updated" change events. The single
	 *            exception to this is that the property cmis:objectId MUST always
	 *            be included.
	 * @param filter
	 *            the filter
	 * @param includePolicyIds
	 *            If TRUE, then the repository MUST include the ids of the policies
	 *            applied to the object referenced in each change event, if the
	 *            change event modiﬁed the set of policies applied to the object.
	 * 
	 *            If FALSE (default), then the repository MUST not include policy
	 *            information.
	 * @param includeAcl
	 *            If TRUE, then the repository MUST return the ACLs for each object
	 *            in the result set. Defaults to FALSE.
	 * @param maxItems
	 *            This is the maximum number of items to return in a response. The
	 *            repository MUST NOT exceed this maximum. Default is
	 *            repository-speciﬁc.
	 * @return ObjectList - the content changes (A change event represents a single
	 *         action that occurred to an object in the repository that affected the
	 *         persisted state of the object.)
	 */
	public ObjectList getContentChanges(Holder<String> changeLogToken, Boolean includeProperties, String filter,
			Boolean includePolicyIds, Boolean includeAcl, BigInteger maxItems);

}
