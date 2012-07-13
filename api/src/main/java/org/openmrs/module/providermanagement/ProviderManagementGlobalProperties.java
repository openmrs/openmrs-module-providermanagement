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

import org.apache.commons.lang.StringUtils;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProviderManagementGlobalProperties {

    /**
     * @return person attribute types to be displayed on the provider dashboard
     */
    public static final List<PersonAttributeType> GLOBAL_PROPERTY_PERSON_ATTRIBUTE_TYPES() {
        String propertyValue = Context.getAdministrationService().getGlobalProperty("providermanagement.personAttributeTypes");
        List<PersonAttributeType> l = new ArrayList<PersonAttributeType>();
        if (StringUtils.isNotBlank(propertyValue)) {
            for (String s : propertyValue.split("\\|")) {
                PersonAttributeType t = Context.getPersonService().getPersonAttributeTypeByUuid(s);
                if (t != null) {
                    l.add(t);
                }
            }
        }
        return l;
    }

    public static final Map<String,String> GLOBAL_PROPERTY_PROVIDER_SEARCH_DISPLAY_FIELDS() {
        return globalPropertyToMap("providermanagement.providerSearchDisplayFields");
    }

    public static final Map<String,String> GLOBAL_PROPERTY_PROVIDER_LIST_DISPLAY_FIELDS() {
        return globalPropertyToMap("providermanagement.providerListDisplayFields");
    }

    public static final Map<String,String> GLOBAL_PROPERTY_PATIENT_LIST_DISPLAY_FIELDS() {
        return globalPropertyToMap("providermanagement.patientListDisplayFields");
    }

    public static final Map<String,String> GLOBAL_PROPERTY_HISTORICAL_PATIENT_LIST_DISPLAY_FIELDS() {
        return globalPropertyToMap("providermanagement.historicalPatientListDisplayFields");
    }

    public static final Map<String,String> GLOBAL_PROPERTY_PATIENT_SEARCH_DISPLAY_FIELDS() {
        return globalPropertyToMap("providermanagement.patientSearchDisplayFields");
    }

    public static final Map<String,String> GLOBAL_PROPERTY_PERSON_SEARCH_DISPLAY_FIELDS() {
        return globalPropertyToMap("providermanagement.personSearchDisplayFields");
    }

    public static final PersonAttributeType GLOBAL_PROPERTY_ADVANCED_SEARCH_PERSON_ATTRIBUTE_TYPE() {
        String propertyValue = Context.getAdministrationService().getGlobalProperty("providermanagement.advancedSearchPersonAttributeType");

        if (StringUtils.isNotBlank(propertyValue)) {
            return Context.getPersonService().getPersonAttributeTypeByUuid(propertyValue);
        }
        else {
            return null;
        }
    }

    public static final String GLOBAL_PROPERTY_ADDRESS_WIDGET() {
        String propertyValue = Context.getAdministrationService().getGlobalProperty("providermanagement.addressWidget");
        if (StringUtils.isNotBlank(propertyValue)) {
            return propertyValue;
        }
        else {
            return "personAddress";
        }
    }

    public static final Map<String,String> globalPropertyToMap(String globalPropertyName) {

        // load the appropriate global property
        String propertyValue = Context.getAdministrationService().getGlobalProperty(globalPropertyName);

        Map<String,String> map = new LinkedHashMap<String,String>();
        if (StringUtils.isNotBlank(propertyValue)) {

            // split the global property on the pipe symbol
            for (String s: propertyValue.split("\\|"))  {
                if (StringUtils.isNotBlank(s))  {

                    // now split on : for each key-value pair
                    String [] field = s.split(":");

                    // ignore any malformed entries
                    if (field != null && field.length == 2) {
                        map.put(field[0], field[1]);
                    }
                }
            }
        }

        // TODO: will need to change this if this method is used for any other kind of global property
        // if for some reason we have no entries, add name as a default
        if (map.size() == 0) {
            map.put("Name","personName");
        }

        return map;
    }
}
