package org.geogebra.web.web.euclidian;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.ShowLabelModel;
import org.geogebra.common.gui.dialog.options.model.ShowLabelModel.IShowLabelListener;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.ClickEndHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.ContextMenuGeoElementW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.app.GGWToolBar;
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
		IShowLabelListener {

	private EuclidianController ec;

	/**
	 * popup menu
	 */
	ContextMenuGeoElementW popup;
	private FlowPanel main;
	private LocalizationW loc;
	private Label lblName;
	private AutoCompleteTextFieldW tfName;

	private GCheckMarkLabel cmName;
	private GCheckMarkLabel cmValue;
	private ShowLabelModel model;
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
						GGWToolBar.getMyIconResourceBundle()
								.mode_showhidelabel_32(),
						this);
		ec = app.getActiveEuclidianView().getEuclidianController();
		createPopup();
		addStyleName("MyCanvasButton-borderless");
		model = new ShowLabelModel(app, this);
	}

	private void createPopup() {
		popup = ((GuiManagerW) app.getGuiManager())
				.getPopupMenu(ec.getAppSelectedGeos());
		popup.getWrappedPopup().getPopupPanel().addCloseHandler(this);
		// addClickHandler(this);
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
		createDialog();

	}

	private void createDialog() {

		main = new FlowPanel();
		lblName = new Label();
		InputPanelW input = new InputPanelW(null, app, 1, -1, true);
		tfName = input.getTextComponent();
		tfName.setAutoComplete(false);
		tfName.addFocusListener(new FocusListenerW(this) {
			@Override
			protected void wrapFocusLost() {
				// TODO: implement
			}
		});

		tfName.addKeyHandler(new KeyHandler() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					// TODO: implement
				}
			}
		});

		Command nameValueCmd = new Command() {

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

		main.add(LayoutUtilW.panelRow(lblName, tfName));
		main.add(cmName.getPanel());
		main.add(cmValue.getPanel());
		getMyPopup().setWidget(main);
		setLabels();
	}


	@Override
	public void onClose(CloseEvent<GPopupPanel> event) {
		// handle close
	}
	


	@Override
	public void onMouseOver(MouseOverEvent event) {
		// TODO Auto-generated method stub

	}

	public void setLabels() {
		lblName.setText(loc.getPlain("Name") + ":");
		cmName.setText(loc.getPlain("ShowLabel"));
		cmValue.setText(loc.getPlain("ShowValue"));
	}

	/**
	 * Apply settings to seleted geo(s).
	 */
	void applyCheckboxes() {
		boolean name = cmName.isChecked();
		boolean value = cmValue.isChecked();
		int mode = -1;
		if (name && !value) {
			Log.debug("CHECK - name only");
			mode = 0;
		} else if (name && value) {
			Log.debug("CHECK - name and value");
			mode = 1;
		} else if (!name && value) {
			Log.debug("CHECK - value only");
			mode = 2;
		} else if (!name && !value) {
			Log.debug("CHECK - none");
		}

		if (mode == -1) {

		} else {
			model.setGeos(
					app.getSelectionManager().getSelectedGeos().toArray());
			model.applyModeChanges(mode, true);
		}
	}

	public Object updatePanel(Object[] geos2) {
		// TODO Auto-generated method stub
		return null;
	}

	public void update(boolean isEqualVal, boolean isEqualMode) {
		// TODO Auto-generated method stub

	}
}
