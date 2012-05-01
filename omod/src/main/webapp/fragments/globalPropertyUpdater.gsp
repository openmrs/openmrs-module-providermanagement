
${ ui.startForm("saveGlobalProperty", [propertyName: property.property])}
    <% if (config.options) { %>
    ${ ui.includeFragment("widget/selectList", [ formFieldName: "values",
            selected: values, options: config.options, optionsDisplayField: config.optionsKey,
            optionsValueField: config.optionsValue, multiple: config.multiple] ) }
    <% } else { %>
        <textarea name="value" rows="5" cols="30">${ value ?: ''}</textarea>
    <% } %>
    <input type="submit"/>
${ ui.endForm() }



