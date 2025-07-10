package org.openmrs.module.providermanagement.rest;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ExtendedProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.exception.ProviderRoleInUseException;
import org.openmrs.module.providermanagement.rest.controller.ProviderManagementRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + ProviderManagementRestController.PROVIDER_MANAGEMENT_REST_NAMESPACE + "/providerrole", supportedClass = ExtendedProviderRole.class,
        supportedOpenmrsVersions = {"1.9.* - 9.*"})
public class ProviderRoleResource extends MetadataDelegatingCrudResource<ExtendedProviderRole> {

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display", findMethod("getDisplayString"));
            description.addProperty("name");
            description.addProperty("description");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display", findMethod("getDisplayString"));
            description.addProperty("name");
            description.addProperty("description");
            // TODO: add superviseeProviderRoles, relationshipTypes, and providerRoleAttributes
            description.addProperty("auditInfo", findMethod("getAuditInfo"));
            description.addSelfLink();
            return description;
        }
        return null;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addRequiredProperty("name");
        description.addRequiredProperty("description");
        return description;
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() {
        return getCreatableProperties();
    }

    @Override
    public ExtendedProviderRole getByUniqueId(String uuid) {
        return Context.getService(ProviderManagementService.class).getProviderRoleByUuid(uuid);
    }

    @Override
    public ExtendedProviderRole newDelegate() {
        return new ExtendedProviderRole();
    }

    @Override
    public ExtendedProviderRole save(ExtendedProviderRole providerRole) {
        if (providerRole != null) {
            return Context.getService(ProviderManagementService.class).saveProviderRole(providerRole);
        }
        return null;
    }

    @Override
    public void purge(ExtendedProviderRole providerRole, RequestContext requestContext) throws ResponseException {
        if (providerRole != null) {
            try {
                Context.getService(ProviderManagementService.class).purgeProviderRole(providerRole);
            } catch (ProviderRoleInUseException e) {
                throw new APIException(e);
            }
        }
    }

    @Override
    protected NeedsPaging<ExtendedProviderRole> doGetAll(RequestContext context) throws ResponseException {
        return new NeedsPaging<ExtendedProviderRole>(Context.getService(ProviderManagementService.class).getAllProviderRoles(context.getIncludeAll()), context);
    }


    @Override
    public String getResourceVersion() {
        return "1.9";
    }
}
