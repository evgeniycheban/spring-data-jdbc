/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jdbc.testing;

import org.junit.AssumptionViolatedException;

import org.springframework.core.annotation.Order;
import org.springframework.core.env.Profiles;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import org.testcontainers.containers.Db2Container;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.LicenseAcceptance;

/**
 * {@link TestExecutionListener} to selectively skip tests if the license for a particular database container was not
 * accepted.
 * 
 * @author Mark Paluch
 */
@Order(Integer.MIN_VALUE)
public class LicenseListener implements TestExecutionListener {

	@Override
	public void prepareTestInstance(TestContext testContext) {

		StandardEnvironment environment = new StandardEnvironment();

		if (environment.acceptsProfiles(Profiles.of("db2"))) {
			assumeLicenseAccepted(Db2Container.DEFAULT_DB2_IMAGE_NAME + ":" + Db2Container.DEFAULT_TAG);
		}

		if (environment.acceptsProfiles(Profiles.of("mssql"))) {
			assumeLicenseAccepted(MSSQLServerContainer.IMAGE + ":" + MSSQLServerContainer.DEFAULT_TAG);
		}
	}

	private static void assumeLicenseAccepted(String imageName) {

		try {
			LicenseAcceptance.assertLicenseAccepted(imageName);
		} catch (IllegalStateException e) {
			throw new AssumptionViolatedException(e.getMessage());
		}
	}

}
