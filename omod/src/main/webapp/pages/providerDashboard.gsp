
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


        // TODO: this should also reset the form!
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
    ${ ui.includeFragment("providerEdit", [ actionButtons: [ [label: ui.message("general.save"), type: "submit"],
                                                                [label: ui.message("general.cancel"), id: "cancelEdit", type: "reset"] ] ]) }
</div>

<div id="supervisors">
    <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: supervisors.sort { item -> item.personName?.toString() },
            title: ui.message("providermanagement.supervisedBy"),
            columns: providerListDisplayFields,
            selectAction: ui.pageLink('providerDashboard'),
            selectIdParam: "personId" ]) %>

</div>

<% if (provider.providerRole?.superviseeProviderRoles) { %>
    <div id="supervisees">
        <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: supervisees.sort { item -> item.personName?.toString() },
                                                                    title: ui.message("providermanagement.supervising"),
                                                                    columns: providerListDisplayFields,
                                                                    selectAction: ui.pageLink('providerDashboard'),
                                                                    selectIdParam: "personId",
                                                                    formAction: ui.actionLink("providerEdit","removeSupervisees", [supervisor: person.id]),
                                                                    formFieldName: "supervisees",
                                                                    actionButtons: [[label: ui.message("general.remove"), type: "submit"]] ]) %>

    </div>

    <div id="addSupervisee">
        <%= ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.addSupervisee"),
                                                        searchAction: ui.actionLink("providerSearch", "getProviders"),
                                                        searchParams: [ providerRoles: provider.providerRole?.superviseeProviderRoles.collect { it.id } ],
                                                        resultFields: providerSearchDisplayFields,
                                                        selectAction: ui.actionLink('providerEdit', 'addSupervisee'),
                                                        selectIdParam: "supervisee",
                                                        selectParams: [ supervisor: person.id ]  ]) %>
    </div>
<% } %>

<div id="patients">
    <!-- this map is keyed on relationship types; value is a list of patients associated with the provider for that relationship type -->
    <% patientMap?.each { %>
        <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: it.value.sort { item -> item.personName.toString() },
                title: it.key.aIsToB  + " " + ui.message("providermanagement.patients"),
                columns: patientListDisplayFields,
                actionButtons: [[label: ui.message("general.remove"), type: "submit"]],
                formAction: ui.actionLink("providerEdit","removePatients", [provider: person.id, relationshipType: it.key.id ]),
                formFieldName: "patients",
                actionButtons: [[label: ui.message("general.remove"), type: "submit"]] ]) %>

        <%=  ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.addPatient"),
                searchAction: ui.actionLink("patientSearch", "getPatients"),
                resultFields: patientSearchDisplayFields,
                selectAction: ui.actionLink('providerEdit', 'addPatient'),
                selectIdParam: "patient",
                selectParams: [ provider: person.id, relationshipType: it.key.id ] ])  %>
    <% } %>
</div>