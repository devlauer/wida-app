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
package de.elnarion.web.wida.common;

import java.math.BigInteger;

/**
 * The Interface WidaErrorConstants.
 */
public interface WidaErrorConstants {

	// not supported

	/** The Constant NOT_SUPPORTED_CONTROLLABLE_ACL. */
	public final static BigInteger NOT_SUPPORTED_CONTROLLABLE_ACL = new BigInteger("1000");

	/** The Constant NOT_SUPPORTED_CONTROLLABLE_POLICY. */
	public final static BigInteger NOT_SUPPORTED_CONTROLLABLE_POLICY = new BigInteger("1001");

	/** The Constant NOT_SUPPORTED_NON_FILEABLE_DOCUMENT_TYPE. */
	public final static BigInteger NOT_SUPPORTED_NON_FILEABLE_DOCUMENT_TYPE = new BigInteger("1002");

	/** The Constant NOT_SUPPORTED_FULLTEXT_INDEX. */
	public final static BigInteger NOT_SUPPORTED_FULLTEXT_INDEX = new BigInteger("1003");

	/** The Constant NOT_SUPPORTED_VERSIONABLE. */
	public final static BigInteger NOT_SUPPORTED_VERSIONABLE = new BigInteger("1004");

	/** The Constant NOT_SUPPORTED_NON_FILEABLE_FOLDER_TYPE. */
	public final static BigInteger NOT_SUPPORTED_NON_FILEABLE_FOLDER_TYPE = new BigInteger("1005");

	/** The Constant NOT_SUPPORTED_FILEABLE_SECONDARY_TYPE. */
	public final static BigInteger NOT_SUPPORTED_FILEABLE_SECONDARY_TYPE = new BigInteger("1006");

	/** The Constant NOT_SUPPORTED_VERSIONING. */
	public final static BigInteger NOT_SUPPORTED_VERSIONING = new BigInteger("1007");

	/** The Constant NOT_SUPPORTED_RELATIONSHIPS. */
	public static final BigInteger NOT_SUPPORTED_RELATIONSHIPS = new BigInteger("1008");

	/** The Constant NOT_SUPPORTED_POLICIES. */
	public static final BigInteger NOT_SUPPORTED_POLICIES = new BigInteger("1009");

	/** The Constant NOT_SUPPORTED_ITEMS. */
	public static final BigInteger NOT_SUPPORTED_ITEMS = new BigInteger("1010");

	/** The Constant NOT_SUPPORTED_RENDITIONS. */
	public static final BigInteger NOT_SUPPORTED_RENDITIONS = new BigInteger("1011");

	/** The Constant NOT_SUPPORTED_BULK_UPDATES. */
	public static final BigInteger NOT_SUPPORTED_BULK_UPDATES = new BigInteger("1012");

	/** The Constant NOT_SUPPORTED_ADD_CONTENT_STREAM_AFTER_DOCUMENT_CREATION. */
	public static final BigInteger NOT_SUPPORTED_ADD_CONTENT_STREAM_AFTER_DOCUMENT_CREATION = new BigInteger("1013");

	/** The Constant NOT_SUPPORTED_APPEND_CONTENT_STREAM_AFTER_DOCUMENT_CREATION. */
	public static final BigInteger NOT_SUPPORTED_APPEND_CONTENT_STREAM_AFTER_DOCUMENT_CREATION = new BigInteger("1014");

	/** The Constant NOT_SUPPORTED_DELETE_CONTENT_STREAM. */
	public static final BigInteger NOT_SUPPORTED_DELETE_CONTENT_STREAM = new BigInteger("1015");

	/** The Constant NOT_SUPPORTED_GET_CONTENT_CHANGES. */
	public static final BigInteger NOT_SUPPORTED_GET_CONTENT_CHANGES = new BigInteger("1016");	
	
	/** The Constant NOT_SUPPORTED_TYPE_REMOVAL. */
	public static final BigInteger NOT_SUPPORTED_TYPE_REMOVAL = new BigInteger("1017");
	
	/** The Constant NOT_SUPPORTED_UPDATING_NON_EXISTANT_TYPE. */
	public static final BigInteger NOT_SUPPORTED_UPDATING_NON_EXISTANT_TYPE = new BigInteger("1018");

	/** The Constant NOT_SUPPORTED_CREATING_EXISTANT_TYPE. */
	public static final BigInteger NOT_SUPPORTED_CREATING_EXISTANT_TYPE = new BigInteger("1019");

	
	// type errors

	/** The Constant TYPE_DEFINITION_ALREADY_EXISTS. */
	public final static BigInteger TYPE_DEFINITION_ALREADY_EXISTS = new BigInteger("2000");

	/** The Constant TYPE_PARENT_DOES_NOT_EXIST. */
	public final static BigInteger TYPE_PARENT_DOES_NOT_EXIST = new BigInteger("2001");

	/** The Constant TYPE_ATTRIBUTE_ID_NULL_NOT_ALLOWED. */
	public final static BigInteger TYPE_ATTRIBUTE_ID_NULL_NOT_ALLOWED = new BigInteger("2002");

	/** The Constant TYPE_ATTRIBUTE_CONTENT_STREAM_OPTIONAL_NOT_ALLOWED. */
	public final static BigInteger TYPE_ATTRIBUTE_CONTENT_STREAM_OPTIONAL_NOT_ALLOWED = new BigInteger("2003");

	/** The Constant TYPE_ATTRIBUTE_BASE_TYPE_MODIFICATION_NOT_ALLOWED. */
	public final static BigInteger TYPE_ATTRIBUTE_BASE_TYPE_MODIFICATION_NOT_ALLOWED = new BigInteger("2004");

	/** The Constant TYPE_CREATION_NOT_ALLOWED_FOR_THIS_PARENT. */
	public final static BigInteger TYPE_CREATION_NOT_ALLOWED_FOR_THIS_PARENT = new BigInteger("2005");

	/** The Constant TYPE_NOT_CHILD_OF_ALLOWED_BASIC_TYPES. */
	public final static BigInteger TYPE_NOT_CHILD_OF_ALLOWED_BASIC_TYPES = new BigInteger("2006");

	/** The Constant TYPE_EMPTY_NOT_ALLOWED. */
	public final static BigInteger TYPE_EMPTY_NOT_ALLOWED = new BigInteger("2007");

	/** The Constant TYPE_DOES_NOT_EXIST. */
	public final static BigInteger TYPE_DOES_NOT_EXIST = new BigInteger("2008");

	/** The Constant TYPE_IS_NOT_ALLOWED. */
	public final static BigInteger TYPE_IS_NOT_ALLOWED = new BigInteger("2009");

	/** The Constant TYPE_BASE_CANNOT_BE_REMOVED. */
	public final static BigInteger TYPE_BASE_CANNOT_BE_REMOVED = new BigInteger("2010");

	/** The Constant TYPE_PARENT_EMPTY_NOT_ALLOWED. */
	public static final BigInteger TYPE_PARENT_EMPTY_NOT_ALLOWED = new BigInteger("2011");
	
	
	// property errors

	/** The Constant PROPERTY_ALREADY_DEFINED. */
	public final static BigInteger PROPERTY_ALREADY_DEFINED = new BigInteger("3000");

	/** The Constant PROPERTY_EMPTY_DEFINITION_NOT_ALLOWED. */
	public final static BigInteger PROPERTY_EMPTY_DEFINITION_NOT_ALLOWED = new BigInteger("3001");

	/** The Constant PROPERTY_UNKOWN_DEFINITION_TYPE. */
	public final static BigInteger PROPERTY_UNKOWN_DEFINITION_TYPE = new BigInteger("3002");

	/** The Constant PROPERTY_TYPE_SWITCH_NOT_ALLOWED. */
	public final static BigInteger PROPERTY_TYPE_SWITCH_NOT_ALLOWED = new BigInteger("3003");

	/** The Constant PROPERTY_CARDINALITY_SWITCH_NOT_ALLOWED. */
	public final static BigInteger PROPERTY_CARDINALITY_SWITCH_NOT_ALLOWED = new BigInteger("3004");

	/** The Constant PROPERTY_CHOICES_NOT_ALLOWED. */
	public final static BigInteger PROPERTY_CHOICES_NOT_ALLOWED = new BigInteger("3005");

	/** The Constant PROPERTY_SWITCH_REQUIRED_NOT_ALLOWED. */
	public final static BigInteger PROPERTY_SWITCH_REQUIRED_NOT_ALLOWED = new BigInteger("3006");

	/** The Constant PROPERTY_UPDATABILITY_WHENCHECKEDOUT_NOT_ALLOWED. */
	public final static BigInteger PROPERTY_UPDATABILITY_WHENCHECKEDOUT_NOT_ALLOWED = new BigInteger("3007");

	/** The Constant PROPERTY_DEFINITION_TYPE_REMOVAL_NOT_ALLOWED. */
	public final static BigInteger PROPERTY_DEFINITION_TYPE_REMOVAL_NOT_ALLOWED = new BigInteger("3008");

	// invalid arguments

	/** The Constant INVALID_ARGUMENT_ORDER_BY. */
	public static final BigInteger INVALID_ARGUMENT_ORDER_BY = new BigInteger("4000");

	/** The Constant INVALID_ARGUMENT_EMPTY. */
	public static final BigInteger INVALID_ARGUMENT_EMPTY = new BigInteger("4001");

	/** The Constant INVALID_ARGUMENT_MISSING_FILENAME_FOR_CONTENT_STREAM. */
	public static final BigInteger INVALID_ARGUMENT_MISSING_FILENAME_FOR_CONTENT_STREAM = new BigInteger("4002");

	/** The Constant INVALID_ARGUMENT_MISSING_MIMETYPE_FOR_CONTENT_STREAM. */
	public static final BigInteger INVALID_ARGUMENT_MISSING_MIMETYPE_FOR_CONTENT_STREAM = new BigInteger("4003");


	// constraint exception codes
	
	/** The Constant INVALID_ARGUMENT_ID_TOO_BIG. */
	public static final BigInteger CONSTRAINT_NAME_TOO_BIG = new BigInteger("5000");

	/** The Constant CONSTRAINT_UPDATE_IMMUTABLE_TYPE. */
	public static final BigInteger CONSTRAINT_UPDATE_IMMUTABLE_TYPE = new BigInteger("5001");

	/** The Constant CONSTRAINT_UPDATE_TYPE_WITH_ARGUMENT_EMPTY. */
	public static final BigInteger CONSTRAINT_UPDATE_TYPE_WITH_ARGUMENT_EMPTY = new BigInteger("5002");
	
	/** The Constant CONSTRAINT_CREATE_TYPE_WITH_ARGUMENT_EMPTY. */
	public static final BigInteger CONSTRAINT_CREATE_TYPE_WITH_ARGUMENT_EMPTY = new BigInteger("5003");

	/** The Constant CONSTRAINT_CREATE_CHILD_OF_IMMUTABLE_TYPE. */
	public static final BigInteger CONSTRAINT_CREATE_CHILD_OF_IMMUTABLE_TYPE = new BigInteger("5004");

	/** The Constant CONSTRAINT_OBJECT_TYPE_UNKOWN. */
	public static final BigInteger CONSTRAINT_OBJECT_TYPE_UNKOWN = new BigInteger("5005");

	/** The Constant CONSTRAINT_INVALID_TYPE_ID. */
	public static final BigInteger CONSTRAINT_INVALID_TYPE_ID = new BigInteger("5006");

	
	// runtime exception codes
	
	/** The Constant RUNTIME_EXCEPTION_CONTENT_TABLE_MISSING. */
	public static final BigInteger RUNTIME_EXCEPTION_CONTENT_TABLE_MISSING = new BigInteger("6000");

	/** The Constant RUNTIME_EXCEPTION_WRONG_TYPE_FOR_UPDATE_USED. */
	public static final BigInteger RUNTIME_EXCEPTION_WRONG_TYPE_FOR_UPDATE_USED = new BigInteger("6001");

	/** The Constant RUNTIME_EXCEPTION_STORAGE_WRITE_EXCEPTION. */
	public static final BigInteger RUNTIME_EXCEPTION_STORAGE_WRITE_EXCEPTION = new BigInteger("6002");
	
	
	// not found exception codes
	
	/** The Constant NOT_FOUND_OBJECT. */
	public static final BigInteger NOT_FOUND_OBJECT = new BigInteger("7000");

	


}
