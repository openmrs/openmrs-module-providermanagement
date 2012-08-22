
<% ui.decorateWith("providermanagement", "providerManagementPage") %>

<div id="adminMenu">
    <a href="${ ui.pageLink('providermanagement', 'manageProviderRoles') }">${ ui.message("providermanagement.manageProviderRoles") }</a> |
    <a href="${ ui.pageLink('providermanagement', 'manageSuggestions') }">${ ui.message("providermanagement.manageSuggestions") }</a> |
    <a href="${ ui.pageLink('providermanagement', 'manageOtherSettings') }">${ ui.message("providermanagement.manageOtherSettings") }</a>
</div>

${ config.content }


