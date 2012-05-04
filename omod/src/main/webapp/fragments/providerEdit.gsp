<% ui.includeCss("providermanagement", "providerEdit.css") %>

<div class="content providerEdit">


    ${ ui.startForm("saveProvider", [personId: person.id]) }
        <table class="providerHeader">

            <!-- include the name fragment -->
            ${ ui.includeFragment("personName", [personName: person.personName, mode: 'edit']) }

            <tr>
                <td><span class="label">${ ui.message("providermanagement.providerRole") }</span></td>
                <td> ${ ui.includeFragment("widget/selectList", [ formFieldName: "provider.providerRole",
                        selected: [provider.providerRole.id], options: providerRoles, optionsDisplayField: 'name',
                        optionsValueField: 'id'] ) }</td>
            </tr>
        </table>

        <table class="providerInfo">
            <tr>
                <th colspan="2">${ ui.message("providermanagement.general") }</th>
            </tr>

            <tr>
                <td><span class="label">${ ui.message("providermanagement.identifier") }:</span></td>
                <td><input type="text" id="provider.identifier" name="provider.identifier" size="20" value="${ provider.identifier ?: ''}"/></td>
            </tr>

            <tr>
                <td><span class="label">${ ui.message("Person.gender") }:</span></td>
                <td><input name="gender" type="radio" value="M" ${ person.gender == 'M' ? 'checked' : '' }> ${ ui.message("Person.gender.male") }
                    <input name="gender" type="radio" value="F" ${ person.gender == 'F' ? 'checked' : '' }> ${ ui.message("Person.gender.female") }</td>
            </tr>


            <!-- TODO: add widget that allows specifying birthdate via age? -->
            <tr>
                <td><span class="label">${ ui.message("Person.birthdate") }:</span></td>
                <td>${ ui.includeFragment("widget/field", [ class: java.util.Date,
                        formFieldName: "birthdate",
                        initialValue: person.birthdate ]) }</td>
            </tr>

            <% personAttributeTypes?.each { %>
            <tr>
                <td><span class="label">${ it.name }:</span></td>
                <td>
                    ${ ui.includeFragment("widget/field", [ class: it.format, includeEmptyOption: true,
                        formFieldName: "attributeMap[" + it.name + "].value",
                        initialValue: person.attributes.find{ attribute -> attribute.attributeType == it }?.hydratedObject ?: null] ) }
                </td>
             </tr>
            <% } %>

            <!-- TODO: assumption here is that there is only one attribute of any type? -->
            <% provider.providerRole?.providerAttributeTypes?.each { %>
            <tr>
                <td><span class="label">${ it.name }:</span></td>
                <td>${ provider.attributes.find{ attribute -> attribute.attributeType == it }?.value ?: '' }</td>
            </tr>
            <% } %>

            <tr>
                <th colspan="2">${ ui.message("Person.address") }</th>
            </tr>

            <!-- include the address fragment -->
            ${ ui.includeFragment("personAddress", [personAddress: person.personAddress, mode: 'edit']) }

        </table>


        <% if (config.actionButtons) { %>
        <table class="providerActionButtons">
            <tr>
                <td colspan="2">
                    ${ ui.includeFragment("widget/actionButtons", [actionButtons: config.actionButtons])}
                </td>
            </tr>
        </table>
        <% } %>
    ${ ui.endForm() }
</div>
