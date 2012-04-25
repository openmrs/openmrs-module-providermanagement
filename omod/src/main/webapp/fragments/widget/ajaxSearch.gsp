
<%  ui.includeCss("providermanagement", "widget/ajaxSearch.css")
    def id = config.id ?: ui.randomId()  %>

<!-- TODO: will need to tweak this so that it doesn't reload on every keystroke? -->
<!-- TODO: add support for searching by identifier -->

<script>
    jq(function() {
        jq('#searchField_${ id }').keyup(function() {
            if (jq(this).val()) {
                jq.getJSON('${ config.searchAction }',
                        { 'returnFormat': 'json',
                            'searchValue': jq(this).val(),
                            'resultFields': [ <%= config.resultFields.collect { "'${ it }'" }.join(",") %> ]
                            <% config.params.each { %>
                                , '${ it.key }': ${ it.value }
                            <% } %>
                        })
                        .success(function(data) {
                            jq('#searchTable_${ id } > tbody > tr').remove();
                            var tbody = jq('#searchTable_${ id } > tbody');
                            for (index in data) {
                                var item = data[index];
                                var row = '<tr>';
                            <% config.resultFields.each { %>
                                row += '<td>' + item.${ it } + '</td>';
                            <% } %>
                                row += '</tr>';
                                tbody.append(row);
                            }
                        })
                        .error(function(xhr, status, err) {
                            alert('search error ' + err);
                        })
            }
            else {
                // remove the results if no text in the search field
                jq('#searchTable_${ id } > tbody > tr').remove();
            }
        });
    });
</script>

<div class="content ajaxSearch">
    <table id="searchTable_${ id }">
        <thead>
            <tr>
                <th colspan="${ config.resultFields.size }">${ config.title }</th>
            </tr>

            <tr>
                <td colspan="${ config.resultFields.size }"><input id="searchField_${ id }" type="text" size="40"/></td>
            </tr>
        </thead>

        <tbody>

        </tbody>

    </table>
</div>

