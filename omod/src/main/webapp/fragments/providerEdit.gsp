<% ui.includeCss("providermanagement", "providerEdit.css") %>

<div class="content providerEdit">

    <table class="providerHeader">

        <!-- include the name fragment -->
        ${ ui.includeFragment("personName", [personName: person.personName, mode: 'edit']) }

        <tr>
            <td colspan="2"> ${ provider.providerRole?.name ?: '' }</td>
        </tr>
    </table>

    <table class="providerInfo">
        <tr>
            <th colspan="2">${ ui.message("providermanagement.general") }</th>
        </tr>

        <tr>
            <td><span class="label">${ ui.message("providermanagement.identifier") }:</span></td>
            <td><input type="text" id="identifier" name="identifier" size="20" value="${ provider.identifier ?: ''}"/></td>
        </tr>

        <tr>
            <td><span class="label">${ ui.message("Person.gender") }:</span></td>
            <td>${ person.gender == 'M' ? ui.message("Person.gender.male") : ui.message("Person.gender.female") }</td>
        </tr>

        <tr>
            <td><span class="label">${ ui.message("Person.birthdate") }:</span></td>
            <td>${ ui.format(person.birthdate) }</td>
        </tr>

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

</div>
