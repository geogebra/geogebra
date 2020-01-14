package org.geogebra.web.full.gui.dialog;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.dialog.ToolCreationDialogModel;
import org.geogebra.common.gui.dialog.ToolInputOutputListener;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.ToolNameIconPanelW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

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

/**
 * Dialog to create a new user defined tool
 */
public class ToolCreationDialogW extends DialogBoxW implements
		GeoElementSelectionListener, ClickHandler, ToolInputOutputListener {

	private AppW appw;
	/**
	 * The underlying ToolModel, managing all input and output lists
	 */
	ToolCreationDialogModel toolModel;

	// Widgets
	private Button btBack;
	private Button btNext;
	private Button btCancel;
	private VerticalPanel mainWidget;
	private FlowPanel bottomWidget;
	private TabPanel tabPanel;
	private ListBox outputAddLB;
	private ListBox outputLB;
	private ListBox inputAddLB;
	private ListBox inputLB;
	private ToolNameIconPanelW toolNameIconPanel;
	private AsyncOperation<Macro> returnHandler;
	private Localization loc;

	/**
	 * Creates new tool creation dialog, if in macro-editing mode,
	 * 
	 * @param app
	 *            Aplication to which this dialog belongs
	 */

	public ToolCreationDialogW(App app) {
		super(false, false, null, ((AppW) app).getPanel(), app);

		this.appw = (AppW) app;
		this.loc = app.getLocalization();
		createGUI();

		toolModel = new ToolCreationDialogModel(app, this);

		Macro appMacro = app.getMacro();
		if (appMacro != null) {
			this.setFromMacro(appMacro); // TODO
		}
	}

	private void setFromMacro(Macro macro) {
		toolNameIconPanel.setMacro(macro);
		toolModel.setFromMacro(macro);
	}

	/**
	 * Creates new tool creation dialog, if in macro-editing mode, if launched
	 * from another Dialog this can be use to return to that dialog again. The
	 * returnHandler is passed the newly created {@link Macro} if successful or
	 * null if unsuccessful.
	 * 
	 * @param app
	 *            Application to which this dialog belongs
	 * @param returnHandler
	 *            the {@link AsyncOperation} handling the resulting
	 *            {@link Macro}
	 * 
	 */
	public ToolCreationDialogW(AppW app, AsyncOperation<Macro> returnHandler) {
		this(app);
		this.returnHandler = returnHandler;
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
		addStyleName("GeoGebraPopup");

		getCaption().setText(loc.getMenu("Tool.CreateNew"));

		setWidget(mainWidget = new VerticalPanel());
		mainWidget.add(tabPanel = new TabPanel());

		// Create panel with ListBoxes for input and output objects and
		// add ChangeHandler
		outputAddLB = new ListBox();
		outputAddLB.addChangeHandler(new ChangeHandler() {
			@Override
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
			@Override
			public void onChange(ChangeEvent event) {
				// added empty option at top, so use index-1
				toolModel.addToInput(inputAddLB.getSelectedIndex() - 1);
			}
		});
		inputLB = new ListBox();
		VerticalPanel inputObjectPanel = createInputOutputPanel(inputAddLB,
				inputLB);

		toolNameIconPanel = new ToolNameIconPanelW(appw);

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
	 * Assembles the input or output listboxes in a VerticalPanel adding the
	 * up/down/remove buttons on the right
	 * 
	 * @param addLB
	 *            dropdown Listbox for adding the inputs or outputs
	 * @param lB
	 *            multiselect, multiline Listbox with the used input and output
	 *            elements
	 * @return the panel containing the listboxes and controls
	 */
	VerticalPanel createInputOutputPanel(ListBox addLB, ListBox lB) {
		lB.setVisibleItemCount(7);
		lB.setMultipleSelect(true);

		VerticalPanel inputPanel = new VerticalPanel();
		Label labelInputAdd = new Label(loc.getMenu("Tool.SelectObjects"));
		inputPanel.add(labelInputAdd);

		FlowPanel addListUpDownPanel = new FlowPanel();
		addListUpDownPanel.add(addLB);

		HorizontalPanel upDownRemovePanel = new HorizontalPanel();
		upDownRemovePanel.add(lB);
		upDownRemovePanel.add(createListUpDownRemovePanel());
		upDownRemovePanel.setCellWidth(lB, "80%"); // TODO

		addListUpDownPanel.add(upDownRemovePanel);

		inputPanel.add(addListUpDownPanel);
		return inputPanel;
	}

	private VerticalPanel createListUpDownRemovePanel() {
		Button btUp = new Button("\u25b2");
		btUp.setTitle(loc.getMenu("Up"));
		btUp.addClickHandler(this);
		btUp.getElement().getStyle().setMargin(3, Style.Unit.PX);

		Button btDown = new Button("\u25bc");
		btDown.setTitle(loc.getMenu("Down"));
		btDown.addClickHandler(this);
		btDown.getElement().getStyle().setMargin(3, Style.Unit.PX);

		Button btRemove = new Button("\u2718");
		btRemove.setTitle(loc.getMenu("Remove"));
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

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int tab = event.getSelectedItem();

				updateBackNextButtons(tab);
			}
		};
		return handler;
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
		mainWidget.add(bottomWidget = new FlowPanel());
		bottomWidget.setStyleName("DialogButtonPanel");
		// buttons
		btBack = new Button("< " + loc.getMenu("Back"));
		btBack.addClickHandler(this);
		btBack.setEnabled(false);
		btBack.getElement().getStyle().setMargin(3, Style.Unit.PX);

		btNext = new Button(loc.getMenu("Next") + " >");
		btNext.addClickHandler(this);
		btNext.getElement().getStyle().setMargin(3, Style.Unit.PX);

		btCancel = new Button(loc.getMenu("Cancel"));
		btCancel.addStyleName("cancelBtn");
		btCancel.addClickHandler(this);
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);

		bottomWidget.add(btBack);
		bottomWidget.add(btNext);
		bottomWidget.add(btCancel);
	}

	@Override
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		int selectedTab = tabPanel.getTabBar().getSelectedTab();
		switch (selectedTab) {
		case 0: // output objects
			toolModel.addToOutput(geo);
			break;

		case 1: // input objects
			toolModel.addToInput(geo);
			break;
		default:

		}
	}

	@Override
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
			setVisible(false);
			callHandler();
			hide();
		} else {
			ArrayList<Integer> selIndices = new ArrayList<>();
			switch (selectedTab) {
			default:
				// do nothing
				break;
			case 0:
				updateList(outputLB, true, target, selIndices);
				break;
			case 1:
				updateList(inputLB, false, target, selIndices);
				break;
			}
		}
	}

	private void updateList(ListBox listPanel, boolean output, Element target,
			ArrayList<Integer> selIndices) {
		if (listPanel.getSelectedIndex() >= 0) {
			for (int i = 0; i < listPanel.getItemCount(); i++) {
				if (listPanel.isItemSelected(i)) {
					selIndices.add(i);
				}
			}
			if (target.getTitle().equals(loc.getMenu("Down"))) {
				toolModel.moveDown(selIndices, output);
			} else if (target.getTitle().equals(loc.getMenu("Up"))) {
				toolModel.moveUp(selIndices, output);
			} else if (target.getTitle().equals(loc.getMenu("Remove"))) {
				toolModel.removeFromList(selIndices, output);
			}
		}
	}

	private void finish() {
		final App appToSave;
		if (appw.getMacro() != null) {
			appToSave = appw.getMacro().getKernel().getApplication();
		} else {
			appToSave = appw;
		}

		final String commandName = toolNameIconPanel.getCommandName();
		if (appToSave.getKernel().getMacro(commandName) != null) {
			String[] options = { loc.getMenu("Tool.Replace"),
					loc.getMenu("Tool.DontReplace") };
			appw.getGuiManager()
					.getOptionPane()
					.showOptionDialog(
							appw.getLocalization().getPlain(
									"Tool.ReplaceQuestion", commandName),
							loc.getMenu("Question"),
 0,
							GOptionPane.QUESTION_MESSAGE, null, options,
							new AsyncOperation<String[]>() {

								@Override
								public void callback(String[] dialogResult) {
									if ("0".equals(dialogResult[0])) {
										saveMacro(appToSave);
									}
								}
							});
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
			if (returnHandler == null) {
				ToolTipManagerW.sharedInstance().showBottomMessage(
						loc.getMenu("Tool.CreationSuccess"), true, appw);

			}
		} else {
			appw.getGuiManager()
					.getOptionPane()
					.showConfirmDialog(loc.getMenu("Tool.NotCompatible"),
							appw.getLocalization().getError("Error"),
							GOptionPane.OK_OPTION, GOptionPane.ERROR_MESSAGE,
							null);
		}

		if (appw.isToolLoadedFromStorage()) {
			appw.storeMacro(appw.getMacro(), true);
		}
		if (success) {
			setVisible(false);
			callHandler();
			hide();
		}

	}

	private void callHandler() {
		if (returnHandler != null) {
			returnHandler.callback(toolModel.getNewTool());
			returnHandler = null;
		} else {
			appw.getActiveEuclidianView().requestFocusInWindow();
		}
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
		for (int i = 0; i < geos.length; i++) {
			lb.addItem(geos[i].getLongDescription());
			SelectElement selectElement = SelectElement.as(lb.getElement());
			NodeList<OptionElement> options = selectElement.getOptions();
			options.getItem(options.getLength() - 1).getStyle()
					.setColor(GColor.getColorString(geos[i].getAlgebraColor()));
		}
	}

}
