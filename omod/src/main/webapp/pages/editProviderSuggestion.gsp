<% ui.decorateWith("providerManagementAdminPage") %>

${ ui.includeFragment("providerSuggestionForm", [providerSuggestion: param.providerSuggestion,  successUrl: ui.pageLink("manageSuggestions")]) }

