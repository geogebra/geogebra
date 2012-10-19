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

	function onInitFs(fs) {

		fs.root.getFile('geogebra.xml', {
			create : true
		}, function(fileEntry) {

			// Create a FileWriter object for our FileEntry (log.txt).
			fileEntry.createWriter(function(fileWriter) {

				fileWriter.onwriteend = function(e) {
					console.log('Write completed.');
				};

				fileWriter.onerror = function(e) {
					console.log('Write failed: ' + e.toString());
				};

				// Create a new Blob and write it to log.txt.
				var blob = new Blob([ 'xml...' ], {
					type : 'text/plain'
				});

				fileWriter.write(blob);

			}, errorHandler);

		}, errorHandler);

	}

//	requestFileSystem(window.TEMPORARY, 10 * 1024 * 1024, onInitFs,
//			errorHandler);

		
		
	
// var requestFileSystem = obj.webkitRequestFileSystem ||
// obj.mozRequestFileSystem || obj.requestFileSystem;
//
//    function onerror(message) {
//        alert(message);
//    }
//
    function createTempGGBFile(callback) {
        var tmpFilename = "geogebra.ggb";
//        window.requestFileSystem(TEMPORARY, 4 * 1024 * 1024 * 1024, function(filesystem) {
//            function create() {
//                filesystem.root.getFile(tmpFilename, {
//                    create : true
//                }, function(zipFile) {
//                    callback(zipFile);
//                });
//            }
//
//            filesystem.root.getFile(tmpFilename, null, function(entry) {
//                entry.remove(create, create);
//            }, create);
//        });
    }
//
    var model = (function() {
//        var zipFileEntry, zipWriter, writer, creationMethod, URL = obj.webkitURL || obj.mozURL || obj.URL;
//
        return {
//            setCreationMethod : function(method) {
//                creationMethod = method;
//            },
            addFiles : function addFiles(files, oninit, onadd, onprogress, onend) {
                var addIndex = 0;

                function nextFile() {
                    var file = files[addIndex];
                    onadd(file);
                    zipWriter.add(file.name, new zip.BlobReader(file), function() {
                        addIndex++;
                        if (addIndex < files.length)
                            nextFile();
                        else
                            onend();
                    }, onprogress);
                }

                function createZipWriter() {
                    zip.createWriter(writer, function(writer) {
                        zipWriter = writer;
                        oninit();
                        nextFile();
                    }, onerror);
                }

                if (zipWriter){
                    nextFile();
//                } else if (creationMethod == "Blob") {
//                    writer = new zip.BlobWriter();
//                    createZipWriter();
                } else {
                    createTempGGBFile(function(fileEntry) {
                        zipFileEntry = fileEntry;
                        writer = new zip.FileWriter(zipFileEntry);
                        createZipWriter();
                    });
                }
            },
            getBlobURL : function(callback) {
                zipWriter.close(function(blob) {
//                    var blobURL = creationMethod == "Blob" ? URL.createObjectURL(blob) : zipFileEntry.toURL();
					var blobURL = zipFileEntry.toURL();            
                    callback(blobURL, function() {
//                        if (creationMethod == "Blob")
//                            URL.revokeObjectURL(blobURL);
                    });
                    zipWriter = null;
                });
            }
        };
    })();

    function downloadGGBfile2() {
//        fileInput.addEventListener('change', function() {
//            fileInput.disabled = true;
//            creationMethodInput.disabled = true;
//            model.addFiles(fileInput.files, function() {
//            }, function(file) {
//                var li = document.createElement("li");
//                zipProgress.value = 0;
//                zipProgress.max = 0;
//                li.textContent = file.name;
//                li.appendChild(zipProgress);
//                fileList.appendChild(li);
//            }, function(current, total) {
//                zipProgress.value = current;
//                zipProgress.max = total;
//            }, function() {
//                if (zipProgress.parentNode)
//                    zipProgress.parentNode.removeChild(zipProgress);
//                fileInput.value = "";
//                fileInput.disabled = false;
//            });
//        }, false);
//        creationMethodInput.addEventListener('change', function() {
//            model.setCreationMethod(creationMethodInput.value);
//        }, false);
    	
    	downloadButton = document.getElementById("downloadButton");
        downloadButton.addEventListener("click", function(event) {
            var target = event.target, entry;
            if (!downloadButton.download) {
                model.getBlobURL(function(blobURL, revokeBlobURL) {
                    var clickEvent = document.createEvent("MouseEvent");
                    clickEvent.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
                    downloadButton.href = blobURL;
                    downloadButton.download = filenameInput.value;
                    downloadButton.dispatchEvent(clickEvent);
                    creationMethodInput.disabled = false;
                    fileList.innerHTML = "";
                    setTimeout(revokeBlobURL, 1);
                });
                event.preventDefault();
                return false;
            }
        }, false);

    }
    
    obj.downloadggb = {
    		tempfunction: function(){
    			window.alert("aaa");
    		},
    downloadGGBfunction : function(event) {
//        var target = event.target, entry;
//        if (!downloadButton.download) {
//        	console.log("downloadggb.js - downloadGGBfile - if start");
//            model.getBlobURL(function(blobURL, revokeBlobURL) {
//            	console.log("downloadggb.js - downloadGGBfile - model.getBlobURL start");
//                var clickEvent = document.createEvent("MouseEvent");
//                clickEvent.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
//                downloadButton.href = blobURL;
//                downloadButton.download = filenameInput.value;
//                downloadButton.dispatchEvent(clickEvent);
//                creationMethodInput.disabled = false;
//                fileList.innerHTML = "";
//                setTimeout(revokeBlobURL, 1);
//            });
//            event.preventDefault();
//            return false;
//        }
    }
    }
})(this);
