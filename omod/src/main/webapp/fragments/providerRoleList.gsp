
<table>
    <tr>
        <th>${ ui.message("providermanagement.providerRole") }</th>
        <th>${ ui.message("providermanagement.superviseeRoles") }</th>
        <th>${ ui.message("providermanagement.relationshipTypes") }</th>
        <th>${ ui.message("providermanagement.attributeTypes") }</th>
        <th>${ ui.message("general.retired") }</th>
        <th>&nbsp;</th>
    </tr>

    <!-- TODO: add strikethrough formatting if a person attribute is retired? -->

    <% providerRoles.each { %>
    <tr>
        <td><a href="${ ui.pageLink("editProviderRole", [providerRoleId: it.id]) }">${ it.name }</a></td>
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
         <td>
             ${ ui.includeFragment("widget/actionButtons", [actionButtons: [ [label: ui.message("general.edit"), link: ui.pageLink("editProviderRole", [providerRoleId: it.id])],
                                                                                [label: ui.message("general.retire"), link: ui.actionLink("providerRoleForm", "retireProviderRole", [providerRoleId: it.id]), confirm: ui.message("providermanagement.confirm")],
                                                                                [label: ui.message("general.delete"), link: ui.actionLink("providerRoleForm", "deleteProviderRole", [providerRoleId: it.id]), confirm: ui.message("providermanagement.confirm")]]]
             )}
         </td>
    </tr>
    <% } %>

</table>



