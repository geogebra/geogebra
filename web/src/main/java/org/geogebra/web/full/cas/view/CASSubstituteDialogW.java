package org.geogebra.web.full.cas.view;

import java.util.Vector;

import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
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
		withLabel.addStyleName("with");
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
		subst.addTextComponentKeyUpHandler(event -> data.get(idx).set(0, subst.getText()));
		InputPanelW with = new InputPanelW(data.get(idx).get(1), app, 1, -1, false);
		with.getTextComponent().addStyleName("with");
		with.addTextComponentKeyUpHandler(event -> {
			setPosBtnDisabled(false);
			data.get(idx).set(1, with.getText());
		});
		block.add(subst);
		block.add(with);

		if (data.get(idx).get(1) != null && !data.get(idx).get(1).isEmpty()) {
			setPosBtnDisabled(false);
		}

		addFocusHandler(subst);
		addFocusHandler(with);
		addHoverHandler(subst);
		addHoverHandler(with);
		subst.getTextComponent().addBlurHandler(event -> {
			removeOrAddEmptyLine(subst, data, idx, 1, block);
			subst.getTextComponent().removeStyleName("focused");
		});
		with.getTextComponent().addBlurHandler(event -> {
			removeOrAddEmptyLine(with, data, idx, 0, block);

			if (noWithInput(data)) {
				setPosBtnDisabled(true);
			}
		});

		addDialogContent(block);
	}

	private void removeOrAddEmptyLine(InputPanelW inputField, Vector<Vector<String>> data, int idx,
			int vectElem, FlowPanel parenPanel) {
		if (inputField.getText().isEmpty() && idx != data.size()
				&& data.get(idx).get(vectElem).isEmpty() && idx != data.size() - 1) {
			data.remove(idx);
			parenPanel.removeFromParent();
		}

		if (!inputField.getText().isEmpty() && (idx == data.size() - 1 || idx == data.size())) {
			extendData(data);
		}
		inputField.getTextComponent().removeStyleName("focused");

		Dom.toggleClass(this, "hasBorder", data.size() >= 7);
	}

	private void addFocusHandler(InputPanelW inputField) {
		inputField.getTextComponent().addFocusHandler(event ->
				inputField.getTextComponent().addStyleName("focused"));
	}

	private void addHoverHandler(InputPanelW inputField) {
		inputField.getTextComponent().addDomHandler(event -> {
			inputField.getTextComponent().addStyleName("hover");
		}, MouseOverEvent.getType());
		inputField.getTextComponent().addDomHandler(event -> {
			inputField.getTextComponent().removeStyleName("hover");
		}, MouseOutEvent.getType());
	}

	private void extendData(Vector<Vector<String>> data) {
		Vector<String> vec = new Vector<>();
		vec.setSize(2);
		vec.set(0, "");
		vec.set(1, "");
		data.add(vec);
		buildSubstWithBlock(data, data.size() - 1);
	}

	private boolean noWithInput(Vector<Vector<String>> data) {
		return data.stream().allMatch(elem -> elem.get(1).isEmpty());
	}
}
