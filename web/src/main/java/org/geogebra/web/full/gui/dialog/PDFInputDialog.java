package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.css.PDFResources;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.pdf.PDFWrapper;
import org.geogebra.web.html5.util.pdf.PDFWrapper.PDFListener;
import org.geogebra.web.resources.JavaScriptInjector;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 *
 */
public class PDFInputDialog extends DialogBoxW implements FastClickHandler, PDFListener {
	/**
	 * see {@link App}
	 */
	protected AppW appW;
	private FlowPanel mainPanel;
	private FlowPanel pdfContainerPanel;
	private FlowPanel pdfPreviewPanel;
	private FlowPanel imgTextPanel;
	private FlowPanel pdfPageTextPanel;
	private Label clickOrDragText;
	private Label loadText;
	private Label errorText;
	private FlowPanel buttonPanel;
	/**
	 * Insert button
	 */
	StandardButton insertBtn;
	private StandardButton cancelBtn;
	private StandardButton leftBtn;
	private StandardButton rightBtn;
	private Label pageLbl;
	private NoDragImage previewImg;
	private AutoCompleteTextFieldW curPageNrField;
	private Label ofPageLbl;
	private PDFChooser pdfChooser = new PDFChooser();
	/**
	 * pdf.js wrapper
	 */
	PDFWrapper pdf;
	private ProgressBar progressBar;

	/** indicates if current page number text field is in focus */
	protected boolean tfActive = false;
	private String previewSrc;
	private boolean isFocus = false;

	private class PDFChooser extends FileUpload
			implements ChangeHandler {
		public PDFChooser() {
			super();
			addChangeHandler(this);
			getElement().setAttribute("accept", ".pdf");
		}

		public void open() {
			click();
		}

		@Override
		public void onChange(ChangeEvent event) {
			PDFInputDialog.this.loadPdf(getSelectedFile());
		}

		private native JavaScriptObject getSelectedFile()/*-{
			return $doc.querySelector('input[type=file]').files[0];
		}-*/;
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
		JavaScriptInjector.inject(PDFResources.INSTANCE.pdfCombinedJs());
	}

	private void initGui() {
		mainPanel = new FlowPanel();
		mainPanel.addStyleName("pdfDialogContent");
		// panel for pdf
		pdfContainerPanel = new FlowPanel();
		pdfContainerPanel.setStyleName("pdfContainer");
		addHelpToImgText();
		addDropHandler(pdfContainerPanel.getElement());
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
		getContainerElement().addClassName("pdfDialogMainPanel");
		getContainerElement().getFirstChildElement()
				.addClassName("pdfDialogTable");
		setGlassEnabled(true);
		setLabels();
	}

	private void createFolderImg() {
		imgTextPanel = new FlowPanel();
		imgTextPanel.addStyleName("imgTextElement");
		NoDragImage folderImg = new NoDragImage(
				MaterialDesignResources.INSTANCE.mow_pdf_open_folder(), 96);
		folderImg.addStyleName("folderImg");
		imgTextPanel.add(folderImg);
	}

	private void addHelpToImgText() {
		createFolderImg();
		clickOrDragText = new Label();
		clickOrDragText.addStyleName("pdfDialogText");
		clickOrDragText.addStyleName("clickOrDragText");
		imgTextPanel.add(clickOrDragText);
		pdfContainerPanel.add(imgTextPanel);
	}

	private native void addDropHandler(
			com.google.gwt.dom.client.Element element) /*-{
		var that = this;
		element
				.addEventListener(
						"drop",
						function(event) {
							var files = event.dataTransfer.files;
							that.@org.geogebra.web.full.gui.dialog.PDFInputDialog::loadPdf(Lcom/google/gwt/core/client/JavaScriptObject;)(files[0]);
							event.stopPropagation();
							event.preventDefault();
						});

	}-*/;

	private void buildPdfContainer() {
		pdfContainerPanel.clear();
		pdfContainerPanel.addStyleName("withPdf");
		pdfPreviewPanel = new FlowPanel();
		pdfPreviewPanel.addStyleName("pdfPreview");
		previewImg = new NoDragImage("");
		previewImg.addStyleName("previewImage");
		leftBtn = createImgButton(pdfPreviewPanel,
				KeyboardResources.INSTANCE.keyboard_arrowLeft_black(), 24,
				"leftBtn");
		pdfPreviewPanel.add(previewImg);
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
		initPreviewActions();
	}

	private void initPreviewActions() {

		curPageNrField.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					changePageFromTextField();
				}
			}
		});

		curPageNrField.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				changePageFromTextField(true);
			}
		});
		addFocusBlurHandlers();
		addHoverHandlers();
	}

	private void addFocusBlurHandlers() {
		curPageNrField.getTextBox().addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				setPageTextFieldStyleName("focus");
				setIsFocus(true);
			}
		});

		curPageNrField.getTextBox().addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				setPageTextFieldStyleName("default");
				setIsFocus(false);
			}
		});
	}

	/**
	 * @param isFocus
	 *            true if text field has focus
	 */
	public void setIsFocus(boolean isFocus) {
		this.isFocus = isFocus;
	}

	/**
	 * @return true if text field has focus
	 */
	public boolean isFocus() {
		return isFocus;
	}

	/**
	 * Add mouse over/ out handlers
	 */
	private void addHoverHandlers() {
		curPageNrField.getTextBox().addMouseOverHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				setPageTextFieldStyleName("hover");
			}
		});
		curPageNrField.getTextBox().addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				if (!isFocus()) {
					setPageTextFieldStyleName("default");
				}
			}
		});
	}

	/**
	 * @param additionalStyle
	 *            additional style name to define hover/focus/blur style
	 */
	public void setPageTextFieldStyleName(String additionalStyle) {
		curPageNrField.setStyleName("AutoCompleteTextFieldW curPageField");
		curPageNrField.addStyleName(additionalStyle);
	}

	/**
	 * Changes PDF page displayed depending on page number in its text field.
	 */
	void changePageFromTextField() {
		changePageFromTextField(false);
	}

	/**
	 * Changes PDF page displayed depending on page number in its text field.
	 *
	 * @param silent
	 *            set true for not calling any additional fallback method.
	 */
	void changePageFromTextField(boolean silent) {
		try {
			int pageNr = Integer.parseInt(curPageNrField.getText());
			if (!(pdf.setPageNumber(pageNr) || silent)) {
				displayCurrentPageNumber();
			}
		} catch (NumberFormatException e) {
			Log.debug("bad number: " + e.getMessage());
		}
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
				if (!hasPdf()) {
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
		if (pdf != null) {
			ofPageLbl.setText(appW.getLocalization().getMenu("of") + " "
					+ pdf.getPageCount()); // of
		}
		if (loadText != null) {
			loadText.setText(appW.getLocalization().getMenu("PdfLoadText"));
		}
		if (errorText != null) {
			errorText.setText(appW.getLocalization().getMenu("PdfErrorText"));
		}

	}

	@Override
	public void onClick(Widget source) {
		if (source == cancelBtn) {
			hide();
		} else if (source == insertBtn) {
			insertImage();
		} else if (source == leftBtn) {
			pdf.previousPage();
			displayCurrentPageNumber();
		} else if (source == rightBtn) {
			pdf.nextPage();
			displayCurrentPageNumber();
		}
	}

	private void insertImage() {
		String data = previewSrc;
		((AppW) app).imageDropHappened("pdf.svg", data);
		hide();
	}

	@Override
	public void hide() {
		super.hide();
		appW.getGuiManager().setMode(EuclidianConstants.MODE_SELECT_MOW,
				ModeSetter.TOOLBAR);
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
	 * @param file
	 *            to load.
	 *
	 */
	void loadPdf(JavaScriptObject file) {
		buildLoadingPanel();
		pdf = new PDFWrapper(file, this);
	}

	/**
	 * @return if PDF is already chosen.
	 */
	boolean hasPdf() {
		return pdf != null;
	}

	@Override
	public void onPageDisplay(String imgSrc) {
		previewImg.getElement().setAttribute("src", imgSrc);
		previewSrc = imgSrc;
	}

	private void displayCurrentPageNumber() {
		curPageNrField.setText(Integer.toString(pdf.getPageNumber()));
	}

	/**
	 * Progress bar.
	 *
	 * @author judit
	 *
	 */
	public class ProgressBar extends SimplePanel {

		/**
		 * Loaded part of the progress bar.
		 */
		SimplePanel loadedPart;

		/**
		 * Creates a new progress bar.
		 */
		public ProgressBar() {
			addStyleName("progressBar");
			loadedPart = new SimplePanel();
			add(loadedPart);
			loadedPart.setWidth("0%");
		}

		/**
		 * After the pdf loaded, the progress bar should be finished quickly.
		 *
		 * @param result
		 *            true if the loading of the pdf was successful
		 */
		public void finishLoading(boolean result) {
			if (result) {
				onPDFLoaded();
			} else {
				pdf = null;
				insertBtn.setEnabled(false);
				buildErrorPanel();
			}
		}

		/**
		 * Sets the value of the progress bar for the given percent.
		 * 
		 * @param percent
		 *            the new value of the progress bar
		 */
		public void setPercent(double percent) {
			loadedPart.setWidth(percent + "%");
		}
	}

	@Override
	public void setProgressBarPercent(double percent) {
		progressBar.setPercent(percent);
	}

	/**
	 * method that is called right after PDF loads.
	 */
	void onPDFLoaded() {
		pdf.setPageNumber(1);
		buildPdfContainer();
		if (pdf.getPageCount() == 1) {
			leftBtn.addStyleName("hidden");
			rightBtn.addStyleName("hidden");
			pdfPageTextPanel.addStyleName("hidden");
		} else {
			displayCurrentPageNumber();
		}
		setLabels();
		insertBtn.setEnabled(true);
	}

	private void buildLoadingPanel() {
		pdfContainerPanel.clear();
		pdfContainerPanel.removeStyleName("withPdf");
		imgTextPanel = new FlowPanel();
		imgTextPanel.addStyleName("imgTextElement");
		progressBar = new ProgressBar();
		pdfContainerPanel.add(progressBar);
		if (loadText == null) {
			loadText = new Label();
			loadText.addStyleName("pdfDialogText");
			loadText.addStyleName("loadText");
			loadText.setText(appW.getLocalization().getMenu("PdfLoadText"));
		}
		imgTextPanel.add(progressBar);
		imgTextPanel.add(loadText);
		pdfContainerPanel.add(imgTextPanel);
	}

	/**
	 * Builds error panel, if the opening of pdf failed.
	 */
	void buildErrorPanel() {
		pdfContainerPanel.clear();
		pdfContainerPanel.removeStyleName("withPdf");
		createFolderImg();
		if (errorText == null) {
			errorText = new Label();
			errorText.addStyleName("pdfDialogText");
			errorText.addStyleName("errorText");
			errorText.setText(appW.getLocalization().getMenu("PdfErrorText"));
		}
		imgTextPanel.add(errorText);
		pdfContainerPanel.add(imgTextPanel);
	}

	@Override
	public void finishLoading(boolean result) {
		progressBar.finishLoading(result);
	}
}

