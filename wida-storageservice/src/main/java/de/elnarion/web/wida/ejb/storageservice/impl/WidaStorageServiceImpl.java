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
package de.elnarion.web.wida.ejb.storageservice.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.resource.ResourceException;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.xadisk.additional.XAFileOutputStreamWrapper;
import org.xadisk.bridge.proxies.interfaces.XAFileOutputStream;
import org.xadisk.connector.outbound.XADiskConnection;
import org.xadisk.connector.outbound.XADiskConnectionFactory;
import org.xadisk.filesystem.exceptions.FileAlreadyExistsException;
import org.xadisk.filesystem.exceptions.FileNotExistsException;
import org.xadisk.filesystem.exceptions.FileUnderUseException;
import org.xadisk.filesystem.exceptions.InsufficientPermissionOnFileException;
import org.xadisk.filesystem.exceptions.LockingFailedException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

import de.elnarion.web.wida.ejb.storageservice.WidaContainerResourcesMissingException;
import de.elnarion.web.wida.ejb.storageservice.WidaFileWriteException;
import de.elnarion.web.wida.ejb.storageservice.WidaStorageService;
import de.elnarion.web.wida.ejb.storageservice.WidaStorageServiceException;

/**
 * The Class WidaStorageServiceImpl.
 */
@Stateless
public class WidaStorageServiceImpl implements WidaStorageService {

	/** The xa connection factory. */
	@Resource(lookup = "java:/XADiskCF", type = org.xadisk.connector.outbound.XADiskConnectionFactory.class)
	private XADiskConnectionFactory xaConnectionFactory;

	@Resource(name = "wida.rootPath")
	String rootPath;

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");

	private final static Logger LOGGER = Logger.getLogger(WidaStorageServiceImpl.class);
	/**
	 * Instantiates a new wida storage service impl.
	 */
	public WidaStorageServiceImpl() {
	}

	@Override
	@Transactional
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String saveFile(InputStream stream,BigInteger size) throws WidaStorageServiceException {
		XADiskConnection diskConnection;
		String fileName="";
		String createdPath="";
		String storageId = "";
		try {
			diskConnection = xaConnectionFactory.getConnection();
			fileName = UUID.randomUUID().toString() + ".bin";
			if (rootPath == null)
				rootPath = ".";
			String folderStructure = dateFormat.format(new Date());
			String[] folders = folderStructure.split("-");
			// TODO directory creation is part of the transaction and directory gets locked!
			// so implement another solution
			String currentPath = rootPath;
			for (String folder : folders) {
				currentPath += File.pathSeparator + folder;
				storageId+= File.pathSeparator + folder;
				File currentDirectory = new File(currentPath);
				if (!diskConnection.fileExists(currentDirectory))
					diskConnection.createFile(currentDirectory, true);
			}
			createdPath = currentPath;
			currentPath += File.separator + fileName;
			File saveFile = new File(currentPath);
			boolean heavyWrite = false;
			if(size!=null&&size.longValue()>1024)
				heavyWrite= true;
			XAFileOutputStream fileOutputStream=null;
			try {
				 fileOutputStream= diskConnection.createXAFileOutputStream(saveFile, heavyWrite);
			} catch (FileUnderUseException e) {
				fileName = UUID.randomUUID().toString() + ".bin";
				currentPath += File.separator + fileName;
				saveFile = new File(currentPath);
				fileOutputStream= diskConnection.createXAFileOutputStream(saveFile, heavyWrite);
			}
			XAFileOutputStreamWrapper streamWrapper = new XAFileOutputStreamWrapper(fileOutputStream);
			IOUtils.copy(stream, streamWrapper);
			// filepath without root folder path
			storageId+= File.pathSeparator + fileName;
			return storageId;
		} catch (NoTransactionAssociatedException|ResourceException e) {
			String message = "Could not find resource or transaction for file creation";
			LOGGER.error(message, e);
			throw new WidaContainerResourcesMissingException(message,e);
		} catch (IOException|InterruptedException | LockingFailedException 
				| InsufficientPermissionOnFileException | FileNotExistsException e) {
			String message = "File creation failed because of io,interrupted, locking, insufficient permission or missing file. Please check os or filesystem";
			LOGGER.warn(message,e);
			throw new WidaFileWriteException(message,e);
		} catch (FileUnderUseException|FileAlreadyExistsException e) {
			String message = "Duplicate file found with filename "+fileName+" and path "+createdPath+" found. This should not happen because of generating filename twice with UUID!";
			throw new WidaFileWriteException(message,e);
		} 
	}

}
