
<%
    context.requirePrivilege("Provider Management Dashboard - Edit Providers")
    ui.decorateWith("providermanagement", "providerManagementPage")
    ui.includeCss("providermanagement", "similarPeople.css")
%>

<script>
    jq(function() {

        // determine which list to display on page load
        <% if (similarProviders) { %>
            jq('#providerList').show();
        <% } else { %>
            jq('#peopleList').show();
        <% } %>

        // define click action for provider button
        // either show peopleList, or if people list is empty, skip directly to create provider
        <% if (similarPeople) { %>
            jq('#notFoundInProviderListButton').click(function() {
                jq('#providerList').hide();
                jq('#peopleList').show();
            });
        <% } else { %>
            window.location = '${ ui.pageLink("providermanagement", "providerCreate", [name: name]) }';
        <% } %>

        // define click action for the people button
        jq('#notFoundInPeopleListButton').click(function() {
            window.location = '${ ui.pageLink("providermanagement", "providerCreate", [name: name]) }';
        });

    });

</script>

<!-- the list of any similar providers -->

<div id="providerList" class="list">
      ${ ui.message('providermanagement.similarProviders.message') }
      <%=  ui.includeFragment("providermanagement", "widget/multiSelectCheckboxTable", [ items: similarProviders,
            columns: providerSearchDisplayFields.values(),
            columnLabels: providerSearchDisplayFields.keySet(),
            selectAction: ui.pageLink("providermanagement", "providerDashboard"),
            selectIdParam: "personId",
            disabled: true,
            actionButtons: [[label: ui.message("providermanagement.providerNotFoundInList"), id: "notFoundInProviderListButton"]]
      ]) %>
</div>


<!-- the list of any similar persons -->

<div id="peopleList" class="list">
    ${ ui.message('providermanagement.similarPersons.message') }
    <%=  ui.includeFragment("providermanagement", "widget/multiSelectCheckboxTable", [ items: similarPeople,
            columns: personSearchDisplayFields.values(),
            columnLabels: personSearchDisplayFields.keySet(),
            selectAction: ui.pageLink("providermanagement", "providerCreate"),
            selectIdParam: "personId",
            disabled: true,
            actionButtons: [[label: ui.message("providermanagement.providerNotFoundInList"), id: "notFoundInPeopleListButton"]]
    ]) %>
</div>