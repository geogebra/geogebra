package org.geogebra.web.full.gui.dialog.tools;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.dialog.ToolCreationDialogModel;
import org.geogebra.common.gui.dialog.ToolInputOutputListener;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.dom.client.NodeList;
import org.gwtproject.dom.client.OptionElement;
import org.gwtproject.dom.client.SelectElement;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.logical.shared.SelectionHandler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.ListBox;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.URL;
import jsinterop.base.JsPropertyMap;

/**
 * Dialog to create a new user defined tool
 */
public class ToolCreationDialogW extends ComponentDialog implements
		GeoElementSelectionListener, ToolInputOutputListener {

	private final AppW appw;
	/**
	 * The underlying ToolModel, managing all input and output lists
	 */
	ToolCreationDialogModel toolModel;

	// Widgets
	private StandardButton btBack;
	private StandardButton btNext;
	private MultiRowsTabPanel tabPanel;
	private ListBox outputAddLB;
	private ListBox outputLB;
	private ListBox inputAddLB;
	private ListBox inputLB;
	private ToolNameIconPanelW toolNameIconPanel;
	private final Localization loc;

	/**
	 * Creates new tool creation dialog, if in macro-editing mode,
	 * 
	 * @param app
	 *            Aplication to which this dialog belongs
	 */

	public ToolCreationDialogW(AppW app) {
		super(app, new DialogData("Tool.CreateNew", "Cancel", null), false, false);
		this.appw = app;
		this.loc = app.getLocalization();
		createGUI();

		toolModel = new ToolCreationDialogModel(app, this);

		Macro appMacro = app.getEditMacro();
		if (appMacro != null) {
			this.setFromMacro(appMacro); // TODO
		}
	}

	private void setFromMacro(Macro macro) {
		toolNameIconPanel.setMacro(macro);
		toolModel.setFromMacro(macro);
	}

	@Override
	public void setVisible(boolean flag) {
		super.setVisible(flag);

		if (flag) {
			// add all possible output and input elements to add Lists
			toolModel.initAddLists();

			// add all currently selected geos to output list
			toolModel.addSelectedGeosToOutput();

			appw.setMoveMode();
			appw.getSelectionManager().addSelectionListener(this);
		} else {
			appw.getSelectionManager().removeSelectionListener(this);
		}
	}

	private void createGUI() {
		addStyleName("toolCreationDialog");

		addDialogContent(tabPanel = new MultiRowsTabPanel("dialogThreeTabs"));

		// Create panel with ListBoxes for input and output objects and
		// add ChangeHandler
		outputAddLB = new ListBox();
		outputAddLB.addChangeHandler(event -> {
			// added empty option at top, so use index-1
			toolModel.addToOutput(outputAddLB.getSelectedIndex() - 1);
		});
		outputLB = new ListBox();
		FlowPanel outputObjectPanel = createInputOutputPanel(outputAddLB,
				outputLB, true);

		inputAddLB = new ListBox();
		inputAddLB.addChangeHandler(event -> {
			// added empty option at top, so use index-1
			toolModel.addToInput(inputAddLB.getSelectedIndex() - 1);
		});
		inputLB = new ListBox();
		FlowPanel inputObjectPanel = createInputOutputPanel(inputAddLB,
				inputLB, false);

		toolNameIconPanel = new ToolNameIconPanelW(appw, this);
		toolNameIconPanel.addStyleName("toolCreationDialogTab");

		// Create tabPanel and add Selectionhandler
		tabPanel.add(outputObjectPanel, loc.getMenu("OutputObjects"));
		tabPanel.selectTab(0);
		tabPanel.add(inputObjectPanel, loc.getMenu("InputObjects"));
		tabPanel.add(toolNameIconPanel, loc.getMenu("NameIcon"));
		SelectionHandler<Integer> handler = getSelectionHandler();
		tabPanel.addSelectionHandler(handler);

		// Create button navigation
		createNavigation();
	}

	/**
	 * Assembles the input or output listboxes in a panel adding the
	 * up/down/remove buttons on the right
	 * 
	 * @param addLB
	 *            dropdown Listbox for adding the inputs or outputs
	 * @param objectMultiselect
	 *            multiselect, multiline Listbox with the used input and output
	 *            elements
	 * @return the panel containing the listboxes and controls
	 */
	FlowPanel createInputOutputPanel(ListBox addLB,
			final ListBox objectMultiselect, final boolean output) {
		objectMultiselect.setVisibleItemCount(9);
		objectMultiselect.setMultipleSelect(true);

		FlowPanel inputPanel = new FlowPanel();
		Label labelInputAdd = new Label(loc.getMenu("Tool.SelectObjects"));
		labelInputAdd.addStyleName("toolSelectObjectLabel");
		inputPanel.add(labelInputAdd);

		FlowPanel addListUpDownPanel = new FlowPanel();
		addListUpDownPanel.add(addLB);

		FlowPanel objectMultiselectPanel = new FlowPanel();
		objectMultiselectPanel.addStyleName("multiSelectList");
		objectMultiselectPanel.add(objectMultiselect);
		objectMultiselectPanel.add(new MultiSelectButtonsPanel(
				new MultiSelectButtonsPanel.ButtonsListener() {
					@Override
					public void moveSelection(boolean up) {
						if (up) {
							toolModel.moveUp(selIndices(objectMultiselect), output);
						} else {
							toolModel.moveDown(selIndices(objectMultiselect), output);
						}
					}

					@Override
					public void deleteSelection() {
						toolModel.removeFromList(selIndices(objectMultiselect), output);
					}
				}));

		addListUpDownPanel.add(objectMultiselectPanel);

		inputPanel.add(addListUpDownPanel);
		return inputPanel;
	}

	private SelectionHandler<Integer> getSelectionHandler() {
		return event -> {
			int tab = event.getSelectedItem();

			updateBackNextButtons(tab);
		};
	}

	/**
	 * @param tab
	 *            selected tab
	 */
	protected void updateBackNextButtons(int tab) {
		btBack.setEnabled(tab > 0);

		switch (tab) {
		case 1: // input objects
			toolModel.updateInputList();
			//$FALL-THROUGH$
		case 0: // output objects
			btNext.setText(loc.getMenu("Next") + " >");
			btNext.setEnabled(true);
			break;

		case 2: // name panel (finish)
			if (toolModel.createTool()) {
				btNext.setText(loc.getMenu("Finish"));
				btNext.setEnabled(true);
			} else {
				btNext.setEnabled(false);
			}
			break;

		default:
			break;
		}

	}

	private void createNavigation() {
		FlowPanel bottomWidget;
		addDialogContent(bottomWidget = new FlowPanel());
		bottomWidget.setStyleName("dialogNavigation");
		// buttons
		btBack = new StandardButton("< " + loc.getMenu("Back"));
		btBack.addStyleName("materialOutlinedButton ");
		btBack.addFastClickHandler(e ->
			tabPanel.selectTab(getSelectedTab() - 1)
		);
		btBack.setEnabled(false);
		btBack.getElement().getStyle().setMargin(3, Unit.PX);

		btNext = new StandardButton(loc.getMenu("Next") + " >");
		btNext.addStyleName("materialOutlinedButton ");
		btNext.addFastClickHandler(e -> {
			if (getSelectedTab() == tabPanel.getTabBar().getWidgetCount() - 1) {
				finish();
			} else {
				tabPanel.selectTab(getSelectedTab() + 1);
			}
		});

		setOnNegativeAction(() -> {
			setVisible(false); // a bit redundant, we call hide afterwards
			requestFocus();
		});

		bottomWidget.add(btBack);
		bottomWidget.add(btNext);
	}

	private int getSelectedTab() {
		return tabPanel.getTabBar().getSelectedTab();
	}

	@Override
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		switch (getSelectedTab()) {
		case 0: // output objects
			toolModel.addToOutput(geo);
			break;

		case 1: // input objects
			toolModel.addToInput(geo);
			break;
		default:

		}
	}

	private ArrayList<Integer> selIndices(ListBox listPanel) {
		ArrayList<Integer> selIndices = new ArrayList<>();
		if (listPanel.getSelectedIndex() >= 0) {
			for (int i = 0; i < listPanel.getItemCount(); i++) {
				if (listPanel.isItemSelected(i)) {
					selIndices.add(i);
				}
			}

		}
		return selIndices;
	}

	private void finish() {
		final App appToSave;
		if (appw.getEditMacro() != null) {
			appToSave = appw.getEditMacro().getKernel().getApplication();
		} else {
			appToSave = appw;
		}

		final String commandName = toolNameIconPanel.getCommandName();
		if (appToSave.getKernel().getMacro(commandName) != null) {
			DialogData data = new DialogData("Question", "Cancel", "Tool.Replace");
			ComponentDialog dialog = new ComponentDialog(appw, data, false, true);
			Label message = new Label(appw.getLocalization().getPlain(
					"Tool.ReplaceQuestion", commandName));
			dialog.addDialogContent(message);
			dialog.setOnPositiveAction(() -> saveMacro(appToSave));
			dialog.show();
		} else {
			saveMacro(appToSave);
		}
	}

	/**
	 * Finish creation of user defined tool. Overwrites an existing macro with
	 * the macro (without warning) if macros are compatible
	 * 
	 * @param appToSave
	 *            application
	 */
	void saveMacro(final App appToSave) {
		final String commandName = toolNameIconPanel.getCommandName();
		final String toolName = toolNameIconPanel.getToolName();
		final String toolHelp = toolNameIconPanel.getToolHelp();
		final boolean showInToolBar = toolNameIconPanel.getShowTool();
		final String iconFileName = toolNameIconPanel.getIconFileName();

		boolean success = toolModel.finish(appToSave, commandName, toolName,
				toolHelp, showInToolBar, iconFileName);
		if (success) {
			((AppW) app).getToolTipManager().showBottomMessage(
					loc.getMenu("Tool.CreationSuccess"), appw);
		} else {
			DialogData data = new DialogData(appw.getLocalization().getError("Error"),
					null, "OK");
			ComponentDialog dialog = new ComponentDialog(appw, data, false, true);
			Label label = new Label(loc.getMenu("Tool.NotCompatible"));
			dialog.addDialogContent(label);
			dialog.show();
		}

		if (appw.isOpenedForMacroEditing()) {
			Macro editMacro = toolModel.getNewTool();
			String editMacroName = editMacro.getEditName();
			String editMacroPreviousName = appw.getEditMacroPreviousName();
			if (!editMacroPreviousName.equals(editMacroName)) {
				appw.removeMacro(editMacroPreviousName);
				appw.storeMacro(editMacro);
				appw.setEditMacroPreviousName(editMacroName);
				DomGlobal.document.title = editMacroName;
				URL url = new URL(DomGlobal.location.href);
				if (url.searchParams != null) {
					url.searchParams.set(AppW.EDIT_MACRO_URL_PARAM_NAME, editMacroName);
					appw.updateURL(url);
				}
			}
			StringBuilder xml = new StringBuilder();
			editMacro.getXML(xml);
			JsPropertyMap<Object> message = JsPropertyMap.of();
			message.set(AppW.EDITED_MACRO_NAME_KEY, editMacroName);
			message.set(AppW.EDITED_MACRO_XML_KEY, xml.toString());
			DomGlobal.window.opener.postMessage(Global.JSON.stringify(message), "*");
		}
		if (success) {
			setVisible(false);
			requestFocus();
			hide();
		}
	}

	private void requestFocus() {
		appw.getActiveEuclidianView().requestFocusInWindow();
	}

	@Override
	public void updateLists() {
		updateListBox(outputAddLB, toolModel.getOutputAddList(), true);
		updateListBox(outputLB, toolModel.getOutputList(), false);
		updateListBox(inputAddLB, toolModel.getInputAddList(), true);
		updateListBox(inputLB, toolModel.getInputList(), false);
	}

	private static void updateListBox(ListBox lb, GeoElementND[] geos,
			boolean addList) {
		lb.clear();
		if (addList) {
			lb.addItem(" ");
		}
		for (GeoElementND geo : geos) {
			lb.addItem(geo.getLongDescription());
			SelectElement selectElement = SelectElement.as(lb.getElement());
			NodeList<OptionElement> options = selectElement.getOptions();
			options.getItem(options.getLength() - 1).getStyle()
					.setColor(GColor.getColorString(geo.getAlgebraColor()));
		}
	}
}
