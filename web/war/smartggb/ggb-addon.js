var materialTemplate = '<table class="materialtable" border="0" width="100%">' +
'<tr><td class="thumbnail_cell"><a class="thumbnail" style="background-image: url(\'%THUMBNAIL%\');display:block;width:80px;height:80px;"></a></td>' +
'<td align="left" class="info"><div class="title">' +
            '<a href="http://www.geogebratube.org/material/show/id/%ID%?lang=en" target="_blank">%TITLE%</a>' +
        '</div>%DATE%<br />' +
'By <a href="%AUTHOR_URL%?lang=en" target="_blank">%AUTHOR%</a></td>' +
'<td class="matrightcell" align="right" valign="top"><ul id="material-buttons"></ul></td>' +
    '</tr></table>' +

'<div id="advanced-popup-%ID%" class="inline-popup" style="display:none;"></div>';


function updateHeight() {
    $("#modal-body").height($(window).height() - 220);
}
// This function is run when the app is ready to start interacting with the host application
// It ensures the DOM is ready before adding click handlers to buttons

    $(document).ready(function () {
        // If setSelectedDataAsync method is supported by the host application
        // the UI buttons are hooked up to call the method else the buttons are removed

      
            clickHandler();
            searchResults(false);			
			updateHeight();
            
			window.onresize = function(event) {
				updateHeight();
			}
    });

var material_id = 0;

function setSelectedMaterial(id, width, height) {
    if($("#material_"+material_id)){
        $("#material_" + material_id).removeClass("selected");
    }
    $("#material_" + id).addClass("selected");
	if(id == material_id){
		writeContent();
	}
    material_id = id;
}

function formatDate(d){
    var cdate = d.getDate();
    var cmonth = d.getMonth() + 1; //Months are zero based
    var cyear = d.getFullYear();
    return(cdate + ". " + cmonth + ". " + cyear);
}

function displayMaterialNumber(useSearch) {
    var tubeRequest = new XMLHttpRequest();
    var tubeResponse;
    
    tubeRequest.open('POST', 'http://www.geogebratube.org/api/json.php', false);
    var data = "{\"request\":{\"-api\":\"1.0.0\", \"task\":{\"-type\":\"count\"}}}";
    tubeRequest.setRequestHeader("Content-type","text/plain");
    tubeRequest.send(data);
    var materials = "70,000+";
    if (tubeRequest.status === 200) {
        try{
            tubeResponse = JSON.parse(tubeRequest.responseText);
			materials = tubeResponse.responses.response["-value"];
        } catch (e) {
			
        }
        
        
    } else {
       // $("#debug").html(data);
    }
	$("#welcome span").text($("#welcome span").text().replace(/\{\$1\}/,materials));
}

function searchResults(useSearch) {
    var tubeRequest = new XMLHttpRequest();
    var tubeResponse;
    
    tubeRequest.open('POST', 'http://www.geogebratube.org/api/json.php', false);
    var filter = "{\"field\":[{\"-name\":\"type\",\"#text\":\"ggb\"},{\"-name\":\"featured\",\"#text\":\"true\"}]}";
    var sortBy = "likes";
    if (useSearch) {
        //JSON.stringify adds quotes itself
        filter = "{\"field\":[{\"-name\":\"type\",\"#text\":\"ggb\"},{\"-name\":\"search\",\"#text\":"+JSON.stringify($("#term").val())+"}]}";
        sortBy = "relevance";
    }
    
    //var filter = "{\"field\":[{\"-name\":\"type\",\"#text\":\"ggb\"},{\"-name\":\"search\",\"#text\":\"" + this.query + "\"}]}";
    var data = "{\"request\":{\"-api\":\"1.0.0\", \"task\":{\"-type\":\"fetch\", \"fields\":{\"field\":[{\"-name\":\"id\"},{\"-name\":\"title\"},{\"-name\":\"type\"},{\"-name\":\"author\"},{\"-name\":\"timestamp\"},{\"-name\":\"author_url\"},{\"-name\":\"url\"},{\"-name\":\"url_direct\"},{\"-name\":\"language\"},{\"-name\":\"thumbnail\"},{\"-name\":\"featured\"},{\"-name\":\"likes\"},{\"-name\":\"width\"},{\"-name\":\"height\"}]}, \"filters\":"
            + filter + ", \"order\":{\"-by\":\"" + sortBy + "\", \"-type\":\"desc\"}, \"limit\":{\"-num\":\"30\"}}}}";
    tubeRequest.setRequestHeader("Content-type","text/plain");
    tubeRequest.send(data);
    
    if (tubeRequest.status === 200) {
        try{
            tubeResponse = JSON.parse(tubeRequest.responseText);
        } catch (e) {

        }
        
        $("#resultul").empty();
        if (!tubeResponse.responses.response.item) {
            //$("#debug").html("Nothing");
            return;
        }
        
        var materials = tubeResponse.responses.response.item;
        if (!(materials instanceof Array)) {
            materials = new Array(materials);
        }
        if (materials[0]) {
            for (var i = 0; i < materials.length ; i++) {
                var current = materials[i];
                if (current.width == '') {
                    current.width = 800;
                }
                if (current.height == '') {
                    current.height = 600;
                }
                var materialLi = $('<li class="material_box" id="material_' + current.id + '"onclick="setSelectedMaterial(' + current.id + ',' + current.width + ',' + current.height + ')">' + materialTemplate
                    .replace(/%ID%/g, current.id)
                    .replace(/%TITLE%/g, current.title)
                    .replace(/%AUTHOR%/g, current.author)
                    .replace(/%AUTHOR_URL%/g, current.author_url)
                    .replace(/%DATE%/g, formatDate(new Date(1000 * current.timestamp)))
                    .replace(/%THUMBNAIL%/g, materials[i].thumbnail) + '</li>');
                $("#resultul").append(materialLi);
            }
        }
    } else {
       // $("#debug").html(data);
    }
}

var observer;
function addWidget(ggbData){
	var widget = new NB.objectPrototype.file("widget/index.html");
	//var widget = new NB.objectPrototype.file("smartggb.galleryitem");

	widget.x = 0; 
	widget.y = 20;
	
	if(observer){
		NB.removeObserver(observer);
	}

	observer = NB.addObserver('ggbWidgetReady',function(e){
		var evt = {};
		evt[e.eventData.widgetId] = ggbData;
		NB.postEvent("setBase64", evt);
		console.log(JSON.stringify(evt));
	});
	NB.addObject(widget);
}

function writeContent() {

	var tubeRequest = new XMLHttpRequest();
	
	 tubeRequest.open('POST', 'http://www.geogebratube.org/api/json.php', false);
	    
    //JSON.stringify adds quotes itself
	filter = "{\"field\":[{\"-name\":\"id\",\"#text\":\""+material_id+"\"}]}";

	    
	    //var filter = "{\"field\":[{\"-name\":\"type\",\"#text\":\"ggb\"},{\"-name\":\"search\",\"#text\":\"" + this.query + "\"}]}";
	var data = "{\"request\":{\"-api\":\"1.0.0\", \"task\":{\"-type\":\"fetch\", \"fields\":{\"field\":[{\"-name\":\"ggbBase64\"},{\"-name\":\"width\"},{\"-name\":\"height\"},{\"-name\":\"toolbar\"},{\"-name\":\"menubar\"},{\"-name\":\"inputbar\"}]}, \"filters\":"
	            + filter + ", \"order\":{\"-by\":\"likes\", \"-type\":\"desc\"}, \"limit\":{\"-num\":\"1\"}}}}";
	tubeRequest.setRequestHeader("Content-type","text/plain");
	console.log(data);
	tubeRequest.send(data);	
	if(tubeRequest.status === 200){
		var tubeResponse = JSON.parse(tubeRequest.responseText);
		if(tubeResponse.responses && tubeResponse.responses.response && tubeResponse.responses.response.item){
			addWidget(tubeResponse.responses.response.item);
		}
	}
	

}

function clickHandler() {



    
    $('#submitLink').click(function () { writeContent(); return false; });
       
    
}