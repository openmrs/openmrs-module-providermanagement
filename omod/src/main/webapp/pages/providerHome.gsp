
<% ui.decorateWith("providerManagementPage") %>

<% ui.includeCss("providermanagement", "providerHome.css") %>

<div id="providerSearch" class="content">

    <%= ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.findProvider"),
            searchAction: ui.actionLink("providerSearch", "getProviders"),
            resultFields: providerSearchDisplayFields,
            selectIdParam: "personId",
            selectAction: ui.pageLink('providerDashboard') ]) %>

</div>

<div id="searchOptions" class="content">
    ${ ui.includeFragment("widget/actionButtons", [actionButtons: [ [label: ui.message("providermanagement.advancedSearch"),
                                                                     link: ui.pageLink("providerAdvancedSearchPage")] ] ]
    )}

</div>

<div id="otherOptions" class="content">
    ${ ui.includeFragment("widget/actionButtons", [actionButtons: [ [label: ui.message("providermanagement.createProvider"),
                                                                     link: ui.pageLink("providerCreate")],
                                                                    [label: ui.message("providermanagement.createProviderFromExistingPerson"),
                                                                     id: "showPersonSearchButton"] ] ]
    )}

</div>

<!-- TODO: does this need to be restricted to show only users?  not patients?  what about privileges required, since this is in essence a patient search? -->

<div id="personSearch" class="content">
    ${ ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.selectPerson"),
                                                searchAction: ui.actionLink("personSearch", "getPeople"),
                                                resultFields: personSearchDisplayFields,
                                                selectAction: ui.pageLink("providerCreate"),
                                                selectIdParam: "person"] ) }

</div>

