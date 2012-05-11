<% ui.decorateWith("providerManagementAdminPage") %>

${ ui.includeFragment("supervisionSuggestionForm", [supervisionSuggestion: param.supervisionSuggestion,  successUrl: ui.pageLink("manageSuggestions")]) }

