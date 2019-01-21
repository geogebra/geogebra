package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.common.gui.view.spreadsheet.CreateObjectModel;
import org.geogebra.common.gui.view.spreadsheet.CreateObjectModel.ICreateObjectListener;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.web.full.gui.dialog.InputDialogW;
import org.geogebra.web.full.gui.view.algebra.DOMIndexHTMLBuilder;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.CardPanel;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;

import com.google.gwt.canvas.client.Canvas;
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

	private CreateObjectModel coModel;
	private Label lblObject;
	private Label lblName;

	private CheckBox ckSort;
	/** transpose checkbox */
	CheckBox ckTranspose;
	private RadioButton btnValue;
	private RadioButton btnObject;
	/** switch scan between rows and columns */
	ListBox cbScanOrder;

	private boolean isIniting = true;
	private FlowPanel optionsPanel;
	private FlowPanel typePanel;
	/** name input */
	AutoCompleteTextFieldW fldName;

	private ScrollPanel previewPanel;

	/** box for coord order */
	ListBox cbLeftRightOrder;
	private CardPanel cards;
	private Label lblPreview;
	private FlowPanel optionPane;
	//private DefaultListModel model;
	private ListBox typeList;
	private Label lblPreviewHeader;
	private Label lblOptions;
	private FlowPanel centerPanel;
	
	/**
	 * @param app
	 *            app
	 * @param view
	 *            spreadsheet
	 * @param objectType
	 *            resulting object type
	 */
	public CreateObjectDialogW(AppW app, SpreadsheetViewW view, int objectType) {
		super(false, app, false);
		MyTableW table = view.getSpreadsheetTable();
		coModel = new CreateObjectModel(app, objectType, this);
		coModel.setCellRangeProcessor(table.getCellRangeProcessor());
		coModel.setSelectedCellRanges(table.getSelectedCellRanges());
		// cp = table.getCellRangeProcessor();
		// selectedCellRanges = table.selectedCellRanges;
		//
		boolean showApply = false;
		//
		createGUI(coModel.getTitle(), coModel.getTitle(), false, 16, 1, false, false, false,
				showApply, DialogType.GeoGebraEditor);

		createAdditionalGUI();

		isIniting = false;
		setLabels();

		// optionPane.add(inputPanel, BorderLayout.CENTER);
		typeList.setSelectedIndex(objectType);
		objectTypeChanged();
		
		centerAndFocus(false);
	
		updateGUI();
	}

	/**
	 * Change object type in model
	 */
	void objectTypeChanged() {
		coModel.setObjectType(typeList.getSelectedIndex());
		coModel.createNewGeo(fldName.getText());
	}

	private void createAdditionalGUI() {
		typeList = new ListBox();
		typeList.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				objectTypeChanged();
			}
		});

		lblName = new Label();
		lblName.setStyleName("panelTitle");
		InputPanelW input = new InputPanelW(app, -1, false);

		fldName = input.getTextComponent();
		fldName.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				apply(fldName);
			}
		});

		cbScanOrder = new ListBox();
		cbScanOrder.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				apply(cbScanOrder);
			}
		});
		
		cbLeftRightOrder = new ListBox();
		cbLeftRightOrder.addChangeHandler(new ChangeHandler() {
			
			@Override
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
			
			@Override
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

		FlowPanel namePanel = new FlowPanel();
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
		centerPanel.add(LayoutUtilW.panelRow(optionPane, pp));
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
		if (isIniting) {
			return;
		}

		// TODO: using buttons incorrectly for now
		// btnOK = cancel, cancel = create
		btOK.setText(loc.getMenu("Create"));
		btApply.setText(loc.getMenu("Apply"));
		btCancel.setText(loc.getMenu("Cancel"));

		// object/value checkboxes
		btnObject.setText(loc.getMenu("DependentObjects"));
		btnValue.setText(loc.getMenu("FreeObjects"));
		
		// transpose checkbox
		ckTranspose.setText(loc.getMenu("Transpose"));
		ckSort.setText(loc.getMenu("Sort"));
		
		lblName.setText(loc.getMenu("Name") + ": ");

		/*
		 * lblTake.setText(loc.getMenu("Take") + ": ");
		 * lblOrder.setText(loc.getMenu("Order") + ":");
		 * lblXYOrder.setText(loc.getMenu("Order") + ": ");
		 */

		cbScanOrder.clear();
		cbScanOrder.addItem(loc.getMenu("RowOrder"));
		cbScanOrder.addItem(loc.getMenu("ColumnOrder"));

		cbLeftRightOrder.clear();
		cbLeftRightOrder.addItem(SpreadsheetViewInterface.X_TO_Y);
		cbLeftRightOrder.addItem(SpreadsheetViewInterface.Y_FROM_X);

		typeList.clear();
		for (String item : coModel.getObjectTypeNames()) {
			typeList.addItem(item);
		}

		lblObject.setText(loc.getMenu("Object") + ":");

		lblPreviewHeader.setText(loc.getMenu("Preview") + ":");
//
//		namePanel.setBorder(BorderFactory.createCompoundBorder(
		// BorderFactory.createTitledBorder(loc.getMenu("Name")),
//				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		lblOptions.setText(loc.getMenu("Options"));
		wrappedPopup.getCaption().setText(coModel.getTitle());
		
	}

	private void updateGUI() {
		coModel.update();

		int idx = coModel.getOptionType();
		cards.setSelectedIndex(idx);
	}

	@Override
	public void updatePreview(String latexStr, boolean isLatexDrawable) {
		if (latexStr != null && isLatexDrawable) {

			Canvas c = Canvas.createIfSupported();
			previewPanel.setWidget(c);
			DrawEquationW.paintOnCanvas(coModel.getGeo(), latexStr, c,
					app.getFontSizeWeb());
		} else {
			previewPanel.setWidget(lblPreview);
			lblPreview.getElement().removeAllChildren();
			coModel.getNonLatexText(new DOMIndexHTMLBuilder(lblPreview, app));
		}
	}

	/**
	 * Process confirmation event (blur, change, click)
	 * 
	 * @param source
	 *            event source
	 */
	void apply(Widget source) {
		if (source == fldName) {
			doTextFieldActionPerformed();
		}
		else if (source == btCancel) {
			coModel.cancel();

		} else if (source == btApply) {
			// processInput();

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
	protected void actionPerformed(DomEvent<?> event) {
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

	@Override
	public void setName(String name) {
		fldName.setText(name);
	}

	@Override
	public void setSortVisible(boolean isVisible) {
		ckSort.setVisible(isVisible);
	}

	@Override
	public boolean isVisible() {
		return wrappedPopup != null && wrappedPopup.isVisible();
	}

	@Override
	public boolean isCopiedByValue() {
		return btnValue.getValue();
	}

	@Override
	public boolean isScannedByColumn() {
		return cbScanOrder.getSelectedIndex() == 1;
	}

	@Override
	public boolean isLeftToRight() {
		return cbLeftRightOrder.getSelectedIndex() == 0;
	}

	@Override
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
		btCancel.addStyleName("cancelBtn");
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
