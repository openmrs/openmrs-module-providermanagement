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
import org.junit.Test;
import org.openmrs.Relationship;
import org.openmrs.api.APIException;

import java.util.Date;

public class ProviderManagementUtilsTest {

    public static final Date DATE = ProviderManagementUtils.clearTimeComponent(new Date());

    public static final Date PAST_DATE = ProviderManagementUtils.clearTimeComponent(new Date(DATE.getTime() - 31536000000L));

    public static final Date FURTHER_PAST_DATE = ProviderManagementUtils.clearTimeComponent(new Date(PAST_DATE.getTime() - 31536000000L));

    public static final Date FUTURE_DATE = ProviderManagementUtils.clearTimeComponent(new Date(DATE.getTime() + 31536000000L));


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
