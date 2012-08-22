<%  ui.includeCss("providermanagement", "widget/ajaxSearch.css")
    def id = config.id ?: ui.randomId()
    def selectIdParam = config.selectIdParam ?: 'id'
    def minSearchCharacters = context.getAdministrationService().getGlobalProperty("minSearchCharacters") ?: 3
%>

<script>
    jq(function() {

        // define the AJAX search function
        var search = function () {

            // first, hide the submit button and clear out the hidden field each time we do a search
            <% if (config.submitButtonId) { %>
                jq('#${ config.submitButtonId }').hide();
            <% } %>

            jq('#searchValue_${ id }').val('');

            if (jq('#searchField_${ id }').val() && jq('#searchField_${ id }').val().length >= ${ minSearchCharacters }) {
                jq.getJSON('${ config.searchAction }',
                        { 'returnFormat': 'json',
                            'searchValue': jq('#searchField_${ id }').val(),
                            <% if (config.retiredToggle) { %>
                            'includeRetired': jq('#includeRetired_${ id }').is(':checked') ? true : false,
                            <% } %>
                            'resultFields': [ <%= config.resultFields.collect { "'${ it }'" }.join(",") %> ]
                            <% config.searchParams.each { %>
                            , '${ it.key }': ${ it.value }
                            <% } %>
                        })
                        .success(function(data) {

                            jq('#searchTable_${ id } > tbody > tr').remove();
                            var tbody = jq('#searchTable_${ id } > tbody');

                            // first, add the header row
                            var headerRow = '<tr>';
                            <% config.resultFieldLabels.each { %>
                            headerRow += '<th> ${ it }</th>';
                            <% } %>
                            headerRow += '</tr>';
                            tbody.append(headerRow);

                            if (data && data.length > 0) {
                                for (index in data) {
                                    var item = data[index];
                                    var row = '<tr class="resultRow"><input class="id" type="hidden" value="' + item.id + '"/>';

                                    // we store all the fields we want to display in the input field when an
                                    // item is selected in an hidden field
                                    <% if (config.selectDisplayFields) { %>
                                        row += '<input class="selectDisplayFields" type="hidden" value="';
                                        <% config.selectDisplayFields.each { %>
                                            row += ((item
                                                    <% it.split('\\.').each { field -> %>
                                                    ['${field}']
                                                    <% } %>
                                                    != undefined) ? item
                                                    <% it.split('\\.').each { field -> %>
                                                    ['${field}']
                                                    <% } %>
                                                    + ' ' : '');
                                        <% } %>
                                        row += '"/>'
                                    <% } %>

                                    // display the actual result fields
                                    <% config.resultFields.each { %>
                                        row += '<td>' + ((item
                                                <% it.split('\\.').each { field -> %>
                                                ['${field}']
                                                <% } %>
                                                != undefined) ? item
                                                <% it.split('\\.').each { field -> %>
                                                ['${field}']
                                                <% } %>
                                                : '') + '</td>';
                                    <% } %>
                                    row += '</tr>';
                                    tbody.append(row);
                                }

                                // configure the action that occurs on a row click
                                jq('#searchTable_${ id } > tbody > tr').click(function() {

                                    // if there is a select action, perform that action
                                    <% if (config.selectAction) { %>
                                        window.location = '${ config.selectAction }' + ${ config.selectAction.contains('?') ? '' : '\'?\' + ' }
                                                          '&${ selectIdParam }=' + jq(this).children('.id').val();
                                    <% } else { %>
                                    // otherwise, by default, a select should populate the input box with the display fields of the specified item
                                        jq('#searchField_${ id }').val(jq(this).children('.selectDisplayFields').val());
                                        jq('#searchValue_${ id }').val(jq(this).children('.id').val());

                                        // show the submit button, if it exists
                                        <% if (config.submitButtonId) { %>
                                            jq('#${ config.submitButtonId }').show();
                                        <% } %>

                                        // clear out the search table
                                        jq('#searchTable_${ id } > tbody > tr').remove();
                                    <% }%>
                                });
                            }
                                    <% if (config.emptyMessage) { %>
                            else {
                                tbody.append('<tr><td>${ config.emptyMessage }</td></tr>');
                            }
                            <% } %>

                            // configure highlighting
                            jq('#searchTable_${ id } > tbody > tr').mouseover(function() {
                                jq(this).addClass('highlighted');
                            });
                            jq('#searchTable_${ id } > tbody > tr').mouseout(function() {
                                jq(this).removeClass('highlighted');
                            });
                        })
                        .error(function(xhr, status, err) {
                            alert('search error ' + err);
                        })
            }
            else {
                // remove the results if no text in the search field
                jq('#searchTable_${ id } > tbody > tr').remove();
            }
        };

        // trigger the search on key up in the search field, or a change in the retired toggle
        jq('#searchField_${ id }').keyup(search);

        <% if (config.retiredToggle) { %>
            jq('#includeRetired_${ id }').change(search);
        <% } %>


        // handle what happens when you click on the submit button
        <% if (config.submitButtonId) { %>
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
                                        '&${ config.submitIdParam }=' + jq('#searchValue_${ id }').val();
                }
                else {
                    alert(errorMessage);
                }
            });
        <% } %>

        jq(document).ready(function(){
            <% if (config.submitButtonId) { %>
                // hide the submit button to start out
                jq('#${ config.submitButtonId }').hide();
            <% } %>
        });


    });
</script>

<div class="content ajaxSearch">
    <table id="searchTable_${ id }" class="searchTable">
        <thead>
            <tr>
                <th colspan="${ config.resultFields.size() }">${ config.title }</th>
            </tr>

            <tr>
                <td colspan="${ config.resultFields.size() }">
                    <input id="searchField_${ id }" class="searchField" type="text" size="40"/>
                    <input id="searchValue_${ id }" type="hidden"/>

                    <% if (config.retiredToggle) { %>
                        <input id="includeRetired_${id}" type="checkbox"/> ${ ui.message("providermanagement.includeRetired") }
                    <% } %>

                     <% config.fields.each { %>
                        ${ it.label }: ${ ui.includeFragment("uilibrary", "widget/field", [ class: it.class, formFieldName: it.name + "_" +  id, initialValue: it.initialValue, maxDate: it.maxDate ?: null, minDate: it.minDate ?: null ]) }
                     <% } %>

                     <% if (config.actionButtons) { %>
                        ${ ui.includeFragment("providermanagement", "widget/actionButtons", [actionButtons: config.actionButtons]) }
                     <% } %>
                </td>
            </tr>
        </thead>

        <tbody>

        </tbody>

    </table>
</div>

