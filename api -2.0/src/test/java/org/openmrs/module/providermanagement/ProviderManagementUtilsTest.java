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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.exception.InvalidSupervisorException;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.module.providermanagement.exception.ProviderAlreadyAssignedToSupervisorException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

public class ProviderManagementUtilsTest extends BaseModuleContextSensitiveTest{
	protected static final String XML_DATASET_PATH = "org/openmrs/module/providermanagement/include/";

    protected static final String XML_DATASET = "providerManagement-dataset.xml";
	
	private ProviderManagementService providerManagementService;

    public static final Date DATE = ProviderManagementUtils.clearTimeComponent(new Date());

    public static final Date PAST_DATE = ProviderManagementUtils.clearTimeComponent(new Date(DATE.getTime() - 31536000000L));

    public static final Date FURTHER_PAST_DATE = ProviderManagementUtils.clearTimeComponent(new Date(PAST_DATE.getTime() - 31536000000L));

    public static final Date FUTURE_DATE = ProviderManagementUtils.clearTimeComponent(new Date(DATE.getTime() + 31536000000L));

    @Before
    public void init() throws Exception {
        // execute the provider management test dataset
        executeDataSet(XML_DATASET_PATH + XML_DATASET);

        // initialize the service
        providerManagementService = Context.getService(ProviderManagementService.class);
    }
    
    @Test
    public void testClearTimeComponent(){
    	Date date = new Date(2018, 11, 15);
    	date.setHours(10);
    	date.setMinutes(32);
    	date.setSeconds(46);
    	Date result = ProviderManagementUtils.clearTimeComponent(date);
    	Assert.assertEquals(new Date(2018, 11, 15), result);
    }
    
    @Test
    public void testClearTimeComponentofNegativeDate(){
    	Date date = new Date(1950, -1, 2);
    	date.setHours(15);
    	date.setMinutes(20);
    	date.setSeconds(12);
    	Date result = ProviderManagementUtils.clearTimeComponent(date);
    	Assert.assertEquals(new Date(1950, -1, 2), result);
    }
    
    @Test
    public void testClearTimeComponentofNewDay(){
    	Date date = new Date(2018, 7, 20);
    	Date result = ProviderManagementUtils.clearTimeComponent(date);
    	Assert.assertEquals(date, result);
    }

	@Test
   	public void shouldSetupContext() {
   		assertNotNull(Context.getService(ProviderManagementService.class));
   	}
    
    @Test
    public void shouldAssignProviderToSupervisorAndReturnListOfSupervisors() throws PersonIsNotProviderException, InvalidSupervisorException, ProviderAlreadyAssignedToSupervisorException {
    	Provider superviseeProvider = new Provider();
    	superviseeProvider.setIdentifier("1000X");
    	Person supervisee = Context.getPersonService().getPerson(6);
    	Person supervisor = Context.getPersonService().getPerson(2);
    	superviseeProvider.setPerson(supervisee);
    	providerManagementService.assignProviderToSupervisor(supervisee, supervisor);
    	Assert.assertFalse(ProviderManagementUtils.getSupervisors(superviseeProvider).isEmpty());
    	Assert.assertEquals(1, ProviderManagementUtils.getSupervisors(superviseeProvider).size());
    }
    
    @Test
    public void supervisorListShouldBeEmptyIfNoSupervisors() throws PersonIsNotProviderException {
    	Provider provider = new Provider();
    	Person person = Context.getPersonService().getPerson(6);
    	provider.setPerson(person);
    	Assert.assertTrue(ProviderManagementUtils.getSupervisors(provider).isEmpty());
    }
    
    @Test
    public void shouldAssignSuperviseeToProviderAndReturnListOfSupervisees() throws PersonIsNotProviderException, InvalidSupervisorException, ProviderAlreadyAssignedToSupervisorException {
    	Provider supervisorProvider = new Provider();
    	supervisorProvider.setIdentifier("1000X");
    	Person supervisee = Context.getPersonService().getPerson(6);
    	Person supervisor = Context.getPersonService().getPerson(2);
    	supervisorProvider.setPerson(supervisor);
    	providerManagementService.assignProviderToSupervisor(supervisee, supervisor);
    	Assert.assertFalse(ProviderManagementUtils.getSupervisees(supervisorProvider).isEmpty());
    	Assert.assertEquals(1, ProviderManagementUtils.getSupervisees(supervisorProvider).size());
    }
    
    @Test
    public void superviseeListShouldBeEmptyIfNoSupervisors() throws PersonIsNotProviderException {
    	Provider provider = new Provider();
    	Person person = Context.getPersonService().getPerson(6);
    	provider.setPerson(person);
    	Assert.assertTrue(ProviderManagementUtils.getSupervisees(provider).isEmpty());
    }
    
    @Test
    public void shouldReturnTrueForRelationshipWithStartDateInPastAndNoEndDate() {
        Relationship rel = new Relationship();
        rel.setStartDate(PAST_DATE);
        Assert.assertTrue(ProviderManagementUtils.isRelationshipActive(rel));
    }

    @Test
    public void shouldReturnTrueForRelationshipWithStartDateInPastAndEndDateInFuture() {
        Relationship rel = new Relationship();
        rel.setStartDate(PAST_DATE);
        rel.setEndDate(FUTURE_DATE);
        Assert.assertTrue(ProviderManagementUtils.isRelationshipActive(rel));
    }

    @Test
    public void shouldReturnTrueForRelationshipWithCurrentDateAsStartDateAndNoEndDate() {
        Relationship rel = new Relationship();
        rel.setStartDate(DATE);
        Assert.assertTrue(ProviderManagementUtils.isRelationshipActive(rel));
    }

    @Test
    public void shouldReturnTrueForRelationshipWithCurrentDateAsStartDateAndEndDateInFuture() {
        Relationship rel = new Relationship();
        rel.setStartDate(DATE);
        rel.setEndDate(FUTURE_DATE);
        Assert.assertTrue(ProviderManagementUtils.isRelationshipActive(rel));
    }

    @Test
    public void shouldReturnFalseForRelationshipWithStartDateInPastAndCurrentDateForEndDate() {
        Relationship rel = new Relationship();
        rel.setStartDate(PAST_DATE);
        rel.setEndDate(DATE);
        Assert.assertFalse(ProviderManagementUtils.isRelationshipActive(rel));
    }

    @Test
    public void shouldReturnFalseForRelationshipWithStartAndEndDateOnCurrentDate() {
        Relationship rel = new Relationship();
        rel.setStartDate(DATE);
        rel.setEndDate(DATE);
        Assert.assertFalse(ProviderManagementUtils.isRelationshipActive(rel));
    }

    @Test
    public void shouldReturnFalseForRelationshipWithStartAndEndDateInPast() {
        Relationship rel = new Relationship();
        rel.setStartDate(FURTHER_PAST_DATE);
        rel.setEndDate(PAST_DATE);
        Assert.assertFalse(ProviderManagementUtils.isRelationshipActive(rel));
    }

    @Test
    public void shouldReturnFalseForRelationshipWithStartDateInFuture() {
        Relationship rel = new Relationship();
        rel.setStartDate(FUTURE_DATE);
        Assert.assertFalse(ProviderManagementUtils.isRelationshipActive(rel));
    }

    @Test(expected = APIException.class)
    public void shouldThrowAPIExceptionIfEndDateBeforeStartDate() {
        Relationship rel = new Relationship();
        rel.setStartDate(FUTURE_DATE);
        rel.setEndDate(PAST_DATE);
        ProviderManagementUtils.isRelationshipActive(rel);
    }
}