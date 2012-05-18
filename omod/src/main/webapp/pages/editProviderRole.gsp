
<%  context.requirePrivilege("Provider Management - Admin")
    ui.decorateWith("providerManagementAdminPage") %>

<div id="providerRoleForm">
    ${ ui.includeFragment("providerRoleForm", [providerRoleId: param.providerRoleId,  successUrl: ui.pageLink("manageProviderRoles"),
                                               actionButtons: [[label: ui.message("general.cancel"), link: ui.pageLink("manageProviderRoles"), type: "reset"]] ]
    )}
<div>

