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
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.exception.InvalidRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.module.providermanagement.relationship.ProviderPersonRelationship;

import java.util.ArrayList;
import java.util.Arrays;
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

    /**
     * Given a Provider it returns a List of supervisors for that Provider along with information about the relationship between provider and supervisor
     * @param provider
     * @return List<ProviderPersonRelationship>
     * @throws PersonIsNotProviderException
     */
    public static List<ProviderPersonRelationship> getSupervisors(Provider provider)
            throws PersonIsNotProviderException {

        ProviderManagementService providerManagementService = Context.getService(ProviderManagementService.class);
        List<ProviderPersonRelationship> supervisors = new ArrayList<ProviderPersonRelationship>();
        Person person = provider.getPerson();
        List<Person> supervisorPersons = providerManagementService.getSupervisorsForProvider(person);
        for (Person supervisor : supervisorPersons) {
            List<Provider> providersByPerson = providerManagementService.getProvidersByPerson(supervisor, true);
            if (providersByPerson !=null && providersByPerson.size() > 0) {
                RelationshipType supervisorRelationshipType = providerManagementService.getSupervisorRelationshipType();
                Relationship supervisorRelationship = null;
                Provider supervisorProvider = providersByPerson.get(0);
                List<Relationship> relationships = Context.getPersonService().getRelationships(supervisor,
                        person, supervisorRelationshipType, null);
                if (relationships != null && relationships.size() > 0 ){
                    for (Relationship relationship : relationships) {
                        if ( ( supervisorRelationship == null && relationship.getEndDate() == null ) ||
                                (supervisorRelationship != null && relationship.getEndDate() == null
                                        && relationship.getStartDate().after(supervisorRelationship.getStartDate()))) {
                            // select the most recent active relationship
                            supervisorRelationship = relationship;
                        }
                    }
                }
                supervisors.add(new ProviderPersonRelationship(
                        supervisorProvider.getPerson(),
                        supervisorProvider.getIdentifier(),
                        supervisor.getId(),
                        supervisorRelationship,
                        supervisorRelationshipType));
            }
        }
        return supervisors;
    }

    /**
     * Given a Provider it returns a List of supervisees for the Provider along with information about the relationship between supervisor and supervisees
     * @param provider
     * @return List<ProviderPersonRelationship>
     * @throws PersonIsNotProviderException
     */
    public static List<ProviderPersonRelationship> getSupervisees(Provider provider)
            throws PersonIsNotProviderException {

        ProviderManagementService providerManagementService = Context.getService(ProviderManagementService.class);
        List<ProviderPersonRelationship> supervisees = new ArrayList<ProviderPersonRelationship>();
        Person person = provider.getPerson();
        List<Person> superviseesForSupervisor = providerManagementService.getSuperviseesForSupervisor(person);
        for (Person supervisee : superviseesForSupervisor) {
            List<Provider> providersByPerson = providerManagementService.getProvidersByPerson(supervisee, true);
            if (providersByPerson !=null && providersByPerson.size() > 0) {
                RelationshipType supervisorRelationshipType = providerManagementService.getSupervisorRelationshipType();
                Relationship supervisorRelationship = null;
                Provider supervisorProvider = providersByPerson.get(0);
                List<Relationship> relationships = Context.getPersonService().getRelationships(person, supervisee,
                        supervisorRelationshipType, null);
                if (relationships != null && relationships.size() > 0 ){
                    for (Relationship relationship : relationships) {
                        if ( ( supervisorRelationship == null && relationship.getEndDate() == null ) ||
                                (supervisorRelationship != null && relationship.getEndDate() == null
                                        && relationship.getStartDate().after(supervisorRelationship.getStartDate()))) {
                            // select the most recent active relationship
                            supervisorRelationship = relationship;
                        }
                    }
                }
                supervisees.add(new ProviderPersonRelationship(
                        supervisorProvider.getPerson(),
                        supervisorProvider.getIdentifier(),
                        supervisee.getId(),
                        supervisorRelationship,
                        supervisorRelationshipType));
            }
        }
        return supervisees;
    }

    /**
     * Given a Provider it returns a List of assigned Patients along with information about the relationship between Provider and Patient
     * @param provider
     * @return List<ProviderPersonRelationship>
     * @throws InvalidRelationshipTypeException
     * @throws PersonIsNotProviderException
     */
    public static List<ProviderPersonRelationship> getAssignedPatients(Provider provider)
            throws InvalidRelationshipTypeException, PersonIsNotProviderException {

        ProviderManagementService providerManagementService = Context.getService(ProviderManagementService.class);
        PatientService patientService = Context.getService(PatientService.class);
        List<ProviderPersonRelationship> patientsList = new ArrayList<ProviderPersonRelationship>();
        for (RelationshipType relationshipType : provider.getProviderRole().getRelationshipTypes() ) {
            if (!relationshipType.isRetired()) {
                for (Relationship relationship : providerManagementService.getPatientRelationshipsForProvider(provider.getPerson(), relationshipType, null)) {
                    if (relationship.getPersonB().isPatient()) {
                        Patient temp = patientService.getPatient(relationship.getPersonB().getId());
                        if (!temp.isVoided()) {
                            patientsList.add(new ProviderPersonRelationship(
                                    temp,
                                    (temp.getPatientIdentifier() != null) ? temp.getPatientIdentifier().getIdentifier() : null,
                                    temp.getPatientId(),
                                    relationship,
                                    relationshipType));
                        }
                    }
                }
            }
        }
        return patientsList;
    }

    /**
     * Returns a List of roles that are configured via the GP_RESTRICTED_ROLES to be included in the UI
     * @return List<String>
     */
    public static List<String> getRestrictedRolesGP() {
        List<String> restrictedRoles = null;
        String gpRestrictedRoles = Context.getAdministrationService().getGlobalProperty(ProviderManagementConstants.GP_RESTRICTED_ROLES);
        if (StringUtils.isNotBlank(gpRestrictedRoles)) {
            restrictedRoles = Arrays.asList(gpRestrictedRoles.split("\\s*,\\s*"));
        }

        return restrictedRoles;
    }
}


