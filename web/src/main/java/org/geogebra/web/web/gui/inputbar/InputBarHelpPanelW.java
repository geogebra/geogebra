package org.geogebra.web.web.gui.inputbar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.util.TableSymbols;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.util.LowerCaseDictionary;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.GuiManagerW;

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

/**
 * @author G. Sturr
 * 
 */
public class InputBarHelpPanelW extends VerticalPanel implements SetLabels, BooleanRenderable {

	private AppW app;
	private Tree indexTree;
	private VerticalPanel syntaxPanel;
	private Button btnOnlineHelp;
	private LocaleSensitiveComparator comparator;
	private SplitLayoutPanel sp;
	private InlineLabel lblSyntax;
	private MyTreeItem itmFunction;
	private AutoCompleteTextFieldW inputField;

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
	
	public void setInputField(AutoCompleteTextFieldW field){
		this.inputField = field;
	}

	private void createGUI() {

		// create syntax panel
		syntaxPanel = new VerticalPanel();

		// create help button
		btnOnlineHelp = new Button(app.getPlain("ShowOnlineHelp"));
		btnOnlineHelp.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btnOnlineHelp.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				openOnlineHelp();
			}
		});
		render(app.getNetworkOperation().isOnline());
		app.getNetworkOperation().getView().add(this);
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
		sp.addStyleName("ggbdockpanelhack");
		sp.addEast(treeScroller, 250);
		sp.add(new ScrollPanel(detailPanel));

		// now add the split panel to our main panel
		add(sp);

	}

	public void render(boolean online) {
		btnOnlineHelp.setEnabled(online);
	    
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

		int h = (int) (AppW.getRootComponent(app).getOffsetHeight()*app.getArticleElement().getScaleX() - 60);
		int w = (int) Math.min(700, AppW.getRootComponent(app).getOffsetWidth()*app.getArticleElement().getScaleX() - 60);
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
		for(int i = 0; i< rows.size(); i++){
			syntaxPanel.add(rows.get(i));
		}

	}

	private ArrayList<Widget> cmdSyntaxHTML() {
		ArrayList<Widget> ret = new ArrayList<Widget>();

		// internal name of selected command
		String cmd = app.getReverseCommand(getSelectedCommand());
		
		Localization loc = app.getLocalization();

		String syntaxBasic = loc.getCommandSyntax(cmd);
		String syntaxCAS = loc.getCommandSyntaxCAS(cmd);

		if (GeoGebraConstants.CAS_VIEW_ENABLED
		        && loc.isCASCommand(cmd)) {

			if (!syntaxBasic.equals(cmd + Localization.syntaxStr)) {
				formattedHTMLString(ret,syntaxBasic, false);
			}
			Label headCAS = new Label(app.getMenu("Type.CAS") + ":");
			headCAS.addStyleName("inputHelp-headerCAS");
			ret.add(headCAS);
			
			
			formattedHTMLString(ret,syntaxCAS,true);
			

		} else {

			formattedHTMLString(ret, syntaxBasic, false);
		}

		return ret;

	}

	/**
	 * Converts a java string to a SafeHTML string with newline characters
	 * replaced by paragraph tags. This tag is required for the hanging indent
	 * css style used to format syntax descriptions.
	 * @param b 
	 * @param ret 
	 */
	private void formattedHTMLString(ArrayList<Widget> ret, String s, boolean b) {
		String[]lines = s.split("\n");
		for(String line: lines){
			Label syntax = syntaxLabel(line);
			if(b){
				syntax.addStyleName("inputHelp-CAScmdSyntax");
			}
			ret.add(syntax);
		}
	}
	
	private Label syntaxLabel(String line) {
		Label syntax = new Label(line);
		final String fLine = line;
		syntax.addMouseDownHandler(new MouseDownHandler(){

			@Override
            public void onMouseDown(MouseDownEvent event) {
				event.preventDefault();
				event.stopPropagation();
                insertText(fLine);
                
            }});
		return syntax;
    }

	void insertText(String s){
		if (this.inputField != null) {
			this.inputField.getTextField().setText(s);
			ArrayList<String> arr = new ArrayList<String>();
			arr.add(s);
			this.inputField.validateAutoCompletion(0, arr);
		}
	}

	private ArrayList<Widget> functionTableHTML() {
		String[][] f = TableSymbols.getTranslatedFunctionsGrouped(app);
		ArrayList<Widget> ret = new ArrayList<Widget>();
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

}
