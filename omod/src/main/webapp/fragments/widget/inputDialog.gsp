
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
                    <% config.formFields.each { %>
                        '&${ it.name }=' + jq('[name="${ it.name }_${ id }"]').val() +
                    <% } %>
                    '';
        });
    });
</script>


<div class="content inputDialog">
    <table>
        <thead>
            <tr>
                <th>${ config.title }</th>
            </tr>

        </thead>

        <tbody>

            <% config.formFields.each { %>
                <tr>
                    <td>
                        ${ it.label }: ${ ui.includeFragment("widget/field", [ class: it.class, formFieldName: it.name + "_" +  id, initialValue: it.initialValue ]) }
                    </td>
                </tr>
            <% } %>

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


