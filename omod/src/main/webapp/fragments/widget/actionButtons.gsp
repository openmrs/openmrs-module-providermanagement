<% config.actionButtons.each { %>
    <% if (it.link) { %>
        <a class="buttonLink" href="${ it.link }"
          <% if (it.confirm) { %>
            onclick="return confirm('${ it.confirm }')"
          <% } %>
        />
    <% } %>
            <button id="${ it.id ?: ''}" class="${ it.class ?: ''}" type="${ it.type ?: ''}">${ it.label }</button>
    <% if (it.link) { %>
        </a>
    <% } %>
<% } %>

