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

package org.openmrs.module.providermanagement.command;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Relationship;

import java.util.Iterator;
import java.util.List;

// command object for adding a patient and a relationship to the model as a unit
public class PatientAndRelationshipAndPatientPrograms {

    private Integer id;

    private Patient patient;

    private Relationship relationship;

    private List<PatientProgram> patientPrograms;

    public PatientAndRelationshipAndPatientPrograms() {
    }

    public PatientAndRelationshipAndPatientPrograms(Patient patient, Relationship relationship, List<PatientProgram> patientPrograms) {
        this.patient = patient;
        this.relationship = relationship;
        this.patientPrograms = patientPrograms;
        this.id = relationship.getId();
    }

    /**
     * Convenient method that providers a string list of all patient programs for display in UI
     *
     * @return
     */
    public String getPatientProgramNames() {
        StringBuffer buffer = new StringBuffer();

        Iterator<PatientProgram> i = patientPrograms.iterator();

        while (i.hasNext()) {
            buffer.append(i.next().getProgram().getName());
            if (i.hasNext()) {
                buffer.append(", ");
            }
        }

        return buffer.toString();
    }

    /**
     * Getters and Setters
     * @return
     */

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public List<PatientProgram> getPatientPrograms() {
        return patientPrograms;
    }

    public void setPatientPrograms(List<PatientProgram> patientPrograms) {
        this.patientPrograms = patientPrograms;
    }
}

