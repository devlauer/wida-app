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

/**
 * The Interface WidaMetaDataConstants.
 */
public interface WidaMetaDataConstants {

	// global
	
	/** The Constant METADATA_TYPE_PK_PREFIX. */
	public static final String METADATA_PK_PREFIX = "PK_";

	/** The Constant METADATA_CONTENT_INDEX_PREFIX. */
	public final static String METADATA_INDEX_PREFIX = "X_";

	/** The Constant METADATA_FOREIGN_KEY_PREFIX. */
	public static final String METADATA_FOREIGN_KEY_PREFIX = "FK_";
	
	/** The Constant MAX_NAME_LENGTH. */
	public static final int MAX_NAME_LENGTH =30;

	/** The Constant METADATA_ROOTFOLDER_OBJECT_ID. */
	public static final String METADATA_ROOTFOLDER_OBJECT_ID = "fb7aa0d1-4b52-47c5-9a90-ab81c3f4a5ef";
	
	/** The Constant METADATA_DEFAULT_NAMESPACE. */
	public static final String METADATA_DEFAULT_NAMESPACE = "https://www.github.com/devlauer/wida-app";

	
	// content metadata specific 

	/** The Constant METADATA_ID_COLUMN_NAME. */
	public final static String METADATA_ID_COLUMN_NAME = "ID";
	
	/** The Constant CONTENT_METADATA_TABLE_PREFIX. */
	public final static String METADATA_CONTENT_TABLE_PREFIX = "CM_";
	
	/** The Constant METADATA_CONTENT_ITEM_TABLE. */
	public static final String METADATA_CONTENT_ITEM_TABLE = METADATA_CONTENT_TABLE_PREFIX+"ITEM";
	
	/** The Constant METADATA_CONTENT_DOCUMENT_TABLE. */
	public static final String METADATA_CONTENT_DOCUMENT_TABLE = METADATA_CONTENT_TABLE_PREFIX+"DOCUMENT";
	
	// type metadata specific
	
	/** The Constant METADATA_TYPE_TABLE_PREFIX. */
	public static final String METADATA_TYPE_TABLE_PREFIX = "TY_";
	
	/** The Constant METADATA_TYPE_PROPERTYDEFINITION_TABLE. */
	public static final String METADATA_TYPE_PROPERTYDEFINITION_TABLE = METADATA_TYPE_TABLE_PREFIX+"PROPDEF";

	/** The Constant METADATA_TYPE_TYPEBASE_DEFINITION_TABLE. */
	public static final String METADATA_TYPE_TYPEBASE_DEFINITION_TABLE = METADATA_TYPE_TABLE_PREFIX+"TYPEBASE";

	/** The Constant METADATA_TYPE_VERSION_LOCK_TABLE. */
	public static final String METADATA_TYPE_VERSION_LOCK_TABLE = METADATA_TYPE_TABLE_PREFIX+"VERSIONLOCK";
	
	/** The Constant METADATA_TYPE_TYPE_DOCUMENT_DEFINITION_TABLE. */
	public static final String METADATA_TYPE_TYPE_DOCUMENT_DEFINITION_TABLE = METADATA_TYPE_TABLE_PREFIX+"TYPEDOCUMENT";

	/** The Constant TYPE_TO_SECONDARY_TYPE_TABLE. */
	public static final String METADATA_TYPE_TYPE_TO_SECONDARY_TYPE_TABLE = METADATA_TYPE_TABLE_PREFIX+"TYPE_TO_SECONDARY_TYPE";

	/** The Constant METADATA_TYPE_TYPE_TO_PROPERTY_TYPE_TABLE. */
	public static final String METADATA_TYPE_TYPE_TO_PROPERTY_TYPE_TABLE = METADATA_TYPE_TABLE_PREFIX+"TYPE_TO_PROP_DEF";
	
	/** The Constant METADATA_VERSION_LOCK_KEY. */
	public static final String METADATA_VERSION_LOCK_KEY = "TYPE_VERSION_LOCK";

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
