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
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderManagementGlobalProperties;
import org.openmrs.module.providermanagement.ProviderManagementWebUtil;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;

public class ProviderAdvancedSearchFragmentController {

    public class AdvancedSearchCommand {

        private String name;

        private String identifier;

        private PersonAddress personAddress = new PersonAddress();

        private ProviderRole providerRole;

        private PersonAttribute attribute = new PersonAttribute();

        private String [] resultFields;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public PersonAddress getPersonAddress() {
            return personAddress;
        }

        public void setPersonAddress(PersonAddress address) {
            this.personAddress = address;
        }

        public PersonAttribute getAttribute() {
            return attribute;
        }

        public void setAttribute(PersonAttribute attribute) {
            this.attribute = attribute;
        }

        public ProviderRole getProviderRole() {
            return providerRole;
        }

        public void setProviderRole(ProviderRole providerRole) {
            this.providerRole = providerRole;
        }

        public String[] getResultFields() {
            return resultFields;
        }

        public void setResultFields(String[] resultFields) {
            this.resultFields = resultFields;
        }
    }


    public AdvancedSearchCommand initializeCommand() {

        PersonAttributeType personAttributeType =  ProviderManagementGlobalProperties.GLOBAL_PROPERTY_ADVANCED_SEARCH_PERSON_ATTRIBUTE_TYPE();

        AdvancedSearchCommand command = new AdvancedSearchCommand();
        command.getAttribute().setAttributeType(personAttributeType);

        return command;
    }

    public List<SimpleObject> getProviders(@MethodParam("initializeCommand") @BindParams() AdvancedSearchCommand command,
                                             UiUtils ui)
                    throws PersonIsNotProviderException {

        if (command.getResultFields() == null || command.getResultFields().length == 0) {
            command.setResultFields(new String[] {"personName"});
        }

        // now fetch the results
        List<ProviderRole> roles = new ArrayList<ProviderRole>();
        if (command.getProviderRole() != null) {
            roles.add(command.getProviderRole());
        }
        else if (ProviderManagementGlobalProperties.GLOBAL_PROPERTY_RESTRICT_SEARCH_TO_PROVIDERS_WITH_PROVIDER_ROLES() != null
                && ProviderManagementGlobalProperties.GLOBAL_PROPERTY_RESTRICT_SEARCH_TO_PROVIDERS_WITH_PROVIDER_ROLES()) {
            roles.addAll(Context.getService(ProviderManagementService.class).getAllProviderRoles(true));
        }

        List<Person> persons = Context.getService(ProviderManagementService.class).getProvidersAsPersons(command.getName(), command.getIdentifier(), command.getPersonAddress(), command.getAttribute(), roles, false);

         // convert to a simple object list
        return ProviderManagementWebUtil.convertPersonListToSimpleObjectList(persons, ui, command.getResultFields());
    }

    public void controller(FragmentModel model) {

        // add the possible provider roles
        model.addAttribute("providerRoles", Context.getService(ProviderManagementService.class).getAllProviderRoles(false));

        // add the person attribute type we want to include on this page
        PersonAttributeType personAttributeType =  ProviderManagementGlobalProperties.GLOBAL_PROPERTY_ADVANCED_SEARCH_PERSON_ATTRIBUTE_TYPE();
        model.addAttribute("advancedSearchPersonAttributeType", personAttributeType);

        // add the address widget to use
        model.addAttribute("addressWidget", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_ADDRESS_WIDGET());

        // add the empty command object
        model.addAttribute("command", initializeCommand());
    }
}
