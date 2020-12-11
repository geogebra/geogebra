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
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import elemental2.dom.DragEvent;
import elemental2.dom.File;
import elemental2.dom.FileList;
import elemental2.dom.HTMLInputElement;
import jsinterop.base.Js;

/**
 * dialog to insert pdf page as image
 */
public class PDFInputDialog extends ComponentDialog
		implements FastClickHandler, PDFListener {
	private FlowPanel pdfContainerPanel;
	private FlowPanel imgTextPanel;
	private FlowPanel pdfPageTextPanel;
	private StandardButton leftBtn;
	private StandardButton rightBtn;
	private NoDragImage previewImg;
	private AutoCompleteTextFieldW curPageNrField;
	private FileUpload pdfChooser = getPDFChooser();
	/**
	 * pdf.js wrapper
	 */
	PDFWrapper pdf;
	private ProgressBar progressBar;

	/** indicates if current page number text field is in focus */
	private String previewSrc;
	private boolean isFocus = false;

	private FileUpload getPDFChooser() {
		FileUpload pdfChooser = new FileUpload();
		pdfChooser.addChangeHandler(event -> {
				HTMLInputElement el = Js.uncheckedCast(pdfChooser.getElement());
				loadPdf(el.files.item(0));
			});
		pdfChooser.getElement().setAttribute("accept", ".pdf");
		return pdfChooser;
	}

	/**
	 * @param app
	 *            see {@link App}
	 * @param data
	 * 			  dialog transkeys
	 */
	public PDFInputDialog(AppW app, DialogData data) {
		super(app, data, false, true);
		addStyleName("pdfDialog");
		buildContent();
		initActions();
		JavaScriptInjector.inject(PDFResources.INSTANCE.pdfJs());
		JavaScriptInjector.inject(PDFResources.INSTANCE.pdfWorkerJs());
	}

	private void buildContent() {
		FlowPanel contentPanel = new FlowPanel();
		contentPanel.addStyleName("pdfDialogContent");

		pdfContainerPanel = new FlowPanel();
		pdfContainerPanel.setStyleName("pdfContainer");
		addHelpToImgText();
		addDropHandler(pdfContainerPanel.getElement());
		contentPanel.add(pdfContainerPanel);
		contentPanel.add(pdfChooser);

		pdfChooser.addStyleName("hidden");
		setPosBtnDisabled(true);

		addDialogContent(contentPanel);
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
		Label clickOrDragText = new Label(app.getLocalization().getMenu("pdfClickOrDrag"));
		clickOrDragText.addStyleName("pdfDialogText");
		clickOrDragText.addStyleName("clickOrDragText");
		imgTextPanel.add(clickOrDragText);
		pdfContainerPanel.add(imgTextPanel);
	}

	private void addDropHandler(Element element) {
		elemental2.dom.Element el = Js.uncheckedCast(element);
		el.addEventListener("drop", evt -> {
			FileList files = ((DragEvent) evt).dataTransfer.files;
			loadPdf(files.item(0));
			evt.stopPropagation();
			evt.preventDefault();
		});
	}

	private void buildPdfContainer() {
		pdfContainerPanel.clear();
		pdfContainerPanel.addStyleName("withPdf");
		FlowPanel pdfPreviewPanel = new FlowPanel();
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
		curPageNrField = new AutoCompleteTextFieldW(3, app);
		curPageNrField.setText("1");
		curPageNrField.addStyleName("curPageField");
		Label pageLbl = new Label(app.getLocalization().getMenu("page"));
		pdfPageTextPanel.add(pageLbl);
		pdfPageTextPanel.add(curPageNrField);
		Label ofPageLbl = new Label(app.getLocalization().getMenu("of") + " "
				+ pdf.getNumberOfPages());
		pdfPageTextPanel.add(ofPageLbl);
		pdfContainerPanel.add(pdfPreviewPanel);
		pdfContainerPanel.add(pdfPageTextPanel);
		initPreviewActions();
	}

	private void initPreviewActions() {
		curPageNrField.addKeyPressHandler(event -> {
			if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
				changePageFromTextField();
			}
		});

		curPageNrField.addKeyUpHandler(event -> changePageFromTextField(true));
		addFocusBlurHandlers();
		addHoverHandlers();
	}

	private void addFocusBlurHandlers() {
		curPageNrField.getTextBox().addFocusHandler(event -> {
			setPageTextFieldStyleName("focus");
			setIsFocus(true);
		});

		curPageNrField.getTextBox().addBlurHandler(event -> {
			setPageTextFieldStyleName("default");
			setIsFocus(false);
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
		curPageNrField.getTextBox().addMouseOverHandler(event
				-> setPageTextFieldStyleName("hover"));
		curPageNrField.getTextBox().addMouseOutHandler(event -> {
			if (!isFocus()) {
				setPageTextFieldStyleName("default");
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
			Log.debug("page nr not exists: " + e.getMessage());
		}
	}

	private StandardButton createImgButton(FlowPanel root,
			ImageResource imgSource, int size, String styleName) {
		StandardButton btn = new StandardButton(imgSource, null, size, size);
		btn.addStyleName(styleName);
		btn.addFastClickHandler(this);
		root.add(btn);
		return btn;
	}

	private void initActions() {
		setOnPositiveAction(() -> {
			String data = previewSrc;
			((AppW) app).imageDropHappened("pdf.png", data);
		});
		pdfContainerPanel.sinkEvents(Event.ONCLICK);
		pdfContainerPanel.addHandler(event -> {
			if (!hasPdf()) {
				choosePdfFile();
			}
		}, ClickEvent.getType());
	}

	@Override
	public void onClick(Widget source) {
		if (source == leftBtn) {
			pdf.previousPage();
			displayCurrentPageNumber();
		} else if (source == rightBtn) {
			pdf.nextPage();
			displayCurrentPageNumber();
		}
	}

	@Override
	public void hide() {
		super.hide();
		app.getGuiManager().setMode(EuclidianConstants.MODE_SELECT_MOW,
				ModeSetter.TOOLBAR);
	}

	/**
	 * Choose PDF to insert.
	 */
	void choosePdfFile() {
		pdfChooser.click();
	}

	/**
	 * loads the pdf
	 *
	 * @param file
	 *            to load.
	 *
	 */
	void loadPdf(File file) {
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
	 * Progress bar for loading pdf
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
				setPosBtnDisabled(true);
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
		if (pdf.getNumberOfPages() == 1) {
			leftBtn.addStyleName("hidden");
			rightBtn.addStyleName("hidden");
			pdfPageTextPanel.addStyleName("hidden");
		} else {
			displayCurrentPageNumber();
		}
		setPosBtnDisabled(false);
	}

	private void buildLoadingPanel() {
		pdfContainerPanel.clear();
		pdfContainerPanel.removeStyleName("withPdf");
		imgTextPanel = new FlowPanel();
		imgTextPanel.addStyleName("imgTextElement");
		progressBar = new ProgressBar();
		pdfContainerPanel.add(progressBar);
		Label loadText = new Label(app.getLocalization().getMenu("PdfLoadText"));
		loadText.addStyleName("pdfDialogText");
		loadText.addStyleName("loadText");
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
		Label errorText = new Label(app.getLocalization().getMenu("PdfErrorText"));
		errorText.addStyleName("pdfDialogText");
		errorText.addStyleName("errorText");
		imgTextPanel.add(errorText);
		pdfContainerPanel.add(imgTextPanel);
	}

	@Override
	public void finishLoading(boolean result) {
		progressBar.finishLoading(result);
	}
}