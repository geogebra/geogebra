package org.geogebra.web.shared;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.SaveController.SaveListener;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 * 
 *         Joint share dialog for mow (group + link sharing)
 *
 */
public class ShareDialogMow2 extends DialogBoxW
		implements FastClickHandler, SetLabels, SaveListener {
	private AppW appW;

	private FlowPanel dialogContent;
	private Label selGroupLbl;
	private FlowPanel groupPanel;
	private ScrollPanel scrollPanel;
	private FlowPanel buttonPanel;
	private StandardButton cancelBtn;
	private StandardButton saveBtn;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public ShareDialogMow2(AppW app) {
		super(app.getPanel(), app);
		this.appW = app;
		buildGui();
	}

	private void buildGui() {
		addStyleName("shareDialogMow");
		dialogContent = new FlowPanel();
		selGroupLbl = new Label();
		selGroupLbl.addStyleName("selGrLbl");
		dialogContent.add(selGroupLbl);
		buildGroupPanel();
		buildButtonPanel();
		add(dialogContent);
		setLabels();
	}

	private void buildGroupPanel() {
		groupPanel = new FlowPanel();
		groupPanel.addStyleName("groupPanel");
		scrollPanel = new ScrollPanel();
		groupPanel.add(scrollPanel);
		FlowPanel groups = new FlowPanel();
		groups.add(new GroupButtonMow(appW, "group group group group"));
		groups.add(new GroupButtonMow(appW, "group group group group"));
		scrollPanel.add(groups);
		dialogContent.add(groupPanel);
	}

	private void buildButtonPanel() {
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		cancelBtn = addButton("Cancel");
		saveBtn = addButton("Save");
		dialogContent.add(buttonPanel);
	}

	private StandardButton addButton(String transKey) {
		StandardButton btn = new StandardButton(
				appW.getLocalization().getMenu(transKey), appW);
		btn.addFastClickHandler(this);
		buttonPanel.add(btn);
		return btn;
	}

	public void setLabels() {
		getCaption().setText(app.getLocalization()
				.getMenu("Share"));
		selGroupLbl
				.setText(app.getLocalization().getMenu("shareGroupHelpText"));
		cancelBtn.setText(app.getLocalization().getMenu("Cancel"));
		saveBtn.setText(appW.getLocalization().getMenu("Save"));
	}

	public void onClick(Widget source) {
		if (source == cancelBtn) {
			hide();
		} else if (source == saveBtn) {
			// TODO share functionality
			hide();
		}
	}

	@Override
	public void show() {
		super.show();
		super.center();
	}
}
