
<% ui.decorateWith("providerManagementPage") %>

<% ui.includeCss("providermanagement", "providerDashboard.css") %>

<!-- TODO: permissions! -->

<script>
    jq(function() {
        // buttons
        jq('#showEdit').click(function() {
            jq('#providerView').hide();
            jq('#providerEdit').show();
        });
        jq('#cancelEdit').click(function() {
            jq('#providerEdit').hide();
            jq('#providerView').show();
        });
    });
</script>

<div id="providerView">
    ${ ui.includeFragment("providerView", [ actionButtons: [ [label: ui.message("general.edit"), id: "showEdit"],
                                                             [label: ui.message("general.retire")]] ]) }
</div>

<div id="providerEdit">
    ${ ui.includeFragment("providerEdit", [ actionButtons: [ [label: ui.message("general.save")],
                                                                [label: ui.message("general.cancel"), id: "cancelEdit"] ] ]) }
</div>

<div id="patients">
    <% patients?.each { %>
        <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: it.value.sort { item -> item.personName.toString() },
                                                                        title: it.key.aIsToB  + " " + ui.message("providermanagement.patients"),
                                                                        columns: ["personName"],
                                                                        actionButtons: [[label: ui.message("general.remove")]] ]) %>

        <%=  ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.addPatient"),
                                                        searchAction: ui.actionLink("patientSearch", "getPatients"),
                                                        resultFields: ["personName"],
                                                        selectAction: ui.actionLink('providerUpdate', 'addPatient'),
                                                        selectParams: [ [key: 'providerId', value: person.id],
                                                                        [key: 'relationshipTypeId', value: it.key.id ]] ])  %>
    <% } %>
</div>

<% if (provider.providerRole?.superviseeProviderRoles) { %>
    <div id="supervisees">
        <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: supervisees.sort { item -> item.personName.toString() },
                                                                    title: ui.message("providermanagement.supervising"),
                                                                    columns: ["personName"],
                                                                    selectAction: ui.pageLink('providerDashboard'),
                                                                    selectIdParam: "personId",
                                                                    actionButtons: [[label: ui.message("general.remove")]] ]) %>

    </div>

    <div id="addSupervisee">
        <%= ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.addSupervisee"),
                                                        searchAction: ui.actionLink("providerSearch", "getProviders"),
                                                        searchParams: [ [key: "providerRoleIds", value: provider.providerRole?.superviseeProviderRoles.collect { it.id } ]],
                                                        resultFields: ["personName"],
                                                        selectAction: ui.actionLink('providerUpdate', 'addSupervisee'),
                                                        selectParams: [ [key: 'superviserId', value: person.id] ] ]) %>
    </div>

<% } %>