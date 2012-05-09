<%@ include file="/WEB-INF/template/include.jsp" %>


<div id="providerRelationshipPortlet">

    <table style="margin: 0px 0px 1em 2em;" cellpadding="3" cellspacing="0" id="providerTable" class="providerTable">
        <thead>
        <tr bgcolor="whitesmoke">
            <td><spring:message code="providermanagement.provider"/></td>
            <td><spring:message code="providermanagement.type"/></td>
            <td><spring:message code="Relationship.startDate"/></td>
            <td><spring:message code="Relationship.endDate"/></td>
        </tr>
        </thead>
        <tbody>
            <c:forEach var="providerRelationship" items="${model.providerRelationships}">
                <tr>
                    <td>${providerRelationship.personA.personName}</td>
                    <td>${providerRelationship.relationshipType.aIsToB}</td>
                    <td><openmrs:formatDate date="${providerRelationship.startDate}"/></td>
                    <td><openmrs:formatDate date="${providerRelationship.endDate}"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</div>
