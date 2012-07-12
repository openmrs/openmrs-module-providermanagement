
<%
   context.requirePrivilege("Provider Management Dashboard - View Providers")
   ui.decorateWith("providerManagementPage")
   ui.includeCss("providermanagement", "providerDashboard.css")

    // add the uuids we are going to use to identify each panel
    // (note that we remember these so that we know what panel to open on reload
    def superviseesId = "83814a90-9f89-11e1-a8b0-0800200c9a66"
    def supervisorsId = "a4ce1250-9f89-11e1-a8b0-0800200c9a66"
%>


<script>
    jq(function() {

        var resetActionDialogs = function () {

            // hide the add, suggest, transfer, remove, and void sections
            jq('.add').hide();
            jq('.suggest').hide();
            jq('.transfer').hide();
            jq('.remove').hide();
            jq('.void').hide();

            // clear out any existing search values
            jq('.searchField').val('');
            jq('.searchTable > tbody > tr').remove();

        }

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

        // handles showing the appropriate pane
        jq('.paneSelect').click(function(){
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            // remove any existing hightlights from the table
            jq('.paneSelectTop').removeClass('selected');
            jq('.paneSelectBottom').removeClass('selected');

            // hide any existing panels on display
            jq('.pane').hide();

            resetActionDialogs();

            // highlight selection and show the appropriate patient pane
            jq('#paneSelectTop_' + id).addClass('selected');
            jq('#paneSelectBottom_' + id).addClass('selected');
            jq('#pane_' + id).show();
        });


        // handles displaying the transfer divs
        jq('.transferButton').click(function() {

            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            resetActionDialogs();

            // show the appropriate transfer div
            jq('#transfer_' + id).show();
        }) ;

        // handles clicking on the add buttons
        jq('.addButton').click(function() {

            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            resetActionDialogs();

            // show the appropriate add div
            jq('#add_' + id).show();
        });

        // handles displaying the remove divs
        jq('.removeButton').click(function() {
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            resetActionDialogs();

            // show the appropriate transfer div
            jq('#remove_' + id).show();
        }) ;

        // handles displaying the void divs
        jq('.voidButton').click(function() {
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            resetActionDialogs();

            // show the appropriate transfer div
            jq('#void_' + id).show();
        }) ;

        // handles clicking on the suggest button
        jq('.suggestButton').click(function() {

            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            resetActionDialogs();

            // show the appropriate div
            jq('#suggest_' + id).show();
        })

        // handles clicking cancel buttons
        jq('.cancelButton').click(function() {
            resetActionDialogs();
        });

        jq(document).ready(function(){
           // display the proper pane on page reload
            <% if (paneId) { %>
                jq('#pane_${ paneId }').show();
                jq('#paneSelectTop_${ paneId }').addClass('selected');
                jq('#paneSelectBottom_${ paneId }').addClass('selected');
            <% } else { %>
                // if no pane specified, just show the first pane
                jq('.pane:first').show();
                jq('.paneSelectTop:first').addClass('selected');
                jq('.paneSelectBottom:first').addClass('selected');
            <% } %>
        });

    });
</script>

<% if (context.hasPrivilege("Provider Management Dashboard - Edit Providers")) { %>
    <div id="providerView">
        ${ ui.includeFragment("providerView", [ actionButtons: [ [label: ui.message("general.edit"), id: "showEditButton"],
                [label: ui.message("general.retire"),id: "retire", confirm: ui.message("providermanagement.confirmRetire"),
                        link: ui.actionLink("providerEdit", "retireProvider", [provider: person.id]) ] ] ]) }
    </div>

    <div id="providerEdit">
        ${ ui.includeFragment("providerEdit", [ actionButtons: [ [label: ui.message("general.save"), type: "submit"],
                                                                    [label: ui.message("general.cancel"), id: "cancelEditButton", type: "reset"] ] ]) }
    </div>
<% } else { %>
    <!-- if no edit privileges, just show the provider view panel without the edit and retire action buttons -->
    <div id="providerView">
        ${ ui.includeFragment("providerView") }
    </div>
<% } %>

<div id="relationshipsPanel">

    <div id="patientSupervisorHeader">
        <table>
            <tr>
                <td class="borderCell">&nbsp;</td>

                <% if (context.hasPrivilege("Provider Management Dashboard - View Patients")) { %>
                    <% currentPatientMap?.each { %>
                        <td id="paneSelectTop_${ it.key.uuid }" class="paneSelectTop paneSelect"> <img src=" ${ ui.resourceLink ("images/patient-nested.png") }"/></td>
                    <% } %>
                <% } else { %>
                    <% patientCount?.each { %>
                         <td id="paneSelectTop_${ it.key.uuid }" class="paneSelectTop paneSelect"> <img src=" ${ ui.resourceLink ("images/patient-nested.png") }"/></td>
                    <% } %>
                <% } %>

                <% if (provider.providerRole?.isSupervisorRole()) { %>
                    <td id="paneSelectTop_${ superviseesId }" class="paneSelectTop paneSelect"> <img src=" ${ ui.resourceLink ("images/supervisee-nested.png") }"/></td>
                <% } %>

                <td id="paneSelectTop_${ supervisorsId }" class="paneSelectTop paneSelect"> <img src=" ${ ui.resourceLink ("images/supervisor-nested.png") }"/></td>

                <td class="borderCell">&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>

                <% if (context.hasPrivilege("Provider Management Dashboard - View Patients")) { %>
                    <% currentPatientMap?.each { %>
                        <td id="paneSelectBottom_${ it.key.uuid }" class="paneSelectBottom paneSelect">${ it.key.aIsToB }<br/>${ ui.message("providermanagement.patients") }</td>
                    <% } %>
                <% } else { %>
                    <% patientCount?.each { %>
                        <td id="paneSelectBottom_${ it.key.uuid }" class="paneSelectBottom paneSelect">${ it.key.aIsToB }<br/>${ ui.message("providermanagement.patients") }</td>
                    <% } %>
                <% } %>

                <% if (provider.providerRole?.isSupervisorRole()) { %>
                    <td id="paneSelectBottom_${ superviseesId }" class="paneSelectBottom paneSelect">${ ui.message("providermanagement.supervisees") }</td>
                <% } %>

                <td id="paneSelectBottom_${ supervisorsId }" class="paneSelectBottom paneSelect">${ ui.message("providermanagement.supervisors") }</td>

                <td>&nbsp;</td>
            </tr>
        </table>

    </div>

    <div id="patientSupervisorHeaderDivider">

    </div>

    <% if (context.hasPrivilege("Provider Management Dashboard - View Patients")) { %>
        <!-- this map is keyed on relationship types; value is a list of patients associated with the provider for that relationship type -->
        <% currentPatientMap?.each {   %>

            <div id="pane_${ it.key.uuid }" class="pane">

                <div id="list_${ it.key.uuid }" class="list">
                    <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: it.value.sort { item -> item.patient.personName?.toString() },
                            id: it.key.uuid,
                            columns: patientListDisplayFields.values(),
                            columnLabels: patientListDisplayFields.keySet(),
                            formAction: ui.actionLink("providerEdit","removePatients", [provider: person.id, relationshipType: it.key.id, successUrl: ui.pageLink("providerDashboard", [personId: person.id, paneId: it.key.uuid] )]),
                            formFieldName: "patientRelationships",
                            disabled: !context.hasPrivilege("Provider Management Dashboard - Edit Patients"),
                            emptyMessage: ui.message("providermanagement.none"),
                            footer: it.value.size + " " + (it.value.size != 1 ? ui.message("providermanagement.totalPatients") : ui.message("providermanagement.totalPatient")),
                            actionButtons: ( context.hasPrivilege("Provider Management Dashboard - Edit Patients") ?
                                            [[label: ui.message("general.add"), id: "addButton_${ it.key.uuid }", class: "addButton", type: "button"],
                                            [label: ui.message("providermanagement.transfer"), id: "transferButton_${ it.key.uuid }", class: "transferButton", type: "button"],
                                            [label: ui.message("general.remove"), id: "removeButton_${ it.key.uuid }", class: "removeButton", type: "button"],
                                            [label: ui.message("general.void"), id: "voidButton_${ it.key.uuid }", class: "voidButton", type: "button"]] : [])
                    ]) %>
                </div>

                <% if (context.hasPrivilege("Provider Management Dashboard - Edit Patients")) { %>
                    <div id="transfer_${ it.key.uuid }" class="transfer">
                        <%=  ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.transferPatients"),
                                searchAction: ui.actionLink("providerSearch", "getProviders"),
                                searchParams: [ providerRoles: [ provider.providerRole?.id ], excludeProvider: person.id ],
                                resultFields: providerSearchDisplayFields.values(),
                                resultFieldLabels: providerSearchDisplayFields.keySet(),
                                selectAction: ui.actionLink('providerEdit', 'transferPatients', [successUrl: ui.pageLink("providerDashboard", [personId: person.id, paneId: it.key.uuid] )]),
                                selectIdParam: "newProvider",
                                selectParams: [ oldProvider: person.id, relationshipType: it.key.id],
                                selectForm: "multiSelectCheckboxForm_" + it.key.uuid,
                                showDateField: true,
                                dateLabel: ui.message("providermanagement.onDate"),
                                emptyMessage: ui.message("providermanagement.noMatches"),
                                actionButtons: [[label: ui.message("general.cancel"), id: "transferCancelButton_${ superviseesId }", class: "cancelButton"]]
                        ])  %>
                    </div>

                    <div id="add_${ it.key.uuid }" class="add">
                        <%=  ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.addPatient"),
                                searchAction: ui.actionLink("patientSearch", "getPatients"),
                                searchParams: [excludePatientsOf: person.id, existingRelationshipTypeToExclude: it.key.id ],
                                resultFields: patientSearchDisplayFields.values(),
                                resultFieldLabels: patientSearchDisplayFields.keySet(),
                                selectAction: ui.actionLink('providerEdit', 'addPatient', [successUrl: ui.pageLink("providerDashboard", [personId: person.id, paneId: it.key.uuid] )]),
                                selectIdParam: "patient",
                                selectParams: [ provider: person.id, relationshipType: it.key.id],
                                showDateField: true,
                                dateLabel: ui.message("providermanagement.onDate"),
                                emptyMessage: ui.message("providermanagement.noMatches"),
                                actionButtons: [[label: ui.message("general.cancel"), id: "addCancelButton_${ it.key.uuid }", class: "cancelButton"]]
                        ])  %>
                    </div>

                    <div id="remove_${ it.key.uuid }" class="remove">
                        <%=  ui.includeFragment("widget/inputDialog", [title: ui.message("providermanagement.confirmRemovalPatients"),
                                  submitAction: ui.actionLink('providerEdit', 'removePatients', [successUrl: ui.pageLink("providerDashboard", [personId: person.id, paneId: it.key.uuid] )]),
                                  submitParams: [ provider: person.id, relationshipType: it.key.id ],
                                  submitButtonId: "confirmRemoveButton_${ it.key.uuid }",
                                  submitForm: "multiSelectCheckboxForm_${ it.key.uuid }",
                                  class: java.util.Date,
                                  formFieldName: "date",
                                  dateLabel: ui.message("providermanagement.stopDate"),
                                  actionButtons: [[label: ui.message("general.remove"), id: "confirmRemoveButton_${ it.key.uuid }", class: "confirmRemoveButton"],
                                                  [label: ui.message("general.cancel"), id: "removeCancelButton_${ it.key.uuid }", class: "cancelButton"]]
                        ])  %>
                    </div>

                    <div id="void_${ it.key.uuid }" class="void">
                        <%=  ui.includeFragment("widget/inputDialog", [title: ui.message("providermanagement.confirmVoidPatients"),
                                submitAction: ui.actionLink('providerEdit', 'voidPatients', [successUrl: ui.pageLink("providerDashboard", [personId: person.id, paneId: it.key.uuid] )]),
                                submitParams: [ provider: person.id ],
                                submitButtonId: "confirmVoidButton_${ it.key.uuid }",
                                submitForm: "multiSelectCheckboxForm_${ it.key.uuid }",
                                class: java.lang.String,
                                formFieldName: "voidReason",
                                dateLabel: ui.message("providermanagement.voidReason"),
                                actionButtons: [[label: ui.message("general.void"), id: "confirmVoidButton_${ it.key.uuid }", class: "confirmVoidButton"],
                                        [label: ui.message("general.cancel"), id: "voidCancelButton_${ it.key.uuid }", class: "cancelButton"]]
                        ])  %>
                    </div>
                <% } %>
            </div>
        <% } %>
    <% } else { %>
            <!-- this map is keyed on relationship types; value is a count of the patients associated with the provider for that relationship type -->
            <% patientCount?.each {   %>
                <div id="pane_${ it.key.uuid }" class="pane">
                    <div class="content">
                        ${ it.value } ${ it.key.aIsToB } ${ ui.message("providermanagement.patients") }
                    </div>
                </div>
            <% } %>
    <% } %>

    <% if (provider.providerRole?.isSupervisorRole()) { %>
        <div id="pane_${ superviseesId }" class="pane">

            <div id="list_${ superviseesId }" class="list">
                <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: currentSupervisees.sort { item -> item.provider.person.personName?.toString() },
                        id: superviseesId,
                        columns: providerListDisplayFields.values(),
                        columnLabels: providerListDisplayFields.keySet(),
                        selectAction: ui.pageLink('providerDashboard'),
                        selectIdParam: "personId",
                        formAction: ui.actionLink("providerEdit","removeSupervisees", [supervisor: person.id, successUrl: ui.pageLink("providerDashboard", [personId: person.id, paneId: superviseesId] )]),
                        formFieldName: "superviseeRelationships",
                        disabled: !context.hasPrivilege("Provider Management Dashboard - Edit Patients"),
                        emptyMessage: ui.message("providermanagement.none"),
                        footer: currentSupervisees.size + " " + (currentSupervisees.size != 1 ? ui.message("providermanagement.totalSupervisees") : ui.message("providermanagement.totalSupervisee")),
                        actionButtons: (context.hasPrivilege("Provider Management Dashboard - Edit Providers") ?
                                [[label: ui.message("general.add"), id: "addButton_${ superviseesId }", class: "addButton", type: "button"],
                                [label: ui.message("providermanagement.transfer"), id: "transferButton_${ superviseesId } ", class: "transferButton", type: "button"],
                                [label: ui.message("providermanagement.suggest"), id: "suggestButton_${ superviseesId }", class: "suggestButton", type: "button"],
                                [label: ui.message("general.remove"), id: "removeButton_${ superviseesId }", class: "removeButton", type: "button"],
                                [label: ui.message("general.void"), id: "voidButton_${ superviseesId }", class: "voidButton", type: "button"]] : [])
                ]) %>

            </div>

            <% if (context.hasPrivilege("Provider Management Dashboard - Edit Providers")) { %>
                <div id="transfer_${ superviseesId }" class="transfer">
                    <%=  ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.transferSupervisees"),
                            searchAction: ui.actionLink("providerSearch", "getProviders"),
                            searchParams: [ providerRoles: [ provider.providerRole?.id ] ],
                            resultFields: providerSearchDisplayFields.values(),
                            resultFieldLabels: providerSearchDisplayFields.keySet(),
                            selectAction: ui.actionLink('providerEdit', 'transferSupervisees', [successUrl: ui.pageLink("providerDashboard", [personId: person.id, paneId: superviseesId] )]),
                            selectIdParam: "newSupervisor",
                            selectParams: [ oldSupervisor: person.id ],
                            selectForm: "multiSelectCheckboxForm_" + superviseesId,
                            showDateField: true,
                            dateLabel: ui.message("providermanagement.onDate"),
                            emptyMessage: ui.message("providermanagement.noMatches"),
                            actionButtons: [[label: ui.message("general.cancel"), id: "transferCancelButton_${ superviseesId }", class: "cancelButton"]]
                    ])  %>
                </div>


                <div id="add_${ superviseesId }" class="add">
                    <%= ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.addSupervisee"),
                            searchAction: ui.actionLink("providerSearch", "getProviders"),
                            searchParams: [ excludeSuperviseesOf: person.id, providerRoles: provider.providerRole?.superviseeProviderRoles.collect { it.id } ],
                            resultFields: providerSearchDisplayFields.values(),
                            resultFieldLabels: providerSearchDisplayFields.keySet(),
                            selectAction: ui.actionLink('providerEdit', 'addSupervisee', [successUrl: ui.pageLink("providerDashboard", [personId: person.id, paneId: superviseesId] )]),
                            selectIdParam: "supervisee",
                            selectParams: [ supervisor: person.id ],
                            showDateField: true,
                            dateLabel: ui.message("providermanagement.onDate"),
                            emptyMessage: ui.message("providermanagement.noMatches"),
                            actionButtons: [[label: ui.message("general.cancel"), id: "addCancelButton_${ superviseesId }", class: "cancelButton"]]
                    ])  %>
                </div>

                <div id="remove_${ superviseesId }" class="remove">
                    <%=  ui.includeFragment("widget/inputDialog", [title: ui.message("providermanagement.confirmRemovalSupervisees"),
                            submitAction: ui.actionLink('providerEdit', 'removeSupervisees', [successUrl: ui.pageLink("providerDashboard", [personId: person.id, paneId: superviseesId] )]),
                            submitParams: [ supervisor: person.id ],
                            submitButtonId: "confirmRemoveButton_${ superviseesId }",
                            submitForm: "multiSelectCheckboxForm_${ superviseesId }",
                            class: java.util.Date,
                            formFieldName: "date",
                            dateLabel: ui.message("providermanagement.stopDate"),
                            actionButtons: [[label: ui.message("general.remove"), id: "confirmRemoveButton_${ superviseesId }", class: "confirmRemoveButton"],
                                    [label: ui.message("general.cancel"), id: "removeCancelButton_${ superviseesId }", class: "cancelButton"]]
                    ])  %>
                </div>

                <div id="void_${ superviseesId }" class="void">
                    <%=  ui.includeFragment("widget/inputDialog", [title: ui.message("providermanagement.confirmVoidSupervisees"),
                            submitAction: ui.actionLink('providerEdit', 'voidSupervisees', [successUrl: ui.pageLink("providerDashboard", [personId: person.id, paneId: superviseesId] )]),
                            submitParams: [ supervisor: person.id ],
                            submitButtonId: "confirmVoidButton_${ superviseesId }",
                            submitForm: "multiSelectCheckboxForm_${ superviseesId }",
                            class: java.lang.String,
                            formFieldName: "voidReason",
                            dateLabel: ui.message("providermanagement.voidReason"),
                            actionButtons: [[label: ui.message("general.void"), id: "confirmVoidButton_${ superviseesId }", class: "confirmVoidButton"],
                                    [label: ui.message("general.cancel"), id: "voidCancelButton_${ superviseesId }", class: "cancelButton"]]
                    ])  %>
                </div>

                <% if (suggestedSupervisees != null) { %>   <!-- note that we want to display this if the results are an empty list, hence the explicit test for null here -->
                <div id="suggest_${ superviseesId }" class="suggest">
                    <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: suggestedSupervisees.sort { item -> item.personName?.toString() },
                            title: ui.message("providermanagement.suggestedSupervisees"),
                            columns: providerSearchDisplayFields.values(),
                            columnLabels: providerSearchDisplayFields.keySet(),
                            selectAction: ui.pageLink('providerDashboard'),
                            selectIdParam: "personId",
                            formAction: ui.actionLink("providerEdit","addSupervisees", [supervisor: person.id, successUrl: ui.pageLink("providerDashboard", [personId: person.id, paneId: superviseesId] )]),
                            formFieldName: "supervisees",
                            emptyMessage: ui.message("providermanagement.none"),
                            actionButtons: [[label: ui.message("general.add"), type: "submit"],
                                            [label: ui.message("general.cancel"), id: "suggestCancelButton_${ superviseesId}", class:"cancelButton", type: "reset"]]
                    ]) %>
                </div>
                <% } %>
            <% } %>
        </div>
    <% } %>

    <div id="pane_${ supervisorsId }" class="pane">
        <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: currentSupervisors.sort { item -> item.provider.person.personName?.toString() },
                columns: providerListDisplayFields.values(),
                columnLabels: providerListDisplayFields.keySet(),
                selectAction: ui.pageLink('providerDashboard'),
                selectIdParam: "personId",
                emptyMessage: ui.message("providermanagement.none"),
                disabled: true ]) %>

    </div>

</div>
