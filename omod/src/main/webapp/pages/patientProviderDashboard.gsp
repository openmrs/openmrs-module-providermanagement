
<% ui.decorateWith("providermanagement", "providerManagementPage") %>

<% ui.includeCss("providermanagement", "patientProviderDashboard.css") %>

<!--TODO: not currently complete (see PROV-3) -->

<div id="providers">

    <!-- this map is keyed on relationship types -->
    <% providerMap?.each {  %>

        <%=  ui.includeFragment("providermanagement", "widget/multiSelectCheckboxTable", [ items: it.value?.sort { item -> item.personName.toString() },
                title: it.key.aIsToB  + " " + ui.message("providermanagement.providers"),
                columns: providerListDisplayFields,
                formAction: ui.actionLink("providermanagement", "patientEdit","removeProviders", [patient: patient.id, relationshipType: it.key.id ]),
                formFieldName: "providers",
                actionButtons: [[label: ui.message("general.remove"), type: "submit"]] ]) %>


        <% if (providerSuggestionMap[it.key]) { %>

            <%=  ui.includeFragment("providermanagement", "widget/multiSelectCheckboxTable", [ items: providerSuggestionMap[it.key]?.sort { item -> item.personName.toString() },
                    title: it.key.aIsToB  + " " + ui.message("providermanagement.suggestions"),
                    columns: providerListDisplayFields,
                    formAction: ui.actionLink("providermanagement", "patientEdit","addProviders", [patient: patient.id, relationshipType: it.key.id ]),
                    formFieldName: "providers",
                    actionButtons: [[label: ui.message("general.add"), type: "submit"]] ]) %>


        <% } %>

    <% } %>

</div>
