<div>

    <table>
        <tr>
            <th>${ ui.message("providermanagement.providerRole") }</th>
            <th>${ ui.message("providermanagement.superviseeRoles") }</th>
            <th>${ ui.message("providermanagement.relationshipTypes") }</th>
            <th>${ ui.message("providermanagement.attributeTypes") }</th>
            <th>${ ui.message("general.retired") }</th>
            <th>&nbsp;</th>
            <th>&nbsp;</th>
            <th>&nbsp;</th>
        </tr>

        <!-- TODO: add strikethrough formatting if a person attribute is retired? -->

        <% providerRoles.each { %>
        <tr>
            <td>${ it.name }</td>
            <td>
                ${ it.superviseeProviderRoles?.collect { it.name }.join(', ') }
            </td>
            <td>
                ${ it.relationshipTypes?.collect { it.aIsToB }.join(', ') }
            </td>
            <td>
                ${ it.providerAttributeTypes?.collect { it.name }.join(', ') }
            </td>
            <td>${ it.retired ? ui.message("general.yes") : ui.message("general.no") }</td>
            <td><a href="${ ui.pageLink("editProviderRole", [providerRoleId: it.id]) }">${ ui.message("general.edit") }</a></td>
            <td><a href="${ ui.actionLink("providerRoleForm", "retireProviderRole", [providerRoleId: it.id]) }">${ ui.message("general.retire") }</a></td>
            <td><a href="${ ui.actionLink("providerRoleForm", "deleteProviderRole", [providerRoleId: it.id]) }">${ ui.message("general.delete") }</a></td>
        </tr>
        <% } %>

    </table>

</div>

