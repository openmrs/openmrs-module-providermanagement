

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
            <td>
                ${ (it.retired ? '<span class="retired">' + it.name + '</span>' : it.name) }
            </td>
            <td>
                ${ (it.retired ? '<span class="retired">' + it.providerRole?.name + '</span>' : it.providerRole?.name) }
            </td>
            <td>
                ${ (it.retired ? '<span class="retired">' + it.suggestionType + '</span>' : it.suggestionType) }
            </td>
            <td>
                ${ it.evaluator }
            </td>
            <td>${ it.retired ? ui.message("general.yes") : ui.message("general.no") }</td>
            <td>
                ${ ui.includeFragment("providermanagement", "widget/actionButtons", [actionButtons: [ [label: ui.message("general.edit"), link: ui.pageLink("providermanagement", "editSupervisionSuggestion", [supervisionSuggestion: it.id])],
                        [label: ui.message("general.retire"), link: ui.actionLink("providermanagement", "supervisionSuggestionForm", "retireSupervisionSuggestion", [supervisionSuggestion: it.id]), confirm: ui.message("providermanagement.confirm")],
                        [label: ui.message("general.delete"), link: ui.actionLink("providermanagement", "supervisionSuggestionForm", "deleteSupervisionSuggestion", [supervisionSuggestion: it.id]), confirm: ui.message("providermanagement.confirm")]]]
                )}
            </td>
        </tr>
        <% } %>

    </table>

</div>

