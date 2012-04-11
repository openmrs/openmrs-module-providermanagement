<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu">
	<li class="first"><a
		href="${pageContext.request.contextPath}/admin"><spring:message
				code="admin.title.short" /></a></li>

	<li>
		<a href="${pageContext.request.contextPath}/module/providermanagement/manageProviderRoles.form">
            <spring:message code="providermanagement.manageProviderRoles" /></a>
	</li>

    <li>
    <a
            href="${pageContext.request.contextPath}/module/providermanagement/manageSuggestions.form"><spring:message
            code="providermanagement.manageSuggestions" /></a>
    </li>

</ul>
<h2>
	<spring:message code="providermanagement.title" />
</h2>
