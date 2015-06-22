package org.geogebra.web.web.gui.util;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SaveDialogW extends DialogBoxW implements PopupMenuHandler,
        EventRenderable {

	private enum Visibility {
		Private(0), Shared(1), Public(2);

		private int index;
		private String token;

		Visibility(int index) {
			this.index = index;
			this.token = createToken();
		}

		int getIndex() {
			return this.index;
		}

		String getToken() {
			return this.token;
		}

		private String createToken() {
			if (this.index == 2) {
				return "O";
			} else if (this.index == 1) {
				return "S";
			} else {
				return "P";
			}
		}
	}

	private final static String GGT_EDIT_URL = "http://tube.geogebra.org/material/edit/id/";
	protected AppW app;
	FlowPanel contentPanel;
	VerticalPanel p;
	protected TextBox title;
	StandardButton dontSaveButton;
	StandardButton saveButton;
	private Label titleLabel;
	private final int MIN_TITLE_LENGTH = 1;
	Runnable runAfterSave;
	// SaveCallback saveCallback;
	private PopupMenuButton providerPopup;
	private FlowPanel buttonPanel;
	private ListBox listBox;
	private MaterialType saveType;
	private ArrayList<Material.Provider> supportedProviders = new ArrayList<Material.Provider>();

	// private MaterialCallback materialCB;


	/**
	 * @param app
	 *            AppW
	 * 
	 *            Creates a new GeoGebraFileChooser Window
	 */
	public SaveDialogW(final AppW app) {
		super();
		this.app = app;
		this.addStyleName("GeoGebraFileChooser");
		this.setGlassEnabled(true);
		// this.saveCallback = new SaveCallback(this.app);
		this.contentPanel = new FlowPanel();
		this.add(this.contentPanel);

		this.getCaption().setText(app.getMenu("Save"));
		this.p = new VerticalPanel();
		this.p.add(getTitelPanel());
		this.p.add(getButtonPanel());
		this.contentPanel.add(p);
		this.saveType = MaterialType.ggb;

		addCancelButton();

		this.addCloseHandler(new CloseHandler<PopupPanel>() {

			public void onClose(final CloseEvent<PopupPanel> event) {
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
				if (parseResponse.size() == 1) {
					Material newMat = parseResponse.get(0);
					newMat.setThumbnail(((EuclidianViewWInterface) app
					        .getActiveEuclidianView())
					        .getCanvasBase64WithTypeString());
					app.getKernel().getConstruction().setTitle(title.getText());
					app.setTubeId(newMat.getId());
					// last synchronization is equal to last modified
					app.setSyncStamp(newMat.getModified());


					newMat.setSyncStamp(newMat.getModified());
					app.setTubeId(newMat.getId());
					app.setActiveMaterial(newMat);
					app.setSyncStamp(newMat.getModified());
					saveLocalIfNeeded(newMat.getModified(),
					        forked ? SaveState.FORKED : SaveState.OK);
					// if we got there via file => new, do the file =>new now
					runAfterSaveCallback();
				} else {
					resetCallback();
					saveLocalIfNeeded(getCurrentTimestamp(app), SaveState.ERROR);
				}

				hide();
			}


			@Override
			public void onError(final Throwable exception) {
				Log.error("SAVE Error" + exception.getMessage());

				resetCallback();
				((GuiManagerW) app.getGuiManager()).openFilePicker();
				saveLocalIfNeeded(getCurrentTimestamp(app), SaveState.ERROR);
				hide();
			}

			private void saveLocalIfNeeded(long modified, SaveState state) {
				if (app.getFileManager().shouldKeep(0)
						|| app.has(Feature.LOCALSTORAGE_FILES)
				        || state == SaveState.ERROR) {
					app.getKernel().getConstruction().setTitle(title.getText());
					((FileManager) app.getFileManager()).saveFile(base64,
					        modified, new SaveCallback(app, state));
				} else {
					SaveCallback.onSaved(app, state);
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
		this.titleLabel = new Label(app.getPlain("Title") + ": ");
		titlePanel.add(this.titleLabel);
		titlePanel.add(title = new GTextBox());
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
		buttonPanel.add(saveButton = new StandardButton(app.getMenu("Save")));
		saveButton.addStyleName("saveButton");
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
		        SelectionTable.MODE_IMAGE);
		this.providerPopup.getMyPopup().addStyleName("providersPopup");

		listBox = new ListBox();
		listBox.addStyleName("visibility");
		listBox.addItem(app.getMenu("Private"));
		listBox.addItem(app.getMenu("Shared"));
		listBox.addItem(app.getMenu("Public"));
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
			if (app.getActiveMaterial() == null) {
				app.setActiveMaterial(new Material(0, MaterialType.ggb));
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
		app.setSaved();
		hide();
		runAfterSaveCallback();
	}

	private void saveLocal() {
		ToolTipManagerW.sharedInstance().showBottomMessage(
				app.getMenu("Saving"), false, app);
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
				        app.getKernel().getConstruction().getTitle())) {
					App.debug("SAVE filename changed");
					app.setTubeId(0);
					doUploadToGgt(app.getTubeId(), visibility, base64,
					        initMaterialCB(base64, false));
				} else if (app.getTubeId() == 0) {
					App.debug("SAVE had no Tube ID");
					doUploadToGgt(app.getTubeId(), visibility, base64,
					        initMaterialCB(base64, false));
				} else {
					handleSync(base64, visibility);
				}

			}

		};

		ToolTipManagerW.sharedInstance().showBottomMessage(
				app.getMenu("Saving"), false, app);

		if (saveType == MaterialType.ggt) {
			app.getGgbApi().getMacrosBase64(true, handler);
		} else {
			app.getGgbApi().getBase64(true, handler);
		}

		hide();
	}

	private void uploadToDrive() {
		ToolTipManagerW.sharedInstance().showBottomMessage(
				app.getMenu("Saving"), false, app);
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
		        "GeoGebra");
		app.getGgbApi().getBase64(true, callback);
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
						        App.debug("SAVE MULTIPLE"
						                + parseResponse.get(0).getModified()
						                + ":" + app.getSyncStamp());
						        app.setTubeId(0);
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
		        .uploadMaterial(app, tubeID, visibility, this.title.getText(),
		                base64, materialCallback);
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
				this.listBox.setSelectedIndex(Visibility.Private.getIndex());
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
	 * @param newConstruction
	 *            {@link Runnable}
	 */
	public void showIfNeeded(Runnable newConstruction) {
		runAfterSave = newConstruction;
		if (!app.isSaved()) {
			center();
			position();
		} else {
			runAfterSaveCallback();
		}
	}

	private void position() {
		int left = (Window.getClientWidth() - getOffsetWidth()) >> 1;
		int top = Math.min((Window.getClientHeight() - getOffsetHeight()) >> 1,
				100);
		setPopupPosition(Math.max(Window.getScrollLeft() + left, 0),
				Math.max(Window.getScrollTop() + top, 0));

	}

	private void setTitle() {
		String consTitle = app.getKernel().getConstruction().getTitle();
		if (consTitle != null && !consTitle.equals("")) {
			if (consTitle.startsWith(FileManager.FILE_PREFIX)) {
				consTitle = getTitleOnly(consTitle);
			}
			this.title.setText(consTitle);
		} else {
			this.title.setText(app.getMenu("Untitled"));
			this.title.setSelectionRange(0, this.title.getText().length());
		}
	}

	private String getTitleOnly(String key) {
		return key.substring(key.indexOf("_", key.indexOf("_") + 1) + 1);
	}

	public void setLabels() {
		this.getCaption().setText(app.getMenu("Save"));
		this.titleLabel.setText(app.getPlain("Title") + ": ");
		this.dontSaveButton.setText(app.getMenu("DontSave"));
		this.saveButton.setText(app.getMenu("Save"));
		this.listBox.setItemText(Visibility.Private.getIndex(),
		        app.getMenu("Private"));
		this.listBox.setItemText(Visibility.Shared.getIndex(),
		        app.getMenu("Shared"));
		this.listBox.setItemText(Visibility.Public.getIndex(),
		        app.getMenu("Public"));
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

	public MaterialType getSaveType() {
		return saveType;
	}

	public void setSaveType(MaterialType saveType) {
		this.saveType = saveType;
	}
}
