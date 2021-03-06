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

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import de.elnarion.web.wida.common.test.IntegrationTest;
import de.elnarion.web.wida.metadataservice.domain.contentmetadata.Document;
import de.elnarion.web.wida.metadataservice.domain.contentmetadata.Document_;
import de.elnarion.web.wida.metadataservice.domain.contentmetadata.Folder;

/**
 * The Class DocumentIntegrationTest.
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
public class DocumentIntegrationTest extends WildflyTestServerSetup {

	@PersistenceContext(unitName = "wida_ctx")
	private EntityManager entityManager;

	@Inject
	private UserTransaction userTransaction;


	/**
	 * Test create object.
	 *
	 * @throws NotSupportedException
	 *             the not supported exception
	 * @throws SystemException
	 *             the system exception
	 * @throws SecurityException
	 *             the security exception
	 * @throws IllegalStateException
	 *             the illegal state exception
	 * @throws RollbackException
	 *             the rollback exception
	 * @throws HeuristicMixedException
	 *             the heuristic mixed exception
	 * @throws HeuristicRollbackException
	 *             the heuristic rollback exception
	 */
	@Test
	public void testCreateObject() throws NotSupportedException, SystemException, SecurityException,
			IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		userTransaction.begin();
		entityManager.joinTransaction();

		Folder testFolder = createFolder();
		entityManager.persist(testFolder);

		Document document = createDocument(testFolder);

		entityManager.persist(document);
		userTransaction.commit();
	}

	private Document createDocument(Folder testFolder) {
		Document document = new Document();
		document.setBaseTypeId("cmis:document");
		document.setCheckinComment(null);
		document.setContentStreamFileName("contentfilename.pdf");
		document.setContentStreamLength(new BigInteger("10"));
		document.setContentStreamMimeType("application/pdf");
		document.setCreatedBy("myself");
		document.setCreationDate(new GregorianCalendar());
		document.setImmutable(true);
		document.setLastMajorVersion(true);
		document.setLastModificationDate(new GregorianCalendar());
		document.setLastModifiedBy("someoneelse");
		document.setLastVersion(true);
		document.setMajorVersion(true);
		document.setName("cool content name");
		document.setObjectTypeId("cmis:document");
		document.setStorageId("some storageid");
		document.setVersionLabel(null);
		document.setVersionSeriesCheckedOut(false);
		document.setVersionSeriesCheckedOutBy(null);
		document.setVersionSeriesCheckedOutId(null);
		document.setVersionSeriesId(null);
		document.setParent(testFolder);
		return document;
	}

	private Folder createFolder() {
		Folder testFolder = new Folder();
		testFolder.setBaseTypeId("cmis:folder");
		testFolder.setCreatedBy("myself");
		testFolder.setCreationDate(new GregorianCalendar());
		testFolder.setLastModificationDate(new GregorianCalendar());
		testFolder.setLastModifiedBy("someoneelse");
		testFolder.setName("xfolder");
		testFolder.setObjectTypeId("cmis:folder");
		return testFolder;
	}

	/**
	 * Test update object.
	 *
	 * @throws SecurityException
	 *             the security exception
	 * @throws IllegalStateException
	 *             the illegal state exception
	 * @throws RollbackException
	 *             the rollback exception
	 * @throws HeuristicMixedException
	 *             the heuristic mixed exception
	 * @throws HeuristicRollbackException
	 *             the heuristic rollback exception
	 * @throws SystemException
	 *             the system exception
	 * @throws NotSupportedException
	 *             the not supported exception
	 */
	@Test
	public void testUpdateObject() throws SecurityException, IllegalStateException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException, SystemException, NotSupportedException {
		userTransaction.begin();
		entityManager.joinTransaction();

		Folder testFolder = createFolder();
		entityManager.persist(testFolder);

		Document document = createDocument(testFolder);

		entityManager.persist(document);
		userTransaction.commit();
		entityManager.clear();
		userTransaction.begin();
		try {
			entityManager.joinTransaction();
			System.out.println("EntityManager part of the current transaction" + entityManager.isJoinedToTransaction());
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Document> query = cb.createQuery(Document.class);
			Root<Document> rootCriteria = query.from(Document.class);
			query.where(cb.equal(rootCriteria.get(Document_.id), document.getId()));
			TypedQuery<Document> query2 = entityManager.createQuery(query);
			try {
				document = query2.getSingleResult();
			} catch (Exception e) {
				e.printStackTrace();
				document=null;
			}
			if (document != null) {
				document.setName("cool updated name");
				entityManager.persist(document);
				userTransaction.commit();
				entityManager.clear();
				userTransaction.begin();
				entityManager.joinTransaction();
				Document documentUpdated = entityManager.find(Document.class, document.getId());
				assertEquals(document.getName(), documentUpdated.getName());
				
			}
			else
			{
				Assert.assertTrue(false);
			}
		} finally {
			userTransaction.commit();
		}
	}

	/**
	 * Test delete object.
	 *
	 * @throws NotSupportedException
	 *             the not supported exception
	 * @throws SystemException
	 *             the system exception
	 * @throws SecurityException
	 *             the security exception
	 * @throws IllegalStateException
	 *             the illegal state exception
	 * @throws RollbackException
	 *             the rollback exception
	 * @throws HeuristicMixedException
	 *             the heuristic mixed exception
	 * @throws HeuristicRollbackException
	 *             the heuristic rollback exception
	 */
	@Test
	public void testDeleteObject() throws NotSupportedException, SystemException, SecurityException,
			IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		userTransaction.begin();
		entityManager.joinTransaction();

		Folder testFolder = createFolder();
		entityManager.persist(testFolder);

		Document document = createDocument(testFolder);

		entityManager.persist(document);
		
		userTransaction.commit();
		entityManager.clear();
		userTransaction.begin();
		try {
			entityManager.joinTransaction();
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Document> query = cb.createQuery(Document.class);
			Root<Document> rootCriteria = query.from(Document.class);
			query.where(cb.equal(rootCriteria.get(Document_.id), document.getId()));
			try {
				document = entityManager.createQuery(query).getSingleResult();
			} catch (Exception e) {
				e.printStackTrace();
				document=null;
			}
			if (document != null) {
				Folder parentFolder = document.getParent();
				entityManager.remove(document);
				entityManager.remove(parentFolder);
			}
			else
			{
				Assert.assertTrue(false);
			}
		} finally {
			userTransaction.commit();
		}
	}

	
}
