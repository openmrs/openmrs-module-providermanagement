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

package org.openmrs.module.providermanagement.page.controller;

import org.openmrs.module.providermanagement.ProviderManagementGlobalProperties;
import org.openmrs.ui.framework.page.PageModel;

public class ProviderHomePageController {

    public void controller(PageModel pageModel) {
        // add the global properties that specify the fields to display in the provider and person search results
        pageModel.addAttribute("providerSearchDisplayFields", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PROVIDER_SEARCH_DISPLAY_FIELDS());
        pageModel.addAttribute("personSearchDisplayFields", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PERSON_SEARCH_DISPLAY_FIELDS());
    }
}
