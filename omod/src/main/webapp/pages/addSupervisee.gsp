
<% ui.decorateWith("providerManagementPage") %>

<div id="addSupervisee">
    ${ ui.includeFragment("widget/selectProvider", [ person: param.personId,
                                                        actionButtons: [[label: ui.message("general.add")],
                                                                [label: ui.message("general.remove")],
                                                                [label: ui.message("providermanagement.transfer")]] ]) }
</div>
