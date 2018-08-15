package org.geogebra.web.full.gui.util;

import java.util.ArrayList;

import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.main.MaterialsManager;
import org.geogebra.common.main.SaveController.SaveListener;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.Material.Provider;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.full.gui.browser.BrowseResources;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

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

/**
 * Dialog for online saving (tube/drive)
 */
public class SaveDialogW extends DialogBoxW implements PopupMenuHandler,
		SaveListener, EventRenderable {

	private static final int MAX_TITLE_LENGTH = 60;
	/** application */
	protected AppW app;
	/** title box */
	protected TextBox title;
	private StandardButton dontSaveButton;
	private StandardButton saveButton;
	private Label titleLabel;
	private final static int MIN_TITLE_LENGTH = 1;
	// SaveCallback saveCallback;
	private PopupMenuButtonW providerPopup;
	private FlowPanel buttonPanel;
	private ListBox listBox;

	private ArrayList<Material.Provider> supportedProviders = new ArrayList<>();
	private MaterialVisibility defaultVisibility;
	private Localization loc;

	/**
	 * Creates a new GeoGebra save dialog.
	 * 
	 * @param app
	 *            AppW
	 */
	public SaveDialogW(final AppW app) {
		super(app.getPanel(), app);
		this.defaultVisibility = app.isWhiteboardActive() ? MaterialVisibility.Private
				: MaterialVisibility.Shared;
		this.app = app;
		this.loc = app.getLocalization();
		this.addStyleName("GeoGebraFileChooser");
		this.setGlassEnabled(true);
		FlowPanel contentPanel = new FlowPanel();
		this.add(contentPanel);

		this.getCaption().setText(loc.getMenu("Save"));
		VerticalPanel p = new VerticalPanel();
		p.add(getTitelPanel());
		p.add(getButtonPanel());
		contentPanel.add(p);
		addCancelButton();

		this.addCloseHandler(new CloseHandler<GPopupPanel>() {

			@Override
			public void onClose(final CloseEvent<GPopupPanel> event) {
				handleClose();
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

	/**
	 * Handle dialog closed (escape or cancel)
	 */
	protected void handleClose() {
		app.setDefaultCursor();
		dontSaveButton.setEnabled(true);
		app.closePopupsNoTooltips();
	}

	private HorizontalPanel getTitelPanel() {
		final HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.titleLabel = new Label(loc.getMenu("Title") + ": ");
		if (app.has(Feature.DIALOG_DESIGN)) {
			titleLabel.addStyleName("coloredLabel");
		}
		titlePanel.add(this.titleLabel);
		titlePanel.add(title = new GTextBox());
		title.setMaxLength(MAX_TITLE_LENGTH);
		title.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(final KeyUpEvent event) {
				handleKeyUp(event);
			}
		});

		titlePanel.addStyleName("titlePanel");
		return titlePanel;
	}

	/**
	 * @param event
	 *            key event
	 */
	protected void handleKeyUp(KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER
				&& saveButton.isEnabled()) {
			onSave();
		} else if (title.getText().length() < MIN_TITLE_LENGTH) {
			saveButton.setEnabled(false);
		} else {
			saveButton.setEnabled(true);
		}

	}

	private FlowPanel getButtonPanel() {
		buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("buttonPanel");

		buttonPanel.add(
				dontSaveButton = new StandardButton(loc.getMenu("DontSave"),
						app));
		buttonPanel
				.add(saveButton = new StandardButton(loc.getMenu("Save"), app));

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
		providerPopup = new PopupMenuButtonW(app, ImageOrText.convert(
		        providerImages, 24), 1, providerCount,
				SelectionTable.MODE_ICON,
				app.isUnbundledOrWhiteboard());
		this.providerPopup.getMyPopup().addStyleName("providersPopup");

		listBox = new ListBox();
		listBox.addStyleName("visibility");
		listBox.addItem(loc.getMenu("Private"));
		listBox.addItem(loc.getMenu("Shared"));
		listBox.addItem(loc.getMenu("Public"));
		listBox.setItemSelected(MaterialVisibility.Private.getIndex(), true);
		if (app.getLAF().supportsGoogleDrive()) {
			providerPopup.addPopupHandler(this);
			providerPopup.setSelectedIndex(app.getFileManager()
			        .getFileProvider() == Provider.GOOGLE ? 1 : 0);
		} else if (app.getLAF().supportsLocalSave()) {
			providerPopup.addPopupHandler(this);
			providerPopup.setSelectedIndex(app.getFileManager()
			        .getFileProvider() == Provider.LOCAL ? 1 : 0);
		}
		providerPopup.getElement().getStyle()
		        .setPosition(com.google.gwt.dom.client.Style.Position.ABSOLUTE);
		providerPopup.getElement().getStyle().setLeft(10, Unit.PX);
		if (!app.isWhiteboardActive()) {
			buttonPanel.add(providerPopup);
			buttonPanel.add(listBox);
		}
	}

	/**
	 * <li>if user is offline, save local.</li>
	 * 
	 * <li>if user is online and has chosen GOOGLE, {@code uploadToDrive}</li>
	 * 
	 * <li>material was already saved as PUBLIC or SHARED, than only update
	 * (API)</li>
	 * 
	 * <li>material is new or was private, than link to GGT</li>
	 */
	public void onSave() {
		app.getSaveController().saveAs(title.getText(), getSelectedVisibility(), this);
	}

	private MaterialVisibility getSelectedVisibility() {
		switch (listBox.getSelectedIndex()) {
		case 1:
			return MaterialVisibility.Shared;
		case 2:
			return MaterialVisibility.Public;
		case 0:
		default:
			return MaterialVisibility.Private;
		}

	}
	/**
	 * sets the application as "saved" and closes the dialog
	 */
	protected void onDontSave() {
		hide();
		app.getSaveController().cancel();
	}

	@Override
	public void show() {
		this.setAnimationEnabled(false);
		super.show();
		app.invokeLater(new Runnable() {
			@Override
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
						.equals(MaterialVisibility.Public.getToken())) {
					this.listBox.setSelectedIndex(MaterialVisibility.Public.getIndex());
				} else if (app.getActiveMaterial().getVisibility()
						.equals(MaterialVisibility.Shared.getToken())) {
					this.listBox.setSelectedIndex(MaterialVisibility.Shared.getIndex());
				} else {
					this.listBox
							.setSelectedIndex(MaterialVisibility.Private.getIndex());
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
			@Override
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
		showIfNeeded(runnable, !app.isSaved(), null);
	}

	/**
	 * @param runnable
	 *            callback
	 * @param needed
	 *            whether it's needed to save (otherwise just run callback)
	 * @param anchor
	 *            relative element
	 */
	public void showIfNeeded(Runnable runnable, boolean needed, Widget anchor) {
		if (needed && !app.getLAF().isEmbedded()) {
			app.getSaveController().setRunAfterSave(runnable);
			if (anchor == null) {
				center();
			} else {
				showRelativeTo(anchor);
			}
			position();
		} else {
			app.getSaveController().setRunAfterSave(null);
			runnable.run();
		}
	}

	/**
	 * Like center, but more to the top
	 */
	protected void position() {
		int left = (getRootPanel().getOffsetWidth() - getOffsetWidth()) >> 1;
		int top = Math.min(
				(getRootPanel().getOffsetHeight() - getOffsetHeight()) >> 1,
				100);
		setPopupPosition(Math.max(left, 0), Math.max(top, 0));

	}

	/**
	 * Sets initial title for the material to save.
	 */
	public void setTitle() {
		String consTitle = app.getKernel().getConstruction().getTitle();
		if (consTitle != null && !"".equals(consTitle)
				&& !app.getSaveController().isMacro()) {
			if (consTitle.startsWith(MaterialsManager.FILE_PREFIX)) {
				consTitle = getTitleOnly(consTitle);
			}
			this.title.setText(consTitle);
		} else {
			this.title.setText(loc.getMenu("Untitled"));
			this.title.setSelectionRange(0, this.title.getText().length());
		}
	}

	private static String getTitleOnly(String key) {
		return key.substring(key.indexOf("_", key.indexOf("_") + 1) + 1);
	}

	/**
	 * Update localization
	 */
	public void setLabels() {
		this.getCaption().setText(loc.getMenu("Save"));
		this.titleLabel.setText(loc.getMenu("Title") + ": ");
		this.dontSaveButton.setText(loc.getMenu("DontSave"));
		this.saveButton.setText(loc.getMenu("Save"));
		this.listBox.setItemText(MaterialVisibility.Private.getIndex(),
				loc.getMenu("Private"));
		this.listBox.setItemText(MaterialVisibility.Shared.getIndex(),
				loc.getMenu("Shared"));
		this.listBox.setItemText(MaterialVisibility.Public.getIndex(),
				loc.getMenu("Public"));
	}

	@Override
	public void fireActionPerformed(PopupMenuButtonW actionButton) {
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
	 * @param visibility
	 *            new default
	 * @return this
	 */
	public SaveDialogW setDefaultVisibility(MaterialVisibility visibility) {
		this.defaultVisibility = visibility;
		return this;
	}

	/**
	 * Sets material type to be saved.
	 * 
	 * @param saveType
	 *            for the dialog.
	 */
	public void setSaveType(MaterialType saveType) {
		app.getSaveController().setSaveType(saveType);
	}
}
