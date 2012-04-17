<div>


${ ui.startForm("updateProviderRole") }

    <table>

       <tr>
           <td>${ ui.message("providermanangement.providerRole") }:</td>
           <td><input name="name" type="text" value="${ providerRole?.name ?: ''}"/></td>
       </tr>


        <tr>
            <td>${ ui.message("providermanangement.associatedRelationshipTypes") }:</td>
            <td>
                <select name="relationshipTypes" multiple>
                    <% relationshipTypes.each { %>
                        <option value="${ it.id }">${ it.aIsToB }</option>
                    <% } %>
                </select>
            </td>
        </tr>


        <tr>
            <td>&nbsp;</td>
            <td><input type="submit"/></td>
        </tr>

    </table>

${ ui.endForm() }

</div>

