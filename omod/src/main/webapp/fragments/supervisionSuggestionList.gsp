

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

        <!-- TODO: add strikethrough formatting if a person attribute is retired? -->

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
            <td><a href="${ ui.pageLink("editSupervisionSuggestion", [supervisionSuggestion: it.id]) }">${ ui.message("general.edit") }</a></td>
            <td><a href="${ ui.actionLink("supervisionSuggestionForm", "retireSupervisionSuggestion", [supervisionSuggestion: it.id]) }">${ ui.message("general.retire") }</a></td>
            <td><a href="${ ui.actionLink("supervisionSuggestionForm", "deleteSupervisionSuggestion", [supervisionSuggestion: it.id]) }">${ ui.message("general.delete") }</a></td>
        </tr>
        <% } %>

    </table>

</div>

