<select name="${ config.field }" ${ config.multiple ? 'multiple' : '' }>
    <% config.options.each { %>
    <option value="${ it[config.optionsValueField] }"
        ${ config.values?.contains(it[config.optionsValueField]) ? 'selected' : ''}
    >${ it[config.optionsKeyField] }</option>
    <% } %>
</select>

