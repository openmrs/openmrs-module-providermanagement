
<% ui.decorateWith("providerManagementPage")
   ui.includeCss("providermanagement", "providerDashboard.css")

   def transferSuperviseesSearchId = ui.randomId()
   def addSuperviseeSearchId = ui.randomId()
   def superviseeTableId = ui.randomId()
%>

<!-- TODO: permissions! -->

<script>
    jq(function() {

        // handles showing/hiding the provider edit pane
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

        // handles showing the patients pane
        jq('.patientPaneSelect').click(function(){
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            // remove any existing hightlights from the table
            jq('.paneSelect').removeClass('selected');

            // hide any existing panes on display
            jq('#supervisees').hide();
            jq('#supervisors').hide();
            jq('.patients').hide();

            // highlight selection and show the appropriate patient pane
            jq('#patientPaneSelectTop_' + id).addClass('selected');
            jq('#patientPaneSelectBottom_' + id).addClass('selected');
            jq('#patient_' + id).show();
        });


        // handles showing the supervisee pane
        jq('.superviseePaneSelect').click(function() {
            // remove any existing highlights from the table
            jq('.paneSelect').removeClass('selected');

            // hide any existing panes on display
            jq('#supervisors').hide();
            jq('.patients').hide();

            // highlight selection and show the supervisee pane
            jq('.superviseePaneSelect').addClass('selected');
            jq('#supervisees').show();
        });

        // handles showing the supervisor pane
        jq('.supervisorPaneSelect').click(function() {
            // remove any existing highlights from the table
            jq('.paneSelect').removeClass('selected');

            // hide any existing panes on display
            jq('#supervisees').hide();
            jq('.patients').hide();

            // highlight selection and show supervisor panel
            jq('.supervisorPaneSelect').addClass('selected');
            jq('#supervisors').show();
        });


        // handles showing/hiding the add and transfer panes within the supervisees pane
        jq('#transferSuperviseesButton').click(function() {
            jq('#addSupervisee').hide();
            // clear out the add supervisee search form
            jq('#searchField_${ addSuperviseeSearchId }').val('');
            jq('#searchTable_${ addSuperviseeSearchId } > tbody > tr').remove();

            jq('#suggestSupervisees').hide();
            jq('#transferSupervisees').show();
        }) ;

        jq('#transferSuperviseesCancelButton').click(function() {
            jq('#transferSupervisees').hide();

            // clear out the transfer supervisees search form
            jq('#searchField_${ transferSuperviseesSearchId }').val('');
            jq('#searchTable_${ transferSuperviseesSearchId } > tbody > tr').remove();
        });

        jq('#addSuperviseeButton').click(function() {
            jq('#transferSupervisees').hide();
            // clear out the transfer supervisees search form
            jq('#searchField_${ transferSuperviseesSearchId }').val('');
            jq('#searchTable_${ transferSuperviseesSearchId } > tbody > tr').remove();

            jq('#suggestSupervisees').hide();
            jq('#addSupervisee').show();
        });

        jq('#addSuperviseeCancelButton').click(function() {
            jq('#addSupervisee').hide();

            // clear out the add supervisees search form
            jq('#searchField_${ addSuperviseeSearchId }').val('');
            jq('#searchTable_${ addSuperviseeSearchId } > tbody > tr').remove();
        });

        // handles showing/hiding the add and transfer panes within the patient pane
        jq('.addPatientButton').click(function() {
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            // clear out the associated transfer patients form
            jq('#searchField_' + id).val('');
            jq('#searchTable_' + id + ' > tbody > tr').remove();

            jq('#addPatient_' + id).show();
        });

        jq('.addPatientCancelButton').click(function() {
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            jq('#addPatient_' + id).hide();

            // clear out the add patients search form
            jq('#searchField_' + id).val('');
            jq('#searchTable_' + id + ' > tbody > tr').remove();
        });

    });
</script>

<div id="providerTopBar">

</div>

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

    <div id="patientSupervisorHeader">
        <table>
            <tr>
                <td class="borderCell">&nbsp;</td>

                <% patientMap?.each { %>
                    <td id="patientPaneSelectTop_${ it.key.id }" class="patientPaneSelect paneSelect"> <img src=" ${ ui.resourceLink ("images/patient-nested.png") }"/></td>
                <% } %>

                <% if (provider.providerRole?.isSupervisorRole()) { %>
                    <td class="superviseePaneSelect paneSelect"> <img src=" ${ ui.resourceLink ("images/supervisee-nested.png") }"/></td>
                <% } %>

                <td class="supervisorPaneSelect paneSelect"> <img src=" ${ ui.resourceLink ("images/supervisor-nested.png") }"/></td>

                <td class="borderCell">&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>

                <% patientMap?.each { %>
                    <td id="patientPaneSelectBottom_${ it.key.id }" class="patientPaneSelect paneSelect">${ it.key.aIsToB }<br/>${ ui.message("providermanagement.patients") }</td>
                <% } %>

                <% if (provider.providerRole?.isSupervisorRole()) { %>
                    <td class="superviseePaneSelect paneSelect">${ ui.message("providermanagement.supervisees") }</td>
                <% } %>

                <td class="supervisorPaneSelect paneSelect">${ ui.message("providermanagement.supervisors") }</td>

                <td>&nbsp;</td>
            </tr>
        </table>

    </div>

    <div id="patientSupervisorHeaderDivider">

    </div>

    <div id="supervisors">
        <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: supervisors.sort { item -> item.personName?.toString() },
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


        <!-- this map is keyed on relationship types; value is a list of patients associated with the provider for that relationship type -->
    <% patientMap?.each {   %>

        <div id="patient_${ it.key.id }" class="patients">

            <div id="listPatients_${ it.key.id }" class="listPatients">
                <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: it.value.sort { item -> item.personName.toString() },
                        id: it.key.id,
                        columns: patientListDisplayFields.values(),
                        columnLabels: patientListDisplayFields.keySet(),
                        formAction: ui.actionLink("providerEdit","removePatients", [provider: person.id, relationshipType: it.key.id ]),
                        formFieldName: "patients",
                        actionButtons: [[label: ui.message("general.add"), id: "addPatientButton_${ it.key.id }", class: "addPatientButton", type: "button"],
                                        [label: ui.message("providermanagement.transfer"), id: "transferPatientsButton_${ it.key.id }", class: "transferPatientsButton", type: "button"],
                                        [label: ui.message("general.remove"), type: "submit"]] ]) %>
            </div>

            <div id="transferPatients_${ it.key.id }" class="transferPatients">
                <%=  ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.transferPatients"),
                        searchAction: ui.actionLink("providerSearch", "getProviders"),
                        searchParams: [ providerRoles: [ provider.providerRole?.id ] ],
                        resultFields: providerSearchDisplayFields.values(),
                        resultFieldLabels: providerSearchDisplayFields.keySet(),
                        selectAction: ui.actionLink('providerEdit', 'transferPatients'),
                        selectIdParam: "newProvider",
                        selectParams: [ oldProvider: person.id, relationshipType: it.key.id ],
                        selectForm: "multiSelectCheckboxForm_" + it.key.id])  %>
            </div>

            <div id="addPatient_${ it.key.id }" class="addPatient">
                <%=  ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.addPatient"),
                        searchAction: ui.actionLink("patientSearch", "getPatients"),
                        searchParams: [excludePatientsOf: person.id, existingRelationshipTypeToExclude: it.key.id ],
                        resultFields: patientSearchDisplayFields.values(),
                        resultFieldLabels: patientSearchDisplayFields.keySet(),
                        selectAction: ui.actionLink('providerEdit', 'addPatient'),
                        selectIdParam: "patient",
                        selectParams: [ provider: person.id, relationshipType: it.key.id ],
                        actionButtons: [[label: ui.message("general.cancel"), id: "addPatientCancelButton_${ it.key.id }", class: "addPatientCancelButton"]] ])  %>
            </div>

        </div>
    <% } %>


</div>
