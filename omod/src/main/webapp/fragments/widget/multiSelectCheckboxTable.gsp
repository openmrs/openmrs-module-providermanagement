
<%  ui.includeCss("providermanagement", "widget/multiSelectCheckboxTable.css")
    def id = config.id ?: ui.randomId()
    def selectIdParam = config.selectIdParam ?: 'id'
    def disabled = config.disabled ?: false
%>

<script>
    jq(function() {
        // configure highlighting
        jq('#multiSelectCheckboxTable_${ id } > tbody > tr').mouseover(function() {
            jq(this).addClass('highlighted');
        });
        jq('#multiSelectCheckboxTable_${ id } > tbody > tr').mouseout(function() {
            jq(this).removeClass('highlighted');
        });

        // handle the select all function
        jq('#selectAll_${ id }').click(function() {
           if(jq(this).attr('checked')) {
               jq('.checkbox_${ id }').attr('checked', true);
           }
           else {
               jq('.checkbox_${ id }').attr('checked', false);
           }
        });
    });
</script>


<div class="content multiSelectCheckboxTable">
    
    <% if (!disabled) { %>
        <form id="multiSelectCheckboxForm_${ id }" method="post" action="${ config.formAction ?: '' }" >
    <% } %>
    
        <table id="multiSelectCheckboxTable_${ id }">
            <thead>

                <% if (config.title) { %>
                    <tr class="multiSelectTitle">
                        <th colspan="${ config.columns.size() + 1 }">${ config.title }</th>
                    </tr>
                <% } %>

                <tr class="multiSelectColumnLabels">
                    <% config.columnLabels.each { %>
                        <th>${ it }</th>
                    <% } %>

                    <% if (!disabled) { %>
                        <th class="checkboxCell">
                            <% if (config.items?.size) { %>
                                <input id="selectAll_${ id }" type="checkbox" />
                            <% } else { %>
                                &nbsp;
                            <% } %>
                        </th>
                    <% } %>
                </tr>
            </thead>

            <tbody>
                <% if (config.items) {
                    config.items?.each { item -> %>

                    <%
                            def selectId

                            if (config.selectAction) {
                                if (config.selectId) {
                                    selectId = item

                                    config.selectId.split("\\.").each { field ->
                                        if (selectId) { selectId = selectId[field] }
                                    }
                                }
                                else {
                                    selectId = item.id
                                }
                            }
                    %>

                    <tr>
                        <% config.columns.each { %>
                        <td>
                            <% if (config.selectAction) { %>
                                    <a href="${ config.selectAction }${ config.selectAction.contains('?') ? '' : '?' }<% if (config.selectParams) { %>&<%= config.selectParams.collect { "${ it.key }=${ it.value }" }.join("&") %><% } %>&${ selectIdParam }=${ selectId }">
                                <% } %>

                                <%
                                        // allows for displaying nested fields
                                        def display = item
                                        it.split("\\.").each { field ->
                                            if (display) { display = display[field] }
                                        }
                                        print display ? ui.format(display) : ''
                                %>

                                <% if (config.selectAction) { %>
                                    </a>
                            <% } %>
                        </td>
                        <% } %>

                        <% if (!disabled) { %>
                        <td class="checkboxCell">
                            <% if (config.formFieldName) { %>
                            <input name="${config.formFieldName}" class="checkbox_${ id }" type="checkbox" value="${ item.id }"/>
                            <% } else { %>
                            &nbsp;
                            <% } %>
                        </td>
                        <% } %>
                    </tr>
                    <% }
                 } else if (config.emptyMessage) { %>
                    <tr>
                        <td colspan="${ config.columns.size() + 1 }">${ config.emptyMessage }</td>
                    </tr>
                <% } %>
            </tbody>

            <% if (config.footer) { %>
                <tr class="multiSelectFooter">
                    <th colspan="${ config.columns.size() + 1 }">${ config.footer }</th>
                </tr>
            <% } %>

            <% if (config.actionButtons) { %>
                <tr class="multiSelectActionButtons">
                    <td colspan="${ config.columns.size() + 1 }">
                        ${ ui.includeFragment("widget/actionButtons", [actionButtons: config.actionButtons]) }
                    </td>
                </tr>
            <% } %>
        </table>

    <% if (!disabled) { %>
        </form>
     <% } %>
</div>

