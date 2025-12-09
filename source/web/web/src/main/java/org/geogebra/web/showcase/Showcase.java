/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.showcase;

import static org.geogebra.common.properties.PropertyView.*;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.CheckForNull;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.factory.SimpleBooleanProperty;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.editor.share.syntax.SyntaxHintImpl;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.ComponentComboBox;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.components.ComponentExpandableList;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.components.ComponentProgressBar;
import org.geogebra.web.full.gui.components.ComponentTextArea;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonData;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonPanel;
import org.geogebra.web.full.gui.view.algebra.ToastController;
import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.AppWsimple;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.shared.components.ComponentSwitch;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;
import org.geogebra.web.shared.components.tab.ComponentTab;
import org.geogebra.web.shared.components.tab.TabData;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.RootPanel;

import com.google.gwt.core.client.EntryPoint;

import elemental2.dom.DomGlobal;

public class Showcase implements EntryPoint {

	@Override
	public void onModuleLoad() {
		Log.setLogger(new LoggerW());
		GeoGebraElement ae = GeoGebraElement.as(RootPanel.getBodyElement());
		AppletParameters params = new AppletParameters("suite");
		GeoGebraFrameSimple frame = new GeoGebraFrameSimple(ae, params, null);
		AppW app = new AppWsimple(ae,
				params, frame, false);
		ComponentCheckbox[] checkbox = {
				new ComponentCheckbox(app.getLocalization(), true, "Vanilla"),
				new ComponentCheckbox(app.getLocalization(), false, "Chocolate"),
				new ComponentCheckbox(app.getLocalization(), true, "Pineapple")
		};
		checkbox[2].setDisabled(true);
		ComponentComboBox componentComboBox = new ComponentComboBox(app,
				"Fruit", Arrays.asList("Apple", "Orange", "Banana"));
		ComponentDropDown componentDropDown = new ComponentDropDown(app, "Fruit",
				Arrays.asList("Apple", "Orange", "Banana"), 0);
		ComponentInfoErrorPanel errorPanel = new ComponentInfoErrorPanel(app.getLocalization(),
				new InfoErrorData("404", "Content not found"));
		ComponentSwitch componentSwitch = new ComponentSwitch(true, bool -> {});
		ComponentInputField inputField = new ComponentInputField(app, "Write here",
				"Name", null, null, null);
		inputField.getTextField().getTextComponent().addEnterPressHandler(() ->
				inputField.setError(inputField.getText().length() < 2 ? "Too short" : null)
		);
		ComponentProgressBar progressBar = new ComponentProgressBar(false, true);
		progressBar.setIndicatorWidth(42);
		RadioButtonPanel<Integer> radioButtonPanel = new RadioButtonPanel<>(
					app.getLocalization(), List.of(
				new RadioButtonData<>("three", 3),
				new RadioButtonData<>("five", 5),
				new RadioButtonData<>("seven", 7),
				new RadioButtonData<>("nine", true, 9)), 5, val -> {});
		RootPanel.get().add(frame);
		StandardButton showDialog = createDialogButton(app);
		final boolean[] bool = {true};
		BooleanProperty booleanProperty = new SimpleBooleanProperty(app.getLocalization(), "",
				() -> bool[0], b -> bool[0] = b);
		Checkbox checkBoxProperty = (Checkbox) PropertyView.of(booleanProperty);
		ComponentExpandableList expandableList = new ComponentExpandableList(app, checkBoxProperty,
				"Expand me");
		expandableList.addToContent(new Label("TBD"));
		StandardButton showSnackBar = newStandardButton("Show Snack Bar");
		showSnackBar.addFastClickHandler((widget) -> app.getToolTipManager()
				.showBottomMessage("Success.", app));
		StandardButton showToast = getToastButton(app);
		ComponentTextArea textArea = new ComponentTextArea(app.getLocalization(), "Input");
		TabData[] data = {
				new TabData("Checkbox", wrap(checkbox)),
				new TabData("Combo Box", wrap(componentComboBox)),
				new TabData("Dialog", wrap(showDialog)),
				new TabData("Dropdown", wrap(componentDropDown)),
				new TabData("Expandable List", wrap(expandableList)),
				new TabData("Info/Error Panel", wrap(errorPanel)),
				new TabData("Input Field", wrap(inputField)),
				new TabData("Progress Bar", wrap(progressBar)),
				new TabData("Text Area", wrap(textArea)),
				new TabData("Radio Button", wrap(radioButtonPanel)),
				new TabData("Snack Bar", wrap(showSnackBar)),
				new TabData("Switch", wrap(componentSwitch)),
				new TabData("Toast", wrap(showToast)),
		};
		String hash = DomGlobal.location.hash;
		ComponentTab tab = new ComponentTab(app, "Showcase", data);
		tab.addTabChangedListener(selected -> {
			for (int i = 0; i < data.length; i++) {
				final boolean show = selected != null && i == selected;
				((FlowPanel) data[i].getTabPanel()).forEach(child -> child.setVisible(show));
			}
			if (selected != null) {
				DomGlobal.location.hash = data[selected].getTabTitle()
						.replace(" ", "").toLowerCase(Locale.ROOT);
			}
		});
		if (!hash.isEmpty()) {
			for (int i = 0; i < data.length; i++) {
				if (data[i].getTabTitle().replace(" ", "").toLowerCase(Locale.ROOT)
						.equals(hash.substring(1))) {
					tab.switchToTab(i);
					break;
				}
			}
		}
		frame.add(tab);
	}

	private StandardButton createDialogButton(AppW app) {
		StandardButton showDialog = newStandardButton("Show Dialog");
		ComponentDialog dialog = new ComponentDialog(app, new DialogData("Important"), false, true);
		dialog.addDialogContent(new Label("Please read carefully."));
		showDialog.addFastClickHandler((widget) -> dialog.show());
		return showDialog;
	}

	private StandardButton getToastButton(AppW app) {
		StandardButton showToast = newStandardButton("Show Toast");
		ToastController controller = new ToastController(app, () -> new Rectangle(0, 0, 500, 500));
		SyntaxHintImpl syntaxHint = new SyntaxHintImpl();
		syntaxHint.update("Midpoint", List.of("A", "B"), 1);
		showToast.addFastClickHandler((widget) -> {
			controller.updateSyntaxTooltip(syntaxHint);
			DomGlobal.setTimeout((a) -> controller.hide(), 4000);
		});
		return showToast;
	}

	private StandardButton newStandardButton(String label) {
		StandardButton button = new StandardButton(label);
		button.addStyleName("materialTextButton");
		return button;
	}

	private static class SampleProperty extends AbstractNamedEnumeratedProperty<String> {
		String value;

		public SampleProperty(AppW app) {
			super(app.getLocalization(), "Fruit");
			setNamedValues(Stream.of("Apple", "Orang", "Banana")
					.map(f -> Map.entry(f, f)).collect(Collectors.toList()));
		}

		@Override
		public String getValue() {
			return value;
		}

		@Override
		protected void doSetValue(String value) {
			this.value = value;
		}
	}

	private static final class SampleSuggestionProperty extends AbstractValuedProperty<String>
			implements StringPropertyWithSuggestions {

		String value;

		private SampleSuggestionProperty(Localization localization) {
			super(localization, "Fruit");
		}

		@Override
		public List<String> getSuggestions() {
			return List.of("Apple", "Orange", "Banana");
		}

		@Override
		public @CheckForNull String validateValue(String value) {
			return value.length() < 2 ? "Too short" : null;
		}

		@Override
		public String getValue() {
			return value;
		}

		@Override
		public void doSetValue(String value) {
			this.value = value;
		}
	}

	/**
	 * @param widgets
	 *            widgets
	 * @return widgets merged in a column
	 */
	public static FlowPanel wrap(IsWidget... widgets) {
		FlowPanel p = new FlowPanel();
		for (IsWidget widget : widgets) {
			p.add(widget);
		}
		p.getElement().getStyle().setProperty("padding", "1em");
		return p;
	}
}
