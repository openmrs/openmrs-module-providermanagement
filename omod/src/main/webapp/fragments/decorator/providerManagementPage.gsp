${ ui.includeFragment("uilibrary", "standardIncludes") }

<% ui.includeCss("providermanagement", "providerManagement.css") %>
<% ui.includeJavascript("jquery.js") %>
<% ui.includeJavascript("jquery-ui.js") %>


<!DOCTYPE html>
<html>
    <head>
        <title>${ "OpenMRS" }</title>
        <link rel="shortcut icon" type="image/ico" href="/${ ui.contextPath() }/images/openmrs-favicon.ico"/>
        <link rel="icon" type="image/png\" href="/${ ui.contextPath() }/images/openmrs-favicon.png"/>
        ${ ui.resourceLinks() }
    </head>
    <body>

        <script>
            var jq = jQuery;
        </script>


        <div id="providerHeader">
            <!-- banner -->
            ${ ui.includeFragment("providermanagement", "providerManagementBanner") }

            <!-- include the menu -->
            ${ ui.includeFragment("providermanagement","providerManagementMenu") }
        </div>

        <!--  show any generic error messages -->
        ${ ui.includeFragment("uilibrary", "flashMessage") }

        <div id="content">
            ${ config.content }
        </div>
    </body>
</html>

