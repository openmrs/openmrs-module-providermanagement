
<% ui.decorateWith("providerManagementPage") %>

<% ui.includeCss("providermanagement", "providerCreate.css") %>

<!-- TODO: permissions! -->

<div id="providerEdit">
    ${ ui.includeFragment("providerEdit", [ actionButtons: [ [label: ui.message("general.save"), type: "submit"],
            [label: ui.message("general.cancel"), link: ui.pageLink("providerHome")] ] ]) }
</div>



