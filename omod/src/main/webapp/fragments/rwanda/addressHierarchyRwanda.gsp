
<% def addressIndex = config.addressIndex ?: '1'
    def displayStructured = config.displayStructured ?: false %>

<script>

    function getPatientAddress(patientId) {
        jq.getJSON("/${ ui.contextPath() }/module/addresshierarchyrwanda/patientAddress.form", {
            patientId :patientId
        }, function(data) {
            var count = -1;
            jq.each(data.addresses, function(i, address) {
                count++;
                jq("#cnt_" + count).val(address.country);
                jq("#sp_" + count).val(address.stateProvince);
                jq("#cd_" + count).val(address.countyDistrict);
                jq("#cv_" + count).val(address.cityVillage);
                jq("#nc_" + count).val(address.neighborhoodCell);
                jq("#a1_" + count).val(address.address1);
                jq("#structured_" + count).text(address.structured);
            });

        });
    }

    //this is a hack for google chrome, for which :hidden selector doesn't work...
    function isVisible(a){
        //this block is a hack for google chrome.
        for (var i = 0 ; i < jq(a).parents().size(); i++){
            var x = jq(a).parents()[i];
            //if (jq(x).hasClass("tabBox") || ( jq(x).attr("id", "addressPortlet") && jq(x).is("div")) ){
            var style = new String(jq(x).attr("style"));
            if (style.indexOf("display") > -1 && style.indexOf("none") > -1){
                return false;
            }
            //}
        }
        return true;
    }

    function changeLocation(parentLocationId, nextElement) {
        var locIdString = "" + parentLocationId;
        var parsedParentLocationId = locIdString.substring(3);
        jq.getJSON("/${ ui.contextPath() }/module/addresshierarchyrwanda/locations.form", {
            locationId :parsedParentLocationId
        }, function(data) {
            var options = "<option value='--'>--</option>";
            jq.each(data.addresses, function(i, address) {
                options += "<option value='ah_" + address.id  + "' id = '" + address.id +"'>"+address.display+"</option>";
            });
            jq(nextElement).append(options);

            if (jq(nextElement).attr('class') != undefined){
                if (getSiblingWithinValueFromClass(nextElement,jq(nextElement).attr('class').replace("Class","SaveClass"))){

                    //set selected value
                    var textBoxValue = getSiblingWithinValueFromClass(jq(nextElement),jq(nextElement).attr('class').replace("Class","SaveClass"));
                    textBoxValue = new String(textBoxValue);
                    var op = jq(nextElement).children('option').filter(function() {
                        if ((jq(this).text().trim() + "") == textBoxValue && jq(this).val() != "--" && isVisible(this)) {  //hidden works in firefox, fails in chrome && !jq(this).is(":hidden")
                            return this;
                        }
                    });
                    jq(op[0]).attr("selected", "selected");
                    var list = jq(nextElement).find("option:selected").filter(function(){
                        if (jq(this).val() != '--' && isVisible(this) ) return this;  //same chrome problem
                    });
                    if (jq(list).text() == getSiblingWithinValueFromClass(jq(nextElement),jq(nextElement).attr('class').replace("Class","SaveClass"))){
                        var nextSelect = findNextSelect(nextElement);
                        if (nextSelect != null)
                            setTimeout(function() {changeLocation(jq(nextElement).val(),jq(nextSelect))}, 10);
                    }
                }
            }
        });
    }

    function findNextSelect(nextElement){
        var inputs = jq(nextElement).closest("table:visible").find(':input:visible');
        var winningPos = null;
        for (var i = 0; i < inputs.length - 2; i++){
            if (jq(inputs[i]).attr("name") == jq(nextElement).attr("name")){
                winningPos = i + 2;
                break;
            }
        }
        if (winningPos != null)
            return jq(inputs[winningPos]);
    }


    function validateSingleAddress(someElement,country, province, district, sector, cell, umudugudu, targetElement){
        //alert(country + province + district + sector + cell + umudugudu);
        jq.getJSON("/${ ui.contextPath() }/module/addresshierarchyrwanda/ahValidateAddress.form", {
            country:country, province:province, district:district, sector:sector, cell:cell, umudugudu:umudugudu
        },  function (json){
            jq.each(json.values, function(i, value) {
                if(value.value == 1){
                    jq(someElement).empty();
                    jq(someElement).append(jq(document.createElement("img")).attr("src","/${ ui.contextPath() }/images/checkmark.png"));
                } else {
                    jq(someElement).empty();
                    jq(someElement).append(jq(document.createElement("img")).attr("src","/${ ui.contextPath() }/images/error.gif"));
                }
            });
        });
    }

    function updateStructuredIcon(data){
        alert(data);
    }


    function validateAddressesOnPage(){

        var structuredElements = jq(".isstructured");
        var country, province, district, sector, cell, umudugudu;

        for(var i = 0; i < structuredElements.length; i++){

            country = getSiblingWithinValueFromClass(structuredElements[i],"countrySaveClass");
            province = getSiblingWithinValueFromClass(structuredElements[i],"provinceSaveClass");
            district = getSiblingWithinValueFromClass(structuredElements[i],"districtSaveClass");
            sector = getSiblingWithinValueFromClass(structuredElements[i],"sectorSaveClass");
            cell = getSiblingWithinValueFromClass(structuredElements[i],"cellSaveClass");
            umudugudu = getSiblingWithinValueFromClass(structuredElements[i],"address1SaveClass");

            if (country, province, district, sector, cell, umudugudu)
                validateSingleAddress(structuredElements[i],country, province, district, sector, cell, umudugudu, null);

        }
    }

    function getSiblingWithinValueFromClass(someElement,className){

        var td = jq(someElement).closest("table:visible")
                .children("tbody")
                .children("tr")
                .children("td");
        //alert("returning " + jq(td).children("."+className).val());
        return jq(td).children("."+className).val() != undefined ? jq(td).children("."+className).val().trim() : undefined;
    }

    function getSiblingWithinTableFromClass(someElement,className){

        var td = jq(someElement).closest("table:visible")
                .children("tbody:visible")
                .children("tr:visible")
                .children("td:visible");
        return jq(td).children("."+className);
    }

    function validateAddressOnChange(targetElement){

        var structuredElement = getSiblingWithinTableFromClass(targetElement,"isstructured");

        var country = getSiblingWithinValueFromClass(structuredElement,"countrySaveClass");
        var province = getSiblingWithinValueFromClass(structuredElement,"provinceSaveClass");
        var district = getSiblingWithinValueFromClass(structuredElement,"districtSaveClass");
        var sector = getSiblingWithinValueFromClass(structuredElement,"sectorSaveClass");
        var cell = getSiblingWithinValueFromClass(structuredElement,"cellSaveClass");
        var umudugudu = getSiblingWithinValueFromClass(structuredElement,"address1SaveClass");

        validateSingleAddress(structuredElement, country, province, district, sector, cell, umudugudu, targetElement);
    }

    jq(document).ready(

            function() {

                jq(".countryClass").live(
                        "change",
                        function() {
                            if (jq(this).val() != "--") {
                                jq(".provinceClass:visible").children().remove();
                                jq(".districtClass:visible").children().remove();
                                jq(".sectorClass:visible").children().remove();
                                jq(".cellClass:visible").children().remove();
                                jq(".address1Class:visible").children().remove();
                                changeLocation(jq(this).val(),
                                        jq(".provinceClass:visible"));
                                jq(".countrySaveClass:visible").val(
                                        jq(this).children(":selected").text());
                            }
                            validateAddressOnChange(this);
                        });

                jq(".provinceClass").live(
                        "change",
                        function() {
                            if (jq(this).val() != "--") {
                                jq(".districtClass:visible ").children().remove();
                                jq(".sectorClass:visible ").children().remove();
                                jq(".cellClass:visible").children().remove();
                                jq(".address1Class:visible").children().remove();
                                changeLocation(jq(this).val(),
                                        jq(".districtClass:visible"));
                                jq(".provinceSaveClass:visible").val(
                                        jq(this).children(":selected").text());
                            }
                            validateAddressOnChange(this);
                        });

                jq(".districtClass").live(
                        "change",
                        function() {
                            if (jq(this).val() != "--") {
                                jq(".sectorClass:visible").children().remove();
                                jq(".cellClass:visible").children().remove();
                                jq(".address1Class:visible").children().remove();
                                changeLocation(jq(this).val(),
                                        jq(".sectorClass:visible"));
                                jq(".districtSaveClass:visible").val(
                                        jq(this).children(":selected").text());
                            }
                            validateAddressOnChange(this)
                        });

                jq(".sectorClass").live(
                        "change",
                        function() {
                            if (jq(this).val() != "--") {
                                jq(".cellClass:visible").children().remove();
                                jq(".address1Class:visible").children().remove();
                                changeLocation(jq(this).val(),
                                        jq(".cellClass:visible"));
                                jq(".sectorSaveClass:visible").val(
                                        jq(this).children(":selected").text());
                            }
                            validateAddressOnChange(this)
                        });

                jq(".cellClass").live(
                        "change",
                        function() {
                            if (jq(this).val() != "--") {
                                jq(".address1Class:visible").children().remove();
                                changeLocation(jq(this).val(),
                                        jq(".address1Class:visible"));
                                jq(".cellSaveClass:visible").val(
                                        jq(this).children(":selected").text());
                            }
                            validateAddressOnChange(this)
                        });

                jq(".address1Class").live(
                        "change",
                        function() {

                            if (jq(this).val() != "--") {
                                jq(".address1SaveClass:visible").val(
                                        jq(this).children(":selected").text());
                            }
                            validateAddressOnChange(this);
                        });




                // ===================== handlers for changes in the text box ================== //
                jq(".address1SaveClass").live(
                        "keyup",
                        function() {
                            validateAddressOnChange(this);
                        });

                jq(".cellSaveClass").live(
                        "keyup",
                        function() {
                            validateAddressOnChange(this);
                        });

                jq(".sectorSaveClass").live(
                        "keyup",
                        function() {
                            validateAddressOnChange(this);
                        });
                jq(".districtSaveClass").live(
                        "keyup",
                        function() {
                            validateAddressOnChange(this);
                        });

                jq(".provinceSaveClass").live(
                        "keyup",
                        function() {
                            validateAddressOnChange(this);
                        });
                jq(".countrySaveClass").live(
                        "keyup",
                        function() {
                            validateAddressOnChange(this);
                        });


                changeLocation(-1, jq(".countryClass"));
                validateAddressesOnPage();

                jq(".voided").live(
                        "change",
                        function() {
                            var voidedReasonRow = jq(this).closest("table")
                                    .siblings("table").children("tbody").children(
                                    ".voidedReasonRowClass");
                            if (voidedReasonRow.css("display") == "none") {
                                voidedReasonRow.show("fast");
                            } else {
                                voidedReasonRow.hide("fast");
                            }
                        });


            });
</script>


<!-- nest the rwanda address hierarchy widgets in a table within this table -->
<tr>
    <td colspan="2">  <!-- TODO: this should be handled better, not a hardcoded colspan? -->

        <table class="tableClass">
            <tbody>
            <tr>
                <td>Country</td>
                <td>
                    <input type="text" name="personAddress.country"
                           value="${ config.personAddress && config.personAddress['country'] ? config.personAddress['country']  : '' }" id="cnt_${addressIndex}"
                           class="countrySaveClass" />
                </td>
                <td><select name="countryselect" class="countryClass">
                </select></td>
            </tr>
            <tr>
                <td>Province</td>
                <td>
                    <input type="text" name="personAddress.stateProvince"
                           value="${ config.personAddress && config.personAddress['stateProvince'] ? config.personAddress['stateProvince']  : '' }" id="sp_${addressIndex}"
                           class="provinceSaveClass" />
                </td>
                <td><select name="stateProvinceselect" class="provinceClass" /></td>
            </tr>
            <tr>
                <td>District</td>
                <td>
                    <input type="text" name="personAddress.countyDistrict"
                           value="${ config.personAddress && config.personAddress['countyDistrict'] ? config.personAddress['countyDistrict']  : '' }" id="cd_${addressIndex}"
                           class="districtSaveClass" />
                </td>
                <td><select name="countryDistrictselect" class="districtClass" /></td>
            </tr>
            <tr>
                <td>Sector</td>
                <td>
                    <input type="text" name="personAddress.cityVillage"
                           value="${ config.personAddress && config.personAddress['cityVillage'] ? config.personAddress['cityVillage']  : '' }" class="sectorSaveClass" />
                </td>
                <td><select name="cityVillageselect" class="sectorClass" /></td>
            </tr>
            <tr>
                <td>Cell</td>
                    <td><input type="text" name="personAddress.neighborhoodCell"
                               value="${ config.personAddress && config.personAddress['neighborhoodCell'] ? config.personAddress['neighborhoodCell']  : '' }" id="nc_${addressIndex}"
                               class="cellSaveClass" /></td>
                <td><select name="neighborhoodCellselect" class="cellClass" /></td>
            </tr>
            <tr>
                <td>Umudugudu</td>
                <td>
                    <input type="text" name="personAddress.address1"
                           value="${ config.personAddress && config.personAddress['address1'] ? config.personAddress['address1']  : '' }" class="address1SaveClass" />
                </td>
                <td><select name="address1select" class="address1Class" /></td>
            </tr>
            <tr ${ !displayStructured ? 'style="display:none"' : '' }>
                <td>Structured:</td>
                <td><span class="isstructured" id="structured_${addressIndex}">--</span></td>
                <td />
            </tr>
            </tbody>
        </table>
    </td>
</tr>

