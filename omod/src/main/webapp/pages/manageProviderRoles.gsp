<% context.requirePrivilege("Provider Management - Admin")
   ui.includeCss("providermanagement", "manage.css")
   ui.decorateWith("providermanagement", "providerManagementAdminPage") %>

<div id="providerRoleList">
    <a href="${ ui.pageLink("providermanagement", "editProviderRole") }">${ ui.message("providermanagement.addAProviderRole") }</a>
    ${ ui.includeFragment("providermanagement", "providerRoleList") }
</div>

