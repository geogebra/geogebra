(function(obj) {
	
	var downloadButton;
	
	obj.tempjs = {	
			
		   	setDownloadButton : function(button){
	    		downloadButton = button;
	    	},
			
			tempCallback : function(ggbZip){
				
				ggburl = ggbZip.toURL();
				
				downloadButton.download = "geogebra.ggb";
				downloadButton.setAttribute("ggburl", ggburl);
				downloadButton.disabled = false;

       
			}
	}
	
	
})(this);
