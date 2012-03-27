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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;

import java.util.*;

public class ProviderManagementUtils {

    private static ProviderAttributeType providerRoleAttributeType = null;

    /**
     * Returns the provider roles associated with the specified provider
     *
     * @param provider
     * @return the provider role associated with the specified provider
     */
    public static List<ProviderRole> getProviderRoles(Person provider) {

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (!isProvider(provider)) {
            // return empty list if this person is not a provider
            return new ArrayList<ProviderRole>();
        }
        
        // otherwise, collect all the roles associated with this provider
        // (we use a set to avoid duplicates at this point)
        Set<ProviderRole> providerRoles = new HashSet<ProviderRole>();
        
        Collection<Provider> providers = Context.getProviderService().getProvidersByPerson(provider);

        // TODO: no need to manually exclude retired providers after TRUNK-3219 has been implemented
        filterRetired(providers);
        
        for (Provider p : providers) {
            ProviderRole providerRole = getProviderRole(p);
            if (providerRole != null) {
                providerRoles.add(providerRole);
            }
        }

        return new ArrayList<ProviderRole>(providerRoles);
    }

    /**
     * Returns whether or not the passed person has one or more associated providers (unretired or retired)
     * (So note that a person that only is associated with retired Provider objects is still consider a "provider")
     * 
     * @param person
     * @return whether or not the passed person has one or more associated providers
     */
    public static boolean isProvider(Person person) {

        if (person == null) {
            throw new APIException("Person cannot be null");
        }

        // TODO: change this to explicitly include retired providers after TRUNK-3219 has been implemented
        Collection<Provider> providers = Context.getProviderService().getProvidersByPerson(person);
        return providers == null || providers.size() == 0 ? false : true;
    }


    /**
     * Returns whether or not the passed provider has the specified provider role
     *
     * @param provider
     * @param role
     * @return whether or not the passed provider has the specified provider role
     */
    public static boolean hasRole(Person provider, ProviderRole role) {

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (role == null) {
            throw new APIException("Role cannot be null");
        }

        return getProviderRoles(provider).contains(role);
    }

    /**
     * 
     * Returns true if the specified provider can support the specified relationship type, false otherwise
     *
     * @param provider
     * @param relationshipType
     * @return true if the specified provider can support the specified relationship type, false otherwise
     */
    public static boolean supportsRelationshipType(Person provider, RelationshipType relationshipType) {

        Collection<Provider> providers = Context.getProviderService().getProvidersByPerson(provider);

        // TODO: no need to manually exclude retired providers after TRUNK-3219 has been implemented
        filterRetired(providers);

        if (providers == null || providers.size() == 0) {
            return false;
        }
        
        for (Provider p : providers) {
            if (supportsRelationshipType(p, relationshipType)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Filters retired providers out of the list of passed providers
     * 
     * @param providers
     */
    public static void filterRetired(Collection<Provider> providers) {
        CollectionUtils.filter(providers, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                return !((Provider) o).isRetired();
            }
        });
    }

    /**
     * Filters out all relationship types that are not associated with a provider role
     * (Does not filter out retired relationship types associated with a provider role)
     *
     * @param relationships
     */
    public static void filterNonProviderRelationships(Collection<Relationship> relationships) {
        final List<RelationshipType> providerRelationshipTypes = Context.getService(ProviderManagementService.class).getAllProviderRoleRelationshipTypes(true);
        CollectionUtils.filter(relationships, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                return providerRelationshipTypes.contains(((Relationship) o).getRelationshipType());
            }
        });

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
     * Returns the role associated with the passed provider object
     *
     * @param provider
     * @return the role associated with the passed provider object
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
     * Utility methods
     */
    private static ProviderAttributeType getProviderRoleAttributeType() {
        if (providerRoleAttributeType == null) {
            providerRoleAttributeType = Context.getService(ProviderManagementService.class).getProviderRoleAttributeType();
        }

        return providerRoleAttributeType;
    }

    private static boolean supportsRelationshipType(Provider provider, RelationshipType relationshipType) {

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
   
}


