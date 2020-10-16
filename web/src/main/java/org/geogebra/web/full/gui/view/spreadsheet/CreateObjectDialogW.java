package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.gui.view.spreadsheet.CreateObjectModel;
import org.geogebra.common.gui.view.spreadsheet.CreateObjectModel.ICreateObjectListener;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.view.algebra.DOMIndexHTMLBuilder;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.CardPanel;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.canvas.client.Canvas;
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
 */
public class CreateObjectDialogW extends ComponentDialog implements ICreateObjectListener {

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
	private ListBox typeList;
	private Label lblPreviewHeader;
	private Label lblOptions;
	private FlowPanel centerPanel = new FlowPanel();
	private Localization loc;
	
	/**
	 * @param app
	 *            app
	 * @param view
	 *            spreadsheet
	 * @param objectType
	 *            resulting object type
	 * @param title
	 * 			  dialog title
	 */
	public CreateObjectDialogW(AppW app, SpreadsheetViewW view, int objectType, String title) {
		super(app, new DialogData(title), false, false);
		addStyleName("createObjDialog");
		MyTableW table = view.getSpreadsheetTable();
		coModel = new CreateObjectModel(app, objectType, this);
		coModel.setCellRangeProcessor(table.getCellRangeProcessor());
		coModel.setSelectedCellRanges(table.getSelectedCellRanges());
		loc = app.getLocalization();

		createAdditionalGUI();

		isIniting = false;
		setLabels();

		typeList.setSelectedIndex(objectType);
		objectTypeChanged();

		updateGUI();
		setOnNegativeAction(() -> coModel.cancel());
		setOnPositiveAction(() -> coModel.ok());
	}

	/**
	 * @param  objectType - type of object
	 * @return title
	 */
	public String getTitle(int objectType) {
		switch (objectType) {
		default:
			return null;
		case 0:
			return "CreateList";
		case 1:
			return "CreateListOfPoints";
		case 3:
			return "CreateTable";
		case 4:
			return "CreatePolyLine";
		case 2:
			return "CreateMatrix";
		}
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
		typeList.addChangeHandler(event -> objectTypeChanged());

		lblName = new Label();
		lblName.setStyleName("panelTitle");
		InputPanelW input = new InputPanelW(app, -1, false);

		fldName = input.getTextComponent();
		fldName.addBlurHandler(event -> apply(fldName));

		cbScanOrder = new ListBox();
		cbScanOrder.addChangeHandler(event -> apply(cbScanOrder));
		
		cbLeftRightOrder = new ListBox();
		cbLeftRightOrder.addChangeHandler(event -> apply(cbLeftRightOrder));
	
		btnObject = new RadioButton("group1", "");
		btnValue = new RadioButton("group1", "");
		btnObject.setValue(true);
		
		ckSort = new CheckBox();
		ckSort.setValue(false);

		ckTranspose = new CheckBox();
		ckTranspose.setValue(false);
		ckTranspose.addClickHandler(event -> apply(ckTranspose));

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
		addDialogContent(centerPanel);
	}

	private void buildOptionsPanel() {
		optionPane = new FlowPanel();
		lblOptions = new Label();
		lblOptions.setStyleName("panelTitle");
		FlowPanel copyPanel = new FlowPanel();
		copyPanel.add(btnObject);
		copyPanel.add(btnValue);

		FlowPanel northPanel = new FlowPanel();
		
		northPanel.add(copyPanel);

		FlowPanel orderPanel = new FlowPanel();
		orderPanel.add(cbScanOrder);

		FlowPanel transposePanel = new FlowPanel();
		transposePanel.add(ckTranspose);

		FlowPanel xySwitchPanel = new FlowPanel();
		xySwitchPanel.add(cbLeftRightOrder);

		// TODO: this is not a good way to manage visibility of option panels
		// ..fix it if we need more options in the future
		cards = new CardPanel();
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

	/**
	 * update labels
	 */
	public void setLabels() {
		if (isIniting) {
			return;
		}

		// object/value checkboxes
		btnObject.setText(loc.getMenu("DependentObjects"));
		btnValue.setText(loc.getMenu("FreeObjects"));
		
		// transpose checkbox
		ckTranspose.setText(loc.getMenu("Transpose"));
		ckSort.setText(loc.getMenu("Sort"));

		lblName.setText(loc.getMenu("Name") + ": ");

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
		lblOptions.setText(loc.getMenu("Options"));
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
		} else if (source == btnObject) {
			btnValue.setValue(!btnObject.getValue());
			coModel.createNewGeo(fldName.getText());
		} else if (source == btnValue) {
			btnObject.setValue(!btnValue.getValue());
			coModel.createNewGeo(fldName.getText());
		} else if (source == cbScanOrder || source == cbLeftRightOrder
				|| source == ckTranspose) {
			coModel.createNewGeo(fldName.getText());
		}
	}
	
	private void doTextFieldActionPerformed() {
		coModel.createNewGeo(fldName.getText());
	}

	@Override
	public void setVisible(boolean isVisible) {
		// clean up on exit: either remove our geo or keep it and make it
		// visible
		if (!isVisible) {
			coModel.cleanUp();
			hide();
		}
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
		return super.isVisible();
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
}