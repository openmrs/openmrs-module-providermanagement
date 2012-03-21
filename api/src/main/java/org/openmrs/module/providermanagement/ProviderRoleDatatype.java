package org.openmrs.module.providermanagement;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.CustomDatatype;
import org.openmrs.customdatatype.InvalidCustomValueException;
import org.openmrs.customdatatype.SerializingCustomDatatype;
import org.openmrs.module.providermanagement.api.ProviderManagementService;

/**
 * Class for using Provider Role as a datatype
 */
public class ProviderRoleDatatype implements CustomDatatype<ProviderRole> {

    @Override
    public void setConfiguration(String s) {
        // doesn't need to do anything; no configuration required for this datatype
    }

    @Override
    public String save(ProviderRole providerRole, String s) throws InvalidCustomValueException {
        if (s != null &&  !s.equals(providerRole.getUuid())) {
            throw new RuntimeException("Fatal error saving ProviderRole attribute type; uuids don't match");
        }
        else {
            Context.getService(ProviderManagementService.class).saveProviderRole(providerRole);
            return providerRole.getUuid();
        }
    }

    @Override
    public String getReferenceStringForValue(ProviderRole providerRole) throws UnsupportedOperationException {
        if (providerRole == null) {
            return null;
        }
        else {
            return providerRole.getUuid();
        }
    }

    @Override
    public ProviderRole fromReferenceString(String s) throws InvalidCustomValueException {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        else {
            return Context.getService(ProviderManagementService.class).getProviderRoleByUuid(s);
        }
    }

    @Override
    public Summary getTextSummary(String s) {
        // TODO: implement this?
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void validate(ProviderRole providerRole) throws InvalidCustomValueException {
        // TODO: implement this
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
