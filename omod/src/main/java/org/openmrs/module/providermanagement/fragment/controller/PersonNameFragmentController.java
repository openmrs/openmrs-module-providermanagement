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
import java.lang.reflect.InvocationTargetException;

import org.openmrs.ui.framework.fragment.FragmentModel;

public class PersonNameFragmentController {
	
	/**
	 * @param model
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	
	public void controller(FragmentModel model) throws ClassNotFoundException, NoSuchMethodException, SecurityException,
	        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		/*
		 * backward compatibility
		 */
		Class<?> nameSurpport;
		
		try {
			nameSurpport = Class.forName("org.openmrs.layout.name.NameSupport");
		}
		catch (ClassNotFoundException e) {
			nameSurpport = Class.forName("org.openmrs.layout.web.name.NameSupport");
		}
		
		Method getInstance = nameSurpport.getDeclaredMethod("getInstance");
		Object instance = getInstance.invoke(null);
		
		Method getLayoutTemplate = nameSurpport.getMethod("getDefaultLayoutTemplate");
		Object layoutTemplate = getLayoutTemplate.invoke(instance);
		
		model.addAttribute("layoutTemplate", layoutTemplate);
		
	}
	
}
