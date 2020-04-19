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
package org.openmrs.module.providermanagement.api.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.providermanagement.ProviderManagementConstants;
import org.openmrs.module.providermanagement.ProviderManagementUtils;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.api.db.ProviderManagementDAO;
import org.openmrs.module.providermanagement.comparator.PersonByFirstNameComparator;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * It is a default implementation of {@link ProviderManagementService}.
 */
public class ProviderManagementServiceImpl extends BaseOpenmrsService implements ProviderManagementService {

    // TODO: (??? --not sure what this comment means anymore?) add checks to make sure person is not voided automatically when appropriate (in the assignment classes?)

	protected final Log log = LogFactory.getLog(this.getClass());
	
	private ProviderManagementDAO dao;

    private static RelationshipType supervisorRelationshipType = null;
	
	/**
     * @param dao the dao to set
     */
    public void setDao(ProviderManagementDAO dao) {
	    this.dao = dao;
    }

    /**
     * @return the dao
     */
    public ProviderManagementDAO getDao() {
	    return dao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderRole> getAllProviderRoles(boolean includeRetired) {
        return dao.getAllProviderRoles(includeRetired);
    }

    /**
     * Gets restricted Provider Roles in the database
     *
     * @param includeRetired whether or not to include retired provider roles
     * @return list of restricted provider roles in the system
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProviderRole> getRestrictedProviderRoles(boolean includeRetired) {

        List<ProviderRole> uiProviderRoles = new ArrayList<ProviderRole>();
        List<ProviderRole> allProviderRoles = getAllProviderRoles(includeRetired);
        if (allProviderRoles != null && allProviderRoles.size() > 0 ) {
            List<String> restrictedRolesGP = ProviderManagementUtils.getRestrictedRolesGP();
            if (restrictedRolesGP != null && restrictedRolesGP.size() > 0) {
                for (ProviderRole role : allProviderRoles) {
                    for (String gp : restrictedRolesGP) {
                        if (StringUtils.equals(role.getUuid(), gp)) {
                            uiProviderRoles.add(role);
                            break;
                        }
                    }
                }
                return uiProviderRoles;
            }
        }
        return allProviderRoles;
    }


    @Override
    @Transactional(readOnly = true)
    public ProviderRole getProviderRole(Integer id) {
        return dao.getProviderRole(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProviderRole getProviderRoleByUuid(String uuid) {
        return dao.getProviderRoleByUuid(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderRole> getProviderRolesByRelationshipType(RelationshipType relationshipType) {
        if (relationshipType == null) {
            throw new APIException("relationshipType cannot be null");
        }
        else {
            return dao.getProviderRolesByRelationshipType(relationshipType);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderRole> getProviderRolesBySuperviseeProviderRole(ProviderRole providerRole) {
        if (providerRole == null) {
            throw new APIException("providerRole cannot be null");
        }
        else {
            return dao.getProviderRolesBySuperviseeProviderRole(providerRole);
        }
    }

    @Override
    @Transactional
    public ProviderRole saveProviderRole(ProviderRole role) {
        return dao.saveProviderRole(role);
    }

    @Override
    @Transactional
    public void retireProviderRole(ProviderRole role, String reason) {
        // BaseRetireHandler handles retiring the object
        dao.saveProviderRole(role);
    }

    @Override
    @Transactional
    public void unretireProviderRole(ProviderRole role) {
        // BaseUnretireHandler handles unretiring the object
        dao.saveProviderRole(role);
    }

    @Override
    @Transactional
    public void purgeProviderRole(ProviderRole role)
            throws ProviderRoleInUseException {

        // first, remove this role as supervisee from any roles that can supervise it
        for (ProviderRole r : getProviderRolesBySuperviseeProviderRole(role)) {
            r.getSuperviseeProviderRoles().remove(role);
            Context.getService(ProviderManagementService.class).saveProviderRole(r);   // call through service so AOP save handler picks this up
        }

        try {
            dao.deleteProviderRole(role);
            Context.flushSession();  // shouldn't really have to do this, but we do to force a commit so that the exception will be thrown if necessary
        }
        catch (ConstraintViolationException e) {
            throw new ProviderRoleInUseException("Cannot purge provider role. Most likely it is currently linked to an existing provider ", e);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipType> getAllProviderRoleRelationshipTypes(boolean includeRetired) {

        Set<RelationshipType> relationshipTypes = new HashSet<RelationshipType>();

        for (ProviderRole providerRole : getAllProviderRoles(includeRetired)) {
            
            if (includeRetired) {
                relationshipTypes.addAll(providerRole.getRelationshipTypes());
            }
            // filter out any retired relationships
            else {
                relationshipTypes.addAll(CollectionUtils.select(providerRole.getRelationshipTypes(), new Predicate() {
                    @Override
                    public boolean evaluate(Object relationshipType) {
                        return !((RelationshipType) relationshipType).getRetired();
                    }
                }));
            }
        }

        return new ArrayList<RelationshipType>(relationshipTypes);
    }

    @Override
    public List<Person> getProvidersAsPersons(String query, List<ProviderRole> providerRoles, Boolean includeRetired) {

        // return empty list if no query
        if (query == null || query.length() == 0) {
            return new ArrayList<Person>();
        }

        List<Person> nameMatches = getProvidersAsPersons(query, null, providerRoles, includeRetired);
        List<Person> identifierMatches = getProvidersAsPersons(null, query, providerRoles, includeRetired);

        if (identifierMatches == null || identifierMatches.size() == 0) {
            return nameMatches;
        }
        else if (nameMatches == null || nameMatches.size() == 0) {
            return identifierMatches;
        }
        else {
            // do a union
            // TODO: how is the performance of this?
            nameMatches.removeAll(identifierMatches);
            identifierMatches.addAll(nameMatches);
            Collections.sort(identifierMatches, new PersonByFirstNameComparator());
            return identifierMatches;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> getProvidersAsPersons(String name, String identifier, List<ProviderRole> providerRoles, Boolean includeRetired) {
        if (providerRoles == null) {
            providerRoles = Collections.emptyList();
        }

        if (includeRetired == null) {
            throw new RuntimeException("include retired must be specified when searching for providers");
        }

        return getProvidersAsPersons(name, identifier, null, null, providerRoles, includeRetired);
    }

    @Override
    public List<Person> getProvidersAsPersons(String name, String identifier, PersonAddress personAddress, PersonAttribute personAttribute, List<ProviderRole> providerRoles, Boolean includeRetired) {
        return dao.getProviders(name, identifier, personAddress, personAttribute, providerRoles, includeRetired);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderRole> getProviderRoles(Person provider) {
        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (!isProvider(provider)) {
            // return empty list if this person is not a provider
            return new ArrayList<ProviderRole>();
        }

        // otherwise, collect all the roles associated with this provider
        // (we use a set to avoid duplicates at this point)
        Set<ProviderRole> providerRoles = new HashSet<ProviderRole>();

        Collection<Provider> providers = getProvidersByPerson(provider, false);

        for (Provider p : providers) {
            if (p.getProviderRole() != null) {
                providerRoles.add(p.getProviderRole());
            }
        }

        return new ArrayList<ProviderRole>(providerRoles);
    }

    @Override
    @Transactional
    public void assignProviderRoleToPerson(Person provider, ProviderRole role, String identifier) {

        if (provider == null) {
            throw new APIException("Cannot set provider role: provider is null");
        }
        
        if (role == null) {
            throw new APIException("Cannot set provider role: role is null");
        }

        if (provider.isVoided()) {
            throw new APIException("Cannot set provider role: underlying person has been voided");
        }

        if (hasRole(provider,role)) {
            // if the provider already has this role, do nothing
            return;
        }
        
        // create a new provider object and associate it with this person
        Provider p = new Provider();
        p.setPerson(provider);
        p.setIdentifier(identifier);
        p.setProviderRole(role);
        Context.getProviderService().saveProvider(p);
    }

    @Override
    @Transactional
    public void unassignProviderRoleFromPerson(Person provider, ProviderRole role) {

        if (provider == null) {
            throw new APIException("Cannot set provider role: provider is null");
        }

        if (role == null) {
            throw new APIException("Cannot set provider role: role is null");
        }

        if (!hasRole(provider,role)) {
            // if the provider doesn't have this role, do nothing
            return;
        }

        // note that we don't check to make sure this provider is a person

        // iterate through all the providers and retire any with the specified role
        for (Provider p : getProvidersByPerson(provider, true)) {
            if (p.getProviderRole().equals(role)) {
                Context.getProviderService().retireProvider(p, "removing provider role " + role + " from " + provider);
            }
        }
    }

    @Override
    @Transactional
    public void purgeProviderRoleFromPerson(Person provider, ProviderRole role) {
        if (provider == null) {
            throw new APIException("Cannot set provider role: provider is null");
        }

        if (role == null) {
            throw new APIException("Cannot set provider role: role is null");
        }

        if (!hasRole(provider,role)) {
            // if the provider doesn't have this role, do nothing
            return;
        }

        // note that we don't check to make sure this provider is a person

        // iterate through all the providers and purge any with the specified role
        for (Provider p : getProvidersByPerson(provider, true)) {
            if (p.getProviderRole().equals(role)) {
                Context.getProviderService().purgeProvider(p);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> getProvidersAsPersonsByRoles(List<ProviderRole> roles) {
        return providersToPersons(getProvidersByRoles(roles));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Provider> getProvidersByRoles(List<ProviderRole> roles) {
        // not allowed to pass null or empty set here
        if (roles == null || roles.isEmpty()) {
            throw new APIException("Roles cannot be null or empty");
        }
        return dao.getProvidersByProviderRoles(roles, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> getProvidersAsPersonsByRole(ProviderRole role) {
        // not allowed to pass null here
        if (role == null) {
            throw new APIException("Role cannot be null");
        }

        List<ProviderRole> roles = new ArrayList<ProviderRole>();
        roles.add(role);
        return getProvidersAsPersonsByRoles(roles);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> getProvidersAsPersonsByRelationshipType(RelationshipType relationshipType) {

        if (relationshipType == null) {
            throw new  APIException("Relationship type cannot be null");
        }

        // first fetch the roles that support this relationship type, then fetch all the providers with those roles
        List<ProviderRole> providerRoles = getProviderRolesByRelationshipType(relationshipType);
        if (providerRoles == null || providerRoles.size() == 0) {
            return new ArrayList<Person>();  // just return an empty list
        }
        else {
            return getProvidersAsPersonsByRoles(providerRoles);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderRole> getProviderRolesThatCanSuperviseThisProvider(Person provider) {
       
        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        // first fetch all the roles for this provider
        List<ProviderRole> providerRoles = getProviderRoles(provider);


        // now fetch the roles that can supervise the roles this provider has
        Set<ProviderRole> providerRolesThatCanSupervise = new HashSet<ProviderRole>();

        for (ProviderRole providerRole : providerRoles) {
            List<ProviderRole> roles = getProviderRolesBySuperviseeProviderRole(providerRole);
            if (roles != null && roles.size() > 0) {
                 providerRolesThatCanSupervise.addAll(roles);
            }
        }

        return new ArrayList<ProviderRole>(providerRolesThatCanSupervise);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderRole> getProviderRolesThatProviderCanSupervise(Person provider) {

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        Set<ProviderRole> rolesThatProviderCanSupervise = new HashSet<ProviderRole>();

        // iterate through all the provider roles this provider supports
        for (ProviderRole role : getProviderRoles(provider)) {
            // add all roles that this role can supervise
            if (role.getSuperviseeProviderRoles() != null && role.getSuperviseeProviderRoles().size() > 0) {
                rolesThatProviderCanSupervise.addAll(role.getSuperviseeProviderRoles());
            }
        }

        return new ArrayList<ProviderRole> (rolesThatProviderCanSupervise);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProvider(Person person) {

        if (person == null) {
            throw new APIException("Person cannot be null");
        }

        Collection<Provider> providers = getProvidersByPerson(person, true);
        return providers == null || providers.size() == 0 ? false : true;
    }


    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(Person provider, ProviderRole role) {

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (role == null) {
            throw new APIException("Role cannot be null");
        }

        return getProviderRoles(provider).contains(role);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean supportsRelationshipType(Person provider, RelationshipType relationshipType) {

        Collection<Provider> providers = getProvidersByPerson(provider, false);

        if (providers == null || providers.size() == 0) {
            return false;
        }

        for (Provider p : providers) {
            if (supportsRelationshipType(p, relationshipType)) {
                return true;
            }
        }

        return false;
    }


    @Override
    @Transactional(readOnly = true)
    public boolean canSupervise(Person supervisor, Person supervisee) {

        if (supervisor == null) {
            throw new APIException("Supervisor cannot be null");
        }

        if (supervisee == null) {
            throw new APIException("Provider cannot be null");
        }

        // return false if supervisor and supervisee are the same person!
        if (supervisor.equals(supervisee)) {
            return false;
        }

        // get all the provider roles the supervisor can supervise
        List<ProviderRole> rolesThatProviderCanSupervisee = getProviderRolesThatProviderCanSupervise(supervisor);

        // get all the roles associated with the supervisee
        List<ProviderRole> superviseeProviderRoles = getProviderRoles(supervisee);

        return ListUtils.intersection(rolesThatProviderCanSupervisee, superviseeProviderRoles).size() > 0 ? true : false;
    }


    @Override
    @Transactional
    public void assignPatientToProvider(Patient patient, Person provider, RelationshipType relationshipType, Date date)
            throws ProviderDoesNotSupportRelationshipTypeException, PatientAlreadyAssignedToProviderException,
            PersonIsNotProviderException, DateCannotBeInFutureException {

        if (patient == null) {
            throw new APIException("Patient cannot be null");
        }

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (relationshipType == null) {
            throw new APIException("Relationship type cannot be null");
        }

        if (patient.isVoided()) {
            throw new APIException("Patient cannot be voided");
        }

        if (provider.isVoided()) {
            throw new APIException("Provider cannot be voided");
        }
        
        if (!isProvider(provider)) {
             throw new PersonIsNotProviderException(provider.getPersonName() + " is not a provider");
        }
        
        if (!supportsRelationshipType(provider, relationshipType)) {
            throw new ProviderDoesNotSupportRelationshipTypeException(provider.getPersonName() + " cannot support " + relationshipType);
        }

        // use current date if no date specified
        if (date == null) {
            date = new Date();
        }

        if (date.after(new Date())) {
            throw new DateCannotBeInFutureException("Assignment date cannot be in the future");
        }

        // test to mark sure the relationship doesn't already exist
        List<Relationship> relationships = null;
        try {
            relationships = getActiveProviderRelationshipsForPatient(patient, provider, relationshipType, date);
        } catch (InvalidRelationshipTypeException e) {
            throw new ProviderDoesNotSupportRelationshipTypeException(provider.getPersonName() + " cannot support " + relationshipType);
        }
        if (relationships != null && relationships.size() > 0) {
            throw new PatientAlreadyAssignedToProviderException(patient.getPersonName() + " is already assigned to " + provider.getPersonName() + " with a " + relationshipType + " relationship on " + Context.getDateFormat().format(date));
        }
        
        // go ahead and create the relationship
        Relationship relationship = new Relationship();
        relationship.setPersonA(provider);
        relationship.setPersonB(patient);
        relationship.setRelationshipType(relationshipType);
        relationship.setStartDate(ProviderManagementUtils.clearTimeComponent(date));
        Context.getPersonService().saveRelationship(relationship);
    }

    @Override
    @Transactional
    public void assignPatientToProvider(Patient patient, Person provider, RelationshipType relationshipType)
            throws ProviderDoesNotSupportRelationshipTypeException, PatientAlreadyAssignedToProviderException,
            PersonIsNotProviderException {
        try {
            assignPatientToProvider(patient, provider, relationshipType, new Date());
        }
        // we should never get a DateCannotBeInFuture exception since this method is suppose to do the assignment on the current date
        catch (DateCannotBeInFutureException e) {
            throw new APIException("DateCannotBeInFutureException should never be thrown here", e);
        }
    }


    @Override
    @Transactional
    public void unassignPatientFromProvider(Patient patient, Person provider, RelationshipType relationshipType, Date date)
        throws PatientNotAssignedToProviderException, PersonIsNotProviderException, InvalidRelationshipTypeException,
            DateCannotBeInFutureException {

        if (patient == null) {
            throw new APIException("Patient cannot be null");
        }

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (relationshipType == null) {
            throw new APIException("Relationship type cannot be null");
        }

        if (!isProvider(provider)) {
            throw new PersonIsNotProviderException(provider + " is not a provider");
        }

        // we don't need to assure that the person supports the relationship type, but we need to make sure this a provider/patient relationship type
        if (!getAllProviderRoleRelationshipTypes(false).contains(relationshipType)) {
            throw new InvalidRelationshipTypeException("Invalid relationship type: " + relationshipType + " is not a provider/patient relationship type");
        }

        // use current date if no date specified
        if (date == null) {
            date = new Date();
        }

        if (date.after(new Date())) {
            throw new DateCannotBeInFutureException("Unassignment date cannot be in the future");
        }

        // find the existing relationship
        List<Relationship> relationships = getActiveProviderRelationshipsForPatient(patient, provider, relationshipType, date);
        if (relationships == null || relationships.size() == 0) {
            throw new PatientNotAssignedToProviderException(patient.getPersonName() + " is not assigned to " + provider.getPersonName() + " with a " + relationshipType + " relationship on " + Context.getDateFormat().format(date));
        }
        if (relationships.size() > 1) {
            throw new APIException("Duplicate " + relationshipType + " between " + provider.getPersonName() + " and " + patient.getPersonName());
        }

        // go ahead and set the end date of the relationship
        Relationship relationship = relationships.get(0);
        relationship.setEndDate(ProviderManagementUtils.clearTimeComponent(date));
        Context.getPersonService().saveRelationship(relationship);
    }

    @Override
    @Transactional
    public void unassignPatientFromProvider(Patient patient, Person provider, RelationshipType relationshipType)
            throws PatientNotAssignedToProviderException, PersonIsNotProviderException, InvalidRelationshipTypeException {

        try {
            unassignPatientFromProvider(patient, provider, relationshipType, new Date());
        }
        // we should never get a DateCannotBeInFuture exception since this method is suppose to do the assignment on the current date
        catch (DateCannotBeInFutureException e) {
            throw new APIException("DateCannotBeInFutureException should never be thrown here", e);
        }
    }

    @Override
    @Transactional
    public void unassignAllPatientsFromProvider(Person provider, RelationshipType relationshipType)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException {

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (relationshipType == null) {
            throw new APIException("Relationship type cannot be null");
        }

        if (!isProvider(provider)) {
            throw new PersonIsNotProviderException(provider.getPersonName() + " is not a provider");
        }

        // we don't need to assure that the person supports the relationship type, but we need to make sure this a provider/patient relationship type
        if (!getAllProviderRoleRelationshipTypes(false).contains(relationshipType)) {
            throw new InvalidRelationshipTypeException("Invalid relationship type: " + relationshipType + " is not a provider/patient relationship type");
        }

        // go ahead and end each relationship on the current date
        List<Relationship> relationships =
                Context.getPersonService().getRelationships(provider, null, relationshipType, ProviderManagementUtils.clearTimeComponent(new Date()));
        if (relationships != null || relationships.size() > 0) {
            for (Relationship relationship : relationships) {
                relationship.setEndDate(ProviderManagementUtils.clearTimeComponent(new Date()));
                Context.getPersonService().saveRelationship(relationship);
            }
        }
    }

    @Override
    @Transactional
    public void unassignAllPatientsFromProvider(Person provider)
            throws PersonIsNotProviderException {
        
        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        for (RelationshipType relationshipType : getAllProviderRoleRelationshipTypes(false)) {
            try {
                unassignAllPatientsFromProvider(provider, relationshipType);
            }
            catch (InvalidRelationshipTypeException e) {
                // we should never get this exception, since getAlProviderRoleRelationshipTypes
                // should only return valid relationship types; so if we do get this exception, throw a runtime exception
                // instead of forcing calling methods to catch it
                throw new APIException(e);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Relationship> getPatientRelationshipsForProvider(Person provider, RelationshipType relationshipType, Date date)
        throws PersonIsNotProviderException, InvalidRelationshipTypeException {

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (!isProvider(provider)) {
            throw new PersonIsNotProviderException(provider.getPersonName() + " is not a provider");
        }

        if (relationshipType != null && !getAllProviderRoleRelationshipTypes(false).contains(relationshipType)) {
            throw new InvalidRelationshipTypeException("Invalid relationship type: " + relationshipType + " is not a provider/patient relationship type");
        }

        // get the specified relationships for the provider
        List<Relationship> relationships =
                Context.getPersonService().getRelationships(provider, null, relationshipType, date);

        // if a relationship type was not specified, we need to filter this list to only contain provider relationships
        if (relationshipType == null) {
            ProviderManagementUtils.filterNonProviderRelationships(relationships);
        }

        return relationships;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Relationship> getPatientRelationshipsForProvider(Person provider, RelationshipType relationshipType)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException {
        return getPatientRelationshipsForProvider(provider, relationshipType, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> getPatientsOfProvider(Person provider, RelationshipType relationshipType, Date date)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException {

        List<Relationship> relationships = Context.getService(ProviderManagementService.class).getPatientRelationshipsForProvider(provider, relationshipType, date);

        // iterate through the relationships and fetch the patients
        Set<Patient> patients = new HashSet<Patient>();
        for (Relationship relationship : relationships) {

            if (!relationship.getPersonB().isPatient()) {
                throw new APIException("Invalid relationship " + relationship + ": person b must be a patient");
            }

            Patient p = Context.getPatientService().getPatient(relationship.getPersonB().getId());
            if (!p.isVoided()) {
                patients.add(p);
            }
        }

        return new ArrayList<Patient>(patients);
    }

    @Override
    public int getPatientsOfProviderCount(Person provider, RelationshipType relationshipType, Date date)
        throws PersonIsNotProviderException, InvalidRelationshipTypeException {

       // note that we can't just call getPatientsOfProvider and then do a count of the returns results,
       // because we want this method to be callable by users that don't have the right to view patients
        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (!isProvider(provider)) {
            throw new PersonIsNotProviderException(provider.getPersonName() + " is not a provider");
        }

        if (relationshipType != null && !getAllProviderRoleRelationshipTypes(false).contains(relationshipType)) {
            throw new InvalidRelationshipTypeException("Invalid relationship type: " + relationshipType + " is not a provider/patient relationship type");
        }

        // get the specified relationships for the provider
        List<Relationship> relationships =
                Context.getPersonService().getRelationships(provider, null, relationshipType, date);

        // if a relationship type was not specified, we need to filter this list to only contain provider relationships
        if (relationshipType == null) {
            ProviderManagementUtils.filterNonProviderRelationships(relationships);
        }

        // TODO: the one flaw here is that this counts relationships with voided patients (but hopefully any relationships for voided patients will also be voided)
        return relationships != null ? relationships.size() : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> getPatientsOfProvider(Person provider, RelationshipType relationshipType)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException {
        return getPatientsOfProvider(provider, relationshipType, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Relationship> getProviderRelationshipsForPatient(Patient patient, Person provider, RelationshipType relationshipType, Date date)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException {
        
        if (patient == null) {
            throw new APIException("Patient cannot be null");
        }
        
        if (provider != null && !isProvider(provider)) {
            throw new PersonIsNotProviderException(provider.getPersonName() + " is not a provider");
        }
        
        if (relationshipType != null && !getAllProviderRoleRelationshipTypes(false).contains(relationshipType)) {
            throw new InvalidRelationshipTypeException(relationshipType + " is not a patient/provider relationship");
        }

        // fetch the relationships
        List<Relationship> relationships = Context.getPersonService().getRelationships(provider, patient, relationshipType, date);

        // if a relationship type was not specified, we need to filter this list to only contain provider relationships
        if (relationshipType == null) {
            ProviderManagementUtils.filterNonProviderRelationships(relationships);
        }

        return relationships;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Relationship> getActiveProviderRelationshipsForPatient(Patient patient, Person provider, RelationshipType relationshipType, Date date)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException {

        List<Relationship> activeRelationships = null;
        if (patient == null) {
            throw new APIException("Patient cannot be null");
        }

        if (provider != null && !isProvider(provider)) {
            throw new PersonIsNotProviderException(provider.getPersonName() + " is not a provider");
        }

        if (relationshipType != null && !getAllProviderRoleRelationshipTypes(false).contains(relationshipType)) {
            throw new InvalidRelationshipTypeException(relationshipType + " is not a patient/provider relationship");
        }

        // fetch the relationships
        List<Relationship> relationships = Context.getPersonService().getRelationships(provider, patient, relationshipType, date);

        // if a relationship type was not specified, we need to filter this list to only contain provider relationships
        if (relationshipType == null) {
            ProviderManagementUtils.filterNonProviderRelationships(relationships);
        }
        if ( relationships != null && relationships.size() > 0) {
            activeRelationships = new ArrayList<Relationship>();
            for (Relationship relationship : relationships) {
                if (relationship.getEndDate() == null) {
                    activeRelationships.add(relationship);
                }
            }
        }

        return activeRelationships;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Relationship> getProviderRelationshipsForPatient(Patient patient, Person provider, RelationshipType relationshipType)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException {
        return getProviderRelationshipsForPatient(patient, provider, relationshipType, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> getProvidersAsPersonsForPatient(Patient patient, RelationshipType relationshipType, Date date)
            throws InvalidRelationshipTypeException {

        List<Relationship> relationships;

        try {
            relationships = getProviderRelationshipsForPatient(patient, null, relationshipType, date);
        }
        catch (PersonIsNotProviderException e) {
            // should never reach here since we aren't specifying a provider in the above method
            // just through an APIException here to avoid having have this method throw this exception
            throw new APIException(e);
        }

        Set<Person> providers = new HashSet<Person>();
        
        for (Relationship relationship : relationships) {
            if (!isProvider(relationship.getPersonA()))   {
                // something has gone really wrong here
                throw new APIException(relationship.getPersonA().getPersonName() + " is not a provider");
            }
            else {
                providers.add(relationship.getPersonA());
            }
        }
        
        return new ArrayList<Person>(providers);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> getProvidersAsPersonsForPatient(Patient patient, RelationshipType relationshipType)
            throws InvalidRelationshipTypeException {
        return getProvidersAsPersonsForPatient(patient, relationshipType, null);
    }

    @Override
    @Transactional
    public void transferPatients(List<Patient> patients, Person sourceProvider, Person destinationProvider, RelationshipType relationshipType, Date date)
            throws ProviderDoesNotSupportRelationshipTypeException, SourceProviderSameAsDestinationProviderException,
            PersonIsNotProviderException, InvalidRelationshipTypeException, PatientNotAssignedToProviderException,
            DateCannotBeInFutureException {

        if (sourceProvider == null) {
            throw new APIException("Source provider cannot be null");
        }

        if (destinationProvider == null) {
            throw new APIException("Destination provider cannot be null");
        }

        if (!isProvider(sourceProvider)) {
            throw new PersonIsNotProviderException(sourceProvider.getPersonName() + " is not a provider");
        }

        if (!isProvider(destinationProvider)) {
            throw new PersonIsNotProviderException(destinationProvider.getPersonName() + " is not a provider");
        }

        if (sourceProvider.equals(destinationProvider)) {
            throw new SourceProviderSameAsDestinationProviderException("Provider " + sourceProvider.getPersonName() + " is the same as provider " + destinationProvider.getPersonName());
        }

        if (relationshipType == null) {
            throw new APIException("Relationship type cannot be null");
        }

        // assign these patients to the new provider, unassign them from the old provider
        for (Patient patient : patients) {
            try {
                assignPatientToProvider(patient, destinationProvider, relationshipType, date);
            }
            catch (PatientAlreadyAssignedToProviderException e) {
                // we can ignore this exception; no need to assign patient if already assigned
            }

            unassignPatientFromProvider(patient, sourceProvider, relationshipType, date);
        }
    }

    @Override
    public void transferPatients(List<Patient> patients, Person sourceProvider, Person destinationProvider, RelationshipType relationshipType)
            throws ProviderDoesNotSupportRelationshipTypeException, SourceProviderSameAsDestinationProviderException,
            PersonIsNotProviderException, InvalidRelationshipTypeException, PatientNotAssignedToProviderException,
            DateCannotBeInFutureException{

        transferPatients(patients, sourceProvider, destinationProvider, relationshipType, new Date());
    }

    @Override
    @Transactional
    public void transferAllPatients(Person sourceProvider, Person destinationProvider, RelationshipType relationshipType, Date date)
            throws ProviderDoesNotSupportRelationshipTypeException, SourceProviderSameAsDestinationProviderException,
            PersonIsNotProviderException, InvalidRelationshipTypeException, DateCannotBeInFutureException {

        try {
            transferPatients(getPatientsOfProvider(sourceProvider, relationshipType, date), sourceProvider, destinationProvider, relationshipType, date);
        }
        catch (PatientNotAssignedToProviderException e) {
            // we should fail hard here, because getPatientsOfProvider should only return patients of the provider,
            // so if this exception has been thrown, something has gone really wrong
            throw new APIException("All patients here should be assigned to provider,", e);
        }
    }

    @Override
    @Transactional
    public void transferAllPatients(Person sourceProvider, Person destinationProvider, RelationshipType relationshipType)
        throws ProviderDoesNotSupportRelationshipTypeException, SourceProviderSameAsDestinationProviderException,
        PersonIsNotProviderException, InvalidRelationshipTypeException, DateCannotBeInFutureException {

        transferAllPatients(sourceProvider, destinationProvider, relationshipType, new Date());
    }

    @Override
    @Transactional
    public void transferAllPatients(Person sourceProvider, Person destinationProvider, Date date)
            throws ProviderDoesNotSupportRelationshipTypeException, PersonIsNotProviderException,
            SourceProviderSameAsDestinationProviderException, DateCannotBeInFutureException {
        for (RelationshipType relationshipType : getAllProviderRoleRelationshipTypes(false)) {
            try {
                transferAllPatients(sourceProvider, destinationProvider, relationshipType, date);
            }
            catch (InvalidRelationshipTypeException e) {
                // we should never get this exception, since getAlProviderRoleRelationshipTypes
                // should only return valid relationship types; so if we do get this exception, throw a runtime exception
                // instead of forcing calling methods to catch it
                throw new APIException(e);
            }
        }
    }

    @Override
    @Transactional
    public void transferAllPatients(Person sourceProvider, Person destinationProvider)
            throws ProviderDoesNotSupportRelationshipTypeException, PersonIsNotProviderException,
            SourceProviderSameAsDestinationProviderException, DateCannotBeInFutureException {

        transferAllPatients(sourceProvider, destinationProvider, new Date());
    }

    @Override
    @Transactional(readOnly = true)
    public RelationshipType getSupervisorRelationshipType() {
        
        if (supervisorRelationshipType == null) {
            supervisorRelationshipType = Context.getPersonService().getRelationshipTypeByUuid(ProviderManagementConstants.SUPERVISOR_RELATIONSHIP_TYPE_UUID);

            // if the relationship type is still null, throw an exception here
            if (supervisorRelationshipType == null) {
                throw new APIException("Superviser relationship type does not exist in relationship type table");
            }
        }

        return supervisorRelationshipType;
    }

    @Override
    @Transactional
    public void assignProviderToSupervisor(Person provider, Person supervisor, Date date)
            throws PersonIsNotProviderException, InvalidSupervisorException,
            ProviderAlreadyAssignedToSupervisorException, DateCannotBeInFutureException {

        if (supervisor == null) {
            throw new APIException("Supervisor cannot be null");
        }

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (!isProvider(supervisor)) {
            throw new PersonIsNotProviderException(supervisor.getPersonName() + " is not a provider");
        }

        if (!isProvider(provider)) {
            throw new PersonIsNotProviderException(provider.getPersonName() + " is not a provider");
        }

        if (!canSupervise(supervisor, provider)) {
            throw new InvalidSupervisorException(supervisor.getPersonName() + " is not a valid supervisor for " + provider.getPersonName());
        }
        
        // if no date specified, use today's date
        if (date == null) {
            date = new Date();
        }

        if (date.after(new Date())) {
            throw new DateCannotBeInFutureException("Unassignment date cannot be in the future");
        }

        // test to mark sure the relationship doesn't already exist
        List<Relationship> relationships = Context.getPersonService().getRelationships(supervisor, provider, getSupervisorRelationshipType(), date);
        if (relationships != null && relationships.size() > 0) {
            for (Relationship relationship : relationships) {
                if (relationship.getEndDate() == null) {
                    throw new ProviderAlreadyAssignedToSupervisorException(provider.getPersonName() + " is already assigned to " + supervisor.getPersonName());
                }
            }
        }

        // go ahead and create the relationship
        Relationship relationship = new Relationship();
        relationship.setPersonA(supervisor);
        relationship.setPersonB(provider);
        relationship.setRelationshipType(getSupervisorRelationshipType());
        relationship.setStartDate(ProviderManagementUtils.clearTimeComponent(date));
        Context.getPersonService().saveRelationship(relationship);
    }

    @Override
    @Transactional
    public void assignProviderToSupervisor(Person provider, Person supervisor)
            throws PersonIsNotProviderException, InvalidSupervisorException,
            ProviderAlreadyAssignedToSupervisorException {

        try {
            assignProviderToSupervisor(provider, supervisor, new Date());
        }
        // we should never get a DateCannotBeInFuture exception since this method is suppose to do the assignment on the current date
        catch (DateCannotBeInFutureException e) {
            throw new APIException("DateCannotBeInFutureException should never be thrown here", e);
        }
    }

    @Override
    @Transactional
    public void unassignProviderFromSupervisor(Person provider, Person supervisor, Date date)
            throws PersonIsNotProviderException, ProviderNotAssignedToSupervisorException,
                DateCannotBeInFutureException {

        if (supervisor == null) {
            throw new APIException("Supervisor cannot be null");
        }

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (!isProvider(supervisor)) {
            throw new PersonIsNotProviderException(supervisor.getPersonName() + " is not a provider");
        }

        if (!isProvider(provider)) {
            throw new PersonIsNotProviderException(provider.getPersonName() + " is not a provider");
        }

        // if no date specified, use today's date
        if (date == null) {
            date = new Date();
        }

        if (date.after(new Date())) {
            throw new DateCannotBeInFutureException("Unassignment date cannot be in the future");
        }

        // find the existing relationship
        List<Relationship> relationships = Context.getPersonService().getRelationships(supervisor, provider, getSupervisorRelationshipType(), date);
        if (relationships == null || relationships.size() == 0) {
            throw new ProviderNotAssignedToSupervisorException("Provider " + provider.getPersonName() + " is not assigned to supervisor " + supervisor.getPersonName() + " on " + Context.getDateFormat().format(date));
        }
        if (relationships.size() > 1) {
            throw new APIException("Duplicate supervisor relationship between " + provider + " and " + supervisor);
        }

        // go ahead and set the end date of the relationship
        Relationship relationship = relationships.get(0);
        relationship.setEndDate(ProviderManagementUtils.clearTimeComponent(date));
        Context.getPersonService().saveRelationship(relationship);
    }

    @Override
    @Transactional
    public void unassignProviderFromSupervisor(Person provider, Person supervisor)
            throws PersonIsNotProviderException, ProviderNotAssignedToSupervisorException {

        try {
            unassignProviderFromSupervisor(provider, supervisor, new Date());
        }
        // we should never get a DateCannotBeInFuture exception since this method is suppose to do the assignment on the current date
        catch (DateCannotBeInFutureException e) {
            throw new APIException("DateCannotBeInFutureException should never be thrown here", e);
        }
    }

    @Override
    @Transactional
    public void unassignAllSupervisorsFromProvider(Person provider) 
            throws PersonIsNotProviderException {
         if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (!isProvider(provider)) {
            throw new PersonIsNotProviderException(provider.getPersonName() + " is not a provider");
        }

        // go ahead and end each relationship on the current date
        List<Relationship> relationships =
                Context.getPersonService().getRelationships(null, provider, getSupervisorRelationshipType(), ProviderManagementUtils.clearTimeComponent(new Date()));
        if (relationships != null || relationships.size() > 0) {
            for (Relationship relationship : relationships) {
                relationship.setEndDate(ProviderManagementUtils.clearTimeComponent(new Date()));
                Context.getPersonService().saveRelationship(relationship);
            }
        }
    }

    @Override
    @Transactional
    public void unassignAllProvidersFromSupervisor(Person supervisor)
            throws PersonIsNotProviderException {
        if (supervisor == null) {
            throw new APIException("Provider cannot be null");
        }

        if (!isProvider(supervisor)) {
            throw new PersonIsNotProviderException(supervisor.getPersonName() + " is not a provider");
        }

        // go ahead and end each relationship on the current date
        List<Relationship> relationships =
                Context.getPersonService().getRelationships(supervisor, null, getSupervisorRelationshipType(), ProviderManagementUtils.clearTimeComponent(new Date()));
        if (relationships != null || relationships.size() > 0) {
            for (Relationship relationship : relationships) {
                relationship.setEndDate(ProviderManagementUtils.clearTimeComponent(new Date()));
                Context.getPersonService().saveRelationship(relationship);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Relationship> getSupervisorRelationshipsForProvider(Person provider, Date date)
            throws PersonIsNotProviderException {

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (!isProvider(provider)) {
            throw new PersonIsNotProviderException(provider.getPersonName() + " is not a provider");
        }

        return Context.getPersonService().getRelationships(null, provider, getSupervisorRelationshipType(), date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Relationship> getSupervisorRelationshipsForProvider(Person provider)
            throws PersonIsNotProviderException{
        return getSupervisorRelationshipsForProvider(provider, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> getSupervisorsForProvider(Person provider, Date date)
            throws PersonIsNotProviderException {

        List<Relationship> relationships = getSupervisorRelationshipsForProvider(provider, date);

        Set<Person> providers = new HashSet<Person>();

        for (Relationship relationship : relationships) {
            if (!isProvider(relationship.getPersonA()))   {
                // something has gone really wrong here
                throw new APIException(relationship.getPersonA().getPersonName() + " is not a provider");
            }
            else {
                providers.add(relationship.getPersonA());
            }
        }

        return new ArrayList<Person>(providers);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> getSupervisorsForProvider(Person provider)
            throws PersonIsNotProviderException {
        return getSupervisorsForProvider(provider, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Relationship> getSuperviseeRelationshipsForSupervisor(Person supervisor, Date date)
            throws PersonIsNotProviderException {
        
        if (supervisor == null) {
            throw new APIException("Supervisor cannot be null");
        }

        if (!isProvider(supervisor)) {
            throw new PersonIsNotProviderException(supervisor.getPersonName() + " is not a provider");
        }

        return Context.getPersonService().getRelationships(supervisor, null, getSupervisorRelationshipType(), date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Relationship> getSuperviseeRelationshipsForSupervisor(Person supervisor)
            throws PersonIsNotProviderException {
        return getSuperviseeRelationshipsForSupervisor(supervisor, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> getSuperviseesForSupervisor(Person supervisor, Date date)
            throws PersonIsNotProviderException {
        
        List<Relationship> relationships = getSuperviseeRelationshipsForSupervisor(supervisor, date);

        Set<Person> providers = new HashSet<Person>();

        for (Relationship relationship : relationships) {
            if (!isProvider(relationship.getPersonA()))   {
                // something has gone really wrong here
                throw new APIException(relationship.getPersonA().getPersonName() + " is not a provider");
            }
            else {
                providers.add(relationship.getPersonB());
            }
        }

        return new ArrayList<Person>(providers);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> getSuperviseesForSupervisor(Person supervisor)
            throws PersonIsNotProviderException {
        return getSuperviseesForSupervisor(supervisor, null);
    }

    @Override
    @Transactional
    public void transferSupervisees(List<Person> supervisees, Person sourceSupervisor, Person destinationSupervisor, Date date)
            throws PersonIsNotProviderException, SourceProviderSameAsDestinationProviderException, InvalidSupervisorException,
            ProviderNotAssignedToSupervisorException, DateCannotBeInFutureException {

        if (sourceSupervisor == null) {
            throw new APIException("Source supervisor cannot be null");
        }

        if (destinationSupervisor == null) {
            throw new APIException("Destination supervisor cannot be null");
        }

        if (!isProvider(sourceSupervisor)) {
            throw new PersonIsNotProviderException(sourceSupervisor.getPersonName() + " is not a provider");
        }

        if (!isProvider(destinationSupervisor)) {
            throw new PersonIsNotProviderException(destinationSupervisor.getPersonName() + " is not a provider");
        }

        if (sourceSupervisor.equals(destinationSupervisor)) {
            throw new SourceProviderSameAsDestinationProviderException("Provider " + sourceSupervisor.getPersonName() + " is the same as provider " + destinationSupervisor.getPersonName());
        }

        for (Person supervisee : supervisees) {
            // first assign the supervisee to the new superviser
            try {
                assignProviderToSupervisor(supervisee, destinationSupervisor, date);
            }
            catch (ProviderAlreadyAssignedToSupervisorException e) {
                // don't worry about doing anything here, no need to worry about assigning if already assigned
                // however, note that we don't trap the invalid supervisor exception that could occur here
            }

            // now unassign the supervisee from the old supervisor
            unassignProviderFromSupervisor(supervisee, sourceSupervisor, date);
        }
    }

    @Override
    @Transactional
    public void transferSupervisees(List<Person> supervisees, Person sourceSupervisor, Person destinationSupervisor)
            throws PersonIsNotProviderException, SourceProviderSameAsDestinationProviderException, InvalidSupervisorException,
            ProviderNotAssignedToSupervisorException, DateCannotBeInFutureException {

         transferSupervisees(supervisees, sourceSupervisor, destinationSupervisor, new Date());
    }

    @Override
    public void transferAllSupervisees(Person sourceSupervisor, Person destinationSupervisor, Date date)
            throws PersonIsNotProviderException, SourceProviderSameAsDestinationProviderException, InvalidSupervisorException,
            DateCannotBeInFutureException {
        try {
            transferSupervisees(getSuperviseesForSupervisor(sourceSupervisor, date), sourceSupervisor, destinationSupervisor, date);
        }
        catch (ProviderNotAssignedToSupervisorException e) {
            // we can fail hard here because getSuperviseesForSupervisor should never return providers who aren't supervisees of sourceSupervisor
            throw new RuntimeException(e);
        }
    }

    @Override
    public void transferAllSupervisees(Person sourceSupervisor, Person destinationSupervisor)
            throws PersonIsNotProviderException, SourceProviderSameAsDestinationProviderException, InvalidSupervisorException,
            DateCannotBeInFutureException {

        transferAllSupervisees(sourceSupervisor, destinationSupervisor, new Date());
    }

    /**
     * Methods to fetch Provider objects based on persons
     */

    @Override
    @Transactional(readOnly = true)
    public List<Provider> getProvidersByPerson(Person person, boolean includeRetired) {
        return dao.getProvidersByPerson(person, includeRetired);
    }

    /**
     * Utility methods
     */
    private List<Person> providersToPersons(List<Provider> providers) {
        
        if (providers == null) {
            return null;
        }
        
        Set<Person> persons = new HashSet<Person>();

        // note that simply ignores providers that are not person, as the module cannot handle them (and I believe that it has been determined that OpemMRS won't support them)
        for (Provider provider : providers) {
            if (provider.getPerson() != null) {
                persons.add(provider.getPerson());
            }
            else {
                log.warn("Ignoring provider " + provider.getId() + " because they are not a person");
            }
        }

        return new ArrayList<Person>(persons);
    }

    private boolean supportsRelationshipType(Provider provider, RelationshipType relationshipType) {

        if (provider == null) {
            throw new APIException("Provider should not be null");
        }

        if (relationshipType == null) {
            throw new APIException("Relationship type should not be null");
        }

        // if this provider has no role, return false
        if (provider.getProviderRole() == null) {
            return false;
        }
        // otherwise, test if the provider's role supports the specified relationship type
        else {
            return provider.getProviderRole().supportsRelationshipType(relationshipType);
        }
    }
}
