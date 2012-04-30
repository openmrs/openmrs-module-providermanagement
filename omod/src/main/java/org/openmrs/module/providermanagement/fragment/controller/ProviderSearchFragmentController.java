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

package org.openmrs.module.providermanagement.fragment.controller;

import org.apache.commons.lang3.ArrayUtils;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class ProviderSearchFragmentController {

    public List<SimpleObject> getProviders(
            HttpServletRequest request, @RequestParam(value="searchValue", required=true) String searchValue,
                                          @RequestParam(value="providerRoleIds", required=false) Integer[] providerRoleIds,
                                          @RequestParam(value="resultFields", required=false) String[] resultFields,
                                          UiUtils ui) {

        if (resultFields == null || resultFields.length == 0) {
            resultFields = new String[] {"personName"};
        }

        // always want to return the id of the result objects
         resultFields = ArrayUtils.add(resultFields, "id");

        // build the list of roles from the request params
        List<ProviderRole> providerRoles = new ArrayList<ProviderRole>();

        // TODO: may need to set providerRoleId back to a String if this doesn't work correctly once binding is fixed
        if (providerRoleIds != null && providerRoleIds.length > 0) {
            for (Integer providerRoleId : providerRoleIds) {
                providerRoles.add(Context.getService(ProviderManagementService.class).getProviderRole(providerRoleId));
            }
        }

        // TODO: we will have to hack in a way to display provider parameters here?

        // now fetch the results
        List<Person> providers = Context.getService(ProviderManagementService.class).getProviders(searchValue, null, providerRoles, false);
        return SimpleObject.fromCollection(providers, ui, resultFields);
    }

}
