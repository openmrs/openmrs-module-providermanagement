
<% ui.includeCss("providermanagement", "providerManagementBanner.css") %>

<div id="banner">
    <table>
        <tr>
            <td align="left">
                <a href="/${ ui.contextPath() }">
                    <img class="logo" src=" ${ ui.resourceLink ("images/openmrs_logo_white_large.png") }"/>
                </a>
            </td>
            <td align="right">
                <span class="loginStringSection">${ ui.message("header.logged.in") } ${ context?.authenticatedUser?.personName }</span> |
                <span class="loginStringSection"><a href='/${ ui.contextPath() }/logout'>${ ui.message("header.logout") } </a></span>
            </td>
        </tr>
    </table>
</div>