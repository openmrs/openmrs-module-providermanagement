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
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.providermanagement.ProviderManagementConstants;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.exception.DateCannotBeInFutureException;
import org.openmrs.module.providermanagement.exception.InvalidRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.InvalidSupervisorException;
import org.openmrs.module.providermanagement.exception.PatientAlreadyAssignedToProviderException;
import org.openmrs.module.providermanagement.exception.PatientNotAssignedToProviderException;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.module.providermanagement.exception.ProviderAlreadyAssignedToSupervisorException;
import org.openmrs.module.providermanagement.exception.ProviderDoesNotSupportRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.ProviderNotAssignedToSupervisorException;
import org.openmrs.module.providermanagement.exception.ProviderRoleInUseException;
import org.openmrs.module.providermanagement.exception.SourceProviderSameAsDestinationProviderException;

import java.util.Date;
import java.util.List;

/**
 * Provider Management Service
 */

public interface ProviderManagementService extends OpenmrsService {

    // TODO: make sure we are handling excluding/including retired metadata in a logical manner

    /*
      * Basic methods for operating on provider roles
      */

    /**
     * Gets all Provider Roles in the database
     *
     * @param includeRetired whether or not to include retired provider roles
     * @return list of all provider roles in the system
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<ProviderRole> getAllProviderRoles(boolean includeRetired);

    /**
     * Gets restricted Provider Roles in the database
     *
     * @param includeRetired whether or not to include retired provider roles
     * @return list of restricted provider roles in the system
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<ProviderRole> getRestrictedProviderRoles(boolean includeRetired);

    /**
     * Gets the provider role referenced by the specified id
     *
     * @param id
     * @return providerRole
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public ProviderRole getProviderRole(Integer id);

    /**
     * Gets the provider role referenced by the specified uui
     *
     * @param uuid
     * @return providerRole
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public ProviderRole getProviderRoleByUuid(String uuid);

    /**
     * Returns all the provider roles that support the specified relationship type
     * (Excludes retired provider roles)
     *
     * @param relationshipType
     * @return the provider roles that support that relationship type
     * @should throw exception if relationshipType is null
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<ProviderRole> getProviderRolesByRelationshipType(RelationshipType relationshipType);

    /**
     * Returns all provider roles that are able to supervise the specified provider role
     * (Excludes retired provider roles)
     *
     * @param providerRole
     * @return the provider roles that can supervise the specified provider role
     * @should throw exception if providerRole is null
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<ProviderRole> getProviderRolesBySuperviseeProviderRole(ProviderRole providerRole);

    /**
     * Saves/updates a provider role
     *
     * @param role the provider role to save
     * @return the saved provider role
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public ProviderRole saveProviderRole(ProviderRole role);

    /**
     * Retires a provider role
     * @param role the role to retire
     * @param reason the reason the role is being retired
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void retireProviderRole(ProviderRole role, String reason);

    /**
     * Unretires a provider role
     * @param role the role to unretire
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void unretireProviderRole(ProviderRole role);
    
    /**
     * Deletes a provider role
     *
     * @param role the provider role to delete
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void purgeProviderRole(ProviderRole role)
            throws ProviderRoleInUseException;

    /**
     * Get all the relationship types associated with provider roles
     *
     * @param includeRetired whether or not to include retired relationship types
     * @return all the relationship types associated with provider roles
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<RelationshipType> getAllProviderRoleRelationshipTypes(boolean includeRetired);

    /**
     * Basic methods for operating on providers using the new provider roles
     */

    /**
     * Gets the list of providers that match a specified name OR identifier, restricting based on specified provider roles
     * (If query is null, empty list is returned)
     *
     * @param query name or identifier to search on (does a like 'query%' search)
     * @param providerRoles restrict results to providers with at least one of these roles
     * @param includeRetired whether or not to include retired providers
     * @should return empty list if query null
     * @return result list of providers
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Person> getProvidersAsPersons(String query, List<ProviderRole> providerRoles, Boolean includeRetired);

    /**
     * Gets the list of providers that match the specified name, identifier, and provider roles
     * (If any field is null it is ignored)
     *
     * @param name name to search on (does an ilike 'name%' search against name fields)
     * @param identifier provider identifier (does a ilike 'identifier%' search)
     * @param providerRoles restrict results to providers with at least one of these roles
     * @param includeRetired whether or not to include retired providers
     * @return result list of providers
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Person> getProvidersAsPersons(String name, String identifier, List<ProviderRole> providerRoles, Boolean includeRetired);

    /**
     * Gets the list of providers that match the specified name, identifier, and provider roles
     * (If any field is null it is ignored)
     *
     * @param name name to search on (does an ilike 'name%' search against name fields)
     * @param identifier provider identifier (does an ilike 'identifier%' search)
     * @param personAddress address to match on (does an ilike 'addressField%' search against each field that is not null)
     * @param personAttribute person attribute to match on
     * @param providerRoles restrict results to providers with at least one of these roles
     * @param includeRetired whether or not to include retired providers
     * @return result list of providers
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
     public List<Person> getProvidersAsPersons(String name, String identifier, PersonAddress personAddress, PersonAttribute personAttribute, List<ProviderRole> providerRoles, Boolean includeRetired);

    /**
     * Returns the provider roles associated with the specified provider
     *
     * @param provider
     * @return the provider role associated with the specified provider
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<ProviderRole> getProviderRoles(Person provider);

    /**
     * Assigns a provider role to a person
     *
     * @param provider the provider whose role we wish to set
     * @param role the role to set
     * @param identifier the identifier to associate with this provider/role combination (mandatory)
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void assignProviderRoleToPerson(Person provider, ProviderRole role, String identifier);

    /**
     * Unassigns a provider role from a person by retiring the provider associated with that role
     *
     * @param provider
     * @param role
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void unassignProviderRoleFromPerson(Person provider, ProviderRole role);

    /**
     * Purges a provider role from a person by purging the provider associated with that role
     *
     * @param provider
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void purgeProviderRoleFromPerson(Person provider, ProviderRole role);

    /**
     * Gets all providers whose role is in the list of specified roles
     *
     * @param roles
     * @return all providers with one of the specified roles
     * @should throw APIException if roles are empty or null
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Person> getProvidersAsPersonsByRoles(List<ProviderRole> roles);

    /**
     * Gets all providers whose role is in the list of specified roles
     *
     * @param roles
     * @return all providers with one of the specified roles
     * @should throw APIException if roles are empty or null
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Provider> getProvidersByRoles(List<ProviderRole> roles);

    /**
     * Gets all providers with the specified role
     * (Excludes retired providers)
     *
     * @param role
     * @return list of providers with the specified role
     * @should throw APIException if role is null
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Person> getProvidersAsPersonsByRole(ProviderRole role);

    /**
     * Gets all providers that support the specified relationship type
     *  (Excludes retired providers)
     *
     * @param relationshipType
     * @return the list of providers that support the specified relationship type
     * @should throw API Exception if relationship type is null
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Person> getProvidersAsPersonsByRelationshipType(RelationshipType relationshipType);

    /**
     * Gets all the provider roles that can server as supervisors of the specified provider
     *  (Excludes retired providers)
     * 
     * @param provider
     * @return the list of provider roles that can supervise the specific provider
     * @should throw API Exception if the provider is null
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<ProviderRole> getProviderRolesThatCanSuperviseThisProvider(Person provider);

    /**
     * Returns all the valid roles that the specified provider can supervise
     *
     * @param provider
     * @return all the valid roles that the specified provider can supervise
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<ProviderRole> getProviderRolesThatProviderCanSupervise(Person provider);

     /**
     * Returns whether or not the passed person has one or more associated providers (unretired or retired)
     * (So note that a person that only is associated with retired Provider objects is still consider a "provider")
     *
     * @param person
     * @return whether or not the passed person has one or more associated providers
     */
     @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public boolean isProvider(Person person);

    /**
     * Returns whether or not the passed provider has the specified provider role
     *
     * @param provider
     * @param role
     * @return whether or not the passed provider has the specified provider role
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public boolean hasRole(Person provider, ProviderRole role);

    /**
     * Returns true if the specified provider can support the specified relationship type, false otherwise
     *
     * @param provider
     * @param relationshipType
     * @return true if the specified provider can support the specified relationship type, false otherwise
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public boolean supportsRelationshipType(Person provider, RelationshipType relationshipType);

    /**
     * Returns true if the specified supervisor can supervise the specified supervisee, false otherwise
     *
     * @param supervisor
     * @param supervisee
     * @return true if the specified supervisor can supervise the specified supervisee, false otherwise
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public boolean canSupervise(Person supervisor, Person supervisee);


    /**
    * Methods for assigning patient to providers
    */

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
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void assignPatientToProvider(Patient patient, Person provider, RelationshipType relationshipType, Date date)
            throws ProviderDoesNotSupportRelationshipTypeException, PatientAlreadyAssignedToProviderException,
            PersonIsNotProviderException, DateCannotBeInFutureException;

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
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
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
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void unassignPatientFromProvider(Patient patient, Person provider, RelationshipType relationshipType, Date date)
            throws PatientNotAssignedToProviderException, PersonIsNotProviderException, InvalidRelationshipTypeException,
                DateCannotBeInFutureException;

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
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
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
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
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
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void unassignAllPatientsFromProvider(Person provider)
            throws PersonIsNotProviderException;

    // TODO: we will probably need a "purge" option for purging relationships created by accident, (will need to be implemented when we take on PROV-1)

    // TODO: unit test these two methods (see PROV-51)

    /**
     * Returns the (non-voided) patient relationships for a specified provider on the specified date
     *
     * @param provider the provider
     * @param relationshipType the relationshipType (if null, tests against all provider/patient relationship types--ie, relationship types associated wtih at least one provider role)
     * @param date the date the relationship (if null, return all relationships of the given type regardless of date)
     * @return
     * @throws PersonIsNotProviderException
     * @throws InvalidRelationshipTypeException
     * @should fail if provider null
     * @should fail if relationshipType is not a valid provider/patient relationship type
     * @should return all patient relationships of the specified relationshipType if date is null
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Relationship> getPatientRelationshipsForProvider(Person provider, RelationshipType relationshipType, Date date)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException;

    /**
     * Returns the (non-voided) patient relationships for a specified provider on the specified date
     *
     * @param provider the provider
     * @param relationshipType the relationshipType (if null, tests against all provider/patient relationship types--ie, relationship types associated wtih at least one provider role)
     * @return
     * @throws PersonIsNotProviderException
     * @throws InvalidRelationshipTypeException
     * @should fail if provider null
     * @should fail if relationshipType is not a valid provider/patient relationship type
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Relationship> getPatientRelationshipsForProvider(Person provider, RelationshipType relationshipType)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException;

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
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Patient> getPatientsOfProvider(Person provider, RelationshipType relationshipType, Date date)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException;

    /**
     * Gets all patients that are patients (current and historical) of the specified provider with the specified relationship type
     *
     * @param provider
     * @param relationshipType limits returned patients to those related to the provider by a specific relationship type (if null, returns all patients linked by any provider relationships)
     * @return list of patients associated with the specified provider via the specified relationship type
     * @should ignore voided patients
     * @should fail if provider is null
     * @should fail if relationship type is null
     * @should fail if provider not associated with person
     * @should fail if relationship type is not a provider/patient relationship type
     * @should fail if invalid relationship found
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Patient> getPatientsOfProvider(Person provider, RelationshipType relationshipType)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException;

    /**
     * Gets a count of all the patients that are patients of the specified provider with the specified relationship type on the specified date
     *
     * @param provider
     * @param relationshipType limits returned patients to those related to the provider by a specific relationship type (if null, returns all patients linked by any provider relationships)
     * @param date
     * @return count of patients associated with the specified provider via the specified relationship type, on the specified date
     * @throws PersonIsNotProviderException
     * @throws InvalidRelationshipTypeException
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public int getPatientsOfProviderCount(Person provider, RelationshipType relationshipType, Date date)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException;


    /**
     * Returns all the provider relationships associated with the given patient
     *
     * @param patient
     * @param provider limits returned relationships to those with the specified provider (if null, returns relationships with all providers)
     * @param relationshipType limits returned relationships to those of a specified type (if null, returns all provider relationships)
     * @param date returns only those relationships on the specified date (default is current date)
     * @return all the provider relationships associated with the given patient
     * @throws PersonIsNotProviderException
     * @throws InvalidRelationshipTypeException
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Relationship> getProviderRelationshipsForPatient(Patient patient, Person provider, RelationshipType relationshipType, Date date)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException;

    /**
     * Returns all the provider relationships (current and historical) associated with the given patient
     *
     * @param patient
     * @param provider limits returned relationships to those with the specified provider (if null, returns relationships with all providers)
     * @param relationshipType limits returned relationships to those of a specified type (if null, returns all provider relationships)
     * @return all the provider relationships associated with the given patient
     * @throws PersonIsNotProviderException
     * @throws InvalidRelationshipTypeException
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Relationship> getProviderRelationshipsForPatient(Patient patient, Person provider, RelationshipType relationshipType)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException;

    /**
     * Returns active(endDate = null) provider relationships associated with the given patient
     * @param patient
     * @param provider limits returned relationships to those with the specified provider (if null, returns relationships with all providers)
     * @param relationshipType limits returned relationships to those of a specified type (if null, returns all provider relationships)
     * @param date returns only those relationships on the specified date
     * @return active provider relationships associated with the given patient
     * @throws PersonIsNotProviderException
     * @throws InvalidRelationshipTypeException
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Relationship> getActiveProviderRelationshipsForPatient(Patient patient, Person provider, RelationshipType relationshipType, Date date)
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
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Person> getProvidersAsPersonsForPatient(Patient patient, RelationshipType relationshipType, Date date)
            throws InvalidRelationshipTypeException;

    /**
     * Returns all providers (current and historical) associated with the given patient
     *
     * @param patient
     * @param relationshipType limits returned providers to those linked by a specific type (if null, returns all providers)
     * @return all providers associated with the given patient
     * @throws InvalidRelationshipTypeException
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Person> getProvidersAsPersonsForPatient(Patient patient, RelationshipType relationshipType)
            throws InvalidRelationshipTypeException;


    /**
     * Transfers the selected patients from the source provider to the destination provider for the specified relationship type on the specified date)
     * (ie., unassigns patients with the specified relationship type from the source provider and assigns them to the destination provider)
     * (uses current date if date = null)
     *
     * @param patients
     * @param sourceProvider
     * @param destinationProvider
     * @param relationshipType
     * @param date
     * @throws ProviderDoesNotSupportRelationshipTypeException
     * @throws SourceProviderSameAsDestinationProviderException
     * @throws PersonIsNotProviderException
     * @throws InvalidRelationshipTypeException
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void transferPatients(List<Patient> patients, Person sourceProvider, Person destinationProvider, RelationshipType relationshipType, Date date)
            throws ProviderDoesNotSupportRelationshipTypeException, SourceProviderSameAsDestinationProviderException,
            PersonIsNotProviderException, InvalidRelationshipTypeException, PatientNotAssignedToProviderException,
            DateCannotBeInFutureException;

    /**
     * Transfers the selected patients from the source provider to the destination provider for the specified relationship type (on the current date)
     * (ie., unassigns patients with the specified relationship type from the source provider and assigns them to the destination provider)
     *
     * @param patients
     * @param sourceProvider
     * @param destinationProvider
     * @param relationshipType
     * @throws ProviderDoesNotSupportRelationshipTypeException
     * @throws SourceProviderSameAsDestinationProviderException
     * @throws PersonIsNotProviderException
     * @throws InvalidRelationshipTypeException
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void transferPatients(List<Patient> patients, Person sourceProvider, Person destinationProvider, RelationshipType relationshipType)
            throws ProviderDoesNotSupportRelationshipTypeException, SourceProviderSameAsDestinationProviderException,
            PersonIsNotProviderException, InvalidRelationshipTypeException, PatientNotAssignedToProviderException,
            DateCannotBeInFutureException;

    /**
     * Transfers all patients currently assigned to the source provider with the specified relationship type to the destination provider on the specified date
     * (ie., unassigns all patients with the specified relationship type from the source provider and assigns them to the destination provider)
     * (uses current date if date = null)
     *
     * @param sourceProvider
     * @param destinationProvider
     * @param relationshipType
     * @param date
     * @should fail if sourceProvider is null
     * @should fail if destinationProvider is null
     * @should fail if sourceProvider is not associated with a person
     * @should fail if destinationProvider is not associated with a person
     * @should fail if relationshipType is null
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void transferAllPatients(Person sourceProvider, Person destinationProvider, RelationshipType relationshipType, Date date)
            throws ProviderDoesNotSupportRelationshipTypeException, SourceProviderSameAsDestinationProviderException,
            PersonIsNotProviderException, InvalidRelationshipTypeException, DateCannotBeInFutureException;

    /**
     * Transfers all patients currently assigned to the source provider with the specified relationship type to the destination provider (on the current date)
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
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void transferAllPatients(Person sourceProvider, Person destinationProvider, RelationshipType relationshipType)
            throws ProviderDoesNotSupportRelationshipTypeException, SourceProviderSameAsDestinationProviderException,
            PersonIsNotProviderException, InvalidRelationshipTypeException, DateCannotBeInFutureException;

    /**
     * Transfers all patients (of any relationship type) currently assigned to the source provider to the destination provider on the specified date
     * (ie., unassigns all patients from the source provider and assigns them to the destination provider)
     * (uses current date if date = null)
     *
     * @param sourceProvider
     * @param destinationProvider
     * @param date
     * @should fail if sourceProvider is null
     * @should fail if destinationProvider is null
     * @should fail if sourceProvider is not associated with a person
     * @should fail if destinationProvider is not associated with a person
     * @should fail if relationshipType is null
     * @should fail if source provider equals destination provider
     * @should fail if destination provider dose not support a relationship type that exists between source provider and patient
     * @should not fail if destination provider is already associated with patient
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void transferAllPatients(Person sourceProvider, Person destinationProvider, Date date)
            throws ProviderDoesNotSupportRelationshipTypeException, SourceProviderSameAsDestinationProviderException,
            PersonIsNotProviderException, DateCannotBeInFutureException;


    /**
     * Transfers all patients (of any relationship type) currently assigned to the source provider to the destination provider (on the current date)
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
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void transferAllPatients(Person sourceProvider, Person destinationProvider)
            throws ProviderDoesNotSupportRelationshipTypeException, SourceProviderSameAsDestinationProviderException,
            PersonIsNotProviderException, DateCannotBeInFutureException;


    /**
     * Methods that handle supervisee to supervisor relationships
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
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
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void assignProviderToSupervisor(Person provider, Person supervisor, Date date)
            throws PersonIsNotProviderException, InvalidSupervisorException,
            ProviderAlreadyAssignedToSupervisorException, DateCannotBeInFutureException;

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
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
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
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void unassignProviderFromSupervisor(Person provider, Person supervisor, Date date)
            throws PersonIsNotProviderException, ProviderNotAssignedToSupervisorException,
                DateCannotBeInFutureException;

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
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void unassignProviderFromSupervisor(Person provider, Person supervisor)
            throws PersonIsNotProviderException, ProviderNotAssignedToSupervisorException;

    /**
     * Unassignes all of the supervisors currently associated with the passed provider
     *
     * @param provider
     * @should fail if provider is null
     * @should fail if provider is not a provider
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void unassignAllSupervisorsFromProvider(Person provider)
            throws PersonIsNotProviderException;

    /**
     * Unassignes all of the providers currently associated with the passed supervisor
     *
     * @param supervisor
     * @should fail if supervisor is null
     * @should fail if supervisor is not a provider
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
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
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Relationship> getSupervisorRelationshipsForProvider(Person provider, Date date)
            throws PersonIsNotProviderException;

    /**
     * Returns all the relationships (current and historical) this provider has with supervisors
     *
     * @param provider
     * @return all the relationships this provider has with supervisors
     * @throws PersonIsNotProviderException
     * @should fail if provider is null
     * @should fail if provider is not a provider
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
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
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Person> getSupervisorsForProvider(Person provider, Date date)
            throws PersonIsNotProviderException;

    /**
     * Returns all the providers (current and historical) that that given provider supervises
     *
     * @param provider
     * @return all the providers that that given provider supervises
     * @should fail if provider is null
     * @should fail if provider is not a provider
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
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
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Relationship> getSuperviseeRelationshipsForSupervisor(Person supervisor, Date date)
            throws PersonIsNotProviderException;

    /**
     * Returns all the relationships (current and historical) this supervisor has with supervisees
     *
     * @param supervisor
     * @return all the relationships this supervisor has with supervisees
     * @throws PersonIsNotProviderException
     * @should fail if provider is null
     * @should fail if provider is not a provider
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
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
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Person> getSuperviseesForSupervisor(Person supervisor, Date date)
            throws PersonIsNotProviderException;

    /**
     * Returns all the persons (current and historical) this supervisor supervises
     *
     * @param supervisor
     * @return all the persons this supervisor supervises
     * @throws PersonIsNotProviderException
     * @should fail if provider is null
     * @should fail if provider is not a provider
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Person> getSuperviseesForSupervisor(Person supervisor)
            throws PersonIsNotProviderException;


    /**
     * Transfers the specified supervises from the source supervisor to the destination supervisor on the specified date
     * (uses current date if date = null)
     *
     * @param supervisees
     * @param sourceSupervisor
     * @param destinationSupervisor
     * @param date
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void transferSupervisees(List<Person> supervisees, Person sourceSupervisor, Person destinationSupervisor, Date date)
            throws PersonIsNotProviderException, SourceProviderSameAsDestinationProviderException, InvalidSupervisorException,
            ProviderNotAssignedToSupervisorException, DateCannotBeInFutureException;


    /**
     * Transfers the specified supervises from the source supervisor to the destination supervisor (on the current date)
     *
     * @param supervisees
     * @param sourceSupervisor
     * @param destinationSupervisor
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void transferSupervisees(List<Person> supervisees, Person sourceSupervisor, Person destinationSupervisor)
            throws PersonIsNotProviderException, SourceProviderSameAsDestinationProviderException, InvalidSupervisorException,
            ProviderNotAssignedToSupervisorException, DateCannotBeInFutureException;

    /**
     * Transfers all supervisees from the source supervisor to the destination supervisor on the specified date
     * (uses current date if date = null)
     *
     * @param sourceSupervisor
     * @param destinationSupervisor
     * @param date
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void transferAllSupervisees(Person sourceSupervisor, Person destinationSupervisor, Date date)
            throws PersonIsNotProviderException, SourceProviderSameAsDestinationProviderException, InvalidSupervisorException,
            DateCannotBeInFutureException;

    /**
     * Transfers all supervisees from the source supervisor to the destination supervisor (on the current date)
     *
     * @param sourceSupervisor
     * @param destinationSupervisor
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void transferAllSupervisees(Person sourceSupervisor, Person destinationSupervisor)
            throws PersonIsNotProviderException, SourceProviderSameAsDestinationProviderException, InvalidSupervisorException,
            DateCannotBeInFutureException;

    /**
     * Replacement for ProviderService.getProvidersByPerson to fetch new expanded provider model
     * Should generally only be used internally, since the idea is this API "hides" knowledge of the Provider object
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Provider> getProvidersByPerson(Person person, boolean includeRetired);
}