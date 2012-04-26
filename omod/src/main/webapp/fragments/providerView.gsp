
<% ui.includeCss("providermanagement", "providerView.css") %>

<div class="content providerView">

    <table class="providerHeader">
        <tr>
            <td class="label">${ person.personName }</td>
        </tr>
        <tr>
            <td> ${ provider.providerRole?.name ?: '' }</td>
        </tr>
    </table>

    <table class="providerInfo">
        <tr>
            <th>${ ui.message("providermanagement.general") }</th>
        </tr>

        <tr>
            <td><span class="label">${ ui.message("providermanagement.identifier") }:</span> ${ provider.identifier }</td>
        </tr>

        <tr>
            <td><span class="label">${ ui.message("Person.gender") }:</span> ${ person.gender == 'M' ? ui.message("Person.gender.male") : ui.message("Person.gender.female") }</td>
        </tr>

        <tr>
            <td><span class="label">${ ui.message("Person.birthdate") }:</span> ${ ui.format(person.birthdate) }</td>
        </tr>

        <% provider.providerRole?.providerAttributeTypes?.each { %>
        <tr>
            <td><span class="label">${ it.name }:</span> ${ provider.attributes.find{ attribute -> attribute.attributeType == it }?.value ?: '' }</td>
        </tr>
        <% } %>

        <tr>
            <th>${ ui.message("Person.address") }</th>
        </tr>

        <!-- include the address fragment -->
        ${ ui.includeFragment("personAddress", [personAddress: person.personAddress, mode: 'view']) }

    </table>


    <% if (config.actionButtons) { %>
        <table class="providerActionButtons">
            <tr>
                <td>
                    <% config.actionButtons.each { %>
                        <a href="${ it.link }"><button>${ it.label }</button></a>
                    <% } %>
                </td>
            </tr>
        </table>
    <% } %>

</div>
