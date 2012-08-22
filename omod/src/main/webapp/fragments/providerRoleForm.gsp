<% ui.includeCss("providermanagement", "manage.css") %>

${ ui.startForm("saveProviderRole", [id: providerRole?.providerRoleId, successUrl: config.successUrl]) }

    <table>

       <tr>
           <td>${ ui.message("providermanagement.providerRole") }:</td>
           <td><input name="name" type="text" value="${ providerRole?.name ?: ''}"/></td>
       </tr>



        <tr>
            <td>${ ui.message("providermanagement.superviseeRoles") }:</td>
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
            <td>${ ui.message("providermanagement.patientRelationshipTypes") }:</td>
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
            <td>${ ui.message("providermanagement.associatedAttributeTypes") }:</td>
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
            <td><button type="submit">${ ui.message("general.submit") }</button>
                ${ ui.includeFragment("providermanagement", "widget/actionButtons", [actionButtons: config.actionButtons]) }
            </td>
        </tr>

    </table>

${ ui.endForm() }


