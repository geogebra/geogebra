/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui.toolbar;
import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.gui.toolbar.ToolbarItem;
import geogebra.gui.layout.DockPanel;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Toolbar configuration panel.
 *  
 * @author Markus Hohenwarter, based on a dialog from geonext.de
 *
 */
public class ToolbarConfigPanel extends javax.swing.JPanel implements java.awt.event.ActionListener, javax.swing.event.TreeExpansionListener {	
	
	private static final long serialVersionUID = 1L;
	
	private static final int SCROLL_PANEL_WIDTH = 300;
	private static final int SCROLL_PANEL_HEIGHT = 400;
	
	private DockPanel dockPanel;
	
	private JButton insertButton;
	private JButton moveUpButton;
	private JButton moveDownButton;
	private JButton deleteButton;
	private JTree tree;
	private JScrollPane configScrollPane;
	private JScrollPane modeScrollPane;
	private JPanel selectionPanel;
	private JList toolList;	
	private DefaultListModel toolListModel;
	private AppD app;	
	
	/**
	 * Creates new toolbar config panel.
	 * @param app application
	 */
	public ToolbarConfigPanel(AppD app) {
		super();	
		this.app = app;			
		
		selectionPanel = new JPanel();
		selectionPanel.setLayout(new BorderLayout(5, 5));
		selectionPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(5, 5));
				
		tree = generateTree();
		
		toolListModel = new DefaultListModel();
		toolList = new JList(toolListModel);
		
		setToolbar(null, app.getGuiManagerD().getToolbarDefinition());	
		
		configScrollPane = new JScrollPane(tree);
		configScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		configScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		//configScrollPane.setSize(150, 150);	
		JPanel scrollSpacePanel = new JPanel();
		scrollSpacePanel.setLayout(new BorderLayout(0, 0));
		scrollSpacePanel.setBorder(new EmptyBorder(3, 5, 3, 5));
		scrollSpacePanel.add(configScrollPane, BorderLayout.CENTER); //
		JPanel scrollPanel = new JPanel();
		scrollPanel.setLayout(new BorderLayout(0, 0));
		scrollPanel.setBorder(new TitledBorder(app.getMenu("Toolbar")));
		scrollPanel.add(scrollSpacePanel, BorderLayout.CENTER);
				
		scrollPanel.setPreferredSize(new Dimension(SCROLL_PANEL_WIDTH, SCROLL_PANEL_HEIGHT));
		//
		selectionPanel.add(scrollPanel, BorderLayout.WEST);
		//
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(Box.createVerticalGlue());
		
		insertButton = new JButton("< " + app.getPlain("Insert"));		
		insertButton.addActionListener(this);
		insertButton.setAlignmentX(CENTER_ALIGNMENT);
		buttonPanel.add(insertButton);
		buttonPanel.add(Box.createVerticalStrut(10));
		
		deleteButton = new javax.swing.JButton(app.getPlain("Remove") + " >");		
		deleteButton.addActionListener(this);
		deleteButton.setAlignmentX(CENTER_ALIGNMENT);
		buttonPanel.add(deleteButton);		
		
		buttonPanel.add(Box.createVerticalGlue());
		selectionPanel.add(buttonPanel, BorderLayout.CENTER);
		
		
		
		//		
		JPanel upDownPanel = new JPanel();
		moveUpButton = new javax.swing.JButton("\u25b2 " + app.getPlain("Up"));	
		moveUpButton.addActionListener(this);
		upDownPanel.add(moveUpButton);
		//
		moveDownButton = new javax.swing.JButton("\u25bc " + app.getPlain("Down"));		
		moveDownButton.addActionListener(this);
		upDownPanel.add(moveDownButton);
		
		scrollPanel.add(upDownPanel, BorderLayout.SOUTH);

		//
		
		JPanel buttonAllPanel = new JPanel(new BorderLayout());			
		buttonAllPanel.add(buttonPanel, BorderLayout.NORTH);
		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.Y_AXIS));
		tempPanel.add(Box.createRigidArea(new Dimension(10,150)));
		tempPanel.add(buttonAllPanel);
		tempPanel.add(Box.createVerticalGlue());
		
		selectionPanel.add(tempPanel, BorderLayout.CENTER);
		JPanel modePanel = new JPanel();
		modePanel.setLayout(new BorderLayout(0, 0));
		modePanel.setBorder(new TitledBorder(app.getMenu("Tools")));
		
		ListSelectionModel lsm = toolList.getSelectionModel();
		lsm.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		toolList.setBackground(SystemColor.text);
		modeScrollPane = new JScrollPane(toolList);		
		modeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		modeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		toolList.setCellRenderer(new ModeCellRenderer(app));
		toolList.setSelectedIndex(0);
		//
		//
		JPanel modeSpacePanel = new JPanel();
		modeSpacePanel.setLayout(new BorderLayout(0, 0));
		modeSpacePanel.setBorder(new EmptyBorder(3, 5, 3, 5));
		modeSpacePanel.add("Center", modeScrollPane);
		
		modePanel.add("Center", modeSpacePanel);
		modePanel.setPreferredSize(new Dimension(SCROLL_PANEL_WIDTH, SCROLL_PANEL_HEIGHT));
		selectionPanel.add("East", modePanel);		
		add("Center", selectionPanel);
		
		try {
			tree.setSelectionRow(1);
		} catch (Exception exc) {
			tree.setSelectionRow(0);
		}				
	}
		
	
	
	
	/**
	 * Handles remove, add and up, down buttons.
	 */
	public void actionPerformed(ActionEvent e) {					
		// get selected node in tree
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();							
		TreePath selPath = tree.getSelectionPath();
		if (selPath == null) {			
		    tree.setSelectionRow(0); // take root if nothing is selected
		    selPath = tree.getSelectionPath();
		} 
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
		// remember row number
		int selRow = tree.getRowForPath(selPath);	
		
		DefaultMutableTreeNode parentNode;		
		if (selNode == root) { // root is selected
			parentNode = selNode;
		} else {
			parentNode = (DefaultMutableTreeNode) selNode.getParent();							
		}	
		int childIndex = parentNode.getIndex(selNode);
		
		Object src = e.getSource();	
		
		// DELETE
		if (src == deleteButton) {									
			if (selRow > 0) { // not root				
				Object userOb = selNode.getUserObject();
				if (userOb == null)
					userOb = ((DefaultMutableTreeNode) selNode.getFirstChild()).getUserObject();
				
				// not move mode: delete node
				model.removeNodeFromParent(selNode);				
				// remove empty menu too	
				if (parentNode.getChildCount() == 0) {
					if (!parentNode.isRoot()) { 					
						model.removeNodeFromParent(parentNode);
						selRow--;
					}
				}
				
				toolListModel.addElement(userOb);
				sortToolList();
				
				// select node at same row or above
				if (selRow >= tree.getRowCount())
					selRow--;
				tree.setSelectionRow(selRow);
			}						
		} 
		// INSERT
		else if (src == insertButton) {		
			childIndex++;
			
			boolean didInsert = false;
			Object [] tools = toolList.getSelectedValues();						
			for (int i=0; i < tools.length; i++) {
				// check if too is already there
				Integer modeInt = (Integer)tools[i];
				if (modeInt.intValue() > -1 && containsTool(root, (Integer)tools[i]))
					continue;
				
				DefaultMutableTreeNode newNode;
				if (parentNode == root && modeInt.intValue() > -1) {
					// parent is root: create new submenu
					newNode = new DefaultMutableTreeNode();			
					newNode.add(new DefaultMutableTreeNode(modeInt));
				}
				else {
					// parent is submenu
					newNode = new DefaultMutableTreeNode(modeInt);						
				}											
				model.insertNodeInto(newNode, parentNode, childIndex++);				
				didInsert = true;	
				
				// remove node from list of unused tools if the node is not a separator
				if(modeInt.intValue() > -1)
					toolListModel.removeElement(modeInt);
			}
			
			if (didInsert) {
				// make sure that root is expanded
				tree.expandPath(new TreePath(model.getRoot()));
				
				// select first inserted node
				tree.setSelectionRow(++selRow);
				tree.scrollRowToVisible(selRow);
				configScrollPane.getHorizontalScrollBar().setValue(0); // scroll to left
				
				// sort tool list
				sortToolList();
			}
		}
		
		// UP
		else if (src == moveUpButton) {
			if (selNode == root)
				return;
						
			if (parentNode.getChildBefore(selNode) != null) {							
				model.removeNodeFromParent(selNode);
				model.insertNodeInto(selNode, parentNode, --childIndex);
				tree.setSelectionRow(--selRow);
			}			
		}
		
		// DOWN
		else if (src == moveDownButton) {
			if (selNode == root)
				return;
						
			if (parentNode.getChildAfter(selNode) != null) {							
				model.removeNodeFromParent(selNode);
				model.insertNodeInto(selNode, parentNode, ++childIndex);
				tree.setSelectionRow(++selRow);
			}			
		}
	}	
	
	private boolean containsTool(DefaultMutableTreeNode node, Integer mode) {
        // compare modes
		Object ob = node.getUserObject();
        if (ob != null && mode.compareTo((Integer)ob) == 0) {           	
        	return true;
        }
    
        if (node.getChildCount() >= 0) {
            for (Enumeration<DefaultMutableTreeNode> e=node.children(); e.hasMoreElements(); ) {
            	DefaultMutableTreeNode n = e.nextElement();
            	if (containsTool(n, mode))
            		return true;
            }
        }
        return false;
    }
	
	/**
	 * Inits the toolbar tree in this panel to show the given toolbar definition string.
	 * @param dockPanel dock panel
	 * @param toolbarDefinition toolbar as string (sequence of numbers and delimiters)
	 */
	public void setToolbar(DockPanel dockPanel, String toolbarDefinition) {
		this.dockPanel = dockPanel;
		
		// create new tree model
		Vector<ToolbarItem> toolVec = ToolBar.parseToolbarString(toolbarDefinition);		
		DefaultTreeModel model = new DefaultTreeModel(generateRootNode(toolVec));
		tree.setModel(model);		
		collapseAllRows();	
		tree.setRowHeight(-1);
		
		Vector<Integer> allTools = generateToolsVector(Toolbar.getAllTools(app));
		Vector<Integer> usedTools = generateToolsVector(toolbarDefinition);
		
		toolListModel.clear();
		toolListModel.addElement(ToolBar.SEPARATOR); // always display the separator in the tools list
		
		for(Iterator<Integer> iter = allTools.iterator(); iter.hasNext();) {
			Integer next = iter.next();
			
			if(!usedTools.contains(next)) {
				toolListModel.addElement(next);
			}
		}
	}
	
	public void apply() {
		if(dockPanel != null) {
			dockPanel.setToolbarString(getToolBarString());
		} else {
			app.getGuiManagerD().setToolBarDefinition(getToolBarString());
		}
	}
	
	/**
	 * Reset the current toolbar to its default state.
	 */
	public void resetDefaultToolbar() {
		if(dockPanel != null) {
			setToolbar(dockPanel, dockPanel.getDefaultToolbarString());
		} else {
			setToolbar(null, app.getGuiManagerD().getDefaultToolbarString());
		}
	}
	
	/**
	 * Returns the custom toolbar created with this panel as a String.
	 * Separator ("||" between menus, "," in menu), New menu starts with "|"
	 * @return toolbar as string
	 */
	public String getToolBarString() {								
		StringBuilder sb = new StringBuilder();
		
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();		            
        for (int i=0; i < root.getChildCount(); i++) {
        	DefaultMutableTreeNode menu = (DefaultMutableTreeNode) root.getChildAt(i);
        	
        	if (menu.getChildCount() == 0) { // new menu with separator
        		sb.append("|| ");
        	} 
        	else if (i > 0 && !sb.toString().endsWith("|| ")) // new menu
        		sb.append("| ");
        	
        	for (int j=0; j < menu.getChildCount(); j++) {
            	DefaultMutableTreeNode node = (DefaultMutableTreeNode) menu.getChildAt(j);
            	int mode = ((Integer) node.getUserObject()).intValue();
            	            	
            	if (mode < 0) // separator
            		sb.append(", ");
            	else { // mode number            		
            		sb.append(mode);
            		sb.append(" ");
            	}            	
            }        	        	
        }
        
        return sb.toString().trim();    
	}		
	
	/**
	 * Collapses all rows
	 */
	public void collapseAllRows() {
		int z = tree.getRowCount();
		for (int i = z; i > 0; i--) {
			tree.collapseRow(i);
		}
	}
	
	/**
	 * @param toolbarDefinition toolbar definition string (see EuclidianConstants)
	 * @return vector of menus (vectors of ints) and separators (ints)
	 * 
	 */
	public Vector<Integer> generateToolsVector(String toolbarDefinition) {				
		Vector<Integer> vector = new Vector<Integer>();		
		// separator
		vector.add(ToolBar.SEPARATOR);
				
		// get default toolbar as nested vectors
		Vector<ToolbarItem> defTools = ToolBar.parseToolbarString(toolbarDefinition);				
		for (int i=0; i < defTools.size(); i++) {
			ToolbarItem element = defTools.get(i);
			
			if (element.getMenu()!=null) {
				Vector<Integer> menu = element.getMenu();
				for (int j=0; j < menu.size(); j++) {
					Integer modeInt = menu.get(j);
					int mode = modeInt.intValue();
					if (mode != -1)
						vector.add(modeInt);
				}
			} else {
				Integer modeInt = element.getMode();
				int mode = modeInt.intValue();
				if (mode != -1)
					vector.add(modeInt); 
			}			
		}				
		return vector;
	}
	/**
	 *
	 */
	private JTree generateTree() {			
		final JTree jTree = new JTree() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void setExpandedState(TreePath path, boolean state) {
	            // Ignore all collapse requests of root        	
	            if (path != getPathForRow(0)) {
	                super.setExpandedState(path, state);
	            }
	        }
	    };	    	    	   
		
		jTree.setCellRenderer(new ModeCellRenderer(app));
		jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		jTree.putClientProperty("JTree.lineStyle", "Angled");
		jTree.addTreeExpansionListener(this);						
		
		return jTree;
	}
	/**
	 * @param toolbarModes list of menus and separators
	 * @return toolbar as DefaultMutableTreeNode 
	 * 
	 */
	public DefaultMutableTreeNode generateRootNode(Vector<ToolbarItem> toolbarModes) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode();
		
		for (int i = 0; i < toolbarModes.size(); i++) {
			ToolbarItem ob = toolbarModes.get(i);
			if (ob.getMenu()!=null) {
				Vector<Integer> menu = ob.getMenu();  
				DefaultMutableTreeNode sub = new DefaultMutableTreeNode();
				for (int j = 0; j < menu.size(); j++) {
					sub.add(new DefaultMutableTreeNode(menu.get(j)));
				}
				node.add(sub);
			}
			else
				node.add(new DefaultMutableTreeNode(ob.getMode()));
		}
		return node;
	}

	/**
	 * Add an item to the tool list
	 * 
	 * TODO Rename method
	 * TODO Use this method to insert new items into the model
	 * TODO Use the default toolbar vector to keep the standard sorting
	 * 
	 * @author Florian Sonner
	 * @version 2008-10-22
	 */
	private void sortToolList() {
		/*int numItems = toolListModel.getSize();

		if (numItems < 2)
			return;

		// copy list data into an array
		Integer[] a = new Integer[numItems];
		for (int i = 0; i < numItems; ++i) {
			a[i] = (Integer) toolListModel.getElementAt(i);
		}

		// sort array..
		Arrays.sort(a);

		// copy the sorted array back into the model
		for (int i = 0; i < numItems; ++i) {
			toolListModel.setElementAt(a[i], i);
		}*/
	}
		
	/**
	 * 
	 */
	public void treeCollapsed(javax.swing.event.TreeExpansionEvent event) {/* do nothing*/}
	
	/**
	 * 
	 */
	public void treeExpanded(javax.swing.event.TreeExpansionEvent event) {
		/*tabbed.invalidate();
		tabbed.validateTree();*/
	}
	
	/**
	 * @param e list selection event 
	 */
	public void valueChanged(javax.swing.event.ListSelectionEvent e) {/* do nothing*/}
	
	
}