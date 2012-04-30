
<% ui.includeCss("providermanagement", "providerView.css") %>

<div class="content providerView">

    <table class="providerHeader">
        <tr>
            <td colspan="2" class="label">${ person.personName }</td>
        </tr>
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
            <td>${ provider.identifier ?: '' }</td>
        </tr>

        <tr>
            <td><span class="label">${ ui.message("Person.gender") }:</span></td>
            <td>${ person.gender == 'M' ? ui.message("Person.gender.male") : ui.message("Person.gender.female") }</td>
        </tr>

        <tr>
            <td><span class="label">${ ui.message("Person.birthdate") }:</span></td>
            <td>${ ui.format(person.birthdate) }</td>
        </tr>

        <!-- display person attribute types: assumption is only one attribute per person per type -->
        <% personAttributeTypes?.each { %>
        <tr>
            <td><span class="label">${ it.name }:</span></td>
            <td>${ person.attributes.find{ attribute -> attribute.attributeType == it }?.value ?: '' }</td>
        </tr>
        <% } %>

        <!-- display provider attribute types: assumption is only one attribute per provider per type -->
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
        ${ ui.includeFragment("personAddress", [personAddress: person.personAddress, mode: 'view']) }

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
