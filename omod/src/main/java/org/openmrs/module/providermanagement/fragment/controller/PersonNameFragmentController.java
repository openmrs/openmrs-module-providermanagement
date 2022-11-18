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

import java.lang.reflect.*;

import org.openmrs.layout.web.name.NameSupport;
import org.openmrs.ui.framework.fragment.FragmentModel;

public class PersonNameFragmentController {

    /**
     * @param model
     */
    public void controller(FragmentModel model) {
        System.out.println(
                "***************************************this works properly man!!!******************************");
        try {
            Class cls = Class.forName("org.openmrs.layout.name.NameSupport");

            Constructor ctorlist[] = cls.getDeclaredConstructors();
            
        } catch (Throwable e) {
            System.err.println(e);
        }
        model.addAttribute("layoutTemplate", NameSupport.getInstance().getDefaultLayoutTemplate());
    }

}
