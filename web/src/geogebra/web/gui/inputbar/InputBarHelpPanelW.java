package geogebra.web.gui.inputbar;

import geogebra.common.GeoGebraConstants;
import geogebra.common.gui.util.TableSymbols;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.util.LowerCaseDictionary;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.main.AppW;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author G. Sturr
 * 
 */
public class InputBarHelpPanelW extends VerticalPanel {

	private AppW app;
	private Tree indexTree;
	private HTML syntaxPanel;
	private Button btnOnlineHelp;
	private LocaleSensitiveComparator comparator;
	private SplitLayoutPanel sp;
	private InlineLabel lblSyntax;
	private MyTreeItem itmFunction;

	/**
	 * @param app
	 */
	public InputBarHelpPanelW(AppW app) {

		super();
		this.app = app;
		comparator = new LocaleSensitiveComparator();

		createGUI();
		setLabels();

	}

	private void createGUI() {

		// create syntax panel
		syntaxPanel = new HTML();

		// create help button
		btnOnlineHelp = new Button(app.getPlain("ShowOnlineHelp"));
		btnOnlineHelp.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btnOnlineHelp.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				openOnlineHelp();
			}
		});
		btnOnlineHelp.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		FlowPanel pnlButton = new FlowPanel();
		pnlButton.add(btnOnlineHelp);
		pnlButton.getElement().getStyle().setFloat(Style.Float.RIGHT);

		// create detail title panel
		lblSyntax = new InlineLabel();
		lblSyntax.getElement().getStyle().setTextAlign(TextAlign.LEFT);

		HorizontalPanel detailTitlePanel = new HorizontalPanel();
		detailTitlePanel
		        .setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		detailTitlePanel.add(lblSyntax);
		detailTitlePanel.add(pnlButton);
		detailTitlePanel.addStyleName("inputHelp-detailPanelTitle");

		// create the detail panel
		VerticalPanel detailPanel = new VerticalPanel();
		detailPanel.add(detailTitlePanel);
		detailPanel.add(syntaxPanel);
		detailPanel.setWidth("100%");

		// create the index tree and put it in a scroll panel
		indexTree = new Tree();
		indexTree.addStyleName("inputHelp-tree");
		indexTree.setAnimationEnabled(true);
		ScrollPanel treeScroller = new ScrollPanel(indexTree);
		treeScroller.setSize("100%", "100%");

		// put the detail panel and index tree side by side in a
		// SplitLayoutPanel
		sp = new SplitLayoutPanel();
		sp.addEast(treeScroller, 250);
		sp.add(new ScrollPanel(detailPanel));

		// now add the split panel to our main panel
		add(sp);

	}

	// =================================================================
	// Getters/Setters & Event Handlers
	// =================================================================

	protected void openOnlineHelp() {

		if (getSelectedCommand() == null) {
			((GuiManagerW) app.getGuiManager()).openHelp("InputBar");

		} else if (getSelectedCommand().equals(
		        app.getLocalization().getMenu("MathematicalFunctions"))) {
			((GuiManagerW) app.getGuiManager()).openHelp(App.WIKI_OPERATORS);

		} else {
			((GuiManagerW) app.getGuiManager())
			        .openCommandHelp(getSelectedCommand());
		}
	}

	public String getSelectedCommand() {
		if (indexTree == null || indexTree.getSelectedItem() == null
		        || indexTree.getSelectedItem().getChildCount() > 0) {
			return null;
		}
		return indexTree.getSelectedItem().getWidget().getElement()
		        .getInnerText();
	}

	public void setLabels() {

		setCommands();
		// show Mathematical Functions tree item initially
		indexTree.setSelectedItem(itmFunction);
		updateDetailPanel();
		btnOnlineHelp.setText(app.getLocalization().getPlain("ShowOnlineHelp"));
	}

	/**
	 * Adjusts the panel size relative to the current application panel size
	 */
	public void updateGUI() {

		int h = AppW.getRootComponent(app).getOffsetHeight() - 60;
		int w = Math.min(700, AppW.getRootComponent(app).getOffsetWidth() - 60);
		sp.setPixelSize(w, h);
	}

	// =================================================================
	// Index Tree
	// =================================================================

	public void setCommands() {

		indexTree.clear();

		itmFunction = new MyTreeItem();
		itmFunction.setWidget(new TreeItemButton(app.getLocalization().getMenu(
		        "MathematicalFunctions"), itmFunction, true));
		indexTree.addItem(itmFunction);

		MyTreeItem itmAllCommands = new MyTreeItem();
		itmAllCommands.setWidget(new TreeItemButton(app.getLocalization()
		        .getMenu("AllCommands"), itmAllCommands, false));

		addCmdNames(itmAllCommands, getAllCommandsTreeSet());
		indexTree.addItem(itmAllCommands);

		TreeMap<String, TreeSet<String>> cmdMap = getCommandTreeMap();

		Iterator<Entry<String, TreeSet<String>>> i = cmdMap.entrySet()
		        .iterator();
		while (i.hasNext()) {

			Entry<String, TreeSet<String>> entry = i.next();

			// add command set branch to tree
			String cmdSetName = entry.getKey();
			TreeItem itmCmdSet = new MyTreeItem();
			itmCmdSet
			        .setWidget(new TreeItemButton(cmdSetName, itmCmdSet, false));
			indexTree.addItem(itmCmdSet);

			// add command names to this branch
			TreeSet<String> cmdNames = entry.getValue();
			addCmdNames(itmCmdSet, cmdNames);

		}

	}

	private void addCmdNames(TreeItem item, TreeSet<String> names) {
		Iterator<String> it = names.iterator();
		while (it.hasNext()) {
			String cmdName = it.next();
			if (cmdName != null && cmdName.length() > 0) {
				MyTreeItem cmd = new MyTreeItem();
				cmd.setWidget(new TreeItemButton(cmdName, cmd, true));
				item.addItem(cmd);
			}
		}
	}

	private class TreeItemButton extends InlineLabel {
		TreeItem item;
		boolean isLeaf;

		public TreeItemButton(String text, final TreeItem item,
		        final boolean isLeaf) {
			super(text);
			this.item = item;
			this.isLeaf = isLeaf;
			addStyleName("inputHelp-treeItem");

			if (isLeaf) {
				addStyleName("inputHelp-leaf");
			}

			this.addClickHandler(new ClickHandler() {
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

	private class MyTreeItem extends TreeItem {

		public MyTreeItem() {
			// TODO Auto-generated constructor stub
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
	private class LocaleSensitiveComparator implements Comparator<String> {

		public LocaleSensitiveComparator() {
			// TODO Auto-generated constructor stub
		}

		public native int compare(String source, String target) /*-{
			return source.localeCompare(target);
		}-*/;
	}

	private TreeMap<String, TreeSet<String>> getCommandTreeMap() {

		TreeMap<String, TreeSet<String>> cmdTreeMap = new TreeMap<String, TreeSet<String>>(
		        comparator);
		LowerCaseDictionary[] subDict = app.getSubCommandDictionary();

		for (int i = 0; i < subDict.length; i++) {

			if (subDict[i].isEmpty()) {
				continue;
			}

			String cmdSetName = app.getKernel().getAlgebraProcessor()
			        .getSubCommandSetName(i);

			TreeSet<String> cmdTree = new TreeSet<String>(comparator);

			Iterator<?> it = subDict[i].getIterator();
			while (it.hasNext()) {
				String cmd = subDict[i].get(it.next());
				if (cmd != null && cmd.length() > 0) {
					cmdTree.add(cmd);
				}
			}
			cmdTreeMap.put(cmdSetName, cmdTree);
		}
		return cmdTreeMap;
	}

	private TreeSet<String> getAllCommandsTreeSet() {

		TreeSet<String> treeSet = new TreeSet<String>(comparator);

		LowerCaseDictionary dict = app.getCommandDictionary();
		Iterator<?> it = dict.getIterator();
		while (it.hasNext()) {
			String cmdName = dict.get(it.next());
			if (cmdName != null && cmdName.length() > 0) {
				treeSet.add(cmdName);
			}
		}
		return treeSet;
	}

	// =================================================================
	// Syntax Description
	// =================================================================

	protected void updateDetailPanel() {

		if (getSelectedCommand() == null) {
			syntaxPanel.setHTML("");
			lblSyntax.setText("");
			syntaxPanel.setHTML("");
			return;
		}

		lblSyntax.setText(getSelectedCommand());

		if (getSelectedCommand().equals(
		        app.getLocalization().getMenu("MathematicalFunctions"))) {

			syntaxPanel.setHTML(functionTableHTML());
			syntaxPanel.removeStyleName("inputHelp-cmdSyntax");
			syntaxPanel.addStyleName("inputHelp-functionTable");

		} else {

			syntaxPanel.setHTML(cmdSyntaxHTML());
			syntaxPanel.removeStyleName("inputHelp-functionTable");
			syntaxPanel.addStyleName("inputHelp-cmdSyntax");
		}

	}

	private String cmdSyntaxHTML() {

		StringBuilder sb = new StringBuilder();

		// internal name of selected command
		String cmd = app.getReverseCommand(getSelectedCommand());
		
		Localization loc = app.getLocalization();

		String syntaxBasic = loc.getCommandSyntax(cmd);
		String syntaxCAS = loc.getCommandSyntaxCAS(cmd);

		if (GeoGebraConstants.CAS_VIEW_ENABLED
		        && loc.isCASCommand(cmd)) {

			if (!syntaxBasic.equals(cmd + Localization.syntaxStr)) {
				sb.append(formattedHTMLString(syntaxBasic + "\n"));
			}

			sb.append("<div class = inputHelp-headerCAS>");
			sb.append(app.getMenu("Type.CAS") + ":");
			sb.append("</div><br>");

			sb.append("<div class = inputHelp-CAScmdSyntax >");
			sb.append(formattedHTMLString(syntaxCAS));
			sb.append("</div>");

		} else {

			sb.append(formattedHTMLString(syntaxBasic));
		}

		return sb.toString();

	}

	/**
	 * Converts a java string to a SafeHTML string with newline characters
	 * replaced by paragraph tags. This tag is required for the hanging indent
	 * css style used to format syntax descriptions.
	 */
	private String formattedHTMLString(String s) {
		SafeHtml h = new SafeHtmlBuilder().appendEscapedLines(s.toString())
		        .toSafeHtml();
		return (h.asString().replaceAll("<br>", "<p>"));
	}

	private String functionTableHTML() {
		String[][] f = TableSymbols.getTranslatedFunctionsGrouped(app);
		StringBuilder sb = new StringBuilder();
		// sb.append("<table>");
		for (int i = 0; i < f.length; i++) {
			sb.append("<table><tr>");
			for (int j = 0; j < f[i].length; j++) {
				sb.append("<td>");
				sb.append(f[i][j]);
				sb.append("</td>");
			}
			sb.append("</tr></table>");
		}

		return sb.toString();
	}

}
