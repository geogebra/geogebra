package org.geogebra.web.full.gui.util;

import java.util.ArrayList;

import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MaterialVisibility;
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
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.FastButton;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;
import org.geogebra.web.shared.SharedResources;
import org.geogebra.web.shared.components.ComponentCheckbox;

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
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog for online saving (tube/drive)
 */
public class SaveDialogW extends DialogBoxW implements PopupMenuHandler,
		SaveListener, EventRenderable, SaveDialogI {

	private static final int MAX_TITLE_LENGTH = 60;
	/** appWlication */
	protected AppW appW;
	/** title box */
	protected GTextBox title;
	private StandardButton dontSaveButton;
	private StandardButton saveButton;
	private FastButton cancelButton;

	private Label titleLabel;
	private final static int MIN_TITLE_LENGTH = 1;
	// SaveCallback saveCallback;
	private PopupMenuButtonW providerPopup;
	private FlowPanel buttonPanel;
	private ListBox listBox;

	private ArrayList<Material.Provider> supportedProviders = new ArrayList<>();
	private Localization loc;
	private BaseWidgetFactory widgetFactory;
	private ComponentCheckbox templateCheckbox;
	private Label templateTxt;

	/**
	 * Creates a new GeoGebra save dialog.
	 * 
	 * @param app
	 *            see {@link AppW}
	 * @param factory
	 *            widget factory
	 */
	public SaveDialogW(final AppW app, BaseWidgetFactory factory) {
		super(app.getPanel(), app);
		this.widgetFactory = factory;
		this.appW = app;
		this.loc = appW.getLocalization();
		this.addStyleName("GeoGebraFileChooser");
		this.setGlassEnabled(true);
		FlowPanel contentPanel = new FlowPanel();
		this.add(contentPanel);

		this.getCaption().setText(loc.getMenu("Save"));
		VerticalPanel p = new VerticalPanel();
		p.add(getTitelPanel());
		if (app.isWhiteboardActive()) {
			p.add(getCheckboxPanel());
		}
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
				appW.closePopups();

			}
		}, ClickEvent.getType());
		if (appW.getLoginOperation() != null) {
			appW.getLoginOperation().getView().add(this);
		}
		if (appW.getGoogleDriveOperation() != null) {
			appW.getGoogleDriveOperation().initGoogleDriveApi();
		}
	}

	/**
	 * Handle dialog closed (escape or cancel)
	 */
	protected void handleClose() {
		appW.setDefaultCursor();
		dontSaveButton.setEnabled(true);
		appW.closePopupsNoTooltips();
	}

	private HorizontalPanel getTitelPanel() {
		final HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.titleLabel = new Label(loc.getMenu("Title") + ": ");
		if (appW.isUnbundledOrWhiteboard()) {
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

	private FlowPanel getCheckboxPanel() {
		templateTxt = new Label();
		templateTxt.setText(loc.getMenu("saveTemplate"));
		templateCheckbox = new ComponentCheckbox(false, templateTxt);
		return templateCheckbox;
	}

	private FlowPanel getButtonPanel() {
		buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("buttonPanel");

		buttonPanel.add(
				dontSaveButton = new StandardButton(loc.getMenu("DontSave"),
						appW));
		buttonPanel
				.add(saveButton = new StandardButton(loc.getMenu("Save"),
						appW));

		saveButton.addStyleName("saveButton");
		dontSaveButton.addStyleName("cancelBtn");
		listBox = widgetFactory.newListBox();
		listBox.addStyleName("visibility");
		setAvailableProviders();

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
		GeoGebraTubeUser user = null;
		if (appW.getLoginOperation() != null) {
			user = appW.getLoginOperation().getModel().getLoggedInUser();
		}
		if (user != null && user.hasGoogleDrive()
				&& appW.getLAF().supportsGoogleDrive()) {
			providerImages[providerCount++] = BrowseResources.INSTANCE
			        .location_drive();
			this.supportedProviders.add(Provider.GOOGLE);
		}
		if (appW.getLAF().supportsLocalSave()) {
			providerImages[providerCount++] = BrowseResources.INSTANCE
			        .location_local();
			this.supportedProviders.add(Provider.LOCAL);
		}
		if (providerPopup != null) {
			buttonPanel.remove(providerPopup);
		}
		providerPopup = new PopupMenuButtonW(appW, ImageOrText.convert(
		        providerImages, 24), 1, providerCount,
				SelectionTable.MODE_ICON,
				appW.isUnbundledOrWhiteboard());
		this.providerPopup.getMyPopup().addStyleName("providersPopup");

		if (appW.getLAF().supportsGoogleDrive()) {
			providerPopup.addPopupHandler(this);
			providerPopup.setSelectedIndex(appW.getFileManager()
			        .getFileProvider() == Provider.GOOGLE ? 1 : 0);
		} else if (appW.getLAF().supportsLocalSave()) {
			providerPopup.addPopupHandler(this);
			providerPopup.setSelectedIndex(appW.getFileManager()
			        .getFileProvider() == Provider.LOCAL ? 1 : 0);
		}
		providerPopup.getElement().getStyle()
		        .setPosition(com.google.gwt.dom.client.Style.Position.ABSOLUTE);
		providerPopup.getElement().getStyle().setLeft(10, Unit.PX);
		buttonPanel.add(providerPopup);
		buttonPanel.add(listBox);
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
		appW.getSaveController().saveAs(title.getText(),
				getSelectedVisibility(), this);
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
	 * sets the appWlication as "saved" and closes the dialog
	 */
	protected void onDontSave() {
		hide();
		appW.getSaveController().dontSave();
	}

	@Override
	public void show() {
		this.setAnimationEnabled(false);
		super.show();
		appW.invokeLater(new Runnable() {
			@Override
			public void run() {
				position();
			}
		});

		setTitle();
		if (appW.isOffline()) {
			this.providerPopup.setVisible(this.supportedProviders
					.contains(Provider.LOCAL));
		} else {
			this.providerPopup.setVisible(true);
			this.providerPopup.setSelectedIndex(this.supportedProviders
					.indexOf(appW.getFileManager().getFileProvider()));
		}
		rebuildVisibilityList();
		listBox.setVisible(
				appW.getFileManager().getFileProvider() == Provider.TUBE);
		if (this.title.getText().length() < MIN_TITLE_LENGTH) {
			this.saveButton.setEnabled(false);
		}
		if (templateCheckbox != null) {
			templateCheckbox.setVisible(true);
			Material activeMaterial = ((AppW) app).getActiveMaterial();
			templateCheckbox.setSelected(activeMaterial != null && MaterialType.ggsTemplate
					.equals(activeMaterial.getType()));
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				title.setFocus(true);
			}
		});
	}

	@Override
	public void setDiscardMode() {
		// could be useful
	}

	@Override
	public void showAndPosition(Widget anchor) {
		if (anchor == null) {
			center();
		} else {
			showRelativeTo(anchor);
		}
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

	/**
	 * Update localization
	 */
	@Override
	public void setLabels() {
		this.getCaption().setText(loc.getMenu("Save"));
		this.titleLabel.setText(loc.getMenu("Title") + ": ");
		this.dontSaveButton.setText(loc.getMenu("DontSave"));
		this.saveButton.setText(loc.getMenu("Save"));
        if (templateTxt != null) {
            templateTxt.setText(loc.getMenu("saveTemplate"));
        }
		rebuildVisibilityList();
	}

	private void rebuildVisibilityList() {
		listBox.clear();
		listBox.addItem(loc.getMenu("Private"));
		listBox.addItem(loc.getMenu("Shared"));
		MaterialVisibility currentVisibility = getCurrentVisibility();
		if (currentVisibility == MaterialVisibility.Public) {
			listBox.addItem(loc.getMenu("Public"));
		}
		listBox.setSelectedIndex(currentVisibility.getIndex());
	}

	private MaterialVisibility getCurrentVisibility() {
		Material activeMaterial = appW.getActiveMaterial();
		if (activeMaterial != null
				&& app.getLoginOperation().owns(activeMaterial)) {
			return MaterialVisibility.value(activeMaterial.getVisibility());
		}
		return MaterialVisibility.Shared;
	}

	@Override
	public void fireActionPerformed(PopupMenuButtonW actionButton) {
		Provider provider = this.supportedProviders.get(actionButton.getSelectedIndex());
		appW.getFileManager().setFileProvider(provider);

		listBox.setVisible(provider == Provider.TUBE);

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
		appW.getSaveController().setSaveType(saveType);
	}

	/**
	 * Adds a little cross to cancel the dialog if there is already a panel
	 * attached to the Dialogbox. If the first child of the Dialogbox is not a
	 * Panel this will do nothing!
	 *
	 * Pulled up from SaveDialogW
	 */
	private void addCancelButton() {
		if (getWidget() instanceof Panel
				&& !(getWidget() instanceof SimplePanel)) {
			SimplePanel cancel = new SimplePanel();
			this.cancelButton = new StandardButton(
					SharedResources.INSTANCE.dialog_cancel(), app);
			this.cancelButton.addStyleName("cancelSaveButton");
			this.cancelButton.addFastClickHandler(new FastClickHandler() {
				@Override
				public void onClick(Widget source) {
					onCancel();
					app.getSaveController().cancel();
				}
			});

			cancel.add(this.cancelButton);

			((Panel) getWidget()).add(cancel);
		}
	}
}
