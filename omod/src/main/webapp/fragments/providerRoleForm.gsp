<div>


${ ui.startForm("updateProviderRole", [id: providerRole?.providerRoleId, successUrl: config.successUrl]) }

    <table>

       <tr>
           <td>${ ui.message("providermanangement.providerRole") }:</td>
           <td><input name="name" type="text" value="${ providerRole?.name ?: ''}"/></td>
       </tr>



        <tr>
            <td>${ ui.message("providermanangement.superviseeRoles") }:</td>
            <td>
                <select name="superviseeProviderRoles" multiple>
                    <% providerRoles.each { %>
                    <option value="${ it.id }"
                        ${ providerRole?.superviseeProviderRoles?.collect{types -> types.id}?.contains(it.id) ? 'selected' : '' }
                    >${ it.name }</option>
                    <% } %>
                </select>
            </td>
        </tr>


        <tr>
            <td>${ ui.message("providermanangement.associatedRelationshipTypes") }:</td>
            <td>
                <select name="relationshipTypes" multiple>
                    <% relationshipTypes.each { %>
                        <option value="${ it.id }"
                            ${ providerRole?.relationshipTypes?.collect{types -> types.id}?.contains(it.id) ? 'selected' : '' }
                        >${ it.aIsToB }</option>
                    <% } %>
                </select>
            </td>
        </tr>


        <tr>
            <td>${ ui.message("providermanangement.associatedAttributeTypes") }:</td>
            <td>
                <select name="providerAttributeTypes" multiple>
                    <% providerAttributeTypes.each { %>
                    <option value="${ it.id }"
                        ${ providerRole?.providerAttributeTypes?.collect{types -> types.id}?.contains(it.id) ? 'selected' : '' }
                    >${ it.name }</option>
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

