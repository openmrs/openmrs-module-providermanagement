
<%  ui.includeCss("providermanagement", "widget/multiSelectCheckboxTable.css")
    def id = config.id ?: ui.randomId()
    def selectIdParam = config.selectIdParam ?: 'id'
    def disabled = config.disabled ?: false
%>

<script>
    jq(function() {

        // checks how many checkboxes are currently checked
        // if more than one are checked, disable any buttons flagged as "disableOnMultiSelect"
        // and hide any objects passed to the disableOnMultiSelect parameter
        var handleMultiSelect = function () {

            if(jq('.checkbox_${ id }:checked').length > 1) {
                // set up the action buttons we want to disable
                <% config.actionButtons.each  {
                    if (it.disableOnMultiSelect) { %>
                        jq('#${ it.id }').hide();
                <%  }
                 } %>

                // hide any objects passed to the disableOnMultiSelect parameter
                <% config.disableOnMultiSelect.each { %>
                    jq('#${ it }').hide();
                <% } %>

            }
            else {
                // set up the action buttons we want to enable
                <% config.actionButtons.each  {
                    if (it.disableOnMultiSelect) { %>
                        jq('#${ it.id }').show();
                <%  }
                } %>

                // show any objects passed to the disableOnMultiSelect parameter
                <% config.disableOnMultiSelect.each { %>
                    jq('#${ it }').show();
                <% } %>
            }

        }

        // configure highlighting
        jq('#multiSelectCheckboxTable_${ id } > tbody > tr').mouseover(function() {
            jq(this).addClass('highlighted');
        });
        jq('#multiSelectCheckboxTable_${ id } > tbody > tr').mouseout(function() {
            if (!jq(this).find('.checkbox_${ id }').attr('checked')) {
                jq(this).removeClass('highlighted');
            }
        });

        // configure what happens when a checkbox is checked
        jq('.checkbox_${ id }').click(function () {
            // handle highlighting
            if (jq(this).attr('checked')) {
                jq(this).closest('tr').addClass('highlighted');
            }
            else {
                jq(this).closest('tr').removeClass('highlighted');
            }
            // toggle any buttons and hide any divs as needed
            handleMultiSelect();
        });

        // handle the select all function
        jq('#selectAll_${ id }').click(function() {
           if(jq(this).attr('checked')) {
               jq('.checkbox_${ id }').attr('checked', true);
               jq('#multiSelectCheckboxTable_${ id } > tbody > tr').addClass('highlighted');
           }
           else {
               jq('.checkbox_${ id }').attr('checked', false);
               jq('#multiSelectCheckboxTable_${ id } > tbody > tr').removeClass('highlighted');
           }
            // toggle any buttons and hide any divs as needed
            handleMultiSelect();
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
                        ${ ui.includeFragment("providermanagement", "widget/actionButtons", [actionButtons: config.actionButtons]) }
                    </td>
                </tr>
            <% } %>
        </table>

    <% if (!disabled) { %>
        </form>
     <% } %>
</div>

