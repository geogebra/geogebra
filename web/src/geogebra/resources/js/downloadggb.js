(function(obj) {

	var requestFileSystem = obj.webkitRequestFileSystem ||
	 obj.mozRequestFileSystem || obj.requestFileSystem;

	
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

	}

    function createTempGGBFile(callback) {
        var tmpFilename = "geogebra.ggb";
        requestFileSystem(TEMPORARY, 4 * 1024 * 1024 * 1024, function(filesystem) {
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
        });
    }

    var model = (function() {
        var zipFileEntry, zipWriter, writer; //, creationMethod, URL = obj.webkitURL || obj.mozURL || obj.URL;

        return {
                createZipWriter : function createZipWriter() {
                    zip.createWriter(writer, function(writer) {
                        zipWriter = writer;
                    }, onerror);
                },
                
                addGGBFiles: function addGGBFiles(){
                	//TODO: add the files (geogebra.xml, etc.) to the zip 
                }
        };
    })();
 

    
    obj.downloadggb = {
    	setDownloadButton : function(button){
    		downloadButton = button;
    	},
    	downloadGGBfunction : function(event) {
    		console.log("downloadGGBfunction");
    		//requestFileSystem(window.TEMPORARY, 1024*1024, createXML, errorHandler);
    		createTempGGBFile(function(fileEntry) {
                zipFileEntry = fileEntry;
                writer = new zip.FileWriter(zipFileEntry);
                //createZipWriter();
                //addGGBFiles();
            });
    		
    		

    	}
    }
})(this);
