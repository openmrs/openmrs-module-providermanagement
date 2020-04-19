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

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.ProviderAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.annotation.DisableHandlers;
import org.openmrs.api.handler.RequiredDataHandler;

import java.io.Serializable;
import java.util.Set;

/**
 * Used to store the possible provider roles.  A Provide can only have a single role (though a single person
 * could be associated with more than one Provider object).
 *
 * A provider role specifies what Provider/Patient relationships a provider with that role can support,
 * as well as the provider roles that another provider role can provider oversight for.
 *
 * For example, a "Community Health Worker" role might support an "Accompagnateur" relationship,
 * and "Head Surgeon" role might be able to oversee a person with Provider Role of "Surgeon".
 */
    public class ProviderRole extends BaseOpenmrsMetadata implements Serializable {

        private static final long serialVersionUID = 1L;

        private Integer providerRoleId;

        // the provider/patient relationships this role can support
        @DisableHandlers(handlerTypes = { RequiredDataHandler.class })  // disable all required data handlers (save, retire, etc)
        private Set<RelationshipType> relationshipTypes;

        // the provider roles this provider role can supervise
        @DisableHandlers(handlerTypes = { RequiredDataHandler.class })  // disable all required data handlers (save, retire, etc)
        private Set<ProviderRole> superviseeProviderRoles;

        // the attribute types associated with this role
        @DisableHandlers(handlerTypes = { RequiredDataHandler.class }) // disable all required data handlers (save, retire, etc)
        private Set<ProviderAttributeType> providerAttributeTypes;

        // whether or not this role can serve as a supervisor
        public boolean isSupervisorRole() {
            return (!(superviseeProviderRoles == null || superviseeProviderRoles.size() == 0));
        }

        // whether or not this role can provide direct patient care
        public boolean isDirectPatientCareRole() {
            return (!(relationshipTypes == null || relationshipTypes.size() == 0));
        }

        // whether or not this role supports the specified relationship type
        public boolean supportsRelationshipType(RelationshipType relationshipType) {
            return (relationshipTypes != null && relationshipType != null && relationshipTypes.contains(relationshipType) ? true : false);
        }

        @Override
        public String toString() {
            return "ProviderRole{" +
                    "providerRoleId=" + providerRoleId +
                    ", name=" + this.getName() +
                    '}';
        }

        @Override
        public Integer getId() {
            return providerRoleId;
        }

        @Override
        public void setId(Integer id) {
            this.providerRoleId = id;
        }

        public Integer getProviderRoleId() {
            return providerRoleId;
        }

        public void setProviderRoleId(Integer id) {
            this.providerRoleId = id;
        }

        public Set<RelationshipType> getRelationshipTypes() {
            return relationshipTypes;
        }

        public void setRelationshipTypes(Set<RelationshipType> relationshipTypes) {
            this.relationshipTypes = relationshipTypes;
        }

        public Set<ProviderRole> getSuperviseeProviderRoles() {
            return superviseeProviderRoles;
        }

        public void setSuperviseeProviderRoles(Set<ProviderRole> superviseeProviderRoles) {
            this.superviseeProviderRoles = superviseeProviderRoles;
        }

        public Set<ProviderAttributeType> getProviderAttributeTypes() {
            return providerAttributeTypes;
        }

        public void setProviderAttributeTypes(Set<ProviderAttributeType> providerAttributeTypes) {
            this.providerAttributeTypes = providerAttributeTypes;
        }
}