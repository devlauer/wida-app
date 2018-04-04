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
package de.elnarion.web.wida.metadataservice.impl;

import java.math.BigInteger;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.apache.chemistry.opencmis.commons.enums.Cardinality;
import org.apache.chemistry.opencmis.commons.enums.DecimalPrecision;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.commons.beanutils.DynaBean;

import de.elnarion.ddlutils.Platform;
import de.elnarion.ddlutils.PlatformFactory;
import de.elnarion.ddlutils.model.CascadeActionEnum;
import de.elnarion.ddlutils.model.CloneHelper;
import de.elnarion.ddlutils.model.Column;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.ForeignKey;
import de.elnarion.ddlutils.model.IndexColumn;
import de.elnarion.ddlutils.model.NonUniqueIndex;
import de.elnarion.ddlutils.model.Reference;
import de.elnarion.ddlutils.model.Table;
import de.elnarion.web.wida.common.WidaErrorConstants;
import de.elnarion.web.wida.metadataservice.WidaMetaDataConstants;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionBase;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionBoolean;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionDateTime;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionDecimal;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionHTML;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionId;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionInteger;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionString;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.PropertyDefinitionURI;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeBase;

/**
 * The Class WidaContentMetaDataStructureManager is an ejb responsible for
 * managing the database structure of the additional properties of new types
 * beyond the base types.
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class WidaContentMetaDataStructureManager {

	/** The wida datasource. */
	@Resource(lookup = "jdbc/wida_xads", type = DataSource.class)
	private DataSource widaDatasource;

	private Database databaseModel;

	private CloneHelper cloneHelper = new CloneHelper();

	private Platform platform = null;

	/**
	 * Compares the structure of the database with the TypeDefinition and adds
	 * missing tables and columns. Because this repository does not allow to remove
	 * properties or change their type no further processing is done.
	 *
	 * @param paramTypeWithSubHierarchy
	 *            the param type with sub hierarchy
	 * @param ignoreTypeHierarchy
	 *            the ignore type hierarchy
	 */
	@Lock(LockType.WRITE)
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateDatabaseStructure(TypeBase paramTypeWithSubHierarchy, boolean ignoreTypeHierarchy) {
		Database actualMetadata = databaseModel;
		
		CloneHelper cloneHelper = new CloneHelper();
		Database desiredMetaData = cloneHelper.clone(actualMetadata);

		// ignore base types (id equals basetypeid) because they are already part of the
		// database structure and are accessed via JPA - process only dynamic child
		// types
		if (paramTypeWithSubHierarchy.getBaseTypeId().value().equals(paramTypeWithSubHierarchy.getId())) {
			if (!ignoreTypeHierarchy) {
				Set<TypeBase> children = paramTypeWithSubHierarchy.getChildren();
				if (children != null && children.size() > 0) {
					updateModel(children, desiredMetaData);
				}
			}
			else
			{
				// nothing to do return no childs to process and no base type to process
				return;
			}
		} else {
			updateModel(paramTypeWithSubHierarchy, desiredMetaData,ignoreTypeHierarchy);
		}
		platform.alterModel(actualMetadata, desiredMetaData, false);
		reReadModel();
	}

	/**
	 * Update model.
	 *
	 * @param children
	 *            the children
	 * @param desiredMetaData
	 *            the desired meta data
	 */
	private void updateModel(Set<TypeBase> children, Database desiredMetaData) {
		for (TypeBase typeDefinition : children) {
			updateModel(typeDefinition, desiredMetaData, false);
		}
	}

	/**
	 * Update model.
	 *
	 * @param typeDefinition
	 *            the type definition
	 * @param desiredMetaData
	 *            the desired meta data
	 */
	private void updateModel(TypeBase typeDefinition, Database desiredMetaData, boolean ignoreHierarchy) {
		String tableName = typeDefinition.getTablename();
		Table typeDefinitionTable = desiredMetaData.findTable(tableName);
		if (typeDefinitionTable == null) {
			// create basic table definition with id column and foreign key to content item
			// table's id column
			typeDefinitionTable = new Table();
			typeDefinitionTable.setName(tableName);
			typeDefinitionTable.setSchema(WidaMetaDataConstants.METADATA_DB_SCHEMA);
			typeDefinitionTable.setDescription("Metadatatable for type definition " + typeDefinition.getDisplayName());
			Column idColumn = createIdColumn();
			idColumn.setDescription("This id column references the id column of the table cm_item");
			typeDefinitionTable.addColumn(idColumn);
			Table contentItemTable = desiredMetaData.findTable(WidaMetaDataConstants.METADATA_CONTENT_ITEM_TABLE);
			if (contentItemTable == null)
				throw new CmisRuntimeException(
						"Content item table is not found in the database! Something went wrong. Please check startup of the repository for errors or the db for table existance.",
						WidaErrorConstants.RUNTIME_EXCEPTION_CONTENT_TABLE_MISSING);
			Column contentItemTableIdColumn = contentItemTable.findColumn("id");
			ForeignKey foreignKey = createForeignKey(tableName, idColumn, contentItemTable, contentItemTableIdColumn);
			typeDefinitionTable.addForeignKey(foreignKey);
			desiredMetaData.addTable(typeDefinitionTable);
		}
		List<PropertyDefinitionBase<?>> properties = typeDefinition.getPropertyDefinitionsList();
		for (PropertyDefinitionBase<?> propertyDefinition : properties) {
			// ignore inherited property defintions because they are already defined in the
			// parent types
			if (propertyDefinition.isInherited())
				continue;
			// single value properties can be part of the type definition table,
			// multi-value properties have to have a separate metadata table
			if (propertyDefinition.getCardinality().equals(Cardinality.SINGLE)) {
				Column propertyColumn = typeDefinitionTable.findColumn(propertyDefinition.getColumnName());
				if (propertyColumn == null) {
					propertyColumn = new Column();
					typeDefinitionTable.addColumn(propertyColumn);
				}
				mapPropertyDefinitionToColumn(propertyColumn, propertyDefinition);
			} else {
				defineMultiValuePropertyTable(desiredMetaData, propertyDefinition, typeDefinition.getTablename());
			}

		}
		if (!ignoreHierarchy) {
			Set<TypeBase> children = typeDefinition.getChildren();
			if (children != null && children.size() > 0) {
				updateModel(children, desiredMetaData);
			}
		}
	}

	/**
	 * Map property definition to column.
	 *
	 * @param propertyColumn
	 *            the property column
	 * @param propertyDefinition
	 *            the property definition
	 */
	private void mapPropertyDefinitionToColumn(Column propertyColumn, PropertyDefinitionBase<?> propertyDefinition) {
		propertyColumn.setDescription(propertyDefinition.getDescription());
		propertyColumn.setName(propertyDefinition.getColumnName());
		propertyColumn.setPrimaryKey(false);
		propertyColumn.setRequired(propertyDefinition.isRequired());
		if (propertyDefinition instanceof PropertyDefinitionDecimal) {
			DecimalPrecision decimalPrecision = ((PropertyDefinitionDecimal) propertyDefinition).getPrecision();
			// decimal in cmis is defined as a floating point number with two precisions (32
			// bit or 64 Bit)
			// so it is mapped to float or double - default is double
			if (decimalPrecision != null) {
				boolean isFloatPrecision = decimalPrecision.value()
						.equals(new BigInteger(WidaMetaDataConstants.METADATA_PRECISION_FLOAT));
				propertyColumn.setTypeCode(isFloatPrecision ? Types.FLOAT : Types.DOUBLE);
			} else {
				propertyColumn.setTypeCode(Types.DOUBLE);
			}
		} else if (propertyDefinition instanceof PropertyDefinitionString) {
			PropertyDefinitionString stringProperty = (PropertyDefinitionString) propertyDefinition;
			propertyColumn.setTypeCode(Types.VARCHAR);
			propertyColumn.setSize(
					stringProperty.getMaxLength() == null ? WidaMetaDataConstants.METADATA_CONTENT_STRING_MAX_LENGTH
							: stringProperty.getMaxLength().toString());
		} else if (propertyDefinition instanceof PropertyDefinitionBoolean) {
			propertyColumn.setTypeCode(Types.BOOLEAN);
		} else if (propertyDefinition instanceof PropertyDefinitionInteger) {
			propertyColumn.setTypeCode(Types.INTEGER);
		} else if (propertyDefinition instanceof PropertyDefinitionDateTime) {
			PropertyDefinitionDateTime dateTimeProperty = (PropertyDefinitionDateTime) propertyDefinition;
			switch (dateTimeProperty.getDateTimeResolution()) {
			case YEAR:
				propertyColumn.setTypeCode(Types.INTEGER);
				break;
			case DATE:
				propertyColumn.setTypeCode(Types.DATE);
				break;
			case TIME:
				propertyColumn.setTypeCode(Types.TIME);
				break;
			default:
				propertyColumn.setTypeCode(Types.DATE);
			}
		} else if (propertyDefinition instanceof PropertyDefinitionURI) {
			propertyColumn.setTypeCode(Types.VARCHAR);
			propertyColumn.setSize(WidaMetaDataConstants.METADATA_CONTENT_URI_MAX_LENGTH);
		} else if (propertyDefinition instanceof PropertyDefinitionId) {
			propertyColumn.setTypeCode(Types.VARCHAR);
			propertyColumn.setSize(WidaMetaDataConstants.METADATA_CONTENT_ID_MAX_LENGTH);
		} else if (propertyDefinition instanceof PropertyDefinitionHTML) {
			propertyColumn.setTypeCode(Types.CLOB);
		}
	}

	/**
	 * Creates the foreign key.
	 *
	 * @param localTableName
	 *            the local table name
	 * @param localColumnDefinition
	 *            the local column definition
	 * @param foreignTableDefinition
	 *            the foreign table definition
	 * @param foreignColumnDefinition
	 *            the foreign column definition
	 * @return the foreign key
	 */
	private ForeignKey createForeignKey(String localTableName, Column localColumnDefinition,
			Table foreignTableDefinition, Column foreignColumnDefinition) {
		ForeignKey foreignKey = new ForeignKey();
		foreignKey.setForeignTable(foreignTableDefinition);
		foreignKey.setName(WidaMetaDataConstants.METADATA_FOREIGN_KEY_PREFIX + localTableName + "_"
				+ localColumnDefinition.getName());
		foreignKey.setOnDelete(CascadeActionEnum.CASCADE);
		foreignKey.setOnUpdate(CascadeActionEnum.CASCADE);
		Reference fkReference = new Reference();
		fkReference.setForeignColumn(foreignColumnDefinition);
		fkReference.setLocalColumn(localColumnDefinition);
		foreignKey.addReference(fkReference);
		return foreignKey;
	}

	/**
	 * Creates the id column.
	 *
	 * @return the column
	 */
	private Column createIdColumn() {
		Column idColumn = new Column();
		idColumn.setName("id");
		idColumn.setPrimaryKey(true);
		idColumn.setRequired(true);
		idColumn.setTypeCode(Types.INTEGER);
		return idColumn;
	}

	/**
	 * Define multi value property table.
	 *
	 * @param desiredMetaData
	 *            the desired meta data
	 * @param propertyDefinition
	 *            the property definition
	 * @param tablename
	 *            the tablename
	 */
	private void defineMultiValuePropertyTable(Database desiredMetaData, PropertyDefinitionBase<?> propertyDefinition,
			String tablename) {
		String multivaluetablename = createMultivalueTablename(propertyDefinition, tablename);
		Table multivalueTable = desiredMetaData.findTable(multivaluetablename);

		if (multivalueTable == null) {
			// create basic table definition with id column and foreign key to parent
			// table id column
			multivalueTable = new Table();
			multivalueTable.setName(multivaluetablename);
			multivalueTable.setSchema(WidaMetaDataConstants.METADATA_DB_SCHEMA);
			multivalueTable.setDescription(
					"Metadatatable for multi value property type definition " + propertyDefinition.getDisplayName());
			Column vidColumn = new Column();
			vidColumn.setName("vid");
			vidColumn.setPrimaryKey(true);
			vidColumn.setRequired(true);
			vidColumn.setTypeCode(Types.INTEGER);
			vidColumn.setAutoIncrement(true);
			multivalueTable.addColumn(vidColumn);

			Column tidColumn = new Column();
			tidColumn.setName("parent_id");
			tidColumn.setPrimaryKey(false);
			tidColumn.setRequired(true);
			tidColumn.setTypeCode(Types.INTEGER);
			tidColumn.setAutoIncrement(false);
			tidColumn.setDescription("This parent_id column references the id column of the table " + tablename);
			multivalueTable.addColumn(tidColumn);

			IndexColumn tindexColumn = new IndexColumn();
			tindexColumn.setColumn(tidColumn);

			NonUniqueIndex tidIndex = new NonUniqueIndex();
			tidIndex.setName(WidaMetaDataConstants.METADATA_INDEX_PREFIX + multivaluetablename + "_parent_id");
			multivalueTable.addIndex(tidIndex);

			Table parentTypeTable = desiredMetaData.findTable(tablename);
			if (parentTypeTable == null)
				throw new CmisRuntimeException(tablename
						+ " table is not found in the database! Something went wrong. Please check startup of the repository for errors or the db for table existance.",
						WidaErrorConstants.RUNTIME_EXCEPTION_CONTENT_TABLE_MISSING);
			Column contentItemTableIdColumn = parentTypeTable.findColumn("id");
			ForeignKey foreignKey = createForeignKey(multivaluetablename, tidColumn, parentTypeTable,
					contentItemTableIdColumn);
			multivalueTable.addForeignKey(foreignKey);
			desiredMetaData.addTable(multivalueTable);
		}
		Column valueColumn = multivalueTable.findColumn(propertyDefinition.getColumnName());
		if (valueColumn == null)
			valueColumn = new Column();
		mapPropertyDefinitionToColumn(valueColumn, propertyDefinition);
		multivalueTable.addColumn(valueColumn);
	}

	private String createMultivalueTablename(PropertyDefinitionBase<?> propertyDefinition, String tablename) {
		return tablename + "_multi_" + propertyDefinition.getColumnName();
	}

	/**
	 * Gets the wida datasource.
	 *
	 * @return DataSource - the wida datasource
	 */
	public DataSource getWidaDatasource() {
		return widaDatasource;
	}

	/**
	 * Sets the wida datasource.
	 *
	 * @param widaDatasource
	 *            the wida datasource
	 */
	public void setWidaDatasource(DataSource widaDatasource) {
		this.widaDatasource = widaDatasource;
	}


	/**
	 * Fetch properties for id.
	 *
	 * @param paramType
	 *            the param type
	 * @param paramId
	 *            the param id
	 * @return the map
	 */
	@Lock(LockType.READ)
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Map<String, Object> fetchPropertiesForId(TypeBase paramType, Long paramId) {
		Map<String, Object> resultProperties = new HashMap<>();
		fetchValuesForType(paramType, resultProperties, platform, paramId);
		return resultProperties;
	}

	private String asIdentifier(Platform platform, String name) {
		if (platform.isDelimitedIdentifierModeOn()) {
			return platform.getPlatformInfo().getDelimiterToken() + name
					+ platform.getPlatformInfo().getDelimiterToken();
		} else {
			return name;
		}
	}

	private void fetchValuesForType(TypeBase paramTypeBase, Map<String, Object> properties, Platform platform,
			Long paramId) {
		// empty parent should be ignored although this should not happen (empty parent
		// is only allowed for base types)
		if (paramTypeBase == null)
			return;
		// base type tables should be ignored
		if (paramTypeBase.getId().equals(paramTypeBase.getBaseTypeId().value()))
			return;
		Table typeTable = databaseModel.findTable(paramTypeBase.getTablename());
		List<DynaBean> result = platform.fetch(databaseModel,
				"SELECT * FROM " + asIdentifier(platform, typeTable.getName()) + " where id=" + paramId,
				new Table[] { typeTable });
		// for normal type tables it is only possible to have one row in it
		if (result != null && result.size() > 0)
			throw new CmisRuntimeException(
					"Found more than one row for " + paramId + " in table " + typeTable.getName());
		if (result != null) {
			List<PropertyDefinitionBase<?>> propertyDefinitions = paramTypeBase.getPropertyDefinitionsList();
			for (PropertyDefinitionBase<?> propertyDefinition : propertyDefinitions) {
				if (propertyDefinition.getCardinality().equals(Cardinality.SINGLE)) {
					DynaBean tableRow = result.iterator().next();
					Object value = tableRow.get(propertyDefinition.getColumnName());
					properties.put(propertyDefinition.getId(), value);
				} else {
					String multivalueTableName = createMultivalueTablename(propertyDefinition,
							paramTypeBase.getTablename());
					fetchMultiValueTableRows(propertyDefinition, multivalueTableName, platform, properties, paramId);
				}
			}
		}
		fetchValuesForType(paramTypeBase.getParent(), properties, platform, paramId);
	}

	private void fetchMultiValueTableRows(PropertyDefinitionBase<?> property, String multivalueTableName,
			Platform platform, Map<String, Object> properties, Long paramId) {
		Table multivalueTable = databaseModel.findTable(multivalueTableName);
		List<DynaBean> result = platform.fetch(databaseModel,
				"SELECT * FROM " + asIdentifier(platform, multivalueTableName) + " where parent_id=" + paramId,
				new Table[] { multivalueTable });
		if (result != null) {
			List<Object> values = new Vector<>();
			properties.put(property.getId(), values);
			for (DynaBean dbean : result) {
				Object value = dbean.get(property.getColumnName());
				values.add(value);
			}
		} else {
			properties.put(property.getId(), new Vector<>());
		}
	}

	/**
	 * Re read model in a separate transaction.
	 */
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Lock(LockType.WRITE)
	public void reReadModel() {
		databaseModel = platform.readModelFromDatabase(WidaMetaDataConstants.METADATA_DB_SCHEMA);
	}

	/**
	 * Drop tables.
	 *
	 * @param repoValue
	 *            the repo value
	 */
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Lock(LockType.WRITE)
	public void dropTables(TypeBase repoValue) {
		List<Table> tablesToDrop = new ArrayList<>();
		findAndAddTableObjectsForType(repoValue, tablesToDrop);
		Database desiredModel = cloneHelper.clone(databaseModel);
		desiredModel.removeTables(tablesToDrop.toArray(new Table[0]));
		reReadModel();
	}

	private void findAndAddTableObjectsForType(TypeBase repoValue, List<Table> tablesToDrop) {
		String tablename = repoValue.getTablename();
		// first add property tables
		List<PropertyDefinitionBase<?>> properties = repoValue.getPropertyDefinitionsList();
		if (properties != null)
			for (PropertyDefinitionBase<?> propertyDefinition : properties) {
				if (Cardinality.MULTI.equals(propertyDefinition.getCardinality())) {
					String multivaluetablename = createMultivalueTablename(propertyDefinition, tablename);
					Table multivalueTable = databaseModel.findTable(multivaluetablename);
					tablesToDrop.add(multivalueTable);
				}
			}
		// second add type tables
		Set<TypeBase> children = repoValue.getChildren();
		if (children != null)
			for (TypeBase child : children) {
				findAndAddTableObjectsForType(child, tablesToDrop);
			}
		// last add own table
		Table typeTable = databaseModel.findTable(tablename);
		tablesToDrop.add(typeTable);
	}

	/**
	 * Inits the singleton bean.
	 */
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@PostConstruct
	public void init() {
		platform = PlatformFactory.createNewPlatformInstance(getWidaDatasource());
		reReadModel();
	}

}
