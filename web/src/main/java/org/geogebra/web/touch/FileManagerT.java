package org.geogebra.web.touch;

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.common.main.MaterialsManager;
import org.geogebra.common.move.ggtapi.models.JSONParserGGT;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.MaterialFilter;
import org.geogebra.common.move.ggtapi.models.SyncEvent;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.main.FileManager;
import org.geogebra.web.full.util.SaveCallback;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.BrowserStorage;

import com.google.gwt.core.client.Callback;
import com.googlecode.gwtphonegap.client.PhoneGap;
import com.googlecode.gwtphonegap.client.file.DirectoryEntry;
import com.googlecode.gwtphonegap.client.file.DirectoryReader;
import com.googlecode.gwtphonegap.client.file.EntryBase;
import com.googlecode.gwtphonegap.client.file.File;
import com.googlecode.gwtphonegap.client.file.FileCallback;
import com.googlecode.gwtphonegap.client.file.FileEntry;
import com.googlecode.gwtphonegap.client.file.FileError;
import com.googlecode.gwtphonegap.client.file.FileObject;
import com.googlecode.gwtphonegap.client.file.FileReader;
import com.googlecode.gwtphonegap.client.file.FileSystem;
import com.googlecode.gwtphonegap.client.file.FileWriter;
import com.googlecode.gwtphonegap.client.file.Flags;
import com.googlecode.gwtphonegap.client.file.ReaderCallback;
import com.googlecode.gwtphonegap.collection.shared.LightArray;
//import geogebra.web.gui.dialog.DialogManagerW;

public class FileManagerT extends FileManager {
	private static final String META_PREFIX = "meta_";
	private static final String GGB_DIR = "GeoGebra";
	private static final String META_DIR = "meta";
	private static final String FILE_EXT = ".ggb";
	
	// to convert files from older ggbVersions
	private static final String OLD_FILE_PREFIX = "file#";
	private static final String OLD_THUMB_PREFIX = "img#";
	private static final String OLD_META_PREFIX = "meta#";
	BrowserStorage stockStore = BrowserStorage.LOCAL;
	
	PhoneGap phonegap;
	Flags createIfNotExist = new Flags(true, false);
	Flags dontCreateIfNotExist = new Flags(false, false);
	int nextFreeID = 1;

	/**
	 * @param app
	 *            application
	 */
	public FileManagerT(final AppW app) {
		super(app);
		init();
	}
	
	protected void init() {
		this.phonegap = PhoneGapManager.getPhoneGap();
		convertToNewFileFormat();
	}

	void getGgbDir(final Callback<DirectoryEntry, FileError> callback) {
		this.phonegap.getFile().requestFileSystem(
		        File.LocalFileSystem_PERSISTENT, 0,
		        new FileCallback<FileSystem, FileError>() {

			        @Override
			        public void onSuccess(final FileSystem entry) {
				        final DirectoryEntry directoryEntry = entry.getRoot();

				        directoryEntry.getDirectory(GGB_DIR, createIfNotExist,
				                new FileCallback<DirectoryEntry, FileError>() {

					                @Override
					                public void onSuccess(
					                        final DirectoryEntry ggbDir) {
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
				ggbDir.getDirectory(META_DIR, createIfNotExist,
				        new FileCallback<DirectoryEntry, FileError>() {

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
	 * @param key
	 *            of file
	 * @param flags
	 *            (true, false) to create file, if it doesn't exist. (false,
	 *            false) don't create file if it doesn't exist
	 * @param callback
	 *            Callback
	 */
	void getGgbFile(final String key, final Flags flags,
	        final Callback<FileEntry, FileError> callback) {
		getGgbDir(new Callback<DirectoryEntry, FileError>() {

			@Override
			public void onSuccess(final DirectoryEntry ggbDir) {
				ggbDir.getFile(key, flags,
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
	 * @param key
	 *            of file
	 * @param flags
	 *            (true, false) to create file, if it doesn't exist. (false,
	 *            false) don't create file if it doesn't exist
	 * @param callback
	 *            Callback
	 */
	void getMetaFile(final String key, final Flags flags,
	        final Callback<FileEntry, FileError> callback) {
		getMetaDir(new Callback<DirectoryEntry, FileError>() {

			@Override
			public void onSuccess(final DirectoryEntry metaDir) {
				metaDir.getFile(key, flags,
				        new FileCallback<FileEntry, FileError>() {

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
	 * Deletes the ggbFile and metaFile from the device. Updates the BrowseView.
	 * 
	 * @param mat
	 *            {@link Material}
	 */
	@Override
	public void delete(final Material mat, boolean permanent,
	        final Runnable onSuccess) {
		if (!permanent) {
			mat.setDeleted(true);
			this.createMetaData(getFileKey(mat), mat, null);
			return;
		}

		final String key = getFileKey(mat);

		getGgbFile(key + FILE_EXT, dontCreateIfNotExist,
		        new Callback<FileEntry, FileError>() {

			        @Override
			        public void onSuccess(final FileEntry ggbFile) {
				        ggbFile.remove(new FileCallback<Boolean, FileError>() {

					        @Override
					        public void onSuccess(final Boolean entryDeleted) {
						        removeFile(mat);
						        onSuccess.run();
						        deleteMetaData(key);
								Log.debug("DELETE success: " + key + FILE_EXT);
					        }

					        @Override
					        public void onFailure(final FileError error) {
								Log.debug("DELETE Could not remove file: "
						                + key + FILE_EXT);
					        }
				        });
			        }

			        @Override
			        public void onFailure(final FileError reason) {
						Log.debug("DELETE Could not get file: " + key
				                + FILE_EXT);
			        }
		        });
	}

	/**
	 * deletes the metaFile with given filename.
	 * 
	 * @param key
	 *            String
	 */
	void deleteMetaData(final String key) {
		getMetaFile(META_PREFIX + key, dontCreateIfNotExist,
		        new Callback<FileEntry, FileError>() {

			        @Override
			        public void onSuccess(final FileEntry metaFile) {
				        metaFile.remove(new FileCallback<Boolean, FileError>() {

					        @Override
					        public void onSuccess(final Boolean entryDeleted) {
						        //
					        }

					        @Override
					        public void onFailure(final FileError error) {
								Log.debug("Could not delete metafile");
					        }
				        });
			        }

			        @Override
			        public void onFailure(final FileError reason) {
						Log.debug("Could not get metafile");
			        }
		        });
	}

	/**
	 * loads every file of the device depending on the {@link MaterialFilter
	 * filter} into the BrowseView.
	 * 
	 * @param filter
	 *            {@link MaterialFilter}
	 */
	@Override
	protected void getFiles(final MaterialFilter filter) {
		getGgbDir(new Callback<DirectoryEntry, FileError>() {

			@Override
			public void onSuccess(final DirectoryEntry ggbDir) {
				final DirectoryReader directoryReader = ggbDir.createReader();
				directoryReader
						.readEntries(
								new FileCallback<LightArray<EntryBase>, FileError>() {

									@Override
									public void onSuccess(
											final LightArray<EntryBase> entries) {
										for (int i = 0; i < entries
												.length(); i++) {
											final EntryBase entryBase = entries
													.get(i);
											if (entryBase.isFile()) {
												processFile(entryBase, filter);
											}
										}
									}

									@Override
									public void onFailure(
											final FileError error) {
										Log.debug(
												"Could not read the file entries");
									}
								});
			}

			@Override
			public void onFailure(final FileError reason) {
				Log.debug("Could not read GGBDir");
			}
		});
	}
	
	@Override
	public void rename(final String newTitle, final Material mat) {
		rename(newTitle, mat, null);
	}

	@Override
	public void rename(final String newTitle, final Material mat,
	        final Runnable callback) {
		Log.debug("RENAME" + mat.getTitle() + "->" + newTitle);

		final String newKey = MaterialsManager.createKeyString(mat.getLocalID(),
		        newTitle);
		final String oldKey = getFileKey(mat);
		Log.debug("RENAME local fn" + oldKey + "," + mat.getModified());
		getGgbDir(new Callback<DirectoryEntry, FileError>() {

			@Override
			public void onSuccess(final DirectoryEntry ggbDir) {
				ggbDir.getFile(oldKey + FILE_EXT, dontCreateIfNotExist,
				        new FileCallback<FileEntry, FileError>() {

					        @Override
					        public void onSuccess(FileEntry ggbFile) {
								Log.debug("RENAME accessed fn" + oldKey);
						        ggbFile.moveTo(
						                ggbDir,
						                newKey + FILE_EXT,
						                new FileCallback<FileEntry, FileError>() {

							                @Override
							                public void onSuccess(
							                        FileEntry entry) {
								                mat.setTitle(newTitle);
								                renameMetaData(oldKey, newKey,
								                        mat, callback);
							                }

							                @Override
							                public void onFailure(
							                        FileError error) {
												Log.debug("RENAME cannotMove fn"
								                        + oldKey + "/" + newKey);
							                }
						                });
					        }

					        @Override
					        public void onFailure(FileError error) {
								Log.debug("RENAME not found fn" + oldKey);
					        }
				        });
			}

			@Override
			public void onFailure(FileError reason) {
				// TODO Auto-generated method stub

			}
		});

	}

	protected void processFile(EntryBase entryBase,
			final MaterialFilter filter) {
		final FileEntry fileEntry = entryBase.getAsFileEntry();
		// get key without ending (.ggb)
		final String key = fileEntry.getName().substring(0,
				fileEntry.getName().indexOf("."));
		readMetaData(key, dontCreateIfNotExist,
				new Callback<String, FileError>() {

					@Override
					public void onSuccess(String result) {
						Material mat = JSONParserGGT.parseMaterial(result);
						if (mat == null) {
							mat = new Material(0, MaterialType.ggb);
							mat.setTitle(getTitleFromKey(key));
						}
						final Material mat1 = mat;
						mat1.setLocalID(MaterialsManager.getIDFromKey(key));

						if (filter.check(mat1)) {
							addMaterial(mat1);
						}
						}

					@Override
					public void onFailure(FileError reason) {
						Log.debug("Could not read meta data ");
						}
				});
	}

	/**
	 * Deletes the old metaData and creates a new one
	 * 
	 * @param oldKey
	 *            String
	 * @param newKey
	 *            String
	 * @param mat
	 *            {@link Material}
	 * @param callback
	 *            run after rename
	 */
	void renameMetaData(final String oldKey, final String newKey,
	        final Material mat, final Runnable callback) {

		getMetaFile(META_PREFIX + newKey, createIfNotExist,
		        new Callback<FileEntry, FileError>() {

			        @Override
			        public void onSuccess(final FileEntry metaFile) {
				        metaFile.createWriter(new FileCallback<FileWriter, FileError>() {

					        @Override
					        public void onSuccess(final FileWriter writer) {
						        mat.setBase64("");
						        deleteMetaData(oldKey);
						        writer.write(mat.toJson().toString());
						        if (callback != null) {
							        callback.run();
						        }
					        }

					        @Override
					        public void onFailure(final FileError error) {
						        // TODO
					        }
				        });
			        }

			        @Override
			        public void onFailure(final FileError error) {
				        // TODO
			        }
		        });
	}

	/**
	 * @param oldKey
	 *            String
	 * @param flag
	 *            Flags
	 * @param cb
	 *            Callback
	 */
	void readMetaData(String oldKey, Flags flag,
	        final Callback<String, FileError> cb) {
		getMetaFile(META_PREFIX + oldKey, dontCreateIfNotExist,
		        new Callback<FileEntry, FileError>() {

			        @Override
			        public void onFailure(FileError reason) {
				        cb.onFailure(reason);
			        }

			        @Override
			        public void onSuccess(FileEntry metaFile) {
				        metaFile.getFile(new FileCallback<FileObject, FileError>() {

					        @Override
							public void onSuccess(FileObject entry) {
						        final FileReader reader = PhoneGapManager
						                .getPhoneGap().getFile().createReader();

						        reader.setOnloadCallback(new ReaderCallback<FileReader>() {
							        @Override
									public void onCallback(FileReader result) {
								        cb.onSuccess(result.getResult());
							        }
						        });

						        reader.setOnErrorCallback(new ReaderCallback<FileReader>() {
							        @Override
									public void onCallback(FileReader result) {
								        cb.onFailure(result.getError());
							        }
						        });
						        reader.readAsText(entry);
					        }

					        @Override
							public void onFailure(FileError error) {
						        cb.onFailure(error);
					        }
				        });

			        }
		        });
	}

	/**
	 * Saves the active file (with metaData) on the device into directory
	 * "GeoGebra".
	 * 
	 * @param cb
	 *            {@link SaveCallback}
	 * @param base64 String
	 */
	@Override
	public void saveFile(final String base64, final long modified,
	        final SaveCallback cb) {
		if (getApp().getLocalID() >= 0) {
			doSaveFile(getApp().getLocalID(), base64, modified, cb);
			return;
		}
		createID(new Callback<Integer, String>() {

			@Override
			public void onFailure(String reason) {
				Log.error("SAVE FAILED " + reason);

			}

			@Override
			public void onSuccess(Integer id) {
				doSaveFile(id, base64, modified, cb);
			}

		});

	}

	void doSaveFile(final int localID, final String base64,
	        final long modified,
	        final SaveCallback cb) {

		if (getApp().getLocalID() == -1) {
			getApp().setLocalID(localID);
		}
		final String key = MaterialsManager.createKeyString(localID, getApp()
		        .getKernel().getConstruction().getTitle());
		getGgbFile(key + FILE_EXT, createIfNotExist,
		        new Callback<FileEntry, FileError>() {

			        @Override
			        public void onSuccess(final FileEntry ggbFile) {
				        ggbFile.createWriter(new FileCallback<FileWriter, FileError>() {

					        @Override
					        public void onSuccess(final FileWriter writer) {
						        writer.write(base64);
						        Material mat = createMaterial("", modified);
						        // ensure material has the correct local id
						        mat.setLocalID(app.getLocalID());
						        createMetaData(key, mat , cb);
					        }

					        @Override
					        public void onFailure(final FileError error) {
						        cb.onError();
					        }
				        });
			        }

			        @Override
			        public void onFailure(final FileError error) {
				        cb.onError();
			        }
		        });
	}

	/**
	 * @param cb
	 *            Callback
	 */
	void createID(final Callback<Integer, String> cb) {
		getGgbDir(new Callback<DirectoryEntry, FileError>() {

			@Override
			public void onSuccess(DirectoryEntry ggbDir) {
				final DirectoryReader directoryReader = ggbDir.createReader();
				directoryReader
				        .readEntries(new FileCallback<LightArray<EntryBase>, FileError>() {

					        @Override
					        public void onSuccess(
					                final LightArray<EntryBase> entries) {

										for (int i = 0; i < entries
												.length(); i++) {
											if (entries.get(i).isFile()) {
												final FileEntry fileEntry = entries
														.get(i)
														.getAsFileEntry();
												increaseNextIdForFile(fileEntry);
											}
										}
										cb.onSuccess(nextFreeID);
										nextFreeID++;
									}

									@Override
									public void onFailure(
											final FileError error) {
										//
									}
				        });
			}

			@Override
			public void onFailure(FileError reason) {
				// TODO Auto-generated method stub
			}
		});
	}

	protected void increaseNextIdForFile(FileEntry fileEntry) {
		final String key = fileEntry.getName().substring(0,
				fileEntry.getName().indexOf("."));
		if (key.startsWith(MaterialsManager.FILE_PREFIX)) {
			int fileID = MaterialsManager.getIDFromKey(key);
			if (fileID >= nextFreeID) {
				nextFreeID = fileID + 1;
			}
		}
	}

	/**
	 * create metaData.
	 * 
	 * @param key
	 *            String
	 * @param mat
	 *            metadata
	 * @param cb
	 *            {@link SaveCallback}
	 */
	void createMetaData(final String key, final Material mat,
	        final SaveCallback cb) {
		if (mat.isDeleted()) {
			Log.debug("DELETE flag" + mat.getId());
		}
		getMetaFile(META_PREFIX + key, createIfNotExist,
		        new Callback<FileEntry, FileError>() {

			        @Override
			        public void onSuccess(final FileEntry metaFile) {
				        metaFile.createWriter(new FileCallback<FileWriter, FileError>() {

					        @Override
					        public void onSuccess(final FileWriter writer) {

						        // mat.setTitle(FileManager.getTitleFromKey(key));
						        mat.setBase64("");
						        writer.write(mat.toJson().toString());
						        if (cb != null) {
							        cb.onSaved(mat, true);
						        }
					        }

					        @Override
					        public void onFailure(final FileError error) {
						        if (cb != null) {
							        cb.onError();
						        }
					        }
				        });
			        }

			        @Override
			        public void onFailure(final FileError error) {
				        if (cb != null) {
					        cb.onError();
				        }
			        }
		        });
	}

	@Override
	public void uploadUsersMaterials(final ArrayList<SyncEvent> events) {
		getGgbDir(new Callback<DirectoryEntry, FileError>() {

			@Override
			public void onSuccess(final DirectoryEntry ggbDir) {
				final DirectoryReader directoryReader = ggbDir.createReader();
				directoryReader
				        .readEntries(new FileCallback<LightArray<EntryBase>, FileError>() {

					        @Override
					        public void onSuccess(
					                final LightArray<EntryBase> entries) {
						        setNotSyncedFileCount(entries.length(), events);
						        for (int i = 0; i < entries.length(); i++) {
							        final EntryBase entryBase = entries.get(i);
							        if (entryBase.isFile()) {
								        final FileEntry fileEntry = entryBase
								                .getAsFileEntry();
								        // get name without ending (.ggb)
								        final String key = fileEntry.getName()
								                .substring(
								                        0,
								                        fileEntry.getName()
								                                .indexOf("."));
								        readMetaData(
								                key,
								                dontCreateIfNotExist,
								                new Callback<String, FileError>() {

									                @Override
									                public void onSuccess(
									                        String result) {
																final Material mat = JSONParserGGT
										                        .parseMaterial(result);
																mat.setLocalID(
																		MaterialsManager
										                        .getIDFromKey(key));

										                sync(mat, events);
									                }

									                @Override
									                public void onFailure(
									                        FileError reason) {
										                ignoreNotSyncedFile(events);
																Log.debug(
																		"Could not read meta data");
									                }

								                });
							        } else {
								        ignoreNotSyncedFile(events);
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

	@Override
	public void autoSave(int counter) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getAutosaveJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void restoreAutoSavedFile(String json) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteAutoSavedFile() {
		// TODO Auto-generated method stub
	}

	@Override
	public void saveLoggedOut(App app1) {
		((DialogManagerW) app1.getDialogManager()).showSaveDialog();
	}

	@Override
	public void setTubeID(String localID, Material mat) {
		createMetaData(localID, mat, null);
	}

	@Override
    public void openMaterial(final Material material) {
		getBase64(getFileKey(material),
		        new Callback<String, FileError>() {

			@Override
			public void onFailure(FileError reason) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(String result) {
				material.setBase64(result);
				doOpenMaterial(material);

			}
		});
	}

	private void getBase64(final String fileKey,
	        final Callback<String, FileError> cb) {
		getGgbDir(new Callback<DirectoryEntry, FileError>() {

			@Override
			public void onFailure(FileError reason) {
				Log.error(fileKey + " not found");

			}

			@Override
			public void onSuccess(DirectoryEntry result) {
				result.getFile(fileKey + FileManagerT.FILE_EXT,
				        dontCreateIfNotExist,
				        new FileCallback<FileEntry, FileError>() {

					        @Override
					        public void onSuccess(FileEntry entry) {
						        readFile(entry, cb);

					        }

					        @Override
					        public void onFailure(FileError error) {
						        Log.error(fileKey + " not readable");

					        }
				        });

			}
		});

	}

	/**
	 * @param entry {@link FileEntry}
	 * @param cb {@link Callback} to get content of file as String
	 */
	protected void readFile(FileEntry entry, final Callback<String, FileError> cb) {
		entry.getFile(new FileCallback<FileObject, FileError>() {

			@Override
			public void onSuccess(FileObject fileObject) {
				final FileReader reader = PhoneGapManager.getPhoneGap()
				        .getFile().createReader();

				reader.setOnloadCallback(new ReaderCallback<FileReader>() {
					@Override
					public void onCallback(FileReader result) {
						cb.onSuccess(result.getResult());
					}
				});

				reader.setOnErrorCallback(new ReaderCallback<FileReader>() {
					@Override
					public void onCallback(FileReader result) {
						cb.onFailure(result.getError());
					}
				});
				reader.readAsText(fileObject);
			}

			@Override
			public void onFailure(FileError error) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * @param m {@link Material}
	 */
	protected void doOpenMaterial(Material m) {
		super.openMaterial(m);
	}
	
	/**
	 * @param m {@link Material}
	 */
	protected void doUpload(Material m) {
		super.upload(m);
	}

	@Override
	protected void updateFile(final String key, final long modified,
	        final Material material) {
		if (key == null) {
			this.createID(new Callback<Integer, String>() {

				@Override
				public void onFailure(String reason) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSuccess(Integer id) {
					String key2 = MaterialsManager.createKeyString(id,
					        material.getTitle());
					updateFile(key2, modified, material);
				}
			});
			return;
		}
		getGgbFile(key + FILE_EXT, createIfNotExist,
				new Callback<FileEntry, FileError>() {

					@Override
					public void onSuccess(final FileEntry ggbFile) {
						ggbFile.createWriter(
								new FileCallback<FileWriter, FileError>() {

									@Override
									public void onSuccess(
											final FileWriter writer) {
										material.setModified(modified);
										writeFile(writer, material, key);
									}

									@Override
									public void onFailure(
											final FileError error) {
										Log.error("Cannot write to file" + key
												+ ", error "
												+ error.getErrorCode());
									}
								});
					}

					@Override
					public void onFailure(final FileError error) {
						Log.error("Cannot create file" + key + ", error "
								+ error.getErrorCode());
					}

				});
	}

	/**
	 * @param writer
	 *            writer
	 * @param material
	 *            material
	 * @param key
	 *            file key
	 */
	protected void writeFile(FileWriter writer, Material material, String key) {
		writer.write(material.getBase64());
		material.setLocalID(MaterialsManager.getIDFromKey(key));
		String newKey = MaterialsManager.createKeyString(material.getLocalID(),
				material.getTitle());
		if (key.equals(newKey)) {
			createMetaData(key, material, null);
		} else {
			String newTitle = material.getTitle();
			Log.debug("incoming rename " + newTitle);
			material.setTitle(MaterialsManager.getTitleFromKey(key));
			material.setSyncStamp(material.getModified());
			rename(newTitle, material);
		}
	}

	@Override
	public void upload(final Material mat) {

		getBase64(getFileKey(mat), new Callback<String, FileError>() {

			@Override
			public void onSuccess(String fileID) {
				mat.setBase64(fileID);
				doUpload(mat);
			}

			@Override
			public void onFailure(FileError fe) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * convert old files (saved to localStorage) to a locale file of
	 * the device and delete them from localStorage.
	 */
	private void convertToNewFileFormat() {
		if (this.stockStore == null || this.stockStore.getLength() <= 0) {
			return;
		}

		for (int i = 0; i < this.stockStore.getLength(); i++) {
			final String key = this.stockStore.key(i);
			if (!key.startsWith(OLD_FILE_PREFIX)) {
				break;
			}
			final String keyStem = key.substring(OLD_FILE_PREFIX.length());
			final String base64 = this.stockStore.getItem(key);
			createID(new Callback<Integer, String>() {

				@Override
				public void onSuccess(Integer id) {
					final String keyString = MaterialsManager
							.createKeyString(id,
					        keyStem);
					getGgbFile(keyString + FILE_EXT, createIfNotExist,
							new Callback<FileEntry, FileError>() {

						@Override
						public void onSuccess(final FileEntry ggbFile) {
							ggbFile.createWriter(new FileCallback<FileWriter, FileError>() {

								@Override
								public void onSuccess(final FileWriter writer) {
									writer.write(base64);
									        createMetaData(
									                keyString,
									                createMaterial("",
											                System.currentTimeMillis() / 1000),
									                null);
									stockStore.removeItem(OLD_FILE_PREFIX + keyStem);
									stockStore.removeItem(OLD_META_PREFIX + keyStem);
									stockStore.removeItem(OLD_THUMB_PREFIX + keyStem);
								}

								@Override
								public void onFailure(final FileError error) {
									//
								}
							});
						}

						@Override
						public void onFailure(final FileError error) {
							//
						}

					});
				}

				@Override
				public void onFailure(String reason) {
					// TODO Auto-generated method stub
				}
			});
		}
	}
	
	@Override
	public void export(final App app1) {
		((AppW) app1).getGgbApi().getBase64(true, new AsyncOperation<String>() {

			@Override
			public void callback(String s) {
				nativeShare(s, app1.getExportTitle());
			}
		});
	}

	@Override
	public void showExportAsPictureDialog(String url, String filename,
			String extension, String titleKey, App appW) {

		exportImage(url, filename, extension);
		// TODO check if it really happened
		appW.dispatchEvent(
				new Event(EventType.EXPORT, null, "[\"" + extension + "\"]"));
	}

	@Override
	public boolean hasBase64(Material material) {
		return true;
	}

	@Override
	public void refreshAutosaveTimestamp() {
		// TODO Auto-generated method stub

	}
}