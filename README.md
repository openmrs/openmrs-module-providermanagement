[![Build Status](https://travis-ci.org/openmrs/openmrs-module-providermanagement.svg?branch=master)](https://travis-ci.org/openmrs/openmrs-module-providermanagement)

Provider Management Module
===========================

## Overview

Allows for the creation of provider roles, as well as the management of provider/provider and provider/patient relationships.

## Purpose

- The Provider Management module assists in the management of health care providers by providing a means to:
	- Search for providers and edit their demographic information
	- Assign/unassign patients to providers (via relationships)
	- Assign/unassign supervisees to providers (via relationships)
- To facilitate this, the module adds new metadata, Provider Role, which is linked directly to a Provider via foreign key.

## Requirements
- OpenMRS 1.9.0+
- UI Framework Module 1.7+
- UI Library Module 1.4+
- Logic Module 0.5.2 (This is required only for OpenMRS versions 1.9.\*. Since the logic module is a core module for OpenMRS 1.9.\*, it should automatically be available).

## Installation
	git clone https://github.com/openmrs/openmrs-module-providermanagement.git
	cd openmrs-module-providermanagement
	mvn clean install

## Additional Documentation

Additional documentation can be found on the community wiki page for the Provider Management Module, available here:

https://wiki.openmrs.org/display/docs/Provider+Management+Module

## Reporting Issues

The Provider Management Module utilizes a JIRA page in which bugs and desired improvements can be reported, available here:

https://issues.openmrs.org/projects/PROV/issues