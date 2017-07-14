package org.geogebra.web.web.euclidian;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.NameValueModel;
import org.geogebra.common.gui.dialog.options.model.NameValueModel.INameValueListener;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.ClickEndHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.util.PopupMenuButtonW;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;
import org.geogebra.web.web.javax.swing.GCheckMarkLabel;

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

	private static final int LABEL_MODE_NAME_ONLY = 0;
	private static final int LABEL_MODE_NAME_AND_VALUE = 1;
	private static final int LABEL_MODE_VALUE_ONLY = 2;

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
	private TabbedKeyboard kbd;
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
		ImgResourceHelper
				.setIcon(
						MaterialDesignResources.INSTANCE.label_settings(),
						this);
		createPopup();

		addStyleName("MyCanvasButton-borderless");
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
		tfName.enableGGBKeyboard();
		tfName.setDeferredFocus(true);

		tfName.addFocusListener(new FocusListenerW(this) {
			@Override
			protected void wrapFocusLost() {
				onEnter();
			}
		});

		tfName.addKeyHandler(new KeyHandler() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					onEnter();

				}
			}
		});


		Command nameValueCmd = new Command() {

			@Override
			public void execute() {
				applyCheckboxes();
			}
		};
		cmName = new GCheckMarkLabel("", MaterialDesignResources.INSTANCE
				.check_black().getSafeUri().asString(), true, nameValueCmd);

		cmValue = new GCheckMarkLabel("",
				MaterialDesignResources.INSTANCE.check_black().getSafeUri()
						.asString(),
				true, nameValueCmd);

		boolean isSelectionMode = app.getActiveEuclidianView()
				.getEuclidianController()
				.getMode() == EuclidianConstants.MODE_SELECT;
		if (!isSelectionMode) {
			main.add(LayoutUtilW.panelRow(lblName, tfName));
		}
		main.add(cmName.getPanel());
		main.add(cmValue.getPanel());
		main.setStyleName("labelPopupPanel");
		getMyPopup().setWidget(main);
		kbd = (TabbedKeyboard) ((GuiManagerW) app.getGuiManager())
				.getOnScreenKeyboard(tfName, null);
		getMyPopup().addAutoHidePartner(kbd.getElement());
		setLabels();

	}


	/**
	 * Submit the change
	 */
	protected void onEnter() {
		model.applyNameChange(tfName.getText(), app.getErrorHandler());

	}

	@Override
	public void onClose(CloseEvent<GPopupPanel> event) {
		// TODO
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
			mode = LABEL_MODE_NAME_ONLY;
		} else if (name && value) {
			mode = LABEL_MODE_NAME_AND_VALUE;
		} else if (!name && value) {
			mode = LABEL_MODE_VALUE_ONLY;
		} else if (!name && !value) {
			// hide, nothing to do.
		}
		model.applyModeChanges(mode, mode != -1);
	}

	@Override
	public Object updatePanel(Object[] geos2) {
		return null;
	}

	@Override
	public void update(boolean isEqualVal, boolean isEqualMode, int mode) {
		cmName.setChecked(mode == LABEL_MODE_NAME_ONLY
				|| mode == LABEL_MODE_NAME_AND_VALUE);
		cmValue.setChecked(mode == LABEL_MODE_VALUE_ONLY
				|| mode == LABEL_MODE_NAME_AND_VALUE);
		// tfName.setText(model.getGeoAt(0).getDefaultLabel());
	}

	@Override
	protected void onClickAction() {
		model.setGeos(app.getSelectionManager().getSelectedGeos().toArray());
		model.updateProperties();
		kbd.selectAbc();
		tfName.requestFocus();
	}

	@Override
	public void setNameText(String text) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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
