
${ ui.startForm("saveGlobalProperty", [propertyName: property.property])}
    <% if (config.options) { %>
    ${ ui.includeFragment("widget/selectList", [ field: "values",
            values: values, options: config.options, optionsKeyField: config.optionsKey,
            optionsValueField: config.optionsValue, multiple: config.multiple] ) }
    <% } else { %>
        <textarea name="value" rows="5" cols="30">${ value ?: ''}</textarea>
    <% } %>
    <input type="submit"/>
${ ui.endForm() }



