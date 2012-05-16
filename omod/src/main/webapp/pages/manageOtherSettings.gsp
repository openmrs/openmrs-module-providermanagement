<% ui.includeCss("providermanagement", "manage.css") %>

<% ui.decorateWith("providerManagementAdminPage") %>

<div id="manageOtherSettings">
    <table>
        <tr>
            <td>${ ui.message("providermanagement.setPersonAttributeTypes")}
            <br/>
            ${ ui.includeFragment("globalPropertyUpdater", [propertyName: "providermanagement.personAttributeTypes", type: "selectList",
                    options: context.getPersonService().getAllPersonAttributeTypes(false),
                    optionsKey: "name", optionsValue: "uuid", multiple: true] ) }
            </td>
        </tr>

        <tr>
            <td>${ ui.message("providermanagement.setProviderListDisplayFields")}
            <br/>
            ${ ui.includeFragment("globalPropertyUpdater", [propertyName: "providermanagement.providerListDisplayFields", type: "text"] ) }
            </td>
        </tr>

        <tr>
            <td>${ ui.message("providermanagement.setProviderSearchDisplayFields")}
            <br/>
            ${ ui.includeFragment("globalPropertyUpdater", [propertyName: "providermanagement.providerSearchDisplayFields", type: "text"] ) }
            </td>
        </tr>

        <tr>
            <td>${ ui.message("providermanagement.setPatientListDisplayFields")}
            <br/>
            ${ ui.includeFragment("globalPropertyUpdater", [propertyName: "providermanagement.patientListDisplayFields", type: "text"] ) }
            </td>
        </tr>

        <tr>
            <td>${ ui.message("providermanagement.setPatientSearchDisplayFields")}
            <br/>
            ${ ui.includeFragment("globalPropertyUpdater", [propertyName: "providermanagement.patientSearchDisplayFields", type: "text"] ) }
            </td>
        </tr>

        <tr>
            <td>${ ui.message("providermanagement.setPersonSearchDisplayFields")}
            <br/>
            ${ ui.includeFragment("globalPropertyUpdater", [propertyName: "providermanagement.personSearchDisplayFields", type: "text"] ) }
            </td>
        </tr>

        <tr>
            <td>${ ui.message("providermanagement.setAdvancedSearchPersonAttributeType")}
            <br/>
            ${ ui.includeFragment("globalPropertyUpdater", [propertyName: "providermanagement.advancedSearchPersonAttributeType", type: "selectList",
                    options: context.getPersonService().getAllPersonAttributeTypes(false),
                    optionsKey: "name", optionsValue: "uuid", multiple: false] ) }
            </td>
        </tr>
    </table>
</div>



