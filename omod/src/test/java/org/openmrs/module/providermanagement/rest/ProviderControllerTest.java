package org.openmrs.module.providermanagement.rest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProviderControllerTest  extends MainResourceControllerTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/providermanagement/include/";

    protected static final String XML_DATASET = "providerManagement-dataset.xml";

    @Before
    public void init() throws Exception {
        executeDataSet(XML_DATASET_PATH + XML_DATASET);
    }

    @Test
    public void createProvider_shouldCreateANewProvider() throws Exception {
        int before = Context.getProviderService().getAllProviders().size();
        String json = "{ \"person\": \"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\", \"identifier\":\"abc123ez\" }";

        handle(newPostRequest(getURI(), json));
        Assert.assertEquals(before + 1, Context.getProviderService().getAllProviders().size());
    }

    @Test
    public void createProvider_shouldCreateANewProviderWithAttributes() throws Exception {
        int before = Context.getProviderService().getAllProviders().size();
        String json = "{ \"person\": \"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\"," + "\"identifier\":\"abc123ez\","
                + "\"attributes\":[{\"attributeType\":\"" + "9616cc50-6f9f-11e0-8414-001e378eb67e"
                + "\",\"value\":\"212\"}]}";

        MockHttpServletResponse res = handle(newPostRequest(getURI(), json));
        Assert.assertEquals(before + 1, Context.getProviderService().getAllProviders().size());

        SimpleObject obj = SimpleObject.parseJson(res.getContentAsString());
        Provider provider = (Provider) Context.getProviderService().getProviderByUuid(obj.get("uuid").toString());
        Assert.assertEquals(1, provider.getAttributes().size());
    }

    @Test
    public void createProvider_shouldCreateANewProviderWithProviderRole() throws Exception {
        int before = Context.getProviderService().getAllProviders().size();
        String json = "{ \"person\": \"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\", " +
                "\"identifier\":\"123\", " +
                "\"providerRole\":\"da7f523f-27ce-4bb2-86d6-6d1d05312bd5\" }";

        MockHttpServletResponse res = handle(newPostRequest(getURI(), json));
        Assert.assertEquals(before + 1, Context.getProviderService().getAllProviders().size());

        SimpleObject obj = SimpleObject.parseJson(res.getContentAsString());
        Provider provider = (Provider) Context.getProviderService().getProviderByUuid(obj.get("uuid").toString());
        Assert.assertEquals("da7f523f-27ce-4bb2-86d6-6d1d05312bd5", provider.getProviderRole().getUuid());
    }

    @Test(expected = ConversionException.class)
    public void updateProvider_shouldFailWhenChangingAPersonPropertyOnAProvider() throws Exception {
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String json = "{\"birthdate\":\"" + df.format(now) + "\"}";

        handle(newPostRequest(getURI() + "/" + getUuid(), json));
    }

    @Test
    public void voidProvider_shouldRetireAProvider() throws Exception {
        Provider pat = (Provider) Context.getProviderService().getProviderByUuid(getUuid());
        Assert.assertFalse(pat.isRetired());

        MockHttpServletRequest request = request(RequestMethod.DELETE, getURI() + "/" + getUuid() );
        request.addParameter("reason", "unit test");
        handle(request);

        pat = (Provider) Context.getProviderService().getProviderByUuid(getUuid());
        Assert.assertTrue(pat.isRetired());
        Assert.assertEquals("unit test", pat.getRetireReason());
    }

    @Test
    public void shouldEditAProvider() throws Exception {
        final String EDITED_PERSON_UUID = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";  // from standard test dataset
        Provider provider = (Provider)  Context.getProviderService().getProviderByUuid(getUuid());
        Assert.assertFalse(EDITED_PERSON_UUID.equals(provider.getPerson().getUuid()));

        String json = "{\"person\":\"" + EDITED_PERSON_UUID + "\"" + "}";
        handle(newPostRequest(getURI() + "/" + getUuid(), json));

        Provider updatedProvider = (Provider) Context.getProviderService().getProviderByUuid(getUuid());
        Assert.assertEquals(EDITED_PERSON_UUID, updatedProvider.getPerson().getUuid());
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        return "providermanagement/provider";
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        return "da7f523f-cca9-11e0-9572-0800200c9a66";
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
     */
    @Override
    public long getAllCount() {
        return Context.getProviderService().getAllProviders(false).size();
    }


}
