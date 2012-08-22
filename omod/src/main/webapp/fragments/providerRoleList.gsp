
<table>
    <tr>
        <th>${ ui.message("providermanagement.providerRole") }</th>
        <th>${ ui.message("providermanagement.superviseeRoles") }</th>
        <th>${ ui.message("providermanagement.patientRelationshipTypes") }</th>
        <th>${ ui.message("providermanagement.attributeTypes") }</th>
        <th>${ ui.message("general.retired") }</th>
        <th>&nbsp;</th>
    </tr>

    <% providerRoles.each { %>
    <tr>
        <td><a href="${ ui.pageLink("providermanagement", "editProviderRole", [providerRoleId: it.id]) }">${ (it.retired ? '<span class="retired">' + it.name + '</span>': it.name) }</a></td>
        <td>
            ${ it.superviseeProviderRoles?.collect { (it.retired ? '<span class="retired">' + it.name + '</span>' : it.name) }.join(', ') }
        </td>
        <td>
            ${ it.relationshipTypes?.collect { (it.retired ? '<span class="retired">' + it.aIsToB + '</span>' : it.aIsToB) }.join(', ') }
        </td>
        <td>
            ${ it.providerAttributeTypes?.collect { (it.retired ? '<span class="retired">' + it.name + '</span>' : it.name) }.join(', ') }
        </td>
        <td>${ it.retired ? ui.message("general.yes") : ui.message("general.no") }</td>
         <td>
             ${ ui.includeFragment("providermanagement", "widget/actionButtons", [actionButtons: [ [label: ui.message("general.edit"), link: ui.pageLink("providermanagement", "editProviderRole", [providerRoleId: it.id])],
                                                                                [label: ui.message("general.retire"), link: ui.actionLink("providermanagement", "providerRoleForm", "retireProviderRole", [providerRoleId: it.id]), confirm: ui.message("providermanagement.confirm")],
                                                                                [label: ui.message("general.delete"), link: ui.actionLink("providermanagement", "providerRoleForm", "deleteProviderRole", [providerRoleId: it.id]), confirm: ui.message("providermanagement.confirm")]]]
             )}
         </td>
    </tr>
    <% } %>

</table>



