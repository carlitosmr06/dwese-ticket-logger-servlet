<%@ include file="header.jsp" %>
    <%-- Modo edición si la provincia ya tiene id; si no, es una alta nueva --%>
    <c:set var="isEdit" value="${not empty province and province.id > 0}" />

    <h1>
        <c:choose>
            <c:when test="${isEdit}">
                <fmt:message key="msg.province-form.edit" />
            </c:when>
            <c:otherwise>
                <fmt:message key="msg.province-form.add" />
            </c:otherwise>
        </c:choose>
    </h1>

    <%-- Mostrar el mensaje de validación (internacionalizado) si existe --%>
    <c:if test="${not empty errorKey}">
        <div class="error-message" style="color: red;"><fmt:message key="${errorKey}" /></div>
    </c:if>

    <form action="provinces" method="post">
        <input type="hidden" name="id" value="${isEdit ? province.id : ''}" />
        <input type="hidden" name="action" value="${isEdit ? 'update' : 'insert'}" />

        <label for="code"><fmt:message key="msg.province-form.code" />:</label>
        <input type="text" name="code" id="code" maxlength="10"
               value="<c:out value='${province.code}' />" />

        <label for="name"><fmt:message key="msg.province-form.name" />:</label>
        <input type="text" name="name" id="name" maxlength="100"
               value="<c:out value='${province.name}' />" />

        <label for="id_region"><fmt:message key="msg.province-form.region" />:</label>
        <select name="id_region" id="id_region">
            <option value=""><fmt:message key="msg.province-form.select-region" /></option>
            <c:forEach var="region" items="${listRegions}">
                <option value="${region.id}" ${province.region.id == region.id ? 'selected' : ''}>
                    <c:out value="${region.name}" />
                </option>
            </c:forEach>
        </select>

        <!-- Cuando una cadena de caracteres incluye a otra esta debe llevar comillas simples -->
        <c:choose>
            <c:when test="${isEdit}">
                <input type="submit" value="<fmt:message key='msg.province-form.update' />" />
            </c:when>
            <c:otherwise>
                <input type="submit" value="<fmt:message key='msg.province-form.create' />" />
            </c:otherwise>
        </c:choose>
    </form>
    <a href="provinces"><fmt:message key="msg.province-form.returnback" /></a>
<%@ include file="footer.jsp" %>
