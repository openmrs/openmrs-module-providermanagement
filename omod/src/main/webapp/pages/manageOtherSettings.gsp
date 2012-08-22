<% context.requirePrivilege("Provider Management - Admin")
   ui.includeCss("providermanagement", "manage.css")
   ui.decorateWith("providermanagement", "providerManagementAdminPage") %>

<div id="manageOtherSettings">
    <table>
        <tr>
            <td>${ ui.message("providermanagement.setPersonAttributeTypes")}
            <br/>
            ${ ui.includeFragment("providermanagement", "globalPropertyUpdater", [propertyName: "providermanagement.personAttributeTypes", type: "selectList",
                    options: context.getPersonService().getAllPersonAttributeTypes(false),
                    optionsKey: "name", optionsValue: "uuid", multiple: true] ) }
            </td>
        </tr>

        <tr>
            <td>${ ui.message("providermanagement.setProviderListDisplayFields")}
            <br/>
            ${ ui.includeFragment("providermanagement", "globalPropertyUpdater", [propertyName: "providermanagement.providerListDisplayFields", type: "text"] ) }
            </td>
        </tr>

        <tr>
            <td>${ ui.message("providermanagement.setProviderSearchDisplayFields")}
            <br/>
            ${ ui.includeFragment("providermanagement", "globalPropertyUpdater", [propertyName: "providermanagement.providerSearchDisplayFields", type: "text"] ) }
            </td>
        </tr>

        <tr>
            <td>${ ui.message("providermanagement.setPatientListDisplayFields")}
            <br/>
            ${ ui.includeFragment("providermanagement", "globalPropertyUpdater", [propertyName: "providermanagement.patientListDisplayFields", type: "text"] ) }
            </td>
        </tr>

        <tr>
            <td>${ ui.message("providermanagement.setPatientSearchDisplayFields")}
            <br/>
            ${ ui.includeFragment("providermanagement", "globalPropertyUpdater", [propertyName: "providermanagement.patientSearchDisplayFields", type: "text"] ) }
            </td>
        </tr>

        <tr>
            <td>${ ui.message("providermanagement.setPersonSearchDisplayFields")}
            <br/>
            ${ ui.includeFragment("providermanagement", "globalPropertyUpdater", [propertyName: "providermanagement.personSearchDisplayFields", type: "text"] ) }
            </td>
        </tr>

        <tr>
            <td>${ ui.message("providermanagement.setAdvancedSearchPersonAttributeType")}
            <br/>
            ${ ui.includeFragment("providermanagement", "globalPropertyUpdater", [propertyName: "providermanagement.advancedSearchPersonAttributeType", type: "selectList",
                    options: context.getPersonService().getAllPersonAttributeTypes(false),
                    optionsKey: "name", optionsValue: "uuid", multiple: false] ) }
            </td>
        </tr>

        <tr>
            <td>${ ui.message("providermanagement.selectAddressWidget")}
                <br/>
                ${ ui.includeFragment("providermanagement", "globalPropertyUpdater", [propertyName: "providermanagement.addressWidget", type: "selectList",
                        options: [[name: "Standard", value: "personAddress"], [name: "Rwanda Address Hierarchy", value: "rwanda/addressHierarchyRwanda"]],
                        optionsKey: "name", optionsValue: "value", multiple: false] ) }
            </td>
        </tr>

        <tr>
            <td>${ ui.message("providermanagement.selectToRestrictSearchToProvidersWithProviderRoles")}
                <br/>
                ${ ui.includeFragment("providermanagement", "globalPropertyUpdater", [propertyName: "providermanagement.restrictSearchToProvidersWithProviderRoles", type: "selectList",
                        options: [[name: ui.message("providermanagement.restrictSearchResults"), value: "true"],
                                    [name: ui.message("providermanagement.doNotRestrictSearchResults"), value: "false"]],
                        optionsKey: "name", optionsValue: "value", multiple: false] ) }
            </td>
        </tr>

    </table>
</div>



