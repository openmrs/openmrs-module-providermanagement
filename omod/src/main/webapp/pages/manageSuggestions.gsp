<% context.requirePrivilege("Provider Management - Admin")
   ui.includeCss("providermanagement", "manage.css")
   ui.decorateWith("providermanagement", "providerManagementAdminPage") %>

<div id="suggestionLists">

    <!-- currently hiding provider suggestions until we begin to use them witin the module -->
    <% //
      //<div id="providerSuggestionList">
        //<a href="${ ui.pageLink("providermanagement", "editProviderSuggestion") }">${ ui.message("providermanagement.addAProviderSuggestion") }</a>
        //${ ui.includeFragment("providermanagement", "providerSuggestionList") }
    //</div>
    %>

    <!-- <br/><br/><br/>  -->

    <div id="providerSupervisionSuggestionList">
        <a href="${ ui.pageLink("providermanagement", "editSupervisionSuggestion") }">${ ui.message("providermanagement.addASupervisionSuggestion") }</a>
        ${ ui.includeFragment("providermanagement", "supervisionSuggestionList") }
    </div>
</div>