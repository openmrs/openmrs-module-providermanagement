
<div>

    <table>
        <tr>
            <th>${ ui.message("general.name") }</th>
            <th>${ ui.message("providermanagement.relationshipType") }</th>
            <th>${ ui.message("providermanagement.evaluator") }</th>
            <th>${ ui.message("general.retired") }</th>
            <th>&nbsp;</th>
            <th>&nbsp;</th>
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
                <td><a href="${ ui.pageLink("editProviderSuggestion", [providerSuggestion: it.id]) }">${ ui.message("general.edit") }</a></td>
                <td><a href="${ ui.actionLink("providerSuggestionForm", "retireProviderSuggestion", [providerSuggestion: it.id]) }">${ ui.message("general.retire") }</a></td>
                <td><a href="${ ui.actionLink("providerSuggestionForm", "deleteProviderSuggestion", [providerSuggestion: it.id]) }">${ ui.message("general.delete") }</a></td>
            </tr>
        <% } %>

    </table>

</div>