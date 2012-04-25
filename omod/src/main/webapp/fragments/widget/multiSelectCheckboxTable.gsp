
<!-- TODO: make this use a random widget name, and have css configurable by that name -->
<!-- TODO: add a select all function -->

<%  ui.includeCss("providermanagement", "widget/multiSelectCheckboxTable.css")
    def id = config.id ?: ui.randomId()
    def selectIdParam = config.selectIdParam ?: 'id' %>

<script>
    jq(function() {
       <% if (config.selectAction) { %>
            // configure the action that occurs on a row click
            jq('#multiSelectCheckboxTable_${ id } > tbody > tr').click(function() {
                window.location = '${ config.selectAction }'+
                        <% if (config.selectParams) { %>
                        '&<%= config.selectParams.collect { "${ it.key }=${ it.value }" }.join("&") %>' +
                        <% } %>
                        '&${ selectIdParam }=' + jq(this).find('input').val();
            });
        <% } %>

        // configure highlighting
        jq('#multiSelectCheckboxTable_${ id } > tbody > tr').mouseover(function() {
            jq(this).addClass('highlighted');
        });
        jq('#multiSelectCheckboxTable_${ id } > tbody > tr').mouseout(function() {
            jq(this).removeClass('highlighted');
        });
    });
</script>


<div class="content multiSelectCheckboxTable">
    <table id="multiSelectCheckboxTable_${ id }">
        <thead>
            <tr>
                <th colspan="${ config.columns.size + 1 }">${ config.title }</th>
            </tr>
        </thead>

        <tbody>
            <% config.items?.each { item -> %>
                <tr>
                     <% config.columns.each { %>
                        <td>${ item[it] }</td>
                     <% } %>
                    <td class="checkboxCell"><input type="checkbox" value="${ item.id }"/></td>
                </tr>
            <% } %>
        </tbody>

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

