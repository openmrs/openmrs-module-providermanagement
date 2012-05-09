
<%  def id = config.id ?: ui.randomId() %>


<script>
    jq(function() {
        jq('#advancedSearch').submit(function(e) {
            e.preventDefault();
            var form = jq(this);
            var data = form.serialize();
            jq.ajax({
                type: "POST",
                url: form.attr('action'),
                data: data,
                dataType: "json"
            })
                    .success(function(data) {

                        jq('#advancedSearchTable_${ id } > tbody > tr').remove();
                        var tbody = jq('#searchTable_${ id } > tbody');
                        for (index in data) {
                            var item = data[index];
                            var row = '<tr><input type="hidden" value="' + item.id + '"/>';
                        <% config.resultFields.each { %>
                            row += '<td>' + ((item.${ it } != undefined) ? item.${ it } : '') + '</td>';
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
                                    '&${ selectIdParam }=' + jq(this).children('input').val();
                        });

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

        });
    });
</script>

<form id="advancedSearch" action="${ ui.actionLink('getProviders') }"/>
    <table id="advancedSearchTable_${ id }">
        <thead>
        <th colspan="2" class="label">
            ${ ui.message("providermanagement.findProviderAdvanced") }
        </th>
        </thead>

        <tbody>

        <tr>
            <td>
                <table>
                    <tr>
                        <td><span class="label">${ ui.message("general.name") }:</span></td>
                        <td><input type="text" id="name" name="name" size="20" value="${ command?.name ?: ''}"/></td>
                    </tr>

                    <tr>
                        <td><span class="label">${ ui.message("providermanagement.identifier") }:</span></td>
                        <td><input type="text" id="identifier" name="provider.identifier" size="20" value="${ command.identifier ?: ''}"/></td>
                    </tr>

                    <tr>
                        <td colspan="2">&nbsp;</td>
                    </tr>

                    <!-- include the address fragment -->
                    ${ ui.includeFragment("personAddress", [personAddress: command?.personAddress, mode: 'edit']) }

                </table>
            </td>

            <td>
                <table>
                    <tr>
                        <td><span class="label">${ ui.message("providermanagement.providerRole") }</span></td>
                        <td> ${ ui.includeFragment("widget/selectList", [ formFieldName: "provider.providerRole",
                                selected: [command?.providerRole?.id], options: providerRoles, optionsDisplayField: 'name',
                                optionsValueField: 'id'] ) }</td>
                    </tr>

                    <% if (advancedSearchPersonAttributeType) { %>
                    <tr>
                        <td><span class="label">${ advancedSearchPersonAttributeType.name }:</span></td>
                        <td>
                            ${ ui.includeFragment("widget/field", [ class: advancedSearchPersonAttributeType.format,
                                    formFieldName: "attribute.value", initialValue: command?.attribute?.hydratedObject ] ) }
                        </td>
                    </tr>
                    <% } %>

                </table>
            </td>
        </tr>

        <tr>
            <td colspan="2">
                ${ ui.includeFragment("widget/actionButtons", [actionButtons: [ [type: "submit", label: ui.message("general.search")] ]]) }
            </td>
        </tr>

        </tbody>

    </table>
</form>