
<% layoutTemplate.lines.each { it.each { item -> if (item.isToken == layoutTemplate.layoutToken) { %>
    <tr>
        <td><span class="label">${ ui.message(item.displayText) }:</span></td>
          <% if (config.mode?.equalsIgnoreCase('edit')) { %>
               <td><input type="text" id="personAddress.${ item.codeName }" name="personAddress.${ item.codeName }" value="${ config.personAddress && config.personAddress[item.codeName] ? config.personAddress[item.codeName]  : '' }" size="${ item.displaySize }"/></td>
          <% }
             else { %>   <!-- we assume that we are in "view" mode -->
                <td>${ config.personAddress && config.personAddress[item.codeName] ? config.personAddress[item.codeName]  : '' }</td>
          <% } %>
        </td>
    </tr>
<% } } } %>
