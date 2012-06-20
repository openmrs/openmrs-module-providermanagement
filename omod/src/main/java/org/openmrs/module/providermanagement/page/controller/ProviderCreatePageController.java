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

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderManagementWebUtil;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ProviderCreatePageController {

    public void controller (PageModel pageModel,
                            @RequestParam(value = "person", required = false) Person personParam,
                            @RequestParam(value = "personId", required = false) Integer personId,
                            @RequestParam(value = "name", required = false) String name,
                            UiUtils ui) {

        // fetch any person that may have been specified
        Person person = ProviderManagementWebUtil.getPerson(personParam, personId);

        // if no person has been specified, see if we have received a name that we should populate
        // the new record with
        if (person == null && StringUtils.isNotBlank(name)) {
            person = new Person();
            person.addName(Context.getPersonService().parsePersonName(name));
        }

        pageModel.addAttribute("person", person);
    }
}
