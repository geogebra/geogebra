package geogebra.gui.view.consprotocol;

import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.view.consprotocol.ConstructionProtocolView.ColumnKeeper;
import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JToolBar;
import javax.swing.table.TableColumn;


public class ConstructionProtocolStyleBar extends JToolBar implements ActionListener {
	
	private static final long serialVersionUID = 1L;

	/**
	 * The construction protocol view which uses this tool bar.
	 */
	protected ConstructionProtocolView cpView;
	
	/**
	 * Instance of the application.
	 */
	protected Application app;
	
	PopupMenuButton btnColumns, btnOptions;
	JButton btnExport, btnPrint, btnHelp;
	JCheckBoxMenuItem miShowOnlyBreakpoints, miColorfulConstructionProtocol;
	
	
	/**
	 * Helper bar.
	 * @param cpView
	 * @param app
	 */
	public ConstructionProtocolStyleBar(ConstructionProtocolView cpView, Application app) {
		this.cpView = cpView;
		this.app = app;
		
		setFloatable(false);
		
		addButtons();
	}
	
	/**
	 * add the buttons
	 */
	protected void addButtons(){
		
		// "columns" button
		
		btnColumns = new PopupMenuButton(app){
			public boolean prepareToShowPopup(){
				JCheckBoxMenuItem item;
				removeAllMenuItems();
				for (int k = 1; k < cpView.getTableColumns().length; k++) {
					item = new JCheckBoxMenuItem(
							cpView.getData().columns[k].getTranslatedTitle());
					TableColumn column = cpView.getTableColumns()[k];
					item.setSelected(cpView.isColumnInModel(column));
					ColumnKeeper colKeeper = cpView.new ColumnKeeper(column, cpView.getData().columns[k]);
					item.addActionListener(colKeeper);
					btnColumns.addPopupMenuItem(item);
					
				}
				
				return true;
			}
		};
		btnColumns.setKeepVisible(true);
		btnColumns.setStandardButton(true);  // mouse clicks over total button region
		btnColumns.setIcon(app.getImageIcon("header_column.png"));
		
		add(btnColumns);
		
		
		addSeparator();
		
		//options button
		// PopupMenuButton without selection table, add JMenuItems directly.
		 
		btnOptions = new PopupMenuButton(app){
			public boolean prepareToShowPopup(){
				miShowOnlyBreakpoints.setSelected(app.getKernel().showOnlyBreakpoints());
				miColorfulConstructionProtocol.setSelected(cpView.getUseColors());
				return true;
			}
		};
		btnOptions.setKeepVisible(true);
		btnOptions.setStandardButton(true);  // mouse clicks over total button region
		btnOptions.setIcon(app.getImageIcon("document-properties.png"));
		
		miShowOnlyBreakpoints = new JCheckBoxMenuItem(app.getPlain("ShowOnlyBreakpoints"));
		miShowOnlyBreakpoints.setSelected(app.getKernel().showOnlyBreakpoints());
		miShowOnlyBreakpoints.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				app.getKernel().setShowOnlyBreakpoints(!app.getKernel().showOnlyBreakpoints());
				cpView.getData().initView();
				cpView.repaint();
			}
		});
		btnOptions.addPopupMenuItem(miShowOnlyBreakpoints);
		
		miColorfulConstructionProtocol = new JCheckBoxMenuItem(app.getPlain("ColorfulConstructionProtocol"));
		miColorfulConstructionProtocol.setSelected(cpView.getUseColors());
		miColorfulConstructionProtocol.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				cpView.setUseColors(!cpView.getUseColors());
			}
		});
		btnOptions.addPopupMenuItem(miColorfulConstructionProtocol);
		add(btnOptions);
	
		addSeparator();
		
		//export button
		
		btnExport = new JButton(app.getImageIcon("text-html.png"));
		btnExport.setToolTipText(app.getPlainTooltip("ExportAsWebpage"));
		btnExport.addActionListener(this);
		add(btnExport);
		
		addSeparator();

		//print button
		btnPrint = new JButton(app.getImageIcon("document-print-preview.png"));
		btnPrint.setToolTipText(app.getPlainTooltip("Print"));
		btnPrint.addActionListener(this);
		add(btnPrint);
		
		addSeparator();
		
		//Help button
		btnHelp = new JButton(app.getImageIcon("help.png"));
		//btnHelp.setToolTipText(app.getPlainTooltip("FastHelp"));
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						app.getGuiManager().openHelp("Construction_Protocol");
					}
				};
				runner.start();
			}
		});
		add(btnHelp);
		
		setLabels();
	}
	
	
	/**
	 * Set the tool tip texts (used for language change, and at initialization labels).
	 */
	public void setLabels() {
		btnColumns.setToolTipText(app.getMenuTooltip("Columns"));
		btnOptions.setToolTipText(app.getMenuTooltip("Options"));
		btnExport.setToolTipText(app.getPlainTooltip("ExportAsWebpage"));
		btnPrint.setToolTipText(app.getMenuTooltip("Print"));
		btnHelp.setToolTipText(app.getMenuTooltip("FastHelp"));
		miShowOnlyBreakpoints.setText(app.getPlain("ShowOnlyBreakpoints"));
		miColorfulConstructionProtocol.setText(app.getPlain("ColorfulConstructionProtocol"));
	}

	/**
	 * React to button presses.
	 */
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==btnColumns){
			int selIndex = btnColumns.getSelectedIndex();
			TableColumn column = cpView.getTableColumns()[selIndex];
			ColumnKeeper colKeeper = cpView.new ColumnKeeper(column, cpView.getData().columns[selIndex]);
			colKeeper.actionPerformed(e);
			
		}
		
		if(e.getSource()==btnOptions){
			if(btnOptions.getSelectedIndex()==0){
				app.getKernel().setShowOnlyBreakpoints(!app.getKernel().showOnlyBreakpoints());
				cpView.getData().initView();
				cpView.repaint();
			}
			else if(btnOptions.getSelectedIndex()==1)
				cpView.setUseColors(!cpView.getUseColors());
		}
		
		if(e.getSource()==btnExport){
			cpView.getExportHtmlAction().actionPerformed(e);
		}
		if(e.getSource()==btnPrint){
			cpView.getPrintPreviewAction().actionPerformed(e);
		}
	}
}
