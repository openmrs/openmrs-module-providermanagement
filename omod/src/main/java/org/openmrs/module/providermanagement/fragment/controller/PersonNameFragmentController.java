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

import java.lang.reflect.Method;

import org.openmrs.ui.framework.fragment.FragmentModel;

public class PersonNameFragmentController {

    /**
     * Controller method to retrieve the layout template and add it to the model.
     * 
     * @param model The fragment model to which the layout template will be added.
     * @throws Exception If there are any issues during reflection or if the layout template retrieval fails.
     */

    public void controller(FragmentModel model)throws Exception {

        Class<?> nameSupport;

        try {
            // Attempt to load the NameSupport class from org.openmrs.layout.name
            nameSupport = Class.forName("org.openmrs.layout.name.NameSupport");
        } catch (ClassNotFoundException e) {
            // If the NameSupport class is not found in org.openmrs.layout.name, try loading it from org.openmrs.layout.web.name
            nameSupport = Class.forName("org.openmrs.layout.web.name.NameSupport");
        }

        if (nameSupport == null) {
            // If the NameSupport class couldn't be loaded, return.
            return;
        }

        // Use reflection to invoke the "getInstance" method
        Method getInstance = nameSupport.getDeclaredMethod("getInstance");
                        Object instance = getInstance.invoke(null);

        // Use reflection to invoke the "getDefaultLayoutTemplate" method
        Method getLayoutTemplate = nameSupport.getMethod("getDefaultLayoutTemplate");
        Object layoutTemplate = getLayoutTemplate.invoke(instance);

        // Add the layoutTemplate to the model
        model.addAttribute("layoutTemplate", layoutTemplate);

    }
}
