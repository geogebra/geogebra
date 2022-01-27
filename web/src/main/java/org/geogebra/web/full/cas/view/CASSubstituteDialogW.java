package org.geogebra.web.full.cas.view;

import java.util.Vector;

import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class CASSubstituteDialogW extends ComponentDialog {
	/**
	/**
	 * base dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 */
	public CASSubstituteDialogW(AppW app, DialogData dialogData, Vector<Vector<String>> data) {
		super(app, dialogData, false, true);
		addStyleName("substituteDialog");
		setPosBtnDisabled(true);
		buildGUI(app.getLocalization(), data);
	}

	private void buildGUI(Localization loc, Vector<Vector<String>> data) {
		FlowPanel labelPanel = new FlowPanel();
		labelPanel.addStyleName("flexGroup");
		Label substLabel = new Label(loc.getMenu("Substitute"));
		labelPanel.add(substLabel);
		Label withLabel = new Label(loc.getMenu("With"));
		labelPanel.add(withLabel);
		addDialogContent(labelPanel);

		for (int i = 0; i < data.size(); i++) {
			buildSubstWithBlock(data, i);
		}
	}

	private void buildSubstWithBlock(Vector<Vector<String>> data, int idx) {
		FlowPanel block = new FlowPanel();
		block.addStyleName("flexGroup");

		InputPanelW subst = new InputPanelW(data.get(idx).get(0), app, 1, -1, false);
		subst.addTextComponentKeyUpHandler(event -> {
			data.get(idx).set(0, subst.getText());
		});
		if (data.get(idx).get(1) != null && !data.get(idx).get(1).isEmpty()) {
			setPosBtnDisabled(false);
		}
		InputPanelW with = new InputPanelW(data.get(idx).get(1), app, 1, -1, false);
		with.addTextComponentKeyUpHandler(event -> {
			setPosBtnDisabled(false);
			data.get(idx).set(1, with.getText());
		});
		block.add(subst);
		block.add(with);

		addDialogContent(block);
	}
}
