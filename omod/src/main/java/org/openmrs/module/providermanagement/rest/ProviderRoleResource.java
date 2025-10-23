package org.openmrs.module.providermanagement.rest;

import org.openmrs.ProviderRole;
import org.openmrs.api.context.Context;
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

@Resource(name = RestConstants.VERSION_1 + ProviderManagementRestController.PROVIDER_MANAGEMENT_REST_NAMESPACE + "/providerrole", supportedClass = ProviderRole.class,
        supportedOpenmrsVersions = {"2.8.* - 9.*"})
public class ProviderRoleResource extends MetadataDelegatingCrudResource<ProviderRole> {

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
    public ProviderRole getByUniqueId(String uuid) {
        return Context.getProviderService().getProviderRoleByUuid(uuid);
    }

    @Override
    public ProviderRole newDelegate() {
        return new ProviderRole();
    }

    @Override
    public ProviderRole save(ProviderRole providerRole) {
        if (providerRole != null) {
            return Context.getProviderService().saveProviderRole(providerRole);
        }
        return null;
    }

    @Override
    public void purge(ProviderRole providerRole, RequestContext requestContext) throws ResponseException {
        if (providerRole != null) {
            Context.getProviderService().purgeProviderRole(providerRole);
        }
    }

    @Override
    protected NeedsPaging<ProviderRole> doGetAll(RequestContext context) throws ResponseException {
        return new NeedsPaging<>(Context.getProviderService().getAllProviderRoles(context.getIncludeAll()), context);
    }

    @Override
    public String getResourceVersion() {
        return "1.9";
    }
}
