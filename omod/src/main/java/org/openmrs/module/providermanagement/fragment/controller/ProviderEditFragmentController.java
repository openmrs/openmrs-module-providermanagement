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

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.ProviderAttribute;
import org.openmrs.ProviderAttributeType;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.providermanagement.ProviderManagementGlobalProperties;
import org.openmrs.module.providermanagement.ProviderManagementUtils;
import org.openmrs.module.providermanagement.ProviderManagementWebUtil;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.ObjectResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.HandlerUtil;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProviderEditFragmentController {

    // simple command object for provider
    public class ProviderCommand {

        private String identifier;

        private ProviderRole providerRole;

        private Map<String, String> attributeMap = new HashMap<String, String>();

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public ProviderRole getProviderRole() {
            return providerRole;
        }

        public void setProviderRole(ProviderRole providerRole) {
            this.providerRole = providerRole;
        }

        public Map<String, String> getAttributeMap() {
            return attributeMap;
        }

        public void setAttributeMap(Map<String, String> attributeMap) {
            this.attributeMap = attributeMap;
        }
    }

    public void controller(PageModel sharedPageModel, FragmentModel model,
                           @FragmentParam(value = "person", required = false) Person personParam,
                           @FragmentParam(value = "personId", required = false) Integer personId) {

        // fetch the person and provider
        Person person = ProviderManagementWebUtil.getPerson(sharedPageModel, personParam, personId);
        Provider provider = null;

        // only try to fetch the provider if we have a non-transient person
        // (the providerCreate page may create a transient person witha new person name)
        if (person != null && person.getId() != null) {
            try {
                provider = ProviderManagementWebUtil.getProvider(person);
            }
            catch (PersonIsNotProviderException e) {
                // we are allowed to have persons who are not providers when in the "add" mode
            }
        }

        // add the person and the provider to the module
        model.addAttribute("person", person);
        model.addAttribute("provider", provider);

        // also add the person attribute types we want to display
        model.addAttribute("personAttributeTypes", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PERSON_ATTRIBUTE_TYPES());

        // add the possible provider roles
        model.addAttribute("providerRoles", Context.getService(ProviderManagementService.class).getAllProviderRoles(false));

        // add the address widget to use
        model.addAttribute("addressWidget", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_ADDRESS_WIDGET());

    }


    /**
     * Initializes a person object for binding by adding empty person attributes as needed
     */
    public Person initializePerson(@RequestParam(value = "personId", required = false) Person person) {

        if (person == null) {
            person = new Person();
        }

        if (person.getPersonName() == null) {
            person.getNames().add(new PersonName());
        }

        if (person.getPersonAddress() == null) {
            person.getAddresses().add(new PersonAddress());
        }

        for (PersonAttributeType attributeType : ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PERSON_ATTRIBUTE_TYPES()) {
            if (person.getAttribute(attributeType) == null) {
                PersonAttribute attr = new PersonAttribute(attributeType, null);
                attr.setPerson(person);
                // we have to do this manually, don't use person.addAttribute(), because that method ignores null attributes
                person.getAttributes().add(attr);
            }
        }

        return person;
    }

    public ProviderCommand initializeProviderCommand()  {
        return new ProviderCommand();
    }

    public FragmentActionResult saveProvider(@MethodParam("initializePerson") @BindParams() Person person, //@Validate Person person,
                                                @MethodParam("initializeProviderCommand") @BindParams("provider") ProviderCommand providerCommand) {

        // fetch the provider associated with this person
        Provider provider;

        if (person.getId() != null) {     // make sure this isn't transient person we have just created
            try {
                provider = ProviderManagementWebUtil.getProvider(person);
            }
            catch (PersonIsNotProviderException e) {
                // we will get here if we are upgrading an existing person to a provider
                provider = new Provider();
            }
        }
        else {
            // we will get here if the creating an entirely new person from scratch
            provider = new Provider();
        }

        // need to manually bind the provider attributes
        provider.setIdentifier(providerCommand.getIdentifier());
        provider.setProviderRole(providerCommand.getProviderRole());

        // if this is new person & provider, we may not set have set the person on the provider
        provider.setPerson(person);

        // manually bind the provider attributes
        // TODO: (PROV-10) Improve the editing of Provider Attributes to be able to handle attributes that aren't Strings
        // TODO: (PROV-16) note that when provider attributes are updated they are voided, but person attributes are overwritten?
        if (providerCommand.attributeMap != null) {
            for (Map.Entry entry : providerCommand.attributeMap.entrySet()) {

                if (StringUtils.isNotBlank(entry.getValue().toString())) {
                    ProviderAttributeType type = Context.getProviderService().getProviderAttributeType(Integer.valueOf(entry.getKey().toString()));

                    // NOTE: note that this currently allows only one active attribute of each type--and voids any others
                    boolean foundMatch = false;
                    for (ProviderAttribute attr : provider.getActiveAttributes(type)) {
                        if (attr.getValueReference().equals(entry.getValue())) {
                            foundMatch = true;
                        }
                        else {
                            attr.setVoided(true);
                            attr.setVoidReason("voided during provider management module provider update");
                        }
                    }

                    // sets the attribute if no existing match found
                    if (!foundMatch) {
                        ProviderAttribute attr = new ProviderAttribute();
                        attr.setAttributeType(type);
                        attr.setValueReferenceInternal(entry.getValue().toString());   // TODO: (PROV-10) only works with string attributes
                        provider.addAttribute(attr);
                    }
                }
            }
        }

        // TODO: (PROV-17) stop someone from changing a provider role if they have relationship types or supervisees not supported by the new role?

        // we need to manually validate the provider
        Validator providerValidator = HandlerUtil.getPreferredHandler(Validator.class, Provider.class);
        BindingResult providerErrors = new BeanPropertyBindingResult(provider, "provider");  // TODO: is this the correct nomenclature?
        providerValidator.validate(provider,providerErrors);

        if (providerErrors.hasErrors()) {
            return new FailureResult(providerErrors);
        }

        // now manually validate the person
        Validator personValidator = HandlerUtil.getPreferredHandler(Validator.class, Person.class);
        BindingResult personErrors = new BeanPropertyBindingResult(provider.getPerson(), "person");  // TODO: is this the correct nomenclature?
        personValidator.validate(provider.getPerson(),personErrors);

        if (personErrors.hasErrors()) {
            return new FailureResult(personErrors);
        }

        // need to manually remove any person attributes that have no value
        for (PersonAttributeType attributeType : ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PERSON_ATTRIBUTE_TYPES()) {
            if (person.getAttribute(attributeType) != null  && StringUtils.isBlank(person.getAttribute(attributeType).getValue())) {
                person.removeAttribute(person.getAttribute(attributeType));
            }
        }

        // save the person and the provider
        Context.getPersonService().savePerson(person);
        Context.getProviderService().saveProvider(provider);

        // return the id of the person we have saved
        return new ObjectResult(person.getId());
    }

    public FragmentActionResult retireProvider(@RequestParam(value = "provider", required = true) Person provider,
                                               @RequestParam(value = "reason", required = false) String reason) {

        try {
            Provider p = ProviderManagementWebUtil.getProvider(provider);   // get actual provider object associated with this provider

            // unassign all patients, supervisors, and supervisees from the provider
            Context.getService(ProviderManagementService.class).unassignAllPatientsFromProvider(provider);
            Context.getService(ProviderManagementService.class).unassignAllProvidersFromSupervisor(provider);
            Context.getService(ProviderManagementService.class).unassignAllSupervisorsFromProvider(provider);

            // now retire the provider
            String retireReason = "retired via Provider Management UI";
            if (StringUtils.isNotBlank(reason)) {
                retireReason = reason;
            }
            Context.getProviderService().retireProvider(p, retireReason);
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }

        return new SuccessResult();
    }

    public FragmentActionResult unretireProvider(@RequestParam(value = "provider", required = true) Person provider) {

        try {
            Provider p = ProviderManagementWebUtil.getProvider(provider);   // get actual provider object associated with this provider
            // (note that we don't assign any patients or supervisees back to the provider; this needs to be done manually)
            Context.getProviderService().unretireProvider(p);
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }

        return new SuccessResult();
    }

    public FragmentActionResult addSupervisee(@RequestParam(value = "supervisor", required = true) Person supervisor,
                                              @RequestParam(value = "supervisee", required = false) Person supervisee,
                                              @RequestParam(value = "date", required = false) Date date) {

        // validate input (note that this validation is also handled client-side--this is just a backup)
        if (supervisee == null) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.supervisee.required"));
        }
        else if (date == null) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.startDate.required"));
        }
        else if (ProviderManagementUtils.clearTimeComponent(date).after(new Date())) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.startDate.notInFuture"));
        }

        // if validation passes, try to assign the supervisee to the supervisor
        try {
            Context.getService(ProviderManagementService.class).assignProviderToSupervisor(supervisee, supervisor, date);
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }

    }

    public SimpleObject assignProviderRoleToPerson(
                                               @RequestParam(value = "person", required = true) Person person,
                                               @RequestParam(value = "providerRole", required = true) ProviderRole providerRole,
                                               @RequestParam(value = "identifier", required = true) String identifier,
                                               @SpringBean("providerManagementService") ProviderManagementService providerManagementService) {

        SimpleObject item = new SimpleObject();
        if (person != null && providerRole != null && StringUtils.isNotBlank(identifier)) {
            providerManagementService.assignProviderRoleToPerson(person, providerRole, identifier);
            List<Provider> providersByPerson = providerManagementService.getProvidersByPerson(person, false);
            if (providersByPerson != null && providersByPerson.size() > 0){
                //find the provider record that matches the identifier we just created
                for (Provider provider : providersByPerson) {
                    if (StringUtils.equals(provider.getIdentifier(), identifier) ) {
                        item.put("uuid", provider.getUuid());
                        item.put("identifier", provider.getIdentifier());
                        item.put("id", provider.getId());
                        item.put("providerRoleUuid", provider.getProviderRole().getUuid());
                        item.put("providerRole", provider.getProviderRole().toString());
                        item.put("success", "true");
                        return item;
                    }
                }
            }

        }
        item.put("success", "false");
        return item;

    }

        public FragmentActionResult editSupervisor(@RequestParam(value = "supervisee", required = true) Person supervisee,
                                               @RequestParam(value = "supervisor", required = true) Person supervisor,
                                               @RequestParam(value = "relationship", required = false) Relationship relationship,
                                               @RequestParam(value = "date", required = false) Date date,
                                               @SpringBean("providerManagementService") ProviderManagementService providerManagementService) {

        if (date == null) {
            date = new Date();
        }
        if (relationship != null) {
            if (relationship.getPersonB().getPersonId().compareTo(supervisee.getPersonId()) == 0) {
                if (relationship.getPersonA().getPersonId().compareTo(supervisor.getPersonId()) != 0) {
                    //end this relationship
                    relationship.setEndDate(ProviderManagementUtils.clearTimeComponent(new Date()));
                    Context.getPersonService().saveRelationship(relationship);
                } else {
                    relationship.setStartDate(ProviderManagementUtils.clearTimeComponent(date));
                    // if it is the same relationship with the same supervisor then just update the startDate of the relationship and return
                    Context.getPersonService().saveRelationship(relationship);
                    return new SuccessResult();
                }
            }
        }
        try {
            providerManagementService.assignProviderToSupervisor(supervisee, supervisor, date);
            return new SuccessResult();
        } catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }


    }

    public FragmentActionResult addSupervisees(@RequestParam(value = "supervisor", required = true) Person supervisor,
                                              @RequestParam(value = "supervisees", required = false) List<Person> supervisees) {

        // validate input (note that this validation is also handled client-side--this is just a backup)
        if (supervisees == null || supervisees.size() == 0) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.supervisees.required"));
        }

        // if validation passed, try to assign the supervisees to the supervisor
        try {
            for (Person supervisee : supervisees) {
                Context.getService(ProviderManagementService.class).assignProviderToSupervisor(supervisee, supervisor);
            }
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }

    }

    public FragmentActionResult editSupervisees(@RequestParam(value = "superviseeRelationships", required = false) List<Relationship> superviseeRelationships,
                                                @RequestParam(value = "startDate", required = false) Date startDate,
                                                @RequestParam(value = "endDate", required = false) Date endDate) {

        // validate input (note that this validation--with the exception of start date after end date--is also handled client-side--this is just a backup)
        if (superviseeRelationships == null || superviseeRelationships .size() == 0) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.supervisee.required"));
        }
        else if (startDate == null) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.startDate.required"));
        }
        else if (ProviderManagementUtils.clearTimeComponent(startDate).after(new Date())) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.startDate.notInFuture"));
        }
        else if (endDate != null && ProviderManagementUtils.clearTimeComponent(endDate).after(new Date())) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.endDate.notInFuture"));
        }
        else if (endDate != null && startDate.after(endDate)) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.startDateAfterEndDate"));
        }

        // try to update the start and end date on all the selected relationships
        try {
            for (Relationship superviseeRelationship : superviseeRelationships) {
                superviseeRelationship.setStartDate(startDate);
                superviseeRelationship.setEndDate(endDate);
                Context.getPersonService().saveRelationship(superviseeRelationship);
            }
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }
    }

    public FragmentActionResult unassignSupervisee(@RequestParam(value = "supervisor", required = true) Person supervisor,
                                                  @RequestParam(value = "supervisee", required = true) Person supervisee,
                                                  @RequestParam(value = "endDate", required = false) Date endDate) {

        Date date = new Date();
        if (endDate != null) {
            date = endDate;
        }
        else if (ProviderManagementUtils.clearTimeComponent(date).after(new Date())) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.endDate.notInFuture"));
        }

        try {
            Context.getService(ProviderManagementService.class).unassignProviderFromSupervisor(supervisee, supervisor, date);
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }

    }

    public FragmentActionResult removeSupervisees(@RequestParam(value = "supervisor", required = true) Person supervisor,
                                                  @RequestParam(value = "superviseeRelationships", required = false) List<Relationship> superviseeRelationships,
                                                  @RequestParam(value = "date", required = false) Date date) {

        // validate input (note that this validation is also handled client-side--this is just a backup)
        if (superviseeRelationships == null || superviseeRelationships .size() == 0) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.supervisees.required"));
        }
        else if (date == null) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.endDate.required"));
        }
        else if (ProviderManagementUtils.clearTimeComponent(date).after(new Date())) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.endDate.notInFuture"));
        }

        // attempt to unassign each supervisee from supervisor
        try {
            for (Relationship relationship : superviseeRelationships) {
                Context.getService(ProviderManagementService.class).unassignProviderFromSupervisor(relationship.getPersonB(), supervisor, date);
            }
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }

    }

    public FragmentActionResult transferSupervisees(@RequestParam(value = "oldSupervisor", required = true) Person oldSupervisor,
                                                    @RequestParam(value = "newSupervisor", required = false) Person newSupervisor,
                                                    @RequestParam(value = "superviseeRelationships", required = false) List<Relationship> superviseeRelationships,
                                                    @RequestParam(value = "date", required = false) Date date) {

        // validate input (note that this validation is also handled client-side--this is just a backup)
        if (superviseeRelationships == null || superviseeRelationships .size() == 0) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.supervisees.required"));
        }
        else if (newSupervisor == null) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.newSupervisor.required"));
        }
        else if (date == null) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.transferDate.required"));
        }
        else if (ProviderManagementUtils.clearTimeComponent(date).after(new Date())) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.transferDate.notInFuture"));
        }

        // attempt to transfer all the supervisees
        try {
            List<Person> supervisees = new ArrayList<Person>();

            for (Relationship relationship : superviseeRelationships) {
                supervisees.add(relationship.getPersonB());
            }

            Context.getService(ProviderManagementService.class).transferSupervisees(supervisees, oldSupervisor, newSupervisor);
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }

    }

    public FragmentActionResult voidSupervisees(@RequestParam(value = "superviseeRelationships", required = false) List<Relationship> superviseeRelationships,
                                             @RequestParam(value = "voidReason", required = false) String voidReason) {

        // validate input (note that this validation is also handled client-side--this is just a backup)
        if (superviseeRelationships == null || superviseeRelationships .size() == 0) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.supervisees.required"));
        }
        else if (StringUtils.isBlank(voidReason)) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.voidReason.required"));
        }

        // attempt to void all the supervisee relationships
        try {
            for (Relationship superviseeRelationship : superviseeRelationships) {
                Context.getPersonService().voidRelationship(superviseeRelationship, voidReason);
            }
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }
    }

    public FragmentActionResult addPatient(@RequestParam(value = "provider", required = true) Person provider,
                                           @RequestParam(value = "relationshipType", required = true) RelationshipType relationshipType,
                                           @RequestParam(value = "patient", required = false) Patient patient,
                                           @RequestParam(value = "date", required = false) Date date) {

        // validate input (note that this validation is also handled client-side--this is just a backup)
        if (patient == null) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.patient.required"));
        }
        else if (date == null) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.startDate.required"));
        }
        else if (ProviderManagementUtils.clearTimeComponent(date).after(new Date())) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.startDate.notInFuture"));
        }

        // attempt to assign the patient to the provider
        try {
            Context.getService(ProviderManagementService.class).assignPatientToProvider(patient, provider, relationshipType, date);
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }
    }

    public FragmentActionResult editPatients(@RequestParam(value = "patientRelationships", required = false) List<Relationship> patientRelationships,
                                             @RequestParam(value = "startDate", required = false) Date startDate,
                                             @RequestParam(value = "endDate", required = false) Date endDate) {

        // validate input (note that this validation--with the exception of start date after end date--is also handled client-side--this is just a backup)
        if (patientRelationships == null || patientRelationships.size() == 0) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.patient.required"));
        }
        else if (startDate == null) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.startDate.required"));
        }
        else if (ProviderManagementUtils.clearTimeComponent(startDate).after(new Date())) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.startDate.notInFuture"));
        }
        else if (endDate != null && ProviderManagementUtils.clearTimeComponent(endDate).after(new Date())) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.endDate.notInFuture"));
        }
        else if (endDate != null && startDate.after(endDate)) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.startDateAfterEndDate"));
        }

        // otherwise, try to update the start and end dates for the patient
        try {
            for (Relationship patientRelationship : patientRelationships) {
                patientRelationship.setStartDate(startDate);
                patientRelationship.setEndDate(endDate);
                Context.getPersonService().saveRelationship(patientRelationship);
            }
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }
    }

    public FragmentActionResult removePatients(@RequestParam(value = "provider", required = true) Person provider,
                                                @RequestParam(value = "relationshipType", required = true) RelationshipType relationshipType,
                                                @RequestParam(value = "patientRelationships", required = false) List<Relationship> patientRelationships,
                                                @RequestParam(value = "date", required = false) Date date) {

        // validate input (note that this validation is also handled client-side--this is just a backup)
        if (patientRelationships == null || patientRelationships.size() == 0) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.patients.required"));
        }
        else if (date == null) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.endDate.required"));
        }
        else if (ProviderManagementUtils.clearTimeComponent(date).after(new Date())) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.endDate.notInFuture"));
        }

        // try to unassign all the patients from the provider
        try {
            for (Relationship patientRelationship : patientRelationships) {
                Patient patient = Context.getPatientService().getPatient(patientRelationship.getPersonB().getId());
                Context.getService(ProviderManagementService.class).unassignPatientFromProvider(patient, provider, relationshipType, date);
            }
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }
    }

    public FragmentActionResult removePatient(@RequestParam(value = "provider", required = true) Person provider,
                                               @RequestParam(value = "relationshipType", required = true) RelationshipType relationshipType,
                                               @RequestParam(value = "patientRelationship", required = false) Relationship patientRelationship,
                                               @RequestParam(value = "date", required = false) Date date) {

        if (patientRelationship == null ) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.patients.required"));
        }
        else if (date == null) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.endDate.required"));
        }
        else if (ProviderManagementUtils.clearTimeComponent(date).after(new Date())) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.endDate.notInFuture"));
        }

        // try to unassign patient from the provider
        try {
            Patient patient = Context.getPatientService().getPatient(patientRelationship.getPersonB().getId());
            Context.getService(ProviderManagementService.class).unassignPatientFromProvider(patient, provider, relationshipType, date);
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }
    }

    public FragmentActionResult transferPatients(@RequestParam(value = "oldProvider", required = true) Person oldProvider,
                                                 @RequestParam(value = "newProvider", required = false) Person newProvider,
                                                 @RequestParam(value = "relationshipType", required = true) RelationshipType relationshipType,
                                                 @RequestParam(value = "patientRelationships", required = false) List<Relationship> patientRelationships,
                                                 @RequestParam(value = "date", required = false) Date date) {

        // validate input (note that this validation is also handled client-side--this is just a backup)
        if (patientRelationships == null || patientRelationships.size() == 0) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.patients.required"));
        }
        else if (newProvider == null) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.newProvider.required"));
        }
        else if (date == null) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.transferDate.required"));
        }
        else if (ProviderManagementUtils.clearTimeComponent(date).after(new Date())) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.transferDate.notInFuture"));
        }

        // attempt to transfer all the patients
        try {
            List<Patient> patients = new ArrayList<Patient>();

            for (Relationship patientRelationship : patientRelationships) {
                patients.add(Context.getPatientService().getPatient(patientRelationship.getPersonB().getId()));
            }

            Context.getService(ProviderManagementService.class).transferPatients(patients,oldProvider, newProvider, relationshipType, date);
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }

    }

    public FragmentActionResult voidPatients(@RequestParam(value = "patientRelationships", required = false) List<Relationship> patientRelationships,
                                              @RequestParam(value = "voidReason", required = false) String voidReason) {

        // validate input (note that this validation is also handled client-side--this is just a backup)
        if (patientRelationships == null || patientRelationships.size() == 0) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.patients.required"));
        }
        else if (StringUtils.isBlank(voidReason)) {
            return new FailureResult(Context.getMessageSourceService().getMessage("providermanagement.errors.voidReason.required"));
        }

        // attempt to void all the patient relationships
        try {
            for (Relationship patientRelationship : patientRelationships) {
                Context.getPersonService().voidRelationship(patientRelationship, voidReason);
            }
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }
    }
}

