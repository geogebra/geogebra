package org.geogebra.web.full.gui.openfileview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.dialog.OptionDialog;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GroupShareDialog extends OptionDialog {

	private HashMap<String, GroupCheckBox> checkboxes = new HashMap<>();
	private MaterialCardI card;
	private int progress;
	private int failed;

	private static class GroupCheckBox extends CheckBox {

		public boolean oldState;

		public GroupCheckBox(String string) {
			super(string);
		}

		public void updateShare(MaterialCardI card,
				final GroupShareDialog dialog) {
			if (oldState != getValue().booleanValue()) {
				card.setShare(getText(), getValue().booleanValue(),
						new AsyncOperation<Boolean>() {

							@Override
							public void callback(Boolean obj) {
								oldState = obj.booleanValue();
								dialog.updateProgress(obj);
							}
						});
			}
		}

	}
	
	/**
	 * @param app
	 *            application
	 * @param card
	 *            material card
	 */
	public GroupShareDialog(AppW app, MaterialCardI card) {
		super(app.getPanel(), app);
		this.card = card;
		initGui();
	}

	private void initGui() {
		FlowPanel mainPanel = new FlowPanel();
		final VerticalPanel groups = new VerticalPanel();
		ArrayList<String> groupNames = app.getLoginOperation().getModel().getUserGroups();
		for (int i = 0; i < groupNames.size(); i++) {
			checkboxes.put(groupNames.get(i),
					new GroupCheckBox(groupNames.get(i)));
			groups.add(checkboxes.get(groupNames.get(i)));
		}
		mainPanel.add(groups);
		groups.setVisible(false);
		mainPanel.add(getButtonPanel());
		add(mainPanel);
		setLabels();
		setPrimaryButtonEnabled(true);
		app.getLoginOperation().getGeoGebraTubeAPI().getGroups(
				card.getMaterialID(), new AsyncOperation<List<String>>() {

					@Override
					public void callback(List<String> obj) {
						updateSelection(obj);
						groups.setVisible(true);
					}
				});
	}

	/**
	 * @param availableGroups
	 *            list of available group IDs
	 */
	protected void updateSelection(List<String> availableGroups) {
		if (availableGroups == null) {
			Log.warn("Failed getting groups");
			return;
		}
		for (String groupName : availableGroups) {
			if (checkboxes.get(groupName) != null) {
				checkboxes.get(groupName).setValue(true);
				checkboxes.get(groupName).oldState = true;
			}
		}
	}

	private void setLabels() {
		updateButtonLabels("Share");
	}

	@Override
	protected void processInput() {
		progress = 0;
		failed = 0;
		for (GroupCheckBox cb : checkboxes.values()) {
			cb.updateShare(card, this);
		}
		hide();
	}

	/**
	 * @param success
	 *            whether share worked
	 */
	protected void updateProgress(Boolean success) {
		progress++;
		if (!success) {
			failed++;
		}
		if (progress == checkboxes.size() && failed > 0) {
			app.showError("Today is not the day :(");
		}
	}

}
