package org.openmrs.module.providermanagement.rest.search;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ProviderSearchHandler implements SearchHandler {

    protected final String PROVIDER_ROLES_PARAM = "providerRoles";

    private final SearchConfig searchConfig = new SearchConfig("providerByRole", RestConstants.VERSION_1 + "/provider",
            Collections.singletonList("1.9.* - 9.*"),
            new SearchQuery.Builder(
                    "Allows you to find providers by provider role uuid").withRequiredParameters(PROVIDER_ROLES_PARAM).build());

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSearchConfig()
     */
    @Override
    public SearchConfig getSearchConfig() {
        return searchConfig;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#search(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public PageableResult search(RequestContext context) throws ResponseException {
  
        String[] providerRoleUuidArray = context.getRequest().getParameterValues(PROVIDER_ROLES_PARAM);
        List<ProviderRole> providerRoles = new ArrayList<ProviderRole>();

        // supports both providerRoles=uuid1,uuid2 and providerRoles=uuid1&providerRoles=uuid2
        for (String providerRoleUuidString : providerRoleUuidArray) {
            for (String providerRoleUuid : providerRoleUuidString.split(",")) {
                ProviderRole providerRole = Context.getService(ProviderManagementService.class).getProviderRoleByUuid(providerRoleUuid);
                if (providerRole != null) {
                    providerRoles.add(providerRole);
                } else {
                    throw new APIException("Unable to find provider role with uuid: " + providerRoleUuid);
                }
            }
        }

        List<Provider> providers = Context.getService(ProviderManagementService.class).getProvidersByRoles(providerRoles);
        return new NeedsPaging<Provider>(providers, context);
    }
}
