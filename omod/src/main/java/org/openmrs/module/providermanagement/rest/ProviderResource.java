package org.openmrs.module.providermanagement.rest;

import org.openmrs.ProviderAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.providermanagement.rest.controller.ProviderManagementRestController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Resource(name = RestConstants.VERSION_1 + ProviderManagementRestController.PROVIDER_MANAGEMENT_REST_NAMESPACE + "/provider", supportedClass = Provider.class, supportedOpenmrsVersions = {
        "1.9.* - 9.*" })
public class ProviderResource extends MetadataDelegatingCrudResource<Provider> {

    public ProviderResource() {
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display");
            description.addProperty("person", Representation.REF);
            description.addProperty("identifier");
            description.addProperty("providerRole", Representation.REF);
            description.addProperty("attributes", "activeAttributes", Representation.REF);
            description.addProperty("retired");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display");
            description.addProperty("person", Representation.DEFAULT);
            description.addProperty("identifier");
            description.addProperty("providerRole", Representation.DEFAULT);
            description.addProperty("attributes", "activeAttributes", Representation.DEFAULT);
            description.addProperty("retired");
            description.addProperty("auditInfo");
            description.addSelfLink();
            return description;
        }
        return null;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
     */
    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addRequiredProperty("person");
        description.addRequiredProperty("identifier");
        description.addProperty("attributes");
        description.addProperty("providerRole");
        description.addProperty("retired");
        description.addProperty("providerRole");
        return description;
    }

    /**
     * Sets the attributes of a Provider
     *
     * @param provider whose attributes to be set
     * @param attributes the attributes to be set
     */
    @PropertySetter("attributes")
    public static void setAttributes(Provider provider, Set<ProviderAttribute> attributes) {
        for (ProviderAttribute attribute : attributes) {
            attribute.setOwner(provider);
        }
        provider.setAttributes(attributes);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
     */
    @Override
    public DelegatingResourceDescription getUpdatableProperties() {
        return getCreatableProperties();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#newDelegate()
     */
    @Override
    public Provider newDelegate() {
        return new Provider();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#save(java.lang.Object)
     */
    @Override
    public Provider save(Provider provider) {
        return (Provider) Context.getProviderService().saveProvider(provider);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getByUniqueId(java.lang.String)
     */
    @Override
    public Provider getByUniqueId(String uuid) {
        return (Provider) Context.getProviderService().getProviderByUuid(uuid);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(java.lang.Object,
     *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public void delete(Provider provider, String reason, RequestContext context) throws ResponseException {
        if (provider.isRetired()) {
            // DELETE is idempotent, so we return success here
            return;
        }
        Context.getProviderService().retireProvider(provider, reason);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
     *      org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public void purge(Provider provider, RequestContext context) throws ResponseException {
        if (provider == null) {
            // DELETE is idempotent, so we return success here
            return;
        }
        Context.getProviderService().purgeProvider(provider);
    }

    @Override
    protected NeedsPaging<Provider> doGetAll(RequestContext context) throws ResponseException {

        List<org.openmrs.Provider> providers = Context.getProviderService().getAllProviders(context.getIncludeAll());
        List<Provider> upliftedProviders = new ArrayList<Provider>();

        for (org.openmrs.Provider p : providers) {
            upliftedProviders.add((Provider) p);
        }

        return new NeedsPaging<Provider>(upliftedProviders, context);
    }

    /**
     * @param provider
     * @return identifier + name (for concise display purposes)
     */
    @Override
    @PropertyGetter("display")
    public String getDisplayString(Provider provider) {
        if (provider.getIdentifier() == null) {
            return provider.getName();
        }
        return provider.getIdentifier() + " - " + provider.getName();
    }

    @Override
    @PropertyGetter("auditInfo")
    public SimpleObject getAuditInfo(Provider provider) throws Exception {
        return super.getAuditInfo(provider);
    }

    @Override
    public String getResourceVersion() {
        return "1.9";
    }

}
