package org.geogebra.web.full.gui.util;

import java.util.ArrayList;

import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.main.Localization;
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
import org.geogebra.web.full.gui.components.CompDropDown;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.SharedResources;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.style.shared.Position;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

/**
 * Dialog for online saving
 */
public class SaveDialogW extends ComponentDialog implements PopupMenuHandler,
		SaveListener, EventRenderable, SaveDialogI {

	private static final int MAX_TITLE_LENGTH = 60;
	/** title box */
	protected GTextBox title;
	private StandardButton cancelButton;

	private Label titleLabel;
	private PopupMenuButtonW providerPopup;
	private FlowPanel visibilityPanel;
	private CompDropDown visibilityDropDown;

	private ArrayList<Material.Provider> supportedProviders = new ArrayList<>();
	private Localization loc;
	private ComponentCheckbox templateCheckbox;
	private MaterialVisibilityProperty visibilityProperty;
	private MaterialVisibilityController visibilityController;

	/**
	 * Creates a new GeoGebra save dialog.
	 * @param app see {@link AppW}
	 * @param data dialog translation keys
	 */
	public SaveDialogW(final AppW app, DialogData data) {
		super(app, data, false, true);
		this.loc = app.getLocalization();
		this.addStyleName("GeoGebraFileChooser");
		visibilityController = new MaterialVisibilityController(app.getLoginOperation());
		buildContent();
		setActions();

		this.addCloseHandler(event -> handleClose());
		this.addDomHandler(event -> app.closePopups(), ClickEvent.getType());
		if (app.getLoginOperation() != null) {
			app.getLoginOperation().getView().add(this);
		}
		if (app.getGoogleDriveOperation() != null) {
			app.getGoogleDriveOperation().initGoogleDriveApi();
		}
	}

	private void setActions() {
		setOnNegativeAction(() -> app.getSaveController().dontSave());
		setOnPositiveAction(this::onSave);
	}

	private void buildContent() {
		FlowPanel contentPanel = new FlowPanel();
		contentPanel.add(getTitlePanel());
		if (app.isWhiteboardActive()) {
			contentPanel.add(getCheckboxPanel());
		}
		contentPanel.add(getVisibilityPanel());
		addCancelButton(contentPanel);

		addDialogContent(contentPanel);
	}

	/**
	 * Handle dialog closed (escape or cancel)
	 */
	protected void handleClose() {
		app.setDefaultCursor();
		((AppW) app).closePopupsNoTooltips();
	}

	private FlowPanel getTitlePanel() {
		final FlowPanel titlePanel = new FlowPanel();
		titleLabel = new Label(loc.getMenu("Title"));
		if (app.isUnbundledOrWhiteboard()) {
			titleLabel.addStyleName("coloredLabel");
		}
		titlePanel.add(titleLabel);
		titlePanel.add(title = new GTextBox());
		title.setMaxLength(MAX_TITLE_LENGTH);
		title.addKeyUpHandler(event ->
				setPosBtnDisabled(title.getText().isEmpty()));

		titlePanel.addStyleName("titlePanel");
		return titlePanel;
	}

	private FlowPanel getCheckboxPanel() {
		templateCheckbox = new ComponentCheckbox(app.getLocalization(), false,
				"saveTemplate");
		return templateCheckbox;
	}

	private FlowPanel getVisibilityPanel() {
		visibilityPanel = new FlowPanel();
		visibilityPanel.addStyleName("visibilityPanel");
		visibilityProperty = new MaterialVisibilityProperty(loc);
		visibilityDropDown = new CompDropDown((AppW) app, null,
				visibilityProperty);
		rebuildVisibilityList();
		visibilityDropDown.addStyleName("visibility");
		setAvailableProviders();

		return visibilityPanel;
	}

	private void setAvailableProviders() {
		ImageResource[] providerImages = new ImageResource[3];
		providerImages[0] = BrowseResources.INSTANCE.location_tube();
		int providerCount = 1;
		this.supportedProviders.add(Provider.TUBE);
		GeoGebraTubeUser user = null;
		if (app.getLoginOperation() != null) {
			user = app.getLoginOperation().getModel().getLoggedInUser();
		}
		if (user != null && user.hasGoogleDrive()
				&& ((AppW) app).getLAF().supportsGoogleDrive()) {
			providerImages[providerCount++] = BrowseResources.INSTANCE
			        .location_drive();
			this.supportedProviders.add(Provider.GOOGLE);
		}
		if (((AppW) app).getLAF().supportsLocalSave()) {
			providerImages[providerCount++] = BrowseResources.INSTANCE
			        .location_local();
			this.supportedProviders.add(Provider.LOCAL);
		}
		if (providerPopup != null) {
			visibilityPanel.remove(providerPopup);
		}
		providerPopup = new PopupMenuButtonW((AppW) app, ImageOrText.convert(
		        providerImages, 24), 1, providerCount,
				SelectionTable.MODE_ICON);
		this.providerPopup.getMyPopup().addStyleName("providersPopup");

		if (((AppW) app).getLAF().supportsGoogleDrive()) {
			providerPopup.addPopupHandler(this);
			providerPopup.setSelectedIndex(((AppW) app).getFileManager()
			        .getFileProvider() == Provider.GOOGLE ? 1 : 0);
		} else if (((AppW) app).getLAF().supportsLocalSave()) {
			providerPopup.addPopupHandler(this);
			providerPopup.setSelectedIndex(((AppW) app).getFileManager()
			        .getFileProvider() == Provider.LOCAL ? 1 : 0);
		}
		providerPopup.getElement().getStyle().setPosition(Position.ABSOLUTE);
		providerPopup.getElement().getStyle().setLeft(10, Unit.PX);
		visibilityPanel.add(providerPopup);
		visibilityPanel.add(visibilityDropDown);
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
		if (templateCheckbox != null) {
			setSaveType(templateCheckbox.isSelected()
					? MaterialType.ggsTemplate : MaterialType.ggs);
		}
		app.getSaveController().saveAs(title.getText(),
				visibilityController.getVisibility(visibilityDropDown.getSelectedIndex()),
				this);
	}

	@Override
	public void show() {
		this.setAnimationEnabled(false);
		super.show();

		setTitle();
		setPosBtnDisabled(false);
		if (((AppW) app).isOffline()) {
			this.providerPopup.setVisible(this.supportedProviders
					.contains(Provider.LOCAL));
		} else {
			this.providerPopup.setVisible(true);
			this.providerPopup.setSelectedIndex(this.supportedProviders
					.indexOf(((AppW) app).getFileManager().getFileProvider()));
		}
		rebuildVisibilityList();
		visibilityDropDown.setVisible(
				((AppW) app).getFileManager().getFileProvider() == Provider.TUBE);

		if (templateCheckbox != null) {
			templateCheckbox.setVisible(true);
			Material activeMaterial = ((AppW) app).getActiveMaterial();
			templateCheckbox.setSelected(activeMaterial != null && MaterialType.ggsTemplate
					.equals(activeMaterial.getType()));
		}
		Scheduler.get().scheduleDeferred(() -> {
			title.setFocus(true);
			title.selectAll();
		});
	}

	@Override
	public void setDiscardMode() {
		if (templateCheckbox != null) {
			templateCheckbox.setVisible(false);
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
	@Override
	public void setTitle() {
		boolean selection = app.getSaveController().updateSaveTitle(title,
				loc.getMenu("Untitled"));
		if (selection) {
			this.title.setSelectionRange(0, this.title.getText().length());
		}
	}

	private void rebuildVisibilityList() {
		visibilityProperty.update(visibilityController.getMaterialVisibility(
				app.getActiveMaterial()));
		visibilityDropDown.setLabels();
		visibilityDropDown.setSelectedIndex(visibilityProperty.getIndex());
	}

	@Override
	public void fireActionPerformed(PopupMenuButtonW actionButton) {
		Provider provider = this.supportedProviders.get(actionButton.getSelectedIndex());
		((AppW) app).getFileManager().setFileProvider(provider);

		visibilityDropDown.setVisible(provider == Provider.TUBE);

		providerPopup.getMyPopup().hide();
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent || event instanceof LogOutEvent) {
			setAvailableProviders();
		}
	}

	/**
	 * Sets material type to be saved.
	 * 
	 * @param saveType
	 *            for the dialog.
	 */
	@Override
	public void setSaveType(MaterialType saveType) {
		app.getSaveController().setSaveType(saveType);
	}

	/**
	 * Adds a little cross to cancel the dialog if there is already a panel
	 * attached to the Dialogbox. If the first child of the Dialogbox is not a
	 * Panel this will do nothing!
	 *
	 * Pulled up from SaveDialogW
	 */
	private void addCancelButton(FlowPanel contentPanel) {
		this.cancelButton = new StandardButton(
				SharedResources.INSTANCE.dialog_cancel());
		this.cancelButton.addStyleName("cancelSaveButton");
		this.cancelButton.addFastClickHandler(source -> {
			hide();
			app.getSaveController().cancel();
		});
		contentPanel.add(cancelButton);
	}
}