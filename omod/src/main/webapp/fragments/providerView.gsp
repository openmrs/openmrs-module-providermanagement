
<% ui.includeCss("providermanagement", "providerView.css") %>

<div class="content providerView">

    <table class="providerHeaderTable">
        <tr class="topBar">
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td colspan="2" class="label">${ person.personName } ${ provider.retired ? '(' + ui.message("general.retired") + ')' : '' }</td>
        </tr>
        <tr>
            <td colspan="2"> ${ provider.providerRole?.name ?: '' }</td>
        </tr>
    </table>

    <table class="providerInfoTable">
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
            <td>${ ui.format(person.birthdate) } ${ person?.birthdateEstimated ? '(' + ui.message('Person.birthdateEstimated') + ')' : ''}</td>
        </tr>

        <!-- display person attribute types: assumption is only one attribute per person per type -->
        <% personAttributeTypes?.each { %>
        <tr>
            <td><span class="label">${ it.name }:</span></td>
            <td>${ ui.format(person.attributes.find{ attribute -> attribute.attributeType == it }?.hydratedObject ?: '') }</td>
        </tr>
        <% } %>

        <!-- display provider attribute types: assumption is only one attribute per provider per type -->
        <% provider.providerRole?.providerAttributeTypes?.each {  if (!it.retired) {%>
        <tr>
            <td><span class="label">${ it.name }:</span></td>
            <td>${ provider.attributes.find{ attribute -> attribute.attributeType == it }?.value ?: '' }</td>
        </tr>
        <% } } %>

        <tr>
            <th colspan="2">${ ui.message("Person.address") }</th>
        </tr>

        <!-- include the address fragment -->
        ${ ui.includeFragment("providermanagement", "personAddress", [personAddress: person.personAddress, mode: 'view']) }

    </table>


    <% if (config.actionButtons) { %>
        <table class="providerActionButtonsTable">
            <tr>
                <td colspan="2">
                    ${ ui.includeFragment("providermanagement", "widget/actionButtons", [actionButtons: config.actionButtons]) }
                </td>
            </tr>
        </table>
    <% } %>

</div>
