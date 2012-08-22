<div>


    ${ ui.startForm("saveSupervisionSuggestion", [id: supervisionSuggestion?.id, successUrl: config.successUrl]) }

    <table>

        <tr>
            <td>${ ui.message("general.name") }:</td>
            <td><input name="name" type="text" value="${ supervisionSuggestion?.name ?: ''}"/></td>
        </tr>


        <tr>
            <td>${ ui.message("providermanagement.providerRole") }:</td>
            <td>
                <select name="providerRole">
                    <% providerRoles.each { %>
                    <option value="${ it.id }"
                        ${ supervisionSuggestion?.providerRole?.id == it.id ? 'selected' : '' }
                    >${ it.name }</option>
                    <% } %>
                </select>
            </td>
        </tr>

    <tr>
        <td>${ ui.message("providermanagement.suggestionType") }:</td>
        <td>
            <select name="suggestionType">
                <% suggestionTypes.each { %>
                <option value="${ it }"
                    ${ supervisionSuggestion?.suggestionType == it ? 'selected' : '' }
                >${ it }</option>
                <% } %>
            </select>
        </td>
    </tr>


        <!-- currently only groovy type is supported so this is an uneditable field -->
        <tr>
            <td>${ ui.message("providermanagement.evaluator") }:</td>
            <td>
                ${ supervisionSuggestion?.evaluator }
            </td>
        </tr>

        <tr>
            <td>${ ui.message("providermanagement.criteria") }</td>
            <td>
                <textarea name="criteria" rows="25" cols="80">${ supervisionSuggestion?.criteria ?: '' }</textarea>
            </td>
        </tr>


        <tr>
            <td>&nbsp;</td>
            <td><button type="submit">${ ui.message("general.submit") }</button>
                ${ ui.includeFragment("providermanagement", "widget/actionButtons", [actionButtons: config.actionButtons]) }
            </td>
        </tr>

    </table>

    ${ ui.endForm() }

</div>