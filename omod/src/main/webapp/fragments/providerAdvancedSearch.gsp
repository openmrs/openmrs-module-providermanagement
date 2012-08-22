
<%  ui.includeCss("providermanagement", "providerAdvancedSearch.css")
    def id = config.id ?: ui.randomId()
    def selectIdParam = config.selectIdParam ?: 'id'%>


<script>
    jq(function() {
        jq('#advancedSearchForm_${ id }').submit(function(e) {
            e.preventDefault();
            var form = jq(this);
            var data = form.serialize();

            jq.ajax({
                type: "POST",
                url: "${ ui.actionLink('getProviders') }",
                data: data,
                dataType: "json"
            })
                    .success(function(data) {

                        jq('#advancedSearchResults_${ id } > tbody > tr').remove();
                        var tbody = jq('#advancedSearchResults_${ id } > tbody');

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
                            jq('#advancedSearchResults_${ id } > tbody > tr').click(function() {
                                window.location = '${ config.selectAction }' + ${ config.selectAction.contains('?') ? '' : '\'?\' + ' }
                                        <% if (config.selectParams) { %>
                                        '&<%= config.selectParams.collect { "${ it.key }=${ it.value }" }.join("&") %>' +
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
                        jq('#advancedSearchResults_${ id } > tbody > tr').mouseover(function() {
                            jq(this).addClass('highlighted');
                        });
                        jq('#advancedSearchResults_${ id } > tbody > tr').mouseout(function() {
                            jq(this).removeClass('highlighted');
                        });
                    })
                    .error(function(xhr, status, err) {
                        alert('search error ' + err);
                    })

        });
    });
</script>

<div class="advancedSearch content">
    <form id="advancedSearchForm_${ id }"/>
        <table id="advancedSearchForm_${ id }">
            <th colspan="2" class="label">
                ${ ui.message("providermanagement.findProviderAdvanced") }
            </th>

            <tr>
                <td>
                    <table>
                        <tr>
                            <td><span class="label">${ ui.message("general.name") }:</span></td>
                            <td><input type="text" id="name" name="name" size="20" value="${ command?.name ?: ''}"/></td>
                        </tr>

                        <tr>
                            <td><span class="label">${ ui.message("providermanagement.identifier") }:</span></td>
                            <td><input type="text" id="identifier" name="identifier" size="20" value="${ command.identifier ?: ''}"/></td>
                        </tr>

                        <tr>
                            <td colspan="2">&nbsp;</td>
                        </tr>

                        <!-- include the address fragment -->
                        ${ ui.includeFragment("providermanagement", addressWidget, [personAddress: command?.personAddress, mode: 'edit']) }

                    </table>
                </td>

                <td>
                    <table>
                        <tr>
                            <td><span class="label">${ ui.message("providermanagement.providerRole") }</span></td>
                            <td> ${ ui.includeFragment("uilibrary", "widget/selectList", [ formFieldName: "providerRole",
                                    selected: [command?.providerRole?.id], options: providerRoles, optionsDisplayField: 'name',
                                    optionsValueField: 'id', includeEmptyOption: true] ) }</td>
                        </tr>

                        <% if (advancedSearchPersonAttributeType) { %>
                        <tr>
                            <td><span class="label">${ advancedSearchPersonAttributeType.name }:</span></td>
                            <td>
                                ${ ui.includeFragment("uilibrary", "widget/field", [ class: advancedSearchPersonAttributeType.format,
                                        formFieldName: "attribute.value", initialValue: command?.attribute?.hydratedObject ] ) }
                            </td>
                        </tr>
                        <% } %>

                        <!-- shoulwd we add these directly to the data object instead of having to apply them here as hidden fields? -->
                        <% if (config.resultFields) { config.resultFields.each { %>
                            <input name="resultFields" type="hidden" value="${ it }"/>
                        <% } } %>

                    </table>
                </td>
            </tr>

            <tr>
                <td colspan="2" align="center">
                    ${ ui.includeFragment("providermanagement", "widget/actionButtons", [actionButtons: [ [type: "submit", id: "searchButton_" + id, label: ui.message("general.search")],
                                                                                    [type: "reset", id: "cancelButton_" + id, label: ui.message("general.cancel")] ]]) }
                </td>
            </tr>

        </table>

        <table id="advancedSearchResults_${ id }">

            <tbody>

            </tbody>

        </table>
    </form>
</div>