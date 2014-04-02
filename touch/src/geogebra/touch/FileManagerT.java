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

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.RootPanel;
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

public class FileManagerT {
	private static final String META_PREFIX = "meta_";
	private static final String GGB_DIR = "GeoGebra";
	private static final String META_DIR = "meta";
	private static final String FILE_EXT = ".ggb";

	boolean hasFile = false;
	String data;
	PhoneGap phonegap;

	public FileManagerT() {
		this.phonegap = TouchEntryPoint.getPhoneGap();
	}

	public void delete(final Material mat) {
		final String consTitle = mat.getURL();
		this.phonegap.getFile().requestFileSystem(
				FileSystem.LocalFileSystem_PERSISTENT, 0,
				new FileCallback<FileSystem, FileError>() {

					@Override
					public void onSuccess(final FileSystem entry) {
						final DirectoryEntry directoryEntry = entry.getRoot();

						/**
						 * Get directory GeoGebra, create it, if it doesn't
						 * exist (Flags(true, false)).
						 */
						directoryEntry.getDirectory(GGB_DIR, new Flags(true,
								false),
								new FileCallback<DirectoryEntry, FileError>() {

									@Override
									public void onSuccess(
											final DirectoryEntry ggbDir) {
										ggbDir.getFile(
												consTitle + FILE_EXT,
												new Flags(false, false),
												new FileCallback<FileEntry, FileError>() {

													@Override
													public void onSuccess(
															final FileEntry ggbFile) {
														ggbFile.remove(new FileCallback<Boolean, FileError>() {

															@Override
															public void onSuccess(
																	final Boolean entryDeleted) {
																TouchEntryPoint
																		.getBrowseGUI()
																		.removeFromLocalList(
																				mat);
																/**
																 * remove
																 * metadata only
																 * if the
																 * ggb-file was
																 * removed
																 * successfully
																 */
																ggbDir.getDirectory(
																		META_DIR,
																		new Flags(
																				true,
																				false),
																		new FileCallback<DirectoryEntry, FileError>() {

																			@Override
																			public void onSuccess(
																					final DirectoryEntry metaDir) {
																				deleteMetaData(
																						metaDir,
																						consTitle);
																			}

																			@Override
																			public void onFailure(
																					final FileError error) {

																			}
																		});
															}

															@Override
															public void onFailure(
																	final FileError error) {
															}
														});
													}

													@Override
													public void onFailure(
															final FileError error) {
													}
												});
									}

									@Override
									public void onFailure(final FileError error) {
									}
								});
					}

					@Override
					public void onFailure(final FileError error) {
					}
				});

	}

	void deleteMetaData(final DirectoryEntry metaEntry, final String title) {
		metaEntry.getFile(META_PREFIX + title, new Flags(false, false),
				new FileCallback<FileEntry, FileError>() {

					@Override
					public void onSuccess(final FileEntry fileEntry) {
						fileEntry
								.remove(new FileCallback<Boolean, FileError>() {

									@Override
									public void onSuccess(
											final Boolean entryDeleted) {
									}

									@Override
									public void onFailure(final FileError error) {
									}
								});
					}

					@Override
					public void onFailure(final FileError error) {
					}
				});
	}

	public void getAllFiles() {
		this.getFiles(MaterialFilter.getUniversalFilter());
	}

	String filename;
	int count;

	void getDefaultConstructionTitle(final Localization loc,
			final Callback<String, String> callback) {
		this.count = 1;
		this.filename = loc.getPlain("UntitledA", this.count + "");

		this.phonegap.getFile().requestFileSystem(
				FileSystem.LocalFileSystem_PERSISTENT, 0,
				new FileCallback<FileSystem, FileError>() {

					@Override
					public void onSuccess(final FileSystem entry) {
						final DirectoryEntry directoryEntry = entry.getRoot();

						/**
						 * Get directory GeoGebra, create it, if it doesn't
						 * exist (Flags(true, false)).
						 */
						directoryEntry.getDirectory(GGB_DIR, new Flags(true,
								false),
								new FileCallback<DirectoryEntry, FileError>() {

									@Override
									public void onSuccess(
											final DirectoryEntry ggbDir) {
										final DirectoryReader directoryReader = ggbDir
												.createReader();
										directoryReader
												.readEntries(new FileCallback<LightArray<EntryBase>, FileError>() {

													@Override
													public void onSuccess(
															final LightArray<EntryBase> entries) {
														for (int i = 0; i < entries
																.length(); i++) {
															if (entries.get(i)
																	.isFile()) {
																final FileEntry fileEntry = entries
																		.get(i)
																		.getAsFileEntry();
																// get filename
																// without
																// '.ggb'
																final String name = fileEntry
																		.getName()
																		.substring(
																				0,
																				fileEntry
																						.getName()
																						.indexOf(
																								"."));
																if (name.equals(FileManagerT.this.filename)) {
																	FileManagerT.this.count++;
																	FileManagerT.this.filename = loc
																			.getPlain(
																					"UntitledA",
																					FileManagerT.this.count
																							+ "");
																}
															}
														}
														callback.onSuccess(FileManagerT.this.filename);
													}

													@Override
													public void onFailure(
															final FileError error) {

													}
												});
									}

									@Override
									public void onFailure(final FileError error) {
										// TODO "Directory GeoGebra not found"
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
	 * 
	 * @param filter
	 * @param callback
	 */
	private void getFiles(final MaterialFilter filter) {
		this.phonegap.getFile().requestFileSystem(
				FileSystem.LocalFileSystem_PERSISTENT, 0,
				new FileCallback<FileSystem, FileError>() {

					@Override
					public void onSuccess(final FileSystem entry) {
						final DirectoryEntry directoryEntry = entry.getRoot();

						/**
						 * Get directory GeoGebra, create it, if it doesn't
						 * exist (Flags(true, false)).
						 */
						directoryEntry.getDirectory(GGB_DIR, new Flags(true,
								false),
								new FileCallback<DirectoryEntry, FileError>() {

									@Override
									public void onSuccess(
											final DirectoryEntry ggbDir) {
										final DirectoryReader directoryReader = ggbDir
												.createReader();
										directoryReader
												.readEntries(new FileCallback<LightArray<EntryBase>, FileError>() {

													@Override
													public void onSuccess(
															final LightArray<EntryBase> entries) {
														for (int i = 0; i < entries
																.length(); i++) {
															final EntryBase entryBase = entries
																	.get(i);
															if (entryBase
																	.isFile()) {
																final FileEntry fileEntry = entryBase
																		.getAsFileEntry();
																// get name
																// without
																// ending (.ggb)
																final String name = fileEntry
																		.getName()
																		.substring(
																				0,
																				fileEntry
																						.getName()
																						.indexOf(
																								"."));
																ggbDir.getDirectory(
																		META_DIR,
																		new Flags(
																				true,
																				false),
																		new FileCallback<DirectoryEntry, FileError>() {

																			@Override
																			public void onSuccess(
																					final DirectoryEntry metaDir) {
																				metaDir.getFile(
																						META_PREFIX
																								+ name,
																						new Flags(
																								false,
																								false),
																						new FileCallback<FileEntry, FileError>() {

																							@Override
																							public void onSuccess(
																									final FileEntry metaFile) {
																								final FileReader reader = TouchEntryPoint
																										.getPhoneGap()
																										.getFile()
																										.createReader();
																								// callback
																								// -
																								// only
																								// if
																								// there
																								// was
																								// no
																								// error
																								reader.setOnloadCallback(new ReaderCallback<FileReader>() {

																									@Override
																									public void onCallback(
																											final FileReader result) {
																										Material mat = JSONparserGGT
																												.parseMaterial(result
																														.getResult());
																										if (mat == null) {
																											mat = new Material(
																													0,
																													MaterialType.ggb);
																											mat.setTitle(name);
																										}
																										if (filter
																												.check(mat)) {
																											mat.setURL(name);
																											TouchEntryPoint
																													.getBrowseGUI()
																													.addToLocalList(
																															mat);
																										}

																									}
																								});
																								reader.readAsText(metaFile);
																							}

																							@Override
																							public void onFailure(
																									final FileError error) {
																							}
																						});
																			}

																			@Override
																			public void onFailure(
																					final FileError error) {
																			}
																		});
															}
														}
													}

													@Override
													public void onFailure(
															final FileError error) {
													}
												});
									}

									@Override
									public void onFailure(final FileError error) {
									}
								});
					}

					@Override
					public void onFailure(final FileError error) {
					}
				});
	}

	public void getMaterial(final Material material, final AppWeb app) {
		if (material.getId() > 0) {
			// remote material
			new View(RootPanel.getBodyElement(), app)
					.processFileName("http://www.geogebratube.org/files/material-"
							+ material.getId() + FILE_EXT);
			app.setUnsaved();
		} else {
			((TouchApp) app).setConstructionTitle(material.getTitle());
			this.getFileData(material.getURL(), app);
		}

	}

	/**
	 * Checks if the file with given filename is already saved on the device.
	 * 
	 * @param filename
	 *            String
	 */
	public void hasFile(final String filename,
			final Callback<Boolean, Boolean> callback) {
		this.hasFile = false;
		this.phonegap.getFile().requestFileSystem(
				FileSystem.LocalFileSystem_PERSISTENT, 0,
				new FileCallback<FileSystem, FileError>() {

					@Override
					public void onSuccess(final FileSystem entry) {
						final DirectoryEntry directoryEntry = entry.getRoot();

						/**
						 * Get directory GeoGebra, create it, if it doesn't
						 * exist (Flags(true, false)).
						 */
						directoryEntry.getDirectory(GGB_DIR, new Flags(true,
								false),
								new FileCallback<DirectoryEntry, FileError>() {

									@Override
									public void onSuccess(
											final DirectoryEntry ggbDir) {
										final DirectoryReader directoryReader = ggbDir
												.createReader();
										directoryReader
												.readEntries(new FileCallback<LightArray<EntryBase>, FileError>() {

													@Override
													public void onSuccess(
															final LightArray<EntryBase> entries) {
														for (int i = 0; i < entries
																.length(); i++) {
															if (entries.get(i)
																	.isFile()) {
																final FileEntry fileEntry = entries
																		.get(i)
																		.getAsFileEntry();
																final String name = fileEntry
																		.getName()
																		.substring(
																				0,
																				fileEntry
																						.getName()
																						.indexOf(
																								"."));
																if (name.equals(filename)) {
																	FileManagerT.this.hasFile = true;
																	break;
																}
															}
														}
														callback.onSuccess(FileManagerT.this.hasFile);
													}

													@Override
													public void onFailure(
															final FileError error) {

													}
												});
									}

									@Override
									public void onFailure(final FileError error) {
										// TODO "Directory GeoGebra not found"
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
	 * Save the active file (with metaData) on the device in directory
	 * "GeoGebra".
	 * 
	 * @param app
	 */
	public void saveFile(final App app) {
		final String consTitle = app.getKernel().getConstruction().getTitle();
		final StringHandler base64saver = new StringHandler() {
			@Override
			public void handle(final String s) {
				FileManagerT.this.phonegap.getFile().requestFileSystem(
						FileSystem.LocalFileSystem_PERSISTENT, 0,
						new FileCallback<FileSystem, FileError>() {

							@Override
							public void onSuccess(final FileSystem entry) {
								final DirectoryEntry directoryEntry = entry
										.getRoot();

								/**
								 * Get directory GeoGebra, create it, if it
								 * doesn't exist (Flags(true, false)).
								 */
								directoryEntry
										.getDirectory(
												GGB_DIR,
												new Flags(true, false),
												new FileCallback<DirectoryEntry, FileError>() {

													@Override
													public void onSuccess(
															final DirectoryEntry ggbDir) {
														// use flags true,
														// false, to overwrite
														// existing files
														ggbDir.getFile(
																consTitle
																		+ FILE_EXT,
																new Flags(true,
																		false),
																new FileCallback<FileEntry, FileError>() {

																	@Override
																	public void onSuccess(
																			final FileEntry ggbFile) {
																		ggbFile.createWriter(new FileCallback<FileWriter, FileError>() {

																			@Override
																			public void onSuccess(
																					final FileWriter writer) {
																				writer.write(s);
																			}

																			@Override
																			public void onFailure(
																					final FileError error) {
																				app.showError("WriteFileFailed");
																			}
																		});
																	}

																	@Override
																	public void onFailure(
																			final FileError error) {
																		app.showError("WriteFileFailed");
																	}
																});
														createMetaData(ggbDir,
																consTitle, app);
													}

													@Override
													public void onFailure(
															final FileError error) {
														// TODO
														// "Directory GeoGebra not found"
													}
												});

							}

							@Override
							public void onFailure(final FileError error) {
								// TODO
							}
						});
			}
		};

		((geogebra.html5.main.GgbAPIW) app.getGgbApi()).getBase64(true,
				base64saver);
		app.setSaved();
		((TouchApp) app).approveFileName();
	}

	/**
	 * 
	 * @param title
	 *            of file
	 */
	private void getFileData(final String title, final App app) {
		this.phonegap.getFile().requestFileSystem(
				FileSystem.LocalFileSystem_PERSISTENT, 0,
				new FileCallback<FileSystem, FileError>() {

					@Override
					public void onSuccess(final FileSystem entry) {
						final DirectoryEntry directoryEntry = entry.getRoot();

						/**
						 * Get directory GeoGebra, create it, if it doesn't
						 * exist (Flags(true, false)).
						 */
						directoryEntry.getDirectory(GGB_DIR, new Flags(true,
								false),
								new FileCallback<DirectoryEntry, FileError>() {

									@Override
									public void onSuccess(
											final DirectoryEntry ggbDir) {
										ggbDir.getFile(
												title + FILE_EXT,
												new Flags(false, false),
												new FileCallback<FileEntry, FileError>() {

													@Override
													public void onSuccess(
															final FileEntry ggbFile) {
														final FileReader reader = TouchEntryPoint
																.getPhoneGap()
																.getFile()
																.createReader();

														// callback - only if
														// there was no error
														reader.setOnloadCallback(new ReaderCallback<FileReader>() {

															@Override
															public void onCallback(
																	final FileReader result) {
																app.getGgbApi()
																		.setBase64(
																				result.getResult());
															}
														});
														reader.readAsText(ggbFile);
													}

													@Override
													public void onFailure(
															final FileError error) {
														// TODO Auto-generated
														// method stub

													}
												});
									}

									@Override
									public void onFailure(final FileError error) {
										// TODO "Directory GeoGebra not found"
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
	 * create metaData including meta and thumbnail.
	 * 
	 * @param ggbDir
	 * @param title
	 * @param app
	 */
	void createMetaData(final DirectoryEntry ggbDir, final String title,
			final App app) {
		ggbDir.getDirectory(META_DIR, new Flags(true, false),
				new FileCallback<DirectoryEntry, FileError>() {

					@Override
					public void onSuccess(final DirectoryEntry metaDir) {
						metaDir.getFile(META_PREFIX + title, new Flags(true,
								false),
								new FileCallback<FileEntry, FileError>() {

									@Override
									public void onSuccess(final FileEntry entry) {
										entry.createWriter(new FileCallback<FileWriter, FileError>() {

											@Override
											public void onSuccess(
													final FileWriter writer) {
												// extract metadata
												final Material mat = new Material(
														0, MaterialType.ggb);
												mat.setTimestamp(System
														.currentTimeMillis() / 1000);
												mat.setTitle(title);
												mat.setDescription(app
														.getKernel()
														.getConstruction()
														.getWorksheetText(0));
												mat.setThumbnail(((EuclidianViewWeb) app
														.getEuclidianView1())
														.getCanvasBase64WithTypeString());
												writer.write(mat.toJson()
														.toString());
											}

											@Override
											public void onFailure(
													final FileError error) {
												// TODO Auto-generated method
												// stub

											}
										});
									}

									@Override
									public void onFailure(final FileError error) {
										// TODO Auto-generated method stub

									}
								});
					}

					@Override
					public void onFailure(final FileError error) {
						// TODO Auto-generated method stub

					}
				});
	}

	public void search(final String query) {
		this.getFiles(MaterialFilter.getSearchFilter(query));
	}
}
