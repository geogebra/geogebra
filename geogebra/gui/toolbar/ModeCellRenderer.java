/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui.toolbar;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class ModeCellRenderer extends DefaultTreeCellRenderer
implements ListCellRenderer {
	
	private Application app;
	
	public ModeCellRenderer(Application app) {
		setOpaque(true);
		setBackgroundNonSelectionColor(Color.white);
		this.app = app;	
	}
	
	public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean selected,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus) {		
				
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;			
		
		// mode number				
		if (leaf) {			
			Object ob = node.getUserObject();	
			if (ob instanceof Integer) {
				// mode node
				int mode = ((Integer) ob).intValue();	
				handleModeNode(mode);
			} else {
				// root node
				handleRootNode(node);
			}
		} 
		// folder
		else {		
			if (row == 0) {				
				handleRootNode(node);
			} else {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getFirstChild();
				Object ob = childNode.getUserObject();							
				handleModeNode(((Integer) ob).intValue());									
			}
		}
		
		handleSelection(selected);		
		
		return this;
	}
	
	public Component getListCellRendererComponent(
	         JList list,
	         Object value,
	         int index,
	         boolean isSelected,
	         boolean cellHasFocus)
     {
		handleModeNode(((Integer) value).intValue());
		handleSelection(isSelected);
        return this;
     }
	
	private void handleRootNode(DefaultMutableTreeNode node) {
		setIcon(null);
		Object ob = node.getUserObject();
		if (ob != null)
			setText(ob.toString());
		else
			setText(app.getMenu("Toolbar"));
	}
		
	private void handleModeNode(int mode) {
		if (mode == -1) {			
			setText("\u2500\u2500\u2500 " + app.getMenu("Separator"));
			setIcon(null);			
		} else {			
			setText(app.getToolName(mode));
			setIcon(app.getModeIcon(mode));
		}						
	}
	
	private void handleSelection(boolean selected) {
		if (selected) {
			setBackground(Application.COLOR_SELECTION);
		} else {				
			setBackground(getBackgroundNonSelectionColor());				
		}
	}
	
}