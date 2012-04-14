
<% ui.decorateWith("providerManagementPage") %>

<div id="adminMenu">

   ${ ui.message("admin.title.short") } | <a href="${ ui.pageLink('manageProviderRoles') }">${ ui.message("providermanagement.manageSuggestions") }</a> | ${ ui.message("providermanagement.manageSuggestions") }

</div>

${ config.content }


