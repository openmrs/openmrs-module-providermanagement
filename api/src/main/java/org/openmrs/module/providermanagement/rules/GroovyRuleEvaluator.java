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

package org.openmrs.module.providermanagement.rules;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.providermanagement.api.ProviderManagementService;

import java.util.Date;
import java.util.List;

public class GroovyRuleEvaluator implements RuleEvaluator {


    @Override
    public List<Person> evaluate(Rule rule, Person provider, Date date) {

        // TODO: combine this with

        // TODO: definitely need to add some error catching here!!!

        // TODO: a catch if the evaluate fails!

        Binding bindings = getBindings();
        bindings.setVariable("provider", provider);
        bindings.setVariable("date", date);

        return evaluate(rule, bindings);
    }

    @Override
    public List<Person> evaluate(Rule rule, Patient patient, RelationshipType relationshipType, Date date) {

        Binding bindings = getBindings();
        bindings.setVariable("patient", patient);
        bindings.setVariable("relationshipType", relationshipType);
        bindings.setVariable("date", date);

        return evaluate(rule, bindings);

    }

    private List<Person> evaluate(Rule rule, Binding bindings) {
        GroovyShell shell = new GroovyShell(bindings);
        List<Person> persons = (List<Person>) shell.evaluate(rule.getCriteria());

        return persons;
    }


    //TODO: add a better version of this which is driven by a config file?
    private static Binding getBindings() {
        final Binding binding = new Binding();
        binding.setVariable("administrationService", Context.getAdministrationService());
        binding.setVariable("cohortService", Context.getCohortService());
        binding.setVariable("conceptService", Context.getConceptService());
        binding.setVariable("encounterService", Context.getEncounterService());
        binding.setVariable("formService", Context.getFormService());
        binding.setVariable("localeService", Context.getLocale());
        binding.setVariable("obsService", Context.getObsService());
        binding.setVariable("orderService", Context.getOrderService());
        binding.setVariable("patientService", Context.getPatientService());
        binding.setVariable("patientSetService", Context.getPatientSetService());
        binding.setVariable("personService", Context.getPersonService());
        binding.setVariable("programService", Context.getProgramWorkflowService());
        binding.setVariable("providerService", Context.getProviderService());
        binding.setVariable("userService", Context.getUserService());
        binding.setVariable("providerManagementService", Context.getService(ProviderManagementService.class));

        return binding;
    }
}
