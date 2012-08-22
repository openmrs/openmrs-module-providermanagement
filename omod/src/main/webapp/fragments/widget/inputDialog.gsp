
<% def id = config.id ?: ui.randomId() %>


<script>
    jq(function() {

        // configure the action that when the submit button is clicked
        jq('#${ config.submitButtonId }').click(function() {

            // perform validation
            var valid = true;
            var errorMessage = '';

            <%  if (config.submitForm?.required) { %>
                if(!jq('#${ config.submitForm.name }').serialize()) {
                    errorMessage = errorMessage.concat("${ config.submitForm.requiredErrorMessage }\\n");
                    valid = false;
                }
            <% } %>

            <% config.fields.each {
                if (it.required)  { %>
                       if (!jq('[name="${ it.name }_${ id }"]').val()) {
                           errorMessage = errorMessage.concat("${ ui.message('providermanagement.errors.isRequired', it.label) }\\n");
                           valid = false;
                       }
                <% } %>
            <% } %>

            // do the actual submit
            if (valid) {
                window.location = '${ config.submitAction }' + ${ config.submitAction.contains('?') ? '' : '\'?\' + ' }
                        <% if (config.submitParams) { %>
                            '&<%= config.submitParams.collect { "${ it.key }=${ it.value }" }.join("&") %>' +
                        <% } %>
                        <% if (config.submitForm) { %>
                            '&' + jq('#${ config.submitForm.name }').serialize() +
                        <% } %>
                        <% config.fields.each { %>
                            '&${ it.name }=' + jq('[name="${ it.name }_${ id }"]').val() +
                        <% } %>
                        '';
            }
            else {
                alert(errorMessage);
            }
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
            <tr>
                <td>
                    <% config.fields.each { %>
                        ${ it.label }: ${ ui.includeFragment("uilibrary", "widget/field", [ class: it.class, formFieldName: it.name + "_" +  id, initialValue: it.initialValue, maxDate: it.maxDate ?: null, minDate: it.minDate ?: null ]) }
                    <% } %>

                    <% if (config.actionButtons) { %>
                        ${ ui.includeFragment("providermanagement", "widget/actionButtons", [ actionButtons: config.actionButtons ]) }
                    <% } %>
                </td>
            </tr>
        </tbody>
    </table>
</div>


