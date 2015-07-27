package org.geogebra.web.web.gui.dialog;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.dialog.ToolCreationDialogModel;
import org.geogebra.common.gui.dialog.ToolInputOutputListener;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.ToolNameIconPanel;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ToolCreationDialog extends DialogBoxW implements
        GeoElementSelectionListener, ClickHandler, ToolInputOutputListener {

	private App app;
	/**
	 * The underlying ToolModel, managing all input and output lists
	 */
	ToolCreationDialogModel toolModel;

	// Widgets
	private Button btBack, btNext, btCancel;
	private VerticalPanel mainWidget;
	private FlowPanel bottomWidget;
	private TabPanel tabPanel;
	private ListBox outputAddLB, outputLB;
	private ListBox inputAddLB, inputLB;
	private ToolNameIconPanel toolNameIconPanel;
	private Button btRemove;
	private Button btDown;
	private Button btUp;

	public ToolCreationDialog(App app) {
		super(false, false, null);
		this.setGlassEnabled(false);
		this.app = app;

		Macro appMacro = app.getMacro();
		if (appMacro != null) {
			// this.setFromMacro(appMacro); // TODO
		}

		createGUI();

		toolModel = new ToolCreationDialogModel(app, this);
	}

	@Override
	public void setVisible(boolean flag) {
		super.setVisible(flag);

		// add all possible output and input elements to add Lists
		toolModel.initAddLists();

		// add all currently selected geos to output list
		toolModel.addSelectedGeosToOutput();

		if (flag) {
			app.setMoveMode();
			app.setSelectionListenerMode(this);
		} else {
			app.setSelectionListenerMode(null);
		}
	}

	private void createGUI() {
		addStyleName("toolCreationDialog");
		addStyleName("GeoGebraPopup");

		getCaption().setText(app.getMenu("Tool.CreateNew"));

		setWidget(mainWidget = new VerticalPanel());
		mainWidget.add(tabPanel = new TabPanel());

		// Create panel with ListBoxes for input and output objects and
		// add ChangeHandler
		outputAddLB = new ListBox();
		outputAddLB.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				// added empty option at top, so use index-1
				toolModel.addToOutput(outputAddLB.getSelectedIndex() - 1);
			}
		});
		outputLB = new ListBox();
		VerticalPanel outputObjectPanel = createInputOutputPanel(outputAddLB,
		        outputLB);

		inputAddLB = new ListBox();
		inputAddLB.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				// added empty option at top, so use index-1
				toolModel.addToInput(inputAddLB.getSelectedIndex() - 1);
			}
		});
		inputLB = new ListBox();
		VerticalPanel inputObjectPanel = createInputOutputPanel(inputAddLB,
		        inputLB);

		toolNameIconPanel = new ToolNameIconPanel(app);

		// Create tabPanel and add Selectionhandler
		tabPanel.add(outputObjectPanel, app.getMenu("OutputObjects"));
		tabPanel.selectTab(0);
		tabPanel.add(inputObjectPanel, app.getMenu("InputObjects"));
		tabPanel.add(toolNameIconPanel, app.getMenu("NameIcon"));
		SelectionHandler<Integer> handler = getSelectionHandler();
		tabPanel.addSelectionHandler(handler);

		// Create button navigation
		createNavigation();

	}

	VerticalPanel createInputOutputPanel(ListBox addLB, ListBox lB) {
		lB.setVisibleItemCount(7);
		lB.setMultipleSelect(true);

		VerticalPanel inputPanel = new VerticalPanel();
		Label labelInputAdd = new Label(app.getMenu("Tool.SelectObjects"));
		inputPanel.add(labelInputAdd);

		FlowPanel inAddListUpDownPanel = new FlowPanel();
		inAddListUpDownPanel.add(addLB);

		HorizontalPanel inUpDownRemovePanel = new HorizontalPanel();
		inUpDownRemovePanel.add(lB);
		inUpDownRemovePanel.add(createListUpDownRemovePanel());
		inUpDownRemovePanel.setCellWidth(lB, "80%"); // TODO

		inAddListUpDownPanel.add(inUpDownRemovePanel);

		inputPanel.add(inAddListUpDownPanel);
		return inputPanel;
	}

	private VerticalPanel createListUpDownRemovePanel() {
		btUp = new Button("\u25b2");
		btUp.setTitle(app.getPlain("Up"));
		btUp.addClickHandler(this);
		btUp.getElement().getStyle().setMargin(3, Style.Unit.PX);

		btDown = new Button("\u25bc");
		btDown.setTitle(app.getPlain("Down"));
		btDown.addClickHandler(this);
		btDown.getElement().getStyle().setMargin(3, Style.Unit.PX);

		btRemove = new Button("\u2718");
		btRemove.setTitle(app.getPlain("Remove"));
		btRemove.addClickHandler(this);
		btRemove.getElement().getStyle().setMargin(3, Style.Unit.PX);

		VerticalPanel upDownRemovePanel = new VerticalPanel();
		upDownRemovePanel.add(btUp);
		upDownRemovePanel.add(btDown);
		upDownRemovePanel.add(btRemove);

		return upDownRemovePanel;
	}

	private SelectionHandler<Integer> getSelectionHandler() {
		SelectionHandler<Integer> handler = new SelectionHandler<Integer>() {

			public void onSelection(SelectionEvent<Integer> event) {
				int tab = event.getSelectedItem();

				btBack.setEnabled(tab > 0);

				switch (tab) {
				case 1: // input objects
					toolModel.updateInputList();
				case 0: // output objects
					btNext.setText(app.getPlain("Next") + " >");
					btNext.setEnabled(true);
					break;

				case 2: // name panel (finish)
					if (toolModel.createTool()) {
						btNext.setText(app.getPlain("Finish"));
						// if createTool succeeds there should be no way that
						// any list is empty...!?
						// btNext.setEnabled(inputList.size() > 0
						// && outputList.size() > 0);
						btNext.setEnabled(true);
						// namePanel.requestFocus();
					} else {
						btNext.setEnabled(false);
					}
					break;
				}
			}
		};
		return handler;
	}

	private void createNavigation() {
		mainWidget.add(bottomWidget = new FlowPanel());
		bottomWidget.setStyleName("DialogButtonPanel");
		// buttons
		btBack = new Button("< " + app.getPlain("Back"));
		btBack.addClickHandler(this);
		btBack.setEnabled(false);
		btBack.getElement().getStyle().setMargin(3, Style.Unit.PX);

		btNext = new Button(app.getPlain("Next") + " >");
		btNext.addClickHandler(this);
		btNext.getElement().getStyle().setMargin(3, Style.Unit.PX);

		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.addClickHandler(this);
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);

		bottomWidget.add(btBack);
		bottomWidget.add(btNext);
		bottomWidget.add(btCancel);

	}

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		int selectedTab = tabPanel.getTabBar().getSelectedTab();
		switch (selectedTab) {
		case 0: // output objects
			toolModel.addToOutput(geo);
			break;

		case 1: // input objects
			toolModel.addToInput(geo);
			break;
		}
	}

	public void onClick(ClickEvent e) {
		Element target = e.getNativeEvent().getEventTarget().cast();
		int selectedTab = tabPanel.getTabBar().getSelectedTab();

		if (target == btBack.getElement()) {
			tabPanel.selectTab(selectedTab - 1);
		} else if (target == btNext.getElement()) {
			if (selectedTab == tabPanel.getTabBar().getTabCount() - 1) {
				finish();
			} else {
				tabPanel.selectTab(selectedTab + 1);
			}
		} else if (target == btCancel.getElement()) {
			hide();
			app.getActiveEuclidianView().requestFocusInWindow();
		} else {
			ArrayList<Integer> selIndices = new ArrayList<Integer>();
			switch (selectedTab) {
			case 0:
				if (outputLB.getSelectedIndex() >= 0) {
					for (int i = 0; i < outputLB.getItemCount(); i++) {
						if (outputLB.isItemSelected(i)) {
							selIndices.add(i);
						}
					}
					if (target.getTitle().equals(app.getPlain("Down"))) {
						toolModel.moveOutputDown(selIndices);
					} else if (target.getTitle().equals(app.getPlain("Up"))) {
						toolModel.moveOutputUp(selIndices);
					} else if (target.getTitle().equals(app.getPlain("Remove"))) {
						toolModel.removeFromOutput(selIndices);
					}
				}
				break;
			case 1:
				selIndices = new ArrayList<Integer>();
				if (inputLB.getSelectedIndex() >= 0) {
					for (int i = 0; i < inputLB.getItemCount(); i++) {
						if (inputLB.isItemSelected(i)) {
							selIndices.add(i);
						}
					}
					if (target.getTitle().equals(app.getPlain("Down"))) {
						toolModel.moveInputDown(selIndices);
					} else if (target.getTitle().equals(app.getPlain("Up"))) {
						toolModel.moveInputUp(selIndices);
					} else if (target.getTitle().equals(app.getPlain("Remove"))) {
						toolModel.removeFromInput(selIndices);
					}
				}
			}
		}
	}

	private void finish() {
		App appToSave = app;
		if (app.getMacro() != null) {
			appToSave = app.getMacro().getKernel().getApplication();
		}

		String commandName = toolNameIconPanel.getCommandName();
		String toolName = toolNameIconPanel.getToolName();
		String toolHelp = toolNameIconPanel.getToolHelp();
		boolean showInToolBar = toolNameIconPanel.getShowTool();
		String iconFileName = toolNameIconPanel.getIconFileName();

		toolModel.finish(app, commandName, toolName, toolHelp,
		        showInToolBar, iconFileName);
		AppW w = (AppW) app;
		// if (w.isToolLoadedFromStorage()) {
		// w.storeMacro(app.getMacro(), true);
		//
		// }
		// Not working:
		// app.showMessage(app.getMenu("Tool.CreationSuccess"));
		setVisible(false);
		// TODO: call to toolModel.overwriteMacro
	}

	public void updateLists() {
		updateListBox(outputAddLB, toolModel.getOutputAddList(), true);
		updateListBox(outputLB, toolModel.getOutputList(), false);
		updateListBox(inputAddLB, toolModel.getInputAddList(), true);
		updateListBox(inputLB, toolModel.getInputList(), false);
	}

	private static void updateListBox(ListBox lb, GeoElement[] geos,
	        boolean addList) {
		App.debug("Call to ToolCreationDialog.updateListBox");
		lb.clear();
		if (addList) {
			lb.addItem(" ");
		}
		for (int i = 0; i < geos.length; i++) {
			lb.addItem(geos[i].getLongDescription());
			SelectElement selectElement = SelectElement.as(lb.getElement());
			NodeList<OptionElement> options = selectElement.getOptions();
			options.getItem(options.getLength() - 1).getStyle()
					.setColor(GColor.getColorString(geos[i].getAlgebraColor()));
		}
	}

}
