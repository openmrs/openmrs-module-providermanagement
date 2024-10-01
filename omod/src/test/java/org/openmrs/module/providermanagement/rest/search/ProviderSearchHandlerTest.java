package org.openmrs.module.providermanagement.rest.search;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Provider;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class ProviderSearchHandlerTest extends MainResourceControllerTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/providermanagement/include/";

    protected static final String XML_DATASET = "providerManagement-dataset.xml";

    @Before
    public void init() throws Exception {
        executeDataSet(XML_DATASET_PATH + XML_DATASET);
    }

    @Override
    public String getURI() {
        return "provider";
    }

    @Override
    public String getUuid() {
        return "da7f523f-cca9-11e0-9572-0800200c9a66"; // from providerManagement-dataset.xml
    }

    @Override
    public long getAllCount() {
        return 9; // one from standard test dataset, 8 from providerManagement-dataset.xml
    }

    @Test
    public void shouldReturnProvidersBySingleRole() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("providerRoles", "da7f523f-27ce-4bb2-86d6-6d1d05312bd5"); //binome role
        SimpleObject result = deserialize(handle(req));
        List<Provider> providers = (List<Provider>) result.get("results");
        Assert.assertEquals(3, providers.size());
    }

    @Test
    public void shouldReturnProvidersByMultipleRoles() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("providerRoles", "da7f523f-27ce-4bb2-86d6-6d1d05312bd5,ea7f523f-27ce-4bb2-86d6-6d1d05312bd5"); //binome and binome superviser roles
        SimpleObject result = deserialize(handle(req));
        List<Provider> providers = (List<Provider>) result.get("results");
        Assert.assertEquals(4, providers.size());
    }

    @Test
    public void shouldReturnEmptyListIfInvalidRole() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("providerRoles", "bogus");
        SimpleObject result = deserialize(handle(req));
        List<Provider> providers = (List<Provider>) result.get("results");
        Assert.assertEquals(0, providers.size());
    }

}
