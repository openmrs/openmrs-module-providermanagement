
<%  ui.includeCss("providermanagement", "widget/ajaxSearch.css")
    def id = config.id ?: ui.randomId()
    def selectIdParam = config.selectIdParam ?: 'id'

    def today = new Date()
    today.clearTime()

    def initialValue = config.initialValue ?: today
%>

<script>
    jq(function() {

        // define the AJAX search function
        var search = function () {

            if (jq('#searchField_${ id }').val()) {
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
                                    var row = '<tr><input type="hidden" value="' + item.id + '"/>';
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
                                    window.location = '${ config.selectAction }' + ${ config.selectAction.contains('?') ? '' : '\'?\' + ' }
                                            <% if (config.selectParams) { %>
                                                '&<%= config.selectParams.collect { "${ it.key }=${ it.value }" }.join("&") %>' +
                                            <% } %>
                                            <% if (config.selectForm) { %>
                                                '&' + jq('#${ config.selectForm }').serialize() +
                                            <% } %>
                                            <% if (config.showDateField) { %>
                                                '&date=' + jq('[name="date_${id}"]').val() +
                                            <% } %>
                                                '&${ selectIdParam }=' + jq(this).children('input').val();
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
                    <% if (config.showDateField) { %>
                        ${config.dateLabel}: ${ ui.includeFragment("widget/field", [ class: java.util.Date,
                                                                                     formFieldName: "date_" + id,
                                                                                     initialValue: initialValue ]) }
                    <% } %>
                    <% if (config.retiredToggle) { %>
                        <input id="includeRetired_${id}" type="checkbox"/> ${ ui.message("providermanagement.includeRetired") }
                    <% } %>
                </td>
            </tr>

            <% if (config.actionButtons) { %>
            <tr>
                <td colspan="${ config.resultFields.size() }">
                    ${ ui.includeFragment("widget/actionButtons", [actionButtons: config.actionButtons]) }
                </td>
            </tr>
            <% } %>

        </thead>

        <tbody>

        </tbody>

    </table>
</div>

