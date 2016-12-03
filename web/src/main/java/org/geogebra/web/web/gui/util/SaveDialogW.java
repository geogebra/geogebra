package org.geogebra.web.web.gui.util;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.Material.Provider;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.StringHandler;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.browser.BrowseResources;
import org.geogebra.web.web.gui.dialog.DialogBoxW;
import org.geogebra.web.web.main.FileManager;
import org.geogebra.web.web.move.ggtapi.models.GeoGebraTubeAPIW;
import org.geogebra.web.web.move.ggtapi.models.MaterialCallback;
import org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW;
import org.geogebra.web.web.util.SaveCallback;
import org.geogebra.web.web.util.SaveCallback.SaveState;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SaveDialogW extends DialogBoxW implements PopupMenuHandler,
        EventRenderable {

	public enum Visibility {
		Private(0, "P"), Shared(1, "S"), Public(2, "O");

		private int index;
		private String token;

		Visibility(int index, String tok) {
			this.index = index;
			this.token = tok;
		}

		int getIndex() {
			return this.index;
		}

		String getToken() {
			return this.token;
		}
	}

	private static final int MAX_TITLE_LENGTH = 60;
	protected AppW app;
	FlowPanel contentPanel;
	VerticalPanel p;
	protected TextBox title;
	StandardButton dontSaveButton;
	StandardButton saveButton;
	private Label titleLabel;
	private final static int MIN_TITLE_LENGTH = 1;
	Runnable runAfterSave;
	// SaveCallback saveCallback;
	private PopupMenuButton providerPopup;
	private FlowPanel buttonPanel;
	private ListBox listBox;
	private MaterialType saveType;
	private ArrayList<Material.Provider> supportedProviders = new ArrayList<Material.Provider>();
	private Visibility defaultVisibility = Visibility.Private;
	private Localization loc;

	// private MaterialCallback materialCB;


	/**
	 * @param app
	 *            AppW
	 * 
	 *            Creates a new GeoGebraFileChooser Window <br>
	 *            Use ((DialogManagerW) app.getDialogManager()).getSaveDialog()
	 *            to get a SaveDialog! This looks like a very "special"
	 *            Implementation of a Singleton... ->Refactor?
	 */
	public SaveDialogW(final AppW app) {
		super(app.getPanel());
		this.app = app;
		this.loc = app.getLocalization();
		this.addStyleName("GeoGebraFileChooser");
		this.setGlassEnabled(true);
		// this.saveCallback = new SaveCallback(this.app);
		this.contentPanel = new FlowPanel();
		this.add(this.contentPanel);

		this.getCaption().setText(loc.getMenu("Save"));
		this.p = new VerticalPanel();
		this.p.add(getTitelPanel());
		this.p.add(getButtonPanel());
		this.contentPanel.add(p);
		this.saveType = MaterialType.ggb;

		addCancelButton();

		this.addCloseHandler(new CloseHandler<GPopupPanel>() {

			public void onClose(final CloseEvent<GPopupPanel> event) {
				app.setDefaultCursor();
				dontSaveButton.setEnabled(true);
				title.setEnabled(true);
				app.closePopups();
			}
		});
		this.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				app.closePopups();

			}
		}, ClickEvent.getType());
		app.getLoginOperation().getView().add(this);
		if (app.getGoogleDriveOperation() != null) {
			app.getGoogleDriveOperation().initGoogleDriveApi();
		}
	}

	MaterialCallback initMaterialCB(final String base64, final boolean forked) {
		return new MaterialCallback() {

			@Override
			public void onLoaded(final List<Material> parseResponse,
			        ArrayList<Chapter> meta) {
				if (isWorksheet()) {
					if (parseResponse.size() == 1) {
						Material newMat = parseResponse.get(0);
						newMat.setThumbnailBase64(((EuclidianViewWInterface) app
								.getActiveEuclidianView())
								.getCanvasBase64WithTypeString());
						app.getKernel().getConstruction()
								.setTitle(title.getText());

						// last synchronization is equal to last modified
						app.setSyncStamp(newMat.getModified());

						newMat.setSyncStamp(newMat.getModified());

						app.updateMaterialURL(newMat.getId(),
								newMat.getSharingKeyOrId());

						app.setActiveMaterial(newMat);
						app.setSyncStamp(newMat.getModified());
						saveLocalIfNeeded(newMat.getModified(),
								forked ? SaveState.FORKED : SaveState.OK);
						// if we got there via file => new, do the file =>new
						// now
						runAfterSaveCallback();
					} else {
						resetCallback();
						saveLocalIfNeeded(getCurrentTimestamp(app),
								SaveState.ERROR);
					}
				} else {
					if (parseResponse.size() == 1) {
						SaveCallback.onSaved(app, SaveState.OK,
								isMacro());
					} else {
						SaveCallback.onSaved(app, SaveState.ERROR,
								isMacro());
					}
				}

				hide();
			}


			@Override
			public void onError(final Throwable exception) {
				Log.error("SAVE Error" + exception.getMessage());

				resetCallback();
				((GuiManagerW) app.getGuiManager()).exportGGB();
				saveLocalIfNeeded(getCurrentTimestamp(app), SaveState.ERROR);
				hide();
			}

			private void saveLocalIfNeeded(long modified, SaveState state) {
				if (isWorksheet()
						&& (app.getFileManager().shouldKeep(0)
								|| app.has(Feature.LOCALSTORAGE_FILES) || state == SaveState.ERROR)) {
					app.getKernel().getConstruction().setTitle(title.getText());
					((FileManager) app.getFileManager()).saveFile(base64,
							modified, new SaveCallback(app, state));
				} else {
					app.setSaved();
				}
			}
		};
	}

	public static long getCurrentTimestamp(AppW app) {
		return Math.max(System.currentTimeMillis() / 1000,
		        app.getSyncStamp() + 1);
	}


	private HorizontalPanel getTitelPanel() {
		final HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.titleLabel = new Label(loc.getMenu("Title") + ": ");
		titlePanel.add(this.titleLabel);
		titlePanel.add(title = new GTextBox());
		title.setMaxLength(MAX_TITLE_LENGTH);
		title.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(final KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER
				        && saveButton.isEnabled()) {
					onSave();
				} else if (title.getText().length() < MIN_TITLE_LENGTH) {
					saveButton.setEnabled(false);
				} else {
					saveButton.setEnabled(true);
				}
			}
		});

		titlePanel.addStyleName("titlePanel");
		return titlePanel;
	}

	private FlowPanel getButtonPanel() {
		buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("buttonPanel");

		buttonPanel.add(dontSaveButton = new StandardButton(app
		        .getMenu("DontSave")));
		buttonPanel.add(saveButton = new StandardButton(loc.getMenu("Save")));

		saveButton.addStyleName("saveButton");
		dontSaveButton.addStyleName("cancelBtn");
		setAvailableProviders();
		// ImageOrText[] data, Integer rows, Integer columns, GDimensionW
		// iconSize, geogebra.common.gui.util.SelectionTable mode

		saveButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				onSave();
			}
		});

		dontSaveButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				onDontSave();
			}
		});

		return buttonPanel;
	}

	private void setAvailableProviders() {
		ImageResource[] providerImages = new ImageResource[3];
		providerImages[0] = BrowseResources.INSTANCE.location_tube();
		int providerCount = 1;
		this.supportedProviders.add(Provider.TUBE);
		GeoGebraTubeUser user = app.getLoginOperation().getModel()
		        .getLoggedInUser();
		if (user != null && user.hasGoogleDrive()
		        && app.getLAF().supportsGoogleDrive()) {
			providerImages[providerCount++] = BrowseResources.INSTANCE
			        .location_drive();
			this.supportedProviders.add(Provider.GOOGLE);
		}
		if (user != null && user.hasOneDrive()) {
			providerImages[providerCount++] = BrowseResources.INSTANCE
			        .location_skydrive();
			this.supportedProviders.add(Provider.ONE);
		}
		if (app.getLAF().supportsLocalSave()) {
			providerImages[providerCount++] = BrowseResources.INSTANCE
			        .location_local();
			this.supportedProviders.add(Provider.LOCAL);
		}
		if (providerPopup != null) {
			buttonPanel.remove(providerPopup);
		}
		providerPopup = new PopupMenuButton(app, ImageOrText.convert(
		        providerImages, 24), 1, providerCount,
				SelectionTable.MODE_ICON);
		this.providerPopup.getMyPopup().addStyleName("providersPopup");

		listBox = new ListBox();
		listBox.addStyleName("visibility");
		listBox.addItem(loc.getMenu("Private"));
		listBox.addItem(loc.getMenu("Shared"));
		listBox.addItem(loc.getMenu("Public"));
		listBox.setItemSelected(Visibility.Private.getIndex(), true);
		if (app.getLAF().externalDriveSupported()) {
			providerPopup.addPopupHandler(this);
			providerPopup.setSelectedIndex(app.getFileManager()
			        .getFileProvider() == Provider.GOOGLE ? 1 : 0);
		}else if(app.getLAF().supportsLocalSave()) {
			providerPopup.addPopupHandler(this);
			providerPopup.setSelectedIndex(app.getFileManager()
			        .getFileProvider() == Provider.LOCAL ? 1 : 0);
		}
		providerPopup.getElement().getStyle()
		        .setPosition(com.google.gwt.dom.client.Style.Position.ABSOLUTE);
		providerPopup.getElement().getStyle().setLeft(10, Unit.PX);
		buttonPanel.add(providerPopup);
		buttonPanel.add(listBox);
	}

	/**
	 * if user is offline, save local. </br>if user is online and </br>- has
	 * chosen GOOGLE, {@code uploadToDrive} </br>- material was already saved as
	 * PUBLIC or SHARED, than only update (API) </br>- material is new or was
	 * private, than link to GGT
	 */
	protected void onSave() {
	   if (app.getFileManager().getFileProvider() == Provider.LOCAL) {
			app.getKernel().getConstruction().setTitle(this.title.getText());
			app.getFileManager().export(app);
		}else if (app.isOffline() || !app.getLoginOperation().isLoggedIn()) {
			saveLocal();
		} else if (app.getFileManager().getFileProvider() == Provider.GOOGLE) {
			uploadToDrive();
		}else {
			if (app.getActiveMaterial() == null
					|| isMacro()) {
				app.setActiveMaterial(new Material(0, saveType));
			}
			switch (listBox.getSelectedIndex()) {
			case 0:
				app.getActiveMaterial().setVisibility(
				        Visibility.Private.getToken());
				break;
			case 1:
				app.getActiveMaterial().setVisibility(
				        Visibility.Shared.getToken());
				break;
			case 2:
				app.getActiveMaterial().setVisibility(
				        Visibility.Public.getToken());
				break;
			default:
				app.getActiveMaterial().setVisibility(
				        Visibility.Private.getToken());
				break;
			}

			uploadToGgt(app.getActiveMaterial().getVisibility());
		}
	}

	/**
	 * sets the application as "saved" and closes the dialog
	 */
	protected void onDontSave() {
		hide();
		if (isWorksheet()) {
			app.setSaved();
			runAfterSaveCallback();
		}
	}

	private void saveLocal() {
		ToolTipManagerW.sharedInstance().showBottomMessage(
				loc.getMenu("Saving"), false, app);
		if (!this.title.getText().equals(
		        app.getKernel().getConstruction().getTitle())) {
			app.resetUniqueId();
			app.setLocalID(-1);
		}
		app.getKernel().getConstruction().setTitle(this.title.getText());
		app.getGgbApi().getBase64(true, new StringHandler() {

			@Override
			public void handle(String s) {
				((FileManager) app.getFileManager()).saveFile(s,
				        getCurrentTimestamp(app), new SaveCallback(app,
				                SaveState.OK) {
					        @Override
					        public void onSaved(final Material mat,
					                final boolean isLocal) {
						        super.onSaved(mat, isLocal);
						        runAfterSaveCallback();
					        }
				        });
				hide();
			}
		});

	}



	/**
	 * @return true if material was already public or shared
	 */
	private boolean isAlreadyPublicOrShared() {
		return app.getActiveMaterial().getVisibility()
		        .equals(Visibility.Public.getToken())
		        || app.getActiveMaterial().getVisibility()
		                .equals(Visibility.Shared.getToken());
	}

	/**
	 * Handles the upload of the file and closes the dialog. If there are
	 * sync-problems with a file, a new one is generated on ggt.
	 */
	private void uploadToGgt(final String visibility) {

		final StringHandler handler = new StringHandler() {
			@Override
			public void handle(String base64) {
				if (!SaveDialogW.this.title.getText().equals(
						app.getKernel().getConstruction().getTitle())
						&& isWorksheet()) {
					Log.debug("SAVE filename changed");
					app.updateMaterialURL(0, null);
					doUploadToGgt(app.getTubeId(), visibility, base64,
					        initMaterialCB(base64, false));
				} else if (app.getTubeId() == 0
						|| isMacro()) {
					Log.debug("SAVE had no Tube ID or tool is saved");
					doUploadToGgt(0, visibility, base64,
					        initMaterialCB(base64, false));
				} else {
					handleSync(base64, visibility);
				}

			}

		};

		ToolTipManagerW.sharedInstance().showBottomMessage(
				loc.getMenu("Saving"), false, app);

		if (saveType == MaterialType.ggt) {
			app.getGgbApi().getMacrosBase64(true, handler);
		} else {
			app.getGgbApi().getBase64(true, handler);
		}

		hide();
	}

	private void uploadToDrive() {
		ToolTipManagerW.sharedInstance().showBottomMessage(
				loc.getMenu("Saving"), false, app);
		app.getGoogleDriveOperation().afterLogin(new Runnable() {

			@Override
			public void run() {
				doUploadToDrive();
			}
		});
	}

	/**
	 * GoogleDrive upload
	 */
	void doUploadToDrive() {
		String saveName = this.title.getText();
		String prefix = saveType == MaterialType.ggb ? ".ggb" : ".ggt";
		if (!saveName.endsWith(prefix)) {
			app.getKernel().getConstruction().setTitle(saveName);
			saveName += prefix;
		} else {
			app.getKernel()
			        .getConstruction()
			        .setTitle(
			                saveName.substring(0,
			                        saveName.length() - prefix.length()));
		}
		JavaScriptObject callback = ((GoogleDriveOperationW) app
		        .getGoogleDriveOperation()).getPutFileCallback(saveName,
						"GeoGebra", saveType == MaterialType.ggb);
		if (saveType == MaterialType.ggt) {
			app.getGgbApi().getMacrosBase64(true, callback);
		} else {
			app.getGgbApi().getBase64(true, callback);
		}
	}

	void handleSync(final String base64, final String visibility) {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI())
		        .getItem(app.getTubeId() + "", new MaterialCallback() {

			        @Override
			        public void onLoaded(final List<Material> parseResponse,
			                ArrayList<Chapter> meta) {
				        MaterialCallback materialCallback;
				        if (parseResponse.size() == 1) {
					        if (parseResponse.get(0).getModified() > app
					                .getSyncStamp()) {
								Log.debug("SAVE MULTIPLE"
						                + parseResponse.get(0).getModified()
						                + ":" + app.getSyncStamp());
								app.updateMaterialURL(0, null);
						        materialCallback = initMaterialCB(base64, true);
					        } else {
						        materialCallback = initMaterialCB(base64, false);
					        }
					        doUploadToGgt(app.getTubeId(), visibility, base64,
					                materialCallback);
				        } else {
					        // if the file was deleted meanwhile
							// (parseResponse.size() == 0)
					        app.resetUniqueId();
					        materialCallback = initMaterialCB(base64, false);
					        doUploadToGgt(app.getTubeId(), visibility, base64,
					                materialCallback);
				        }
			        }

			        @Override
			        public void onError(final Throwable exception) {
				        // TODO show correct message
				        app.showError("Error");
			        }
		        });
	}

	/**
	 * does the upload of the actual opened file to GeoGebraTube
	 * 
	 * @param materialCallback
	 *            {@link MaterialCallback}
	 */
	void doUploadToGgt(int tubeID, String visibility, String base64,
	        MaterialCallback materialCallback) {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI())
				.uploadMaterial(tubeID, visibility, this.title.getText(),
						base64, materialCallback, this.saveType);
	}

	@Override
	public void show() {
		this.setAnimationEnabled(false);
		super.show();
		app.getGuiManager().invokeLater(new Runnable() {
			public void run() {
				position();
			}
		});
		
		this.setTitle();
		if (app.isOffline()) {
			this.providerPopup.setVisible(this.supportedProviders
					.contains(Provider.LOCAL));
		} else {
			this.providerPopup.setVisible(true);
			this.providerPopup.setSelectedIndex(this.supportedProviders
					.indexOf(app.getFileManager().getFileProvider()));
			// app.getFileManager().setFileProvider(
			// org.geogebra.common.move.ggtapi.models.Material.Provider.TUBE);
			if (app.getActiveMaterial() != null) {
				if (app.getActiveMaterial().getVisibility()
				        .equals(Visibility.Public.getToken())) {
					this.listBox.setSelectedIndex(Visibility.Public.getIndex());
				} else if (app.getActiveMaterial().getVisibility()
				        .equals(Visibility.Shared.getToken())) {
					this.listBox.setSelectedIndex(Visibility.Shared.getIndex());
				} else {
					this.listBox
					        .setSelectedIndex(Visibility.Private.getIndex());
				}
			} else {
				this.listBox.setSelectedIndex(defaultVisibility.getIndex());
			}
		}
		listBox.setVisible(app.getFileManager().getFileProvider() == Provider.TUBE);
		if (this.title.getText().length() < MIN_TITLE_LENGTH) {
			this.saveButton.setEnabled(false);
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				title.setFocus(true);
			}
		});
	}

	/**
	 * shows the {@link SaveDialogW} if there are unsaved changes before editing
	 * another file or creating a new one
	 * 
	 * Never shown in embedded LAF (Mix, SMART)
	 * 
	 * @param runnable
	 *            runs either after saved successfully or immediately if dialog
	 *            not needed {@link Runnable}
	 */
	public void showIfNeeded(Runnable runnable) {
		showIfNeeded(runnable, !app.isSaved());
	}

	/**
	 * @param runnable
	 *            callback
	 * @param needed
	 *            whether it's needed to save (otherwise just run callback)
	 */
	public void showIfNeeded(Runnable runnable, boolean needed) {
		if (needed && !app.getLAF().isEmbedded()) {
			runAfterSave = runnable;
			center();
			position();
		} else {
			runAfterSave = null;
			runnable.run();
		}
	}

	private void position() {
		int left = (getRootPanel().getOffsetWidth() - getOffsetWidth()) >> 1;
		int top = Math.min(
				(getRootPanel().getOffsetHeight() - getOffsetHeight()) >> 1,
				100);
		setPopupPosition(Math.max(left, 0), Math.max(top, 0));

	}

	private void setTitle() {
		String consTitle = app.getKernel().getConstruction().getTitle();
		if (consTitle != null && !consTitle.equals("")
				&& !isMacro()) {
			if (consTitle.startsWith(FileManager.FILE_PREFIX)) {
				consTitle = getTitleOnly(consTitle);
			}
			this.title.setText(consTitle);
		} else {
			this.title.setText(loc.getMenu("Untitled"));
			this.title.setSelectionRange(0, this.title.getText().length());
		}
	}

	private String getTitleOnly(String key) {
		return key.substring(key.indexOf("_", key.indexOf("_") + 1) + 1);
	}

	public void setLabels() {
		this.getCaption().setText(loc.getMenu("Save"));
		this.titleLabel.setText(loc.getMenu("Title") + ": ");
		this.dontSaveButton.setText(loc.getMenu("DontSave"));
		this.saveButton.setText(loc.getMenu("Save"));
		this.listBox.setItemText(Visibility.Private.getIndex(),
				loc.getMenu("Private"));
		this.listBox.setItemText(Visibility.Shared.getIndex(),
				loc.getMenu("Shared"));
		this.listBox.setItemText(Visibility.Public.getIndex(),
				loc.getMenu("Public"));
	}

	/**
	 * runs the callback
	 */
	public void runAfterSaveCallback() {
		if (runAfterSave != null) {
			runAfterSave.run();
			resetCallback();
		}
	}

	/**
	 * resets the callback
	 */
	void resetCallback() {
		this.runAfterSave = null;
	}

	@Override
	public void fireActionPerformed(PopupMenuButton actionButton) {
		Provider provider = this.supportedProviders.get(actionButton.getSelectedIndex());
		app.getFileManager().setFileProvider(provider);

		listBox.setVisible(provider == Provider.TUBE);

		providerPopup.getMyPopup().hide();
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent || event instanceof LogOutEvent) {
			this.setAvailableProviders();
		}
	}

	/**
	 * @param saveType
	 *            set the saveType for the SaveDialog
	 */
	public void setSaveType(MaterialType saveType) {
		this.saveType = saveType;
	}

	/**
	 * @return true if the MaterialType is ggb
	 */
	boolean isWorksheet() {
		return saveType.equals(MaterialType.ggb);
	}

	/**
	 * @return true if the MaterialType is ggt
	 */
	boolean isMacro() {
		return saveType.equals(MaterialType.ggt);
	}

	/**
	 * @param visibility
	 *            new default
	 */
	public SaveDialogW setDefaultVisibility(Visibility visibility) {
		this.defaultVisibility = visibility;
		return this;
	}
}
