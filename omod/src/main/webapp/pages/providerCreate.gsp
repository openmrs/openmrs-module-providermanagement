<%  context.requirePrivilege("Provider Management Dashboard - Edit Providers")
    ui.decorateWith("providermanagement", "providerManagementPage") %>

<% ui.includeCss("providermanagement", "providerCreate.css") %>

<div id="providerEdit">
    ${ ui.includeFragment("providermanagement", "providerEdit", [ successUrl: ui.pageLink("providermanagement", "providerDashboard"),
                                            actionButtons: [ [label: ui.message("general.save"), type: "submit"],
                                                            [label: ui.message("general.cancel"), link: ui.pageLink("providermanagement", "providerHome")] ] ]) }
</div>



