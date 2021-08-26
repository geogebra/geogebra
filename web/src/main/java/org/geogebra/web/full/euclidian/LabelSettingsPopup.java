package org.geogebra.web.full.euclidian;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.NameValueModel;
import org.geogebra.common.gui.dialog.options.model.NameValueModel.INameValueListener;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.full.gui.util.VirtualKeyboardGUI;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.full.javax.swing.GCheckMarkLabel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.ClickEndHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;

import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * label settings popup
 */
public class LabelSettingsPopup extends PopupMenuButtonW
		implements CloseHandler<GPopupPanel>, MouseOverHandler, SetLabels,
		INameValueListener {

	/**
	 * popup menu
	 */
	private FlowPanel main;
	private LocalizationW loc;
	private Label lblName;
	private AutoCompleteTextFieldW tfName;

	private GCheckMarkLabel cmName;
	private GCheckMarkLabel cmValue;
	private NameValueModel model;
	private VirtualKeyboardGUI kbd;
	private FlowPanel namePanel;

	/**
	 * label related popup
	 * 
	 * @param app
	 *            - application
	 */
	public LabelSettingsPopup(AppW app) {
		super(app, null, -1, -1, null, false, false, null);
		this.app = app;
		loc = app.getLocalization();
		this.setIcon(
				new ImageOrText(ToolbarSvgResourcesSync.INSTANCE
						.mode_showhidelabel_32(), 24));
		createPopup();

		addStyleName("MyCanvasButton");
		model = new NameValueModel(app, this);
	}

	private void createPopup() {
		ClickStartHandler.init(this, new ClickStartHandler(false, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// handle click
			}
		});
		ClickEndHandler.init(this, new ClickEndHandler(false, true) {

			@Override
			public void onClickEnd(int x, int y, PointerEventType type) {
				// only stop
			}
		});

		getMyPopup().addCloseHandler(this);
		createDialog();
	}

	private void createDialog() {

		main = new FlowPanel();
		lblName = new Label();
		tfName = InputPanelW.newTextComponent(app);
		tfName.setAutoComplete(false);
		if (!app.isWhiteboardActive()) {
			tfName.enableGGBKeyboard();
		}
		// remove focus, see GGB-
		// tfName.setDeferredFocus(true);

		tfName.addBlurHandler(event -> {
			if (model.noLabelUpdateNeeded(tfName.getText())) {
				return;
			}
			onEnter();
		});

		tfName.addKeyHandler(e -> {
			if (e.isEnterKey()) {
				onEnter();

			}
		});

		Command nameValueCmd = () -> applyCheckboxes();
		cmName = new GCheckMarkLabel("", MaterialDesignResources.INSTANCE
				.check_black(), true, nameValueCmd);

		cmValue = new GCheckMarkLabel("",
				MaterialDesignResources.INSTANCE.check_black(),
				true, nameValueCmd);

		boolean isSelectionMode = app.getActiveEuclidianView()
				.getEuclidianController()
				.getMode() == EuclidianConstants.MODE_SELECT;
		if (!isSelectionMode) {
			namePanel = LayoutUtilW.panelRow(lblName, tfName);
			main.add(namePanel);
		}
		main.add(cmName.getPanel());
		main.add(cmValue.getPanel());
		main.setStyleName("labelPopupPanel");
		getMyPopup().setWidget(main);
		kbd = ((AppWFull) app).getKeyboardManager().getOnScreenKeyboard();
		getMyPopup().addAutoHidePartner(kbd.getElement());
		setLabels();
	}

	/**
	 * Submit the change
	 */
	protected void onEnter() {
		model.applyNameChange(tfName.getText(), app.getErrorHandler());
		applyCheckboxes();
	}

	@Override
	public void onClose(CloseEvent<GPopupPanel> event) {
		if (model.noLabelUpdateNeeded(tfName.getText())) {
			return;
		}

		model.applyNameChange(tfName.getText(), app.getErrorHandler());
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setLabels() {
		lblName.setText(loc.getMenu("Label") + ":");
		cmName.setText(loc.getMenu("ShowLabel"));
		cmValue.setText(loc.getMenu("ShowValue"));
	}

	/**
	 * Apply settings to seleted geo(s).
	 */
	void applyCheckboxes() {
		boolean name = cmName.isChecked();
		boolean value = cmValue.isChecked();
		int mode = -1;
		if (name && !value) {
			mode = model.isForceCaption() ? GeoElementND.LABEL_CAPTION
					: GeoElementND.LABEL_NAME;
		} else if (name && value) {
			mode = model.isForceCaption() ? GeoElementND.LABEL_CAPTION_VALUE
					: GeoElementND.LABEL_NAME_VALUE;
		} else if (!name && value) {
			mode = GeoElementND.LABEL_VALUE;
		}
		// !name && !value: hide, nothing to do.

		model.applyModeChanges(mode, mode != -1);
	}

	@Override
	public Object updatePanel(Object[] geos2) {
		return null;
	}

	@Override
	public void update(boolean isEqualVal, boolean isEqualMode, int mode) {
		if (namePanel != null) {
			namePanel.setVisible(model.getGeosLength() == 1);
		}
		if (!model.isLabelVisible()) {
			cmName.setChecked(false);
			cmValue.setChecked(false);
			return;
		}
		cmName.setChecked(
				isEqualVal
						&& (mode == GeoElementND.LABEL_NAME
								|| mode == GeoElementND.LABEL_CAPTION_VALUE
								|| mode == GeoElementND.LABEL_NAME_VALUE
								|| mode == GeoElementND.LABEL_CAPTION));
		cmValue.setChecked(
				isEqualMode
						&& (mode == GeoElementND.LABEL_VALUE
								|| mode == GeoElementND.LABEL_CAPTION_VALUE
								|| mode == GeoElementND.LABEL_NAME_VALUE));
	}

	@Override
	protected void onClickAction() {
		model.setGeos(app.getSelectionManager().getSelectedGeos().toArray());
		model.updateProperties();
		kbd.selectTab(KeyboardType.ABC);
		tfName.requestFocus();
	}

	@Override
	public void setNameText(String text) {
		tfName.setValue(text);
	}

	@Override
	public void setDefinitionText(String text) {
		// not used here.
	}

	@Override
	public void setCaptionText(String text) {
		// not used here.
	}

	@Override
	public void updateGUI(boolean showDefinition, boolean showCaption) {
		// not used here.
	}

	@Override
	public void updateDefLabel() {
		// not used here.
	}

	@Override
	public void updateCaption(String text) {
		if (!model.isForceCaption()) {
			return;
		}
		tfName.setText(text);
	}

	@Override
	public void updateName(String text) {
		if (model.isForceCaption()) {
			return;
		}
		tfName.setText(text);
	}

}
