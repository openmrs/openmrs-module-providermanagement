
<% ui.decorateWith("providerManagementPage") %>

<% ui.includeCss("providermanagement", "providerDashboard.css") %>

<!-- TODO: permissions! -->

<div id="providerDetails">
    ${ ui.includeFragment("providerView", [ actionButtons: [ [label: ui.message("general.edit")],
                                                             [label: ui.message("general.retire")]] ]) }
</div>

<div id="patients">
    <% patients?.each { %>
        ${ ui.includeFragment("widget/multiSelectCheckboxTable", [ items: it.value,
                                                                    title: it.key.aIsToB  + " " + ui.message("providermanagement.patients"),
                                                                    columns: ["personName"],
                                                                    actionButtons: [[label: ui.message("general.remove")]] ]) }
    <% } %>
</div>

<% if (provider.providerRole?.superviseeProviderRoles) { %>
    <div id="supervisees">
        ${ ui.includeFragment("widget/multiSelectCheckboxTable", [ items: supervisees,
                                                                    title: ui.message("providermanagement.supervising"),
                                                                    columns: ["personName"],
                                                                    actionButtons: [[label: ui.message("general.remove")]] ]) }

    </div>

    <div id="addSupervisee">
        ${ ui.includeFragment("widget/providerSearch", [title: ui.message("providermanagement.addSupervisee"),
                                                        roles: provider.providerRole?.superviseeProviderRoles,
                                                        actionButtons: [[label: ui.message("general.add"), link: ui.pageLink('addSupervisee', [personId: person.id])]] ]) }
    </div>

<% } %>