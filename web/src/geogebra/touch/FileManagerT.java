package geogebra.touch;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.html5.euclidian.EuclidianViewW;
import geogebra.html5.main.FileManager;
import geogebra.html5.main.StringHandler;
import geogebra.html5.util.ggtapi.JSONparserGGT;
import geogebra.touch.main.AppT;
import geogebra.web.gui.GuiManagerW;

import com.google.gwt.core.client.Callback;
import com.googlecode.gwtphonegap.client.PhoneGap;
import com.googlecode.gwtphonegap.client.file.DirectoryEntry;
import com.googlecode.gwtphonegap.client.file.DirectoryReader;
import com.googlecode.gwtphonegap.client.file.EntryBase;
import com.googlecode.gwtphonegap.client.file.FileCallback;
import com.googlecode.gwtphonegap.client.file.FileEntry;
import com.googlecode.gwtphonegap.client.file.FileError;
import com.googlecode.gwtphonegap.client.file.FileReader;
import com.googlecode.gwtphonegap.client.file.FileSystem;
import com.googlecode.gwtphonegap.client.file.FileWriter;
import com.googlecode.gwtphonegap.client.file.Flags;
import com.googlecode.gwtphonegap.client.file.ReaderCallback;
import com.googlecode.gwtphonegap.collection.shared.LightArray;


public class FileManagerT implements FileManager {
	private static final String META_PREFIX = "meta_";
	private static final String GGB_DIR = "GeoGebra";
	private static final String META_DIR = "meta";
	private static final String FILE_EXT = ".ggb";
	
	protected AppT app;
	boolean hasFile = false;
	String data;
	PhoneGap phonegap;
	Flags createIfNotExist = new Flags(true, false);
	Flags dontCreateIfNotExist = new Flags(false, false);
	String filename;
	int count;

	public FileManagerT(AppT app) {
		this.app = app;
		this.phonegap = PhoneGapManager.getPhoneGap();
	}

	private void getGgbDir(final Callback<DirectoryEntry, FileError> callback) {
		this.phonegap.getFile().requestFileSystem(FileSystem.LocalFileSystem_PERSISTENT, 0, new FileCallback<FileSystem, FileError>() {

			@Override
			public void onSuccess(final FileSystem entry) {
				final DirectoryEntry directoryEntry = entry.getRoot();

				directoryEntry.getDirectory(GGB_DIR, createIfNotExist, new FileCallback<DirectoryEntry, FileError>() {

					@Override
					public void onSuccess(final DirectoryEntry ggbDir) {
						callback.onSuccess(ggbDir);
					}

					@Override
					public void onFailure(final FileError error) {
						callback.onFailure(error);
					}
				});
			}

			@Override
			public void onFailure(final FileError error) {
				callback.onFailure(error);
			}
		});
	}

	private void getMetaDir(final Callback<DirectoryEntry, FileError> callback) {
		getGgbDir(new Callback<DirectoryEntry, FileError>() {

			@Override
			public void onSuccess(final DirectoryEntry ggbDir) {
				ggbDir.getDirectory(META_DIR, createIfNotExist, new FileCallback<DirectoryEntry, FileError>() {

					@Override
					public void onSuccess(final DirectoryEntry metaDir) {
						callback.onSuccess(metaDir);
					}

					@Override
					public void onFailure(final FileError error) {
						callback.onFailure(error);
					}
				});
			}

			@Override
			public void onFailure(final FileError error) {
				callback.onFailure(error);
			}
		});
	}

	/**
	 * Gets access to the ggb-File
	 * 
	 * @param fileName name of file
	 * @param flags (true, false) to create file, if it doesn't exist. 
	 * (false, false) don't create file if it doesn't exist
	 * @param callback Callback
	 */
	void getGgbFile(final String fileName, final Flags flags, final Callback<FileEntry, FileError> callback) {
		getGgbDir(new Callback<DirectoryEntry, FileError>() {

			@Override
			public void onSuccess(final DirectoryEntry ggbDir) {
				ggbDir.getFile(fileName, flags,
						new FileCallback<FileEntry, FileError>() {

					@Override
					public void onSuccess(final FileEntry entry) {
						callback.onSuccess(entry);
					}

					@Override
					public void onFailure(final FileError error) {
						callback.onFailure(error);
					}
				});
			}

			@Override
			public void onFailure(final FileError error) {
				callback.onFailure(error);
			}
		});
	}

	/**
	 * Gets access to the metaFile
	 * 
	 * @param fileName name of file
	 * @param flags (true, false) to create file, if it doesn't exist. 
	 * (false, false) don't create file if it doesn't exist
	 * @param callback Callback
	 */
	void getMetaFile(final String fileName, final Flags flags, final Callback<FileEntry, FileError> callback) {
		getMetaDir(new Callback<DirectoryEntry, FileError>() {

			@Override
			public void onSuccess(final DirectoryEntry metaDir) {
				metaDir.getFile(fileName, flags, new FileCallback<FileEntry, FileError>() {

					@Override
					public void onSuccess(final FileEntry metaFile) {
						callback.onSuccess(metaFile);
					}

					@Override
					public void onFailure(final FileError error) {
						callback.onFailure(error);
					}
				});
			}

			@Override
			public void onFailure(final FileError error) {
				callback.onFailure(error);
			}
		});
	}

	/**
	 * Deletes the ggbFile and metaFile from the device. Updates the
	 * BrowseView.
	 */
	public void delete(final Material mat) {
		final String consTitle = mat.getURL();

		getGgbFile(consTitle + FILE_EXT, dontCreateIfNotExist,
				new Callback<FileEntry, FileError>() {

			@Override
			public void onSuccess(final FileEntry ggbFile) {
				ggbFile.remove(new FileCallback<Boolean, FileError>() {

					@Override
					public void onSuccess(final Boolean entryDeleted) {
						removeFile(mat);
						deleteMetaData(consTitle);
					}

					@Override
					public void onFailure(final FileError error) {
						App.debug("Could not remove file");
					}
				});
			}

			@Override
			public void onFailure(final FileError reason) {
				App.debug("Could not get file");
			}

		});
	}

	/**
	 * deletes the metaFile with given filename.
	 * @param title String
	 */
	void deleteMetaData(final String title) {
		getMetaFile(META_PREFIX + title, dontCreateIfNotExist, new Callback<FileEntry, FileError>() {

			@Override
			public void onSuccess(final FileEntry metaFile) {
				metaFile.remove(new FileCallback<Boolean, FileError>() {

					@Override
					public void onSuccess(final Boolean entryDeleted) {
						//
					}

					@Override
					public void onFailure(final FileError error) {
						App.debug("Could not delete metafile");
					}
				});
			}

			@Override
			public void onFailure(final FileError reason) {
				App.debug("Could not get metafile");
			}
		});
	}


	/**
	 * loads every file of the device into the BrowseView.
	 */
	public void getAllFiles() {
		this.getFiles(MaterialFilter.getUniversalFilter());
	}

	/**
	 * 
	 * @param loc {@link Localization}
	 * @param callback Callback
	 */
	void getDefaultConstructionTitle(final Localization loc,
			final Callback<String, String> callback) {

		this.count = 1;
		this.filename = loc.getPlain("UntitledA", this.count + "");

		getGgbDir(new Callback<DirectoryEntry, FileError>() {

			@Override
			public void onSuccess(final DirectoryEntry ggbDir) {
				final DirectoryReader directoryReader = ggbDir.createReader();
				directoryReader.readEntries(new FileCallback<LightArray<EntryBase>, FileError>() {

					@Override
					public void onSuccess(
							final LightArray<EntryBase> entries) {
						for (int i = 0; i < entries.length(); i++) {
							if (entries.get(i).isFile()) {
								final FileEntry fileEntry = entries
										.get(i).getAsFileEntry();
								// get filename without '.ggb'
								final String name = fileEntry.getName().substring(
										0,
										fileEntry.getName().indexOf("."));
								if (name.equals(FileManagerT.this.filename)) {
									FileManagerT.this.count++;
									FileManagerT.this.filename = loc.getPlain(
											"UntitledA",
											FileManagerT.this.count + "");
								}
							}
						}
						callback.onSuccess(FileManagerT.this.filename);
					}

					@Override
					public void onFailure(final FileError error) {
						App.debug("Could not read files");
					}
				});
			}

			@Override
			public void onFailure(final FileError reason) {
				App.debug("Could not get ggbDir");
			}
		});
	}

	/**
	 * loads every file of the device depending on the {@link MaterialFilter filter} into the BrowseView.
	 * @param filter
	 */
	private void getFiles(final MaterialFilter filter) {

		getGgbDir(new Callback<DirectoryEntry, FileError>() {

			@Override
			public void onSuccess(final DirectoryEntry ggbDir) {
				final DirectoryReader directoryReader = ggbDir.createReader();
				directoryReader
				.readEntries(new FileCallback<LightArray<EntryBase>, FileError>() {

					@Override
					public void onSuccess(final LightArray<EntryBase> entries) {
						for (int i = 0; i < entries.length(); i++) {
							final EntryBase entryBase = entries.get(i);
							if (entryBase.isFile()) {
								final FileEntry fileEntry = entryBase.getAsFileEntry();
								// get name without ending (.ggb)
								final String name = fileEntry.getName().substring(
										0,
										fileEntry.getName().indexOf("."));
								getMetaFile(META_PREFIX + name, dontCreateIfNotExist, new Callback<FileEntry, FileError> () {

									@Override
									public void onSuccess(final FileEntry metaFile) {
										final FileReader reader = PhoneGapManager
												.getPhoneGap()
												.getFile()
												.createReader();

										reader.setOnloadCallback(new ReaderCallback<FileReader>() {

											@Override
											public void onCallback(final FileReader result) {
												Material mat = JSONparserGGT.parseMaterial(result.getResult());
												if (mat == null) {
													mat = new Material(0, MaterialType.ggb);
													mat.setTitle(name);
												}
												if (filter.check(mat)) {
													mat.setURL(name);
													addFile(mat);
												}
											}
										});
										reader.readAsText(metaFile);
									}

									@Override
									public void onFailure(final FileError reason) {
										// TODO Auto-generated method stub

									}
								});
							}
						}
					}

					@Override
					public void onFailure(final FileError error) {
						//
					}
				});
			}

			@Override
			public void onFailure(final FileError reason) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void openMaterial(final Material material) {
		app.getKernel().getConstruction().setTitle(material.getTitle());
		getFileData(material.getURL());
	}

	//	/**
	//	 * Checks if the file with given filename is already saved on the device.
	//	 * 
	//	 * @param filename
	//	 *            String
	//	 */
	//	public void hasFile(final String filename, final Callback<Boolean, Boolean> callback) {
	//		this.hasFile = false;
	//		this.phonegap.getFile().requestFileSystem(
	//				FileSystem.LocalFileSystem_PERSISTENT, 0,
	//				new FileCallback<FileSystem, FileError>() {
	//
	//					@Override
	//					public void onSuccess(final FileSystem entry) {
	//						final DirectoryEntry directoryEntry = entry.getRoot();
	//
	//						/**
	//						 * Get directory GeoGebra, create it, if it doesn't
	//						 * exist (Flags(true, false)).
	//						 */
	//						directoryEntry.getDirectory(GGB_DIR, new Flags(true,
	//								false),
	//								new FileCallback<DirectoryEntry, FileError>() {
	//
	//									@Override
	//									public void onSuccess(
	//											final DirectoryEntry ggbDir) {
	//										final DirectoryReader directoryReader = ggbDir
	//												.createReader();
	//										directoryReader
	//												.readEntries(new FileCallback<LightArray<EntryBase>, FileError>() {
	//
	//													@Override
	//													public void onSuccess(final LightArray<EntryBase> entries) {
	//														for (int i = 0; i < entries.length(); i++) {
	//															if (entries.get(i).isFile()) {
	//																final FileEntry fileEntry = entries.get(i).getAsFileEntry();
	//																final String name = fileEntry.getName().substring(0, fileEntry.getName().indexOf("."));
	//																if (name.equals(filename)) {
	//																	FileManager.this.hasFile = true;
	//																	break;
	//																}
	//															}
	//														}
	//														callback.onSuccess(FileManager.this.hasFile);
	//													}
	//
	//													@Override
	//													public void onFailure(final FileError error) {
	//
	//													}
	//												});
	//									}
	//
	//									@Override
	//									public void onFailure(final FileError error) {
	//										// TODO "Directory GeoGebra not found"
	//									}
	//								});
	//
	//					}
	//
	//					@Override
	//					public void onFailure(final FileError error) {
	//						// TODO
	//					}
	//				});
	//	}

	//	/**
	//	 * not in use yet
	//	 */
	//	public void renameFile(final String oldName, final String newName) {
	//		
	//		getGgbDir(new Callback<DirectoryEntry, FileError>() {
	//
	//			@Override
	//            public void onSuccess(final DirectoryEntry ggbDir) {
	//	            ggbDir.getFile(oldName + FILE_EXT, dontCreateIfNotExist, new FileCallback<FileEntry, FileError>() {
	//					
	//					@Override
	//					public void onSuccess(FileEntry entry) {
	//						renameMetaData(ggbDir, META_PREFIX + oldName, META_PREFIX + newName);
	//					}
	//
	//					@Override
	//					public void onFailure(FileError error) {
	//						// TODO Auto-generated method stub
	//					}
	//				});
	//            }
	//			
	//			@Override
	//            public void onFailure(FileError reason) {
	//	            // TODO Auto-generated method stub
	//	            
	//            }
	//		});
	//	}
	//	
	//	/**
	//	 * not in use yet
	//	 */
	//	void renameMetaData(DirectoryEntry ggbDir, final String metaFileNameOld, final String metaFileNameNew) {
	//		getMetaDir(new Callback<DirectoryEntry, FileError>() {
	//
	//			@Override
	//			public void onSuccess(final DirectoryEntry metaDir) {
	//				metaDir.getFile(metaFileNameOld, createIfNotExist, new FileCallback<FileEntry, FileError>() {
	//
	//					@Override
	//					public void onSuccess(FileEntry metaFile) {
	//						metaFile.moveTo(metaDir, metaFileNameNew, new FileCallback<FileEntry, FileError>() {
	//
	//							@Override
	//							public void onSuccess(FileEntry entry) {
	//								abc("Meta renamed");
	//							}
	//
	//							@Override
	//							public void onFailure(FileError error) {
	//								// TODO Auto-generated method stub
	//
	//							}
	//						});
	//					}
	//
	//					@Override
	//					public void onFailure(FileError error) {
	//						// TODO Auto-generated method stub
	//
	//					}
	//				});
	//
	//			}
	//
	//			@Override
	//			public void onFailure(FileError reason) {
	//				// TODO Auto-generated method stub
	//
	//			}
	//		});
	//	}


	/**
	 * Saves the active file (with metaData) on the device into directory
	 * "GeoGebra".
	 * 
	 * @param cb 
	 */
	public void saveFile(final Callback<String, Throwable> cb) {
//		ToolTipManagerW.sharedInstance().showBottomInfoToolTip("<html>" + StringUtil.toHTMLString(app.getMenu("Save")) + "</html>", "");
		final String consTitle = app.getKernel().getConstruction().getTitle();
		final StringHandler base64saver = new StringHandler() {
			@Override
			public void handle(final String s) {

				getGgbFile(consTitle + FILE_EXT, createIfNotExist, new Callback<FileEntry, FileError>() {

					@Override
					public void onSuccess(final FileEntry ggbFile) {
						ggbFile.createWriter(new FileCallback<FileWriter, FileError>() {

							@Override
							public void onSuccess(final FileWriter writer) {
								writer.write(s);
								createMetaData(consTitle, cb);
								app.setSaved();
								//TODO don't call loadFeatured() only update saved file!!!
								((GuiManagerW) app.getGuiManager()).getBrowseGUI().loadFeatured();
								if (cb != null) {
									cb.onSuccess("Success");
								}
								
							}

							@Override
							public void onFailure(final FileError error) {
								cb.onFailure((Throwable) error);
							}
						});
					}

					@Override
					public void onFailure(final FileError error) {
						cb.onFailure((Throwable) error);
					}

				});
			}
		};
		((geogebra.html5.main.GgbAPIW) app.getGgbApi()).getBase64(true, base64saver);
	}

	/**
	 * 
	 * @param title of file
	 */
	private void getFileData(final String title) {

		getGgbFile(title + FILE_EXT, dontCreateIfNotExist, new Callback<FileEntry, FileError>() {
			@Override
			public void onSuccess(final FileEntry ggbFile) {
				final FileReader reader = phonegap.getFile().createReader();
				reader.setOnloadCallback(new ReaderCallback<FileReader>() {

					@Override
					public void onCallback(final FileReader result) {
						app.getGgbApi().setBase64(result.getResult());
					}
				});
				reader.readAsText(ggbFile);
			}

			@Override 
			public void onFailure(final FileError reason) {
				//
			}
		});
	}

	/**
	 * create metaData including meta and thumbnail.
	 * 
	 * @param title String
	 * @param cb 
	 */
	void createMetaData(final String title, final Callback<String, Throwable> cb) {

		getMetaFile(META_PREFIX + title, createIfNotExist, new Callback<FileEntry, FileError>(){

			@Override
			public void onSuccess(final FileEntry metaFile) {
				metaFile.createWriter(new FileCallback<FileWriter, FileError>() {

					@Override
					public void onSuccess(final FileWriter writer) {
						// extract metadata
						final Material mat = new Material(0, MaterialType.ggb);
						mat.setTimestamp(System.currentTimeMillis() / 1000);
						mat.setTitle(title);
						mat.setDescription(app.getKernel().getConstruction().getWorksheetText(0));
						mat.setThumbnail(((EuclidianViewW) app.getEuclidianView1()).getCanvasBase64WithTypeString());
						writer.write(mat.toJson().toString());
					}

					@Override
					public void onFailure(final FileError error) {
						cb.onFailure((Throwable) error);
					}
				});
			} 

			@Override
			public void onFailure(final FileError error) {
				cb.onFailure((Throwable) error);
			}
		});
	}

	public void search(final String query) {
		this.getFiles(MaterialFilter.getSearchFilter(query));
	}

	/**
	 * different behavior for phone and tablet
	 */
	public void removeFile(final Material mat) {
		// TODO Auto-generated method stub

	}

	/**
	 * different behavior for phone and tablet
	 */
	public void addFile(final Material mat) {
		// TODO Auto-generated method stub

	}
}