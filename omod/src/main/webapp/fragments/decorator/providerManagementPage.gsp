${ ui.includeFragment("standardIncludes") }

<% ui.includeCss("providermanagement", "providerManagement.css") %>
<% ui.includeJavascript("jquery.js") %>
<% ui.includeJavascript("jquery-ui.js") %>

<script>
    var jq = jQuery;
</script>


<div id="providerHeader">
    <!-- banner -->
    ${ ui.includeFragment("providerManagementBanner") }

    <!-- include the menu -->
    ${ ui.includeFragment("providerManagementMenu") }
</div>

<!--  show any generic error messages -->
${ ui.includeFragment("flashMessage") }

<div id="content">
    ${ config.content }
</div>