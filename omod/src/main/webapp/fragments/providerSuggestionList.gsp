
<div>

    <table>
        <tr>
            <th>${ ui.message("general.name") }</th>
            <th>${ ui.message("providermanagement.relationshipType") }</th>
            <th>${ ui.message("providermanagement.evaluator") }</th>
            <th>${ ui.message("general.retired") }</th>
            <th>&nbsp;</th>
        </tr>

        <% providerSuggestions?.each { %>
            <tr>
                <td>
                    ${ (it.retired ? '<span class="retired">' + it.name + '</span>' : it.name) }
                </td>
                <td>
                    ${ (it.retired ? '<span class="retired">' + it.relationshipType.aIsToB + '</span>' : it.relationshipType.aIsToB) }
                </td>
                <td>
                    ${ it.evaluator }
                </td>
                <td>${ it.retired ? ui.message("general.yes") : ui.message("general.no") }</td>
                <td>
                    ${ ui.includeFragment("providermanagement", "widget/actionButtons", [actionButtons: [ [label: ui.message("general.edit"), link: ui.pageLink("providermanagement", "editProviderSuggestion", [providerSuggestion: it.id])],
                            [label: ui.message("general.retire"), link: ui.actionLink("providermanagement", "providerSuggestionForm", "retireProviderSuggestion", [providerSuggestion: it.id]), confirm: ui.message("providermanagement.confirm")],
                            [label: ui.message("general.delete"), link: ui.actionLink("providermanagement", "providerSuggestionForm", "deleteProviderSuggestion", [providerSuggestion: it.id]), confirm: ui.message("providermanagement.confirm")]]]
                    )}
                 </td>
            </tr>
        <% } %>

    </table>

</div>