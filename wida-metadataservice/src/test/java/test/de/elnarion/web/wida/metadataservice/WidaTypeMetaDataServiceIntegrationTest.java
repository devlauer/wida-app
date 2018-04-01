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
package test.de.elnarion.web.wida.metadataservice;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import de.elnarion.web.wida.common.test.IntegrationTest;
import de.elnarion.web.wida.metadataservice.WidaTypeMetaDataService;

/**
 * The Class WidaTypeServiceIntegrationTest tests the TypeService via
 * integration tests.
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
public class WidaTypeMetaDataServiceIntegrationTest extends WildflyTestServerSetup{

	@Inject
	private WidaTypeMetaDataService typeService;

	/**
	 * Instantiates a new wida type service integration test.
	 */
	public WidaTypeMetaDataServiceIntegrationTest() {
	}


	/**
	 * Test get property definition.
	 */
	@Test
	public final void testGetPropertyDefinition() {
		typeService.getPropertyDefinition("cmis:objectTypeId");
	}

}
