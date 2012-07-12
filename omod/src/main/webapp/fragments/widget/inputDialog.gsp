
<% def id = config.id ?: ui.randomId() %>


<script>
    jq(function() {

        // configure the action that when the submit button is clicked
        jq('#${ config.submitButtonId }').click(function() {

            window.location = '${ config.submitAction }' + ${ config.submitAction.contains('?') ? '' : '\'?\' + ' }
                    <% if (config.submitParams) { %>
                    '&<%= config.submitParams.collect { "${ it.key }=${ it.value }" }.join("&") %>' +
                    <% } %>
                    <% if (config.submitForm) { %>
                    '&' + jq('#${ config.submitForm }').serialize() +
                    <% } %>
                    '&${ config.formFieldName }=' + jq('[name="${ config.formFieldName }_${ id }"]').val();
        });
    });
</script>


<div class="content dateDialog">
    <table>
        <thead>
            <tr>
                <th>${ config.title }</th>
            </tr>

        </thead>

        <tbody>
            <tr>
                <td>
                    ${ config.dateLabel }: ${ ui.includeFragment("widget/field", [ class: config.class, formFieldName: config.formFieldName + "_" +  id ]) }
                </td>
            </tr>

            <% if (config.actionButtons) { %>
                <tr>
                    <td>
                        ${ ui.includeFragment("widget/actionButtons", [ actionButtons: config.actionButtons ]) }
                    </td>
                </tr>
            <% } %>
        </tbody>
    </table>
</div>


