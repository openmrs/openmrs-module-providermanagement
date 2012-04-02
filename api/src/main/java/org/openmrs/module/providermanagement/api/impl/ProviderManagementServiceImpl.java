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
import org.apache.commons.collections.Predicate;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.api.APIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.providermanagement.*;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.api.db.ProviderManagementDAO;
import org.openmrs.module.providermanagement.exception.*;

import java.util.*;

/**
 * It is a default implementation of {@link ProviderManagementService}.
 */
public class ProviderManagementServiceImpl extends BaseOpenmrsService implements ProviderManagementService {

    // TODO: add checks to make sure person is not voided automatically when appropriate (in the assignment classes?)

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
    public List<ProviderRole> getAllProviderRoles() {
        return dao.getAllProviderRoles(false);
    }

    @Override
    public List<ProviderRole> getAllProviderRoles(boolean includeRetired) {
        return dao.getAllProviderRoles(includeRetired);
    }

    @Override
    public ProviderRole getProviderRole(Integer id) {
        return dao.getProviderRole(id);
    }

    @Override
    public ProviderRole getProviderRoleByUuid(String uuid) {
        return dao.getProviderRoleByUuid(uuid);
    }

    @Override
    public List<ProviderRole> getProviderRolesByRelationshipType(RelationshipType relationshipType) {
        if (relationshipType == null) {
            throw new APIException("relationshipType cannot be null");
        }
        else {
            return dao.getProviderRolesByRelationshipType(relationshipType);
        }
    }

    @Override
    public List<ProviderRole> getProviderRolesBySuperviseeProviderRole(ProviderRole providerRole) {
        if (providerRole == null) {
            throw new APIException("providerRole cannot be null");
        }
        else {
            return dao.getProviderRolesBySuperviseeProviderRole(providerRole);
        }
    }

    @Override
    public void saveProviderRole(ProviderRole role) {
        dao.saveProviderRole(role);
    }

    @Override
    public void retireProviderRole(ProviderRole role, String reason) {
        // BaseRetireHandler handles retiring the object
        dao.saveProviderRole(role);
    }

    @Override
    public void unretireProviderRole(ProviderRole role) {
        // BaseUnretireHandler handles unretiring the object
        dao.saveProviderRole(role);
    }

    @Override
    public void purgeProviderRole(ProviderRole role) {

        // TODO: this should catch ConstraintViolationExpection? fail if any provider is associated with this role

        // first, remove this role as supervisee from any roles that can supervise it
        for (ProviderRole r : getProviderRolesBySuperviseeProviderRole(role)) {
            r.getSuperviseeProviderRoles().remove(role);
            saveProviderRole(r);
        }

        dao.deleteProviderRole(role);
    }

    @Override
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
    public List<RelationshipType> getAllProviderRoleRelationshipTypes() {
        return getAllProviderRoleRelationshipTypes(false);
    }

    @Override
    public List<Provider> getProvidersByPerson(Person person, boolean includeRetired) {
        return dao.getProvidersByPerson(person, includeRetired);
    }

    @Override
    public List<Provider> getProvidersByPerson(Person person) {
        return getProvidersByPerson(person, false);
    }

    @Override
    public List<ProviderRole> getProviderRoles(Person provider) {
        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (!ProviderManagementUtils.isProvider(provider)) {
            // return empty list if this person is not a provider
            return new ArrayList<ProviderRole>();
        }

        // otherwise, collect all the roles associated with this provider
        // (we use a set to avoid duplicates at this point)
        Set<ProviderRole> providerRoles = new HashSet<ProviderRole>();

        Collection<Provider> providers = Context.getService(ProviderManagementService.class).getProvidersByPerson(provider);

        for (Provider p : providers) {
            if (p.getProviderRole() != null) {
                providerRoles.add(p.getProviderRole());
            }
        }

        return new ArrayList<ProviderRole>(providerRoles);
    }

    @Override
    public void assignProviderRoleToPerson(Person provider, ProviderRole role, String identifier) {
        // TODO: make sure this syncs properly!

        if (provider == null) {
            throw new APIException("Cannot set provider role: provider is null");
        }
        
        if (role == null) {
            throw new APIException("Cannot set provider role: role is null");
        }
        
        if (identifier == null) {
            throw new APIException("Cannot set provider role: identifier is null");
        }

        if (provider.isVoided()) {
            throw new APIException("Cannot set provider role: underlying person has been voided");
        }

        if (ProviderManagementUtils.hasRole(provider,role)) {
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
    public void unassignProviderRoleFromPerson(Person provider, ProviderRole role) {
        // TODO: make sure this syncs properly!

        if (provider == null) {
            throw new APIException("Cannot set provider role: provider is null");
        }

        if (role == null) {
            throw new APIException("Cannot set provider role: role is null");
        }

        if (!ProviderManagementUtils.hasRole(provider,role)) {
            // if the provider doesn't have this role, do nothing
            return;
        }

        // note that we don't check to make sure this provider is a person

        // iterate through all the providers and retire any with the specified role
        for (Provider p : getProvidersByPerson(provider)) {
            if (p.getProviderRole().equals(role)) {
                Context.getProviderService().retireProvider(p, "removing provider role " + role + " from " + provider);
            }
        }
    }

    @Override
    public List<Person> getProvidersByRoles(List<ProviderRole> roles) {

        // TODO: this won't distinguish between retired and unretired providers until TRUNK-3170 is implemented

        // not allowed to pass null or empty set here
        if (roles == null || roles.isEmpty()) {
            throw new APIException("Roles cannot be null or empty");
        }

        // TODO: figure out if we want to sort results here

        List<Provider> providers = dao.getProvidersByProviderRoles(roles, false);

        return providersToPersons(providers);
    }

    @Override
    public List<Person> getProvidersByRole(ProviderRole role) {

        // TODO: this won't distinguish between retired and unretired providers until TRUNK-3170 is implemented

        // not allowed to pass null here
        if (role == null) {
            throw new APIException("Role cannot be null");
        }

        List<ProviderRole> roles = new ArrayList<ProviderRole>();
        roles.add(role);
        return getProvidersByRoles(roles);
    }

    @Override
    public List<Person> getProvidersByRelationshipType(RelationshipType relationshipType) {

        if (relationshipType == null) {
            throw new  APIException("Relationship type cannot be null");
        }

        // first fetch the roles that support this relationship type, then fetch all the providers with those roles
        List<ProviderRole> providerRoles = getProviderRolesByRelationshipType(relationshipType);
        if (providerRoles == null || providerRoles.size() == 0) {
            return new ArrayList<Person>();  // just return an empty list
        }
        else {
            return getProvidersByRoles(providerRoles);
        }
    }

    @Override
    public List<Person> getProvidersBySuperviseeProviderRole(ProviderRole role) {
       
        if (role == null) {
            throw new APIException("Provider role cannot be null");
        }
        
        // first fetch the roles that can supervise this relationship type, then fetch all providers with those roles
        List<ProviderRole> providerRoles = getProviderRolesBySuperviseeProviderRole(role);
        if (providerRoles == null || providerRoles.size() == 0) {
            return new ArrayList<Person>();  // just return an empty list
        }
        else {
            return getProvidersByRoles(providerRoles);
        }
    }

    @Override
    public void assignPatientToProvider(Patient patient, Person provider, RelationshipType relationshipType, Date date)
            throws ProviderDoesNotSupportRelationshipTypeException, PatientAlreadyAssignedToProviderException,
            PersonIsNotProviderException {

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
        
        if (!ProviderManagementUtils.isProvider(provider)) {
             throw new PersonIsNotProviderException(provider + " is not a provider");
        }
        
        if (!ProviderManagementUtils.supportsRelationshipType(provider, relationshipType)) {
            throw new ProviderDoesNotSupportRelationshipTypeException(provider + " cannot support " + relationshipType);
        }

        // use current date if no date specified
        if (date == null) {
            date = new Date();
        }

        // TODO: what about voided relationships?  does the get relationships method ignore voided?

        // test to mark sure the relationship doesn't already exist
        List<Relationship> relationships = Context.getPersonService().getRelationships(provider, patient, relationshipType, date);
        if (relationships != null && relationships.size() > 0) {
            throw new PatientAlreadyAssignedToProviderException("Provider " + provider + " is already assigned to " + patient + " with a " + relationshipType + "relationship");
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
    public void assignPatientToProvider(Patient patient, Person provider, RelationshipType relationshipType)
            throws ProviderDoesNotSupportRelationshipTypeException, PatientAlreadyAssignedToProviderException,
            PersonIsNotProviderException {
        assignPatientToProvider(patient, provider, relationshipType, new Date());
    }

    @Override
    public void unassignPatientFromProvider(Patient patient, Person provider, RelationshipType relationshipType, Date date)
        throws PatientNotAssignedToProviderException, PersonIsNotProviderException, InvalidRelationshipTypeException {

        if (patient == null) {
            throw new APIException("Patient cannot be null");
        }

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (relationshipType == null) {
            throw new APIException("Relationship type cannot be null");
        }

        if (!ProviderManagementUtils.isProvider(provider)) {
            throw new PersonIsNotProviderException(provider + " is not a provider");
        }

        // we don't need to assure that the person supports the relationship type, but we need to make sure this a provider/patient relationship type
        if (!getAllProviderRoleRelationshipTypes().contains(relationshipType)) {
            throw new InvalidRelationshipTypeException("Invalid relationship type: " + relationshipType + " is not a provider/patient relationship type");
        }

        // use current date if no date specified
        if (date == null) {
            date = new Date();
        }

        // find the existing relationship
        List<Relationship> relationships = Context.getPersonService().getRelationships(provider, patient, relationshipType, date);
        if (relationships == null || relationships.size() == 0) {
            throw new PatientNotAssignedToProviderException("Provider " + provider + " is not assigned to " + patient + " with a " + relationshipType + " relationship");
        }
        if (relationships.size() > 1) {
            // TODO: handle this better? maybe void all but one automatically?
            throw new APIException("Duplicate " + relationshipType + " between " + provider + " and " + patient);
        }

        // go ahead and set the end date of the relationship
        Relationship relationship = relationships.get(0);
        relationship.setEndDate(ProviderManagementUtils.clearTimeComponent(date));
        Context.getPersonService().saveRelationship(relationship);
    }

    @Override
    public void unassignPatientFromProvider(Patient patient, Person provider, RelationshipType relationshipType)
            throws PatientNotAssignedToProviderException, PersonIsNotProviderException, InvalidRelationshipTypeException {
        unassignPatientFromProvider(patient, provider, relationshipType, new Date());
    }

    @Override
    public void unassignAllPatientsFromProvider(Person provider, RelationshipType relationshipType)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException {

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (relationshipType == null) {
            throw new APIException("Relationship type cannot be null");
        }

        if (!ProviderManagementUtils.isProvider(provider)) {
            throw new PersonIsNotProviderException(provider + " is not a provider");
        }

        // we don't need to assure that the person supports the relationship type, but we need to make sure this a provider/patient relationship type
        if (!getAllProviderRoleRelationshipTypes().contains(relationshipType)) {
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
    public void unassignAllPatientsFromProvider(Person provider)
            throws PersonIsNotProviderException {
        
        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        for (RelationshipType relationshipType : getAllProviderRoleRelationshipTypes()) {
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
    public List<Patient> getPatientsOfProvider(Person provider, RelationshipType relationshipType, Date date)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException {

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (!ProviderManagementUtils.isProvider(provider)) {
            throw new PersonIsNotProviderException(provider + " is not a provider");
        }

        if (relationshipType != null && !getAllProviderRoleRelationshipTypes().contains(relationshipType)) {
            throw new InvalidRelationshipTypeException("Invalid relationship type: " + relationshipType + " is not a provider/patient relationship type");
        }

        // use current date if no date specified
        if (date == null) {
            date = new Date();
        }

        // get the specified relationships for the provider
        List<Relationship> relationships =
                Context.getPersonService().getRelationships(provider, null, relationshipType, ProviderManagementUtils.clearTimeComponent(date));

        // if a relationship type was not specified, we need to filter this list to only contain provider relationships
        if (relationshipType == null) {
            ProviderManagementUtils.filterNonProviderRelationships(relationships);
        }

        // now iterate through the relationships and fetch the patients
        Set<Patient> patients = new HashSet<Patient>();
        for (Relationship relationship : relationships) {

            if (!relationship.getPersonB().isPatient()) {
                throw new APIException("Invalid relationship " + relationship + ": person b must be a patient");
            }

            Patient p = Context.getPatientService().getPatient(relationship.getPersonB().getId());
            if (!p.isVoided()) {
                patients.add(Context.getPatientService().getPatient(relationship.getPersonB().getId()));
            }
        }

        return new ArrayList<Patient>(patients);
    }

    @Override
    public List<Patient> getPatientsOfProvider(Person provider, RelationshipType relationshipType)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException {
        return getPatientsOfProvider(provider, relationshipType, new Date());
    }

    @Override
    public List<Relationship> getProviderRelationshipsForPatient(Patient patient, Person provider, RelationshipType relationshipType, Date date)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException {
        
        if (patient == null) {
            throw new APIException("Patient cannot be null");
        }
        
        if (provider != null && !ProviderManagementUtils.isProvider(provider)) {
            throw new PersonIsNotProviderException(provider + " is not a provider");
        }
        
        if (relationshipType != null && !getAllProviderRoleRelationshipTypes().contains(relationshipType)) {
            throw new InvalidRelationshipTypeException(relationshipType + " is not a patient/provider relationship");
        }
        
        // default to today's date if no date specified
        if (date == null) {
            date = new Date();
        }
        
        // fetch the relationships
        List<Relationship> relationships = Context.getPersonService().getRelationships(provider, patient, relationshipType, ProviderManagementUtils.clearTimeComponent(date));

        // if a relationship type was not specified, we need to filter this list to only contain provider relationships
        if (relationshipType == null) {
            ProviderManagementUtils.filterNonProviderRelationships(relationships);
        }

        return relationships;
    }

    @Override
    public List<Relationship> getProviderRelationshipsForPatient(Patient patient, Person provider, RelationshipType relationshipType)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException {
        return getProviderRelationshipsForPatient(patient, provider, relationshipType, new Date());
    }

    @Override
    public List<Person> getProvidersForPatient(Patient patient, RelationshipType relationshipType, Date date)
            throws PersonIsNotProviderException, InvalidRelationshipTypeException {
        
        List<Relationship> relationships = getProviderRelationshipsForPatient(patient, null, relationshipType, date);
        
        Set<Person> providers = new HashSet<Person>();
        
        for (Relationship relationship : relationships) {
            if (!ProviderManagementUtils.isProvider(relationship.getPersonA()))   {
                // something has gone really wrong here
                throw new APIException(relationship.getPersonA() + " is not a provider");
            }
            else {
                providers.add(relationship.getPersonA());
            }
        }
        
        return new ArrayList<Person>(providers);
    }

    @Override
    public List<Person> getProvidersForPatient(Patient patient, RelationshipType relationshipType) throws PersonIsNotProviderException, InvalidRelationshipTypeException {
        return getProvidersForPatient(patient, relationshipType, new Date());
    }

    @Override
    public void transferAllPatients(Person sourceProvider, Person destinationProvider, RelationshipType relationshipType)
        throws ProviderDoesNotSupportRelationshipTypeException, SourceProviderSameAsDestinationProviderException,
        PersonIsNotProviderException, InvalidRelationshipTypeException {

        if (sourceProvider == null) {
            throw new APIException("Source provider cannot be null");
        }

        if (destinationProvider == null) {
            throw new APIException("Destination provider cannot be null");
        }

        if (!ProviderManagementUtils.isProvider(sourceProvider)) {
            throw new PersonIsNotProviderException(sourceProvider + " is not a provider");
        }

        if (!ProviderManagementUtils.isProvider(destinationProvider)) {
            throw new PersonIsNotProviderException(destinationProvider + " is not a provider");
        }

        if (sourceProvider.equals(destinationProvider)) {
            throw new SourceProviderSameAsDestinationProviderException("Provider " + sourceProvider + " is the same as provider " + destinationProvider);
        }

        if (relationshipType == null) {
            throw new APIException("Relationship type cannot be null");
        }
        
       // first get all the patients of the source provider
       List<Patient> patients = getPatientsOfProvider(sourceProvider, relationshipType);

       // assign these patients to the new provider, unassign them from the old provider
        for (Patient patient : patients) {
            try {
                assignPatientToProvider(patient, destinationProvider, relationshipType);
            }
            catch (PatientAlreadyAssignedToProviderException e) {
                // we can ignore this exception; no need to assign patient if already assigned
            }
            try {
                unassignPatientFromProvider(patient, sourceProvider, relationshipType);
            }
            catch (PatientNotAssignedToProviderException e) {
                // we should fail hard here, because getPatientsOfProvider should only return patients of the provider,
                // so if this exception has been thrown, something has gone really wrong
                throw new APIException("All patients here should be assigned to provider,", e);
            }
        }
    }
    
    @Override
    public void transferAllPatients(Person sourceProvider, Person destinationProvider)
            throws ProviderDoesNotSupportRelationshipTypeException, PersonIsNotProviderException,
            SourceProviderSameAsDestinationProviderException {
        for (RelationshipType relationshipType : getAllProviderRoleRelationshipTypes()) {
            try {
                transferAllPatients(sourceProvider, destinationProvider, relationshipType);
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
    public RelationshipType getSupervisorRelationshipType() {
        // TODO: error handling?
        
        if (supervisorRelationshipType == null) {
            supervisorRelationshipType = Context.getPersonService().getRelationshipTypeByUuid(ProviderManagementConstants.SUPERVISOR_RELATIONSHIP_TYPE_UUID);   
        }

        return supervisorRelationshipType;
    }

    @Override
    public void assignProviderToSupervisor(Person provider, Person supervisor, Date date)
            throws PersonIsNotProviderException, InvalidSupervisorException,
            ProviderAlreadyAssignedToSupervisorException {

        if (supervisor == null) {
            throw new APIException("Supervisor cannot be null");
        }

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (!ProviderManagementUtils.isProvider(supervisor)) {
            throw new PersonIsNotProviderException(supervisor + " is not a provider");
        }

        if (!ProviderManagementUtils.isProvider(provider)) {
            throw new PersonIsNotProviderException(provider + " is not a provider");
        }

        if (!ProviderManagementUtils.canSupervise(supervisor, provider)) {
            throw new InvalidSupervisorException(supervisor + " is not a valid supervisor for " + provider);
        }
        
        // if no date specified, use today's date
        if (date == null) {
            date = new Date();
        }

        // test to mark sure the relationship doesn't already exist
        List<Relationship> relationships = Context.getPersonService().getRelationships(supervisor, provider, getSupervisorRelationshipType(), date);
        if (relationships != null && relationships.size() > 0) {
            throw new ProviderAlreadyAssignedToSupervisorException(provider + " is already assigned to " + supervisor);
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
    public void assignProviderToSupervisor(Person provider, Person supervisor)
            throws PersonIsNotProviderException, InvalidSupervisorException,
            ProviderAlreadyAssignedToSupervisorException {
        assignProviderToSupervisor(provider, supervisor, new Date());
    }

    @Override
    public void unassignProviderFromSupervisor(Person provider, Person supervisor, Date date)
            throws PersonIsNotProviderException, ProviderNotAssignedToSupervisorException {

        if (supervisor == null) {
            throw new APIException("Supervisor cannot be null");
        }

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (!ProviderManagementUtils.isProvider(supervisor)) {
            throw new PersonIsNotProviderException(supervisor + " is not a provider");
        }

        if (!ProviderManagementUtils.isProvider(provider)) {
            throw new PersonIsNotProviderException(provider + " is not a provider");
        }

        // if no date specified, use today's date
        if (date == null) {
            date = new Date();
        }

        // find the existing relationship
        List<Relationship> relationships = Context.getPersonService().getRelationships(supervisor, provider, getSupervisorRelationshipType(), date);
        if (relationships == null || relationships.size() == 0) {
            throw new ProviderNotAssignedToSupervisorException("Provider " + provider + " is not assigned to supervisor " + supervisor);
        }
        if (relationships.size() > 1) {
            // TODO: handle this better? maybe void all but one automatically?
            throw new APIException("Duplicate supervisor relationship between " + provider + " and " + supervisor);
        }

        // go ahead and set the end date of the relationship
        Relationship relationship = relationships.get(0);
        relationship.setEndDate(ProviderManagementUtils.clearTimeComponent(date));
        Context.getPersonService().saveRelationship(relationship);
    }

    @Override
    public void unassignProviderFromSupervisor(Person provider, Person supervisor)
            throws PersonIsNotProviderException, ProviderNotAssignedToSupervisorException {
        unassignProviderFromSupervisor(provider, supervisor, new Date());
    }

    @Override
    public void unassignAllSupervisorsFromProvider(Person provider) 
            throws PersonIsNotProviderException {
         if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (!ProviderManagementUtils.isProvider(provider)) {
            throw new PersonIsNotProviderException(provider + " is not a provider");
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
    public void unassignAllProvidersFromSupervisor(Person supervisor)
            throws PersonIsNotProviderException {
        if (supervisor == null) {
            throw new APIException("Provider cannot be null");
        }

        if (!ProviderManagementUtils.isProvider(supervisor)) {
            throw new PersonIsNotProviderException(supervisor + " is not a provider");
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
    public List<Relationship> getSupervisorRelationshipsForProvider(Person provider, Date date)
            throws PersonIsNotProviderException {

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (!ProviderManagementUtils.isProvider(provider)) {
            throw new PersonIsNotProviderException(provider + " is not a provider");
        }

        return Context.getPersonService().getRelationships(null, provider, getSupervisorRelationshipType(),ProviderManagementUtils.clearTimeComponent(new Date()));
    }

    @Override
    public List<Relationship> getSupervisorRelationshipsForProvider(Person provider)
            throws PersonIsNotProviderException{
        return getSupervisorRelationshipsForProvider(provider, new Date());
    }

    @Override
    public List<Person> getSupervisorsForProvider(Person provider, Date date)
            throws PersonIsNotProviderException {

        List<Relationship> relationships = getSupervisorRelationshipsForProvider(provider, date);

        Set<Person> providers = new HashSet<Person>();

        for (Relationship relationship : relationships) {
            if (!ProviderManagementUtils.isProvider(relationship.getPersonA()))   {
                // something has gone really wrong here
                throw new APIException(relationship.getPersonA() + " is not a provider");
            }
            else {
                providers.add(relationship.getPersonA());
            }
        }

        return new ArrayList<Person>(providers);
    }

    @Override
    public List<Person> getSupervisorsForProvider(Person provider)
            throws PersonIsNotProviderException {
        return getSupervisorsForProvider(provider, new Date());
    }

    @Override
    public List<Relationship> getSuperviseeRelationshipsForSupervisor(Person supervisor, Date date)
            throws PersonIsNotProviderException {
        
        if (supervisor == null) {
            throw new APIException("Supervisor cannot be null");
        }

        if (!ProviderManagementUtils.isProvider(supervisor)) {
            throw new PersonIsNotProviderException(supervisor + " is not a provider");
        }

        return Context.getPersonService().getRelationships(supervisor, null, getSupervisorRelationshipType(),ProviderManagementUtils.clearTimeComponent(new Date()));
    }

    @Override
    public List<Relationship> getSuperviseeRelationshipsForSupervisor(Person supervisor)
            throws PersonIsNotProviderException {
        return getSuperviseeRelationshipsForSupervisor(supervisor, new Date());
    }

    @Override
    public List<Person> getSuperviseesForSupervisor(Person supervisor, Date date)
            throws PersonIsNotProviderException {
        
        List<Relationship> relationships = getSuperviseeRelationshipsForSupervisor(supervisor, date);

        Set<Person> providers = new HashSet<Person>();

        for (Relationship relationship : relationships) {
            if (!ProviderManagementUtils.isProvider(relationship.getPersonA()))   {
                // something has gone really wrong here
                throw new APIException(relationship.getPersonA() + " is not a provider");
            }
            else {
                providers.add(relationship.getPersonB());
            }
        }

        return new ArrayList<Person>(providers);
    }

    @Override
    public List<Person> getSuperviseesForSupervisor(Person supervisor)
            throws PersonIsNotProviderException {
        return getSuperviseesForSupervisor(supervisor, new Date());
    }

    /**
     * Utility methods
     */
    private List<Person> providersToPersons(List<Provider> providers) {
        
        if (providers == null) {
            return null;
        }
        
        Set<Person> persons = new HashSet<Person>();
        
        for (Provider provider : providers) {
            persons.add(provider.getPerson());
        }

        return new ArrayList<Person>(persons);
    }
}
