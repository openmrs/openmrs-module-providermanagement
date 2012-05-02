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

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.providermanagement.ProviderManagementGlobalProperties;
import org.openmrs.module.providermanagement.ProviderManagementWebUtil;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.Validate;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.validator.PersonValidator;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProviderEditFragmentController {

    public void controller(PageModel sharedPageModel, FragmentModel model,
                           @FragmentParam(value = "person", required = false) Person personParam,
                           @FragmentParam(value = "personId", required = false) Integer personId)
            throws PersonIsNotProviderException {

        // TODO: fix this so that it does't need to fetch the provider?

        // utility methods fetch the person and provider, throwing exceptions if needed
        Person person = ProviderManagementWebUtil.getPerson(sharedPageModel, personParam, personId);
        Provider provider = ProviderManagementWebUtil.getProvider(person);

        // TODO: this is a bit of a hack
        // TODO: should I remove this person address field if not used?
        // make sure the provider has an address (so we can bind to it)
        if (person.getPersonAddress() == null) {
            person.addAddress(new PersonAddress());
            Context.getPersonService().savePerson(person);
        }

        // add the person and the provider to the module
        model.addAttribute("person", person);
        model.addAttribute("provider", provider);

        // also add the person attribute types we want to display
        model.addAttribute("personAttributeTypes", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PERSON_ATTRIBUTE_TYPES());

        // add the possible provider roles
        model.addAttribute("providerRoles", Context.getService(ProviderManagementService.class).getAllProviderRoles(false));

    }

    public void saveProvider(@RequestParam("personId") @BindParams() Person person,
                             @RequestParam("provider.identifier") String identifier,
                             @RequestParam("provider.providerRole") ProviderRole providerRole)
            throws PersonIsNotProviderException {

        // TODO: add validation via annotation when it works

        // fetch the provider associated with this person
        Provider provider = ProviderManagementWebUtil.getProvider(person);

        // TODO: could potentially create a custom personId to provider converter and then could bind directly here, using a @BindParams("provider") and namespacing all the provider fields with "provider"

        // need to manually bind the provider attributes
        provider.setIdentifier(identifier);
        provider.setProviderRole(providerRole);

        // TODO: add provider validation?  should we warn/stop someone from changing a provider role if they have relationship types or supervisees not supported by the new role?
        // TODO: think about validation issues here... if we simply trap person validation and it fails, we would still want to be able to roll back provider information

        // save the provider and the person (may not need to save person, because it cascades?)
        Context.getProviderService().saveProvider(provider);
        Context.getPersonService().savePerson(person);
    }

    public FragmentActionResult addSupervisee(@RequestParam(value = "supervisor", required = true) Person supervisor,
                                              @RequestParam(value = "supervisee", required=true) Person supervisee) {

        // TODO: better handle error cases
        try {
            Context.getService(ProviderManagementService.class).assignProviderToSupervisor(supervisee, supervisor);
            return new SuccessResult();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public FragmentActionResult removeSupervisees(@RequestParam(value = "supervisor", required = true) Person supervisor,
                                                  @RequestParam(value = "supervisees", required = true) List<Person> supervisees) {

        // TODO: better handle error cases
        try {
            for (Person supervisee : supervisees) {
                Context.getService(ProviderManagementService.class).unassignProviderFromSupervisor(supervisee, supervisor);
            }
            return new SuccessResult();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public FragmentActionResult addPatient(@RequestParam(value = "provider", required = true) Person provider,
                                           @RequestParam(value = "relationshipType", required = true) RelationshipType relationshipType,
                                           @RequestParam(value = "patient", required = true) Patient patient) {

        // TODO: better handle error cases
        try {
            Context.getService(ProviderManagementService.class).assignPatientToProvider(patient, provider, relationshipType);
            return new SuccessResult();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
