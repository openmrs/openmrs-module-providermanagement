
<%
   context.requirePrivilege("Provider Management Dashboard - View Providers")
   ui.decorateWith("providerManagementPage")
   ui.includeCss("providermanagement", "providerHome.css")
   def providerSearchId = ui.randomId()
   def personSearchId = ui.randomId()
   def advancedSearchId = ui.randomId() %>

<script>
    jq(function() {

        // buttons
        jq('#advancedSearchShowButton').click(function() {
            // hide the provider search div and reset the result fields
            jq('#providerSearch').hide();
            jq('#searchField_${ providerSearchId }').val('');
            jq('#searchTable_${ providerSearchId } > tbody > tr').remove();

            jq('#advancedSearch').show();
        });

        jq('#cancelButton_${ advancedSearchId }').click(function() {
            // hide the advanced search and reset the result fields
            jq('#advancedSearch').hide();
            jq('#advancedSearchResults_${ advancedSearchId } > tbody > tr').remove();

            jq('#providerSearch').show();
        });

        jq('#personSearchShowButton').click(function() {
            // hide the provider search div and reset the result fields
            jq('#providerSearch').hide();
            jq('#searchField_${ providerSearchId }').val('');
            jq('#searchTable_${ providerSearchId } > tbody > tr').remove();

            jq('#personSearch').show();
        });

        jq('#personSearchCancelButton').click(function() {
            // hide the person search div and reset the result fields
            jq('#personSearch').hide();
            jq('#searchField_${ personSearchId }').val('');
            jq('#searchTable_${ personSearchId } > tbody > tr').remove();

            jq('#providerSearch').show();
        });
    });
</script>


<div id="providerSearch">

    <%= ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.findProvider"),
            id: providerSearchId,
            searchAction: ui.actionLink("providerSearch", "getProviders"),
            resultFields: providerSearchDisplayFields.values(),
            resultFieldLabels: providerSearchDisplayFields.keySet(),
            selectIdParam: "personId",
            selectAction: ui.pageLink("providerDashboard"),
            emptyMessage: ui.message("providermanagement.noMatches"),
            actionButtons: [ [label: ui.message("providermanagement.advancedSearch"),
                    id: "advancedSearchShowButton"] ] ]) %>

    <br/>


    <% if (context.hasPrivilege("Provider Management Dashboard - Edit Providers")) { %>
        <% if (context.hasPrivilege("Provider Management Dashboard - View Patients")) { %>
            ${ ui.includeFragment("widget/actionButtons", [actionButtons: [ [label: ui.message("providermanagement.createProvider"),
                                                                             link: ui.pageLink("providerCreate")],
                                                                            [label: ui.message("providermanagement.createProviderFromExistingPerson"),
                                                                             id: "personSearchShowButton"] ] ]
            )}
        <% } else { %>
            ${ ui.includeFragment("widget/actionButtons", [actionButtons: [ [label: ui.message("providermanagement.createProvider"),
                    link: ui.pageLink("providerCreate")] ] ]
            )}
        <% } %>
    <% } %>

</div>

<!-- TODO: does this need to be restricted to show persons who aren't patients?  what about privileges required, since this is in essence a patient search? -->

<% if (context.hasPrivilege("Provider Management Dashboard - View Patients")) { %>
    <div id="personSearch">
        ${ ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.selectPerson"),
                                                    id: personSearchId,
                                                    searchAction: ui.actionLink("personSearch", "getPeople"),
                                                    resultFields: personSearchDisplayFields.values(),
                                                    resultFieldLabels: personSearchDisplayFields.keySet(),
                                                    selectAction: ui.pageLink("providerCreate"),
                                                    selectIdParam: "person",
                                                    emptyMessage: ui.message("providermanagement.noMatches"),
                                                    actionButtons: [ [label: ui.message("general.cancel"), id: "personSearchCancelButton"] ] ] ) }

    </div>
<% } %>

<div id="advancedSearch">

    ${ ui.includeFragment("providerAdvancedSearch", [selectIdParam: "personId",
                                                     id: advancedSearchId,
                                                     selectAction: ui.pageLink('providerDashboard'),
                                                     emptyMessage: ui.message("providermanagement.noMatches"),
                                                     resultFields: providerSearchDisplayFields.values(),
                                                     resultFieldLabels: providerSearchDisplayFields.keySet()]
    )}

</div>