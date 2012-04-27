
${ ui.startForm("saveGlobalProperty", [propertyName: property.property])}
    <select name="values" ${ config.multiple ? 'multiple' : '' }>
        <% config.options.each { %>
            <option value="${ it[config.valueField] }"
                ${ values?.contains(it[config.valueField]) ? 'selected' : ''}
            >${ it[config.keyField] }</option>
        <% } %>
    </select>
    <input type="submit"/>
${ ui.endForm() }



