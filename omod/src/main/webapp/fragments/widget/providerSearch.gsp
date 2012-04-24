
<% ui.includeCss("providermanagement", "widget/providerSearch.css") %>

<div class="content providerSearch">
    <table>
        <tr>
            <th>${ config.title }</th>
        </tr>

        <% if (config.actionButtons) { %>
        <tr class="providerSearchActionButtons">
            <td>
                <% config.actionButtons.each { %>
                <a href="${ it.link }"><button>${ it.label }</button></a>
                <% } %>
            </td>
        </tr>
        <% } %>

    </table>
</div>

