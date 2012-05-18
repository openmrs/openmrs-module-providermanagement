${ ui.includeFragment("standardIncludes") }

<% ui.includeCss("providermanagement", "providerManagement.css") %>
<% ui.includeJavascript("jquery.js") %>

<script>
    var jq = jQuery;
</script>


<div id="providerHeader">
    <!-- banner -->
    ${ ui.includeFragment("providerManagementBanner") }

    <!-- include the menu -->
    ${ ui.includeFragment("providerManagementMenu") }
</div>

<div id="content">
    ${ config.content }
</div>