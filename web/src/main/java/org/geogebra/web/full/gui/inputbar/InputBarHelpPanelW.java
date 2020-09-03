package org.geogebra.web.full.gui.inputbar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.inputbar.InputBarHelpPanel;
import org.geogebra.common.gui.util.TableSymbols;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author G. Sturr
 * 
 */
public class InputBarHelpPanelW extends VerticalPanel implements SetLabels, BooleanRenderable {

	private AppW app;
	private Tree indexTree;
	private VerticalPanel syntaxPanel;
	private Button btnOnlineHelp;
	private Button btnClose;
	private LocaleSensitiveComparator comparator;
	private SplitLayoutPanel sp;
	private ScrollPanel detailScroller;
	private InlineLabel lblSyntax;
	private MyTreeItem itmFunction;
	private AutoCompleteW inputField;
	private InputBarHelpPanel hp;

	/**
	 * @param app
	 *            application
	 */
	public InputBarHelpPanelW(AppW app) {
		super();
		this.app = app;
		comparator = new LocaleSensitiveComparator();
		hp = new InputBarHelpPanel(app);
		createGUI();
		setLabels();
	}

	/**
	 * @param field
	 *            input field
	 */
	public void setInputField(AutoCompleteW field) {
		this.inputField = field;
	}

	private void createGUI() {
		// create syntax panel
		syntaxPanel = new VerticalPanel();

		// button panel
		FlowPanel pnlButton = new FlowPanel();
		pnlButton.getElement().getStyle().setFloat(Style.Float.RIGHT);

		// create help button
		btnOnlineHelp = new Button(app.getLocalization().getMenu("ShowOnlineHelp"));
			btnOnlineHelp.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					openOnlineHelp();
				}
			});
		render(app.getNetworkOperation().isOnline());
		app.getNetworkOperation().getView().add(this);
		btnOnlineHelp.addStyleName("inputHelp-OnlineHelpBtn");
		pnlButton.add(btnOnlineHelp);

		// create close button
		btnClose = new Button(app.getLocalization().getMenu("Close"));
		btnClose.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		btnClose.setStyleName("inputHelp-CancelBtn");
		pnlButton.add(btnClose);

		// create detail title panel
		lblSyntax = new InlineLabel();
		lblSyntax.getElement().getStyle().setTextAlign(TextAlign.LEFT);

		HorizontalPanel detailTitlePanel = new HorizontalPanel();
		detailTitlePanel
		        .setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		detailTitlePanel.add(lblSyntax);
		detailTitlePanel.add(pnlButton);
		detailTitlePanel.addStyleName("inputHelp-detailPanelTitle");

		add(detailTitlePanel);
		// create the detail panel
		VerticalPanel detailPanel = new VerticalPanel();
		detailPanel.add(syntaxPanel);
		detailPanel.setWidth("100%");

		// create the index tree and put it in a scroll panel
		indexTree = new Tree() {
			@Override
			public void setSelectedItem(TreeItem item, boolean fireEvents) {
				if (item == null) {
					super.setSelectedItem(null, fireEvents);
					return;
				}
				onSelectionNative(item, fireEvents);
			}

			private native void onSelectionNative(TreeItem item, boolean fireEvents) /*-{
				this.@com.google.gwt.user.client.ui.Tree::onSelection(Lcom/google/gwt/user/client/ui/TreeItem;ZZ)(item, fireEvents, false);
			}-*/;
		};

		indexTree.addStyleName("inputHelp-tree");
		indexTree.setAnimationEnabled(true);

		// show only mathematical functions for exam simple calculator
		if (app.getAppletParameters().hasDataParamEnableGraphing()
				&& !app.getAppletParameters().getDataParamEnableGraphing(true)) {
			detailScroller = new ScrollPanel(detailPanel);
			detailScroller.setStyleName("AVHelpDetailScroller");
			add(detailScroller);
		} else {
			ScrollPanel treeScroller = new ScrollPanel(indexTree);
			treeScroller.setSize("100%", "100%");

			// put the detail panel and index tree side by side in a
			// SplitLayoutPanel
			sp = new SplitLayoutPanel();
			sp.addStyleName("ggbdockpanelhack");
			sp.addEast(treeScroller, 280);
			sp.add(new ScrollPanel(detailPanel));

			// now add the split panel to our main panel
			add(sp);
		}
	}

	@Override
	public void render(boolean online) {
		btnOnlineHelp.setEnabled(online);
	}

	void showOnlineHelpButton(boolean show) {
		btnOnlineHelp.setVisible(show);
	}

	// =================================================================
	// Getters/Setters & Event Handlers
	// =================================================================

	/**
	 * Opens browser with online help
	 */
	protected void openOnlineHelp() {
		if (getSelectedCommand() == null) {
			app.getGuiManager().openHelp("InputBar");

		} else if (getSelectedCommand().equals(
		        app.getLocalization().getMenu("MathematicalFunctions"))) {
			app.getGuiManager().openHelp(App.WIKI_OPERATORS);

		} else {
			app.getGuiManager()
			        .openCommandHelp(getSelectedCommand());
		}
	}

	/**
	 * Hide the parent popup
	 */
	protected void hide() {
		((InputBarHelpPopup) this.getParent()).hide();
	}

	/**
	 * @return local command name
	 */
	public String getSelectedCommand() {
		if (indexTree == null || indexTree.getSelectedItem() == null
		        || indexTree.getSelectedItem().getChildCount() > 0) {
			return null;
		}
		return indexTree.getSelectedItem().getWidget().getElement()
		        .getInnerText();
	}

	@Override
	public void setLabels() {
		setCommands();
		// show Mathematical Functions tree item initially
		indexTree.setSelectedItem(itmFunction);
		updateDetailPanel();
		btnOnlineHelp.setText(app.getLocalization().getMenu("ShowOnlineHelp"));
		btnClose.setText(app.getLocalization().getMenu("Close"));
	}

	/**
	 * Adjusts the panel size relative to the current application panel size
	 * 
	 * @param maxOffsetHeight
	 *            max height
	 */
	public void updateGUI(int maxOffsetHeight) {
		showOnlineHelpButton(!app.isExam() && app.showMenuBar());
		int height = maxOffsetHeight - 60;
		double width = ((GuiManagerW) app.getGuiManager()).getRootComponent()
				.getOffsetWidth() - 60;

		if (app.getAppletParameters().hasDataParamEnableGraphing()
				&& !app.getAppletParameters().getDataParamEnableGraphing(true)) {
			int w = (int) Math.min(400, width);
			detailScroller.setPixelSize(w, height);
		} else {
			int w = (int) Math.min(700, width);
			sp.setPixelSize(w, height);
		}
	}
	
	/**
	 * @param scale
	 *            scale
	 * @return pixel width
	 */
	public int getPreferredWidth(double scale) {
		double width = ((GuiManagerW) app.getGuiManager()).getRootComponent()
				.getOffsetWidth() * scale - 60;

		if (app.getAppletParameters().hasDataParamEnableGraphing()
				&& !app.getAppletParameters().getDataParamEnableGraphing(true)) {
			return (int) Math.min(400, width);
		}
		
		return (int) Math.min(700, width);
	}

	// =================================================================
	// Index Tree
	// =================================================================

	/**
	 * Update commands tree
	 */
	public void setCommands() {

		indexTree.clear();

		itmFunction = new MyTreeItem();
		itmFunction.setWidget(new TreeItemButton(
				app.getLocalization().getMenu("MathematicalFunctions"),
				itmFunction, false));
		indexTree.addItem(itmFunction);

		MyTreeItem itmAllCommands = new MyTreeItem();
		itmAllCommands.setWidget(new TreeItemButton(app.getLocalization()
		        .getMenu("AllCommands"), itmAllCommands, false));

		addCmdNames(itmAllCommands, getAllCommandsTreeSet());
		indexTree.addItem(itmAllCommands);

		for (int index = 0; index < hp.getCategoriesCount(); index++) {
			TreeSet<String> cmdNames = InputBarHelpPanel.getCommandTreeMap(app,
					comparator, index);

			if (cmdNames != null) {
				String cmdSetName = app.getKernel().getAlgebraProcessor()
						.getSubCommandSetName(index);
				TreeItem itmCmdSet = new MyTreeItem();
				itmCmdSet.setWidget(
						new TreeItemButton(cmdSetName, itmCmdSet, false));
				// add command set branch to tree
				indexTree.addItem(itmCmdSet);
				// add command names to this branch
				addCmdNames(itmCmdSet, cmdNames);
			}
		}
	}

	private void addCmdNames(TreeItem item, TreeSet<String> names) {
		for (String cmdName : names) {
			if (cmdName != null && cmdName.length() > 0) {
				MyTreeItem cmd = new MyTreeItem();
				cmd.setWidget(new TreeItemButton(cmdName, cmd, true));
				item.addItem(cmd);
			}
		}
	}

	private class TreeItemButton extends InlineLabel {

		public TreeItemButton(String text, final TreeItem item,
		        final boolean isLeaf) {
			super(text);
			addStyleName("inputHelp-treeItem");

			if (isLeaf) {
				addStyleName("inputHelp-leaf");
			}

			this.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (!isLeaf) {
						item.setState(!item.getState());
						updateDetailPanel();
					} else {
						item.setState(true);
						updateDetailPanel();
					}
				}
			});
		}
	}

	private static class MyTreeItem extends TreeItem {

		protected MyTreeItem() {
			// avoid synth access warning
		}

		@Override
		public void setWidget(Widget newWidget) {
			super.setWidget(newWidget);
			this.addStyleName("inputHelp-treeItem");
		}

	}

	// =================================================================
	// Command Name Sorting
	// =================================================================

	/**
	 * Javascript comparator for different locales.
	 * 
	 * TODO: handle accented characters
	 */
	@SuppressFBWarnings({ "SE_COMPARATOR_SHOULD_BE_SERIALIZABLE",
			"not needed" })
	private static class LocaleSensitiveComparator
			implements Comparator<String> {

		protected LocaleSensitiveComparator() {
			// avoid synth access warning
		}

		@Override
		public native int compare(String source, String target) /*-{
			return source.localeCompare(target);
		}-*/;
	}

	private TreeSet<String> getAllCommandsTreeSet() {
		return InputBarHelpPanel.getAllCommandsTreeSet(app, comparator);
	}

	// =================================================================
	// Syntax Description
	// =================================================================

	/**
	 * Update syntax panel
	 */
	protected void updateDetailPanel() {
		syntaxPanel.clear();
		if (getSelectedCommand() == null) {
			
			lblSyntax.setText("");
			
			return;
		}

		lblSyntax.setText(getSelectedCommand());
		ArrayList<Widget> rows;
		if (getSelectedCommand().equals(
		        app.getLocalization().getMenu("MathematicalFunctions"))) {
			rows = functionTableHTML();
			
			syntaxPanel.removeStyleName("inputHelp-cmdSyntax");
			syntaxPanel.addStyleName("inputHelp-functionTable");

		} else {

			rows = cmdSyntaxHTML();
			syntaxPanel.removeStyleName("inputHelp-functionTable");
			syntaxPanel.addStyleName("inputHelp-cmdSyntax");
		}
		for (int i = 0; i < rows.size(); i++) {
			syntaxPanel.add(rows.get(i));
		}
	}

	private ArrayList<Widget> cmdSyntaxHTML() {
		ArrayList<Widget> ret = new ArrayList<>();

		// internal name of selected command
		String cmd = app.getReverseCommand(getSelectedCommand());
		
		Localization loc = app.getLocalization();

		String syntaxBasic = loc.getCommandSyntax(cmd);

		if (loc.isCASCommand(cmd)) {

			if (!syntaxBasic.equals(cmd + Localization.syntaxStr)) {
				formattedHTMLString(ret, syntaxBasic, false);
			}
			// don't show cas specific syntax for exam graphing
			boolean supportsCAS = app.getSettings().getCasSettings().isEnabled();
			if (!app.getAppletParameters().hasDataParamEnableGraphing()
					|| (app.getAppletParameters().hasDataParamEnableGraphing() && supportsCAS)) {

				Label headCAS = new Label(loc.getMenu("Type.CAS") + ":");
				headCAS.addStyleName("inputHelp-headerCAS");
				ret.add(headCAS);
			
				String syntaxCAS = loc.getCommandSyntaxCAS(cmd);
				formattedHTMLString(ret, syntaxCAS, true);
			}
		} else {
			formattedHTMLString(ret, syntaxBasic, false);
		}

		return ret;
	}

	/**
	 * Converts a java string to a SafeHTML string with newline characters
	 * replaced by paragraph tags. This tag is required for the hanging indent
	 * css style used to format syntax descriptions.
	 * 
	 * @param cas
	 *            whether to format it as CAS syntax
	 * @param ret
	 *            list of labels
	 */
	private void formattedHTMLString(ArrayList<Widget> ret, String s, boolean cas) {
		String[]lines = s.split("\n");
		for (String line : lines) {
			Label syntax = syntaxLabel(line);
			if (cas) {
				syntax.addStyleName("inputHelp-CAScmdSyntax");
			}
			ret.add(syntax);
		}
	}
	
	private Label syntaxLabel(String line) {
		Label syntax = new Label(line);
		final String fLine = line;
		syntax.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.preventDefault();
				event.stopPropagation();
				insertText(fLine);
			}
		});
		return syntax;
    }

	/**
	 * @param text
	 *            to be inserted into input field
	 */
	void insertText(String text) {
		if (this.inputField != null) {
			ensureInputHasFocus();
			this.inputField.autocomplete(text);
			this.inputField.setFocus(true);
		}
	}

	private void ensureInputHasFocus() {
		if (!(inputField instanceof RadioTreeItem)) {
			Log.debug("HH not a RadioTreeItem");
			return;
		}

		RadioTreeItem ri = (RadioTreeItem) inputField;
		Log.debug("HH RadioTreeItem turn to editing");
		ri.ensureEditing();
	}

	private ArrayList<Widget> functionTableHTML() {
		String[][] f = TableSymbols.getTranslatedFunctionsGrouped(app);
		ArrayList<Widget> ret = new ArrayList<>();
		// sb.append("<table>");
		
		for (int i = 0; i < f.length; i++) {
			HorizontalPanel widget = new HorizontalPanel();	
			for (int j = 0; j < f[i].length; j++) {
				Label syntax = syntaxLabel(f[i][j]);
				widget.add(syntax);
			}
			
			ret.add(widget);
		}

		return ret;
	}

	/**
	 * @param currentCommand
	 *            command to be selected
	 */
	public void focusCommand(String currentCommand) {
		if (indexTree == null || currentCommand == null) {
			return;
		}
		for (int i = 2; i < indexTree.getItemCount(); i++) {
			TreeItem group = indexTree.getItem(i);
			if (group == null) {
				continue;
			}
			for (int j = 0; j < group.getChildCount(); j++) {
				if (group.getChild(j).getElement().getInnerText()
						.equalsIgnoreCase(currentCommand)) {
					group.setState(true);
					indexTree.setSelectedItem(group.getChild(j), false);
					updateDetailPanel();
					return;
				}
			}
		}
	}
}
