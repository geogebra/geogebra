package geogebra.web.gui.view.spreadsheet;

import geogebra.common.gui.view.algebra.DialogType;
import geogebra.common.gui.view.spreadsheet.CreateObjectModel;
import geogebra.common.gui.view.spreadsheet.CreateObjectModel.ICreateObjectListener;
import geogebra.common.main.App;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.util.CardPanel;
import geogebra.html5.gui.util.LayoutUtil;
import geogebra.html5.main.AppW;
import geogebra.html5.main.DrawEquationWeb;
import geogebra.web.gui.dialog.InputDialogW;
import geogebra.web.gui.view.algebra.InputPanelW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.ui.Button;
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
public class CreateObjectDialogW extends InputDialogW implements
		 ICreateObjectListener {

	private MyTableW table;
	private CreateObjectModel coModel;
	private Label lblObject, lblName;

	private CheckBox ckSort;
	CheckBox ckTranspose;
	private RadioButton btnValue, btnObject;
	ListBox cbScanOrder;

	private boolean isIniting = true;
	private FlowPanel optionsPanel;
	private FlowPanel typePanel;

	AutoCompleteTextFieldW fldName;

	private ScrollPanel previewPanel;

	ListBox cbLeftRightOrder;
	private CardPanel cards;
	private Label lblPreview;
	private Label lblPreviewTitle;
	private FlowPanel namePanel;
	private FlowPanel optionPane;
	//private DefaultListModel model;
	private ListBox typeList;
	boolean showApply = false;
	private Label lblPreviewHeader;
	private Label lblOptions;
	private FlowPanel centerPanel;
	
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
		objectTypeChanged();
		
		centerOnScreen();//
	
		updateGUI();
		
	}

	void objectTypeChanged() {
		coModel.setObjectType(typeList.getSelectedIndex());
		coModel.createNewGeo(fldName.getText());

	}
	private void createAdditionalGUI() {

	//	model = new DefaultListModel();
		typeList = new ListBox();
		typeList.addChangeHandler(new ChangeHandler() {
			
			public void onChange(ChangeEvent event) {
				objectTypeChanged();
			}
		});

		lblName = new Label();
		lblName.setStyleName("panelTitle");
		InputPanelW input = new InputPanelW(null, app, -1, false);

		fldName = input.getTextComponent();
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
		cbLeftRightOrder.addChangeHandler(new ChangeHandler() {
			
			public void onChange(ChangeEvent event) {
				apply(cbLeftRightOrder);
				
			}
		});
	
		btnObject = new RadioButton("group1", "");
		btnValue = new RadioButton("group1", "");
		btnObject.setValue(true);
		
		ckSort = new CheckBox();
		ckSort.setValue(false);

		ckTranspose = new CheckBox();
		ckTranspose.setValue(false);
		ckTranspose.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				apply(ckTranspose);
			}
		});

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

		previewPanel.setStyleName("createObjectsScrollArea");
		
		FlowPanel op = new FlowPanel();
		op.add(p);
		
		

		
		optionPane.add(op);

		FlowPanel pp = new FlowPanel();
		
		lblPreviewHeader = new Label();
		pp.add(lblPreviewHeader);
		lblPreviewHeader.setStyleName("panelTitle");
		pp.add(previewPanel);
		pp.setStyleName("createObjectsPreview");
		centerPanel.add(LayoutUtil.panelRow(optionPane, pp));
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

		lblPreviewHeader.setText(app.getMenu("Preview")+ ":");
//
//		namePanel.setBorder(BorderFactory.createCompoundBorder(
//				BorderFactory.createTitledBorder(app.getPlain("Name")),
//				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		lblOptions.setText(app.getMenu("Options"));
		wrappedPopup.getCaption().setText(coModel.getTitle());
		
	}

	private void updateGUI() {
		coModel.update();
		
		int idx = coModel.getOptionType();

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
		if (latexStr != null && isLatexDrawable) {
			lblPreview.setText("");
			String latex = DrawEquationWeb.inputLatexCosmetics(latexStr);
			DrawEquationWeb.drawEquationAlgebraView(lblPreview.getElement(), "\\mathrm {" + latex
			        + "}");
		} else {
			lblPreview.setText(coModel.getNonLatexText());
		}

		
	}

	
	void apply(Widget source) {
		if (source == fldName) {
			doTextFieldActionPerformed();

		}
		else 
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
		wrappedPopup.setVisible(isVisible);
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

	@Override
	protected void createGUI(String title, String message,
	        boolean autoComplete, int columns, int rows,
	        boolean showSymbolPopupIcon, boolean selectInitText,
			boolean showProperties, boolean showApply1, DialogType type) {

		centerPanel = new FlowPanel();
		
		btOK = new Button();
		btOK.addClickHandler(this);

		btCancel = new Button();
		btCancel.addClickHandler(this);

		btApply = new Button();
		btApply.addClickHandler(this);
	
		// create button panel
		btPanel = new FlowPanel();
		btPanel.addStyleName("DialogButtonPanel");
		btPanel.add(btOK);
		btPanel.add(btCancel);
		// just tmp.
		if (showApply1) {
			btPanel.add(btApply);
		}
		// if (showProperties) {
		// btPanel.add(btProperties);
		// }

		setLabels();


		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName("Dialog-content");
		mainPanel.add(centerPanel);
		mainPanel.add(btPanel);

		wrappedPopup.setWidget(mainPanel);

	}
	
}
