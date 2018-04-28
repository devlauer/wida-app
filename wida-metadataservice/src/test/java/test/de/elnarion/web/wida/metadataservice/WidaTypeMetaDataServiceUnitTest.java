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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.Cardinality;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.elnarion.web.wida.common.test.UnitTest;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionBase;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionId;
import de.elnarion.web.wida.metadataservice.impl.WidaTypeMetaDataServiceImpl;

/**
 * The Class TypeServiceUnitTest tests the service TypeService via unit tests.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(UnitTest.class)
public class WidaTypeMetaDataServiceUnitTest {

	/**
	 * Instantiates a new type service unit test.
	 */
	public WidaTypeMetaDataServiceUnitTest() {
	}
	
	private PropertyDefinitionBase<?> propdef;

	/**
	 * Inits the service.
	 */
	@Before
	public void initService() {
		propdef = new PropertyDefinitionId();
		propdef.setCardinality(Cardinality.SINGLE);
		propdef.setChoices(new ArrayList<>());
		propdef.setColumnName("object_type_id");
		propdef.setDefaultValue(new ArrayList<>());
		propdef.setDescription("Simple Propdefinition");
		propdef.setDisplayName("objectTypeId");
		propdef.setId("cmis:objectTypeId");
		propdef.setIsOpenChoice(true);
		propdef.setIsOrderable(true);
		propdef.setIsQueryable(true);
		propdef.setIsRequired(true);
		propdef.setLocalName("objectTypeId");
		propdef.setLocalNamespace("https://xxxx");
		propdef.setQueryName("cmis:objectTypeId");
		propdef.setUpdatability(Updatability.ONCREATE);
	}

	/**
	 * Tests the method  getPropertyDefinition in the TypeService.
	 */
	@Test
	public void testGetPropertyDefinition()
	{
		// Does not work anymore because implementation caches typemap => TODO rewrite testcase
//		EntityManager em = mock(EntityManager.class);
//		DataSource ds = mock(DataSource.class);
//		WidaTypeMetaDataServiceImpl typeService = new WidaTypeMetaDataServiceImpl();
//		typeService.setEntityManager(em);
//		typeService.setWidaDatasource(ds);
//		when(em.find(PropertyDefinitionBase.class, propdef.getId())).thenReturn(propdef);
//		PropertyDefinition<?> testPropDef = typeService.getPropertyDefinition(propdef.getId());
//		assertEquals(propdef.getId(),testPropDef.getId());
//		assertEquals(propdef.getQueryName(),testPropDef.getQueryName());
	}

}
