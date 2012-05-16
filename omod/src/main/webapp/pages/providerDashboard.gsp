
<% ui.decorateWith("providerManagementPage")
   ui.includeCss("providermanagement", "providerDashboard.css")

    // add the uuids we are going to use to identify each panel
    // (note that we remember these so that we know what panel to open on reload
    def superviseesId = "83814a90-9f89-11e1-a8b0-0800200c9a66"
    def supervisorsId = "a4ce1250-9f89-11e1-a8b0-0800200c9a66"

   // TODO: remove these
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

        // handles showing the appropriate pane
        jq('.paneSelect').click(function(){
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            // remove any existing hightlights from the table
            jq('.paneSelectTop').removeClass('selected');
            jq('.paneSelectBottom').removeClass('selected');

            // hide any existing panels on display
            jq('.pane').hide();

            // hide the add and transfer sections sections
            jq('.add').hide();
            jq('.transfer').hide();

            // clear out any existing search values
            jq('.searchField}').val('');
            jq('.searchTable > tbody > tr').remove();


            // highlight selection and show the appropriate patient pane
            jq('#paneSelectTop_' + id).addClass('selected');
            jq('#paneSelectBottom_' + id).addClass('selected');
            jq('#pane_' + id).show();
        });


        // handles displaying the transfer divs
        jq('.transferButton').click(function() {
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            // hide the add and suggest sections
            jq('.add').hide();
            jq('.suggest').hide();

            // clear out any existing search values
            jq('.searchField}').val('');
            jq('.searchTable > tbody > tr').remove();

            // show the appropriate transfer div
            jq('#transfer_' + id).show();
        }) ;

        // handles clicking the transfer cancel button
        jq('.transferCancelButton').click(function() {
            // hide the transfer section
            jq('.transfer').hide();

            // clear out any existing search values
            jq('.searchField}').val('');
            jq('.searchTable > tbody > tr').remove();
        });

        // handles clicking on the add buttons
        jq('.addButton').click(function() {
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

           // hide the transfer & suggest sections
            jq('.transfer').hide();
            jq('.suggest').hide();

            // clear out any existing search values
            jq('.searchField}').val('');
            jq('.searchTable > tbody > tr').remove();

            // show the appropriate add div
            jq('#add_' + id).show();
        });

        // handles clicking the add cancel button
        jq('.addCancelButton').click(function() {

            // hide the add section
            jq('.add').hide();

            // clear out any existing search values
            jq('.searchField}').val('');
            jq('.searchTable > tbody > tr').remove();
        });

        // handles clicking on the suggest button
        jq('.suggestButton').click(function() {
            // first fetch the id of the pane we are dealing with
            var id = jq(this).attr('id').split("_")[1];

            // hide the add & transfer sections
            jq('.add').hide();
            jq('.transfer').hide();

            // clear out any existing search values
            jq('.searchField}').val('');
            jq('.searchTable > tbody > tr').remove();

            // show the appropriate div
            jq('#suggest_' + id).show();
        })

        jq(document).ready(function(){
            jq('.pane:first').show();
            jq('.paneSelectTop:first').addClass('selected');
            jq('.paneSelectBottom:first').addClass('selected');
        });

        // handles clicking on the suggest cancel
        jq('.suggestCancelButton').click(function() {

            // hide the add section
            jq('.suggest').hide();

            // clear out any existing search values
            jq('.searchField}').val('');
            jq('.searchTable > tbody > tr').remove();
        });

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

    <div id="patientSupervisorHeader">
        <table>
            <tr>
                <td class="borderCell">&nbsp;</td>

                <% patientMap?.each { %>
                    <td id="paneSelectTop_${ it.key.uuid }" class="paneSelectTop paneSelect"> <img src=" ${ ui.resourceLink ("images/patient-nested.png") }"/></td>
                <% } %>

                <% if (provider.providerRole?.isSupervisorRole()) { %>
                    <td id="paneSelectTop_${ superviseesId }" class="paneSelectTop paneSelect"> <img src=" ${ ui.resourceLink ("images/supervisee-nested.png") }"/></td>
                <% } %>

                <td id="paneSelectTop_${ supervisorsId }" class="paneSelectTop paneSelect"> <img src=" ${ ui.resourceLink ("images/supervisor-nested.png") }"/></td>

                <td class="borderCell">&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>

                <% patientMap?.each { %>
                    <td id="paneSelectBottom_${ it.key.uuid }" class="paneSelectBottom paneSelect">${ it.key.aIsToB }<br/>${ ui.message("providermanagement.patients") }</td>
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

        <!-- this map is keyed on relationship types; value is a list of patients associated with the provider for that relationship type -->
    <% patientMap?.each {   %>

        <div id="pane_${ it.key.uuid }" class="pane">

            <div id="list_${ it.key.uuid }" class="list">
                <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: it.value.sort { item -> item.personName.toString() },
                        id: it.key.uuid,
                        columns: patientListDisplayFields.values(),
                        columnLabels: patientListDisplayFields.keySet(),
                        formAction: ui.actionLink("providerEdit","removePatients", [provider: person.id, relationshipType: it.key.id ]),
                        formFieldName: "patients",
                        actionButtons: [[label: ui.message("general.add"), id: "addButton_${ it.key.uuid }", class: "addButton", type: "button"],
                                        [label: ui.message("providermanagement.transfer"), id: "transferButton_${ it.key.uuid }", class: "transferButton", type: "button"],
                                        [label: ui.message("general.remove"), type: "submit"]] ]) %>
            </div>

            <div id="transfer_${ it.key.uuid }" class="transfer">
                <%=  ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.transferPatients"),
                        searchAction: ui.actionLink("providerSearch", "getProviders"),
                        searchParams: [ providerRoles: [ provider.providerRole?.id ] ],
                        resultFields: providerSearchDisplayFields.values(),
                        resultFieldLabels: providerSearchDisplayFields.keySet(),
                        selectAction: ui.actionLink('providerEdit', 'transferPatients'),
                        selectIdParam: "newProvider",
                        selectParams: [ oldProvider: person.id, relationshipType: it.key.id, paneId: it.key.uuid ],
                        selectForm: "multiSelectCheckboxForm_" + it.key.uuid,
                        actionButtons: [[label: ui.message("general.cancel"), id: "transferCancelButton_${ superviseesId }", class: "transferCancelButton"]] ])  %>
            </div>

            <div id="add_${ it.key.uuid }" class="add">
                <%=  ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.addPatient"),
                        searchAction: ui.actionLink("patientSearch", "getPatients"),
                        searchParams: [excludePatientsOf: person.id, existingRelationshipTypeToExclude: it.key.id ],
                        resultFields: patientSearchDisplayFields.values(),
                        resultFieldLabels: patientSearchDisplayFields.keySet(),
                        selectAction: ui.actionLink('providerEdit', 'addPatient'),
                        selectIdParam: "patient",
                        selectParams: [ provider: person.id, relationshipType: it.key.id, paneId: it.key.uuid ],
                        actionButtons: [[label: ui.message("general.cancel"), id: "addCancelButton_${ it.key.uuid }", class: "addCancelButton"]] ])  %>
            </div>

        </div>
    <% } %>

    <% if (provider.providerRole?.isSupervisorRole()) { %>
        <div id="pane_${ superviseesId }" class="pane">

            <div id="list_${ superviseesId }" class="list">
                <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: supervisees.sort { item -> item.personName?.toString() },
                        id: superviseeTableId,
                        columns: providerListDisplayFields.values(),
                        columnLabels: providerListDisplayFields.keySet(),
                        selectAction: ui.pageLink('providerDashboard'),
                        selectIdParam: "personId",
                        formAction: ui.actionLink("providerEdit","removeSupervisees", [supervisor: person.id]),
                        formFieldName: "supervisees",
                        actionButtons: [[label: ui.message("general.add"), id: "addButton_${ superviseesId }", class: "addButton", type: "button"],
                                [label: ui.message("providermanagement.transfer"), id: "transferButton_${ superviseesId } ", class: "transferButton", type: "button"],
                                [label: ui.message("providermanagement.suggest"), id: "suggestButton_${ superviseesId }", class: "suggestButton", type: "button"],
                                [label: ui.message("general.remove"), type: "submit"]] ]) %>

            </div>

            <div id="transfer_${ superviseesId }" class="transfer">
                <%=  ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.transferSupervisees"),
                        id: transferSuperviseesSearchId,
                        searchAction: ui.actionLink("providerSearch", "getProviders"),
                        searchParams: [ providerRoles: [ provider.providerRole?.id ] ],
                        resultFields: providerSearchDisplayFields.values(),
                        resultFieldLabels: providerSearchDisplayFields.keySet(),
                        selectAction: ui.actionLink('providerEdit', 'transferSupervisees'),
                        selectIdParam: "newSupervisor",
                        selectParams: [ oldSupervisor: person.id, paneId: superviseesId ],
                        selectForm: "multiSelectCheckboxForm_" + superviseeTableId,
                        actionButtons: [[label: ui.message("general.cancel"), id: "transferCancelButton_${ superviseesId }", class: "transferCancelButton"]] ])  %>
            </div>


            <div id="add_${ superviseesId }" class="add">
                <%= ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.addSupervisee"),
                        id: addSuperviseeSearchId,
                        searchAction: ui.actionLink("providerSearch", "getProviders"),
                        searchParams: [ excludeSuperviseesOf: person.id, providerRoles: provider.providerRole?.superviseeProviderRoles.collect { it.id } ],
                        resultFields: providerSearchDisplayFields.values(),
                        resultFieldLabels: providerSearchDisplayFields.keySet(),
                        selectAction: ui.actionLink('providerEdit', 'addSupervisee'),
                        selectIdParam: "supervisee",
                        selectParams: [ supervisor: person.id, paneId: superviseesId ],
                        actionButtons: [[label: ui.message("general.cancel"), id: "addCancelButton_${ superviseesId }", class: "addCancelButton"]] ])  %>
            </div>

            <% if (suggestedSupervisees != null) { %>   <!-- note that we want to display this if the results are an empty list, hence the explicit test for null here -->
            <div id="suggest_${ superviseesId }" class="suggest">
                <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: suggestedSupervisees.sort { item -> item.personName?.toString() },
                        title: ui.message("providermanagement.suggestedSupervisees"),
                        columns: providerListDisplayFields.values(),
                        columnLabels: providerListDisplayFields.keySet(),
                        selectAction: ui.pageLink('providerDashboard'),
                        selectIdParam: "personId",
                        formAction: ui.actionLink("providerEdit","addSupervisees", [supervisor: person.id]),
                        formFieldName: "supervisees",
                        actionButtons: [[label: ui.message("general.add"), type: "submit"],
                                        [label: ui.message("general.cancel"), id: "suggestCancelButton_${ superviseesId}", class:"suggestCancelButton", type: "reset"]] ]) %>
            </div>
            <% } %>
        </div>
    <% } %>

    <div id="pane_${ supervisorsId }" class="pane">
        <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: supervisors.sort { item -> item.personName?.toString() },
                columns: providerListDisplayFields.values(),
                columnLabels: providerListDisplayFields.keySet(),
                selectAction: ui.pageLink('providerDashboard'),
                selectIdParam: "personId" ]) %>

    </div>

</div>
