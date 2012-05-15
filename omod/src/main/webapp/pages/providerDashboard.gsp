
<% ui.decorateWith("providerManagementPage")
   ui.includeCss("providermanagement", "providerDashboard.css")
   def transferSuperviseesSearchId = ui.randomId()
   def addSuperviseeSearchId = ui.randomId()
   def superviseeTableId = ui.randomId()  %>

<!-- TODO: permissions! -->

<script>
    jq(function() {
        // buttons
        jq('#showEditButton').click(function() {
            jq('#providerView').hide();
            jq('#relationshipsPanel').hide();
            jq('#providerEdit').show();


        });

        jq('#cancelEditButton').click(function() {
            jq('#providerEdit').hide();
            jq('#providerView').show();
            jq('#relationshipsPanel').show();
        });

        jq('#transferSuperviseesButton').click(function() {
            jq('#addSupervisee').hide();
            // clear out the add supervisee search form
            jq('#searchField_${ addSuperviseeSearchId }').val('');
            jq('#searchTable_${ addSuperviseeSearchId } > tbody > tr').remove();

            jq('#suggestSupervisees').hide();
            jq('#transferSupervisees').show();
        })

        jq('#transferSuperviseesCancelButton').click(function() {
            jq('#transferSupervisees').hide();

            // clear out the transfer supervisees search form
            jq('#searchField_${ transferSuperviseesSearchId }').val('');
            jq('#searchTable_${ transferSuperviseesSearchId } > tbody > tr').remove();
        })

        jq('#addSuperviseeButton').click(function() {
            jq('#transferSupervisees').hide();
            // clear out the transfer supervisees search form
            jq('#searchField_${ transferSuperviseesSearchId }').val('');
            jq('#searchTable_${ transferSuperviseesSearchId } > tbody > tr').remove();

            jq('#suggestSupervisees').hide();
            jq('#addSupervisee').show();
        })

        jq('#addSupervisee').dialog();

        jq('#addSuperviseeCancelButton').click(function() {
            jq('#addSupervisee').hide();

            // clear out the transfer supervisees search form
            jq('#searchField_${ addSuperviseeSearchId }').val('');
            jq('#searchTable_${ addSuperviseeSearchId } > tbody > tr').remove();
        })

        // bound to a click on any of the transfer patient buttons
        jq('.transferPatientsButton').click(function() {

            // first figure out the id of thie button
            var id = jq(this).attr('id').split("_")[1];

            // hide and clear all add and transfer patient divs
            jq('.addPatient').hide();
            jq('.transferPatients').hide();

            // now show the appropriate transfer div
            jq('#transferPatients_' + id).show();



            // clear out the add supervisee search form
            //jq('#searchField_${ addSuperviseeSearchId }').val('');
            //jq('#searchTable_${ addSuperviseeSearchId } > tbody > tr').remove();

            //jq('#transferSupervisees').show();
        })
    });
</script>

<div id="providerView">
    ${ ui.includeFragment("providerView", [ actionButtons: [ [label: ui.message("general.edit"), id: "showEditButton"],
                                                             [label: ui.message("general.retire"),id: "retire", confirm: ui.message("providermanagement.confirmRetire"),
                                                              link: ui.actionLink("providerEdit", "retireProvider", [provider: person.id]) ] ] ]) }
</div>

<div id="providerEdit">
    ${ ui.includeFragment("providerEdit", [ actionButtons: [ [label: ui.message("general.save"), type: "submit"],
                                                                [label: ui.message("general.cancel"), id: "cancelEditButton", type: "reset"] ] ]) }
</div>


<div id="relationshipsPanel">

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
                <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: supervisees.sort { item -> item.personName?.toString() },
                                                                            id: superviseeTableId,
                                                                            title: '',
                                                                            columns: providerListDisplayFields.values(),
                                                                            columnLabels: providerListDisplayFields.keySet(),
                                                                            selectAction: ui.pageLink('providerDashboard'),
                                                                            selectIdParam: "personId",
                                                                            formAction: ui.actionLink("providerEdit","removeSupervisees", [supervisor: person.id]),
                                                                            formFieldName: "supervisees",
                                                                            actionButtons: [[label: ui.message("general.add"), id: "addSuperviseeButton", type: "button"],
                                                                                            [label: ui.message("providermanagement.transfer"), id: "transferSuperviseesButton", type: "button"],
                                                                                            [label: ui.message("providermanagement.suggest"), id: "suggestSuperviseesButton", type: "button"],
                                                                                            [label: ui.message("general.remove"), type: "submit"]] ]) %>

            </div>

            <div id="transferSupervisees">
                <%=  ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.transferSupervisees"),
                        id: transferSuperviseesSearchId,
                        searchAction: ui.actionLink("providerSearch", "getProviders"),
                        searchParams: [ providerRoles: [ provider.providerRole?.id ] ],
                        resultFields: providerSearchDisplayFields.values(),
                        resultFieldLabels: providerSearchDisplayFields.keySet(),
                        selectAction: ui.actionLink('providerEdit', 'transferSupervisees'),
                        selectIdParam: "newSupervisor",
                        selectParams: [ oldSupervisor: person.id ],
                        selectForm: "multiSelectCheckboxForm_" + superviseeTableId,
                        actionButtons: [[label: ui.message("general.cancel"), id: "transferSuperviseesCancelButton"]] ])  %>
            </div>


            <div id="addSupervisee">
                <%= ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.addSupervisee"),
                                                                id: addSuperviseeSearchId,
                                                                searchAction: ui.actionLink("providerSearch", "getProviders"),
                                                                searchParams: [ excludeSuperviseesOf: person.id, providerRoles: provider.providerRole?.superviseeProviderRoles.collect { it.id } ],
                                                                resultFields: providerSearchDisplayFields.values(),
                                                                resultFieldLabels: providerSearchDisplayFields.keySet(),
                                                                selectAction: ui.actionLink('providerEdit', 'addSupervisee'),
                                                                selectIdParam: "supervisee",
                                                                selectParams: [ supervisor: person.id ],
                                                                actionButtons: [[label: ui.message("general.cancel"), id: "addSuperviseeCancelButton"]] ])  %>
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

            <div id="listPatients_${ id }" class="listPatients">
                <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: it.value.sort { item -> item.personName.toString() },
                        id: id,
                        title: it.key.aIsToB  + " " + ui.message("providermanagement.patients"),
                        columns: patientListDisplayFields.values(),
                        columnLabels: patientListDisplayFields.keySet(),
                        formAction: ui.actionLink("providerEdit","removePatients", [provider: person.id, relationshipType: it.key.id ]),
                        formFieldName: "patients",
                        actionButtons: [[label: ui.message("general.add"), id: "addPatientButton_${ id }", class: "addPatientButton", type: "button"],
                                        [label: ui.message("providermanagement.transfer"), id: "transferPatientsButton_${ id }", class: "transferPatientsButton", type: "button"],
                                        [label: ui.message("general.remove"), type: "submit"]] ]) %>
            </div>

            <div id="transferPatients_${ id }" class="transferPatients">
                <%=  ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.transferPatients"),
                        searchAction: ui.actionLink("providerSearch", "getProviders"),
                        searchParams: [ providerRoles: [ provider.providerRole?.id ] ],
                        resultFields: providerSearchDisplayFields.values(),
                        resultFieldLabels: providerSearchDisplayFields.keySet(),
                        selectAction: ui.actionLink('providerEdit', 'transferPatients'),
                        selectIdParam: "newProvider",
                        selectParams: [ oldProvider: person.id, relationshipType: it.key.id ],
                        selectForm: "multiSelectCheckboxForm_" + id])  %>
            </div>

            <div id="addPatient_${ id }" class="addPatient">
                <%=  ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.addPatient"),
                        searchAction: ui.actionLink("patientSearch", "getPatients"),
                        searchParams: [excludePatientsOf: person.id, existingRelationshipTypeToExclude: it.key.id ],
                        resultFields: patientSearchDisplayFields.values(),
                        resultFieldLabels: patientSearchDisplayFields.keySet(),
                        selectAction: ui.actionLink('providerEdit', 'addPatient'),
                        selectIdParam: "patient",
                        selectParams: [ provider: person.id, relationshipType: it.key.id ] ])  %>
            </div>

            <br/><br/>
        <% } %>
    </div>

</div>
