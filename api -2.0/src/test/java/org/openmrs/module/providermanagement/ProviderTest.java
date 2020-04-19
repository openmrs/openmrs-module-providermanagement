package org.openmrs.module.providermanagement;
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

import junit.framework.Assert;
import org.hibernate.ObjectNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

/**
 * Tests that the new Provider defined in the module works properly with the existing provider service
 */
public class ProviderTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/providermanagement/include/";

    protected static final String XML_DATASET = "providerManagement-dataset.xml";

    private ProviderManagementService providerManagementService;

    @Before
    public void init() throws Exception {
        // execute the provider management test dataset
        executeDataSet(XML_DATASET_PATH + XML_DATASET);

        // initialize the service
        providerManagementService = Context.getService(ProviderManagementService.class);
    }

    @Test
    public void shouldFetchProviderWithProperRole() {
        // first just test to make sure we can fetch a provider via the provider management service methods
        Person person = Context.getPersonService().getPerson(6);
        List<Provider> providers = providerManagementService.getProvidersByPerson(person, true);
        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(1004), providers.get(0).getId());
        Assert.assertEquals(new Integer(1001), providers.get(0).getProviderRole().getId());
    }

    @Test
    public void shouldSaveProviderWithRole() {
        // create a new Provider (using the Provider object provided by the module)
        Person person = Context.getPersonService().getPerson(6);
        Provider provider = new Provider();
        provider.setPerson(person);
        provider.setIdentifier("new provider");
        provider.setProviderRole(providerManagementService.getProviderRole(1001));

        // now save the provider using the existing Provider Service
        Context.getProviderService().saveProvider(provider);
        Context.flushSession();

        // confirm that the provider has been saved with the appropriate role
        List<Provider> providers = providerManagementService.getProvidersByPerson(person, true);
        Assert.assertEquals(2, providers.size());

        for (Provider p : providers) {
            if (p.getIdentifier().equals("new provider")) {
                Assert.assertEquals(new Integer(1001), p.getProviderRole().getId());
            }
        }

    }

    @Test(expected = ObjectNotFoundException.class)
    public void shouldPurgeProvider() {
        org.openmrs.Provider provider = Context.getProviderService().getProvider(1004);
        Context.getProviderService().purgeProvider(provider);
        Context.flushSession();
        Assert.assertNull(Context.getProviderService().getProvider(1004));
    }

    @Test
    public void shouldUpdateProviderWithoutLosingRole() {
        // load this provider using the core Provider Service
        org.openmrs.Provider provider = Context.getProviderService().getProvider(1004);

        // change a value on the provider and resave using the core provider service
        provider.setIdentifier("my id");
        Context.getProviderService().saveProvider(provider);
        Context.flushSession();

        // now make sure when we reload the provider using the new Provider object, the role still exists
        Person person = Context.getPersonService().getPerson(6);
        List<Provider> providers = providerManagementService.getProvidersByPerson(person, true);
        Assert.assertEquals(new Integer(1004), providers.get(0).getId());
        Assert.assertEquals(new Integer(1001), providers.get(0).getProviderRole().getId());
        Assert.assertEquals("my id", providers.get(0).getIdentifier());
    }

}
