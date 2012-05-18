

<div>

    <table>
        <tr>
            <th>${ ui.message("general.name") }</th>
            <th>${ ui.message("providermanagement.providerRole") }</th>
            <th>${ ui.message("providermanagement.suggestionType") }</th>
            <th>${ ui.message("providermanagement.evaluator") }</th>
            <th>${ ui.message("general.retired") }</th>
            <th>&nbsp;</th>
            <th>&nbsp;</th>
            <th>&nbsp;</th>
        </tr>

        <% supervisionSuggestions?.each { %>
        <tr>
            <td>${ it.name }</td>
            <td>
                ${ it.providerRole?.name }
            </td>
            <td>
                ${ it.suggestionType }
            </td>
            <td>
                ${ it.evaluator }
            </td>
            <td>${ it.retired ? ui.message("general.yes") : ui.message("general.no") }</td>
            <td>
                ${ ui.includeFragment("widget/actionButtons", [actionButtons: [ [label: ui.message("general.edit"), link: ui.pageLink("editSupervisionSuggestion", [supervisionSuggestion: it.id])],
                        [label: ui.message("general.retire"), link: ui.actionLink("supervisionSuggestionForm", "retireSupervisionSuggestion", [supervisionSuggestion: it.id]), confirm: ui.message("providermanagement.confirm")],
                        [label: ui.message("general.delete"), link: ui.actionLink("supervisionSuggestionForm", "deleteSupervisionSuggestion", [supervisionSuggestion: it.id]), confirm: ui.message("providermanagement.confirm")]]]
                )}
            </td>
        </tr>
        <% } %>

    </table>

</div>

