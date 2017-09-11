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

package org.openmrs.module.providermanagement.fragment.controller;

import org.openmrs.Person;
import org.openmrs.ProviderAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderManagementGlobalProperties;
import org.openmrs.module.providermanagement.ProviderManagementWebUtil;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProviderSearchFragmentController {

    public List<SimpleObject> getProviders(@RequestParam(value="searchValue", required=true) String searchValue,
                                           @RequestParam(value="includeRetired", required=false) Boolean includeRetired,
                                          @RequestParam(value="excludeSuperviseesOf", required=false) Person excludeSuperviseesOf,
                                          @RequestParam(value="excludeProvider", required=false) Person excludeProvider,
                                          @RequestParam(value="providerRoles[]", required=false) ProviderRole[] providerRoles,
                                          @RequestParam(value="resultFields[]", required=false) String[] resultFields,
                                          UiUtils ui)
                throws PersonIsNotProviderException {

        if (resultFields == null || resultFields.length == 0) {
            resultFields = new String[] {"personName"};
        }

        // default is to not include retired providers
        includeRetired = includeRetired != null ? includeRetired : false;

        List<ProviderRole> providerRoleList = null;
        if (providerRoles != null && providerRoles.length > 0) {
            providerRoleList = Arrays.asList(providerRoles);
        }
        else if (ProviderManagementGlobalProperties.GLOBAL_PROPERTY_RESTRICT_SEARCH_TO_PROVIDERS_WITH_PROVIDER_ROLES() != null
                && ProviderManagementGlobalProperties.GLOBAL_PROPERTY_RESTRICT_SEARCH_TO_PROVIDERS_WITH_PROVIDER_ROLES()) {
            providerRoleList = Context.getService(ProviderManagementService.class).getAllProviderRoles(true);
        }

        // now fetch the results
        List<Person> persons = Context.getService(ProviderManagementService.class).getProvidersAsPersons(searchValue, providerRoleList, includeRetired);

        // exclude supervisees of a provider if needed
        if (excludeSuperviseesOf != null) {
            List<Person> supervisees = Context.getService(ProviderManagementService.class).getSuperviseesForSupervisor(excludeSuperviseesOf, new Date());
            persons.removeAll(supervisees);
        }

        // exclude any specified provider
        if (excludeProvider != null) {
            persons.remove(excludeProvider);
        }

        // convert to a simple object list
        return ProviderManagementWebUtil.convertPersonListToSimpleObjectList(persons, ui, resultFields);
    }

    /**
     * Given a provider role, it returns all the potential providers that could supervise someone with this role
     * @param providerRole
     * @param providerManagementService
     * @param ui
     * @return
     * @throws PersonIsNotProviderException
     */
    public List<SimpleObject> getSupervisors(@RequestParam(value="roleId", required=true) ProviderRole providerRole,
                                             @SpringBean("providerManagementService") ProviderManagementService providerManagementService,
                                           UiUtils ui)
            throws PersonIsNotProviderException {


        List<SimpleObject> items = new ArrayList<SimpleObject>();
        List<ProviderRole> roles = providerManagementService.getProviderRolesBySuperviseeProviderRole(providerRole);
        if ( roles!=null && roles.size()>0) {
            List<Person> supervisors = providerManagementService.getProvidersAsPersonsByRoles(roles);
            if (supervisors != null && supervisors.size() > 0 ) {
                for (Person supervisor : supervisors) {
                    SimpleObject item = new SimpleObject();
                    item.put("personId", supervisor.getId());
                    item.put("familyName", supervisor.getFamilyName());
                    item.put("givenName", supervisor.getGivenName());
                    items.add(item);
                }
            }
        }
        return items;
    }

    /**
     * Given a provider role, it returns a list of providers who could be supervised by a provider with this given role
     * @param providerRole
     * @param providerManagementService
     * @param ui
     * @return
     * @throws PersonIsNotProviderException
     */
    public List<SimpleObject> getSupervisees(@RequestParam(value="roleId", required=false) ProviderRole providerRole,
                                             @SpringBean("providerManagementService") ProviderManagementService providerManagementService,
                                             UiUtils ui)
            throws PersonIsNotProviderException {

        List<SimpleObject> items = new ArrayList<SimpleObject>();
        Set<ProviderRole> roles = null;
        if (providerRole != null) {
            roles = providerRole.getSuperviseeProviderRoles();
        } else {
            List<ProviderRole> allProviderRoles = providerManagementService.getAllProviderRoles(false);
            roles = new HashSet<ProviderRole>();
            for (ProviderRole role : allProviderRoles) {
                roles.add(role);
            }
        }
        if ( roles!=null && roles.size() > 0 ) {
            List<Person> supervisees = providerManagementService.getProvidersAsPersonsByRoles(new ArrayList<ProviderRole>(roles));
            if (supervisees != null && supervisees.size() > 0 ) {
                for (Person supervisee : supervisees) {
                    SimpleObject item = new SimpleObject();
                    item.put("personId", supervisee.getId());
                    item.put("familyName", supervisee.getFamilyName());
                    item.put("givenName", supervisee.getGivenName());
                    items.add(item);
                }
            }
        }
        return items;
    }
    public List<SimpleObject> getProviderAttributes(@RequestParam(value="roleId", required=false) ProviderRole providerRole,
                                                    @SpringBean("providerManagementService") ProviderManagementService providerManagementService,
                                                    UiUtils ui) {
        List<SimpleObject> items = new ArrayList<SimpleObject>();
        Set<ProviderAttributeType> providerAttributeTypes = null ;
        if (providerRole != null) {
            providerAttributeTypes =providerRole.getProviderAttributeTypes();
        }
        if (providerAttributeTypes != null && providerAttributeTypes.size() > 0 ) {
            for (ProviderAttributeType providerAttributeType : providerAttributeTypes) {
                SimpleObject item = new SimpleObject();
                item.put("providerAttributeTypeId", providerAttributeType.getProviderAttributeTypeId());
                item.put("name", providerAttributeType.getName());
                item.put("datatypeClassname", providerAttributeType.getDatatypeClassname());
                item.put("datatypeConfig", providerAttributeType.getDatatypeConfig());
                items.add(item);
            }
        }
        return items;
    }
}
