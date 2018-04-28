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
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.chemistry.opencmis.commons.enums.Cardinality;
import org.apache.chemistry.opencmis.commons.enums.ContentStreamAllowed;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import de.elnarion.web.wida.common.test.IntegrationTest;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionBase;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionId;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionInteger;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeDocument;

/**
 * The Class TestTypeDocument tests the domain class TypeDocument.
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
public class TypeDocumentIntegrationTest extends WildflyTestServerSetup {

	@PersistenceContext(unitName = "wida_ctx")
	private EntityManager entityManager;
	
	@Inject
	private UserTransaction userTransaction;
	
	private final static String ID="asdf:document";
	
	/**
	 * Tests the creation and persisting of this object.
	 * @throws SystemException 
	 * @throws NotSupportedException 
	 * @throws HeuristicRollbackException 
	 * @throws HeuristicMixedException 
	 * @throws RollbackException 
	 * @throws IllegalStateException 
	 * @throws SecurityException 
	 */
	@Test
	@InSequence(value=1)
	public void testCreateObject() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException
	{
		userTransaction.begin();
		entityManager.joinTransaction();
		List<PropertyDefinitionBase<?>> propertyDefinitionList = new ArrayList<>();
		PropertyDefinitionId idDefinition = new PropertyDefinitionId();
		idDefinition.setCardinality(Cardinality.SINGLE);
		idDefinition.setChoices(null);
		idDefinition.setColumnName("IDX");
		List<String> defaultValuesId = new ArrayList<>();
		defaultValuesId.add("default1");
		defaultValuesId.add("default2");
		idDefinition.setDefaultValue(defaultValuesId);
		idDefinition.setDescription("propdef description");
		idDefinition.setDisplayName("displayname");
		idDefinition.setId("idx");
		idDefinition.setLocalName("idx");
		idDefinition.setLocalNamespace("some namespace");
		idDefinition.setOpenChoice(true);
		idDefinition.setOrderable(false);
		idDefinition.setQueryable(true);
		idDefinition.setQueryName("idx");
		idDefinition.setRequired(true);
		idDefinition.setUpdatability(Updatability.ONCREATE);
		propertyDefinitionList.add(idDefinition);
		PropertyDefinitionInteger integerDefinition = new PropertyDefinitionInteger();
		integerDefinition.setCardinality(Cardinality.SINGLE);
		integerDefinition.setChoices(null);
		integerDefinition.setColumnName("INTEGERX");
		List<BigInteger> defaultValuesInteger = new ArrayList<>();
		defaultValuesInteger.add(new BigInteger("1"));
		integerDefinition.setDefaultValue(defaultValuesInteger);
		integerDefinition.setDescription("propdef description");
		integerDefinition.setDisplayName("displayname");
		integerDefinition.setId("integerx");
		integerDefinition.setLocalName("integerx");
		integerDefinition.setLocalNamespace("some namespace");
		integerDefinition.setOpenChoice(true);
		integerDefinition.setOrderable(false);
		integerDefinition.setQueryable(true);
		integerDefinition.setQueryName("integerx");
		integerDefinition.setRequired(true);
		integerDefinition.setUpdatability(Updatability.ONCREATE);
		propertyDefinitionList.add(integerDefinition);
		
		TypeDocument documentType = new TypeDocument();
		documentType.setContentStreamAllowed(ContentStreamAllowed.ALLOWED);
		documentType.setControllableAcl(false);
		documentType.setControllablePolicy(false);
		documentType.setCreatable(true);
		documentType.setDescription("something");
		documentType.setDisplayName("displayname");
		documentType.setFileable(true);
		documentType.setFulltextIndexed(false);
		documentType.setId(ID);
		documentType.setIncludedInSupertypeQuery(false);
		documentType.setLocalName(ID);
		documentType.setLocalNamespace("some namespace");
		documentType.setParent(null);
		documentType.setPropertyDefinitionsList(propertyDefinitionList);
		documentType.setQueryable(true);
		documentType.setQueryName(ID);
		documentType.setSecondaryTypes(new TreeSet<>());
		documentType.setTablename("TABLENAME");
		documentType.setTypeMutabilityCreate(true);
		documentType.setTypeMutabilityUpdate(true);
		documentType.setTypeMutabiltiyDelete(true);
		documentType.setVersionable(false);
		
		entityManager.persist(documentType);
		
		userTransaction.commit();
	    // clear the persistence context (first-level cache)
		entityManager.clear();
	}
	
	/**
	 * Tests the update process of this object.
	 * @throws SystemException 
	 * @throws HeuristicRollbackException 
	 * @throws HeuristicMixedException 
	 * @throws RollbackException 
	 * @throws IllegalStateException 
	 * @throws SecurityException 
	 * @throws NotSupportedException 
	 */
	@Test
	@InSequence(value=2)
	public void testUpdateObject() throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException, NotSupportedException
	{
		userTransaction.begin();
		entityManager.joinTransaction();
		TypeDocument documentType = entityManager.find(TypeDocument.class,ID);
		Iterator<PropertyDefinitionBase<?>> it = documentType.getPropertyDefinitionsList().iterator();
		PropertyDefinitionBase<?> propDef1 = it.next();
		PropertyDefinitionBase<?> propDef2 = it.next();
		documentType.removePropertyDefinition(propDef2.getId());
		propDef1.setColumnName("NEW_COLUMNNAME");
		documentType.setLocalName("new localname");
		entityManager.persist(documentType);
		userTransaction.commit();
	    // clear the persistence context (first-level cache)
		entityManager.clear();
		userTransaction.begin();
		entityManager.joinTransaction();
		TypeDocument documentTypeUpdated = entityManager.find(TypeDocument.class,ID);
		assertEquals(documentType.getLocalName(), documentTypeUpdated.getLocalName());
		assertTrue(documentTypeUpdated.getPropertyDefinitionsList().size()==1);
		userTransaction.commit();
	}
	
	/**
	 * Tests the deletion process of this object.
	 * @throws SystemException 
	 * @throws NotSupportedException 
	 * @throws HeuristicRollbackException 
	 * @throws HeuristicMixedException 
	 * @throws RollbackException 
	 * @throws IllegalStateException 
	 * @throws SecurityException 
	 */
	@Test
	@InSequence(value=3)
	public void testDeleteObject() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException
	{
		userTransaction.begin();
		entityManager.joinTransaction();
		TypeDocument documentType = entityManager.find(TypeDocument.class,ID);		
		entityManager.remove(documentType);
		userTransaction.commit();
	}
	

}
