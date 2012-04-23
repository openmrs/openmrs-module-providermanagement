
<% ui.decorateWith("providerManagementPage") %>

<% ui.includeCss("providermanagement", "providerDashboard.css") %>

<div id="providerDetails">
    ${ ui.includeFragment("providerView", [ actionButtons: [ [label: ui.message("general.edit")],
                                                             [label: ui.message("general.retire")]] ]) }
</div>

<div id="patients">
    <% patients?.each { %>
        ${ ui.includeFragment("widget/multiSelectCheckboxTable", [ items: it.value,
                                                                    title: it.key.aIsToB  + " " + ui.message("providermanagement.patients"),
                                                                    columns: ["personName"],
                                                                    actionButtons: [[label: ui.message("general.add")],
                                                                                    [label: ui.message("general.remove")],
                                                                                    [label: ui.message("providermanagement.transfer")]] ]) }
    <% } %>
</div>

<div id="supervisees">
    ${ ui.includeFragment("widget/multiSelectCheckboxTable", [ items: supervisees,
                                                                title: ui.message("providermanagement.supervising"),
                                                                columns: ["personName"],
                                                                actionButtons: [[label: ui.message("general.add")],
                                                                        [label: ui.message("general.remove")],
                                                                        [label: ui.message("providermanagement.transfer")]] ]) }
</div>
