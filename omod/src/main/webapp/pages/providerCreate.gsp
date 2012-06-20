<%  context.requirePrivilege("Provider Management Dashboard - Edit Providers")
    ui.decorateWith("providerManagementPage") %>

<% ui.includeCss("providermanagement", "providerCreate.css") %>

<div id="providerEdit">
    ${ ui.includeFragment("providerEdit", [ actionButtons: [ [label: ui.message("general.save"), type: "submit"],
            [label: ui.message("general.cancel"), link: ui.pageLink("providerHome")] ] ]) }
</div>



