
<% ui.includeCss("providermanagement", "manage.css") %>

<% ui.decorateWith("providerManagementAdminPage") %>

<div id="editSupervisionSuggestion">
    ${ ui.includeFragment("supervisionSuggestionForm", [supervisionSuggestion: param.supervisionSuggestion,  successUrl: ui.pageLink("manageSuggestions")]) }
</div>
