package org.geogebra.desktop.gui.inputbar;

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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.gui.util.TableSymbols;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.LowerCaseDictionary;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.util.GeoGebraIconD;
import org.geogebra.desktop.gui.util.SelectionTableD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;

public class InputBarHelpPanelD extends JPanel implements TreeSelectionListener,
		ActionListener, FocusListener, SetLabels {

	private static final long serialVersionUID = 1L;
	/** application */
	AppD app;
	private InputBarHelpPanelD thisPanel;
	private Color bgColor = Color.WHITE;

	private MyJTree cmdTree;
	private DefaultMutableTreeNode functionTitleNode, rootSubCommands,
			rootAllCommands;
	private DefaultTreeModel cmdTreeModel;

	private String selectedCommand;
	String rollOverCommand;

	public String getSelectedCommand() {
		return selectedCommand;
	}

	private String selectedFunction;

	private JTextPane helpTextPane;
	private JButton btnOnlineHelp, btnRefresh;
	private SelectionTableD functionTable;
	private JScrollPane tablePanel;
	private JPanel syntaxHelpPanel;
	private JSplitPane cmdSplitPane;
	private JLabel titleLabel;
	private JLabel syntaxLabel;
	private JButton btnPaste;
	private JScrollPane scroller;
	private LocalizationD loc;

	/***************************************************
	 * Constructor
	 */
	public InputBarHelpPanelD(AppD app) {

		this.app = app;
		this.loc = app.getLocalization();
		thisPanel = this;
		this.setOpaque(true);
		// this.setBackground(Color.blue);
		bgColor = this.getBackground().brighter();

		createFunctionPanel();
		createCommandTree();
		createSyntaxPanel();

		JPanel commandPanel = new JPanel(new BorderLayout());
		commandPanel.add(cmdTree, BorderLayout.CENTER);
		commandPanel.setBorder(BorderFactory.createEmptyBorder());
		scroller = new JScrollPane(commandPanel);

		// scroller.setBorder(BorderFactory.createEmptyBorder());
		scroller.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel titlePanel = new JPanel();
		titlePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0,
						SystemColor.controlShadow),
				BorderFactory.createEmptyBorder(0, 2, 0, 2)));
		titlePanel.setLayout(new BorderLayout());

		// titlePanel.setBackground(titleColor);
		titleLabel = new JLabel();
		// titleLabel.setForeground(Color.darkGray);

		titlePanel.add(titleLabel, loc.borderWest());

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

	private JScrollPane syntaxScroller;

	// private JLabel errorLabel;
	private void createSyntaxPanel() {
		JPanel p = new JPanel(new BorderLayout());
		try {
			// might throw NPE in Ubuntu
			helpTextPane = new JTextPane();
			// helpTextArea.setText("");
			helpTextPane.setEditable(false);
			// helpTextArea.setMinimumSize(new Dimension(200,300));
			helpTextPane.setBorder(BorderFactory.createEmptyBorder(8, 5, 2, 5));
			helpTextPane.setBackground(bgColor);
			p.add(helpTextPane, BorderLayout.CENTER);
		} catch (Exception e) {
			Log.warn("no syntax panel");
		}

		p.setBorder(BorderFactory.createEmptyBorder());
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 1, 0,
						SystemColor.controlShadow),
				BorderFactory.createEmptyBorder(0, 2, 0, 2)));
		syntaxLabel = new JLabel();
		syntaxLabel.setForeground(Color.darkGray);
		titlePanel.add(syntaxLabel, loc.borderWest());

		syntaxHelpPanel = new JPanel(new BorderLayout());

		syntaxScroller = new JScrollPane(this.helpTextPane);
		syntaxScroller.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		syntaxScroller.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		// scroller.setBorder(BorderFactory.createEmptyBorder());

		JPanel corner = new JPanel(new FlowLayout());
		corner.setBackground(this.getBackground());
		syntaxScroller.setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER,
				corner);

		syntaxHelpPanel.add(syntaxScroller, BorderLayout.CENTER);
		// syntaxHelpPanel.add(titlePanel,BorderLayout.NORTH);
	}

	private JPanel createButtonPanel() {

		btnRefresh = new JButton(app.getScaledIcon(GuiResourcesD.VIEW_REFRESH));
		btnRefresh.setBorderPainted(false);
		btnRefresh.setFocusable(false);
		btnRefresh.setEnabled(false);
		btnRefresh.addActionListener(this);
		btnRefresh.setContentAreaFilled(false);
		btnRefresh.setPreferredSize(new Dimension(24, 20));

		btnPaste = new JButton();
		btnPaste.addActionListener(this);
		btnPaste.setFocusable(false);

		btnOnlineHelp = new JButton(
				app.getLocalization().getMenu("ShowOnlineHelp"));
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
		buttonPanel.add(leftPanel, loc.borderWest());
		buttonPanel.add(rightPanel, loc.borderEast());
		buttonPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0,
						SystemColor.controlShadow),
				BorderFactory.createEmptyBorder(0, 2, 0, 2)));
		// errorLabel = new JLabel();
		// errorLabel.setForeground(Color.RED);
		// buttonPanel.add(errorLabel, BorderLayout.SOUTH);

		return buttonPanel;
	}

	private void createFunctionPanel() {

		functionTable = new SelectionTableD(app,
				TableSymbols.getTranslatedFunctions(app), -1, 2,
				new Dimension(20, 16), SelectionTable.MODE_TEXT);
		functionTable.setShowGrid(true);
		functionTable.setHorizontalAlignment(SwingConstants.LEFT);
		functionTable.setBorder(
				BorderFactory.createLineBorder(functionTable.getGridColor()));
		functionTable.addMouseListener(new TableSelectionListener());
		functionTable.setBackground(bgColor);
		// functionTable.setVisible(false);

		functionTitleNode = new DefaultMutableTreeNode(
				loc.getMenu("MathematicalFunctions"));
		functionTitleNode.add(new DefaultMutableTreeNode(""));

		/*
		 * fPanel = new JPanel(new BorderLayout());
		 * fPanel.add(Box.createRigidArea(new Dimension(35, 1)),
		 * loc.borderWest()); fPanel.add(functionTable, BorderLayout.CENTER);
		 * fPanel.setBackground(bgColor);
		 */

		tablePanel = new JScrollPane(functionTable);
		// tablePanel.add(functionTable, BorderLayout.NORTH);
		// functionTable.setPreferredSize(new Dimension(500, 500));

		functionTable.setAlignmentX(LEFT_ALIGNMENT);

		tablePanel.setBackground(bgColor);
	}

	/** mouse listener to handle table selection */
	public class TableSelectionListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {

			if (e.getSource() == functionTable) {
				if (!cmdTree.getSelectionModel().isSelectionEmpty()) {
					cmdTree.clearSelection();
				}
				selectedFunction = (String) functionTable.getSelectedValue();
				selectedCommand = null;
				helpTextPane.setText("");
				if (e.getClickCount() == 2) {
					doPaste();
				}
			}
		}
	}

	@Override
	public void setLabels() {
		setLabels(true);
	}

	private void setLabels(boolean setCommands) {
		if (setCommands) {
			setCommands();
		}
		cmdTreeModel.setRoot(rootSubCommands);
		cmdTreeModel.reload();

		titleLabel.setText(loc.getMenu("InputHelp"));
		syntaxLabel.setText(loc.getMenu("Syntax"));
		btnPaste.setText(loc.getMenu("Paste"));
		btnOnlineHelp.setText(loc.getMenu("ShowOnlineHelp"));
		if (helpTextPane != null) {
			helpTextPane.setText(null);
		}
		functionTable.populateModel(TableSymbols.getTranslatedFunctions(app));
		functionTitleNode.setUserObject(loc.getMenu("MathematicalFunctions"));
		rootAllCommands.setUserObject(loc.getMenu("AllCommands"));
		updateFonts();
		repaint();
	}

	public void updateFonts() {

		functionTable.updateFonts();
		if (helpTextPane != null) {
			helpTextPane.setFont(app.getPlainFont());
		}
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
		scroller.getVerticalScrollBar()
				.setBlockIncrement(10 * app.getFontSize());
		scroller.getVerticalScrollBar().setUnitIncrement(3 * app.getFontSize());
		btnOnlineHelp.setFont(app.getPlainFont());
		btnPaste.setFont(app.getPlainFont());
		btnRefresh.setIcon(app.getScaledIcon(GuiResourcesD.VIEW_REFRESH));
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

		cmdTree.getSelectionModel()
				.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
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
			@Override
			public void treeCollapsed(TreeExpansionEvent e) {
				// only handle expanding
			}

			@Override
			public void treeExpanded(TreeExpansionEvent e) {
				thisPanel.btnRefresh.setEnabled(true);
			}
		});
	}

	public void setCommands() {
		if (rootSubCommands == null) {
			rootSubCommands = new DefaultMutableTreeNode();
		}
		if (rootAllCommands == null) {
			rootAllCommands = new DefaultMutableTreeNode(
					loc.getMenu("AllCommands"));
		}
		rootSubCommands.removeAllChildren();
		rootAllCommands.removeAllChildren();

		DefaultMutableTreeNode child;
		LowerCaseDictionary[] subDict = app.getSubCommandDictionary();

		// load the sub-command nodes
		for (int i = 0; i < subDict.length; i++) {
			if (subDict[i].isEmpty()) {
				continue;
			}

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
					addNodeInSortedOrder(child,
							new DefaultMutableTreeNode(cmdName));
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

		functionTitleNode = new DefaultMutableTreeNode(
				loc.getMenu("MathematicalFunctions"));
		rootSubCommands.insert(functionTitleNode, 0);
	}

	/**
	 * Adds a new node to a sorted tree. Node leaf strings are sorted in
	 * ascending order using a locale-sensitive Collator class.
	 */
	private void addNodeInSortedOrder(DefaultMutableTreeNode parent,
			DefaultMutableTreeNode child) {

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
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) cmdTree
				.getLastSelectedPathComponent();

		if (node == null)
		 {
			return; // Nothing is selected.
		}

		if (node.isLeaf()) {
			Object nodeInfo = node.getUserObject();
			selectedCommand = (String) nodeInfo;
			selectedFunction = null;
			// insertInputBarCommand(selectedCommand);
			showSelectedSyntax();
			if (functionTable.getSelectedIndex() != -1) {
				functionTable.clearSelection();
			}
		}
	}

	private class RollOverListener extends MouseInputAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getClickCount() == 2) {
				doPaste();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// nothing to do
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
			if (e.getSource() instanceof MyJTree) {
				tree = (MyJTree) e.getSource();
			} else {
				return;
			}

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
						// StringBuilder sb = new StringBuilder();
						// cmd = app.getReverseCommand(cmd); // internal name
						// CommandProcessor.getCommandSyntax(sb, app, cmd, -1);
						// sb.append(app.getLocalization().getCommandSyntax(cmd));
						// helpTextArea.setText(sb.toString());

					}
				}

				tree.repaint();
			}
		}
	}

	private void showSelectedSyntax() {

		String cmd = app.getReverseCommand(selectedCommand); // internal name

		if (cmd == null) {
			syntaxHelpPanel.remove(syntaxScroller);
			syntaxHelpPanel.add(tablePanel);
			SwingUtilities.updateComponentTreeUI(syntaxHelpPanel);
			return;
		}

		syntaxHelpPanel.remove(tablePanel);
		syntaxHelpPanel.add(syntaxScroller);
		SwingUtilities.updateComponentTreeUI(syntaxHelpPanel);
		// String s = "Syntax:\n" + app.getCommandSyntax(cmd);
		String description = app.getLocalization().getCommandSyntax(cmd);
		String descriptionCAS = app.getLocalization().getCommandSyntaxCAS(cmd);
		String descriptionCASHeader = "\n" + loc.getMenu("Type.CAS") + ":\n";

		StyledDocument doc = helpTextPane.getStyledDocument();
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e1) {
			// this should never occur
			e1.printStackTrace();
		}

		// define the regular and italic style
		Style def = StyleContext.getDefaultStyleContext()
				.getStyle(StyleContext.DEFAULT_STYLE);
		Style regular = doc.addStyle("regular", def);

		// changed to use getFontCanDisplayAwt() so that Armenian displays OK
		StyleConstants.setFontFamily(def,
				app.getFontCanDisplayAwt(description).getFamily());

		Style s = doc.addStyle("italic", regular);
		StyleConstants.setItalic(s, true);

		SimpleAttributeSet attrs = new SimpleAttributeSet();
		if (description.length() > 10) {
			StyleConstants.setFirstLineIndent(attrs, -50);
			StyleConstants.setLeftIndent(attrs, 50);
			StyleConstants.setBold(attrs, false);
		} else {
			// fix for indent problem with short strings on some platforms
			StyleConstants.setFirstLineIndent(attrs, 0);
			StyleConstants.setLeftIndent(attrs, 0);
			StyleConstants.setBold(attrs, true);
		}

		doc.setParagraphAttributes(0, doc.getLength(), attrs, false);

		if (loc.isCASCommand(cmd)) {
			if (!description.equals(cmd + Localization.syntaxStr)) {
				try {
					doc.insertString(doc.getLength(), description + "\n",
							doc.getStyle("regular"));
				} catch (BadLocationException e) {
					// should never occur
					e.printStackTrace();
				}
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
			update();
			selectionColor = GColorD.getAwtColor(
					GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR);
			// this.getBackgroundSelectionColor()
			rollOverColor = Color.LIGHT_GRAY;

			this.setTextSelectionColor(Color.black);
			this.setTextNonSelectionColor(Color.black);
			this.setBorderSelectionColor(null);
			this.setBackground(bgColor);

		}

		public void update() {
			setOpenIcon(app.getScaledIcon(GuiResourcesD.TREE_CLOSE));
			setClosedIcon(app.getScaledIcon(GuiResourcesD.TREE_OPEN));
			setLeafIcon(GeoGebraIconD.createEmptyIcon(5, 1));

		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean isSelected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, isSelected,
					expanded, leaf, row, hasFocus);
			update();
			if (value == null) {
				setText("");
				return this;
			}
			setFont(app.getPlainFont());

			setText(value.toString());
			if (leaf) {

				if (isSelected) {
					setBackgroundSelectionColor(selectionColor);
				} else if ((tree instanceof MyJTree)
						&& row == ((MyJTree) tree).rollOverRow) {
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

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnRefresh) {
			cmdTreeModel.setRoot(rootSubCommands);
			cmdTreeModel.reload();
			cmdTree.setRootVisible(false);
			btnRefresh.setEnabled(false);
			helpTextPane.setText("");
			selectedCommand = null;
			selectedFunction = null;
		}

		else if (e.getSource() == btnOnlineHelp) {
			if (selectedCommand != null) {
				((GuiManagerD) app.getGuiManager())
						.openCommandHelp(selectedCommand);
			} else if (selectedFunction != null) {
				((GuiManagerD) app.getGuiManager())
						.openHelp(App.WIKI_OPERATORS);
			} else {
				((GuiManagerD) app.getGuiManager()).openHelp("InputBar");
			}
		}

		else if (e.getSource() == btnPaste) {
			doPaste();
		}
	}

	private void doPaste() {

		if (selectedFunction != null) {
			((AlgebraInputD) ((GuiManagerD) app.getGuiManager())
					.getAlgebraInput()).insertString(selectedFunction);
		}

		if (selectedCommand != null) {
			((AlgebraInputD) ((GuiManagerD) app.getGuiManager())
					.getAlgebraInput()).insertCommand(selectedCommand);
		}
	}

	/*
	 * private void insertInputBarCommand(String cmd){
	 * //((AlgebraInput)app.getGuiManager
	 * ().getAlgebraInput()).getInputPanel().insertString(cmd);
	 * //((AlgebraInput)
	 * ((GuiManagerD)app.getGuiManager()).getAlgebraInput()).insertString(cmd);
	 * //((GuiManagerD)app.getGuiManager()).insertStringIntoTextfield(cmd,
	 * false,false,false);
	 * 
	 * 
	 * if(((GuiManagerD)app.getGuiManager()).getCurrentKeyboardListener() !=
	 * null){ VirtualKeyboardListener tf =
	 * ((GuiManagerD)app.getGuiManager()).getCurrentKeyboardListener();
	 * tf.insertString(cmd); }
	 * 
	 * 
	 * }
	 */

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
	}

	private static class MyJTree extends JTree {

		private static final long serialVersionUID = 1L;

		public int rollOverRow = -1;

		public MyJTree(TreeModel tm) {
			super(tm);
		}
	}

	public void showError(String message) {
		// if (message == null) {
		// errorLabel.setVisible(false);
		// } else {
		// errorLabel.setVisible(true);
		// errorLabel.setText(message);
		// }

	}

	public void focusCommand(String command) {
		for (int i = 0; i < rootSubCommands.getChildCount(); i++) {
			TreeNode group = rootSubCommands.getChildAt(i);
			for (int j = 0; j < group.getChildCount(); j++) {

				if (group.getChildAt(j) instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode cmdNode = (DefaultMutableTreeNode) group
							.getChildAt(j);
					Log.debug(cmdNode.getUserObject());
					if (command.equals(cmdNode.getUserObject())) {
						TreePath path = new TreePath(
								((DefaultTreeModel) cmdTree.getModel())
										.getPathToRoot(group.getChildAt(j)));
						cmdTree.setSelectionPath(path);
						return;
					}
				}
			}
		}
	}
}
