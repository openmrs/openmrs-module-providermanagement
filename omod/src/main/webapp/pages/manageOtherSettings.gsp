
<% ui.decorateWith("providerManagementAdminPage") %>

<table>
    <tr>
        <td>${ ui.message("providermanagement.setPersonAttributes")}</td>
        <td>${ ui.includeFragment("globalPropertyUpdater", [propertyName: "providermanagement.personAttributes",
                options: context.getPersonService().getAllPersonAttributeTypes(false),
                keyField: "name", valueField: "uuid", multiple: true] ) }
        </td>
    </tr>
</table>



