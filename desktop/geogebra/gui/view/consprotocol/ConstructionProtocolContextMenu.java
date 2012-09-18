/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.gui.view.consprotocol;

import geogebra.common.kernel.Construction;
import geogebra.gui.GuiManagerD;
import geogebra.gui.view.consprotocol.ConstructionProtocolView.ColumnKeeper;
import geogebra.main.AppD;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.TableColumn;
/**
 * Context menu for construction protocol
 * 
 *
 */
public class ConstructionProtocolContextMenu extends JPopupMenu {
	
	private static final long serialVersionUID = 1L;
	/** Application */
	AppD app;
	private Construction cons;
	/** Construction protocol view associated with this menu */
	ConstructionProtocolView constprotView;
	
	/**
	 * Creates new context menu
	 * @param app application
	 */
	public ConstructionProtocolContextMenu(AppD app){
		this.app = app;
		this.cons = app.getKernel().getConstruction();
		constprotView = ((GuiManagerD)app.getGuiManager()).getConstructionProtocolView();
		initItems();
	}


	
	/**
	 * Initialize the menu items.
	 */
	private void initItems() {		
		// title for menu
		JLabel title = new JLabel(app.getPlain("ConstructionProtocol"));
		
		JMenu colMenu = new JMenu(app.getMenu("Columns"));
		JMenu optionsMenu = new JMenu(app.getMenu("Options"));
		
        title.setFont(app.getBoldFont());                      
        title.setBackground(Color.white);
        title.setForeground(Color.black);
                
		title.setIcon(app.getEmptyIcon());
        title.setBorder(BorderFactory.createEmptyBorder(5, 15, 2, 5));      
        add(title);
        addSeparator();
        
        title.addMouseListener(new MouseAdapter() {
        	@Override
			public void mouseClicked(MouseEvent e) {
        		setVisible(false);
        	}
        });
		
        // menu items	
		
        
        //"Columns" menu
		for (int k = 1; k < constprotView.getTableColumns().length; k++) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(
					constprotView.getData().columns[k].getTranslatedTitle());
			TableColumn column = constprotView.getTableColumns()[k];
			item.setSelected(constprotView.isColumnInModel(column));
			ColumnKeeper colKeeper = constprotView.new ColumnKeeper(column, constprotView.getData().columns[k]);
			item.addActionListener(colKeeper);
			colMenu.add(item);
			
		}
		add(colMenu);
		
		
		//"Options" menu
		JCheckBoxMenuItem cbShowOnlyBreakpoints = new JCheckBoxMenuItem(
				app.getPlain("ShowOnlyBreakpoints"));
		cbShowOnlyBreakpoints.setSelected(cons.showOnlyBreakpoints());
		
		cbShowOnlyBreakpoints.addActionListener(constprotView);
		optionsMenu.add(cbShowOnlyBreakpoints);

		JCheckBoxMenuItem cbUseColors = new JCheckBoxMenuItem(
				app.getPlain("ColorfulConstructionProtocol"));
		cbUseColors.setSelected(constprotView.getUseColors());
		cbUseColors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				constprotView.setUseColors(!constprotView.getUseColors());
				//constprotView.getData().updateAll();
			}
		});
		optionsMenu.add(cbUseColors);
		add(optionsMenu);
		
		//Export and Print menu		
		add(constprotView.getExportHtmlAction());
		add(constprotView.getPrintPreviewAction());
		
		//Help menu
		JMenuItem mi = new JMenuItem(app.getMenu("FastHelp"),
				app.getImageIcon("help.png"));
		ActionListener lstHelp = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				app.showHelp("ConstructionProtocolHelp");
				requestFocus();
			}
		};
		mi.addActionListener(lstHelp);
		add(mi);
		
		app.setComponentOrientation(this);

		
	}
	
	
}
