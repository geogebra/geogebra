(function(obj) {

	var requestFileSystem = obj.webkitRequestFileSystem ||
	 obj.mozRequestFileSystem || obj.requestFileSystem;
	
	var zipWriter;
	var downloadButton;

	
	function errorHandler(e) {
		var msg = '';

		switch (e.code) {
		case FileError.QUOTA_EXCEEDED_ERR:
			msg = 'QUOTA_EXCEEDED_ERR';
			break;
		case FileError.NOT_FOUND_ERR:
			msg = 'NOT_FOUND_ERR';
			break;
		case FileError.SECURITY_ERR:
			msg = 'SECURITY_ERR';
			break;
		case FileError.INVALID_MODIFICATION_ERR:
			msg = 'INVALID_MODIFICATION_ERR';
			break;
		case FileError.INVALID_STATE_ERR:
			msg = 'INVALID_STATE_ERR';
			break;
		default:
			msg = 'Unknown Error';
			break;
		};

		console.log('Error: ' + msg);
	}

	function createXML(fs) {

		fs.root.getFile('geogebra.xml', {
			create : true
		}, function(fileEntry) {

			// Create a FileWriter object for our FileEntry (geogebra.xml).
			fileEntry.createWriter(function(fileWriter) {

				fileWriter.onwriteend = function(e) {
					console.log('Write completed.');
				};

				fileWriter.onerror = function(e) {
					console.log('Write failed: ' + e.toString());
				};

				// Create a new Blob and write it to geogebra.xml.
				var blob = new Blob([ 'xml...' ], {
					type : 'text/plain'
				});

				fileWriter.write(blob);

			}, errorHandler);

		}, errorHandler);

		zipWriter.add('geogebra.xml', new zip.BlobReader('geogebra.xml'), null, null);
	}

    function createTempGGBFile(callback) {
        var tmpFilename = "geogebra.ggb";
        requestFileSystem(window.TEMPORARY, 4 * 1024 * 1024 * 1024, function(filesystem) {
            function create() {
                filesystem.root.getFile(tmpFilename, {
                    create : true
                }, function(zipFile) {
                    callback(zipFile);
                });
            }

            filesystem.root.getFile(tmpFilename, null, function(entry) {
                entry.remove(create, create);
            }, create);
        }, errorHandler);
        console.log("createTempGGBFile end");
    }


    function createZipWriter(writer) {
                 zip.createWriter(writer, function(writer) {
                    zipWriter = writer;
                    addGGBFiles();
                    setDownloadButton();
                }, onerror);
    }
            
    function addGGBFiles(){
    	console.log("addGGBFiles()");
        //TODO: add the files (geogebra.xml, etc.) to the zip 
        //requestFileSystem(window.TEMPORARY, 1024*1024, createXML, errorHandler);
    }
    
    function setDownloadButton(){
    	downloadButton.download = "geogebra.ggb";
        zipWriter.close(function(blob) {
//        	var clickEvent = document.createEvent("MouseEvent");
//			clickEvent.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
			var blobURL = this.zipFileEntry.toURL();
			zipWriter = null;
			
			console.log("Todo: download ggb");
			downloadButton.setAttribute("ggburl", blobURL);
			downloadButton.disabled = false;

		});
    	
    }
    
obj.downloadggb = {
		zipFileEntry : null,
		writer : null,
		
    	setDownloadButton : function(button){
    		downloadButton = button;
    	},
    	downloadGGBfunction : function() {
    		console.log("downloadGGBfunction");
    		createTempGGBFile(function(fileEntry) {
                this.zipFileEntry = fileEntry;
                this.writer = new zip.FileWriter(this.zipFileEntry);
                createZipWriter(writer);
            });
    	}
    }
})(this);
