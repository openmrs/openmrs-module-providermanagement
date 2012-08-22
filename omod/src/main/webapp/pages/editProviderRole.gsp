
<%  context.requirePrivilege("Provider Management - Admin")
    ui.decorateWith("providermanagement", "providerManagementAdminPage") %>

<div id="providerRoleForm">
    ${ ui.includeFragment("providermanagement", "providerRoleForm", [providerRoleId: param.providerRoleId,  successUrl: ui.pageLink("providermanagement", "manageProviderRoles"),
                                               actionButtons: [[label: ui.message("general.cancel"), link: ui.pageLink("providermanagement", "manageProviderRoles"), type: "reset"]] ]
    )}
<div>

