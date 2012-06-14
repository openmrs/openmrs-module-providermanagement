<% ui.includeCss("providermanagement", "providerEdit.css")
    def id = config.id ?: ui.randomId() %>

<script>
    jq(function() {
        jq('#editProvider_${ id }').submit(function(e) {
            e.preventDefault();
            var form = jq(this);
            var data = form.serialize();

            jq.ajax({
                type: "POST",
                url: "${ ui.actionLink('saveProvider') }",
                data: data,
                dataType: "json"
            })
                    .success(function(data) {
                        location.reload();
                    })
                    .error(function(xhr, status, err) {
                        var errors = jq.parseJSON(xhr.responseText);

                        // first hide and clear the error div
                        jq('#editProvider_${ id }-globalerror').html('').hide();

                        // concatenate the list of messages to display
                        var messages = "";

                        // TODO: confirm that this works right; don't currently have any global errors to test
                        for (globalError in errors.globalErrors) {
                            messages = messages + globalError + "<br/>"
                        }

                        for (key in errors.fieldErrors) {
                            messages = messages + errors.fieldErrors[key] + "<br/>"
                        }

                        // now set the div to the new messages and display
                        jq('#editProvider_${ id }-globalerror').html(messages).show();
                    })

        });
    });
</script>

<div class="content providerEdit">

    <div style="display: none" id="editProvider_${ id }-globalerror" class="error"></div>

    <form id="editProvider_${ id }">
        <input type="hidden" name="personId" value="${person?.id ?: ''}"/>

        <table class="providerHeaderTable">
            <tr class="topBar">
                <td colspan="2">&nbsp;</td>
            </tr>
            <tr>
                <td colspan="2" class="label">${ person?.personName ?: '&nbsp;' } ${ provider?.retired ? '(' + ui.message("general.retired") + ')' : '' }</td>
            </tr>
            <tr>
                <td colspan="2"> ${ provider?.providerRole?.name ?: '' }</td>
            </tr>
        </table>

        <table class="providerInfoTable">

            <!-- include the name fragment -->
            ${ ui.includeFragment("personName", [personName: person?.personName, mode: 'edit', id: id]) }

            <tr>
                <td><span class="label">${ ui.message("providermanagement.providerRole") }</span></td>
                <td> ${ ui.includeFragment("widget/selectList", [ formFieldName: "provider.providerRole",
                        selected: [provider?.providerRole?.id], options: providerRoles, optionsDisplayField: 'name',
                        optionsValueField: 'id', includeEmptyOption: true] ) }</td>
            </tr>

            <tr>
                <th colspan="2">${ ui.message("providermanagement.general") }</th>
            </tr>

            <tr>
                <td><span class="label">${ ui.message("providermanagement.identifier") }:</span></td>
                <td><input type="text" id="provider.identifier" name="provider.identifier" size="20" value="${ provider?.identifier ?: ''}"/></td>
            </tr>

            <tr>
                <td><span class="label">${ ui.message("Person.gender") }:</span></td>
                <td><input name="gender" type="radio" value="M" ${ person?.gender == 'M' ? 'checked' : '' }> ${ ui.message("Person.gender.male") }
                    <input name="gender" type="radio" value="F" ${ person?.gender == 'F' ? 'checked' : '' }> ${ ui.message("Person.gender.female") }
                </td>
            </tr>

            <tr>
                <td><span class="label">${ ui.message("Person.birthdate") }:</span></td>
                <td>${ ui.includeFragment("widget/field", [ class: java.util.Date,
                        formFieldName: "birthdate",
                        initialValue: person?.birthdate ]) }
                </td>
            </tr>

            <% personAttributeTypes?.each { %>
            <tr>
                <td><span class="label">${ it.name }:</span></td>
                <td>
                    ${ ui.includeFragment("widget/field", [ class: it.format,
                        formFieldName: "attributeMap[" + it.name + "].value",
                        initialValue: person?.attributes.find{ attribute -> attribute.attributeType == it }?.hydratedObject ?: null] ) }
                </td>
             </tr>
            <% } %>

            <!-- TODO: NOTE THAT, FOR NOW, THIS ONLY ALLOWS ONE ATTRIBUTE OF EACH TYPE -->
            <!-- TODO: NOTE THAT RIGHT NOW THIS ONLY WORKS FOR STRINGS! -->
            <% provider?.providerRole?.providerAttributeTypes?.each { if (!it.retired) { %>
            <tr>
                <td><span class="label">${ it.name }:</span></td>
                <td>
                    ${ ui.includeFragment("widget/field", [ class: "java.lang.String",
                            formFieldName: "provider.attributeMap['" + it.id + "']",
                            initialValue: provider?.attributes.find{ attribute -> attribute.attributeType == it }?.value ?: null ] ) }
                </td>
            </tr>
            <% } } %>

            <tr>
                <th colspan="2">${ ui.message("Person.address") }</th>
            </tr>

            <!-- include the address fragment -->
            ${ ui.includeFragment(addressWidget, [personAddress: person?.personAddress, mode: 'edit']) }

        </table>


        <% if (config.actionButtons) { %>
        <table class="providerActionButtonsTable">
            <tr>
                <td colspan="2">
                    ${ ui.includeFragment("widget/actionButtons", [actionButtons: config.actionButtons])}
                </td>
            </tr>
        </table>
        <% } %>
    </form>
</div>
