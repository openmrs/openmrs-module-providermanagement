<% context.requirePrivilege("Provider Management - Admin")
   ui.includeCss("providermanagement", "manage.css")
   ui.decorateWith("providerManagementAdminPage") %>

<div id="suggestionLists">

    <!-- currently hiding provider suggestions until we begin to use them witin the module -->
    <% //
      //<div id="providerSuggestionList">
        //<a href="${ ui.pageLink("editProviderSuggestion") }">${ ui.message("providermanagement.addAProviderSuggestion") }</a>
        //${ ui.includeFragment("providerSuggestionList") }
    //</div>
    %>

    <!-- <br/><br/><br/>  -->

    <div id="providerSupervisionSuggestionList">
        <a href="${ ui.pageLink("editSupervisionSuggestion") }">${ ui.message("providermanagement.addASupervisionSuggestion") }</a>
        ${ ui.includeFragment("supervisionSuggestionList") }
    </div>
</div>