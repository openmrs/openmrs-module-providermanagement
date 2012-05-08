<% config.actionButtons.each { %>
    <% if (it.link) { %>
        <a href="${ it.link }">
    <% } %>
            <button id="${ it.id ?: ''}" type="${ it.type ?: ''}">${ it.label }</button>
    <% if (it.link) { %>
        </a>
    <% } %>
<% } %>

