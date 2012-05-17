
<% context.requirePrivilege("Provider Management - Admin")
   ui.includeCss("providermanagement", "manage.css")
   ui.decorateWith("providerManagementAdminPage") %>


<div id="editProviderSuggestion">
    ${ ui.includeFragment("providerSuggestionForm", [providerSuggestion: param.providerSuggestion,  successUrl: ui.pageLink("manageSuggestions")]) }
</div>

