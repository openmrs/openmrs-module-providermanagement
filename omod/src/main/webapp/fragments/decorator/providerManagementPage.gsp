${ ui.includeFragment("uilibrary", "standardIncludes") }

<% ui.includeCss("providermanagement", "providerManagement.css") %>
<% ui.includeJavascript("jquery.js") %>
<% ui.includeJavascript("jquery-ui.js") %>

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