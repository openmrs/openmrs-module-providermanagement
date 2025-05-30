package org.openmrs.module.providermanagement.rest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

public class ProviderRoleControllerTest extends MainResourceControllerTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/providermanagement/include/";

    protected static final String XML_DATASET = "providerManagement-dataset.xml";

    @Before
    public void init() throws Exception {
        executeDataSet(XML_DATASET_PATH + XML_DATASET);
    }

    @Test
    public void createProviderRole_shouldCreateANewProviderRole() throws Exception {
        int before = Context.getService(ProviderManagementService.class).getAllProviderRoles(false).size();
        String json = "{ \"name\": \"Social Worker\", \"description\":\"Clinical Social Worker\" }";
        handle(newPostRequest(getURI(), json));
        Assert.assertEquals(before + 1, Context.getService(ProviderManagementService.class).getAllProviderRoles(false).size());
    }

    @Test
    public void voidProvider_shouldRetireAProvider() throws Exception {
        ProviderRole providerRole = Context.getService(ProviderManagementService.class).getProviderRoleByUuid(getUuid());
        Assert.assertFalse(providerRole.isRetired());

        MockHttpServletRequest request = request(RequestMethod.DELETE, getURI() + "/" + getUuid() );
        request.addParameter("reason", "unit test");
        handle(request);

        providerRole = Context.getService(ProviderManagementService.class).getProviderRoleByUuid(getUuid());
        Assert.assertTrue(providerRole.isRetired());
        Assert.assertEquals("unit test", providerRole.getRetireReason());
    }

    @Test
    public void shouldEditAProvider() throws Exception {
        String json = "{\"description\":\"new description\"}";
        handle(newPostRequest(getURI() + "/" + getUuid(), json));

        ProviderRole updatedProviderRole = (ProviderRole) Context.getService(ProviderManagementService.class).getProviderRoleByUuid(getUuid());
        Assert.assertEquals("new description", updatedProviderRole.getDescription());
    }

    @Override
    public String getURI() {
        return "providermanagement/providerrole";
    }

    @Override
    public String getUuid() {
        return "da7f523f-27ce-4bb2-86d6-6d1d05312bd5";
    }

    @Override
    public long getAllCount() {
        return Context.getService(ProviderManagementService.class).getAllProviderRoles(false).size();
    }
}
