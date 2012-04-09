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

import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.sync.SyncBaseTest;
import org.openmrs.module.sync.SyncTestHelper;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests that the new OpenMRS objects added by the module sync properly
 */
public class SyncTest extends SyncBaseTest {

    @Override
    public String getInitialDataset() {
        return "org/openmrs/module/sync/include/SyncCreateTest.xml";
    }

    @Test
    public void shouldUpdateEncounterType() throws Exception {
        runSyncTest(new SyncTestHelper() {
            AdministrationService adminService = Context.getAdministrationService();
            EncounterService encounterService = Context.getEncounterService();
            public void runOnChild() {

                EncounterType encounterType = new EncounterType();
                encounterType.setName("name");
                encounterType.setDescription("description");
                adminService.createEncounterType(encounterType);

                EncounterType updateEncounterType = encounterService.getEncounterType("name");
                encounterType.setName("new name");
                adminService.updateEncounterType(updateEncounterType);
            }
            public void runOnParent() {
                EncounterType encounterType = encounterService.getEncounterType("name");
                assertNull(encounterType);

                encounterType = encounterService.getEncounterType("new name");
                assertNotNull(encounterType);
            }
        });
    }

}
