<div>

    <table>
        <th>
            <td>${ ui.message("providermanagement.providerRole") }</td>
            <td>${ ui.message("providermanagement.superviseeRoles") }</td>
            <td>${ ui.message("providermanagement.relationshipTypes") }</td>
            <td>${ ui.message("general.retired") }</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
        </th>

        <% providerRoles.each { %>
        <tr>
            <td>${ it.name }</td>
            <td>a</td>
            <td>
                <% it.relationshipTypes.each { %> ${ it.aIsToB }<% } %>
            </td>
            <td>c</td>
            <td>${ ui.message("general.edit") }</td>
            <td>${ ui.message("general.delete") }</td>
        </tr>
        <% } %>

    </table>

</div>

