
<!-- TODO: make this use a random widget name, and have css configurable by that name -->
<!-- TODO: add a select all function -->

<%  ui.includeCss("providermanagement", "widget/multiSelectCheckboxTable.css")
    def id = config.id ?: ui.randomId()
    def selectIdParam = config.selectIdParam ?: 'id' %>

<script>
    jq(function() {
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
    
    <% if (config.formAction) { %>
        <form id="multiSelectCheckboxForm_${ id }" method="post" action="${ config.formAction }" >
    <% } %>
    
        <table id="multiSelectCheckboxTable_${ id }">
            <thead>
                <tr class="multiSelectTitle">
                    <th colspan="${ config.columns.size() + 1 }">${ config.title }</th>
                </tr>
                <tr class="multiSelectColumnLabels">
                    <% config.columnLabels.each { %>
                        <th>${ it }</th>
                    <% } %>
                    <th>&nbsp;</th>
                </tr>
            </thead>

            <tbody>
                <% config.items?.each { item -> %>
                    <tr>
                         <% config.columns.each { %>
                            <td>
                                <% if (config.selectAction) { %>
                                    <a href="${ config.selectAction }${ config.selectAction.contains('?') ? '' : '?' }<% if (config.selectParams) { %>&<%= config.selectParams.collect { "${ it.key }=${ it.value }" }.join("&") %><% } %>&${ selectIdParam }=${ item.id }">
                                 <% } %>

                                  <%
                                    // allows for displaying nested fields
                                    def display = item
                                     it.split("\\.").each { field ->
                                         display = display[field]
                                     }
                                     print display
                                  %>

                                 <% if (config.selectAction) { %>
                                    </a>
                                 <% } %>
                            </td>
                         <% } %>
                        <td class="checkboxCell">
                            <% if (config.formFieldName) { %>
                                <input name="${config.formFieldName}" type="checkbox" value="${ item.id }"/>
                            <% } else { %>
                                 &nbsp;
                            <% } %>
                        </td>
                    </tr>
                <% } %>
            </tbody>

            <% if (config.actionButtons) { %>
                <tr class="multiSelectActionButtons">
                    <td colspan="${ config.columns.size() + 1 }">
                        ${ ui.includeFragment("widget/actionButtons", [actionButtons: config.actionButtons]) }
                    </td>
                </tr>
            <% } %>
        </table>

    <% if (config.formAction) { %>
        </form>
     <% } %>
</div>

