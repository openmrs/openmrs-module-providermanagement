
<div>

    <table>
        <tr>
            <th>${ ui.message("general.name") }</th>
            <th>${ ui.message("providermanagement.relationshipType") }</th>
            <th>${ ui.message("providermanagement.evaluator") }</th>
            <th>${ ui.message("general.retired") }</th>
            <th>&nbsp;</th>
        </tr>

        <!-- TODO: add strikethrough formatting if a person attribute is retired? -->

        <% providerSuggestions?.each { %>
            <tr>
                <td>${ it.name }</td>
                <td>
                    ${ it.relationshipType.aIsToB }
                </td>
                <td>
                    ${ it.evaluator }
                </td>
                <td>${ it.retired ? ui.message("general.yes") : ui.message("general.no") }</td>
                <td>
                    ${ ui.includeFragment("widget/actionButtons", [actionButtons: [ [label: ui.message("general.edit"), link: ui.pageLink("editProviderSuggestion", [providerSuggestion: it.id])],
                            [label: ui.message("general.retire"), link: ui.actionLink("providerSuggestionForm", "retireProviderSuggestion", [providerSuggestion: it.id]), confirm: ui.message("providermanagement.confirm")],
                            [label: ui.message("general.delete"), link: ui.actionLink("providerSuggestionForm", "deleteProviderSuggestion", [providerSuggestion: it.id]), confirm: ui.message("providermanagement.confirm")]]]
                    )}
                 </td>
            </tr>
        <% } %>

    </table>

</div>