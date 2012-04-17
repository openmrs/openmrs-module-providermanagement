
<% ui.decorateWith("providerManagementPage") %>

<div id="adminMenu">

   ${ ui.message("admin.title.short") } | <a href="${ ui.pageLink('manageProviderRoles') }">${ ui.message("providermanagement.manageProviderRoles") }</a> | <a href="${ ui.pageLink('manageProviderRoles') }">${ ui.message("providermanagement.manageSuggestions") }</a>

</div>

${ config.content }


