/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.gui;

import geogebra.euclidian.EuclidianConstants;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.LatexTable;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.gui.util.TableSymbols;
import geogebra.gui.util.TableSymbolsLaTeX;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;
import geogebra.main.GeoGebraColorConstants;
import geogebra.main.MyError;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

/**
 * Input dialog for GeoText objects with additional option
 * to set a "LaTeX formula" flag
 * 
 * @author hohenwarter
 */
public class TextInputDialog extends InputDialog implements DocumentListener {

	private static final long serialVersionUID = 1L;

	// editor and preview panels
	private DynamicTextInputPane editor;
	private TextPreviewPanel textPreviewer;
	private TextInputDialog textInputDialog;

	//GUI
	private JCheckBox cbLaTeX;
	private JToolBar toolBar;
	private JPanel previewPanel, editPanel, toolPanel;
	private PopupMenuButton btInsertLaTeX, btInsertUnicode, btInsertGeo;
	private JLabel previewHeader, editHeader;
	
	// text handling fields
	private GeoText editGeo;
	private boolean isLaTeX;
	private GeoPointND startPoint;
	private boolean isTextMode = false;

	// recent symbol fields
	private SelectionTable recentSymbolTable;
	private ArrayList<String> recentSymbolList;

	// JList for the object menu popup
	private JList geoList;


	boolean isIniting;



	/**
	 * Input Dialog for a GeoText object
	 * @param app 
	 * @param title 
	 * @param editGeo 
	 * @param startPoint 
	 * @param cols 
	 * @param rows 
	 * @param isTextMode 
	 */
	public TextInputDialog(Application app,  String title, GeoText editGeo, GeoPointND startPoint,
			int cols, int rows, boolean isTextMode) {	

		super(app.getFrame(), false);
		this.app = app;
		this.startPoint = startPoint;
		this.isTextMode = isTextMode;
		this.editGeo = editGeo;
		textInputDialog = this;
		inputHandler = new TextInputHandler();

		isIniting = true;

		// build input dialog GUI
		createGUI(title, "", false, cols, rows, false, false, false, false, false, false, true);	
		addHelpButton(Application.WIKI_TEXT_TOOL);
		editor = (DynamicTextInputPane) inputPanel.getTextComponent();
		textPreviewer = new TextPreviewPanel(app.getKernel());

		// build the rest of the GUI
		createAdditionalGUI();

		// init editor with GeoText text
		setGeoText(editGeo);  
		textPreviewer.updatePreviewText(editGeo, editor.buildGeoGebraString(isLaTeX), isLaTeX);
		editor.getDocument().addDocumentListener(this);

		// add key listener to the editor 
		editor.addKeyListener(new MyKeyListener());

		this.setResizable(true);

		isIniting = false;
		setLabels(title);

		this.pack();
	}

	
	/*
	// override because we don't want to set mode to another mode
	public void windowGainedFocus(WindowEvent arg0) {
		if (!isModal()) {
			app.setCurrentSelectionListener(null);
		}
		app.getGuiManager().setCurrentTextfield(this, true);
	}
	
	*/

	
	public void reInitEditor(GeoText text, GeoPointND startPoint) {

		this.startPoint = startPoint;
		setGeoText(text);  
		textPreviewer.updatePreviewText(text, editor.buildGeoGebraString(isLaTeX), isLaTeX);
		editor.requestFocus();
	}


	private void createAdditionalGUI(){

		showSymbolTablePopup(false);

		// create LaTeX checkbox
		cbLaTeX = new JCheckBox();
		cbLaTeX.setSelected(isLaTeX);
		cbLaTeX.addActionListener(this);

		// create insertion buttons
		btInsertUnicode = new PopupMenuButton(app);
		btInsertLaTeX = new PopupMenuButton(app);

		buildInsertLaTeXButton();
		buildInsertUnicodeButton();		
		buildInsertGeoButton();	

		// build toolbar
		toolPanel = new JPanel(new BorderLayout());

		toolBar = new JToolBar();
		toolBar.add(cbLaTeX);
		toolBar.add(btInsertLaTeX);
		toolBar.add(Box.createRigidArea(new Dimension(5,1)));
		toolBar.addSeparator();
		toolBar.add(Box.createRigidArea(new Dimension(5,1)));
		toolBar.add(btInsertUnicode);
		toolBar.add(Box.createRigidArea(new Dimension(5,1)));
		toolBar.addSeparator();
		toolBar.add(Box.createRigidArea(new Dimension(5,1)));
		toolBar.add(btInsertGeo);
		toolBar.setFloatable(false);

		toolPanel.add(toolBar, BorderLayout.NORTH);
		toolPanel.add(createRecentSymbolTable(),BorderLayout.SOUTH);

		// create edit panel to contain both the input panel and toolbar
		editHeader = new JLabel();
		editHeader.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 2));

		editPanel = new JPanel(new BorderLayout(2,2));
		editPanel.add(editHeader, BorderLayout.NORTH);
		editPanel.add(inputPanel, BorderLayout.CENTER);	
		editPanel.add(toolPanel, BorderLayout.SOUTH);	
		editPanel.setBorder(BorderFactory.createEtchedBorder());
		editPanel.setMinimumSize(new Dimension(200, 120));


		// create preview panel	
		previewHeader = new JLabel();
		previewHeader.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		JPanel p = new JPanel(new BorderLayout());
		p.add(textPreviewer, BorderLayout.CENTER);

		previewPanel = new JPanel(new BorderLayout());
		previewPanel.add(previewHeader, BorderLayout.NORTH);	
		previewPanel.add(new JScrollPane(p), BorderLayout.CENTER);		
		previewPanel.setMinimumSize(new Dimension(200, 50));


		// set sizes
		Dimension d = inputPanel.getPreferredSize();
		d.height = 60;  //this.getFont().getSize()*10;
		inputPanel.setPreferredSize(d);
		previewPanel.setPreferredSize(inputPanel.getPreferredSize());
		//textPreviewer.setPreferredSize(editor.getPreferredSize());



		// put the preview and edit panels into a split pane
		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editPanel, previewPanel );
		previewPanel.setPreferredSize(editPanel.getPreferredSize());
		sp.setResizeWeight(0.75);
		sp.setBorder(BorderFactory.createEmptyBorder());

		// put all the sub-panels together
		JPanel centerPanel = new JPanel(new BorderLayout());		
		centerPanel.add(sp, BorderLayout.CENTER);			
		centerPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerOnScreen();

	}




	/** 
	 * Builds unicode insertion button and drop down tables.
	 */
	private void buildInsertUnicodeButton(){

		if(btInsertUnicode != null)
			btInsertUnicode.removeAllMenuItems();

		btInsertUnicode.setKeepVisible(false);
		btInsertUnicode.setStandardButton(true);
		btInsertUnicode.setFixedIcon(GeoGebraIcon.createDownTriangleIcon(10));
		//btInsertUnicode.setText("Symbols");

		JMenu menu = new JMenu(app.getMenu("Properties.Basic"));
		menu.add(new LatexTable(app, this, btInsertUnicode, TableSymbols.basicSymbols(app), -1, 11,SelectionTable.MODE_TEXT));
		btInsertUnicode.addPopupMenuItem(menu);
		//btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.math_ops,0,1,2));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.operators,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.greekLettersPlusVariants(),-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.analysis,-1,8));

		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.sets,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.logical,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.sub_superscripts,-1,10));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.basic_arrows,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.otherArrows,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.geometricShapes,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.games_music,-1,7));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.currency,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.handPointers,-1,6));

	}

	/**
	 * Creates a sub-menu for the unicode insert button.
	 */
	private JMenu createMenuItem(String[] table, int rows, int columns ){
		JMenu menu = new JMenu(table[0] + " " + table[1] + " " + table[2] + "  ");
		menu.add(new LatexTable(app, this, btInsertUnicode, table, rows, columns, SelectionTable.MODE_TEXT));
		return menu;
	}



	/** 
	 * Builds LaTeX insertion button and drop down tables.
	 */
	private void buildInsertLaTeXButton(){

		if(btInsertLaTeX != null){
			btInsertLaTeX.removeAllMenuItems();
		}

		btInsertLaTeX.setKeepVisible(false);
		btInsertLaTeX.setStandardButton(true);
		btInsertLaTeX.setFixedIcon(GeoGebraIcon.createDownTriangleIcon(10));
		btInsertLaTeX.setText("LaTeX");
		btInsertLaTeX.setEnabled(false);

		JMenu menu;
		menu = new JMenu(app.getMenu("RootsAndFractions"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.roots_fractions, 1, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("SumsAndIntegrals"));	
		LatexTable table = new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.sums, 1, -1,SelectionTable.MODE_LATEX);
		//table.setCaretPosition(-3);
		menu.add(table);
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("Accents"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.accents, 2, -1, SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("AccentsExt"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.accentsExtended, 2, -1, SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("Brackets"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.brackets, 2, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("Matrices"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.matrices, 1, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("FrakturLetters"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.mathfrak(), 4, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("CalligraphicLetters"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.mathcal(), 2, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("BlackboardLetters"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.mathbb(), 2, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("CursiveLetters"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.mathscr(), 2, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		JMenuItem menuItem = new JMenuItem(app.getMenu("Space"));
		menuItem.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				insertString(" \\; ");
			}

		});
		btInsertLaTeX.addPopupMenuItem(menuItem);

	}



	/** 
	 * Creates an array of labels of existing geos that can be inserted into the editor as dynamic text
	 */
	private String[] getGeoObjectList(){

		TreeSet<GeoElement> ts = app.getKernel().getConstruction().getGeoSetLabelOrder();
		ArrayList<String> list = new ArrayList<String>(); 
		Iterator<GeoElement> iter = ts.iterator();
		while (iter.hasNext()) {
			GeoElement g = iter.next();
			if (g.isLabelSet() && !g.equals(editGeo)) {
				list.add(g.getLabel());
			}
		}
		String[] geoArray = new String[list.size()];
		geoArray = list.toArray(geoArray);
		return geoArray;
	}


	/** 
	 * Builds GeoElement insertion button. 
	 */
	private void buildInsertGeoButton(){

		// create a JList to hold the geo labels for the object popup menu
		geoList = new JList(getGeoObjectList());
		geoList.setBorder(BorderFactory.createEmptyBorder(0,4,0,4));
		JScrollPane scroller = new JScrollPane(geoList);
		scroller.setBorder(BorderFactory.createEmptyBorder());
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		// add a list selection listener that will insert a selected geo into the editor
		geoList.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()){		
					String label = (String) geoList.getSelectedValue();
					insertGeoElement(app.getKernel().lookupLabel(label));
					btInsertGeo.handlePopupActionEvent();
					geoList.getSelectionModel().clearSelection();
				}
			}

		});

		// create a popup button and add the list to it
		btInsertGeo = new PopupMenuButton(app){
			// update the object list before opening the popup
			public boolean prepareToShowPopup(){
				geoList.setListData(getGeoObjectList());
				int rowCount = Math.min(8,geoList.getModel().getSize());
				geoList.setVisibleRowCount(rowCount);
				return geoList.getModel().getSize() > 0;
			}
		};
		btInsertGeo.addPopupMenuItem(scroller);
		btInsertGeo.setKeepVisible(false);
		btInsertGeo.setStandardButton(true);
		btInsertGeo.setFixedIcon(GeoGebraIcon.createDownTriangleIcon(10));	


	};





	//=============================================================
	//      Recent symbol buttons
	//=============================================================


	public JToolBar createRecentSymbolTable(){


		recentSymbolList = app.getGuiManager().getRecentSymbolList();

		recentSymbolTable = new SelectionTable(app, recentSymbolList.toArray(), 1, recentSymbolList.size(), 
				new Dimension(24,24), SelectionTable.MODE_TEXT);

		recentSymbolTable.setHorizontalAlignment(SwingConstants.CENTER);
		recentSymbolTable.setSelectedIndex(0);
		//	this.setUseColorSwatchBorder(true);
		recentSymbolTable.setShowGrid(true);
		recentSymbolTable.setGridColor(GeoGebraColorConstants.TABLE_GRID_COLOR);
		recentSymbolTable.setBorder(BorderFactory.createLoweredBevelBorder());  
		recentSymbolTable.setShowSelection(false);

		recentSymbolTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				insertString(recentSymbolList.get(recentSymbolTable.getSelectedIndex()), textInputDialog.isLaTeX);
			}
		});


		JToolBar p = new JToolBar();
		p.setFloatable(false);
		//p.add(new JLabel("Recent: "));
		p.add(recentSymbolTable);
		p.setAlignmentX(LEFT_ALIGNMENT);
		p.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
		return p;
	}


	public void addRecentSymbol(String newSymbol){
		if(!recentSymbolList.contains(newSymbol)){
			this.recentSymbolList.add(0,newSymbol);
			this.recentSymbolList.remove(recentSymbolList.size()-1);
			updateRecentSymbolTable();
		}
	}


	public void updateRecentSymbolTable(){
		recentSymbolTable.populateModel(recentSymbolList.toArray());
	}





	//=============================================================
	//      Getters/Setters
	//=============================================================

	public void setLabels() {
		setLabels(app.getPlain("Text"));
	}

	public void setLabels(String title) {

		if(isIniting) return;

		super.setLabels(title);

		if(editHeader != null)
			editHeader.setText(app.getPlain("Edit"));
		if(previewHeader != null)
			previewHeader.setText(app.getMenu("Preview"));

		// rebuild the symbol tables to catch localized symbols
		buildInsertUnicodeButton();
		buildInsertLaTeXButton();

		btInsertLaTeX.setText(app.getPlain("LaTeXFormula"));	
		btInsertUnicode.setText(app.getMenu("Symbols"));
		btInsertGeo.setText(app.getMenu("Objects"));	

	}


	/**
	 * Sets the GeoText element to be edited.
	 * Also sets the editor content to fit the new GeoText string and sets the LaTeX flag.
	 * @param geo
	 *            GeoText element to be edited
	 */
	public void setGeoText(GeoText geo) {

		this.editGeo = geo;
		boolean createText = geo == null;   
		isLaTeX = geo == null ? false: geo.isLaTeX();

		//TODO: not sure if this old code is needed anymore
		if (createText) {
			//initString = " \"\" ";
			initString = null;            
			isLaTeX = false;
		}           
		else {                                			
			initString = "";
			if(geo.isIndependent()){ 
				initString = geo.getTextString();
				if(geo.getKernel().lookupLabel(initString) != null)
					initString = "\"" + initString + "\"";            		 		
			}
			else
				initString = geo.getCommandDescription();            
			isLaTeX = geo.isLaTeX();
		}           
		//----------------------------------------------

		editor.setText(geo, this);
		editor.setCaretPosition(0);
		cbLaTeX.setSelected(false);
		if (isLaTeX) {
			cbLaTeX.doClick();
		}

	}



	/**
	 * @return toolbar with buttons for inserting text symbols and LaTeX formulas
	 */
	public JPanel getToolBar() {
		return toolPanel;
	}

	/**
	 * @return panel with textarea
	 */
	public JPanel getInputPanel() {
		return inputPanel;
	}

	/**
	 * @return preview panel
	 */
	public JPanel getPreviewPanel() {
		return previewPanel;
	}


	/**
	 * @return apply button
	 */
	public JButton getApplyButton() {
		return btApply;
	}


	/**
	 * Returns state of LaTeX Formula checkbox. 
	 * @return true if switched to LaTeX mode
	 */
	public boolean isLaTeX() {
		return cbLaTeX.isSelected();		
	}





	//=============================================================
	//      Event handlers
	//=============================================================


	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
				isLaTeX = cbLaTeX.isSelected();
				boolean finished;
				finished = inputHandler.processInput(editor.buildGeoGebraString(isLaTeX));

				if (isShowing()) {	
					// text dialog window is used and open

					if(isTextMode)// don't clear selected geos don't set mode
						setVisibleForTools(!finished);
					else
						setVisible(!finished);

					//if(isTextMode)
					//	app.setMode(EuclidianConstants.MODE_TEXT);
				}
				if (finished)
					app.setMode(EuclidianConstants.MODE_MOVE);
			} 

			else if (source == btCancel) {
				if (isShowing())
					setVisible(false);		
				else {
					setGeoText(editGeo);
				}
				if(isTextMode)
					app.setMode(EuclidianConstants.MODE_TEXT);
			}

			else if (source == cbLaTeX) {

				btInsertLaTeX.setEnabled(cbLaTeX.isSelected());
				isLaTeX = cbLaTeX.isSelected();
				textPreviewer.updatePreviewText(editGeo, editor.buildGeoGebraString(isLaTeX), isLaTeX);


				if(isLaTeX && inputPanel.getText().length() == 0) {
					insertString("$  $");
					setRelativeCaretPosition(-2);
				}

			}	

		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			ex.printStackTrace();
		}			
	}


	public void setVisible(boolean isVisible) {	
		if(!isVisible ){
			if(textPreviewer != null){
				textPreviewer.removePreviewGeoText();
			}
		}
		super.setVisible(isVisible);
	}

	private class MyKeyListener extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			if((e.isControlDown()||Application.isControlDown(e)) && e.getKeyCode() == KeyEvent.VK_SPACE){
				if(isLaTeX)
					inputPanel.insertString("\\:");
			}
		}
	}



	//=============================================================
	//      Document listener
	//=============================================================

	public void changedUpdate(DocumentEvent e) { }

	/**
	 * Called when an insertion is made in the textarea
	 * @param e the event
	 */
	public void insertUpdate(DocumentEvent e) {
		handleDocumentEvent(e);
	}

	/**
	 * Called when a remove is made in the textarea
	 * @param e the event
	 */
	public void removeUpdate(DocumentEvent e) {
		handleDocumentEvent(e);
	}

	protected void handleDocumentEvent(DocumentEvent e) {

		textPreviewer.updatePreviewText(editGeo, editor.buildGeoGebraString(isLaTeX), isLaTeX);
	}


	public void insertGeoElement(GeoElement geo) {

		if (geo == null) return;

		Document d = editor.insertDynamicText(geo.getLabel(), this);
		d.addDocumentListener(this);
		return;
	}






	//=============================================================
	//      TextInputHandler
	//=============================================================

	/**
	 * Handles creating or redefining GeoText using the current editor string.
	 *
	 */
	private class TextInputHandler implements InputHandler {

		private Kernel kernel;		

		private TextInputHandler() { 
			kernel = app.getKernel();        	
		}        

		public boolean processInput(String inputValue) {
			if (inputValue == null) return false;                        

			// no quotes?
			if (inputValue.indexOf('"') < 0) {
				// this should become either
				// (1) a + "" where a is an object label or
				// (2) "text", a plain text 

				// ad (1) OBJECT LABEL 
				// add empty string to end to make sure
				// that this will become a text object
				if (kernel.lookupLabel(inputValue.trim()) != null) {
					inputValue = "(" + inputValue + ") + \"\"";
				} 
				// ad (2) PLAIN TEXT
				// add quotes to string
				else {
					inputValue = "\"" + inputValue + "\"";
				}        			
			} 
			else {
				// replace \n\" by \"\n, this is useful for e.g.:
				//    "a = " + a + 
				//	"b = " + b 
				inputValue = inputValue.replaceAll("\n\"", "\"\n");
			}

			if (inputValue.equals("\"\"")) return false;

			// create new text
			boolean createText = editGeo == null;
			if (createText) {
				GeoElement [] ret = 
					kernel.getAlgebraProcessor().processAlgebraCommand(inputValue, false);
				if (ret != null && ret[0].isTextValue()) {
					GeoText t = (GeoText) ret[0];
					t.setLaTeX(isLaTeX, true);  

					// make sure for new LaTeX texts we get nice "x"s
					if (isLaTeX) t.setSerifFont(true);

					if (startPoint.isLabelSet()) {
						try { t.setStartPoint(startPoint); }catch(Exception e){};                          
					} else {

						//                    	// Michael Borcherds 2008-04-27 changed to RealWorld not absolute
						// startpoint contains mouse coords
						//t.setAbsoluteScreenLoc(euclidianView.toScreenCoordX(startPoint.inhomX), 
						//		euclidianView.toScreenCoordY(startPoint.inhomY));
						//t.setAbsoluteScreenLocActive(true); 
						Coords coords = startPoint.getInhomCoordsInD(3);
						t.setRealWorldLoc(coords.getX(),coords.getY());
						t.setAbsoluteScreenLocActive(false); 
					}

					// make sure (only) the output of the text tool is selected
					kernel.getApplication().getActiveEuclidianView().getEuclidianController().memorizeJustCreatedGeos(ret);

					t.updateRepaint();
					app.storeUndoInfo();
					return true;
				}
				return false;
			}

			// change existing text
			try {           
				GeoText newText = (GeoText) kernel.getAlgebraProcessor().changeGeoElement(editGeo, inputValue, true, true);                         

				// make sure newText is using correct LaTeX setting
				newText.setLaTeX(isLaTeX, true);
				newText.updateRepaint();

				app.doAfterRedefine(newText);                                
				return newText != null;
			} catch (Exception e) {
				app.showError("ReplaceFailed");
				return false;
			} catch (MyError err) {
				app.showError(err);
				return false;
			} 
		}   
	}





}

