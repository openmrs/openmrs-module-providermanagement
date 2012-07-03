<% ui.includeCss("providermanagement", "providerAdd.css")
def id = config.id ?: ui.randomId() %>

<script>
    jq(function() {
        jq('#editProvider_${ id }').submit(function(e) {
            e.preventDefault();
            var form = jq(this);
            var data = form.serialize();

            jq.ajax({
                type: "POST",
                url: "${ ui.actionLink('addProvider') }",
                data: data,
                dataType: "json"
            })
                    .success(function(data) {
                        location.reload();
                    })
                    .error(function(xhr, status, err) {
                        var errors = jq.parseJSON(xhr.responseText);

                        // first hide and clear the error div
                        jq('#addProvider_${ id }-globalerror').html('').hide();

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
                        jq('#addProvider_${ id }-globalerror').html(messages).show();
                    })

        });
    });
</script>

<div class="content providerAdd">

    <div style="display: none" id="addProvider_${ id }-globalerror" class="error"></div>

    <form id="addProvider_${ id }">

        <table class="providerNameTable">

            <thead>
                <tr>
                    <th colspan="2">${ ui.message("providermanagement.addNewProvider") }</th>
                </tr>
            </thead>

            <!-- include the name fragment -->
            ${ ui.includeFragment("personName", [mode: 'edit']) }

        </table>

        <table class="providerActionButtonsTable">
            <tr>
                <td>
                    ${ ui.includeFragment("widget/actionButtons", [ actionButtons: [[label: ui.message("providermanagement.addProvider"),
                                                                     type: 'Submit']] ] ) }
                </td>
            </tr>
        </table>

    </form>
</div>

