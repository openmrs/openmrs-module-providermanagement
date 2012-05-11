
<% ui.decorateWith("providerManagementAdminPage") %>

<a href="${ ui.pageLink("editProviderSuggestion") }">${ ui.message("providermanagement.addAProviderSuggestion") }</a>

${ ui.includeFragment("providerSuggestionList") }

<a href="${ ui.pageLink("editSupervisionSuggestion") }">${ ui.message("providermanagement.addASupervisionSuggestion") }</a>

${ ui.includeFragment("supervisionSuggestionList") }
