
<% ui.decorateWith("providerManagementAdminPage") %>

<table>
    <tr>
        <td>${ ui.message("providermanagement.setPersonAttributeTypes")}</td>
        <td>${ ui.includeFragment("globalPropertyUpdater", [propertyName: "providermanagement.personAttributeTypes", type: "selectList",
                options: context.getPersonService().getAllPersonAttributeTypes(false),
                optionsKey: "name", optionsValue: "uuid", multiple: true] ) }
        </td>
    </tr>

    <tr>
        <td>${ ui.message("providermanagement.setProviderSearchDisplayFields")}</td>
        <td>${ ui.includeFragment("globalPropertyUpdater", [propertyName: "providermanagement.providerSearchDisplayFields", type: "text"] ) }
        </td>
    </tr>
</table>



