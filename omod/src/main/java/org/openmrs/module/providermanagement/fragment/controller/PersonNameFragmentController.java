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

    public void controller(FragmentModel model)throws Exception {
        
       /*
        * backward compatibility
        * dynamic class loading and reflection to interact with classes that provide layout templates for perso names in the system, allowing for flexibility and extensibility in managing name layouts.
        * using classloader to call the NameSupport classes creating an instance
        * using reflection method to access and invoke the methods of NameSupport class
        */
        Class<?> nameSupport;

        try {
            nameSupport = Class.forName("org.openmrs.layout.name.NameSupport");
        } catch (ClassNotFoundException e) {
            nameSupport = Class.forName("org.openmrs.layout.web.name.NameSupport");
        }

        if (nameSupport == null) {
            return;
        }

        Method getInstance = nameSupport.getDeclaredMethod("getInstance");
                        Object instance = getInstance.invoke(null);

        Method getLayoutTemplate = nameSupport.getMethod("getDefaultLayoutTemplate");
        Object layoutTemplate = getLayoutTemplate.invoke(instance);

        model.addAttribute("layoutTemplate", layoutTemplate);
 
    }

}
