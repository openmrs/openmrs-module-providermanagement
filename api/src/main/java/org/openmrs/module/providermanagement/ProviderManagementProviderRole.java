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
package org.openmrs.module.providermanagement;

import lombok.Getter;
import lombok.Setter;
import org.openmrs.ProviderAttributeType;
import org.openmrs.ProviderRole;
import org.openmrs.RelationshipType;
import org.openmrs.annotation.DisableHandlers;
import org.openmrs.api.handler.RequiredDataHandler;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

/**
 * Extends the core ProviderRole to add additional configuration as to what is supported
 *
 * A provider role specifies what Provider/Patient relationships a provider with that role can support,
 * as well as the provider roles that another provider role can provider oversight for.
 *
 * For example, a "Community Health Worker" role might support an "Accompagnateur" relationship,
 * and "Head Surgeon" role might be able to oversee a person with Provider Role of "Surgeon".
 */
@Entity
@Table(name = "providermanagement_provider_role")
@Inheritance(strategy = InheritanceType.JOINED)
public class ProviderManagementProviderRole extends ProviderRole {

    // the provider/patient relationships this role can support
    @DisableHandlers(handlerTypes = { RequiredDataHandler.class })  // disable all required data handlers (save, retire, etc)
    @Getter @Setter
    @ManyToMany
    @JoinTable(
            name="providermanagement_provider_role_relationship_type",
            joinColumns = @JoinColumn(name = "provider_role_id"),
            inverseJoinColumns = @JoinColumn(name = "relationship_type_id")
    )
    private Set<RelationshipType> relationshipTypes;

    // the provider roles this provider role can supervise
    @DisableHandlers(handlerTypes = { RequiredDataHandler.class })  // disable all required data handlers (save, retire, etc)
    @Getter @Setter
    @ManyToMany
    @JoinTable(
            name="providermanagement_provider_role_supervisee_provider_role",
            joinColumns = @JoinColumn(name = "provider_role_id"),
            inverseJoinColumns = @JoinColumn(name = "supervisee_provider_role_id")
    )
    private Set<ProviderManagementProviderRole> superviseeProviderRoles;

    // the attribute types associated with this role
    @DisableHandlers(handlerTypes = { RequiredDataHandler.class }) // disable all required data handlers (save, retire, etc)
    @Getter @Setter
    @ManyToMany
    @JoinTable(
            name="providermanagement_provider_role_provider_attribute_type",
            joinColumns = @JoinColumn(name = "provider_role_id"),
            inverseJoinColumns = @JoinColumn(name = "provider_attribute_type_id")
    )
    private Set<ProviderAttributeType> providerAttributeTypes;

    // whether this role can serve as a supervisor
    public boolean isSupervisorRole() {
        return (!(superviseeProviderRoles == null || superviseeProviderRoles.isEmpty()));
    }

    // whether this role can provide direct patient care
    public boolean isDirectPatientCareRole() {
        return (!(relationshipTypes == null || relationshipTypes.isEmpty()));
    }

    // whether or not this role supports the specified relationship type
    public boolean supportsRelationshipType(RelationshipType relationshipType) {
        return (relationshipTypes != null && relationshipType != null && relationshipTypes.contains(relationshipType));
    }
}