package geogebra.gui.inputbar;

import geogebra.common.GeoGebraConstants;
import geogebra.common.gui.SetLabels;
import geogebra.common.gui.util.TableSymbols;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.main.App;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.common.util.LowerCaseDictionary;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.SelectionTable;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Collator;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class InputBarHelpPanel extends JPanel implements TreeSelectionListener,
		ActionListener, FocusListener, SetLabels {

	private static final long serialVersionUID = 1L;
	
	private AppD app;
	private InputBarHelpPanel thisPanel;
	private Color bgColor = Color.WHITE;

	//TODO remove?
	private Color titleColor;

	private MyJTree cmdTree, fcnTree;
	private DefaultMutableTreeNode functionTitleNode, rootSubCommands,
			rootAllCommands;
	private DefaultTreeModel cmdTreeModel;

	private String selectedCommand, rollOverCommand;

	public String getSelectedCommand() {
		return selectedCommand;
	}

	private String selectedFunction;

	private JPopupMenu contextMenu;
	private JTextPane helpTextPane;
	private JButton btnOnlineHelp, btnRefresh;
	private SelectionTable functionTable;
	private JPanel tablePanel;
	private JPanel syntaxHelpPanel;
	private JSplitPane cmdSplitPane;
	private JLabel titleLabel;
	private JLabel syntaxLabel;
	private JButton btnPaste;
	private JScrollPane scroller;

	/***************************************************
	 * Constructor
	 */
	public InputBarHelpPanel(AppD app) {

		this.app = app;
		thisPanel = this;
		this.setOpaque(true);
		// this.setBackground(Color.blue);
		titleColor = geogebra.awt.GColorD.getAwtColor(GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER);
		bgColor = this.getBackground().brighter();

		createFunctionPanel();
		createCommandTree();
		createSyntaxPanel();
		contextMenu = new JPopupMenu();

		JPanel commandPanel = new JPanel(new BorderLayout());
		commandPanel.add(tablePanel, BorderLayout.NORTH);
		commandPanel.add(cmdTree, BorderLayout.CENTER);
		commandPanel.setBorder(BorderFactory.createEmptyBorder());
		scroller = new JScrollPane(commandPanel);

		// scroller.setBorder(BorderFactory.createEmptyBorder());
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel titlePanel = new JPanel();
		titlePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(1, 0, 0, 0, SystemColor.controlShadow),
				BorderFactory.createEmptyBorder(0, 2, 0, 2)));
		titlePanel.setLayout(new BorderLayout());

		// titlePanel.setBackground(titleColor);
		titleLabel = new JLabel();
		// titleLabel.setForeground(Color.darkGray);

		titlePanel.add(titleLabel, BorderLayout.WEST);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(scroller, BorderLayout.CENTER);
		mainPanel.add(titlePanel, BorderLayout.NORTH);

		cmdSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainPanel,
				syntaxHelpPanel);
		cmdSplitPane.setResizeWeight(1.0);
		cmdSplitPane.setBorder(BorderFactory.createEmptyBorder());

		this.setLayout(new BorderLayout());
		this.add(cmdSplitPane, BorderLayout.CENTER);
		this.add(createButtonPanel(), BorderLayout.SOUTH);
		// this.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 4));

		setLabels(false);
		updateFonts();
	}

	public int getPreferredWidth() {
		return (int) (1.2 * cmdTree.getPreferredSize().width);
	}

	private void createSyntaxPanel() {

		helpTextPane = new JTextPane();
		// helpTextArea.setText("");
		helpTextPane.setEditable(false);
		// helpTextArea.setMinimumSize(new Dimension(200,300));
		helpTextPane.setBorder(BorderFactory.createEmptyBorder(8, 5, 2, 5));
		helpTextPane.setBackground(bgColor);
		JPanel p = new JPanel(new BorderLayout());
		p.add(helpTextPane, BorderLayout.CENTER);

		p.setBorder(BorderFactory.createEmptyBorder());
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(1, 0, 1, 0, SystemColor.controlShadow),
				BorderFactory.createEmptyBorder(0, 2, 0, 2)));
		syntaxLabel = new JLabel();
		syntaxLabel.setForeground(Color.darkGray);
		titlePanel.add(syntaxLabel, BorderLayout.WEST);

		syntaxHelpPanel = new JPanel(new BorderLayout());
		JScrollPane scroller = new JScrollPane(helpTextPane);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		// scroller.setBorder(BorderFactory.createEmptyBorder());

		JPanel corner = new JPanel(new FlowLayout());
		corner.setBackground(this.getBackground());
		scroller.setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, corner);

		syntaxHelpPanel.add(scroller, BorderLayout.CENTER);
		// syntaxHelpPanel.add(titlePanel,BorderLayout.NORTH);
	}

	private JPanel createButtonPanel() {

		btnRefresh = new JButton(app.getImageIcon("view-refresh.png"));
		btnRefresh.setBorderPainted(false);
		btnRefresh.setFocusable(false);
		btnRefresh.setEnabled(false);
		btnRefresh.addActionListener(this);
		btnRefresh.setContentAreaFilled(false);
		btnRefresh.setPreferredSize(new Dimension(24, 20));

		btnPaste = new JButton();
		btnPaste.addActionListener(this);
		btnPaste.setFocusable(false);

		btnOnlineHelp = new JButton(app.getPlain("ShowOnlineHelp"));
		btnOnlineHelp.setFocusable(false);
		btnOnlineHelp.addActionListener(this);
		// btnOnlineHelp.setBorderPainted(false);
		// btnOnlineHelp.setContentAreaFilled(false);
		// btnOnlineHelp.setPreferredSize(new Dimension(24,20));

		JPanel leftPanel = new JPanel(new FlowLayout());
		leftPanel.add(btnPaste);

		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightPanel.add(btnOnlineHelp);
		rightPanel.add(btnRefresh);

		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(leftPanel, BorderLayout.WEST);
		buttonPanel.add(rightPanel, BorderLayout.EAST);
		buttonPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(1, 0, 0, 0, SystemColor.controlShadow),
				BorderFactory.createEmptyBorder(0, 2, 0, 2)));

		// buttonPanel.setBackground(titleColor);
		// leftPanel.setBackground(titleColor);
		// rightPanel.setBackground(titleColor);

		return buttonPanel;
	}

	private void createFunctionPanel() {

		functionTable = new SelectionTable(app, TableSymbols.getTranslatedFunctions(app), -1, 2,
				new Dimension(20, 16), geogebra.common.gui.util.SelectionTable.MODE_TEXT);
		functionTable.setShowGrid(true);
		functionTable.setHorizontalAlignment(SwingConstants.LEFT);
		functionTable.setBorder(BorderFactory.createLineBorder(functionTable
				.getGridColor()));
		functionTable.addMouseListener(new TableSelectionListener());
		functionTable.setBackground(bgColor);
		functionTable.setVisible(false);

		DefaultMutableTreeNode functionRoot = new DefaultMutableTreeNode();

		functionTitleNode = new DefaultMutableTreeNode(
				app.getMenu("MathematicalFunctions"));
		functionTitleNode.add(new DefaultMutableTreeNode());
		functionRoot.add(functionTitleNode);
		fcnTree = new MyJTree(new DefaultTreeModel(functionRoot));
		fcnTree.addTreeExpansionListener(new TreeExpansionListener() {
			public void treeCollapsed(TreeExpansionEvent e) {
				functionTable.setVisible(false);
				((MyJTree) e.getSource()).rollOverRow = -1;
			}

			public void treeExpanded(TreeExpansionEvent e) {
				functionTable.setVisible(true);
				((MyJTree) e.getSource()).rollOverRow = -1;
				thisPanel.btnRefresh.setEnabled(true);
			}
		});

		// add listener for mouse roll over
		RollOverListener rollOverListener = new RollOverListener();
		fcnTree.addMouseMotionListener(rollOverListener);
		fcnTree.addMouseListener(rollOverListener);

		fcnTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		fcnTree.setCellRenderer(new MyRenderer());
		fcnTree.setRootVisible(false);
		fcnTree.setShowsRootHandles(false);
		fcnTree.setToggleClickCount(1);
		fcnTree.setBackground(bgColor);
		fcnTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		fcnTree.setRowHeight(-1);

		JPanel fPanel = new JPanel(new BorderLayout());
		fPanel.add(Box.createRigidArea(new Dimension(35, 1)), BorderLayout.WEST);
		fPanel.add(functionTable, BorderLayout.CENTER);
		fPanel.setBackground(bgColor);

		tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(fcnTree, BorderLayout.NORTH);

		tablePanel.add(fPanel, BorderLayout.WEST);
		functionTable.setAlignmentX(LEFT_ALIGNMENT);

		tablePanel.setBackground(bgColor);
	}

	/** mouse listener to handle table selection */
	public class TableSelectionListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {

			if (e.getSource() == functionTable) {
				if (!cmdTree.getSelectionModel().isSelectionEmpty())
					cmdTree.clearSelection();
				selectedFunction = (String) functionTable.getSelectedValue();
				selectedCommand = null;
				helpTextPane.setText("");
				if (e.getClickCount() == 2)
					doPaste();
			}
		}
	}

	public void setLabels() {
		setLabels(true);
	}
	
	private void setLabels(boolean setCommands) {
		if(setCommands)
			setCommands();
		cmdTreeModel.setRoot(rootSubCommands);
		cmdTreeModel.reload();

		titleLabel.setText(app.getMenu("InputHelp"));
		syntaxLabel.setText(app.getPlain("Syntax"));
		btnPaste.setText(app.getMenu("Paste"));
		btnOnlineHelp.setText(app.getPlain("ShowOnlineHelp"));

		helpTextPane.setText(null);
		functionTable.populateModel(TableSymbols.getTranslatedFunctions(app));
		functionTitleNode.setUserObject(app.getMenu("MathematicalFunctions"));
		rootAllCommands.setUserObject(app.getMenu("AllCommands"));
		updateFonts();
		repaint();
	}

	public void updateFonts() {

		functionTable.updateFonts();
		helpTextPane.setFont(app.getPlainFont());
		titleLabel.setFont(app.getPlainFont());
		syntaxLabel.setFont(app.getPlainFont());
		this.validate();

		// set the height of the syntax panel
		Dimension d = syntaxHelpPanel.getMinimumSize();
		d.height = 12 * app.getGUIFontSize();
		syntaxHelpPanel.setMinimumSize(d);

		// set the minimum width of the help panel (see
		// Application.setShowInputHelpPanel)
		d = this.getPreferredSize();
		d.width = Math.max(
				(int) (1.1 * this.cmdSplitPane.getPreferredSize().width),
				this.getPreferredSize().width);
		this.setMinimumSize(d);

		// adjust scrolling increments to match font size
		scroller.getVerticalScrollBar().setBlockIncrement(
				10 * app.getFontSize());
		scroller.getVerticalScrollBar().setUnitIncrement(3 * app.getFontSize());
	}

	private void createCommandTree() {

		setCommands();

		cmdTreeModel = new DefaultTreeModel(rootSubCommands);
		cmdTree = new MyJTree(cmdTreeModel);

		cmdTree.setFocusable(false);
		ToolTipManager.sharedInstance().registerComponent(cmdTree);

		// add listener for selection changes.
		cmdTree.addTreeSelectionListener(this);

		// add listener for mouse roll over
		RollOverListener rollOverListener = new RollOverListener();
		cmdTree.addMouseMotionListener(rollOverListener);
		cmdTree.addMouseListener(rollOverListener);

		cmdTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		cmdTree.setCellRenderer(new MyRenderer());
		cmdTree.setLargeModel(true);
		// tree.putClientProperty("JTree.lineStyle", "none");
		cmdTree.setRootVisible(false);
		cmdTree.setShowsRootHandles(false);
		// tree.setScrollsOnExpand(true);
		cmdTree.setToggleClickCount(1);
		cmdTree.setBackground(bgColor);
		cmdTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		cmdTree.setRowHeight(-1);

		cmdTree.addTreeExpansionListener(new TreeExpansionListener() {
			public void treeCollapsed(TreeExpansionEvent e) {
			}

			public void treeExpanded(TreeExpansionEvent e) {
				thisPanel.btnRefresh.setEnabled(true);
			}
		});
	}
	
	public void setCommands() {
		if (rootSubCommands == null)
			rootSubCommands = new DefaultMutableTreeNode();
		if (rootAllCommands == null)
			rootAllCommands = new DefaultMutableTreeNode(
					app.getMenu("AllCommands"));
		rootSubCommands.removeAllChildren();
		rootAllCommands.removeAllChildren();

		DefaultMutableTreeNode child;
		LowerCaseDictionary[] subDict = app.getSubCommandDictionary();

		// load the sub-command nodes
		for (int i = 0; i < subDict.length; i++) {
			if (subDict[i].isEmpty())
			    continue;

			// add stem node: sub-command set name
			String name = app.getKernel().getAlgebraProcessor()
					.getSubCommandSetName(i);
			child = new DefaultMutableTreeNode(name);
			addNodeInSortedOrder(rootSubCommands, child);

			// add leaf nodes: sub-command names
			Iterator<?> it = subDict[i].getIterator();
			while (it.hasNext()) {
				String cmdName = subDict[i].get(it.next());
				if (cmdName != null && cmdName.length() > 0) {
					addNodeInSortedOrder(child, new DefaultMutableTreeNode(
							cmdName));
				}
			}
		}

		// laod the All Commands node
		LowerCaseDictionary dict = app.getCommandDictionary();
		Iterator<?> it = dict.getIterator();
		while (it.hasNext()) {
			String cmdName = dict.get(it.next());
			if (cmdName != null && cmdName.length() > 0) {
				addNodeInSortedOrder(rootAllCommands,
						new DefaultMutableTreeNode(cmdName));
			}
		}
		// ignore sort and put this one first
		rootSubCommands.insert(rootAllCommands, 0);
	}

	/**
	 * Adds a new node to a sorted tree. Node leaf strings are sorted in
	 * ascending order using a locale-sensitive Collator class.
	 */
	private void addNodeInSortedOrder(DefaultMutableTreeNode parent,
			DefaultMutableTreeNode child) {

		if (child.toString() == null)
			return;

		int n = parent.getChildCount();
		if (n == 0) {
			parent.add(child);
			return;
		}

		// make sure eg accented letters get sorted correctly
		Collator collator = Collator.getInstance(app.getLocale());
		collator.setStrength(Collator.SECONDARY);
		collator.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
		DefaultMutableTreeNode node = null;

		for (int i = 0; i < n; i++) {
			node = (DefaultMutableTreeNode) parent.getChildAt(i);
			if (collator.compare(node.toString(), child.toString()) > 0) {
				parent.insert(child, i);
				return;
			}
		}

		parent.add(child);
		return;
	}

	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) cmdTree
				.getLastSelectedPathComponent();

		if (node == null)
			return; // Nothing is selected.

		if (node.isLeaf()) {
			Object nodeInfo = node.getUserObject();
			selectedCommand = (String) nodeInfo;
			selectedFunction = null;
			// insertInputBarCommand(selectedCommand);
			showSelectedSyntax();
			if (functionTable.getSelectedIndex() != -1)
				functionTable.clearSelection();
		}

		else if (e.getSource().equals(fcnTree)) {

			functionTable.setVisible(!functionTable.isVisible());
		}
	}

	private class RollOverListener extends MouseInputAdapter {

		private void myPopupEvent(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			JTree tree = (JTree) e.getSource();
			TreePath tp = tree.getPathForLocation(x, y);
			if (tp == null)
				return;
			DefaultMutableTreeNode node = ((DefaultMutableTreeNode) tp
					.getLastPathComponent());
			if (node.isLeaf()) {
				contextMenu.setBackground(bgColor);
				contextMenu.removeAll();
				// JMenuItem item = new JMenuItem(helpTextArea.getText());

				Object nodeInfo = node.getUserObject();
				String cmd = (String) nodeInfo;
				rollOverCommand = cmd;
				StringBuilder sb = new StringBuilder();
				cmd = app.translateCommand(cmd); // internal name
				CommandProcessor.getCommandSyntax(sb, app, cmd, -1);

				JTextArea t = new JTextArea(sb.toString());
				t.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				contextMenu.add(t);

				// item = new JMenuItem((String) node.getUserObject());
				// contextMenu.add(item);
				contextMenu.addSeparator();
				JMenuItem item = new JMenuItem(app.getPlain("ShowOnlineHelp"));
				item.setIcon(app.getImageIcon("help.png"));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						app.getGuiManager().openCommandHelp(rollOverCommand);
					}
				});

				contextMenu.add(item);

				contextMenu.show(tree, x, y);
				contextMenu.getSelectionModel().clearSelection();
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger())
				myPopupEvent(e);
			if (e.getClickCount() == 2) {
				doPaste();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger())
				myPopupEvent(e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			MyJTree tree;
			if (e.getSource() instanceof JTree) {
				tree = (MyJTree) e.getSource();
			} else {
				return;
			}
			tree.rollOverRow = -1;
			tree.repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {

			// TODO most of this code can be removed now that roll over no
			// longer changes the help text
			MyJTree tree;
			if (e.getSource() instanceof MyJTree)
				tree = (MyJTree) e.getSource();
			else
				return;

			int row = tree.getRowForLocation(e.getX(), e.getY());

			if (row != tree.rollOverRow) {
				tree.rollOverRow = row;
				if (row != -1 && tree.equals(cmdTree)) {

					// get the help text for this node
					TreePath tp = tree.getPathForRow(row);
					DefaultMutableTreeNode node = ((DefaultMutableTreeNode) tp
							.getLastPathComponent());
					if (node.isLeaf()) {
						Object nodeInfo = node.getUserObject();
						String cmd = (String) nodeInfo;
						rollOverCommand = cmd;
						StringBuilder sb = new StringBuilder();
						cmd = app.translateCommand(cmd); // internal name
						// CommandProcessor.getCommandSyntax(sb, app, cmd, -1);
						sb.append(app.getCommandSyntax(cmd));
						// helpTextArea.setText(sb.toString());

					} else {
						// clear the help text
						// rollOverCommand = null;
						// helpTextArea.setText("");
					}
				}

				if (tree.equals(fcnTree)) {
					// clear the help text
					rollOverCommand = null;
					// helpTextArea.setText("");
				}

				tree.repaint();
			}
		}
	}

	private void showSelectedSyntax() {

		String cmd = app.translateCommand(selectedCommand); // internal name
		// String s = "Syntax:\n" + app.getCommandSyntax(cmd);
		String description = app.getCommandSyntax(cmd);
		String descriptionCAS = app.getCommandSyntaxCAS(cmd);
		String descriptionCASHeader = "\n" + app.getMenu("Type.CAS") + ":\n";

		StyledDocument doc = helpTextPane.getStyledDocument();
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e1) {
			// this should never occur
			e1.printStackTrace();
		}

		// define the regular and italic style
		Style def = StyleContext.getDefaultStyleContext().getStyle(
				StyleContext.DEFAULT_STYLE);
		Style regular = doc.addStyle("regular", def);
		
		// changed to use getFontCanDisplayAwt() so that Armenian displays OK
		StyleConstants.setFontFamily(def, app.getFontCanDisplayAwt(description).getFamily());
		
		Style s = doc.addStyle("italic", regular);
		StyleConstants.setItalic(s, true);

		SimpleAttributeSet attrs = new SimpleAttributeSet();
		if(description.length() > 10){
			StyleConstants.setFirstLineIndent(attrs, -50);
			StyleConstants.setLeftIndent(attrs, 50);
			StyleConstants.setBold(attrs, false);
		}else{
			// fix for indent problem with short strings on some platforms 
			StyleConstants.setFirstLineIndent(attrs, 0);
			StyleConstants.setLeftIndent(attrs, 0);
			StyleConstants.setBold(attrs, true);
		}
		
		doc.setParagraphAttributes(0, doc.getLength(), attrs, false);

		if (GeoGebraConstants.CAS_VIEW_ENABLED
				&& !descriptionCAS.equals(cmd + App.syntaxCAS)) {
			if (!description.equals(cmd + App.syntaxStr))
				try {
					doc.insertString(doc.getLength(), description + "\n",
							doc.getStyle("regular"));
				} catch (BadLocationException e) {
					// should never occur
					e.printStackTrace();
				}
			try {
				doc.insertString(doc.getLength(), descriptionCASHeader,
						doc.getStyle("italic"));
				doc.insertString(doc.getLength(), descriptionCAS,
						doc.getStyle("regular"));
			} catch (BadLocationException e) {
				// should never occur
				e.printStackTrace();
			}
		} else {
			try {
				doc.insertString(doc.getLength(), description,
						doc.getStyle("regular"));
			} catch (BadLocationException e) {
				// should never occur
				e.printStackTrace();
			}
		}
		// helpTextArea.setText(app.getCommandSyntax(cmd));
		helpTextPane.setCaretPosition(0);
		helpTextPane.repaint();
	}

	// =============================================
	// Tree Cell Renderer
	// =============================================

	private class MyRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = 1L;
		
		private Color selectionColor, rollOverColor;

		public MyRenderer() {
			setOpenIcon(app.getImageIcon("tree-close.png"));
			setClosedIcon(app.getImageIcon("tree-open.png"));
			setLeafIcon(GeoGebraIcon.createEmptyIcon(5, 1));

			selectionColor = geogebra.awt.GColorD.getAwtColor(GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR);
			 // this.getBackgroundSelectionColor()
			rollOverColor = Color.LIGHT_GRAY;

			this.setTextSelectionColor(Color.black);
			this.setTextNonSelectionColor(Color.black);
			this.setBorderSelectionColor(null);
			this.setBackground(bgColor);

		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean isSelected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, isSelected,
					expanded, leaf, row, hasFocus);

			if (value == null) {
				setText("");
				return this;
			}
			setFont(app.getPlainFont());

			setText(value.toString());
			if (leaf) {

				if (isSelected) {
					setBackgroundSelectionColor(selectionColor);
				} else if (row == ((MyJTree) tree).rollOverRow) {
					setBackgroundNonSelectionColor(rollOverColor);
				} else {
					setBackgroundSelectionColor(bgColor);
					setBackgroundNonSelectionColor(bgColor);
				}

			} else {
				setFont(app.getBoldFont());
				setBackgroundSelectionColor(bgColor);
				setBackgroundNonSelectionColor(bgColor);
				if (row == ((MyJTree) tree).rollOverRow) {
					setBackgroundNonSelectionColor(rollOverColor);
					setBackgroundSelectionColor(rollOverColor);
				}
			}

			return this;
		}
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnRefresh) {
			cmdTreeModel.setRoot(rootSubCommands);
			cmdTreeModel.reload();
			cmdTree.setRootVisible(false);
			fcnTree.collapseRow(0);
			btnRefresh.setEnabled(false);
			helpTextPane.setText("");
			selectedCommand = null;
			selectedFunction = null;
		}

		else if (e.getSource() == btnOnlineHelp) {
			if (selectedCommand != null) {
				app.getGuiManager().openCommandHelp(selectedCommand);
			} else if (selectedFunction != null)
				app.getGuiManager().openHelp(App.WIKI_OPERATORS);
			else
				app.getGuiManager().openHelp("InputBar");
		}

		else if (e.getSource() == btnPaste) {
			doPaste();
		}
	}

	private void doPaste() {

		if (selectedFunction != null) {
			((AlgebraInput) app.getGuiManager().getAlgebraInput())
					.insertString(selectedFunction);
		}

		if (selectedCommand != null) {
			((AlgebraInput) app.getGuiManager().getAlgebraInput())
					.insertCommand(selectedCommand);
		}
	}

	/*
	 * private void insertInputBarCommand(String cmd){
	 * //((AlgebraInput)app.getGuiManager
	 * ().getAlgebraInput()).getInputPanel().insertString(cmd);
	 * //((AlgebraInput)
	 * app.getGuiManager().getAlgebraInput()).insertString(cmd);
	 * //app.getGuiManager().insertStringIntoTextfield(cmd, false,false,false);
	 * 
	 * 
	 * if(app.getGuiManager().getCurrentKeyboardListener() != null){
	 * VirtualKeyboardListener tf =
	 * app.getGuiManager().getCurrentKeyboardListener(); tf.insertString(cmd); }
	 * 
	 * 
	 * }
	 */

	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
	}

	private class MyJTree extends JTree {

		private static final long serialVersionUID = 1L;
		
		public int rollOverRow = -1;

		public MyJTree(TreeModel tm) {
			super(tm);
		}
	}
}
