package org.geogebra.web.full.gui.dialog;

import java.util.ArrayList;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.Export3dDialogInterface;
import org.geogebra.common.gui.dialog.TextInputDialog;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.handler.NumberChangeSignInputHandler;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.gui.dialog.handler.RenameInputHandler;
import org.geogebra.common.gui.view.properties.PropertiesView;
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
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.export.PrintPreviewW;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.components.ComponentInputDialog;
import org.geogebra.web.full.gui.dialog.image.ImageDialog;
import org.geogebra.web.full.gui.dialog.image.UploadImagePanel;
import org.geogebra.web.full.gui.dialog.image.WebcamInputDialog;
import org.geogebra.web.full.gui.dialog.template.TemplateChooser;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.full.gui.util.ColorChooserW;
import org.geogebra.web.full.gui.util.SaveDialog;
import org.geogebra.web.full.gui.util.SaveDialogI;
import org.geogebra.web.full.gui.util.SaveUnsavedChangesDialog;
import org.geogebra.web.full.gui.view.data.DataAnalysisViewW;
import org.geogebra.web.full.gui.view.functioninspector.FunctionInspectorW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.LoadingApplication;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ClipboardUtil;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.FileUpload;
import org.gwtproject.user.client.ui.Image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

import elemental2.dom.File;

public class DialogManagerW extends DialogManager
		implements LoadingApplication {
	private FunctionInspectorW functionInspector;
	private RecoverAutoSavedDialog autoSavedDialog;
	protected SaveDialogI saveDialog = null;
	private TemplateChooser templateChooser;
	private GPopupPanel loadingAnimation = null;
	private ColorChooserDialog colChooser = null;
	private CalculatorSwitcherDialog calcSwitcher;

	/**
	 * @param app
	 *            application
	 */
	public DialogManagerW(AppW app) {
		super(app);
	}

	@Override
	public boolean showFunctionInspector(GeoFunction geoFunction) {
		Log.debug("Show Function Inspector");

		boolean success = true;

		try {
			if (functionInspector == null) {
				functionInspector = new FunctionInspectorW((AppW) app,
						geoFunction);
			} else {
				functionInspector.insertGeoElement(geoFunction);
			}

			// show the view
			app.getGuiManager().setShowView(true,
					App.VIEW_FUNCTION_INSPECTOR);
			functionInspector.setInspectorVisible(true);

		} catch (Exception e) {
			success = false;
			Log.debug(e);
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
		DialogData data = new DialogData("CheckBoxTitle");
		CheckboxCreationDialogW dlg = new CheckboxCreationDialogW((AppW) app,
				data, position, bool);
		dlg.show();
	}

	@Override
	public void showNumberInputDialog(String title, String message,
			String initText, AsyncOperation<GeoNumberValue> callback) {
		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor(), callback, app);
		ComponentInputDialog inputDialog = new NumberInputDialog((AppW) app,
			new DialogData(title), false, true, handler, message,
				initText);
		inputDialog.show();
	}

	/**
	 * shows the {@link RecoverAutoSavedDialog}
	 *
	 * @param appW
	 *            {@link AppWFull}
	 * @param json
	 *            stored JSON
	 */
	public void showRecoverAutoSavedDialog(AppWFull appW, String json) {
		if (autoSavedDialog == null) {
			DialogData data = new DialogData("RecoverUnsaved", "Delete", "Recover");
			autoSavedDialog = new RecoverAutoSavedDialog(appW, data);
			autoSavedDialog.setOnNegativeAction(() -> {
				appW.getFileManager().deleteAutoSavedFile();
				appW.startAutoSave();
			});
			autoSavedDialog.setOnPositiveAction(() -> {
				appW.getFileManager().restoreAutoSavedFile(json);
				appW.getFileManager().deleteAutoSavedFile();
				appW.startAutoSave();
			});
		}
		autoSavedDialog.show();
	}

	@Override
	public void createRedefineDialog(GeoElement geo, String str, InputHandler handler) {
		DialogData data = new DialogData("Redefine");
		ComponentInputDialog redefineInputDialog = new ComponentInputDialog((AppW) app, data,
				false, false, handler, geo.getNameDescription(), str
		);
		redefineInputDialog.show();
	}

	@Override
	public void showNumberInputDialogRegularPolygon(String title,
			EuclidianController ec, GeoPointND geoPoint1, GeoPointND geoPoint2,
			GeoCoordSys2D direction) {
		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		DialogData data = new DialogData(title);
		ComponentInputDialog regularPolyInputDialog = new InputDialogRegularPolygonW((AppW) app,
				data, ec, handler, geoPoint1, geoPoint2, direction);
		regularPolyInputDialog.show();
	}

	@Override
	public void showNumberInputDialogCirclePointRadius(String title,
			GeoPointND geoPoint1, EuclidianView view) {
		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		DialogData data = new DialogData(title);
		ComponentInputDialog circlePointRadiusInputDialog
				= new InputDialogCirclePointRadiusW((AppW) app, data,
				handler, (GeoPoint) geoPoint1, app.getKernel());
		circlePointRadiusInputDialog.show();
	}

	@Override
	public void showAngleInputDialog(String title, String message,
			String initText, AsyncOperation<GeoNumberValue> callback) {
		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor(), callback, app);
		DialogData data = new DialogData(title);
		AngleInputDialogW angleInputDialog = new AngleInputDialogW((AppW) app, message,
				data, initText, handler, true);
		angleInputDialog.show();
	}

	@Override
	public boolean showButtonCreationDialog(int x, int y, boolean textfield) {
		DialogData data = new DialogData(textfield ? "InputBox"
				: "Button.Tool");
		ButtonDialogW buttonDialog = new ButtonDialogW((AppW) app, x, y,
				data, textfield);
		buttonDialog.show();
		return true;
	}

	@Override
	public void showCalcChooser(boolean autoHide) {
		hideCalcChooser(); // remove any previous chooser
		if (calcSwitcher == null) {
			calcSwitcher = new CalculatorSwitcherDialog((AppW) app, autoHide);
		}
		calcSwitcher.buildGUI();
		calcSwitcher.show();
	}

	/**
	 * Hide the calc chooser
	 */
	public void hideCalcChooser() {
		if (calcSwitcher != null) {
			calcSwitcher.hide();
		}
	}

	@Override
	public void closeAll() {
		// do nothing
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

		InputHandler handler = new RenameInputHandler(app, geo, storeUndo);
		DialogData data = new DialogData("Rename");
		ComponentInputDialog renameDialog = new RenameInputDialog((AppW) app, data, false, false,
				handler, app.getLocalization().getPlain("NewNameForA", geo.getNameDescription()),
				initText);
		renameDialog.show();
	}

	/**
	 * @param corner
	 *            {@link GeoPoint}
	 * @param device
	 *            used device
	 */
	public void showImageInputDialog(GeoPoint corner, GDevice device) {
		AppW app = (AppW) this.app;
		if (this.app.isWhiteboardActive()) {
			FileUpload upload = UploadImagePanel.getUploadButton(app, app::imageDropHappened);
			upload.click();
			return;
		}
		DialogData data = new DialogData("Image", "Cancel", null);
		ImageDialog dialog = new ImageDialog(app, data);
		dialog.show();
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
	public void showPDFInputDialog(final File file) {
		GWT.runAsync(new RunAsyncCallback() {

			@Override
			public void onSuccess() {
				LoggerW.loaded("PDF JS");
				DialogData data = new DialogData("pdfDialogTitle", "Cancel", "Insert");
				PDFInputDialog pdfInputDialog = new PDFInputDialog((AppW) app, data);
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
		AudioInputDialog audioInputDialog = new AudioInputDialog((AppW) app);
		audioInputDialog.show();
	}

	@Override
	public void showEmbedDialog() {
		EmbedInputDialog embedDialog = new EmbedInputDialog((AppWFull) app);
		embedDialog.show();
	}

	/**
	 * show export image dialog
	 */
	@Override
	public void showExportImageDialog(String base64Image) {
		DialogData data = new DialogData("exportImage", ClipboardUtil
			.isCopyImageToClipboardAvailable() ? "CopyToClipboard" : null, "Download");
		ExportImageDialog expImgDialog = new ExportImageDialog((AppW) app, data,
				base64Image);
		expImgDialog.show();
	}

	/**
	 * show insert video dialog
	 */
	@Override
	public void showVideoInputDialog() {
		VideoInputDialog videoInputDialog = new VideoInputDialog((AppW) app);
		videoInputDialog.show();
	}

	/**
	 * Show webcam dialog
	 */
	public void showWebcamInputDialog() {
		if (!app.isWhiteboardActive()) {
			return;
		}
		DialogData data = new DialogData("Camera", "Close", "takepicture");
		WebcamInputDialog webcamInputDialog = new WebcamInputDialog((AppW) app, data);
		webcamInputDialog.startVideo();
	}

	@Override
	public void showNumberInputDialog(String title, String message,
			String initText, boolean changingSign, String checkBoxText,
			AsyncOperation<GeoNumberValue> callback) {
		NumberChangeSignInputHandler handler = new NumberChangeSignInputHandler(
				app.getKernel().getAlgebraProcessor(), callback, app);
		DialogData data = new DialogData(title);
		NumberChangeSignInputDialogW extrudeInputDialog = new NumberChangeSignInputDialogW(
				(AppW) app, message, data, initText, handler, changingSign);
		extrudeInputDialog.show();
	}

	/**
	 * Creates a new slider at given location (screen coords).
	 *
	 * @return whether a new slider (number) was create or not
	 */
	@Override
	public boolean showSliderCreationDialog(int x, int y) {
		DialogData data = new DialogData("Slider");
		SliderDialogW sliderDialog = new SliderDialogW((AppW) app, data, x, y);
		sliderDialog.show();
		return true;
	}

	@Override
	public void showNumberInputDialogRotate(String title, GeoPolygon[] polys,
			GeoPointND[] points, GeoElement[] selGeos, EuclidianController ec) {
		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		DialogData data = new DialogData(title);
		InputDialogRotateW rotatePointInputDialog = new InputDialogRotatePointW((AppW) app, data,
				handler, polys, points, selGeos, ec);
		rotatePointInputDialog.show();
	}

	@Override
	public void showNumberInputDialogAngleFixed(String title,
			GeoSegmentND[] segments, GeoPointND[] points, GeoElement[] selGeos,
			EuclidianController ec) {
		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		DialogData data = new DialogData(title);
		InputDialogAngleFixedW angleInputDialog = new InputDialogAngleFixedW((AppW) app,
				data, handler, segments, points, app.getKernel(), ec);
		angleInputDialog.show();
	}

	@Override
	public void showNumberInputDialogDilate(String title, GeoPolygon[] polys,
			GeoPointND[] points, GeoElement[] selGeos, EuclidianController ec) {
		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		DialogData data = new DialogData(title);
		InputDialogDilateW dilateInputDialog = new InputDialogDilateW((AppW) app, data, handler,
				points, selGeos, app.getKernel(), ec);
		dilateInputDialog.show();
	}

	@Override
	public void showNumberInputDialogSegmentFixed(String title,
			GeoPointND geoPoint1) {
		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		DialogData data = new DialogData(title);
		ComponentInputDialog segmentInputDialog = new InputDialogSegmentFixedW((AppW) app, data,
				handler, geoPoint1, app.getKernel());
		segmentInputDialog.show();
	}

	/**
	 * @param addTempCheckBox
	 *        true if template checkbox should be visible
	 * @return {@link SaveDialogI}
	 */
	public SaveDialogI getSaveDialog(boolean addTempCheckBox) {
		DialogData data = getSaveDialogData();
		saveDialog = new SaveDialog((AppW) app, data, addTempCheckBox);
		saveDialog.setSaveType(
				app.isWhiteboardActive() ? MaterialType.ggs : MaterialType.ggb);

		return saveDialog;
	}

	/**
	 * @return The "do you want to save" dialog, 
	 *     which does not have the input panel visible when offline
	 */
	public SaveDialogI getSaveCheckDialog() {
		DialogData data = new DialogData("DoYouWantToSaveYourChanges",
					"Discard", "Save");

		return new SaveUnsavedChangesDialog((AppW) app, data, true);
	}

	public DialogData getSaveDialogData() {
		return new DialogData(getSaveDialogTitle(), "Cancel", "Save");
	}

	private String getSaveDialogTitle() {
		if (isSuite()) {
			return app.getLocalization().getPlain("saveDialog.saveApp",
					app.getLocalization().getMenu(app.getConfig().getAppNameWithoutCalc()));
		}
		return "Save";
	}

	private boolean isSuite() {
		return app.getConfig().getAppCode().equals(GeoGebraConstants.SUITE_APPCODE);
	}

	@Override
	public void showSaveDialog() {
		if (saveDialog == null || !saveDialog.isShowing()) {
			getSaveDialog(app.isWhiteboardActive()).show();
		}
	}

	@Override
	public void showPropertiesDialog(OptionType type,
			ArrayList<GeoElement> geos) {
		if (!app.letShowPropertiesDialog()
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
				// TODO  propPanel.showSliderTab()
				subType = 2;
			}
		}

		// set properties option type
		if (type != null) {
			Log.debug("Viewing optionsPanel subtype " + subType);
			pv.setOptionPanel(type, subType);
		}

		// show the view
		if (app.getConfig().getVersion() == GeoGebraConstants.Version.SCIENTIFIC) {
			((GuiManagerW) app.getGuiManager()).showSciSettingsView();
		} else if (app.isUnbundledOrWhiteboard()) {
			((PropertiesViewW) pv).open();
		} else {
			app.getGuiManager().setShowView(true,
					App.VIEW_PROPERTIES);
		}
	}

	@Override
	protected boolean isPropertiesViewShowing() {
		return super.isPropertiesViewShowing()
				|| isFloatingPropertiesViewShowing()
				|| ((AppWFull) app).getAppletFrame().isSciSettingsOpen();
	}

	private boolean isFloatingPropertiesViewShowing() {
		return app.getGuiManager() != null
				&& ((GuiManagerW) app.getGuiManager()).isPropertiesViewShowing();
	}

	@Override
	protected void hidePropertiesView() {
		if (app.getConfig().getVersion() == GeoGebraConstants.Version.SCIENTIFIC) {
			((AppWFull) app).getAppletFrame().hidePanel(null);
			((AppWFull) app).onBrowserClose();
		} else if (app.isUnbundledOrWhiteboard()) {
			PropertiesView pv = ((GuiManagerW) app.getGuiManager())
					.getPropertiesView(OptionType.OBJECTS);
			((PropertiesViewW) pv).close();
		} else {
			app.getGuiManager().setShowView(false,
					App.VIEW_PROPERTIES);
		}
	}

	@Override
	public void openToolHelp() {
		// only desktop
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

	private GPopupPanel createLoadingAnimation() {
		AppW appw = (AppW) app;
		GPopupPanel anim = new GPopupPanel(appw.getAppletFrame(), app);
		anim.addStyleName("loadinganimation");
		anim.add(new Image(GuiResourcesSimple.INSTANCE.getGeoGebraWebSpinner()));
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
		if (textInputDialog != null) {
			((TextInputDialogW) textInputDialog).setLabels();
		}
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
		DialogData data = new DialogData("ChooseColor", "Cancel", "OK");
		if (colChooser == null) {
			colChooser = new ColorChooserDialog((AppW) app, data, originalColor, handler);
		} else {
			// we want to preserve the used colors panel,
			// but also make sure that the language is updated
			ColorChooserW colorChooserPanel = colChooser.getColorChooserPanel();
			colChooser = new ColorChooserDialog((AppW) app, data, originalColor, handler,
					colorChooserPanel);
		}
		colChooser.show();
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
		return new TextInputDialogW((AppW) app, app.getLocalization().getMenu("Text"),
				text, startPoint, rw,
				app.getMode() == EuclidianConstants.MODE_TEXT);
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
			DialogData data = new DialogData("PrintPreview", "Cancel", "Print");
			new PrintPreviewW((AppW) app, data).show();
		}
	}

	@Override
	public void openTableViewDialog(GeoElement geo) {
		DialogData data = new DialogData("TableOfValues");
		InputDialogTableView tableViewDialog = new InputDialogTableView((AppW) app, data);
		tableViewDialog.show(geo);
	}

	@Override
	public Export3dDialogInterface getExport3dDialog(View view) {
		DialogData data = new DialogData("DownloadAsStl", "Cancel", "Download");
		return new Export3dDialog((AppW) app, data, view);
	}

	@Override
	public void showTemplateChooser() {
		DialogData data = new DialogData("New.Mebis", "Cancel", "Create");
		templateChooser = new TemplateChooser((AppW) app, data,
				((GuiManagerW) ((AppW) app).getGuiManager()).getTemplateController());
		templateChooser.show();
	}

	@Override
	public void closeTemplateChooser() {
		templateChooser.hide();
	}

	/**
	 * @return session expire dialog listener
	 */
	public GTimerListener getSessionExpireDialog() {
		return () -> {
			DialogData data = new DialogData(null, "Cancel", "Save");
			new SessionExpireNotifyDialog((AppW) app, data).show();
		};
	}
}
