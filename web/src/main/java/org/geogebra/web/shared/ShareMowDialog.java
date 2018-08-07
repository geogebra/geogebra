package org.geogebra.web.shared;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 *
 */
public class ShareMowDialog extends DialogBoxW
		implements FastClickHandler, SetLabels {
	private FlowPanel dialog;
	private FlowPanel contentPanel;
	private ScrollPanel groupsPanel;
	private Label chooseGrLbl;
	private FlowPanel buttonPanel;

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public ShareMowDialog(AppW app) {
		super(app.getPanel(), app);
		addStyleName("mowShareDialog");
		buildGUI();
	}

	private void buildGUI() {
		dialog = new FlowPanel();
		contentPanel = new FlowPanel();
		chooseGrLbl = new Label("");
		chooseGrLbl.setStyleName("chooseGrTxt");
		contentPanel.add(chooseGrLbl);
		groupsPanel = new ScrollPanel();
		groupsPanel.setStyleName("groupList");
		buttonPanel = new FlowPanel();

		dialog.add(contentPanel);
		dialog.add(groupsPanel);
		dialog.add(buttonPanel);
		this.add(dialog);
		setLabels();
	}

	public void onClick(Widget source) {

	}

	public void setLabels() {
		getCaption().setText(app.getLocalization().getMenu("Share"));
		chooseGrLbl.setText(app.getLocalization().getMenu("GroupShareTxt"));
	}

	@Override
	public void show() {
		super.show();
		super.center();
	}

}
