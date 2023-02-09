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

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.openmrs.layout.web.name.NameSupport;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsConstants;


public class PersonNameFragmentController {
	String openmrsVersion = OpenmrsConstants.OPENMRS_VERSION_SHORT;

	ComparableVersion version_1 = new ComparableVersion(openmrsVersion);
	ComparableVersion version_2 = new ComparableVersion("2.0.0");

	 
		/**
	 * @param model
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */


	 
	 public void controller(FragmentModel model) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		
		
		Class<?> cls1 = Class.forName("org.openmrs.layout.LayoutSupport");
		Class<?> cls = Class.forName("org.openmrs.layout.name.NameSupport");
		Method getInstance = cls.getMethod("getInstance");
		Object instance = getInstance.invoke(null);
		Method getDefaultLayoutTemplate = cls1.getDeclaredMethod("getDefaultLayoutTemplate");
		Object layoutTemplate = getDefaultLayoutTemplate.invoke(instance);	

		if(version_1.compareTo(version_2) >= 0)
			model.addAttribute("layoutTemplate", layoutTemplate );
		else if(version_1.compareTo(version_2) < 0)
			model.addAttribute("layoutTemplate", NameSupport.getInstance().getDefaultLayoutTemplate() );
	
	
	
	}

}

