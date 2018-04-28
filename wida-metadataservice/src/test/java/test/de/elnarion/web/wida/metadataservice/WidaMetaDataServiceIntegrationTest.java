package test.de.elnarion.web.wida.metadataservice;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.chemistry.opencmis.commons.enums.Cardinality;
import org.apache.chemistry.opencmis.commons.enums.ContentStreamAllowed;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertiesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIdImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIntegerImpl;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import de.elnarion.web.wida.common.test.IntegrationTest;
import de.elnarion.web.wida.metadataservice.WidaContentMetaDataService;
import de.elnarion.web.wida.metadataservice.WidaTypeMetaDataService;
import de.elnarion.web.wida.metadataservice.domain.contentmetadata.Document;
import de.elnarion.web.wida.metadataservice.domain.contentmetadata.Folder;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionBase;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionId;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionInteger;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeDocument;

/**
 * The Class WidaMetaDataServiceIntegrationTest.
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)public class WidaMetaDataServiceIntegrationTest extends WildflyTestServerSetup{

	/**
	 * Instantiates a new wida meta data service integration test.
	 */
	public WidaMetaDataServiceIntegrationTest() {
	}

	/** The content metadata service. */
	@Inject
	private WidaContentMetaDataService contentMetadataService;
	
	/** The type metadata service. */
	@Inject
	private WidaTypeMetaDataService typeMetadataService;

	
	/**
	 * Gets the type metadata service.
	 *
	 * @return WidaTypeMetaDataService - the type metadata service
	 */
	public WidaTypeMetaDataService getTypeMetadataService() {
		return typeMetadataService;
	}

	/**
	 * Sets the type metadata service.
	 *
	 * @param typeMetadataService
	 *            the type metadata service
	 */
	public void setTypeMetadataService(WidaTypeMetaDataService typeMetadataService) {
		this.typeMetadataService = typeMetadataService;
	}

	/**
	 * Gets the content metadata service.
	 *
	 * @return WidaContentMetaDataService - the content metadata service
	 */
	public WidaContentMetaDataService getContentMetadataService() {
		return contentMetadataService;
	}

	/**
	 * Sets the content metadata service.
	 *
	 * @param contentMetadataService
	 *            the content metadata service
	 */
	public void setContentMetadataService(WidaContentMetaDataService contentMetadataService) {
		this.contentMetadataService = contentMetadataService;
	}


	/**
	 * Do integrated document test.
	 */
	@Test
	public void doIntegratedDocumentTest()
	{
		List<PropertyDefinitionBase<?>> propertyDefinitionList = new ArrayList<>();
		PropertyDefinitionId idDefinition = new PropertyDefinitionId();
		idDefinition.setCardinality(Cardinality.SINGLE);
		idDefinition.setChoices(null);
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
		documentType.setContentStreamAllowed(ContentStreamAllowed.REQUIRED);
		documentType.setControllableAcl(false);
		documentType.setControllablePolicy(false);
		documentType.setCreatable(true);
		documentType.setDescription("something");
		documentType.setDisplayName("displayname");
		documentType.setFileable(true);
		documentType.setFulltextIndexed(false);
		documentType.setId("el:document");
		documentType.setIncludedInSupertypeQuery(false);
		documentType.setLocalName("el:document");
		documentType.setLocalNamespace("some namespace");
		documentType.setParentTypeId("cmis:document");
		documentType.setPropertyDefinitionsList(propertyDefinitionList);
		documentType.setQueryable(true);
		documentType.setQueryName("el:document");
		documentType.setSecondaryTypes(new TreeSet<>());
		documentType.setTypeMutabilityCreate(true);
		documentType.setTypeMutabilityUpdate(true);
		documentType.setTypeMutabiltiyDelete(true);
		documentType.setVersionable(false);

		typeMetadataService.addTypeDefinition(documentType, true);
		Document document = new Document();
		document.setBaseTypeId("cmis:document");
		document.setContentStreamFileName("great_test_document.txt");
		document.setContentStreamId("contentStreamId");
		document.setContentStreamMimeType("plain/txt");
		document.setContentStreamLength(new BigInteger("123546789"));
		document.setName("great_test_document.txt");
		document.setObjectTypeId("el:document");
		document.setCreatedBy("me");
		document.setCreationDate((GregorianCalendar) GregorianCalendar.getInstance());
		document.setImmutable(true);
		document.setLastModifiedBy("me");
		document.setLastMajorVersion(true);
		document.setLastModificationDate(document.getCreationDate());
		document.setLastVersion(true);
		document.setMajorVersion(true);
		Folder rootFolder = contentMetadataService.getRootFolder();
		document.setParent(rootFolder);
		PropertiesImpl cmisProperties = new PropertiesImpl();
		PropertyIdImpl propertyId = new PropertyIdImpl();
		propertyId.setDisplayName(idDefinition.getDisplayName());
		propertyId.setId(idDefinition.getId());
		propertyId.setLocalName(idDefinition.getLocalName());
		propertyId.setPropertyDefinition(idDefinition);
		propertyId.setQueryName(idDefinition.getQueryName());
		propertyId.setValue("testID");
		PropertyIntegerImpl propertyInteger = new PropertyIntegerImpl();
		propertyInteger.setDisplayName(integerDefinition.getDisplayName());
		propertyInteger.setId(integerDefinition.getId());
		propertyInteger.setLocalName(integerDefinition.getLocalName());
		propertyInteger.setPropertyDefinition(integerDefinition);
		propertyInteger.setQueryName(integerDefinition.getQueryName());
		propertyInteger.setValue(new BigInteger("123456"));
		cmisProperties.addProperty(propertyId);
		cmisProperties.addProperty(propertyInteger);
		String objectId = contentMetadataService.createDocument(document, cmisProperties);
		Document resultDocument = contentMetadataService.getDocument(objectId);
		Assert.assertEquals(document.getBaseTypeId(), resultDocument.getBaseTypeId() );
		Assert.assertEquals(document.getContentStreamFileName(), resultDocument.getContentStreamFileName());
		Assert.assertEquals(document.getContentStreamMimeType(), resultDocument.getContentStreamMimeType() );
		Assert.assertEquals(document.getCreatedBy(), resultDocument.getCreatedBy());
		Assert.assertEquals(document.getLastModifiedBy(), resultDocument.getLastModifiedBy() );
		Assert.assertEquals(document.getCreationDate(), resultDocument.getCreationDate());
		Assert.assertEquals(document.getObjectTypeId(), resultDocument.getObjectTypeId());
		Map<String, Object> resultProperties = resultDocument.getProperties();
		Assert.assertEquals(propertyId.getFirstValue(), resultProperties.get("idx"));
		System.out.println(propertyInteger.getFirstValue());
		System.out.println(resultProperties.get("integerx"));
		Assert.assertEquals(""+propertyInteger.getFirstValue(),""+ resultProperties.get("integerx"));
	}
	
}
