
<% def id = config.id ?: ui.randomId() %>


<script>
    jq(function() {
        jq('#saveGlobalProperty_${ id }').submit(function(e) {
            e.preventDefault();
            var form = jq(this);
            var data = form.serialize();

            jq.ajax({
                type: "POST",
                url: "${ ui.actionLink('saveGlobalProperty') }",
                data: data,
                dataType: "json"
            })
                    .success(function(data) {
                        alert('${ ui.message("providermanagement.settingUpdated") }');
                    })
                    .error(function(xhr, status, err) {
                        alert('${ ui.message("providermanagement.settingUpdateError") }');
                    })

        });
    });
</script>

<form id="saveGlobalProperty_${ id }" name="saveGlobalProperty_${ id }">

    <input type="hidden" name="propertyName" value="${ property.property }"/>

    <% if (config.options) { %>
    ${ ui.includeFragment("uilibrary", "widget/selectList", [ formFieldName: "values",
            selected: values, options: config.options, optionsDisplayField: config.optionsKey,
            optionsValueField: config.optionsValue, multiple: config.multiple] ) }
    <% } else { %>
        <input name="value" value="${ value ?: ''}" size="200"/>
    <% } %>
    <button type="submit">${ ui.message("providermanagement.update") }</button>

</form>



