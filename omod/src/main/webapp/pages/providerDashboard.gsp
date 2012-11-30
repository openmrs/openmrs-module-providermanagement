
<%
   context.requirePrivilege("Provider Management Dashboard - View Providers")
   ui.decorateWith("providermanagement", "providerManagementPage")
   ui.includeCss("providermanagement", "providerDashboard.css")

    // add the uuids we are going to use to identify each panel
    // (note that we remember these so that we know what panel to open on reload
    def superviseesId = "83814a90-9f89-11e1-a8b0-0800200c9a66"
    def supervisorsId = "a4ce1250-9f89-11e1-a8b0-0800200c9a66"

   def today = new Date()
   today.clearTime()

%>


<script>
    jq(function() {

        var hideActionButtons = function () {
            // hides all the action buttons
            // (we use the visibility attribute because we don't want the containing row to collapse)
            jq('.addButton').css('visibility','hidden');
            jq('.editButton').css('visibility','hidden');
            jq('.transferButton').css('visibility','hidden');
            jq('.removeButton').css('visibility','hidden');
            jq('.suggestButton').css('visibility','hidden');
            jq('.voidButton').css('visibility','hidden');
            jq('.editHistoricalButton').css('visibility','hidden');
            jq('.voidHistoricalButton').css('visibility','hidden');
        }

        var showActionButtons = function () {
            // hides all the action buttons
            jq('.addButton').css('visibility','visible');
            jq('.editButton').css('visibility','visible');
            jq('.transferButton').css('visibility','visible');
            jq('.removeButton').css('visibility','visible');
            jq('.suggestButton').css('visibility','visible');
            jq('.voidButton').css('visibility','visible');
            jq('.editHistoricalButton').css('visibility','visible');
            jq('.voidHistoricalButton').css('visibility','visible');
        }

        var resetActionDialogs = function () {

            // hide the add, edit, suggest, transfer, remove, and void sections
            jq('.add').hide();
            jq('.edit').hide();
            jq('.editHistorical').hide();
            jq('.suggest').hide();
            jq('.transfer').hide();
            jq('.remove').hide();
            jq('.void').hide();
            jq('.voidHistorical').hide();

            // clear out any existing search values
            jq('.searchValue').val('');
            jq('.searchField').val('');
            jq('.searchTable > tbody > tr').remove();

            // hide add & transfer submit buttons
            jq('.confirmAddButton').hide();
            jq('.confirmTransferButton').hide();

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
            showActionButtons();

            // highlight selection and show the appropriate patient pane
            jq('#paneSelectTop_' + id).addClass('selected');
            jq('#paneSelectBottom_' + id).addClass('selected');
            jq('#pane_' + id).show();
        });

        // handles displaying the transfer divs
        jq('.transferButton').click(function() {
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            hideActionButtons();
            resetActionDialogs();

            // show the appropriate transfer div
            jq('#transfer_' + id).show();
        }) ;

        // handles clicking on the add buttons
        jq('.addButton').click(function() {
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            hideActionButtons();
            resetActionDialogs();

            // show the appropriate add div
            jq('#add_' + id).show();
        });

        // handles clicking on the edit buttons
        jq('.editButton').click(function() {
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            hideActionButtons();
            resetActionDialogs();

            // show the appropriate add div
            jq('#edit_' + id).show();
        });

        // handles clicking on the edit historical buttons
        jq('.editHistoricalButton').click(function() {
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            hideActionButtons();
            resetActionDialogs();

            // show the appropriate add div
            jq('#editHistorical_' + id).show();
        });


        // handles displaying the remove divs
        jq('.removeButton').click(function() {
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            hideActionButtons();
            resetActionDialogs();

            // show the appropriate transfer div
            jq('#remove_' + id).show();
        }) ;

        // handles displaying the void divs
        jq('.voidButton').click(function() {
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            hideActionButtons();
            resetActionDialogs();

            // show the appropriate transfer div
            jq('#void_' + id).show();
        }) ;

        // handles displaying the historical void divs
        jq('.voidHistoricalButton').click(function() {
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            hideActionButtons();
            resetActionDialogs();

            // show the appropriate transfer div
            jq('#voidHistorical_' + id).show();
        }) ;

        // handles clicking on the suggest button
        jq('.suggestButton').click(function() {

            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            hideActionButtons();
            resetActionDialogs();

            // show the appropriate div
            jq('#suggest_' + id).show();
        })

        // handles clicking cancel buttons
        jq('.cancelButton').click(function() {
            resetActionDialogs();
            showActionButtons();
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
        <%= ui.includeFragment("providermanagement", "providerView", [ actionButtons: [ [label: ui.message("general.edit"), id: "showEditButton"],
                                                                 (!provider?.retired ? [label: ui.message("general.retire"),id: "retire", confirm: ui.message("providermanagement.confirmRetire"),
                                                                                            link: ui.actionLink("providermanagement", "providerEdit", "retireProvider", [provider: person.id])] :
                                                                                        [label: ui.message("providermanagement.unretire"),id: "retire", confirm: ui.message("providermanagement.confirmUnretire"),
                                                                                            link: ui.actionLink("providermanagement", "providerEdit", "unretireProvider", [provider: person.id])]) ]
        ])%>
    </div>

    <div id="providerEdit">
        ${ ui.includeFragment("providermanagement", "providerEdit", [ actionButtons: [ [label: ui.message("general.save"), type: "submit"],
                                                                    [label: ui.message("general.cancel"), id: "cancelEditButton", type: "reset"] ] ]) }
    </div>
<% } else { %>
    <!-- if no edit privileges, just show the provider view panel without the edit and retire action buttons -->
    <div id="providerView">
        ${ ui.includeFragment("providermanagement", "providerView") }
    </div>
<% } %>

<div id="relationshipsPanel">

    <div id="patientSupervisorHeader">
        <table>
            <tr>
                <td class="borderCell">&nbsp;</td>

                <% if (context.hasPrivilege("Provider Management Dashboard - View Patients")) { %>
                    <% patientMap?.each { %>
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
                    <% patientMap?.each { %>
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
        <% patientMap?.each {   %>

            <div id="pane_${ it.key.uuid }" class="pane">

                <div id="list_${ it.key.uuid }" class="list">
                    <%=  ui.includeFragment("providermanagement", "widget/multiSelectCheckboxTable", [ items: it.value.currentPatients.sort { item -> item.patient.personName?.toString() },
                            id: it.key.uuid,
                            title: ui.message("providermanagement.currentPatients"),
                            columns: patientListDisplayFields.values(),
                            columnLabels: patientListDisplayFields.keySet(),
                            formFieldName: "patientRelationships",
                            disabled: !context.hasPrivilege("Provider Management Dashboard - Edit Patients"),
                            emptyMessage: ui.message("providermanagement.none"),
                            footer: it.value.currentPatients.size + " " + (it.value.currentPatients.size != 1 ? ui.message("providermanagement.totalPatients") : ui.message("providermanagement.totalPatient")),
                            disableOnMultiSelect: ["confirmEditButton_${ it.key.uuid }"],
                            actionButtons: ( context.hasPrivilege("Provider Management Dashboard - Edit Patients") ?
                                            [[label: ui.message("general.add"), id: "addButton_${ it.key.uuid }", class: "addButton", type: "button"],
                                            [label: ui.message("general.edit"), id: "editButton_${ it.key.uuid }", class: "editButton", type: "button", disableOnMultiSelect: true],
                                            [label: ui.message("providermanagement.transfer"), id: "transferButton_${ it.key.uuid }", class: "transferButton", type: "button"],
                                            [label: ui.message("general.remove"), id: "removeButton_${ it.key.uuid }", class: "removeButton", type: "button"],
                                            [label: ui.message("general.void"), id: "voidButton_${ it.key.uuid }", class: "voidButton", type: "button"]] : [])
                    ]) %>
                </div>

                <% if (context.hasPrivilege("Provider Management Dashboard - Edit Patients")) { %>
                    <div id="transfer_${ it.key.uuid }" class="transfer">
                        <%=  ui.includeFragment("providermanagement", "widget/ajaxSearch", [title: ui.message("providermanagement.transferPatients"),
                                searchAction: ui.actionLink("providermanagement", "providerSearch", "getProviders"),
                                searchParams: [ providerRoles: [ provider.providerRole?.id ], excludeProvider: person.id ],
                                resultFields: providerSearchDisplayFields.values(),
                                resultFieldLabels: providerSearchDisplayFields.keySet(),
                                selectDisplayFields: ["personName.givenName","personName.middleName","personName.familyName"],
                                submitAction: ui.actionLink('providermanagement', 'providerEdit', 'transferPatients', [successUrl: ui.pageLink("providermanagement", "providerDashboard", [personId: person.id, paneId: it.key.uuid] )]),
                                submitIdParam: "newProvider",
                                submitButtonId: "confirmTransferButton_${ it.key.uuid }",
                                cancelButtonId: "addTransferButton_${ it.key.uuid }",
                                submitParams: [ oldProvider: person.id, relationshipType: it.key.id],
                                submitForm: [name: "multiSelectCheckboxForm_${ it.key.uuid }", required: true, requiredErrorMessage: ui.message("providermanagement.errors.patients.required")],
                                fields: [ [name: "date", class: java.util.Date, label: ui.message("providermanagement.transferDate"), required: true, maxDate: "+0d", initialValue: today] ],
                                dateLabel: ui.message("providermanagement.onDate"),
                                emptyMessage: ui.message("providermanagement.noMatches"),
                                actionButtons: [[label: ui.message("general.submit"), id: "confirmTransferButton_${ it.key.uuid }", class: "confirmTransferButton"],
                                                [label: ui.message("general.cancel"), id: "transferCancelButton_${ superviseesId }", class: "cancelButton"]]
                        ])  %>
                    </div>

                    <div id="add_${ it.key.uuid }" class="add">
                        <%=  ui.includeFragment("providermanagement","widget/ajaxSearch", [title: ui.message("providermanagement.addPatient"),
                                searchAction: ui.actionLink("providermanagement", "patientSearch", "getPatients"),
                                searchParams: [excludePatientsOf: person.id, existingRelationshipTypeToExclude: it.key.id ],
                                resultFields: patientSearchDisplayFields.values(),
                                resultFieldLabels: patientSearchDisplayFields.keySet(),
                                selectDisplayFields: ["personName.givenName","personName.middleName","personName.familyName"],
                                submitAction: ui.actionLink('providermanagement', 'providerEdit', 'addPatient', [successUrl: ui.pageLink("providermanagement", "providerDashboard", [personId: person.id, paneId: it.key.uuid] )]),
                                submitIdParam: "patient",
                                submitButtonId: "confirmAddButton_${ it.key.uuid }",
                                cancelButtonId: "addCancelButton_${ it.key.uuid }",
                                submitParams: [ provider: person.id, relationshipType: it.key.id],
                                fields: [ [name: "date", class: java.util.Date, label: ui.message("providermanagement.startDate"), required: true, maxDate: "+0d", initialValue: today] ],
                                dateLabel: ui.message("providermanagement.onDate"),
                                emptyMessage: ui.message("providermanagement.noMatches"),
                                actionButtons: [[ label: ui.message("general.submit"), id: "confirmAddButton_${ it.key.uuid }", class: "confirmAddButton"],
                                                [ label: ui.message("general.cancel"), id: "addCancelButton_${ it.key.uuid }", class: "cancelButton"]]
                        ])  %>
                    </div>

                    <div id="edit_${ it.key.uuid }" class="edit">
                        <%=  ui.includeFragment("providermanagement", "widget/inputDialog", [title: ui.message("providermanagement.newStartDatePatients"),
                                submitAction: ui.actionLink('providermanagement', 'providerEdit', 'editPatients', [successUrl: ui.pageLink("providermanagement", "providerDashboard", [personId: person.id, paneId: it.key.uuid])]),
                                submitButtonId: "confirmEditButton_${ it.key.uuid }",
                                submitForm: [name: "multiSelectCheckboxForm_${ it.key.uuid }", required: true, requiredErrorMessage: ui.message("providermanagement.errors.patient.required")],
                                fields: [ [name: "startDate", class: java.util.Date, label: ui.message("providermanagement.startDate"), required: true, maxDate: "+0d"] ],
                                actionButtons: [[label: ui.message("providermanagement.update"), id: "confirmEditButton_${ it.key.uuid }", class: "confirmEditButton", type: "button"],
                                                [label: ui.message("general.cancel"), id: "editCancelButton_${ it.key.uuid }", class: "cancelButton", type: "button"]]
                        ])  %>
                    </div>

                    <div id="remove_${ it.key.uuid }" class="remove">
                        <%=  ui.includeFragment("providermanagement", "widget/inputDialog", [title: ui.message("providermanagement.confirmRemovalPatients"),
                                  submitAction: ui.actionLink('providermanagement', 'providerEdit', 'removePatients', [successUrl: ui.pageLink("providermanagement", "providerDashboard", [personId: person.id, paneId: it.key.uuid] )]),
                                  submitParams: [ provider: person.id, relationshipType: it.key.id ],
                                  submitButtonId: "confirmRemoveButton_${ it.key.uuid }",
                                  submitForm: [name: "multiSelectCheckboxForm_${ it.key.uuid }", required: true, requiredErrorMessage: ui.message("providermanagement.errors.patients.required")],
                                  fields: [ [name: "date", class: java.util.Date, label :ui.message("providermanagement.stopDate"), required: true, maxDate: "+0d", initialValue: today] ],
                                  actionButtons: [[label: ui.message("general.remove"), id: "confirmRemoveButton_${ it.key.uuid }", class: "confirmRemoveButton"],
                                                  [label: ui.message("general.cancel"), id: "removeCancelButton_${ it.key.uuid }", class: "cancelButton"]]
                        ])  %>
                    </div>

                    <div id="void_${ it.key.uuid }" class="void">
                        <%=  ui.includeFragment("providermanagement", "widget/inputDialog", [title: ui.message("providermanagement.confirmVoidPatients"),
                                submitAction: ui.actionLink('providermanagement', 'providerEdit', 'voidPatients', [successUrl: ui.pageLink("providermanagement", "providerDashboard", [personId: person.id, paneId: it.key.uuid] )]),
                                submitParams: [ provider: person.id ],
                                submitButtonId: "confirmVoidButton_${ it.key.uuid }",
                                submitForm: [name: "multiSelectCheckboxForm_${ it.key.uuid }",  required: true, requiredErrorMessage: ui.message("providermanagement.errors.patients.required")],
                                fields: [ [name: "voidReason", class: java.lang.String, label: ui.message("providermanagement.voidReason"), required: true] ],
                                actionButtons: [[label: ui.message("general.void"), id: "confirmVoidButton_${ it.key.uuid }", class: "confirmVoidButton", type: "button"],
                                        [label: ui.message("general.cancel"), id: "voidCancelButton_${ it.key.uuid }", class: "cancelButton", type: "button"]]
                        ])  %>
                    </div>
                <% } %>

                <br/><br/>

                <% if (it.value.historicalPatients && context.hasPrivilege("Provider Management Dashboard - View Historical")) { %>
                    <div id="historicalList_${ it.key.uuid }" class="historicalList">
                        <%=  ui.includeFragment("providermanagement", "widget/multiSelectCheckboxTable", [ items: it.value.historicalPatients.sort { item -> item.patient.personName?.toString() },
                                id: "historical_${ it.key.uuid }",
                                title: ui.message("providermanagement.historicalPatients"),
                                columns: historicalPatientListDisplayFields.values(),
                                columnLabels: historicalPatientListDisplayFields.keySet(),
                                formFieldName: "patientRelationships",
                                disabled: !context.hasPrivilege("Provider Management Dashboard - Edit Patients"),
                                emptyMessage: ui.message("providermanagement.none"),
                                disableOnMultiSelect: ["confirmEditHistoricalButton_${ it.key.uuid }"],
                                actionButtons: ( context.hasPrivilege("Provider Management Dashboard - Edit Patients") ?
                                    [ [label: ui.message("general.edit"), id: "editHistoricalButton_${ it.key.uuid }", class: "editHistoricalButton", type: "button", disableOnMultiSelect: true],
                                      [label: ui.message("general.void"), id: "voidHistoricalButton_${ it.key.uuid }", class: "voidHistoricalButton", type: "button"]] : [])
                        ]) %>
                    </div>

                    <div id="editHistorical_${ it.key.uuid }" class="edit">
                        <%=  ui.includeFragment("providermanagement", "widget/inputDialog", [title: ui.message("providermanagement.newStartAndEndDatePatients"),
                                submitAction: ui.actionLink('providermanagement', 'providerEdit', 'editPatients', [successUrl: ui.pageLink("providermanagement", "providerDashboard", [personId: person.id, paneId: it.key.uuid] )]),
                                submitButtonId: "confirmEditHistoricalButton_${ it.key.uuid }",
                                submitForm: [name: "multiSelectCheckboxForm_historical_${ it.key.uuid }", required: true, requiredErrorMessage: ui.message("providermanagement.errors.patients.required")],
                                fields: [ [name: "startDate", class: java.util.Date, label: ui.message("providermanagement.startDate"), required: true, maxDate: "+0d"],
                                              [name: "endDate", class: java.util.Date, label: ui.message("providermanagement.stopDate"), required: false, maxDate: "+0d"]  ],
                                actionButtons: [[label: ui.message("providermanagement.update"), id: "confirmEditHistoricalButton_${ it.key.uuid }", class: "confirmEditHistoricalButton", type: "button"],
                                                 [label: ui.message("general.cancel"), id: "editHistoricalCancelButton_${ it.key.uuid }", class: "cancelButton",type: "button"]]
                        ])  %>
                    </div>

                    <div id="voidHistorical_${ it.key.uuid }" class="void">
                        <%=  ui.includeFragment("providermanagement", "widget/inputDialog", [title: ui.message("providermanagement.confirmVoidPatients"),
                                submitAction: ui.actionLink('providermanagement', 'providerEdit', 'voidPatients', [successUrl: ui.pageLink("providermanagement", "providerDashboard", [personId: person.id, paneId: it.key.uuid] )]),
                                submitParams: [ provider: person.id ],
                                submitButtonId: "confirmHistoricalVoidButton_${ it.key.uuid }",
                                submitForm: [name: "multiSelectCheckboxForm_historical_${ it.key.uuid }",  required: true, requiredErrorMessage: ui.message("providermanagement.errors.patients.required")],
                                fields: [ [name: "voidReason", class: java.lang.String, label: ui.message("providermanagement.voidReason"), required: true] ],
                                actionButtons: [[label: ui.message("general.void"), id: "confirmHistoricalVoidButton_${ it.key.uuid }", class: "confirmHistoricalVoidButton", type: "button"],
                                        [label: ui.message("general.cancel"), id: "voidCancelButton_${ it.key.uuid }", class: "cancelButton", type: "button"]]
                        ])  %>
                    </div>
                <% } %>

            </div>
        <% } %>
    <% } else { %>
            <!-- this map is keyed on relationship types; value is a count of the patients associated with the provider for that relationship type -->
            <!-- (simply show a patient count for patients that don't have the view patients privilege) -->
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

            <%=  ui.includeFragment("providermanagement", "widget/multiSelectCheckboxTable", [ items: currentSupervisees.sort { item -> item.provider.person.personName?.toString() },
                    id: superviseesId,
                    title: ui.message("providermanagement.currentSupervisees"),
                    columns: providerListDisplayFields.values(),
                    columnLabels: providerListDisplayFields.keySet(),
                    selectAction: ui.pageLink("providermanagement","providerDashboard"),
                    selectId: "provider.person.id",
                    selectIdParam: "personId",
                    formFieldName: "superviseeRelationships",
                    disabled: !context.hasPrivilege("Provider Management Dashboard - Edit Patients"),
                    emptyMessage: ui.message("providermanagement.none"),
                    disableOnMultiSelect: ["confirmEditButton_${ superviseesId }"],
                    footer: currentSupervisees.size + " " + (currentSupervisees.size != 1 ? ui.message("providermanagement.totalSupervisees") : ui.message("providermanagement.totalSupervisee")),
                    actionButtons: (context.hasPrivilege("Provider Management Dashboard - Edit Providers") ?
                                    suggestedSupervisees != null ?
                                        [[label: ui.message("general.add"), id: "addButton_${ superviseesId }", class: "addButton", type: "button"],
                                                [label: ui.message("general.edit"), id: "editButton_${ superviseesId }", class: "editButton", type: "button", disableOnMultiSelect:true],
                                                [label: ui.message("providermanagement.transfer"), id: "transferButton_${ superviseesId } ", class: "transferButton", type: "button"],
                                                [label: ui.message("providermanagement.suggest"), id: "suggestButton_${ superviseesId }", class: "suggestButton", type: "button"],
                                                [label: ui.message("general.remove"), id: "removeButton_${ superviseesId }", class: "removeButton", type: "button"],
                                                [label: ui.message("general.void"), id: "voidButton_${ superviseesId }", class: "voidButton", type: "button"]] :

                                        [[label: ui.message("general.add"), id: "addButton_${ superviseesId }", class: "addButton", type: "button"],
                                                [label: ui.message("general.edit"), id: "editButton_${ superviseesId }", class: "editButton", type: "button"],
                                                [label: ui.message("providermanagement.transfer"), id: "transferButton_${ superviseesId } ", class: "transferButton", type: "button"],
                                                [label: ui.message("general.remove"), id: "removeButton_${ superviseesId }", class: "removeButton", type: "button"],
                                                [label: ui.message("general.void"), id: "voidButton_${ superviseesId }", class: "voidButton", type: "button"]] :

                                        [])
            ]) %>

        </div>

        <% if (context.hasPrivilege("Provider Management Dashboard - Edit Providers")) { %>
        <div id="transfer_${ superviseesId }" class="transfer">
            <%=  ui.includeFragment("providermanagement", "widget/ajaxSearch", [title: ui.message("providermanagement.transferSupervisees"),
                    searchAction: ui.actionLink("providermanagement", "providerSearch", "getProviders"),
                    searchParams: [ providerRoles: [ provider.providerRole?.id ] ],
                    resultFields: providerSearchDisplayFields.values(),
                    resultFieldLabels: providerSearchDisplayFields.keySet(),
                    selectDisplayFields: ["personName.givenName","personName.middleName","personName.familyName"],
                    submitAction: ui.actionLink('providermanagement', 'providerEdit', 'transferSupervisees', [successUrl: ui.pageLink("providermanagement", "providerDashboard", [personId: person.id, paneId: superviseesId] )]),
                    submitIdParam: "newSupervisor",
                    submitParams: [ oldSupervisor: person.id ],
                    submitForm: [name: "multiSelectCheckboxForm_${ superviseesId }", required: true, requiredErrorMessage: ui.message("providermanagement.errors.supervisees.required")],
                    submitButtonId: "confirmTransferButton_${ superviseesId }",
                    fields: [ [name: "date", class: java.util.Date, label: ui.message("providermanagement.transferDate"), required: true, maxDate: "+0d", initialValue: today] ],
                    dateLabel: ui.message("providermanagement.onDate"),
                    emptyMessage: ui.message("providermanagement.noMatches"),
                    actionButtons: [[label: ui.message("general.submit"), id: "confirmTransferButton_${ superviseesId }", class: "confirmTransferButton"],
                                    [label: ui.message("general.cancel"), id: "transferCancelButton_${ superviseesId }", class: "cancelButton"]]
            ])  %>
        </div>


        <div id="add_${ superviseesId }" class="add">
            <%= ui.includeFragment("providermanagement", "widget/ajaxSearch", [title: ui.message("providermanagement.addSupervisee"),
                    searchAction: ui.actionLink("providermanagement", "providerSearch", "getProviders"),
                    searchParams: [ excludeSuperviseesOf: person.id, providerRoles: provider.providerRole?.superviseeProviderRoles.collect { it.id } ],
                    resultFields: providerSearchDisplayFields.values(),
                    resultFieldLabels: providerSearchDisplayFields.keySet(),
                    selectDisplayFields: ["personName.givenName","personName.middleName","personName.familyName"],
                    submitAction: ui.actionLink('providermanagement', 'providerEdit', 'addSupervisee', [successUrl: ui.pageLink("providermanagement", "providerDashboard", [personId: person.id, paneId: superviseesId] )]),
                    submitIdParam: "supervisee",
                    submitParams: [ supervisor: person.id ],
                    submitButtonId: "confirmAddButton_${ superviseesId }",
                    fields: [ [name: "date", class: java.util.Date, label: ui.message("providermanagement.startDate"), required: true, maxDate: "+0d", initialValue: today] ],
                    dateLabel: ui.message("providermanagement.onDate"),
                    emptyMessage: ui.message("providermanagement.noMatches"),
                    actionButtons: [[label: ui.message("general.submit"), id: "confirmAddButton_${ superviseesId }", class: "confirmAddButton"],
                                        [label: ui.message("general.cancel"), id: "addCancelButton_${ superviseesId }", class: "cancelButton"]]
            ])  %>
        </div>

        <div id="edit_${ superviseesId }" class="edit">
            <%=  ui.includeFragment("providermanagement", "widget/inputDialog", [title: ui.message("providermanagement.newStartDateSupervisees"),
                    submitAction: ui.actionLink('providermanagement', 'providerEdit', 'editSupervisees', [successUrl: ui.pageLink("providermanagement", "providerDashboard", [personId: person.id, paneId: superviseesId] )]),
                    submitButtonId: "confirmEditButton_${ superviseesId }",
                    submitForm: [name: "multiSelectCheckboxForm_${ superviseesId }", required: true, requiredErrorMessage: ui.message("providermanagement.errors.supervisee.required")],
                    fields: [ [name: "startDate", class: java.util.Date, label :ui.message("providermanagement.startDate"), required: true, maxDate: "+0d"] ],
                    actionButtons: [[label: ui.message("providermanagement.update"), id: "confirmEditButton_${ superviseesId }", class: "confirmEditButton"],
                            [label: ui.message("general.cancel"), id: "removeCancelButton_${ superviseesId }", class: "cancelButton"]]
            ])  %>
        </div>

        <div id="remove_${ superviseesId }" class="remove">
            <%=  ui.includeFragment("providermanagement", "widget/inputDialog", [title: ui.message("providermanagement.confirmRemovalSupervisees"),
                    submitAction: ui.actionLink('providermanagement', 'providerEdit', 'removeSupervisees', [successUrl: ui.pageLink("providermanagement", "providerDashboard", [personId: person.id, paneId: superviseesId] )]),
                    submitParams: [ supervisor: person.id ],
                    submitButtonId: "confirmRemoveButton_${ superviseesId }",
                    submitForm: [name: "multiSelectCheckboxForm_${ superviseesId }",  required: true, requiredErrorMessage: ui.message("providermanagement.errors.supervisees.required")],
                    fields: [ [name: "date", class: java.util.Date, label :ui.message("providermanagement.stopDate"), required: true, maxDate: "+0d", initialValue: today] ],
                    actionButtons: [[label: ui.message("general.remove"), id: "confirmRemoveButton_${ superviseesId }", class: "confirmRemoveButton"],
                            [label: ui.message("general.cancel"), id: "removeCancelButton_${ superviseesId }", class: "cancelButton"]]
            ])  %>
        </div>

        <div id="void_${ superviseesId }" class="void">
            <%=  ui.includeFragment("providermanagement", "widget/inputDialog", [title: ui.message("providermanagement.confirmVoidSupervisees"),
                    submitAction: ui.actionLink('providermanagement', 'providerEdit', 'voidSupervisees', [successUrl: ui.pageLink("providermanagement", "providerDashboard", [personId: person.id, paneId: superviseesId] )]),
                    submitParams: [ supervisor: person.id ],
                    submitButtonId: "confirmVoidButton_${ superviseesId }",
                    submitForm: [name: "multiSelectCheckboxForm_${ superviseesId }", required: true, requiredErrorMessage: ui.message("providermanagement.errors.supervisees.required")],
                    fields: [ [name: "voidReason", class: java.lang.String, label: ui.message("providermanagement.voidReason"), required: true] ],
                    actionButtons: [[label: ui.message("general.void"), id: "confirmVoidButton_${ superviseesId }", class: "confirmVoidButton"],
                            [label: ui.message("general.cancel"), id: "voidCancelButton_${ superviseesId }", class: "cancelButton"]]
            ])  %>
        </div>

        <% if (suggestedSupervisees != null) { %>
            <div id="suggest_${ superviseesId }" class="suggest">
                <%=  ui.includeFragment("providermanagement", "widget/multiSelectCheckboxTable", [ items: suggestedSupervisees.sort { item -> item.personName?.toString() },
                        title: ui.message("providermanagement.suggestedSupervisees"),
                        columns: providerSearchDisplayFields.values(),
                        columnLabels: providerSearchDisplayFields.keySet(),
                        selectAction: ui.pageLink("providermanagement","providerDashboard"),
                        selectIdParam: "personId",
                        formAction: ui.actionLink("providermanagement", "providerEdit","addSupervisees", [supervisor: person.id, successUrl: ui.pageLink("providermanagement", "providerDashboard", [personId: person.id, paneId: superviseesId] )]),
                        formFieldName: "supervisees",
                        emptyMessage: ui.message("providermanagement.none"),
                        actionButtons: [[label: ui.message("general.add"), type: "submit"],
                                [label: ui.message("general.cancel"), id: "suggestCancelButton_${ superviseesId}", class:"cancelButton", type: "reset"]]
                ]) %>
            </div>
        <% } %>

        <br/><br/>

        <% if (historicalSupervisees && context.hasPrivilege("Provider Management Dashboard - View Historical")) { %>
            <div id="historicalList_${ superviseesId }" class="historicalList">

                <%=  ui.includeFragment("providermanagement", "widget/multiSelectCheckboxTable", [ items: historicalSupervisees.sort { item -> item.provider.person.personName?.toString() },
                        id: "historical_${ superviseesId }",
                        title: ui.message("providermanagement.historicalSupervisees"),
                        columns: historicalProviderListDisplayFields.values(),
                        columnLabels: historicalProviderListDisplayFields.keySet(),
                        formFieldName: "superviseeRelationships",
                        selectAction: ui.pageLink("providermanagement","providerDashboard"),
                        selectIdParam: "personId",
                        selectId: "provider.person.id",
                        disabled: !context.hasPrivilege("Provider Management Dashboard - Edit Patients"),
                        disableOnMultiSelect: ["confirmEditHistoricalButton_${ superviseesId }"],
                        emptyMessage: ui.message("providermanagement.none"),
                        actionButtons: ( context.hasPrivilege("Provider Management Dashboard - Edit Patients") ?
                            [ [label: ui.message("general.edit"), id: "editHistoricalButton_${ superviseesId }", class: "editHistoricalButton", type: "button", disableOnMultiSelect:true],
                                    [label: ui.message("general.void"), id: "voidHistoricalButton_${ superviseesId }", class: "voidHistoricalButton", type: "button"]] : [])
                ]) %>
            </div>

            <div id="editHistorical_${ superviseesId }" class="edit">
                <%=  ui.includeFragment("providermanagement", "widget/inputDialog", [title: ui.message("providermanagement.newStartAndEndDateSupervisees"),
                        submitAction: ui.actionLink('providermanagement', 'providerEdit', 'editSupervisees', [successUrl: ui.pageLink("providermanagement", "providerDashboard", [personId: person.id, paneId: superviseesId] )]),
                        submitButtonId: "confirmEditHistoricalButton_${ superviseesId }",
                        submitForm: [name: "multiSelectCheckboxForm_historical_${ superviseesId }", required: true, requiredErrorMessage: ui.message("providermanagement.errors.supervisee.required")],
                        fields: [ [name: "startDate", class: java.util.Date, label: ui.message("providermanagement.startDate"), required: true, maxDate: "+0d"],
                                [name: "endDate", class: java.util.Date, label: ui.message("providermanagement.stopDate"), required: false, maxDate: "+0d"]  ],
                        actionButtons: [[label: ui.message("providermanagement.update"), id: "confirmEditHistoricalButton_${ superviseesId }", class: "confirmEditHistoricalButton", type: "button"],
                                [label: ui.message("general.cancel"), id: "editHistoricalCancelButton_${ superviseesId }", class: "cancelButton",type: "button"]]
                ])  %>
            </div>

            <div id="voidHistorical_${ superviseesId }" class="void">
                <%=  ui.includeFragment("providermanagement", "widget/inputDialog", [title: ui.message("providermanagement.confirmVoidSupervisees"),
                        submitAction: ui.actionLink('providermanagement', 'providerEdit', 'voidSupervisees', [successUrl: ui.pageLink("providermanagement", "providerDashboard", [personId: person.id, paneId: superviseesId] )]),
                        submitParams: [ provider: person.id ],
                        submitButtonId: "confirmHistoricalVoidButton_${ superviseesId }",
                        submitForm: [name: "multiSelectCheckboxForm_historical_${ superviseesId }",  required: true, requiredErrorMessage: ui.message("providermanagement.errors.supervisees.required")],
                        fields: [ [name: "voidReason", class: java.lang.String, label: ui.message("providermanagement.voidReason"), required: true] ],
                        actionButtons: [[label: ui.message("general.void"), id: "confirmHistoricalVoidButton_${ superviseesId }", class: "confirmHistoricalVoidButton", type: "button"],
                                [label: ui.message("general.cancel"), id: "voidCancelButton_${ superviseesId }", class: "cancelButton", type: "button"]]
                ])  %>
            </div>
        <% } %>

    <% } %>
</div>
<% } %>

    <div id="pane_${ supervisorsId }" class="pane">
        <%=  ui.includeFragment("providermanagement", "widget/multiSelectCheckboxTable", [ items: currentSupervisors.sort { item -> item.provider.person.personName?.toString() },
                columns: providerListDisplayFields.values(),
                columnLabels: providerListDisplayFields.keySet(),
                selectAction: ui.pageLink("providermanagement", "providerDashboard"),
                selectIdParam: "personId",
                selectId: "provider.person.id",
                emptyMessage: ui.message("providermanagement.none"),
                disabled: true ]) %>

    </div>

</div>
