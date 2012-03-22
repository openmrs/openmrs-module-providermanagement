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

package org.openmrs.module.providermanagement;

import org.openmrs.Provider;
import org.openmrs.ProviderAttribute;
import org.openmrs.ProviderAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProviderManagementUtils {

    private static ProviderAttributeType providerRoleAttributeType = null;

    /**
     * Returns the provider role associated with the specified provider
     *
     * @param provider
     * @return the provider role associated with the specified provider
     */
    public static ProviderRole getProviderRole(Provider provider) {

        List<ProviderAttribute> attrs = provider.getActiveAttributes(getProviderRoleAttributeType());

        if (attrs == null || attrs.size() == 0) {
            return null;
        }
        else if (attrs.size() == 1){
            return (ProviderRole) attrs.get(0).getValue();
        }
        else {
            throw new APIException("Provider should never have more than one Provider Role");
        }
    }


    /**
     * Returns true if the specified provider can support the specified relationship type, false otherwise
     *
     * @param provider
     * @param relationshipType
     * @return true if the specified provider can support the specified relationship type, false otherwise
     */
    public static boolean supportsRelationshipType(Provider provider, RelationshipType relationshipType) {
        
        if (provider == null) {
            throw new APIException("Provider should not be null");
        }

        if (relationshipType == null) {
            throw new APIException("Relationship type should not be null");
        }
        
        ProviderRole role = getProviderRole(provider);

        // if this provider has no role, return false
        if (role == null) {
            return false;
        }
        // otherwise, test if the provider's role supports the specified relationship type
        else {
            return role.supportsRelationshipType(relationshipType);
        }
    }

    /**
     * Given a Date object, returns a Date object for the same date but with the time component (hours, minutes, seconds & milliseconds) removed
     */
    public static Date clearTimeComponent(Date date) {
        // Get Calendar object set to the date and time of the given Date object
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        // Set time fields to zero
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * Utility methods
     */
    private static ProviderAttributeType getProviderRoleAttributeType() {
        if (providerRoleAttributeType == null) {
            providerRoleAttributeType = Context.getService(ProviderManagementService.class).getProviderRoleAttributeType();
        }

        return providerRoleAttributeType;
    }



}


