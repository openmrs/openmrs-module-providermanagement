
<% ui.decorateWith("providerManagementPage") %>

<% ui.includeCss("providermanagement", "providerHome.css") %>

<div id="providerSearch">

    <%= ui.includeFragment("widget/ajaxSearch", [title: ui.message("providermanagement.findProvider"),
            searchAction: ui.actionLink("providerSearch", "getProviders"),
            resultFields: ["personName"],
            selectIdParam: "personId",
            selectAction: ui.pageLink('providerDashboard') ]) %>

</div>
