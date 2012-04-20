

<% ui.decorateWith("providerManagementAdminPage") %>

${ ui.includeFragment("providerRoleForm", [providerRoleId: param.providerRoleId,  successUrl: ui.pageLink("manageProviderRoles")]) }

