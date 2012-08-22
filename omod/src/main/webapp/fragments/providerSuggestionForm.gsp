
${ ui.startForm("saveProviderSuggestion", [id: providerSuggestion?.providerSuggestionId, successUrl: config.successUrl]) }

    <table>

        <tr>
            <td>${ ui.message("general.name") }:</td>
            <td><input name="name" type="text" value="${ providerSuggestion?.name ?: ''}"/></td>
        </tr>


        <tr>
            <td>${ ui.message("providermanagement.relationshipType") }:</td>
            <td>
                <select name="relationshipType">
                    <% relationshipTypes.each { %>
                    <option value="${ it.id }"
                        ${ providerSuggestion?.relationshipType?.id == it.id ? 'selected' : '' }
                    >${ it.aIsToB }</option>
                    <% } %>
                </select>
            </td>
        </tr>


        <!-- currently only groovy type is supported so this is an uneditable field -->
        <tr>
            <td>${ ui.message("providermanagement.evaluator") }:</td>
            <td>
                ${ providerSuggestion?.evaluator }
            </td>
        </tr>

        <tr>
            <td>${ ui.message("providermanagement.criteria") }</td>
            <td>
                <textarea name="criteria" rows="25" cols="80">${ providerSuggestion?.criteria ?: '' }</textarea>
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



