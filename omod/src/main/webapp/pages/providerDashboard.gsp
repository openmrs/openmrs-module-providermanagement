
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
                                                             [label: ui.message("general.retire"),id: "retire", confirm: ui.message("providermanagement.confirmRetire"),
                                                              link: ui.actionLink("providerEdit", "retireProvider", [provider: person.id]) ] ] ]) }
</div>

<div id="providerEdit">
    ${ ui.includeFragment("providerEdit", [ actionButtons: [ [label: ui.message("general.save"), type: "submit"],
                                                                [label: ui.message("general.cancel"), id: "cancelEdit", type: "reset"] ] ]) }
</div>

<div id="supervisors">
    <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: supervisors.sort { item -> item.personName?.toString() },
            title: ui.message("providermanagement.supervisedBy"),
            columns: providerListDisplayFields.values(),
            columnLabels: providerListDisplayFields.keySet(),
            selectAction: ui.pageLink('providerDashboard'),
            selectIdParam: "personId" ]) %>

</div>

<% if (provider.providerRole?.isSupervisorRole()) { %>
    <div id="supervisees">

        <div id="listSupervisees">
            <%  def superviseeTableId = ui.randomId() %>
            <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: supervisees.sort { item -> item.personName?.toString() },
                                                                        id: superviseeTableId,
                                                                        title: ui.message("providermanagement.supervising"),
                                                                        columns: providerListDisplayFields.values(),
                                                                        columnLabels: providerListDisplayFields.keySet(),
                                                                        selectAction: ui.pageLink('providerDashboard'),
                                                                        selectIdParam: "personId",
                                                                        formAction: ui.actionLink("providerEdit","removeSupervisees", [supervisor: person.id]),
                                                                        formFieldName: "supervisees",
                                                                        actionButtons: [[label: ui.message("general.remove"), type: "submit"]] ]) %>

        </div>

        <div id="transferSupervisees">
            <%=  ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.transferSupervisees"),
                    searchAction: ui.actionLink("providerSearch", "getProviders"),
                    searchParams: [ providerRoles: [ provider.providerRole?.id ] ],
                    resultFields: providerSearchDisplayFields.values(),
                    resultFieldLabels: providerSearchDisplayFields.keySet(),
                    selectAction: ui.actionLink('providerEdit', 'transferSupervisees'),
                    selectIdParam: "newSupervisor",
                    selectParams: [ oldSupervisor: person.id ],
                    selectForm: "multiSelectCheckboxForm_" + superviseeTableId])  %>
        </div>


        <div id="addSupervisee">
            <%= ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.addSupervisee"),
                                                            searchAction: ui.actionLink("providerSearch", "getProviders"),
                                                            searchParams: [ excludeSuperviseesOf: person.id, providerRoles: provider.providerRole?.superviseeProviderRoles.collect { it.id } ],
                                                            resultFields: providerSearchDisplayFields.values(),
                                                            resultFields: providerSearchDisplayFields.keySet(),
                                                            selectAction: ui.actionLink('providerEdit', 'addSupervisee'),
                                                            selectIdParam: "supervisee",
                                                            selectParams: [ supervisor: person.id ]  ]) %>
        </div>

        <% if (suggestedSupervisees != null) { %>   <!-- note that we want to display this if the results are an empty list, hence the explicit test for null here -->
            <div id="suggestedSupervisees">
                <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: suggestedSupervisees.sort { item -> item.personName?.toString() },
                        title: ui.message("providermanagement.suggestedSupervisees"),
                        columns: providerListDisplayFields.values(),
                        columnLabels: providerListDisplayFields.keySet(),
                        selectAction: ui.pageLink('providerDashboard'),
                        selectIdParam: "personId",
                        formAction: ui.actionLink("providerEdit","addSupervisees", [supervisor: person.id]),
                        formFieldName: "supervisees",
                        actionButtons: [[label: ui.message("general.add"), type: "submit"]] ]) %>
            </div>
        <% } %>
    </div>

<% } %>

<div id="patients">
    <!-- this map is keyed on relationship types; value is a list of patients associated with the provider for that relationship type -->
    <% patientMap?.each {
         def id = ui.randomId()   %>
        <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: it.value.sort { item -> item.personName.toString() },
                id: id,
                title: it.key.aIsToB  + " " + ui.message("providermanagement.patients"),
                columns: patientListDisplayFields.values(),
                columnLabels: patientListDisplayFields.keySet(),
                formAction: ui.actionLink("providerEdit","removePatients", [provider: person.id, relationshipType: it.key.id ]),
                formFieldName: "patients",
                actionButtons: [[label: ui.message("general.remove"), type: "submit"]] ]) %>

        <%=  ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.transferPatients"),
                searchAction: ui.actionLink("providerSearch", "getProviders"),
                searchParams: [ providerRoles: [ provider.providerRole?.id ] ],
                resultFields: providerSearchDisplayFields.values(),
                resultFieldLabels: providerSearchDisplayFields.keySet(),
                selectAction: ui.actionLink('providerEdit', 'transferPatients'),
                selectIdParam: "newProvider",
                selectParams: [ oldProvider: person.id, relationshipType: it.key.id ],
                selectForm: "multiSelectCheckboxForm_" + id])  %>

        <%=  ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.addPatient"),
                searchAction: ui.actionLink("patientSearch", "getPatients"),
                searchParams: [excludePatientsOf: person.id, existingRelationshipTypeToExclude: it.key.id ],
                resultFields: patientSearchDisplayFields.values(),
                resultFieldLabels: patientSearchDisplayFields.keySet(),
                selectAction: ui.actionLink('providerEdit', 'addPatient'),
                selectIdParam: "patient",
                selectParams: [ provider: person.id, relationshipType: it.key.id ] ])  %>
    <% } %>
</div>

