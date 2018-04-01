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
package de.elnarion.web.wida.ejb.discoveryservice.impl;

import java.math.BigInteger;

import javax.ejb.Stateless;

import org.apache.chemistry.opencmis.commons.data.ObjectList;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNotSupportedException;
import org.apache.chemistry.opencmis.commons.spi.Holder;

import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.ejb.discoveryservice.WidaDiscoveryService;

/**
 * The Class WidaDiscoveryServiceImpl implements methods to search for
 * query-able objects within the repository.
 */
@Stateless
public class WidaDiscoveryServiceImpl implements WidaDiscoveryService {

	@Override
	public ObjectList query(String statement, Boolean searchAllVersions, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, BigInteger maxItems,
			BigInteger skipCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectList getContentChanges(Holder<String> changeLogToken, Boolean includeProperties, String filter,
			Boolean includePolicyIds, Boolean includeAcl, BigInteger maxItems) {
		throw new CmisNotSupportedException(
				"This repository does not allow to get content changes, they are not persisted.",
				WidaErrorConstants.NOT_SUPPORTED_GET_CONTENT_CHANGES);
	}

}
