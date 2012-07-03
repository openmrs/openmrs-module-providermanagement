
<%
   context.requirePrivilege("Provider Management Dashboard - View Providers")
   ui.decorateWith("providerManagementPage")
   ui.includeCss("providermanagement", "providerHome.css")
   def providerSearchId = ui.randomId()
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

        // jump to the similar persons page if the user tries to add a provider
        jq('#addNewProviderButton').click(function() {
            var name = jq('#searchField_${ providerSearchId }').val();
            window.location='${ ui.pageLink("similarPeople") }?name=' + name;
        })

        // only show the add new provider button if the end user has entered data
        jq('#searchField_${ providerSearchId}').keyup(function() {
            var name = jq('#searchField_${ providerSearchId }').val();
            if (name == undefined || name == '') {
                jq('#addNewProviderButton').hide();
            }
            else {
                jq('#addNewProviderButton').show();
            }
        })

    });
</script>


<div id="providerSearch">

    <%= ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.findOrAddProvider"),
            id: providerSearchId,
            searchAction: ui.actionLink("providerSearch", "getProviders"),
            resultFields: providerSearchDisplayFields.values(),
            resultFieldLabels: providerSearchDisplayFields.keySet(),
            selectIdParam: "personId",
            selectAction: ui.pageLink("providerDashboard"),
            emptyMessage: ui.message("providermanagement.noMatches"),
            retiredToggle: true,
            actionButtons: [ [label: ui.message("providermanagement.advancedSearch"),
                                id: "advancedSearchShowButton"],
                             [label: ui.message("providermanagement.addNewProvider"),
                                id: "addNewProviderButton"] ] ]) %>

</div>

<div id="advancedSearch">

    ${ ui.includeFragment("providerAdvancedSearch", [selectIdParam: "personId",
                                                     id: advancedSearchId,
                                                     selectAction: ui.pageLink('providerDashboard'),
                                                     emptyMessage: ui.message("providermanagement.noMatches"),
                                                     resultFields: providerSearchDisplayFields.values(),
                                                     resultFieldLabels: providerSearchDisplayFields.keySet()]
    )}

</div>
