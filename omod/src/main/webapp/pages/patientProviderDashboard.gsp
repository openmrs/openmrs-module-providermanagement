
<% ui.decorateWith("providerManagementPage") %>

<% ui.includeCss("providermanagement", "patientProviderDashboard.css") %>

<!--TODO: not currently complete -->

<div id="providers">

    <!-- this map is keyed on relationship types -->
    <% providerMap?.each {  %>

        <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: it.value?.sort { item -> item.personName.toString() },
                title: it.key.aIsToB  + " " + ui.message("providermanagement.providers"),
                columns: providerListDisplayFields,
                formAction: ui.actionLink("patientEdit","removeProviders", [patient: patient.id, relationshipType: it.key.id ]),
                formFieldName: "providers",
                actionButtons: [[label: ui.message("general.remove"), type: "submit"]] ]) %>


        <% if (providerSuggestionMap[it.key]) { %>

            <%=  ui.includeFragment("widget/multiSelectCheckboxTable", [ items: providerSuggestionMap[it.key]?.sort { item -> item.personName.toString() },
                    title: it.key.aIsToB  + " " + ui.message("providermanagement.suggestions"),
                    columns: providerListDisplayFields,
                    formAction: ui.actionLink("patientEdit","addProviders", [patient: patient.id, relationshipType: it.key.id ]),
                    formFieldName: "providers",
                    actionButtons: [[label: ui.message("general.add"), type: "submit"]] ]) %>


        <% } %>

    <% } %>

</div>
