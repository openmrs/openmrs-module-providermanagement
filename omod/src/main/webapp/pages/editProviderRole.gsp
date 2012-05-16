

<% ui.decorateWith("providerManagementAdminPage") %>

<div id="providerRoleForm">
    ${ ui.includeFragment("providerRoleForm", [providerRoleId: param.providerRoleId,  successUrl: ui.pageLink("manageProviderRoles")]) }
<div>

