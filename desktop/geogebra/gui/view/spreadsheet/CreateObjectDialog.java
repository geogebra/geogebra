package geogebra.gui.view.spreadsheet;

import geogebra.common.gui.view.spreadsheet.CellRange;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoPolyLine;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.gui.dialog.InputDialog;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.view.algebra.InputPanelD.DialogType;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Dialog to create GeoElements (lists, matrices, tabletext, etc.) from
 * spreadsheet cell selections
 * 
 * @author G. Sturr
 * 
 */
public class CreateObjectDialog extends InputDialog implements
		ListSelectionListener, FocusListener {

	private static final long serialVersionUID = 1L;
	
	private SpreadsheetView view;
	private CellRangeProcessor cp;
	private ArrayList<CellRange> selectedCellRanges;
	private int selectionType;
	private MyTableD table;

	public static final int TYPE_LIST = 0;
	public static final int TYPE_MATRIX = 1;
	public static final int TYPE_LISTOFPOINTS = 2;
	public static final int TYPE_TABLETEXT = 3;
	public static final int TYPE_POLYLINE = 4;
	private int objectType = TYPE_LIST;

	private JList typeList;
	private DefaultListModel model;
	private JLabel lblObject, lblType, lblName, lblTake, lblOrder, lblXYOrder;

	private JCheckBox ckSort, ckTranspose;
	private JRadioButton btnValue, btnObject;
	private JRadioButton rbOrderNone, rbOrderRow, rbOrderCol, rbOrderSortAZ,
			rbOrderSortZA;
	private JComboBox cbScanOrder, cbTake;

	private boolean isIniting = true;
	private JPanel optionsPanel;
	private JPanel typePanel;

	private MyTextField fldName;

	private GeoElement newGeo;

	private JScrollPane previewPanel;
	private JTextField fldType;

	private String title = null;

	private boolean keepNewGeo = false;
	private JComboBox cbLeftRightOrder;
	private JPanel cards;
	private JLabel lblPreview;
	private JLabel lblPreviewHeader;
	private JPanel namePanel;

	public CreateObjectDialog(AppD app, SpreadsheetView view,
			int objectType) {

		super(app.getFrame(), false);
		this.app = app;
		this.view = view;
		this.objectType = objectType;
		this.table = (MyTableD) view.getTable();
		cp = table.getCellRangeProcessor();
		selectionType = table.getSelectionType();
		selectedCellRanges = table.selectedCellRanges;

		boolean showApply = false;

		createGUI(title, "", false, 16, 1, false, false, false,
				showApply, DialogType.GeoGebraEditor);

		// this.btCancel.setVisible(false);

		createAdditionalGUI();

		updateGUI();

		isIniting = false;
		setLabels(null);
		// setTitle((String) model.getElementAt(objectType));

		// optionPane.add(inputPanel, BorderLayout.CENTER);
		typeList.setSelectedIndex(objectType);

		setResizable(true);
		centerOnScreen();
		btCancel.requestFocus();
		pack();
		addWindowFocusListener(this);
	}

	@SuppressWarnings("rawtypes")
	private void createAdditionalGUI() {

		model = new DefaultListModel();
		typeList = new JList(model);
		typeList.addListSelectionListener(this);

		lblName = new JLabel();
		fldName = new MyTextField(app);
		fldName.setShowSymbolTableIcon(true);
		fldName.addFocusListener(this);

		lblTake = new JLabel();
		cbScanOrder = new JComboBox();
		cbScanOrder.addActionListener(this);
		lblOrder = new JLabel();
		cbTake = new JComboBox();

		cbLeftRightOrder = new JComboBox();
		cbLeftRightOrder.addActionListener(this);

		btnObject = new JRadioButton();
		btnValue = new JRadioButton();
		btnObject.setSelected(true);
		ButtonGroup bg = new ButtonGroup();
		bg.add(btnObject);
		bg.add(btnValue);

		lblXYOrder = new JLabel();

		ckSort = new JCheckBox();
		ckSort.setSelected(false);

		ckTranspose = new JCheckBox();
		ckTranspose.setSelected(false);
		ckTranspose.addActionListener(this);

		// show the object list only if an object type is not given

		lblObject = new JLabel();

		if (objectType < 0) {
			objectType = TYPE_LIST;
			typePanel = new JPanel(new BorderLayout());
			typePanel.add(lblObject, BorderLayout.NORTH);
			typePanel.add(typeList, BorderLayout.WEST);
			typeList.setBorder(BorderFactory.createEmptyBorder(6, 2, 2, 2));
			typePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			optionPane.add(typePanel, BorderLayout.WEST);
		}

		namePanel = new JPanel(new BorderLayout());
		// namePanel.add(lblName, BorderLayout.WEST);
		namePanel.add(fldName, BorderLayout.CENTER);

		buildOptionsPanel();
		JPanel p = new JPanel(new BorderLayout());
		p.add(namePanel, BorderLayout.NORTH);
		p.add(optionsPanel, BorderLayout.SOUTH);

		lblPreview = new JLabel();
		lblPreview.setBorder(BorderFactory.createEtchedBorder());
		lblPreview.setHorizontalAlignment(SwingConstants.CENTER);
		previewPanel = new JScrollPane(lblPreview);
		previewPanel.setBackground(this.getBackground());

		JPanel op = new JPanel(new BorderLayout());
		op.add(p, BorderLayout.WEST);
		op.add(previewPanel, BorderLayout.CENTER);

		previewPanel.setPreferredSize(new Dimension(200,
				p.getPreferredSize().height));

		optionPane.add(op, BorderLayout.CENTER);

	}

	private void buildOptionsPanel() {

		JPanel copyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		copyPanel.add(btnObject);
		copyPanel.add(btnValue);
		// copyPanel.add(cbTake);

		JPanel northPanel = new JPanel(new BorderLayout());
		// northPanel.add(namePanel,BorderLayout.NORTH);
		// northPanel.add(Box.createRigidArea(new Dimension(50,10)),
		// BorderLayout.WEST);
		northPanel.add(copyPanel, BorderLayout.CENTER);

		JPanel orderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		orderPanel.add(cbScanOrder);

		JPanel transposePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		transposePanel.add(ckTranspose);

		JPanel xySwitchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		xySwitchPanel.add(cbLeftRightOrder);

		JPanel pointListPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pointListPanel.add(Box.createRigidArea(lblName.getSize()));

		// TODO: this is not a good way to manage visibility of option panels
		// ..fix it if we need more options in the future
		cards = new JPanel(new CardLayout());
		cards.add("c0", orderPanel);
		cards.add("c1", transposePanel);
		cards.add("c2", xySwitchPanel);
		cards.add("c3", transposePanel);

		optionsPanel = new JPanel(new BorderLayout());
		optionsPanel.add(northPanel, BorderLayout.NORTH);
		// optionsPanel.add(Box.createRigidArea(new Dimension(50,10)),
		// BorderLayout.WEST);
		optionsPanel.add(cards, BorderLayout.CENTER);

		// lblPreviewHeader = new JLabel();
		// optionsPanel.add(lblPreviewHeader, BorderLayout.SOUTH);

	}

	@Override
	public void setLabels(String title) {

		if (isIniting)
			return;

		// TODO: using buttons incorrectly for now
		// btnOK = cancel, cancel = create
		btOK.setText(app.getPlain("Cancel"));
		btApply.setText(app.getPlain("Apply"));
		btCancel.setText(app.getMenu("Create"));

		// object/value checkboxes
		btnObject.setText(app.getPlain("DependentObjects"));
		btnObject.addActionListener(this);
		btnValue.setText(app.getPlain("FreeObjects"));
		btnValue.addActionListener(this);

		// transpose checkbox
		ckTranspose.setText(app.getMenu("Transpose"));
		ckSort.setText(app.getMenu("Sort"));
		ckSort.addActionListener(this);

		lblName.setText(app.getMenu("Name") + ": ");

		/*
		 * lblTake.setText(app.getMenu("Take") + ": ");
		 * lblOrder.setText(app.getMenu("Order") + ":");
		 * lblXYOrder.setText(app.getMenu("Order") + ": ");
		 */

		cbScanOrder.removeAllItems();
		cbScanOrder.addItem(app.getMenu("RowOrder"));
		cbScanOrder.addItem(app.getMenu("ColumnOrder"));

		cbLeftRightOrder.removeAllItems();
		cbLeftRightOrder.addItem(app.getMenu("X->Y"));
		cbLeftRightOrder.addItem(app.getMenu("Y<-X"));

		model.clear();
		model.addElement(app.getMenu("List"));
		model.addElement(app.getMenu("Matrix"));
		model.addElement(app.getMenu("ListOfPoints"));
		model.addElement(app.getMenu("Table"));
		model.addElement(app.getMenu("PolyLine"));

		lblObject.setText(app.getMenu("Object") + ":");

		// lblPreviewHeader.setText(app.getMenu("Preview")+ ":");

		namePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(app.getMenu("Name")),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		previewPanel.setBorder(BorderFactory.createTitledBorder(app
				.getMenu("Preview")));

		optionsPanel.setBorder(BorderFactory.createTitledBorder(app
				.getMenu("Options")));
		// if(typePanel!=null)
		// typePanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Object")));

		String titleText = "";
		switch (objectType) {
		case TYPE_LIST:
			titleText = app.getMenu("CreateList");
			break;

		case TYPE_LISTOFPOINTS:
			titleText = app.getMenu("CreateListOfPoints");
			break;

		case TYPE_TABLETEXT:
			titleText = app.getMenu("CreateTable");
			break;

		case TYPE_POLYLINE:
			titleText = app.getMenu("CreatePolyLine");
			break;

		case TYPE_MATRIX:
			titleText = app.getMenu("CreateMatrix");
			break;
		}
		setTitle(titleText);

	}

	private void updateGUI() {

		if (newGeo == null)
			fldName.setText("");
		else
			fldName.setText(newGeo.getLabel(StringTemplate.defaultTemplate));

		CardLayout cl = (CardLayout) (cards.getLayout());
		cl.show(cards, "c" + typeList.getSelectedIndex());

		/*
		 * cbOrder.removeAllItems(); if(objectType == TYPE_LIST){
		 * cbOrder.addItem(app.getMenu("Row"));
		 * cbOrder.addItem(app.getMenu("Column")); }else{
		 * cbOrder.addItem(app.getMenu("X to Y"));
		 * cbOrder.addItem(app.getMenu("Y to X")); }
		 * 
		 * if(objectType == TYPE_MATRIX){ cbOrder.setVisible(false);
		 * ckTranspose.setVisible(true); }else{ cbOrder.setVisible(true);
		 * ckTranspose.setVisible(false); }
		 */
		ckSort.setVisible(objectType == TYPE_POLYLINE);

	}

	/**
	 * Handles button clicks for dialog.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		try {
			btnValue.removeActionListener(this);
			btnObject.removeActionListener(this);

			if (source instanceof JTextField) {
				doTextFieldActionPerformed((JTextField) source);
			}

			// btCancel acts as create for now
			else if (source == btCancel) {

				keepNewGeo = true;
				setVisible(false);

			} else if (source == btApply) {
				// processInput();

				// btOK acts as cancel for now
			} else if (source == btOK) {
				newGeo.remove();
				setVisible(false);
			}

			else if (source == btnObject) {
				btnValue.setSelected(!btnObject.isSelected());
				createNewGeo();
			} else if (source == btnValue) {
				btnObject.setSelected(!btnValue.isSelected());
				createNewGeo();
			}

			else if (source == cbScanOrder || source == cbLeftRightOrder
					|| source == ckTranspose) {
				createNewGeo();
			}

			btnValue.addActionListener(this);
			btnObject.addActionListener(this);

		} catch (Exception ex) {
			// do nothing on uninitializedValue
			setVisible(false);
		}
	}

	private void doTextFieldActionPerformed(JTextField source) {

		if (source == fldName) {
			createNewGeo();
		}
	}

	@Override
	public void setVisible(boolean isVisible) {
		if (!isModal()) {
			if (isVisible) { // set old mode again
				addWindowFocusListener(this);
			} else {
				removeWindowFocusListener(this);
				app.setSelectionListenerMode(null);
			}
		}

		// clean up on exit: either remove our geo or keep it and make it
		// visible
		if (!isVisible) {
			if (keepNewGeo) {
				addNewGeoToConstruction();
			} else {
				newGeo.remove();
			}
		}
		super.setVisible(isVisible);
	}

	private void closeDialog() {
		// either remove our geo or keep it and make it visible
		if (keepNewGeo) {
			addNewGeoToConstruction();
		} else {
			newGeo.remove();
		}
		setVisible(false);
	}

	private void addNewGeoToConstruction() {

		if (objectType == TYPE_LISTOFPOINTS || objectType == TYPE_POLYLINE) {
			app.getKernel().getConstruction()
					.addToConstructionList(newGeo.getParentAlgorithm(), true);
		}

		newGeo.setEuclidianVisible(true);
		if (!newGeo.isGeoText())
			newGeo.setAuxiliaryObject(false);

		if (objectType == TYPE_LISTOFPOINTS) {
			GeoList gl = (GeoList) newGeo;
			for (int i = 0; i < gl.size(); i++) {
				gl.get(i).setEuclidianVisible(true);
				gl.get(i).setAuxiliaryObject(false);
			}
		}

		if (objectType == TYPE_POLYLINE) {
			GeoPoint[] pts = ((AlgoPolyLine) newGeo.getParentAlgorithm())
					.getPoints();
			for (int i = 0; i < pts.length; i++) {
				pts[i].setEuclidianVisible(true);
				pts[i].setAuxiliaryObject(false);
			}
		}

		newGeo.update();
		app.storeUndoInfo();
	}

	private void createNewGeo() {

		boolean nullGeo = newGeo == null;

		if (!nullGeo) {
			if (objectType == TYPE_LISTOFPOINTS) {
				GeoList gl = (GeoList) newGeo;
				for (int i = 0; i < gl.size(); i++)
					gl.get(i).remove();
			}

			if (objectType == TYPE_POLYLINE) {
				GeoPoint[] pts = ((AlgoPolyLine) newGeo.getParentAlgorithm())
						.getPoints();
				for (int i = 0; i < pts.length; i++)
					pts[i].remove();
			}
			newGeo.remove();
		}

		int column1 = table.selectedCellRanges.get(0).getMinColumn();
		int column2 = table.selectedCellRanges.get(0).getMaxColumn();
		int row1 = table.selectedCellRanges.get(0).getMinRow();
		int row2 = table.selectedCellRanges.get(0).getMaxRow();

		boolean copyByValue = btnValue.isSelected();
		boolean scanByColumn = cbScanOrder.getSelectedIndex() == 1;
		boolean leftToRight = cbLeftRightOrder.getSelectedIndex() == 0;
		boolean transpose = ckTranspose.isSelected();
		boolean doCreateFreePoints = true;
		boolean doStoreUndo = true;
		boolean isSorted = false;

		try {
			switch (objectType) {

			case TYPE_LIST:
				newGeo = cp.createList(selectedCellRanges, scanByColumn,
						copyByValue);
				break;

			case TYPE_LISTOFPOINTS:
				newGeo = cp.createPointGeoList(selectedCellRanges, copyByValue,
						leftToRight, isSorted, doStoreUndo, doCreateFreePoints);
				newGeo.setLabel(null);
				for (int i = 0; i < ((GeoList) newGeo).size(); i++) {
					((GeoList) newGeo).get(i).setAuxiliaryObject(true);
					((GeoList) newGeo).get(i).setEuclidianVisible(false);
				}
				newGeo.updateRepaint();
				break;

			case TYPE_MATRIX:
				newGeo = cp.createMatrix(column1, column2, row1, row2,
						copyByValue, transpose);
				break;

			case TYPE_TABLETEXT:
				newGeo = cp.createTableText(column1, column2, row1, row2,
						copyByValue, transpose);
				break;

			case TYPE_POLYLINE:
				newGeo = cp.createPolyLine(selectedCellRanges, copyByValue,
						leftToRight);
				newGeo.setLabel(null);
				GeoPoint[] pts = ((AlgoPolyLine) newGeo.getParentAlgorithm())
						.getPoints();
				for (int i = 0; i < pts.length; i++) {
					pts[i].setAuxiliaryObject(true);
					pts[i].setEuclidianVisible(false);
				}
				newGeo.updateRepaint();
				break;

			}

			ImageIcon latexIcon = new ImageIcon();
			// String latexStr = newGeo.getLaTeXAlgebraDescription(true);

			String latexStr = newGeo.getFormulaString(
					StringTemplate.latexTemplate, true);

			// System.out.println(latexStr);

			Font latexFont = new Font(app.getPlainFont().getName(), app
					.getPlainFont().getStyle(),
					app.getPlainFont().getSize() - 1);

			if (latexStr != null && newGeo.isLaTeXDrawableGeo(latexStr)) {
				app.getDrawEquation().drawLatexImageIcon(app, latexIcon, latexStr, latexFont, false,
						Color.black, null);
				lblPreview.setText(" ");
			} else {
				lblPreview
						.setText(newGeo
								.getAlgebraDescriptionTextOrHTMLDefault());
			}
			lblPreview.setIcon(latexIcon);

			if (!nullGeo) {
				newGeo.setLabel(fldName.getText());
				newGeo.setAuxiliaryObject(true);
				newGeo.setEuclidianVisible(false);
			}

			updateGUI();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void valueChanged(ListSelectionEvent e) {

		if (e.getSource() == typeList) {
			typeList.removeListSelectionListener(this);
			objectType = typeList.getSelectedIndex();
			// fldName.setText("");
			createNewGeo();
			typeList.addListSelectionListener(this);
		}
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		// close the window and set the geo when focus is lost
		if (isVisible())
			setVisible(false);
	}

	public void focusGained(FocusEvent e) {
	}

	public void focusLost(FocusEvent e) {
		doTextFieldActionPerformed((JTextField) (e.getSource()));
	}

}
