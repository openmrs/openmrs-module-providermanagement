
<% ui.decorateWith("providerManagementPage")
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


<div id="providerSearch" class="content">

    <%= ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.findProvider"),
            id: providerSearchId,
            searchAction: ui.actionLink("providerSearch", "getProviders"),
            resultFields: providerSearchDisplayFields,
            selectIdParam: "personId",
            selectAction: ui.pageLink('providerDashboard'),
            actionButtons: [ [label: ui.message("providermanagement.advancedSearch"),
                    id: "advancedSearchShowButton"] ] ]) %>

    <br/>

    ${ ui.includeFragment("widget/actionButtons", [actionButtons: [ [label: ui.message("providermanagement.createProvider"),
                                                                     link: ui.pageLink("providerCreate")],
                                                                    [label: ui.message("providermanagement.createProviderFromExistingPerson"),
                                                                     id: "personSearchShowButton"] ] ]
    )}

</div>

<!-- TODO: does this need to be restricted to show only users?  not patients?  what about privileges required, since this is in essence a patient search? -->

<div id="personSearch" class="content">
    ${ ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.selectPerson"),
                                                id: personSearchId,
                                                searchAction: ui.actionLink("personSearch", "getPeople"),
                                                resultFields: personSearchDisplayFields,
                                                selectAction: ui.pageLink("providerCreate"),
                                                selectIdParam: "person",
                                                actionButtons: [ [label: ui.message("general.cancel"), id: "personSearchCancelButton"] ] ] ) }

</div>

<div id="advancedSearch" class="content">

    ${ ui.includeFragment("providerAdvancedSearch", [selectIdParam: "personId",
                                                     id: advancedSearchId,
                                                     selectAction: ui.pageLink('providerDashboard'),
                                                     resultFields: providerSearchDisplayFields]) }

</div>