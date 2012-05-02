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
import java.util.List;

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

    public static final List<String> GLOBAL_PROPERTY_PROVIDER_SEARCH_DISPLAY_FIELDS() {
        String propertyValue = Context.getAdministrationService().getGlobalProperty("providermanagement.providerSearchDisplayFields");
        List<String> l = new ArrayList<String>();
        if (StringUtils.isNotBlank(propertyValue)) {
            for (String s: propertyValue.split("\\|"))  {
                if (StringUtils.isNotBlank(s))  {
                    l.add(s);
                }
            }
        }
        return l;
    }

    public static final List<String> GLOBAL_PROPERTY_PROVIDER_LIST_DISPLAY_FIELDS() {
        String propertyValue = Context.getAdministrationService().getGlobalProperty("providermanagement.providerListDisplayFields");
        List<String> l = new ArrayList<String>();
        if (StringUtils.isNotBlank(propertyValue)) {
            for (String s: propertyValue.split("\\|"))  {
                if (StringUtils.isNotBlank(s))  {
                    l.add(s);
                }
            }
        }
        return l;
    }

    public static final List<String> GLOBAL_PROPERTY_PATIENT_LIST_DISPLAY_FIELDS() {
        String propertyValue = Context.getAdministrationService().getGlobalProperty("providermanagement.patientListDisplayFields");
        List<String> l = new ArrayList<String>();
        if (StringUtils.isNotBlank(propertyValue)) {
            for (String s: propertyValue.split("\\|"))  {
                if (StringUtils.isNotBlank(s))  {
                    l.add(s);
                }
            }
        }
        return l;
    }

    public static final List<String> GLOBAL_PROPERTY_PATIENT_SEARCH_DISPLAY_FIELDS() {
        String propertyValue = Context.getAdministrationService().getGlobalProperty("providermanagement.patientSearchDisplayFields");
        List<String> l = new ArrayList<String>();
        if (StringUtils.isNotBlank(propertyValue)) {
            for (String s: propertyValue.split("\\|"))  {
                if (StringUtils.isNotBlank(s))  {
                    l.add(s);
                }
            }
        }
        return l;
    }
}
