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

package org.geogebra.web.full.gui.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.geogebra.common.gui.dialog.ButtonDialogModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.ComponentChip;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.components.ComponentToast;
import org.geogebra.web.full.gui.util.CodeMirrorEditorWidget;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.event.dom.client.MouseOutEvent;
import org.gwtproject.event.dom.client.MouseOverEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

import elemental2.dom.DOMRect;
import elemental2.dom.KeyboardEvent;

/**
 * Dialog for creating buttons and input-boxes
 */
public class ButtonDialogW extends ComponentDialog implements HasKeyboardPopup {
	private static final int BUTTON_MARGIN = 29;
	private static final int TOOLTIP_HEIGHT = 20;
	private ComponentInputField captionInput;
	private final ButtonDialogModel model;
	private CodeMirrorEditorWidget scriptArea;
	private final Localization loc;
	private final List<Commands> chipsCommands = List.of(Commands.SetValue,
			Commands.StartAnimation, Commands.SetColor, Commands.SetVisibleInView);
	private GPopupPanel objectsPopup;

	/**
	 * @param app {@link AppW}
	 * @param x position
	 * @param y position
	 * @param data dialog translation keys
	 * @param inputBox whether this is for input-box
	 */
	public ButtonDialogW(final AppW app, int x, int y, DialogData data, boolean inputBox) {
		super(app, data, false, true);

		this.loc = app.getLocalization();
		model = new ButtonDialogModel(app, x, y, inputBox);
		addStyleName(inputBox ? "inputboxDialog" : "buttonDialog");
		buildContent(inputBox);
		setOnPositiveAction(() -> model.apply(captionInput.getText(), scriptArea.getText()));
		if (!app.isWhiteboardActive()) {
			app.registerPopup(this);
		}

		Dom.addEventListener(getElement(), "click", event -> {
			if (objectsPopup != null && objectsPopup.isShowing()) {
				objectsPopup.hide();
			}
		});
		addCloseHandler(event -> {
			app.unregisterPopup(this);
			app.hideKeyboard();
		});

	}

	private void buildContent(boolean inputBox) {
		String initString = model.getInitString();
		captionInput = new ComponentInputField((AppW) app, "",
				"Button.Caption", "", initString, null);
		captionInput.getTextWidget().setAutoComplete(false);

		Label scriptLabel = BaseWidgetFactory.INSTANCE.newSecondaryText(loc.getMenu("Script"),
				"scriptLabel");
		initScriptArea();

		FlowPanel scriptPanel = new FlowPanel();
		scriptPanel.add(scriptLabel);
		scriptPanel.add(scriptArea);

		FlowPanel contentPanel = new FlowPanel();
		// create object list
		contentPanel.add(captionInput);

		if (model.isTextField()) {
			ArrayList<GeoElement> options = model.getLinkableObjects();
			List<String> optionNames = options.stream()
					.map(geo -> geo == null ? "" : geo.toString(StringTemplate.defaultTemplate))
					.collect(Collectors.toList());
			ComponentDropDown linkedDropDown = new ComponentDropDown((AppW) app, "LinkedObject",
					optionNames, 0);
			linkedDropDown.addChangeHandler(() -> updateModel(linkedDropDown, options));
			linkedDropDown.setDisabled(options.size() < 2);
			linkedDropDown.setFullWidth(true);
			contentPanel.add(linkedDropDown);
		} else {
			contentPanel.add(scriptPanel);
		}

		if (!inputBox) {
			createChips(contentPanel);
			createObjectsHint(contentPanel);
		}

		addDialogContent(contentPanel);
	}

	private void initScriptArea() {
		scriptArea = new CodeMirrorEditorWidget();
		scriptArea.focusEditor();
		Dom.addEventListener(scriptArea.getElement(), "keyup", event -> {
			KeyboardEvent e = (KeyboardEvent) event;
			if ("Escape".equals(e.key) && objectsPopup != null && objectsPopup.isShowing()) {
				objectsPopup.hide();
			} else if ("@".equals(e.key)) {
				DOMRect pixelPosition = scriptArea.getCursorPixelPosition();
				fillAndShowObjectsPopup((int) pixelPosition.left,
						(int) (pixelPosition.bottom - ((AppW) app).getAbsTop()));
			} else if (!"Shift".equals(e.key) && objectsPopup != null
					&& objectsPopup.isShowing()) {
				objectsPopup.hide();
			}
		});
	}

	private void createChips(FlowPanel parentPanel) {
		Label suggestionsLabel = BaseWidgetFactory.INSTANCE.newSecondaryText("Suggestions",
				"suggestionLabel");

		FlowPanel chipsPanel = new FlowPanel();
		chipsPanel.addStyleName("chipsHolder");
		for (Commands command : chipsCommands) {
			ComponentChip chips = new ComponentChip(command.name(), null, true,
					() -> scriptArea.insertCommand(command.getCommand() + "()"));
			chipsPanel.add(chips);
		}

		parentPanel.add(suggestionsLabel);
		parentPanel.add(chipsPanel);
	}

	private void createObjectsHint(FlowPanel parentPanel) {
		StandardButton objectsHintButton = new StandardButton(MaterialDesignResources.INSTANCE
				.alternate_email().withFill(GeoGebraColorConstants.NEUTRAL_500.toString()),
				app.getLocalization().getMenu("Objects"), 16, 16);
		ComponentToast toast = new ComponentToast((AppW) app,
				app.getLocalization().getMenu("ButtonDialog.ObjectTooltip"));
		objectsHintButton.addDomHandler(event -> {
			getRootPanel().add(toast);
			toast.setPopupPosition(objectsHintButton.getAbsoluteLeft(),
					objectsHintButton.getAbsoluteTop() - objectsHintButton.getOffsetHeight()
							- TOOLTIP_HEIGHT - 2 * BUTTON_MARGIN);
			Scheduler.get().scheduleDeferred(() -> toast.addStyleName("fadeIn"));
		}, MouseOverEvent.getType());
		objectsHintButton.addDomHandler(event -> toast.hide(), MouseOutEvent.getType());
		objectsHintButton.addStyleName("objectsHintButton");
		parentPanel.add(objectsHintButton);
	}

	private void fillAndShowObjectsPopup(int left, int top) {
		FlowPanel objectsPanel = new FlowPanel();
		objectsPanel.addStyleName("objectsPanel");
		if (objectsPopup == null) {
			objectsPopup = new GPopupPanel(true, ((AppWFull) app).getAppletFrame(), app);
			objectsPopup.addStyleName("objectsPopup");
			objectsPopup.addAutoHidePartner(getElement());
		}

		TreeSet<GeoElement> geos = app.getKernel().getConstruction()
				.getGeoSetLabelOrder();
		if (geos.isEmpty()) {
			Label emptyLabel = BaseWidgetFactory.INSTANCE.newPrimaryText(
					"ButtonDialog.ObjectsNotFound", "noObjectsHint");
			objectsPanel.add(emptyLabel);
		} else {
			for (GeoElement geo : geos) {
				Label geoWidget =
						BaseWidgetFactory.INSTANCE.newPrimaryText(geo.getNameDescription());
				objectsPanel.add(geoWidget);
				Dom.addEventListener(geoWidget.getElement(), "click", event -> {
					scriptArea.insertGeoBox(geo.getLabelSimple());
					objectsPopup.hide();
				});
			}
		}
		Dom.toggleClass(objectsPopup, "empty", geos.isEmpty());

		objectsPopup.addAutoHidePartner(getElement());
		objectsPopup.clear();
		objectsPopup.add(objectsPanel);
		objectsPopup.show();
		objectsPopup.setPopupPosition(left, top);
	}

	/**
	 * Update linked geo in model.
	 * @param cbAdd list of geos
	 */
	protected void updateModel(ComponentDropDown cbAdd, ArrayList<GeoElement> options) {
		model.setLinkedGeo(options.get(cbAdd.getSelectedIndex()));
	}

	@Override
	public void hide() {
		super.hide();
		if (objectsPopup != null) {
			objectsPopup.hide();
		}
	}
}
