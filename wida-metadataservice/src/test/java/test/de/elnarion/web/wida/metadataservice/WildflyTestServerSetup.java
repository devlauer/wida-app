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

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.ClientConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;

import de.elnarion.web.wida.metadataservice.WidaTypeMetaDataService;
import de.elnarion.web.wida.metadataservice.domain.contentmetadata.BaseItem;
import de.elnarion.web.wida.metadataservice.domain.typemetadata.TypeBase;
import de.elnarion.web.wida.metadataservice.impl.WidaTypeMetaDataServiceImpl;

/**
 * The Class WidaTypeServiceServerSetup is a base class for all integration
 * tests in this service.
 */
@ServerSetup(WildflyTestServerSetup.ServerSetup.class)
public class WildflyTestServerSetup {

	/**
	 * The Class ServerSetup which can be used to send commands to the Wildfly
	 * application server
	 */
	public static class ServerSetup implements ServerSetupTask {

		@Override
		public void setup(ManagementClient managementClient, String containerId) throws Exception {

			ModelControllerClient modelControllerClient = managementClient.getControllerClient();

			ModelNode model = new ModelNode();
			model.get(ClientConstants.OP).set("read-attribute");
			model.get(ClientConstants.OP_ADDR).add("subsystem", "datasources");
			model.get(ClientConstants.OP_ADDR).add("data-source", "wida_xads");
			model.get("name").set("max-pool-size");
			ModelNode result = modelControllerClient.execute(model);
			// if datasource is already initialized return
			if (result != null && !(result.get("outcome").asString().equals("failed")
					&& result.get("failure-description").asString().contains("not found"))) {
				return;
			} else {
				// if not create datasource
				final ModelNode address = new ModelNode();
				address.add("subsystem", "datasources");
				address.add("data-source", "wida_xads");
				address.protect();

				final ModelNode operation = new ModelNode();
				operation.get(ClientConstants.OP).set("add");
				operation.get(ClientConstants.OP_ADDR).set(address);

				operation.get("name").set("MyNewDs");
				operation.get("jndi-name").set("java:jboss/jdbc/wida_xads");
				operation.get("enabled").set(true);

				operation.get("driver-name").set("h2");
				operation.get("pool-name").set("wida_ds_pool");

				operation.get("connection-url").set("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
				operation.get("user-name").set("sa");
				operation.get("password").set("sa");
				modelControllerClient.execute(operation);
			}
		}

		@Override
		public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
			// let the datasource stay in place, it is only a test server for this service
			// and the datasource can be reused for the next test run
		}
	}

	/**
	 * Creates the deployment archive for this embedded integration test.
	 *
	 * @return the java archive
	 */
	@Deployment
	public static Archive<?> createDeployment() {

		EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class);

		File[] libraries = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeAndTestDependencies().resolve()
				.withTransitivity().asFile();

		File[] providedLibraries = Maven.resolver().loadPomFromFile("pom.xml").importDependencies(ScopeType.PROVIDED)
				.resolve().withTransitivity().asFile();

		JavaArchive ejb = ShrinkWrap.create(JavaArchive.class)
				.addPackage(WidaTypeMetaDataServiceImpl.class.getPackage())
				.addPackage(WidaTypeMetaDataService.class.getPackage())
				.addPackage(TypeBase.class.getPackage())
				.addPackage(BaseItem.class.getPackage())
				.addPackage(WildflyTestServerSetup.class.getPackage())
				.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
				.addAsResource("META-INF/ejb-jar.xml").addAsResource("META-INF/jboss-ejb3.xml");

		ear.addAsLibraries(libraries);
		// the libraries wida-common and cmis-support are in the scope provided due to
		// classloader settings in ear file, but we need to add them here too, only the
		// spec libraries should be filtered
		for (File providedFile : providedLibraries)
			if (!providedFile.getName().contains("jee"))
				ear.addAsLibrary(providedFile);
		ear.addAsModule(ejb);
		return ear;
	}

}
