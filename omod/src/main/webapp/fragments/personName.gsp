
<% layoutTemplate.lines.each { it.each { item -> if (item.isToken == layoutTemplate.layoutToken) { %>
    <tr>
        <td><span class="label">${ ui.message(item.displayText) }:</span></td>
        <% if (config.mode?.equalsIgnoreCase('edit')) { %>
             <td>
                 <input type="text" id="personName.${ item.codeName }" name="personName.${ item.codeName }" value="${ config.personName && config.personName[item.codeName] ? config.personName[item.codeName]  : '' }" size="${ item.displaySize }"/>
             </td>
        <% }
        else { %>   <!-- we assume that we are in "view" mode -->
            <td>${ config.personName && config.personName[item.codeName] ? config.personName[item.codeName]  : '' }</td>
        <% } %>
        </td>
    </tr>
<% } } } %>

