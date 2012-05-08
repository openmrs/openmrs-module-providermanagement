
${ ui.startForm("getProviders") }

    <table>
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

${ ui.endForm() }