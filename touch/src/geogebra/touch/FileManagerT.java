package geogebra.touch;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.main.AppWeb;
import geogebra.html5.main.StringHandler;
import geogebra.html5.util.View;
import geogebra.html5.util.ggtapi.JSONparserGGT;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.gwtphonegap.client.file.DirectoryEntry;
import com.googlecode.gwtphonegap.client.file.FileCallback;
import com.googlecode.gwtphonegap.client.file.FileEntry;
import com.googlecode.gwtphonegap.client.file.FileError;
import com.googlecode.gwtphonegap.client.file.FileReader;
import com.googlecode.gwtphonegap.client.file.FileSystem;
import com.googlecode.gwtphonegap.client.file.FileWriter;
import com.googlecode.gwtphonegap.client.file.Flags;
import com.googlecode.gwtphonegap.client.file.ReaderCallback;

public class FileManagerT {
	private static final String FILE_PREFIX = "file#";
	private static final String THUMB_PREFIX = "img#";
	private static final String META_PREFIX = "meta#";
	Storage stockStore = Storage.getLocalStorageIfSupported();

	public FileManagerT() {
		if (this.stockStore != null) {
			this.ensureKeyPrefixes();
		}
	}

	public void delete(final String consTitle) {
		this.stockStore.removeItem(FILE_PREFIX + consTitle);
		
		//begin delete from device
		TouchEntryPoint.getPhoneGap().getFile().requestFileSystem(FileSystem.LocalFileSystem_PERSISTENT, 0, new FileCallback<FileSystem, FileError>() {
			 
	        @Override
	        public void onSuccess(FileSystem entry) {
	                DirectoryEntry directoryEntry = entry.getRoot();
	        
	                /**
	                 * Get directory GeoGebra, don't create it, if it doesn't exist (Flags(false, false)).
	                 */
	                directoryEntry.getDirectory("GeoGebra", new Flags(false, false), new FileCallback<DirectoryEntry, FileError>() {
						
						@Override
						public void onSuccess(DirectoryEntry dirEntry) {
							
							/**
							 * create if and only if the file doesn't exist
							 */
							dirEntry.getFile(consTitle +".ggb", new Flags(false, false), new FileCallback<FileEntry, FileError>() {

								@Override
								public void onSuccess(FileEntry fileEntry) {
									fileEntry.remove(new FileCallback<Boolean, FileError>() {

										@Override
										public void onSuccess(Boolean entry) {
											// TODO Auto-generated method stub
											
										}

										@Override
										public void onFailure(FileError error) {
											// TODO Auto-generated method stub
											
										}
									});
								}

								@Override
								public void onFailure(FileError error) {
									// TODO "File not found"
									
								}
							});
						}
						
						@Override
						public void onFailure(FileError error) {
							// TODO "Directory GeoGebra not found"
							
						}
					});
	        }

	        @Override
	        public void onFailure(FileError error) {
	        	// TODO

	        }
	});
	//end delete from device
		
		
		
		//FIXME META_DATA is never removed from stockStore
		this.stockStore.removeItem(THUMB_PREFIX + consTitle);
		TouchEntryPoint.reloadLocalFiles(consTitle);
	}

	private void ensureKeyPrefixes() {
		if (this.stockStore.getLength() > 0) {
			for (int i = 0; i < this.stockStore.getLength(); i++) {
				final String oldKey = this.stockStore.key(i);
				if (!oldKey.contains("#")) {
					this.stockStore.removeItem(oldKey);
				}
			}
		}
	}

	public List<Material> getAllFiles() {
		return this.getFiles(MaterialFilter.getUniversalFilter());
	}

	String getDefaultConstructionTitle(final Localization loc) {
		int i = 1;
		String filename;
		do {
			filename = loc.getPlain("UntitledA", i + "");
			i++;
		} while (this.hasFile(filename));
		return filename;
	}

	private void printResult(String result) {
		abc("result: " + result);
	}
	
	
	

	
//  FIXME try to read from file	
//	public native void readFileNative(String title) /*-{
//
//	    window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, gotFS, fail);
//	
//	    function gotFS(fileSystem) {
//	    	this.@geogebra.touch.FileManagerT::printResult(Ljava/lang/String;)("gotFS");
//	        fileSystem.root.getDirectory("GeoGebra", null, gotDirEntry, fail);
//	    }
//	    
//	    function gotDirEntry(dirEntry) {
//	    	this.@geogebra.touch.FileManagerT::printResult(Ljava/lang/String;)("gotDirEntry");
//	    	dirEntry.getFile(title+".ggb", null, gotFileEntry, fail);
//	    }
//	
//	    function gotFileEntry(fileEntry) {
//	    	this.@geogebra.touch.FileManagerT::printResult(Ljava/lang/String;)("gotFileEntry");
//	        fileEntry.file(gotFile, fail);
//	    }
//	
//	    function gotFile(file){
//	    	this.@geogebra.touch.FileManagerT::printResult(Ljava/lang/String;)("gotfile");
//	        readDataUrl(file);
//	        readAsText(file);
//	    }
//	
//	    function readDataUrl(file) {
//	    	this.@geogebra.touch.FileManagerT::printResult(Ljava/lang/String;)("error code " + evt.target.error.code);
//	        var reader = new FileReader();
//	        reader.onloadend = function(evt) {
//	        	this.@geogebra.touch.FileManagerT::printResult(Ljava/lang/String;)("Read as data URL" + evt.target.result);
//	        };
//	        reader.readAsDataURL(file);
//	    }
//	
//	    function readAsText(file) {
//	    	this.@geogebra.touch.FileManagerT::printResult(Ljava/lang/String;)("readAsText");
//	        var reader = new FileReader();
//	        reader.onloadend = function(evt) {
//	        	this.@geogebra.touch.FileManagerT::printResult(Ljava/lang/String;)("Read as text" + evt.target.result);
//	        };
//	        reader.readAsText(file);
//	    }
//	
//	    function fail(evt) {
//	    	this.@geogebra.touch.FileManagerT::printResult(Ljava/lang/String;)("error code " + evt.target.error.code);
//	    }
//	}-*/;
//	
//	
	
	private boolean getFile(final String title, final App app) {
//		TODO try to read from file native
//		readFileNative(title);
//		return true;
		
		// try to read from file with gwtPhonegap
		TouchEntryPoint.getPhoneGap().getFile().requestFileSystem(FileSystem.LocalFileSystem_PERSISTENT, 0, new FileCallback<FileSystem, FileError>() {
			 
	        @Override
	        public void onSuccess(FileSystem entry) {
	                DirectoryEntry directoryEntry = entry.getRoot();
	                abc("access to filesystem");
	                /**
	                 * Get directory GeoGebra, don't create it, if it doesn't exist (Flags(false, false)).
	                 */
	                directoryEntry.getDirectory("GeoGebra", new Flags(false, false), new FileCallback<DirectoryEntry, FileError>() {
						@Override
						public void onSuccess(DirectoryEntry dirEntry) {
							abc("onSuccess got dir geogebra");
							dirEntry.getFile(title + ".ggb", new Flags(false, false), new FileCallback<FileEntry, FileError>() {

								@Override
								public void onSuccess(final FileEntry fileEntry) {
									abc("onSuccess got fileEntry");
									FileReader reader = TouchEntryPoint.getPhoneGap().getFile().createReader();
									
									// callback - in every cases
									reader.setOnLoadEndCallback(new ReaderCallback<FileReader>() {

										@Override
										public void onCallback(FileReader result) {
											abc("result: " + result.getResult());
										}
									});
									
									// callback - only if there was no error
									reader.setOnloadCallback(new ReaderCallback<FileReader>() {

										@Override
										public void onCallback(FileReader result) {
											abc("result: " + result.getResult());
											app.getGgbApi().setBase64(result.getResult());
										}
									});
									reader.readAsText(fileEntry);
								}

								@Override
								public void onFailure(FileError error) {
									app.showError("LoadFileFailed");
									abc("filenotfound");
								}
							});
						}
						
						@Override
						public void onFailure(FileError error) {
							app.showError("LoadFileFailed");
							abc("dir not found");
						}
					});
	        }

	        @Override
	        public void onFailure(FileError error) {
				app.showError("LoadFileFailed");
	        }
	});
		
		// this is only for stockStore
		boolean success = true;
		try {
			final String base64 = this.stockStore.getItem(FILE_PREFIX + title);
			if (base64 == null) {
				return false;
			}
			app.getGgbApi().setBase64(base64);
		} catch (final Throwable t) {
			success = false;
			app.showError("LoadFileFailed");
			t.printStackTrace();
		}
		return success;
	}


	private List<Material> getFiles(final MaterialFilter filter) {
		final List<Material> ret = new ArrayList<Material>();
		if (this.stockStore == null || this.stockStore.getLength() <= 0) {
			return ret;
		}

		for (int i = 0; i < this.stockStore.getLength(); i++) {
			final String key = this.stockStore.key(i);
			if (key.startsWith(FILE_PREFIX)) {
				final String keyStem = key.substring(FILE_PREFIX.length());
				Material mat = JSONparserGGT.parseMaterial(this.stockStore
						.getItem(META_PREFIX + keyStem));
				if (mat == null) {
					mat = new Material(0, MaterialType.ggb);
					mat.setTitle(keyStem);
				}
				if (filter.check(mat)) {
					mat.setURL(keyStem);
					ret.add(mat);
				}
			}
		}

		return ret;
	}

	public void getMaterial(final Material material, final AppWeb app) {
		if (material.getId() > 0) {
			// remote material
			new View(RootPanel.getBodyElement(), app)
					.processFileName("http://www.geogebratube.org/files/material-"
							+ material.getId() + ".ggb");
			app.setUnsaved();
		} else {
			((TouchApp) app).setConstructionTitle(material.getTitle());
			this.getFile(material.getURL(), app);
		}

	}

	public String getThumbnailDataUrl(final String title) {
		return this.stockStore.getItem(THUMB_PREFIX + title);
	}

	public boolean hasFile(final String filename) {
		return this.stockStore != null
				&& this.stockStore.getItem(FILE_PREFIX + filename) != null;
	}

	public native void abc(String s) /*-{
	console.log(s);
}-*/;
	
	public void saveFile(final App app) {
		final String consTitle = app.getKernel().getConstruction().getTitle();
		final StringHandler base64saver = new StringHandler() {
			@Override
			public void handle(final String s) {
				TouchEntryPoint.reloadLocalFiles(consTitle);
				stockStore.setItem(FILE_PREFIX + consTitle, s);
				TouchEntryPoint.getPhoneGap().getFile().requestFileSystem(FileSystem.LocalFileSystem_PERSISTENT, 0, new FileCallback<FileSystem, FileError>() {
 
			        @Override
			        public void onSuccess(FileSystem entry) {
			                DirectoryEntry directoryEntry = entry.getRoot();
			                
			                /**
			                 * create directory GeoGebra if it doesn't exist - Flags(true, false)
			                 */
			                directoryEntry.getDirectory("GeoGebra", new Flags(true, false), new FileCallback<DirectoryEntry, FileError>() {
								
								@Override
								public void onSuccess(DirectoryEntry entry) {
									/**
									 * create if and only if the file doesn't exist
									 */
									entry.getFile(consTitle +".ggb", new Flags(true, true), new FileCallback<FileEntry, FileError>() {

										@Override
										public void onSuccess(FileEntry fileEntry) {
											fileEntry.createWriter(new FileCallback<FileWriter, FileError>() {

												@Override
												public void onSuccess(FileWriter writer) {
													writer.write(s);
												}

												@Override
												public void onFailure(FileError error) {
													// TODO Auto-generated method stub
													
												}
											});
										}

										@Override
										public void onFailure(FileError error) {
											// TODO Auto-generated method stub
											
										}
									});
								}
								
								@Override
								public void onFailure(FileError error) {
									// TODO Auto-generated method stub
								}
							});
			        }

			        @Override
			        public void onFailure(FileError error) {
			                abc("requestFileSystem onFailure");
			        }
			});	
			}
		};

		((geogebra.html5.main.GgbAPI) app.getGgbApi()).getBase64(base64saver);

		// extract metadata
		final Material mat = new Material(0, MaterialType.ggb);
		mat.setTimestamp(System.currentTimeMillis() / 1000);
		mat.setTitle(consTitle);
		mat.setDescription(app.getKernel().getConstruction()
				.getWorksheetText(0));

		this.stockStore.setItem(META_PREFIX + consTitle, mat.toJson()
				.toString());
		this.stockStore.setItem(THUMB_PREFIX + consTitle,
				((EuclidianViewWeb) app.getEuclidianView1())
						.getCanvasBase64WithTypeString());
		app.setSaved();
		((TouchApp) app).approveFileName();
	}

	public List<Material> search(final String query) {
		return this.getFiles(MaterialFilter.getSearchFilter(query));
	}
}
