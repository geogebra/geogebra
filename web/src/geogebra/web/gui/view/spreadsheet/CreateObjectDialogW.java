package geogebra.web.gui.view.spreadsheet;

import geogebra.common.gui.view.algebra.DialogType;
import geogebra.common.gui.view.spreadsheet.CreateObjectModel;
import geogebra.common.gui.view.spreadsheet.CreateObjectModel.ICreateObjectListener;
import geogebra.common.main.App;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.util.CardPanel;
import geogebra.web.gui.dialog.InputDialogW;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog to create GeoElements (lists, matrices, tabletext, etc.) from
 * spreadsheet cell selections
 * 
 * @author G. Sturr
 * 
 */
@SuppressWarnings({ "javadoc", "rawtypes" })
public class CreateObjectDialogW extends InputDialogW implements
		 ICreateObjectListener {

	private MyTableW table;
	private CreateObjectModel coModel;
	private Label lblObject, lblName;

	private CheckBox ckSort, ckTranspose;
	private RadioButton btnValue, btnObject;
	private ListBox cbScanOrder;

	private boolean isIniting = true;
	private FlowPanel optionsPanel;
	private FlowPanel typePanel;

	private AutoCompleteTextFieldW fldName;

	private ScrollPanel previewPanel;

	private ListBox cbLeftRightOrder;
	private CardPanel cards;
	private Label lblPreview;
	private FlowPanel namePanel;
	private FlowPanel optionPane;
	//private DefaultListModel model;
	private ListBox typeList;
	boolean showApply = false;
	private Label lblPreviewHeader;
	private Label lblOptions;
	private static final int OPTION_ORDER = 0;
	private static final int OPTION_XY = 1;
	private static final int OPTION_TRANSPOSE = 2;
	
	public CreateObjectDialogW(AppW app, SpreadsheetViewW view, int objectType) {

		super(false);
		this.table = (MyTableW) view.getSpreadsheetTable();
		coModel = new CreateObjectModel(app, view, objectType, this);
		coModel.setCellRangeProcessor(table.getCellRangeProcessor());
		coModel.setSelectedCellRanges(table.selectedCellRanges);
		this.app = app;
		// cp = table.getCellRangeProcessor();
		// selectedCellRanges = table.selectedCellRanges;
		//
		// boolean showApply = false;
		//
		createGUI(coModel.getTitle(), coModel.getTitle(), false, 16, 1, false, false, false,
				showApply, DialogType.GeoGebraEditor);


		createAdditionalGUI();


		isIniting = false;
		setLabels();
		
		// setTitle((String) model.getElementAt(objectType));

		// optionPane.add(inputPanel, BorderLayout.CENTER);
		typeList.setSelectedIndex(objectType);
		
		centerOnScreen();//
	
		updateGUI();
//		objectTypeChanged();
		
	}

	private void objectTypeChanged() {
		coModel.setObjectType(typeList.getSelectedIndex());
		coModel.createNewGeo(fldName.getText());

	}
	@SuppressWarnings("unchecked")
	private void createAdditionalGUI() {

	//	model = new DefaultListModel();
		typeList = new ListBox();
		typeList.addChangeHandler(new ChangeHandler() {
			
			public void onChange(ChangeEvent event) {
				objectTypeChanged();
			}
		});

		//inputPanel.setTitle("wwwwwwwwwww");
		lblName = new Label();
		lblName.setStyleName("panelTitle");
	//	InputPanelW input = new InputPanelW(null, app, -1, false);

		fldName = inputPanel.getTextComponent();
		fldName.showPopupSymbolButton(true);
		fldName.addBlurHandler(new BlurHandler() {
			
			public void onBlur(BlurEvent event) {
				apply(fldName);
			}
		});
		
		cbScanOrder = new ListBox();
		cbScanOrder.addChangeHandler(new ChangeHandler() {
			
			public void onChange(ChangeEvent event) {
				apply(cbScanOrder);
				
			}
		});
		
		cbLeftRightOrder = new ListBox();
		cbScanOrder.addChangeHandler(new ChangeHandler() {
			
			public void onChange(ChangeEvent event) {
				apply(cbScanOrder);
				
			}
		});
	
		btnObject = new RadioButton("group1", "");
		btnValue = new RadioButton("group1", "");
		btnObject.setValue(true);
		
		ckSort = new CheckBox();
		ckSort.setValue(false);

		ckTranspose = new CheckBox();
		ckTranspose.setValue(false);
		
		// show the object list only if an object type is not given

		lblObject = new Label();
		lblObject.setStyleName("panelTitle");
		
		if (coModel.getObjectType() < 0) {
			coModel.setListType();
			typePanel = new FlowPanel();
			typePanel.add(lblObject);
			typePanel.add(typeList);
			optionPane.add(typePanel);
		}

		namePanel = new FlowPanel();
		namePanel.add(lblName);
		namePanel.add(fldName);

		buildOptionsPanel();
		FlowPanel p = new FlowPanel();
		p.add(namePanel);
		p.add(optionsPanel);

		lblPreview = new Label();

		previewPanel = new ScrollPanel(lblPreview);
//		previewPanel.setBackground(this.wrappedDialog.getBackground());

		
		FlowPanel op = new FlowPanel();
		op.add(p);
	
		
//		previewPanel.setPreferredSize(new Dimension(200,
//				p.getPreferredSize().height));
//
		optionPane.add(op);
		inputPanel.add(optionPane);

	}

	private void buildOptionsPanel() {
		optionPane = new FlowPanel();
		lblOptions = new Label();
		lblOptions.setStyleName("panelTitle");
		FlowPanel copyPanel = new FlowPanel();
		copyPanel.add(btnObject);
		copyPanel.add(btnValue);
		// copyPanel.add(cbTake);

		FlowPanel northPanel = new FlowPanel();
		
		northPanel.add(copyPanel);

		FlowPanel orderPanel = new FlowPanel();
		orderPanel.add(cbScanOrder);

		FlowPanel transposePanel = new FlowPanel();
		transposePanel.add(ckTranspose);

		FlowPanel xySwitchPanel = new FlowPanel();
		xySwitchPanel.add(cbLeftRightOrder);

		FlowPanel pointListPanel = new FlowPanel();
		//pointListPanel.add(Box.createRigidArea(lblName.getSize()));

		// TODO: this is not a good way to manage visibility of option panels
		// ..fix it if we need more options in the future
		cards = new CardPanel();
		//cards.getTabBar().setVisible(false);
		cards.setStyleName("panelIndent");
		cards.add(orderPanel);
		cards.add(xySwitchPanel);
		cards.add(transposePanel);
		
		optionsPanel = new FlowPanel();
		optionsPanel.add(northPanel);
		optionsPanel.add(lblOptions);
		// app.borderWest());
		optionsPanel.add(cards);

		lblPreviewHeader = new Label();
		optionsPanel.add(lblPreviewHeader);

	}

	@Override
	public void setLabels() {

		if (isIniting){
			return;
		}

		// TODO: using buttons incorrectly for now
		// btnOK = cancel, cancel = create
		btOK.setText(app.getPlain("Cancel"));
		btApply.setText(app.getPlain("Apply"));
		btCancel.setText(app.getMenu("Create"));

		// object/value checkboxes
		btnObject.setText(app.getPlain("DependentObjects"));
		btnValue.setText(app.getPlain("FreeObjects"));
		
		// transpose checkbox
		ckTranspose.setText(app.getMenu("Transpose"));
		ckSort.setText(app.getMenu("Sort"));
		
		lblName.setText(app.getPlain("Name") + ": ");

		/*
		 * lblTake.setText(app.getMenu("Take") + ": ");
		 * lblOrder.setText(app.getMenu("Order") + ":");
		 * lblXYOrder.setText(app.getMenu("Order") + ": ");
		 */

		cbScanOrder.clear();
		cbScanOrder.addItem(app.getMenu("RowOrder"));
		cbScanOrder.addItem(app.getMenu("ColumnOrder"));

		cbLeftRightOrder.clear();
		cbLeftRightOrder.addItem(app.getMenu("X->Y"));
		cbLeftRightOrder.addItem(app.getMenu("Y<-X"));

		typeList.clear();
		for (String item : coModel.getObjectTypeNames()) {
			typeList.addItem(item);
		}

		lblObject.setText(app.getMenu("Object") + ":");

		// lblPreviewHeader.setText(app.getMenu("Preview")+ ":");
//
//		namePanel.setBorder(BorderFactory.createCompoundBorder(
//				BorderFactory.createTitledBorder(app.getPlain("Name")),
//				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
//		previewPanel.setBorder(BorderFactory.createTitledBorder(app
//				.getMenu("Preview")));

		lblOptions.setText(app.getMenu("Options"));
		wrappedPopup.setTitle(coModel.getTitle());

	}

	private void updateGUI() {
		coModel.update();
		int idx = 0;
		
		switch (coModel.getObjectType()) {
		case CreateObjectModel.TYPE_LIST:
			idx = OPTION_ORDER;
			break;
		case CreateObjectModel.TYPE_LISTOFPOINTS:
			idx = OPTION_TRANSPOSE;
			break;
		case CreateObjectModel.TYPE_MATRIX:
			idx = OPTION_ORDER;
			break;
		case CreateObjectModel.TYPE_TABLETEXT:
			idx = OPTION_TRANSPOSE;
			
			break;
		case CreateObjectModel.TYPE_POLYLINE:
			idx = OPTION_ORDER;
		}
		App.debug("[CO] object type: " + idx);
		cards.setSelectedIndex(idx);
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

	}

	public void updatePreview(String latexStr, boolean isLatexDrawable) {
//		ImageIcon latexIcon = new ImageIcon();
//		Font latexFont = new Font(app.getPlainFont().getName(), app
//				.getPlainFont().getStyle(), app.getPlainFont().getSize() - 1);
//
//		if (latexStr != null && isLatexDrawable) {
//			app.getDrawEquation().drawLatexImageIcon(app, latexIcon, latexStr,
//					latexFont, false, Color.black, null);
//			lblPreview.setText(" ");
//		} else {
//			lblPreview.setText(coModel.getNonLatexText());
//		}
//		lblPreview.setIcon(latexIcon);

	}

	
	private void apply(Widget source) {
		try {
		    if (source == fldName) {
					doTextFieldActionPerformed();

				} else 
				// btCancel acts as create for now
		    if (source == btCancel) {
				coModel.cancel();

			} else if (source == btApply) {
				// processInput();

				// btOK acts as cancel for now
			} else if (source == btOK) {
				coModel.ok();
			}

			else if (source == btnObject) {
				btnValue.setValue(!btnObject.getValue());
				coModel.createNewGeo(fldName.getText());
			} else if (source == btnValue) {
				btnObject.setValue(!btnValue.getValue());
				coModel.createNewGeo(fldName.getText());
			}

			else if (source == cbScanOrder || source == cbLeftRightOrder
					|| source == ckTranspose) {
				coModel.createNewGeo(fldName.getText());
			}


		} catch (Exception ex) {
			// do nothing on uninitializedValue
			setVisible(false);
		}
	}

	@Override
	protected void actionPerformed(DomEvent event) {
		Widget source = (Widget) event.getSource();
		apply(source);
	}
	
	private void doTextFieldActionPerformed() {
		coModel.createNewGeo(fldName.getText());
	}

	@Override
	public void setVisible(boolean isVisible) {
//		if (isModal()) {
//			if (isVisible) { // set old mode again
//				wrappedDialog.addWindowFocusListener(this);
//			} else {
//				wrappedDialog.removeWindowFocusListener(this);
//				app.setSelectionListenerMode(null);
//			}
//		}
//
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
		return btnValue.getValue();
	}

	public boolean isScannedByColumn() {
		return cbScanOrder.getSelectedIndex() == 1;
	}

	public boolean isLeftToRight() {
		return cbLeftRightOrder.getSelectedIndex() == 0;
	}

	public boolean isTranspose() {
		return ckTranspose.getValue();
	}

}
