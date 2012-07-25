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
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ProviderManagementUtils {

    /**
     * Returns true/false whether the relationship is active on the current date
     *
     * Note that if a relationship ends on a certain date, it is not considered active on that date
     *
     * @param relationship
     * @return
     */
    public static boolean isRelationshipActive(Relationship relationship) {

        Date startDate = clearTimeComponent(relationship.getStartDate());
        Date endDate = relationship.getEndDate() != null ? clearTimeComponent(relationship.getEndDate()) : null;
        Date currentDate = clearTimeComponent(new Date());

        if (endDate != null && startDate.after(endDate)) {
            throw new APIException("relationship start date cannot be after end date: relationship id " + relationship.getId());
        }

        return (startDate.before(currentDate) || startDate.equals(currentDate))
                && (endDate == null || endDate.after(currentDate));
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
}


