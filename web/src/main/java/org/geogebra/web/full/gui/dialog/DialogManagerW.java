package org.geogebra.web.full.gui.dialog;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.Export3dDialogInterface;
import org.geogebra.common.gui.dialog.InputDialog;
import org.geogebra.common.gui.dialog.TextInputDialog;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.handler.NumberChangeSignInputHandler;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.gui.dialog.handler.RenameInputHandler;
import org.geogebra.common.gui.view.properties.PropertiesView;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.export.AnimationExportDialogW;
import org.geogebra.web.full.export.PrintPreviewW;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.dialog.image.UploadImageDialog;
import org.geogebra.web.full.gui.dialog.image.WebcamInputDialog;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.full.gui.util.SaveDialogI;
import org.geogebra.web.full.gui.util.SaveDialogMow;
import org.geogebra.web.full.gui.util.SaveDialogW;
import org.geogebra.web.full.gui.view.data.DataAnalysisViewW;
import org.geogebra.web.full.gui.view.functioninspector.FunctionInspectorW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.BrowserDevice;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.full.move.googledrive.events.GoogleLoginEvent;
import org.geogebra.web.full.move.googledrive.operations.GoogleDriveOperationW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.GDialogBox;
import org.geogebra.web.html5.gui.LoadingApplication;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.debug.LoggerW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public class DialogManagerW extends DialogManager
		implements EventRenderable, LoadingApplication {

	private FunctionInspectorW functionInspector;
	protected SaveDialogI saveDialog = null;
	protected UploadImageDialog imageDialog;
	protected WebcamInputDialog webcamInputDialog;
	private RecoverAutoSavedDialog autoSavedDialog;
	private AudioInputDialog audioInputDialog;
	private PDFInputDialog pdfInputDialog;
	private PopupPanel loadingAnimation = null;
	private ColorChooserDialog dialog = null;
	private InputDialogTableView tableViewDialog = null;
	private BaseWidgetFactory widgetFactory = new BaseWidgetFactory();

	/**
	 * @param app
	 *            application
	 */
	public DialogManagerW(AppW app) {
		super(app);
		if (app.getGoogleDriveOperation() != null) {
			((GoogleDriveOperationW) app.getGoogleDriveOperation()).getView()
					.add(this);
		}
	}

	@Override
	public boolean showFunctionInspector(GeoFunction geoFunction) {
		Log.debug("Show Function Inspector");

		boolean success = true;

		try {
			if (functionInspector == null) {
				functionInspector = new FunctionInspectorW(((AppW) app),
						geoFunction);
			} else {
				functionInspector.insertGeoElement(geoFunction);
			}

			// show the view
			((GuiManagerW) app.getGuiManager()).setShowView(true,
					App.VIEW_FUNCTION_INSPECTOR);
			functionInspector.setInspectorVisible(true);

		} catch (Exception e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}

	@Override
	public void showPropertiesDialog(ArrayList<GeoElement> geos) {
		showPropertiesDialog(OptionType.OBJECTS, geos);
	}

	@Override
	public void showBooleanCheckboxCreationDialog(GPoint position,
			GeoBoolean bool) {
		CheckboxCreationDialogW dlg = new CheckboxCreationDialogW((AppW) app,
				position, bool);
		dlg.show();
	}

	@Override
	public void showNumberInputDialog(String title, String message,
			String initText, AsyncOperation<GeoNumberValue> callback) {
		// avoid labeling of num
		final Construction cons = app.getKernel().getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor(), callback, app, oldVal);
		InputDialogW id = new InputDialogW(((AppW) app), message, title,
				initText, false, handler, true, false) {
			@Override
			protected void cancel() {
				cons.setSuppressLabelCreation(false);
				super.cancel();
			}
		};
		id.setVisible(true);
	}

	/**
	 * shows the {@link RecoverAutoSavedDialog}
	 *
	 * @param app2
	 *            {@link AppWFull}
	 * @param json
	 *            stored JSON
	 */
	public void showRecoverAutoSavedDialog(AppWFull app2, String json) {
		if (this.autoSavedDialog == null) {
			this.autoSavedDialog = new RecoverAutoSavedDialog(app2);
		}
		this.autoSavedDialog.setJSON(json);
		this.autoSavedDialog.show();
	}

	@Override
	public void showNumberInputDialogRegularPolygon(String title,
			EuclidianController ec, GeoPointND geoPoint1, GeoPointND geoPoint2,
			GeoCoordSys2D direction) {

		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		InputDialogW id = new InputDialogRegularPolygonW(((AppW) app), ec,
				title, handler, geoPoint1, geoPoint2, direction);
		id.setVisible(true);

	}

	@Override
	public void showNumberInputDialogCirclePointRadius(String title,
			GeoPointND geoPoint1, EuclidianView view) {

		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		InputDialogW id = new InputDialogCirclePointRadiusW(((AppW) app), title,
				handler, (GeoPoint) geoPoint1, app.getKernel());
		id.setVisible(true);

	}

	@Override
	public void showAngleInputDialog(String title, String message,
			String initText, AsyncOperation<GeoNumberValue> callback) {

		// avoid labeling of num
		Construction cons = app.getKernel().getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor(), callback, app, oldVal);
		AngleInputDialogW id = new AngleInputDialogW(((AppW) app), message,
				title, initText, false, handler, true);
		id.setVisible(true);
	}

	@Override
	public boolean showButtonCreationDialog(int x, int y, boolean textfield) {
		ButtonDialogW buttonDialog = new ButtonDialogW(((AppW) app), x, y,
				textfield);
		buttonDialog.setVisible(true);
		return true;
	}

	@Override
	public void closeAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showRenameDialog(GeoElement geo, boolean storeUndo,
			String initText, boolean selectInitText) {
		if (!app.isRightClickEnabled()) {
			return;
		}

		if (app.isUnbundled()) {
			if (app.getActiveEuclidianView().getDynamicStyleBar().isVisible()) {
				return;
			}
		}
		geo.setLabelVisible(true);
		geo.updateRepaint();
		if (app.getGuiManager() != null) {
			app.getGuiManager().clearInputbar();
		}
		InputHandler handler = new RenameInputHandler(app, geo, storeUndo);

		InputDialogW id = new InputDialogW((AppW) app, app.getLocalization()
				.getPlain("NewNameForA", geo.getNameDescription()),
				loc.getMenu("Rename"), initText, false, handler, false,
				selectInitText);

		id.setVisible(true);
	}

	/**
	 * @param corner
	 *            {@link GeoPoint}
	 * @param device
	 *            used device
	 */
	public void showImageInputDialog(GeoPoint corner, GDevice device) {
		if (app.isWhiteboardActive()
				&& device instanceof BrowserDevice) {
			((BrowserDevice) device).getUploadImageWithoutDialog((AppW) app);
			return;
		}
		if (this.imageDialog == null) {
			this.imageDialog = device.getImageInputDialog((AppW) app);
		}
		imageDialog.setLocation(corner);
		imageDialog.center();
		imageDialog.show();
	}

	/**
	 * show insert pdf dialog
	 */
	@Override
	public void showPDFInputDialog() {
		showPDFInputDialog(null);
	}

	/**
	 * show insert pdf dialog
	 *
	 * @param file
	 *            PDF file
	 */
	public void showPDFInputDialog(final JavaScriptObject file) {
		GWT.runAsync(new RunAsyncCallback() {

			@Override
			public void onSuccess() {
				LoggerW.loaded("PDF JS");
				pdfInputDialog = new PDFInputDialog((AppW) app);
				pdfInputDialog.center();
				pdfInputDialog.show();
				if (file != null) {
					pdfInputDialog.loadPdf(file);
				}
			}

			@Override
			public void onFailure(Throwable reason) {
				// handle failure
			}
		});
	}

	/**
	 * show insert audio dialog
	 */
	@Override
	public void showAudioInputDialog() {
		this.audioInputDialog = new AudioInputDialog((AppW) app);
		audioInputDialog.center();
		audioInputDialog.show();
	}

	@Override
	public void showEmbedDialog() {
		EmbedInputDialog embedDialog = new EmbedInputDialog((AppW) app,
				app.getLoginOperation().getGeoGebraTubeAPI().getURLChecker());
		embedDialog.center();
		embedDialog.show();
	}

	/**
	 * show export image dialog
	 */
	@Override
	public void showExportImageDialog(String base64Image) {
		ExportImageDialog expImgDialog = new ExportImageDialog((AppW) app,
				base64Image);
		expImgDialog.center();
		expImgDialog.show();
	}

	/**
	 * show insert video dialog
	 */
	@Override
	public void showVideoInputDialog() {
		VideoInputDialog videoInputDialog = new VideoInputDialog((AppW) app);
		videoInputDialog.center();
		videoInputDialog.show();
	}

	/**
	 * @param device
	 *            device type
	 */
	public void showWebcamInputDialog(GDevice device) {
		if (!(app.isWhiteboardActive()
				&& device instanceof BrowserDevice)) {
			return;
		}
		if (this.webcamInputDialog == null) {
			this.webcamInputDialog = ((BrowserDevice) device)
					.getWebcamInputDialog((AppW) app);
		} else {
			webcamInputDialog.startVideo();
		}
	}

	@Override
	public void showNumberInputDialog(String title, String message,
			String initText, boolean changingSign, String checkBoxText,
			AsyncOperation<GeoNumberValue> callback) {
		boolean oldVal = app.getKernel().getConstruction()
				.isSuppressLabelsActive();
		// avoid labeling of num
		NumberChangeSignInputHandler handler = new NumberChangeSignInputHandler(
				app.getKernel().getAlgebraProcessor(), callback, app, oldVal);
		NumberChangeSignInputDialogW id = new NumberChangeSignInputDialogW(
				((AppW) app), message, title, initText, handler, changingSign,
				checkBoxText);
		id.setVisible(true);

	}

	/**
	 * Creates a new slider at given location (screen coords).
	 *
	 * @return whether a new slider (number) was create or not
	 */
	@Override
	public boolean showSliderCreationDialog(int x, int y) {
		app.setWaitCursor();

		SliderDialogW sliderDialog = new SliderDialogW(((AppW) app), x, y);
		sliderDialog.center();

		app.setDefaultCursor();

		return true;
	}

	@Override
	public void showNumberInputDialogRotate(String title, GeoPolygon[] polys,
			GeoPointND[] points, GeoElement[] selGeos, EuclidianController ec) {

		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		InputDialogRotateW id = new InputDialogRotatePointW(((AppW) app), title,
				handler, polys, points, selGeos, ec);
		id.setVisible(true);

	}

	@Override
	public void showNumberInputDialogAngleFixed(String title,
			GeoSegmentND[] segments, GeoPointND[] points, GeoElement[] selGeos,
			EuclidianController ec) {

		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		InputDialogAngleFixedW id = new InputDialogAngleFixedW(((AppW) app),
				title, handler, segments, points, app.getKernel(), ec);
		id.setVisible(true);

	}

	@Override
	public void showNumberInputDialogDilate(String title, GeoPolygon[] polys,
			GeoPointND[] points, GeoElement[] selGeos, EuclidianController ec) {

		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		InputDialogW id = new InputDialogDilateW(((AppW) app), title, handler,
				points, selGeos, app.getKernel(), ec);
		id.setVisible(true);

	}

	@Override
	public void showNumberInputDialogSegmentFixed(String title,
			GeoPointND geoPoint1) {

		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		InputDialogW id = new InputDialogSegmentFixedW(((AppW) app), title,
				handler, geoPoint1, app.getKernel());
		id.setVisible(true);

	}

	/**
	 *
	 * @return {@link SaveDialogI}
	 */
	public SaveDialogI getSaveDialog() {
		if (saveDialog == null) {
			saveDialog = app.isMebis()
					? new SaveDialogMow((AppW) app)
					: new SaveDialogW((AppW) app, widgetFactory);
		}
		// set default saveType
		saveDialog.setSaveType(
				app.isWhiteboardActive() ? MaterialType.ggs : MaterialType.ggb);
		return saveDialog;
	}

	/**
	 * shows the {@link SaveDialogW} centered on the screen
	 */
	public void showSaveDialog() {
		getSaveDialog().show();
	}

	@Override
	public void showPropertiesDialog(OptionType type,
			ArrayList<GeoElement> geos) {
		if (!((AppW) app).letShowPropertiesDialog()
				|| app.getGuiManager() == null) {
			return;
		}

		// get PropertiesView
		PropertiesView pv = ((GuiManagerW) app.getGuiManager())
				.getPropertiesView(type);
		int subType = -1;
		// select geos
		if (geos != null) {
			if (app.getSelectionManager().getSelectedGeos().size() == 0) {
				app.getSelectionManager().addSelectedGeos(geos, true);
			}

			if (geos.size() == 1 && geos.get(0).isEuclidianVisible()
					&& geos.get(0) instanceof GeoNumeric) {
				// AbstractApplication.debug("TODO :
				// propPanel.showSliderTab()");
				subType = 2;
			}
		}

		// set properties option type
		if (type != null) {
			Log.debug("Viewing optionsPanel subtype " + subType);
			pv.setOptionPanel(type, subType);
		}

		// show the view
		if (app.isUnbundledOrWhiteboard()) {
			((PropertiesViewW) pv).open();
		} else {
			((GuiManagerW) app.getGuiManager()).setShowView(true,
					App.VIEW_PROPERTIES);

		}

	}

	@Override
	public void openToolHelp() {
		int mode = app.getMode();
		ToolTipManagerW.sharedInstance().showBottomInfoToolTip(
				app.getToolTooltipHTML(mode),
				((AppW) app).getGuiManager().getTooltipURL(mode),
				ToolTipLinkType.Help, (AppW) app,
				((AppW) app).getAppletFrame().isKeyboardShowing());
	}

	@Override
	public void showDataSourceDialog(int mode, boolean doAutoLoadSelectedGeos) {
		if (mode == EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS
				|| mode == EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS
				|| mode == EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS) {

			Log.debug("[DAMODE] about to show mode " + mode);
			DataAnalysisViewW da = (DataAnalysisViewW) app.getGuiManager()
					.getDataAnalysisView();
			da.changeMode(mode);
			app.getGuiManager().setShowView(true, App.VIEW_DATA_ANALYSIS);
		}
	}

	/**
	 * Shows alert dialog.
	 *
	 * @param text
	 *            Alert message
	 */
	public void showAlertDialog(String text) {
		((AppW) app).getGuiManager().getOptionPane().showConfirmDialog(
				text, "", GOptionPane.OK_OPTION,
				GOptionPane.INFORMATION_MESSAGE, null);
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof GoogleLoginEvent) {
			if (!((GoogleLoginEvent) event).isSuccessFull()) {
				Log.debug("Login to Google failed");
			}
		}

	}

	/**
	 * Shows a loading animation
	 */
	@Override
	public void showLoadingAnimation() {
		if (loadingAnimation == null) {
			loadingAnimation = createLoadingAnimation();
		}
		loadingAnimation.center();
		loadingAnimation.show();
	}

	private static PopupPanel createLoadingAnimation() {
		PopupPanel anim = new PopupPanel();
		anim.addStyleName("loadinganimation");
		anim.add(
				new Image(GuiResourcesSimple.INSTANCE.getGeoGebraWebSpinner()));
		return anim;
	}

	/**
	 * Hides a loading animation
	 */
	@Override
	public void hideLoadingAnimation() {
		if (loadingAnimation != null) {
			loadingAnimation.hide();
		}
	}

	/**
	 * Update labels in the GUI.
	 */
	public void setLabels() {

		// if (functionInspector != null)
		// functionInspector.setLabels();

		if (textInputDialog != null) {
			((TextInputDialogW) textInputDialog).setLabels();
		}

		if (saveDialog != null) {
			saveDialog.setLabels();
		}

		if (imageDialog != null) {
			imageDialog.setLabels();
		}

		if (this.autoSavedDialog != null) {
			this.autoSavedDialog.setLabels();
		}
		// if (fileChooser != null)
		// updateJavaUILanguage();

		// if (dataSourceDialog != null)
		// dataSourceDialog.setLabels();

	}

	/**
	 * Creates a new {@link ColorChooserDialog}.
	 *
	 * @param originalColor
	 *            initial color
	 * @param handler
	 *            color change listener
	 */
	public void showColorChooserDialog(GColor originalColor,
			ColorChangeHandler handler) {
		if (dialog == null) {
			dialog = new ColorChooserDialog((AppW) app, originalColor, handler);
		} else {
			dialog.setOriginalColor(originalColor);
			dialog.setHandler(handler);
		}
		dialog.center();
	}

	/**
	 * @return {@link FunctionInspectorW}
	 */
	public FunctionInspectorW getFunctionInspector() {
		return functionInspector;
	}

	@Override
	public TextInputDialog createTextDialog(GeoText text, GeoPointND startPoint,
			boolean rw) {
		return new TextInputDialogW(app, loc.getMenu("Text"), text, startPoint,
				rw, 30, 6, app.getMode() == EuclidianConstants.MODE_TEXT);
	}

	@Override
	public InputDialog newInputDialog(App app1, String message, String title,
			String initString, InputHandler handler, GeoElement geo) {
		return new InputDialogW((AppW) app1, message, title, initString,
				handler, geo);
	}

	/**
	 * Show animation export dialog.
	 */
	public void showAnimGifExportDialog() {
		GDialogBox gifDialog = new AnimationExportDialogW((AppW) app);
		gifDialog.center();
		gifDialog.show();
	}

	/**
	 * Open print preview
	 */
	@Override
	public void showPrintPreview() {
		if (app.getGuiManager().showView(App.VIEW_EUCLIDIAN)
				|| app.getGuiManager().showView(App.VIEW_EUCLIDIAN2)
				|| app.getGuiManager().showView(App.VIEW_ALGEBRA)
				|| app.getGuiManager()
						.showView(App.VIEW_CONSTRUCTION_PROTOCOL)) {
			new PrintPreviewW((AppW) app).show();
		}
	}

	@Override
	public void openTableViewDialog(GeoElement geo) {
		if (tableViewDialog == null) {
			tableViewDialog = new InputDialogTableView((AppW) app);
		}
		tableViewDialog.show(geo);
	}

	@Override
	public Export3dDialogInterface getExport3dDialog(View view) {
		return new Export3dDialog((AppW) app, view);
	}

	public void setWidgetFactory(BaseWidgetFactory widgetFactory) {
		this.widgetFactory = widgetFactory;
	}
}
