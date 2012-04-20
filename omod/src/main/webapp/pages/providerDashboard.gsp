
<% ui.decorateWith("providerManagementPage") %>

<% ui.includeCss("providermanagement", "providerDashboard.css") %>

<div id="providerDetails">
    ${ ui.includeFragment("providerView", [ personId: param.personId,
                                            actionButtons: [ [label: ui.message("general.edit")],
                                                             [label: ui.message("general.retire")]] ]) }
</div>

