package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.App;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
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
	private FormLabel pageLbl;
	private AutoCompleteTextFieldW curPageNrField;
	private Label ofPageLbl;

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
		// panel for buttons
		insertBtn = new StandardButton("", appW);
		insertBtn.addStyleName("insertBtn");
		insertBtn.setEnabled(false);
		cancelBtn = new StandardButton("", app);
		cancelBtn.addStyleName("cancelBtn");
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		buttonPanel.add(cancelBtn);
		buttonPanel.add(insertBtn);
		// add panels
		add(mainPanel);
		mainPanel.add(pdfContainerPanel);
		mainPanel.add(buttonPanel);
		// style
		addStyleName("GeoGebraPopup");
		addStyleName("pdfDialog");
		setGlassEnabled(true);
		setLabels();
	}

	private void buildPdfContainer() {
		pdfContainerPanel.clear();
		pdfPreviewPanel = new FlowPanel();
		pdfPreviewPanel.addStyleName("pdfPreview");
		leftBtn = createButton(pdfPreviewPanel,
				KeyboardResources.INSTANCE.keyboard_arrowLeft_black(), 24,
				"leftBtn");
		// TODO add here the preview image of pdf MOW-424
		rightBtn = createButton(pdfPreviewPanel,
				KeyboardResources.INSTANCE.keyboard_arrowRight_black(), 24,
				"rightBtn");
		pdfPageTextPanel = new FlowPanel();
		pdfPageTextPanel.addStyleName("pdfPageText");
		pdfContainerPanel.add(pdfPreviewPanel);
		pdfContainerPanel.add(pdfPageTextPanel);
	}

	private StandardButton createButton(FlowPanel root, ImageResource imgSource,
			int size, String styleName) {
		StandardButton btn = new StandardButton(
				imgSource, null, size, size, appW);
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
				// only for testing here
				buildPdfContainer();
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
}
