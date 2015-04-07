package org.geogebra.desktop.gui.view.spreadsheet;

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

import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.common.gui.view.spreadsheet.CreateObjectModel;
import org.geogebra.common.gui.view.spreadsheet.CreateObjectModel.ICreateObjectListener;
import org.geogebra.desktop.gui.dialog.InputDialogD;
import org.geogebra.desktop.gui.inputfield.MyTextField;
import org.geogebra.desktop.main.AppD;

/**
 * Dialog to create GeoElements (lists, matrices, tabletext, etc.) from
 * spreadsheet cell selections
 * 
 * @author G. Sturr
 * 
 */
@SuppressWarnings({ "javadoc", "rawtypes" })
public class CreateObjectDialog extends InputDialogD implements
		ListSelectionListener, FocusListener, ICreateObjectListener {

	private MyTableD table;
	private CreateObjectModel coModel;
	private JLabel lblObject, lblName;

	private JCheckBox ckSort, ckTranspose;
	private JRadioButton btnValue, btnObject;
	private JComboBox cbScanOrder;

	private boolean isIniting = true;
	private JPanel optionsPanel;
	private JPanel typePanel;

	private MyTextField fldName;

	private JScrollPane previewPanel;

	private JComboBox cbLeftRightOrder;
	private JPanel cards;
	private JLabel lblPreview;
	private JPanel namePanel;
	private DefaultListModel model;
	private JList typeList;
	boolean showApply = false;

	public CreateObjectDialog(AppD app, SpreadsheetView view, int objectType) {

		super(app.getFrame(), false, app.getLocalization());
		this.table = (MyTableD) view.getSpreadsheetTable();
		coModel = new CreateObjectModel(app, view, objectType, this);
		coModel.setCellRangeProcessor(table.getCellRangeProcessor());
		coModel.setSelectedCellRanges(table.selectedCellRanges);
		this.app = app;
		// cp = table.getCellRangeProcessor();
		// selectedCellRanges = table.selectedCellRanges;
		//
		// boolean showApply = false;
		//
		createGUI(coModel.getTitle(), "", false, 16, 1, false, false, false,
				showApply, DialogType.GeoGebraEditor);

		// this.btCancel.setVisible(false);

		createAdditionalGUI();

		updateGUI();

		isIniting = false;
		setLabels(null);
		// setTitle((String) model.getElementAt(objectType));

		// optionPane.add(inputPanel, BorderLayout.CENTER);
		typeList.setSelectedIndex(objectType);

		wrappedDialog.setResizable(true);
		centerOnScreen();
		btCancel.requestFocus();
		wrappedDialog.pack();
		wrappedDialog.addWindowFocusListener(this);
	}

	@SuppressWarnings("unchecked")
	private void createAdditionalGUI() {

		model = new DefaultListModel();
		typeList = new JList(model);
		typeList.addListSelectionListener(this);

		lblName = new JLabel();
		fldName = new MyTextField(app);
		fldName.setShowSymbolTableIcon(true);
		fldName.addFocusListener(this);

		cbScanOrder = new JComboBox();
		cbScanOrder.addActionListener(this);

		cbLeftRightOrder = new JComboBox();
		cbLeftRightOrder.addActionListener(this);

		btnObject = new JRadioButton();
		btnValue = new JRadioButton();
		btnObject.setSelected(true);
		ButtonGroup bg = new ButtonGroup();
		bg.add(btnObject);
		bg.add(btnValue);

		ckSort = new JCheckBox();
		ckSort.setSelected(false);

		ckTranspose = new JCheckBox();
		ckTranspose.setSelected(false);
		ckTranspose.addActionListener(this);

		// show the object list only if an object type is not given

		lblObject = new JLabel();

		if (coModel.getObjectType() < 0) {
			coModel.setListType();
			typePanel = new JPanel(new BorderLayout());
			typePanel.add(lblObject, BorderLayout.NORTH);
			typePanel.add(typeList, loc.borderWest());
			typeList.setBorder(BorderFactory.createEmptyBorder(6, 2, 2, 2));
			typePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			optionPane.add(typePanel, loc.borderWest());
		}

		namePanel = new JPanel(new BorderLayout());
		// namePanel.add(lblName, app.borderWest());
		namePanel.add(fldName, BorderLayout.CENTER);

		buildOptionsPanel();
		JPanel p = new JPanel(new BorderLayout());
		p.add(namePanel, BorderLayout.NORTH);
		p.add(optionsPanel, BorderLayout.SOUTH);

		lblPreview = new JLabel();
		lblPreview.setBorder(BorderFactory.createEtchedBorder());
		lblPreview.setHorizontalAlignment(SwingConstants.CENTER);
		previewPanel = new JScrollPane(lblPreview);
		previewPanel.setBackground(this.wrappedDialog.getBackground());

		JPanel op = new JPanel(new BorderLayout());
		op.add(p, loc.borderWest());
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
		// app.borderWest());
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
		cards.add("c" + CreateObjectModel.OPTION_ORDER, orderPanel);
		cards.add("c" + CreateObjectModel.OPTION_XY, xySwitchPanel);
		cards.add("c" + CreateObjectModel.OPTION_TRANSPOSE, transposePanel);

		optionsPanel = new JPanel(new BorderLayout());
		optionsPanel.add(northPanel, BorderLayout.NORTH);
		// optionsPanel.add(Box.createRigidArea(new Dimension(50,10)),
		// app.borderWest());
		optionsPanel.add(cards, BorderLayout.CENTER);

		// lblPreviewHeader = new JLabel();
		// optionsPanel.add(lblPreviewHeader, BorderLayout.SOUTH);

	}

	@SuppressWarnings("unchecked")
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

		lblName.setText(app.getPlain("Name") + ": ");

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
		for (String item : coModel.getObjectTypeNames()) {
			model.addElement(item);
		}

		lblObject.setText(app.getMenu("Object") + ":");

		// lblPreviewHeader.setText(app.getMenu("Preview")+ ":");

		namePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(app.getPlain("Name")),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		previewPanel.setBorder(BorderFactory.createTitledBorder(app
				.getMenu("Preview")));

		optionsPanel.setBorder(BorderFactory.createTitledBorder(app
				.getMenu("Options")));

		wrappedDialog.setTitle(coModel.getTitle());

	}

	private void updateGUI() {
		coModel.update();

		int idx = coModel.getOptionType();

		CardLayout cl = (CardLayout) (cards.getLayout());
		cl.show(cards, "c" + idx);


	}

	public void updatePreview(String latexStr, boolean isLatexDrawable) {
		ImageIcon latexIcon = new ImageIcon();
		Font latexFont = new Font(app.getPlainFont().getName(), app
				.getPlainFont().getStyle(), app.getPlainFont().getSize() - 1);

		if (latexStr != null && isLatexDrawable) {
			app.getDrawEquation().drawLatexImageIcon(app, latexIcon, latexStr,
					latexFont, false, Color.black, null);
			lblPreview.setText(" ");
		} else {
			lblPreview.setText(coModel.getNonLatexText());
		}
		lblPreview.setIcon(latexIcon);

		updateGUI();
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
				coModel.cancel();

			} else if (source == btApply) {
				// processInput();

				// btOK acts as cancel for now
			} else if (source == btOK) {
				coModel.ok();
			}

			else if (source == btnObject) {
				btnValue.setSelected(!btnObject.isSelected());
				coModel.createNewGeo(fldName.getText());
			} else if (source == btnValue) {
				btnObject.setSelected(!btnValue.isSelected());
				coModel.createNewGeo(fldName.getText());
			}

			else if (source == cbScanOrder || source == cbLeftRightOrder
					|| source == ckTranspose) {
				coModel.createNewGeo(fldName.getText());
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
			coModel.createNewGeo(fldName.getText());
		}
	}

	@Override
	public void setVisible(boolean isVisible) {
		if (!wrappedDialog.isModal()) {
			if (isVisible) { // set old mode again
				wrappedDialog.addWindowFocusListener(this);
			} else {
				wrappedDialog.removeWindowFocusListener(this);
				app.setSelectionListenerMode(null);
			}
		}

		// clean up on exit: either remove our geo or keep it and make it
		// visible
		if (!isVisible) {
			coModel.cleanUp();
		}
		super.setVisible(isVisible);
	}

	@SuppressWarnings("unused")
	private void closeDialog() {
		coModel.close();
		setVisible(false);
	}

	public void valueChanged(ListSelectionEvent e) {

		if (e.getSource() == typeList) {
			typeList.removeListSelectionListener(this);
			coModel.setObjectType(typeList.getSelectedIndex());
			// fldName.setText("");
			coModel.createNewGeo(fldName.getText());
			typeList.addListSelectionListener(this);
		}
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		// close the window and set the geo when focus is lost
		if (wrappedDialog.isVisible()
		// workaround for IE applets: focus is lost immediately -> dialog closes
				&& !app.isApplet()) {
			setVisible(false);
		}
	}

	public void focusGained(FocusEvent e) {
		//
	}

	public void focusLost(FocusEvent e) {
		doTextFieldActionPerformed((JTextField) (e.getSource()));
	}

	public void setName(String name) {
		fldName.setText(name);
	}

	public void setSortVisible(boolean isVisible) {
		ckSort.setVisible(isVisible);
	}

	public boolean isVisible() {
		return isVisible();
	}

	public boolean isCopiedByValue() {
		return btnValue.isSelected();
	}

	public boolean isScannedByColumn() {
		return cbScanOrder.getSelectedIndex() == 1;
	}

	public boolean isLeftToRight() {
		return cbLeftRightOrder.getSelectedIndex() == 0;
	}

	public boolean isTranspose() {
		return ckTranspose.isSelected();
	}

}
