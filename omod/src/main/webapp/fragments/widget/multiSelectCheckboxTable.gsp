
<!-- TODO: make this use a random widget name, and have css configurable by that name -->
<!-- TODO: add a select all function -->


<% ui.includeCss("providermanagement", "widget/multiSelectCheckboxTable.css") %>

<div class="content multiSelectCheckboxTable">
    <table>
        <tr>
            <th colspan="${ config.columns.size + 1 }">${ config.title }</th>
        </tr>

        <% config.items?.each { item -> %>
            <tr>
                 <% config.columns.each { %>
                    <td>${ item[it] }</td>
                 <% } %>
                <td><input type="checkbox"/></td>
            </tr>
        <% } %>

        <% if (config.actionButtons) { %>
            <tr class="multiSelectActionButtons">
                <td colspan="${ config.columns.size + 1 }">
                    <% config.actionButtons.each { %>
                    <a href="${ it.link }"><button>${ it.label }</button></a>
                    <% } %>
                </td>
            </tr>
        <% } %>
    </table>
</div>

