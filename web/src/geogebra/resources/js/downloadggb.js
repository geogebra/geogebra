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

	function createXML(fs, zipWriter2) {

		fs = obj.downloadggb.getFileSystem();
		fs.root.getFile('geogebra.xml', {
			create : true
		}, function(fileEntry) {

			// Create a FileWriter object for our FileEntry (geogebra.xml).
			fileEntry.createWriter(function(fileWriter) {

				fileWriter.onwriteend = function(e) {
					console.log('Write completed.');
					obj.downloadggb.zipWriter.add('geogebra.xml', new zip.BlobReader('geogebra.xml'), function(){setDownloadButton();}, function(){});
					
				};

				fileWriter.onerror = function(e) {
					console.log('Write failed: ' + e.toString());
				};

				// Create a new Blob and write it to geogebra.xml.
				var blob = new Blob([ '<xml></xml>' ], {
					type : 'text/plain'
				});

				console.log("write comes");
				fileWriter.write(blob);

			}, errorHandler);

		}, errorHandler);
		
		
	}

    function createTempGGBFile(callback) {
        var tmpFilename = "geogebra.ggb";
        requestFileSystem(window.TEMPORARY, 4 * 1024 * 1024 * 1024, function(filesystem) {
            function create() {
                filesystem.root.getFile(tmpFilename, {
                    create : true
                }, function(zipFile) {
                	obj.downloadggb.setFileSystem(filesystem);
                    callback(zipFile, filesystem);
                });
            }

            filesystem.root.getFile(tmpFilename, null, function(entry) {
                entry.remove(create, create);
            }, create);
        }, errorHandler);
        console.log("createTempGGBFile end");
    }


    function createZipWriter(writer) {
                 zip.createWriter(writer, function(writer,filesystem) {
                    zipWriter = writer;
                    obj.downloadggb.zipWriter = zipWriter;
                    addGGBFiles(filesystem);
                }, onerror);
    }
            
    function addGGBFiles(filesystem){
        //TODO: add the files (geogebra.xml, etc.) to the zip 
        //requestFileSystem(window.TEMPORARY, 1024*1024, createXML, errorHandler);
    	createXML(filesystem);
    }
    
    function setDownloadButton(){
    	downloadButton.download = "geogebra.ggb";
        zipWriter.close(function(blob) {
        	zfEntry = obj.downloadggb.getZipFileEntry();
        	var blobURL = zfEntry.toURL();
			zipWriter = null;
			
			console.log("Todo: download ggb");
			downloadButton.setAttribute("ggburl", blobURL);
			downloadButton.disabled = false;

		});
    	
    }
    
obj.downloadggb = {
		zipFileEntry : null,
		writer : null,
		fileSystem : null,
		zipWriter : null,
		
               
		setZipFileEntry : function(fe){
			console.log("setZipFileEntry");
			zipFileEntry = fe;
		},

		getZipFileEntry : function(){
			console.log("getZipFileEntry");
			return zipFileEntry;
		},
		
		getWriter : function (){
			console.log(writer.valueOf());
			return writer;
		},
		
		setFileSystem : function(filesys){
			fileSystem = filesys;
		},

		getFileSystem : function(){
			return fileSystem;
		},

    	setDownloadButton : function(button){
    		downloadButton = button;
    	},
    	downloadGGBfunction : function() {
    		console.log("downloadGGBfunction");
    		obj.zip.workerScriptsPath = "web/js/zipjs/"; 
    		createTempGGBFile(function(fileEntry,filesystem) {
                zipFileEntry = fileEntry;
                obj.downloadggb.setZipFileEntry(fileEntry);
                this.writer = new zip.FileWriter(this.zipFileEntry);
                this.downloadggb.getWriter();
                createZipWriter(writer,filesystem);
            });
    	}
    }
})(this);
