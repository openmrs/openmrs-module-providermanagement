package org.openmrs.module.providermanagement.rest.controller;

import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + ProviderManagementRestController.PROVIDER_MANAGEMENT_REST_NAMESPACE)
public class ProviderManagementRestController extends MainResourceController {

    public static final String PROVIDER_MANAGEMENT_REST_NAMESPACE = "/providermanagement";

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController#getNamespace()
     */
    @Override
    public String getNamespace() {
        return RestConstants.VERSION_1 + PROVIDER_MANAGEMENT_REST_NAMESPACE;
    }

}
