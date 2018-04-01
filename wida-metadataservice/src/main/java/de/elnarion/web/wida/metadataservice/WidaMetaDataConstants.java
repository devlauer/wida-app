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
package de.elnarion.web.wida.metadataservice;

// TODO: Auto-generated Javadoc
/**
 * The Interface WidaMetaDataConstants.
 */
public interface WidaMetaDataConstants {

	// global
	
	/** The Constant METADATA_TYPE_PK_PREFIX. */
	public static final String METADATA_PK_PREFIX = "pk_";

	/** The Constant METADATA_CONTENT_INDEX_PREFIX. */
	public final static String METADATA_INDEX_PREFIX = "x_";

	/** The Constant METADATA_FOREIGN_KEY_PREFIX. */
	public static final String METADATA_FOREIGN_KEY_PREFIX = "fk_";
	
	/** The Constant METADATASERVICE_DB_SCHEMA. */
	public static final String METADATA_DB_SCHEMA = "wida";
	
	/** The Constant MAX_NAME_LENGTH. */
	public static final int MAX_NAME_LENGTH =30;

	/** The Constant METADATA_ROOTFOLDER_OBJECT_ID. */
	public static final String METADATA_ROOTFOLDER_OBJECT_ID = "fb7aa0d1-4b52-47c5-9a90-ab81c3f4a5ef";
	
	/** The Constant METADATA_DEFAULT_NAMESPACE. */
	public static final String METADATA_DEFAULT_NAMESPACE = "https://www.github.com/devlauer/wida-app";

	
	// content metadata specific 
	
	/** The Constant CONTENT_METADATA_TABLE_PREFIX. */
	public final static String METADATA_CONTENT_TABLE_PREFIX = "cm_";
	
	/** The Constant METADATA_CONTENT_ITEM_TABLE. */
	public static final String METADATA_CONTENT_ITEM_TABLE = METADATA_CONTENT_TABLE_PREFIX+"item";
	
	/** The Constant METADATA_CONTENT_DOCUMENT_TABLE. */
	public static final String METADATA_CONTENT_DOCUMENT_TABLE = METADATA_CONTENT_TABLE_PREFIX+"document";
	
	// type metadata specific
	
	/** The Constant METADATA_TYPE_TABLE_PREFIX. */
	public static final String METADATA_TYPE_TABLE_PREFIX = "ty_";
	
	/** The Constant METADATA_TYPE_PROPERTYDEFINITION_TABLE. */
	public static final String METADATA_TYPE_PROPERTYDEFINITION_TABLE = METADATA_TYPE_TABLE_PREFIX+"propdef";

	/** The Constant METADATA_TYPE_TYPEBASE_DEFINITION_TABLE. */
	public static final String METADATA_TYPE_TYPEBASE_DEFINITION_TABLE = METADATA_TYPE_TABLE_PREFIX+"typebase";

	/** The Constant METADATA_TYPE_TYPE_DOCUMENT_DEFINITION_TABLE. */
	public static final String METADATA_TYPE_TYPE_DOCUMENT_DEFINITION_TABLE = METADATA_TYPE_TABLE_PREFIX+"typedocument";

	/** The Constant TYPE_TO_SECONDARY_TYPE_TABLE. */
	public static final String METADATA_TYPE_TYPE_TO_SECONDARY_TYPE_TABLE = METADATA_TYPE_TABLE_PREFIX+"type_to_secondary_type";

	/** The Constant METADATA_TYPE_TYPE_TO_PROPERTY_TYPE_TABLE. */
	public static final String METADATA_TYPE_TYPE_TO_PROPERTY_TYPE_TABLE = METADATA_TYPE_TABLE_PREFIX+"type_to_prop_def";

	// type mapping
	
	/** The Constant METADATA_PRECISION_FLOAT. */
	public static final String METADATA_PRECISION_FLOAT = "32";

	/** The Constant METADATA_CONTENT_STRING_MAX_LENGTH. */
	public static final String METADATA_CONTENT_STRING_MAX_LENGTH = "3072";

	/** The Constant METADATA_CONTENT_URI_MAX_LENGTH. */
	public static final String METADATA_CONTENT_URI_MAX_LENGTH = "256";

	/** The Constant METADATA_CONTENT_ID_MAX_LENGTH. */
	public static final String METADATA_CONTENT_ID_MAX_LENGTH = "40";

	
}
