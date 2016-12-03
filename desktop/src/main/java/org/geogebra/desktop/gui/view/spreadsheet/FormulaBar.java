package org.geogebra.desktop.gui.view.spreadsheet;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.gui.inputfield.KeyNavigation;
import org.geogebra.desktop.gui.layout.LayoutD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

public class FormulaBar extends JToolBar implements ActionListener, FocusListener{
	private static final long serialVersionUID = 1L;
	private AppD app;
	private SpreadsheetViewD view;
	private MyTableD table;

	private JButton btnCancelFormula;
	private JButton btnAcceptFormula;
	private AutoCompleteTextFieldD fldFormula;
	private AutoCompleteTextFieldD fldCellName;
	private boolean isIniting;

	private MyCellEditorSpreadsheet editor;

	private int row, column;




	public FormulaBar(AppD app, SpreadsheetViewD view){

		this.app = app;
		this.view = view;
		this.table = (MyTableD) view.getSpreadsheetTable();

		this.editor = table.editor;

		// create GUI objects

		btnCancelFormula = new JButton(
				app.getScaledIcon(GuiResourcesD.DELETE_SMALL));
		btnCancelFormula.setFocusable(false);
		btnCancelFormula.addActionListener(this);
		btnCancelFormula.setBorder(BorderFactory.createEmptyBorder(0,4,0,4));
		btnCancelFormula.addMouseListener(new BarButtonListener());

		btnAcceptFormula = new JButton(app.getScaledIcon(GuiResourcesD.APPLY));
		btnAcceptFormula.addMouseListener(new BarButtonListener());
		btnAcceptFormula.setFocusable(false);
		btnAcceptFormula.setBorder(BorderFactory.createEmptyBorder(0,4,0,4));	

		fldFormula = new AutoCompleteTextFieldD(-1, app, KeyNavigation.IGNORE);
		fldFormula.addActionListener(this);
		fldFormula.addFocusListener(this);
		fldFormula.setShowSymbolTableIcon(true);
		fldCellName = new AutoCompleteTextFieldD(5, app, KeyNavigation.IGNORE);
		fldCellName.setAutoComplete(false);
		//fldCellName.setEditable(false);
		Dimension d = fldCellName.getMaximumSize();
		d.width = fldCellName.getPreferredSize().width;
		fldCellName.setMaximumSize(d);
		fldCellName.addActionListener(this);


		add(fldCellName);
		add(btnCancelFormula);
		add(btnAcceptFormula);
		add(fldFormula);
		setFloatable(false);

		// add document listener to enable updating of the spreadsheet cell editot 
		fldFormula.getDocument().addDocumentListener(documentListener);

		// add an instance of the spreadsheet cell editor listener
		// to enable auto-update while dragging cell ranges during editing
		fldFormula.addKeyListener(editor.new SpreadsheetCellEditorKeyListener(true));
		
		// prevent the focus system from stealing TAB; this allows tabbing through auto-complete cycles
		fldFormula.setFocusTraversalKeysEnabled(false);
		
		setLabels();
	}



	private DocumentListener documentListener = new DocumentListener() {
		public void changedUpdate(DocumentEvent documentEvent) {
			// do nothing
		}
		public void insertUpdate(DocumentEvent documentEvent) {
			updateCellEditor(documentEvent);
		}
		public void removeUpdate(DocumentEvent documentEvent) {
			updateCellEditor(documentEvent);
		}
		private void updateCellEditor(DocumentEvent documentEvent) {

			view.getSpreadsheetTable().updateEditor(fldFormula.getText());
		}
	};



	public void setEditorText(String text){
		if(!fldFormula.hasFocus() || table.isDragging2)
			fldFormula.setText(text);
	}



	public void update(){

		//Application.debug("formula bar update");

		if(table.isSelectNone()){
			fldCellName.setText("");
			fldFormula.setText("");
			//Application.debug("nothing selected");
			return;
		}

		row = table.minSelectionRow;
		column = table.minSelectionColumn;

		String cellName = GeoElementSpreadsheet.getSpreadsheetCellName(column, row);
		fldCellName.removeActionListener(this);
		fldCellName.setText(cellName);
		fldCellName.addActionListener(this);


		String cellContents = "";
		GeoElement cellGeo = RelativeCopy.getValue(app, column, row);
		if(cellGeo != null) {
			//Application.debug("cell with geo selected at: " + row + " ," + column);
			cellContents = cellGeo.getRedefineString(true, false);
			int index = cellContents.indexOf("=");
			if ((!cellGeo.isGeoText())) {
				if (index == -1) {
					cellContents = "=" + cellContents;
				}
			}
		}else{
			//Application.debug("empty cell selected at: " + row + " ," + column);
		}

		fldFormula.removeActionListener(this);
		fldFormula.setText(cellContents);
		fldFormula.addActionListener(this);
	}




	public void focusGained(FocusEvent e) {

		// make sure the spreadsheet gets the view focus in case first click is here
		if(!view.hasViewFocus())
			((LayoutD) app.getGuiManager().getLayout()).getDockManager().setFocusedPanel(App.VIEW_SPREADSHEET);

		// select the upper left corner cell if nothing is selected
		if (table.isSelectNone() || table.getSelectedRow() < 0
				|| table.getSelectedColumn() < 0) {
			table.setSelection(0, 0);
			update();
		}
		
		// start cell editing in the currently selected cell
		// TODO: should these be an anchor cell?
		table.setAllowEditing(true);
		view.getSpreadsheetTable().editCellAt(table.getSelectedRow(), table.getSelectedColumn());
		view.getSpreadsheetTable().repaint();

	}



	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}


	public void actionPerformed(ActionEvent e) {
		if(isIniting) return;

		Object source = e.getSource();	

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField)source);
		}	
	}

	private void doTextFieldActionPerformed(JTextField source) {
		if(isIniting) return;

		String inputText = source.getText().trim();
		if (source == fldCellName) {
			if(table.setSelection(inputText))
				update();
		}

	}


	private class BarButtonListener extends MouseAdapter{
		@Override
		public void mouseClicked(MouseEvent event){
			Object source = event.getSource();	

			if(source == btnCancelFormula){
				Robot robot;
				try {
					robot = new Robot();
					robot.keyPress(KeyEvent.VK_ESCAPE);
				} catch (AWTException e) {
					e.printStackTrace();
				}
			} else if(source == btnAcceptFormula) {
				if(fldFormula.hasFocus()) {
					editor.stopCellEditing(0, 1);
				}
			}
		}
	}

	public void updateFonts(Font font){
		fldFormula.setFont(font);
		fldCellName.setFont(font);
		updateIcons();
		repaint();

	}

	public void updateIcons() {
		btnCancelFormula.setIcon(app.getScaledIcon(GuiResourcesD.DELETE_SMALL));
		btnAcceptFormula.setIcon(app.getScaledIcon(GuiResourcesD.APPLY));
	}

	public void setLabels(){
		Localization loc = app.getLocalization();
		btnAcceptFormula.setToolTipText(loc.getPlain("Apply"));
		btnCancelFormula.setToolTipText(loc.getPlain("Cancel"));

	}

	public boolean editorHasFocus(){
		return fldFormula.hasFocus();
	}

}
