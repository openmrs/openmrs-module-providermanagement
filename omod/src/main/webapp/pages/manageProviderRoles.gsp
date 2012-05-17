<% context.requirePrivilege("Provider Management - Admin")
   ui.includeCss("providermanagement", "manage.css")
   ui.decorateWith("providerManagementAdminPage") %>

<div id="providerRoleList">
    <a href="${ ui.pageLink("editProviderRole") }">${ ui.message("providermanagement.addAProviderRole") }</a>
    ${ ui.includeFragment("providerRoleList") }
</div>

