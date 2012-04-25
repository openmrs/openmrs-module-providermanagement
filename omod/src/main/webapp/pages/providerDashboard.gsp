
<% ui.decorateWith("providerManagementPage") %>

<% ui.includeCss("providermanagement", "providerDashboard.css") %>

<!-- TODO: permissions! -->

<div id="providerDetails">
    ${ ui.includeFragment("providerView", [ actionButtons: [ [label: ui.message("general.edit")],
                                                             [label: ui.message("general.retire")]] ]) }
</div>

<div id="patients">
    <% patients?.each { %>
        <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: it.value.sort { item -> item.personName.toString() },
                                                                    title: it.key.aIsToB  + " " + ui.message("providermanagement.patients"),
                                                                    columns: ["personName"],
                                                                    actionButtons: [[label: ui.message("general.remove")]] ]) %>

        ${ ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.addPatient"),
                searchAction: ui.actionLink("patientSearch", "getPatients"),
                resultFields: ["personName"],
                selectAction: ui.actionLink('providerUpdate', 'addPatient'),
                selectParams: [ [key: 'providerId', value: person.id],
                                [key: 'relationshipTypeId', value: it.key.id ]] ]) }

    <% } %>
</div>

<% if (provider.providerRole?.superviseeProviderRoles) { %>
    <div id="supervisees">
        <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: supervisees.sort { item -> item.personName.toString() },
                                                                    title: ui.message("providermanagement.supervising"),
                                                                    columns: ["personName"],
                                                                    actionButtons: [[label: ui.message("general.remove")]] ]) %>

    </div>

    <div id="addSupervisee">
        ${ ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.addSupervisee"),
                                                    searchAction: ui.actionLink("providerSearch", "getProviders"),
                                                    resultFields: ["personName"],
                                                    searchParams: [ [key: "providerRoleIds", value: provider.providerRole?.superviseeProviderRoles.collect { it.id } ]],
                                                    selectAction: ui.actionLink('providerUpdate', 'addSupervisee'),
                                                    selectParams: [ [key: 'superviserId', value: person.id] ] ]) }
    </div>

<% } %>