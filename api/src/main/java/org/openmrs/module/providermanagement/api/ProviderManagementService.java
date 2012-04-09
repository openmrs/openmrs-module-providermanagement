/**
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
package org.openmrs.module.providermanagement.api;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.exception.*;

import java.util.Date;
import java.util.List;

/**
 * Provider Management Service
 */

public interface ProviderManagementService extends OpenmrsService {

    // TODO: add permissions
    // TODO: make sure we are handling excluding/including retired metadata in a logical manner

    /*
      * Basic methods for operating on provider roles
      */

    /**
     * Gets all Provider Roles in the database
     *
     * @param includeRetired whether or not to include retired providers
     * @return list of all provider roles in the system
     */

    public List<ProviderRole> getAllProviderRoles(boolean includeRetired);

    /**
     * Gets the provider role referenced by the specified id
     *
     * @param id
     * @return providerRole
     */
    
    public ProviderRole getProviderRole(Integer id);

    /**
     * Gets the provider role referenced by the specified uui
     *
     * @param uuid
     * @return providerRole
     */
    
    public ProviderRole getProviderRoleByUuid(String uuid);

    /**
     * Returns all the provider roles that support the specified relationship type
     * (Excludes retired provider roles)
     *
     * @param relationshipType
     * @return the provider roles that support that relationship type
     * @should throw exception if relationshipType is null
     */
    
    public List<ProviderRole> getProviderRolesByRelationshipType(RelationshipType relationshipType);

    /**
     * Returns all provider roles that are able to supervise the specified provider role
     * (Excluded retired provider roles)
     *
     * @param providerRole
     * @return the provider roles that can supervise the specified provider role
     * @should throw exception if providerRole is null
     */
    
    public List<ProviderRole> getProviderRolesBySuperviseeProviderRole(ProviderRole providerRole);

    /**
     * Saves/updates a provider role
     *
     * @param role the provider role to save
     */
    
    public void saveProviderRole(ProviderRole role);

    /**
     * Retires a provider role
     * @param role the role to retire
     * @param reason the reason the role is being retired
     */
    
    public void retireProviderRole(ProviderRole role, String reason);

    /**
     * Unretires a provider role
     * @param role the role to unretire
     */
    
    public void unretireProviderRole(ProviderRole role);
    
    /**
     * Deletes a provider role
     *
     * @param role the provider role to delete
     */
    
    public void purgeProviderRole(ProviderRole role)
            throws ProviderRoleInUseException;

    /**
     * Get all the relationship types associated with provider roles
     *
     * @param includeRetired whether or not to include retired relationship types
     * @return all the relationship types associated with provider roles
     */
    
    public List<RelationshipType> getAllProviderRoleRelationshipTypes(boolean includeRetired);

    /**
     * Basic methods for operating on providers using the new provider roles
     */

    /**
     * Returns the provider roles associated with the specified provider
     *
     * @param provider
     * @return the provider role associated with the specified provider
     */
    
    public List<ProviderRole> getProviderRoles(Person provider);

    /**
     * Assigns a provider role to a person
     *
     * @param provider the provider whose role we wish to set
     * @param role the role to set
     * @param identifier the identifier to associate with this provider/role combination (mandatory)
     */
    
    public void assignProviderRoleToPerson(Person provider, ProviderRole role, String identifier);

    /**
     * Unassigns a provider role from a person by retiring the provider associated with that role
     *
     * @param provider
     * @param role
     */
    
    public void unassignProviderRoleFromPerson(Person provider, ProviderRole role);

    /**
     * Purges a provider role from a person by purging the provider associated with that role
     *
     * @param provider
     */
    
    public void purgeProviderRoleFromPerson(Person provider, ProviderRole role);

    /**
     * Gets all providers whose role is in the list of specified roles
     *
     * @param roles
     * @return all providers with one of the specified roles
     * @should throw APIException if roles are empty or null
     */
    
    public List<Person> getProvidersByRoles(List<ProviderRole> roles);
    
    /**
     * Gets all providers with the specified role
     * (Excludes retired providers)
     *
     * @param role
     * @return list of providers with the specified role
     * @should throw APIException if role is null
     */
    
    public List<Person> getProvidersByRole(ProviderRole role);

    /**
     * Gets all providers that support the specified relationship type
     *
     * @param relationshipType
     * @return the list of providers that support the specified relationship type
     * @should throw API Exception if relationship type is null
     */
    
    public List<Person> getProvidersByRelationshipType(RelationshipType relationshipType);

    /**
     * Gets all the provider roles that can server as supervisors of the specified provider
     * 
     * @param provider
     * @return the list of provider roles that can supervise the specific provider
     * @should throw API Exception if the provider is null
     */
    
    public List<ProviderRole> getProviderRolesThatCanSuperviseThisProvider(Person provider);

    /**
     * Returns all the valid roles that the specified provider can supervise
     *
     * @param provider
     * @return all the valid roles that the specified provider can supervise
     */
    public List<ProviderRole> getProviderRolesThatProviderCanSupervise(Person provider);

     /**
     * Returns whether or not the passed person has one or more associated providers (unretired or retired)
     * (So note that a person that only is associated with retired Provider objects is still consider a "provider")
     *
     * @param person
     * @return whether or not the passed person has one or more associated providers
     */
    public boolean isProvider(Person person);

    /**
     * Returns whether or not the passed provider has the specified provider role
     *
     * @param provider
     * @param role
     * @return whether or not the passed provider has the specified provider role
     */
    public boolean hasRole(Person provider, ProviderRole role);

    /**
     * Returns true if the specified provider can support the specified relationship type, false otherwise
     *
     * @param provider
     * @param relationshipType
     * @return true if the specified provider can support the specified relationship type, false otherwise
     */
    public boolean supportsRelationshipType(Person provider, RelationshipType relationshipType);

    /**
     * Returns true if the specified supervisor can supervise the specified supervisee, false otherwise
     *
     * @param supervisor
     * @param supervisee
     * @return true if the specified supervisor can supervise the specified supervisee, false otherwise
     */
    public boolean canSupervise(Person supervisor, Person supervisee);


    /**
    * Methods for assigning patient to providers
    */

    // TODO: for assignment and unassignments, should we not allow dates in the future?  this is probably a good idea...?

    /**
     * Assigns the patient to the provider using the specified relationship type
     *
     *
     * @param patient
     * @param provider
     * @param relationshipType
     * @param date the date this relationship should start
     * @should fail if provider does not support the specified relationship type
     * @should fail if patient is null
     * @should fail if patient is voided
     * @should fail if provider is null
     * @should fail if provider is already assigned to patient
     */
    
    public void assignPatientToProvider(Patient patient, Person provider, RelationshipType relationshipType, Date date)
            throws ProviderDoesNotSupportRelationshipTypeException, PatientAlreadyAssignedToProviderException,
            PersonIsNotProviderException;

    /**
     * Assigns the patient to the provider using the specified relationship type using current date
     *
     *
     * @param patient
     * @param provider
     * @param relationshipType
     * @should fail if provider does not support the specified relationship type
     * @should fail if patient is null
     * @should fail if patient is voided
     * @should fail if provider is null
     * @should fail if provider is already assigned to patient
     */
    
    public void assignPatientToProvider(Patient patient, Person provider, RelationshipType relationshipType)
            throws ProviderDoesNotSupportRelationshipTypeException, PatientAlreadyAssignedToProviderException,
            PersonIsNotProviderException;

    /**
     * Unassigns the patient from the provider on the specified date
     *
     * @param patient
     * @param provider
     * @param relationshipType
     * @param date
     * @should fail if provider does not support the specified relationship type
     * @should fail if patient is null
     * @should fail if patient is voided
     * @should fail if provider is null
     * @should fail if provider is not associated with a person
     * @should fail if provider is already assigned to patient
     */
    
    public void unassignPatientFromProvider(Patient patient, Person provider, RelationshipType relationshipType, Date date)
            throws PatientNotAssignedToProviderException, PersonIsNotProviderException, InvalidRelationshipTypeException;

    /**
     * Unassigns the patient from the provider on the current date
     *
     * @param patient
     * @param provider
     * @param relationshipType
     * @should fail if provider does not support the specified relationship type
     * @should fail if patient is null
     * @should fail if patient is voided
     * @should fail if provider is null
     * @should fail if provider is not associated with a person
     * @should fail if provider is not assigned to patient
     */
    
    public void unassignPatientFromProvider(Patient patient, Person provider, RelationshipType relationshipType)
            throws PatientNotAssignedToProviderException, PersonIsNotProviderException, InvalidRelationshipTypeException;

    /**
     * Unassigned all patients currently assigned to this provider with the selected relationship type
     * by ending any provider-patient relationships on the current date
     * Note that this method does NOT check to make sure that the provider supports the specified relationship type
     * to allow for edge cases where a provider is somehow linked by a relationship his/her role doesn't technically support
     *
     * @param provider
     * @should fail if provider is null
     * @should fail if relationshipType is null
     * @should fail if provider is not associated with a person
     */
    
    public void unassignAllPatientsFromProvider(Person provider, RelationshipType relationshipType)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException;

    /**
     * Unassigned all patients currently assigned to this patient by ending all active provider role relationships on
     * the current date
     *
     * @param provider
     * @should fail if provider is null
     * @should fail if provider is not associated with a person
     */
    
    public void unassignAllPatientsFromProvider(Person provider)
            throws PersonIsNotProviderException;

    // TODO: we will probably need a "purge" option for purging relationships created by accident, but we should probably spec this out a bit better

    /**
     * Gets all patients that are patients of the specified provider with the specified relationship type on the specified date
     *
     * @param provider
     * @param relationshipType limits returned patients to those related to the provider by a specific relationship type (if null, returns all patients linked by any provider relationships)
     * @param date
     * @return list of patients associated with the specified provider via the specified relationship type, on the specified date
     * @should ignore voided patients
     * @should fail if provider is null
     * @should fail if relationship type is null
     * @should fail if provider not associated with person
     * @should fail if relationship type is not a provider/patient relationship type
     */
    
    public List<Patient> getPatientsOfProvider(Person provider, RelationshipType relationshipType, Date date)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException;

    /**
     * Gets all patients that are patients of the specified provider with the specified relationship type on the current date
     *
     * @param provider
     * @param relationshipType limits returned patients to those related to the provider by a specific relationship type (if null, returns all patients linked by any provider relationships)
     * @return list of patients associated with the specified provider via the specified relationship type, on the specified date
     * @should ignore voided patients
     * @should fail if provider is null
     * @should fail if relationship type is null
     * @should fail if provider not associated with person
     * @should fail if relationship type is not a provider/patient relationship type
     * @should fail if invalid relationship found
     */
    
    public List<Patient> getPatientsOfProvider(Person provider, RelationshipType relationshipType)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException;

    /**
     * Returns all the provider relationships associated with the given patient
     *
     * @param patient
     * @param provider limits returned relationships to those with the specified provider (if null, returns relationships with all providers)
     * @param relationshipType limits returned relationships to those of a specified type (if null, returns all provider relationships)
     * @param date returns only those relationships on the specified date
     * @return all the provider relationships associated with the given patient
     * @throws PersonIsNotProviderException
     * @throws InvalidRelationshipTypeException
     */
    
    public List<Relationship> getProviderRelationshipsForPatient(Patient patient, Person provider, RelationshipType relationshipType, Date date)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException;

    /**
     * Returns all the provider relationships associated with the given patient on the current date
     *
     * @param patient
     * @param provider limits returned relationships to those with the specified provider (if null, returns relationships with all providers)
     * @param relationshipType limits returned relationships to those of a specified type (if null, returns all provider relationships)
     * @return all the provider relationships associated with the given patient
     * @throws PersonIsNotProviderException
     * @throws InvalidRelationshipTypeException
     */
    
    public List<Relationship> getProviderRelationshipsForPatient(Patient patient, Person provider, RelationshipType relationshipType)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException;


    /**
     * Returns all providers associated with the given patient on the specified date
     *
     * @param patient
     * @param relationshipType limits returned providers to those linked by a specific type (if null, returns all providers)
     * @param date returns only those relationships on the specified date
     * @return all providers associated with the given patient on the specified date
     * @throws InvalidRelationshipTypeException
     */
    
    public List<Person> getProvidersForPatient(Patient patient, RelationshipType relationshipType, Date date)
            throws InvalidRelationshipTypeException;

    /**
     * Returns all providers associated with the given patient on the specified date
     *
     * @param patient
     * @param relationshipType limits returned providers to those linked by a specific type (if null, returns all providers)
     * @return all providers associated with the given patient on the specified date
     * @throws InvalidRelationshipTypeException
     */
    
    public List<Person> getProvidersForPatient(Patient patient, RelationshipType relationshipType)
            throws InvalidRelationshipTypeException;

    /**
     * Transfers all patients currently assigned to the source provider with the specified relationship type to the destination provider
     * (ie., unassigns all patients with the specified relationship type from the source provider and assigns them to the destination provider)
     *
     * @param sourceProvider
     * @param destinationProvider
     * @param relationshipType
     * @should fail if sourceProvider is null
     * @should fail if destinationProvider is null
     * @should fail if sourceProvider is not associated with a person
     * @should fail if destinationProvider is not associated with a person
     * @should fail if relationshipType is null
     */
    
    public void transferAllPatients(Person sourceProvider, Person destinationProvider, RelationshipType relationshipType)
            throws ProviderDoesNotSupportRelationshipTypeException, SourceProviderSameAsDestinationProviderException,
            PersonIsNotProviderException, InvalidRelationshipTypeException;

    /**
     * Transfers all patients (of any relationship type) currently assigned to the source provider to the destination provider
     * (ie., unassigns all patients from the source provider and assigns them to the destination provider)
     *
     * @param sourceProvider
     * @param destinationProvider
     * @should fail if sourceProvider is null
     * @should fail if destinationProvider is null
     * @should fail if sourceProvider is not associated with a person
     * @should fail if destinationProvider is not associated with a person
     * @should fail if relationshipType is null
     * @should fail if source provider equals destination provider
     * @should fail if destination provider dose not support a relationship type that exists between source provider and patient
     * @should not fail if destination provider is already associated with patient
     */
    
    public void transferAllPatients(Person sourceProvider, Person destinationProvider)
            throws ProviderDoesNotSupportRelationshipTypeException, SourceProviderSameAsDestinationProviderException,
            PersonIsNotProviderException;


    /**
     * Methods that handle supervisee to supervisor relationships
     */

    public RelationshipType getSupervisorRelationshipType();

    /**
     * Assigns the provider to the supervisor on the specified date
     *
     * @param provider
     * @param supervisor
     * @param date
     * @should fail if provider is null
     * @should fail if supervisor is null
     * @should fail if provider is not a provider
     * @should fail if supervisor is not a provider
     * @should fail if supervisor's role(s) do not support any of supervisee's roles
     * @should fail if provider is already assigned to supervisor
     */
    
    public void assignProviderToSupervisor(Person provider, Person supervisor, Date date)
            throws PersonIsNotProviderException, InvalidSupervisorException,
            ProviderAlreadyAssignedToSupervisorException;

    /**
     * Assigns the provider to the supervisor on the current date
     *
     * @param provider
     * @param supervisor
     * @should fail if provider is null
     * @should fail if supervisor is null
     * @should fail if provider is not a provider
     * @should fail if supervisor is not a provider
     * @should fail if supervisor's role(s) do not support any of supervisee's roles
     * @should fail if provider is already assigned to supervisor
     */
    
    public void assignProviderToSupervisor(Person provider, Person supervisor)
            throws PersonIsNotProviderException, InvalidSupervisorException,
            ProviderAlreadyAssignedToSupervisorException;

    /**
     * Unassigns the provider from the supervisor on the specified date
     *
     * @param provider
     * @param supervisor
     * @param date
     * @should fail if provider is null
     * @should fail if supervisor is null
     * @should fail if provider is not a provider
     * @should fail if supervisor is not a provider
     * @should fail if provider is not assigned to supervisor
     */
    
    public void unassignProviderFromSupervisor(Person provider, Person supervisor, Date date)
            throws PersonIsNotProviderException, ProviderNotAssignedToSupervisorException;

    /**
     * Unassigns the provider from the supervisor on the current date
     *
     * @param provider
     * @param supervisor
     * @should fail if provider is null
     * @should fail if supervisor is null
     * @should fail if provider is not a provider
     * @should fail if supervisor is not a provider
     * @should fail if provider is not assigned to supervisor
     */
    
    public void unassignProviderFromSupervisor(Person provider, Person supervisor)
            throws PersonIsNotProviderException, ProviderNotAssignedToSupervisorException;

    /**
     * Unassignes all of the supervisors currently associated with the passed provider
     *
     * @param provider
     * @should fail if provider is null
     * @should fail if provider is not a provider
     */
    
    public void unassignAllSupervisorsFromProvider(Person provider)
            throws PersonIsNotProviderException;

    /**
     * Unassignes all of the providers currently associated with the passed supervisor
     *
     * @param supervisor
     * @should fail if supervisor is null
     * @should fail if supervisor is not a provider
     */
    
    public void unassignAllProvidersFromSupervisor(Person supervisor)
            throws PersonIsNotProviderException;


    /**
     * Returns all the relationships this provider has with supervisors on the given date
     *
     * @param provider
     * @param date
     * @return all the relationships this provider has with supervisors on the given date
     * @throws PersonIsNotProviderException
     * @should fail if provider is null
     * @should fail if provider is not a provider
     */
    
    public List<Relationship> getSupervisorRelationshipsForProvider(Person provider, Date date)
            throws PersonIsNotProviderException;

    /**
     * Returns all the relationships this provider has with supervisors on the current date
     *
     * @param provider
     * @return all the relationships this provider has with supervisors on the current date
     * @throws PersonIsNotProviderException
     * @should fail if provider is null
     * @should fail if provider is not a provider
     */
    
    public List<Relationship> getSupervisorRelationshipsForProvider(Person provider)
            throws PersonIsNotProviderException;
    
    /**
     * Returns all the providers that that given provider supervises on the given date
     *
     * @param provider
     * @param date
     * @return all the providers that that given provider supervises on the given date
     * @should fail if provider is null
     * @should fail if provider is not a provider
     */
    
    public List<Person> getSupervisorsForProvider(Person provider, Date date)
            throws PersonIsNotProviderException;

    /**
     * Returns all the providers that that given provider supervises on the current date
     *
     * @param provider
     * @return all the providers that that given provider supervises on the current date
     * @should fail if provider is null
     * @should fail if provider is not a provider
     */
    
    public List<Person> getSupervisorsForProvider(Person provider)
            throws PersonIsNotProviderException;

    /**
     * Returns all the relationships this supervisor has with supervisees on the specified date
     *
     * @param supervisor
     * @param date
     * @return all the relationships this supervisor has with supervisees on the specified date
     * @throws PersonIsNotProviderException
     * @should fail if provider is null
     * @should fail if provider is not a provider
     */
    
    public List<Relationship> getSuperviseeRelationshipsForSupervisor(Person supervisor, Date date)
            throws PersonIsNotProviderException;

    /**
     * Returns all the relationships this supervisor has with supervisees on the current date
     *
     * @param supervisor
     * @return all the relationships this supervisor has with supervisees on the cuirrent date
     * @throws PersonIsNotProviderException
     * @should fail if provider is null
     * @should fail if provider is not a provider
     */
    
    public List<Relationship> getSuperviseeRelationshipsForSupervisor(Person supervisor)
            throws PersonIsNotProviderException;

    /**
     * Returns all the persons this supervisor supervises on the specified date
     *
     * @param supervisor
     * @param date
     * @return all the persons this supervisor supervises on the specified date
     * @throws PersonIsNotProviderException
     * @should fail if provider is null
     * @should fail if provider is not a provider
     */
    
    public List<Person> getSuperviseesForSupervisor(Person supervisor, Date date)
            throws PersonIsNotProviderException;

    /**
     * Returns all the persons this supervisor supervises on the current date
     *
     * @param supervisor
     * @return all the persons this supervisor supervises on the current date
     * @throws PersonIsNotProviderException
     * @should fail if provider is null
     * @should fail if provider is not a provider
     */
    
    public List<Person> getSuperviseesForSupervisor(Person supervisor)
            throws PersonIsNotProviderException;


    /**
     * Replacement for ProviderService.getProvidersByPerson to fetch new expanded provider model
     * Should generally only be used internally, since the idea is this API "hides" knowledge of the Provider object
     */

    public List<Provider> getProvidersByPerson(Person person, boolean includeRetired);
}