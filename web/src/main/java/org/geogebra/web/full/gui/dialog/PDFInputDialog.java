package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 *
 */
public class PDFInputDialog extends DialogBoxW implements FastClickHandler {
	/**
	 * see {@link App}
	 */
	protected AppW appW;
	private FlowPanel mainPanel;
	private FlowPanel pdfContainerPanel;
	private FlowPanel pdfPreviewPanel;
	private FlowPanel pdfPageTextPanel;
	private Label clickOrDragText;
	private FlowPanel buttonPanel;
	private StandardButton insertBtn;
	private StandardButton cancelBtn;
	private StandardButton leftBtn;
	private StandardButton rightBtn;
	private Label pageLbl;
	private AutoCompleteTextFieldW curPageNrField;
	private Label ofPageLbl;
	private String pdfFile = null;
	private PDFChooser pdfChooser = new PDFChooser();

	private static class PDFChooser extends FileUpload
			implements ChangeHandler {
		public PDFChooser() {
			super();
			addChangeHandler(this);
		}

		public void open() {
			click();
		}

		@Override
		public void onChange(ChangeEvent event) {
			loadPdf(getFilename());
		}
	}

	/**
	 * @param app
	 *            see {@link App}
	 */
	public PDFInputDialog(AppW app) {
		super(app.getPanel(), app);
		this.appW = app;
		initGui();
		initActions();
	}

	private void initGui() {
		mainPanel = new FlowPanel();
		// panel for pdf
		pdfContainerPanel = new FlowPanel();
		pdfContainerPanel.setStyleName("pdfContainer");
		NoDragImage folderImg = new NoDragImage(
				MaterialDesignResources.INSTANCE.mow_pdf_open_folder(), 96);
		folderImg.addStyleName("folderImg");
		pdfContainerPanel.add(folderImg);
		clickOrDragText = new Label();
		clickOrDragText.addStyleName("clickOrDragText");
		pdfContainerPanel.add(clickOrDragText);
		pageLbl = new Label();
		ofPageLbl = new Label();
		// panel for buttons
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		cancelBtn = createTxtButton(buttonPanel, "cancelBtn", true);
		insertBtn = createTxtButton(buttonPanel, "insertBtn", false);
		// add panels
		add(mainPanel);
		mainPanel.add(pdfContainerPanel);
		mainPanel.add(buttonPanel);
		mainPanel.add(pdfChooser);
		pdfChooser.addStyleName("hidden");
		// style
		addStyleName("GeoGebraPopup");
		addStyleName("pdfDialog");
		setGlassEnabled(true);
		// only for testing here TODO remove me from here
		// buildPdfContainer();
		setLabels();
	}

	private void buildPdfContainer() {
		pdfContainerPanel.clear();
		pdfContainerPanel.addStyleName("withPdf");
		pdfPreviewPanel = new FlowPanel();
		pdfPreviewPanel.addStyleName("pdfPreview");
		leftBtn = createImgButton(pdfPreviewPanel,
				KeyboardResources.INSTANCE.keyboard_arrowLeft_black(), 24,
				"leftBtn");
		// TODO add here the preview image of pdf MOW-424
		rightBtn = createImgButton(pdfPreviewPanel,
				KeyboardResources.INSTANCE.keyboard_arrowRight_black(), 24,
				"rightBtn");
		// text info about pages at bottom
		pdfPageTextPanel = new FlowPanel();
		pdfPageTextPanel.addStyleName("pdfPageText");
		curPageNrField = new AutoCompleteTextFieldW(3, appW);
		curPageNrField.setText("1");
		curPageNrField.addStyleName("curPageField");
		pdfPageTextPanel.add(pageLbl);
		pdfPageTextPanel.add(curPageNrField);
		pdfPageTextPanel.add(ofPageLbl);
		pdfContainerPanel.add(pdfPreviewPanel);
		pdfContainerPanel.add(pdfPageTextPanel);
	}

	private StandardButton createTxtButton(FlowPanel root, String styleName,
			boolean isEnabled) {
		StandardButton btn = new StandardButton("", appW);
		btn.addStyleName(styleName);
		btn.setEnabled(isEnabled);
		root.add(btn);
		return btn;
	}

	private StandardButton createImgButton(FlowPanel root,
			ImageResource imgSource, int size, String styleName) {
		StandardButton btn = new StandardButton(imgSource, null, size, size,
				appW);
		btn.addStyleName(styleName);
		btn.addFastClickHandler(this);
		root.add(btn);
		return btn;
	}

	private void initActions() {
		getCaption().setText(appW.getLocalization().getMenu("pdfDialogTitle"));
		insertBtn.addFastClickHandler(this);
		cancelBtn.addFastClickHandler(this);
		pdfContainerPanel.sinkEvents(Event.ONCLICK);
		pdfContainerPanel.addHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (pdfFile == null) {
					choosePdfFile();
				}
			}

		}, ClickEvent.getType());
	}

	/**
	 * set button labels
	 */
	public void setLabels() {
		clickOrDragText
				.setText(appW.getLocalization().getMenu("pdfClickOrDrag"));
		insertBtn.setText(appW.getLocalization().getMenu("Insert")); // insert
		cancelBtn.setText(appW.getLocalization().getMenu("Cancel")); // cancel
		pageLbl.setText(appW.getLocalization().getMenu("page")); // Page
		ofPageLbl.setText(appW.getLocalization().getMenu("of") + " 649"); // of
	}

	@Override
	public void onClick(Widget source) {
		if (source == cancelBtn) {
			hide();
		} else if (source == insertBtn) {
			// TODO process page of pdf MOW-428
		} else if (source == leftBtn) {
			// TODO handle left button MOW-426
		} else if (source == rightBtn) {
			// TODO handle left button MOW-426
		}
	}

	@Override
	public void hide() {
		appW.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
				ModeSetter.TOOLBAR);
		super.hide();
	}

	/**
	 * Choose PDF to insert.
	 */
	void choosePdfFile() {
		pdfChooser.open();
	}

	/**
	 * loads the pdf
	 * 
	 * @param filename
	 *            to load.
	 * 
	 */
	static void loadPdf(String filename) {
		Log.debug("PDF load: " + filename);
	}
}
