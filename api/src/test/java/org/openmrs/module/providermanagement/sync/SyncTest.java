/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.providermanagement.sync;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleUtil;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.sync.SyncBaseTest;
import org.openmrs.module.sync.SyncTestHelper;
import org.openmrs.test.StartModule;
import org.openmrs.test.StartModuleExecutionListener;
import org.springframework.test.annotation.NotTransactional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests that the new OpenMRS objects added by the module sync properly
 */
@StartModule(value = "sync-omod-1.1-SNAPSHOT.omod")
public class SyncTest extends SyncBaseTest {

    @Before
    public void setGlobalProperty()  {
        GlobalProperty gp = new GlobalProperty();

        gp.setProperty("sync.database_version");
        gp.setValue("11");

        Context.getAdministrationService().saveGlobalProperty(gp);

    }

    @Override
    public String getInitialDataset() {
        return "org/openmrs/module/providermanagement/include/providerManagement-dataset.xml";
    }

    @Test
    @NotTransactional            // needed for some reason
    public void shouldUpdateEncounterType() throws Exception {
        runSyncTest(new SyncTestHelper() {

            EncounterService encounterService = Context.getEncounterService();

            public void runOnChild() {
                assertNotNull(Context.getAuthenticatedUser());
                EncounterType encounterType = new EncounterType();
                encounterType.setName("name");
                encounterType.setDescription("description");
                encounterService.saveEncounterType(encounterType);
            }
            public void runOnParent() {
                assertNotNull(Context.getAuthenticatedUser());
                EncounterType encounterType = encounterService.getEncounterType("name");
                assertNotNull(encounterType);
            }


            //ProviderManagementService providerManagementService = Context.getService(ProviderManagementService.class);

            //public void runOnChild() {

               // ProviderRole role = providerManagementService.getProviderRole(1001);
                //role.setName("new name");
                //providerManagementService.saveProviderRole(role);

            //}
            //public void runOnParent() {
              //  ProviderRole role = providerManagementService.getProviderRole(1001);
             //   Assert.assertEquals("new name", role.getName());
            //}
        });
    }

}
