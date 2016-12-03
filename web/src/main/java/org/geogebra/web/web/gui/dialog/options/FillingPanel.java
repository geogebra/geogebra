package org.geogebra.web.web.gui.dialog.options;

import java.util.ArrayList;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
//import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.dialog.options.model.FillingModel;
import org.geogebra.common.gui.dialog.options.model.FillingModel.IFillingListener;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.geos.GeoElement.FillType;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.MD5EncrypterGWTImpl;
import org.geogebra.common.util.Util;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.SliderPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.FileInputDialog;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.properties.OptionPanel;
import org.geogebra.web.web.gui.util.BarList;
import org.geogebra.web.web.gui.util.GeoGebraIconW;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.PopupMenuButton;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;

public class FillingPanel extends OptionPanel implements IFillingListener {
	FillingModel model;
	SliderPanel opacitySlider;
	SliderPanel angleSlider;
	SliderPanel distanceSlider;
	private Label fillingSliderTitle;
	private Label angleSliderTitle;
	private Label distanceSliderTitle;

	private FlowPanel opacityPanel, hatchFillPanel, imagePanel, anglePanel,
			distancePanel;
	private Label lblSelectedSymbol;
	private Label lblMsgSelected;
	private Button btnOpenFile;

	private PopupMenuButton btnImage;
	// button for removing turtle's image
	private PushButton btnClearImage;
	private Label lblSymbols;
	ArrayList<ImageResource> iconList;
	private ArrayList<String> iconNameList;
	// private PopupMenuButton btInsertUnicode;

	ListBox lbFillType;
	CheckBox cbFillInverse;
	private FlowPanel mainWidget;
	private FlowPanel fillTypePanel;
	private Label fillTypeTitle;
	private FlowPanel btnPanel;
	AutoCompleteTextFieldW tfInsertUnicode;
	private InputPanelW unicodePanel;
	private AppW app;
	private BarList lbBars;

	private class MyImageFileInputDialog extends FileInputDialog {

		private MyImageFileInputDialog myDialog;

		public MyImageFileInputDialog(AppW app, GeoPoint location) {
			super(app, location);
			createGUI();
		}

		@Override
		protected void createGUI() {
			super.createGUI();
			addGgbChangeHandler(getInputWidget().getElement(), app);
		}

		public native void addGgbChangeHandler(Element el, AppW appl) /*-{
																		var dialog = this;
																		appl = this;
																		el.setAttribute("accept", "image/*");
																		el.onchange = function(event) {
																		var files = this.files;
																		if (files.length) {
																		var fileTypes = /^image.*$/;
																		for (var i = 0, j = files.length; i < j; ++i) {
																		if (!files[i].type.match(fileTypes)) {
																		continue;
																		}
																		var fileToHandle = files[i];
																		appl.@org.geogebra.web.web.gui.dialog.options.FillingPanel.MyImageFileInputDialog::openFileAsImage(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(fileToHandle,
																		dialog.@org.geogebra.web.web.gui.dialog.FileInputDialog::getNativeHideAndFocus()());				
																		break
																		}
																		}
																		};
																		}-*/;

		@Override
		public void onClick(ClickEvent event) {
			if (event.getSource() == btCancel) {
				hideAndFocus();
			}
		}

		public native boolean openFileAsImage(JavaScriptObject fileToHandle,
				JavaScriptObject callback) /*-{

			var imageRegEx = /\.(png|jpg|jpeg|gif|bmp|svg)$/i;
			if (!fileToHandle.name.toLowerCase().match(imageRegEx))
				return false;

			var appl = this;
			var reader = new FileReader();
			reader.onloadend = function(ev) {
				if (reader.readyState === reader.DONE) {
					var fileData = reader.result;
					var fileName = fileToHandle.name;
					appl.@org.geogebra.web.web.gui.dialog.options.FillingPanel.MyImageFileInputDialog::applyImage(Ljava/lang/String;Ljava/lang/String;)(fileName, fileData);
					if (callback != null) {
						callback();
					}
				}
			};
			reader.readAsDataURL(fileToHandle);
			return true;
		}-*/;

		public void applyImage(String fileName, String fileData) {
			MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
			String zip_directory = md5e.encrypt(fileData);

			String fn = fileName;
			int index = fn.lastIndexOf('/');
			if (index != -1) {
				fn = fn.substring(index + 1, fn.length()); // filename without
			}
			// path
			fn = Util.processFilename(fn);

			// filename will be of form
			// "a04c62e6a065b47476607ac815d022cc\liar.gif"
			fn = zip_directory + '/' + fn;

			Construction cons = app.getKernel().getConstruction();
			app.getImageManager().addExternalImage(fn, fileData);
			GeoImage geoImage = new GeoImage(cons);
			app.getImageManager().triggerSingleImageLoading(fn, geoImage);
			model.applyImage(fn);
			Log.debug("Applying " + fn + " from dialog");

		}

	}

	public FillingPanel(FillingModel model0, AppW app) {
		this.app = app;
		model = model0;
		model.setListener(this);
		setModel(model);
		mainWidget = new FlowPanel();
		fillTypePanel = new FlowPanel();
		fillTypePanel.setStyleName("optionsPanel");
		fillTypeTitle = new Label();
		lbFillType = new ListBox();

		fillTypePanel.add(fillTypeTitle);
		fillTypePanel.add(lbFillType);

		cbFillInverse = new CheckBox();
		fillTypePanel.add(cbFillInverse);
		lbFillType.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				model.applyFillType(lbFillType.getSelectedIndex());
			}
		});

		cbFillInverse.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				model.applyFillingInverse(cbFillInverse.getValue());
			}
		});

		FlowPanel panel = new FlowPanel();
		panel.add(fillTypePanel);

		unicodePanel = new InputPanelW(null, app, 1, -1, true);
		// buildInsertUnicodeButton();
		unicodePanel.setVisible(false);
		tfInsertUnicode = unicodePanel.getTextComponent();
		tfInsertUnicode.setAutoComplete(false);

		tfInsertUnicode.addStyleName("fillSymbol");
		lblMsgSelected = new Label(app.getLocalization().getMenu(
				"Filling.CurrentSymbol")
				+ ":");
		lblMsgSelected.setVisible(false);
		lblSymbols = new Label(app.getMenu("Filling.Symbol") + ":");
		lblSymbols.setVisible(false);
		lblSelectedSymbol = new Label();

		opacitySlider = new SliderPanel(0, 100);
		opacitySlider.setMajorTickSpacing(25);
		opacitySlider.setMinorTickSpacing(5);
		opacitySlider.setPaintTicks(true);
		opacitySlider.setPaintLabels(true);

		angleSlider = new SliderPanel(0, 180);
		angleSlider.setMajorTickSpacing(45);
		angleSlider.setMinorTickSpacing(5);
		angleSlider.setPaintTicks(true);
		angleSlider.setPaintLabels(true);

		distanceSlider = new SliderPanel(5, 50);
		// distanceSlider.setPreferredSize(new Dimension(150,50));
		distanceSlider.setMajorTickSpacing(10);
		distanceSlider.setMinorTickSpacing(5);
		distanceSlider.setPaintTicks(true);
		distanceSlider.setPaintLabels(true);

		FlowPanel symbol1Panel = new FlowPanel();
		symbol1Panel.setStyleName("optionsPanelCell");
		symbol1Panel.add(lblSymbols);
		symbol1Panel.add(tfInsertUnicode);
		FlowPanel symbol2Panel = new FlowPanel();
		symbol2Panel.setStyleName("optionsPanelCell");
		symbol2Panel.add(lblMsgSelected);
		symbol2Panel.add(lblSelectedSymbol);

		FlowPanel symbolPanel = new FlowPanel();
		symbolPanel.setStyleName("optionsPanelIndent");
		symbolPanel.add(symbol1Panel);
		symbolPanel.add(symbol2Panel);
		lblSelectedSymbol.setVisible(false);
		panel.add(symbolPanel);
		// panels to hold sliders
		opacityPanel = new FlowPanel();
		opacityPanel.setStyleName("optionsPanelIndent");
		fillingSliderTitle = new Label();
		opacityPanel.add(fillingSliderTitle);
		opacityPanel.add(opacitySlider);

		anglePanel = new FlowPanel();
		anglePanel.setStyleName("optionsPanelIndent");
		angleSliderTitle = new Label();
		anglePanel.add(angleSliderTitle);
		anglePanel.add(angleSlider);

		distanceSliderTitle = new Label();
		distancePanel = new FlowPanel();
		distancePanel.setStyleName("optionsPanelIndent");
		distancePanel.add(distanceSliderTitle);
		distancePanel.add(distanceSlider);

		// hatchfill panel: only shown when hatch fill option is selected
		hatchFillPanel = new FlowPanel();
		hatchFillPanel.add(anglePanel);
		hatchFillPanel.add(distancePanel);
		hatchFillPanel.setVisible(false);

		// image panel: only shown when image fill option is selected
		createImagePanel();
		imagePanel.setVisible(false);

		// ===========================================================
		// put all the sub panels together

		mainWidget.add(panel);
		mainWidget.add(opacityPanel);
		mainWidget.add(hatchFillPanel);
		mainWidget.add(imagePanel);

		mainWidget.add(symbolPanel);

		lbBars = new BarList(app);

		lbBars.setVisible(false);

		fillTypePanel.add(lbBars);

		lbBars.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				model.updateProperties();
			}
		});

		setWidget(mainWidget);

		opacitySlider.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				model.applyOpacity(opacitySlider.getValue());
			}
		});

		ChangeHandler angleAndDistanceHandler = new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				model.applyAngleAndDistance(angleSlider.getValue(),
						distanceSlider.getValue());

			}
		};

		angleSlider.addChangeHandler(angleAndDistanceHandler);
		distanceSlider.addChangeHandler(angleAndDistanceHandler);

		tfInsertUnicode.addFocusListener(new FocusListenerW(this) {
			@Override
			protected void wrapFocusLost() {
				String symbolText = tfInsertUnicode.getText();
				if (symbolText.isEmpty()) {
					return;
				}
				selectSymbol(symbolText);
				model.applyUnicode(symbolText);
			}
		});

		tfInsertUnicode.addKeyHandler(new KeyHandler() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					String symbolText = tfInsertUnicode.getText();
					selectSymbol(symbolText);
					model.applyUnicode(symbolText);

				}
			}
		});

		setLabels();
	}

	protected String getImageFileName(String fileName, String fileData) {

		MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
		String zip_directory = md5e.encrypt(fileData);

		String fn = fileName;
		int index = fileName.lastIndexOf('/');
		if (index != -1) {
			fn = fn.substring(index + 1, fn.length()); // filename without
		}
		fn = Util.processFilename(fn);

		// filename will be of form
		// "a04c62e6a065b47476607ac815d022cc\liar.gif"
		return zip_directory + '/' + fn;
	}

	public void applyImage(String fileName0, String fileData) {

		String fileName = getImageFileName(fileName0, fileData);

		Construction cons = app.getKernel().getConstruction();
		app.getImageManager().addExternalImage(fileName, fileData);
		GeoImage geoImage = new GeoImage(cons);
		app.getImageManager().triggerSingleImageLoading(fileName, geoImage);
		model.applyImage(fileName);

	}

	private void createImagePanel() {
		imagePanel = new FlowPanel();
		btnPanel = new FlowPanel();
		iconList = new ArrayList<ImageResource>();
		iconList.add(null); // for delete
		GuiResourcesSimple res = GuiResourcesSimple.INSTANCE;
		iconList.add(res.icons_fillings_arrow_big_down());
		iconList.add(res.icons_fillings_arrow_big_up());
		iconList.add(res.icons_fillings_arrow_big_left());
		iconList.add(res.icons_fillings_arrow_big_right());
		iconList.add(res.icons_fillings_fastforward());
		iconList.add(res.icons_fillings_rewind());
		iconList.add(res.icons_fillings_skipback());
		iconList.add(res.icons_fillings_skipforward());
		iconList.add(res.icons_fillings_play());
		iconList.add(res.icons_fillings_pause());

		iconList.add(res.icons_fillings_cancel());

		iconNameList = new ArrayList<String>();
		for (ImageResource ir : iconList) {

			iconNameList.add(ir != null ? ir.getName() : "");
		}

		final ImageOrText[] iconArray = new ImageOrText[iconList.size()];
		iconArray[0] = GeoGebraIconW.createNullSymbolIcon(24, 24);
		for (int i = 1; i < iconArray.length; i++) {
			iconArray[i] = new ImageOrText(iconList.get(i));
		}
		// // ============================================
		//
		// // panel for button to open external file
		//
		btnImage = new PopupMenuButton(app, iconArray, -1, 4,
				SelectionTable.MODE_ICON) {
			@Override
			public void handlePopupActionEvent() {
				super.handlePopupActionEvent();
				ImageResource resource = null;
				int idx = getSelectedIndex();
				resource = iconList.get(idx);
				if (resource != null) {
					applyImage(resource.getName(), resource.getSafeUri()
							.asString());
					Log.debug("Applying " + resource.getName() + " at index "
							+ idx);
				} else {
					model.applyImage("");
				}
			}

		};
		btnImage.setSelectedIndex(-1);
		btnImage.setKeepVisible(false);
		btnClearImage = new PushButton(new Image(
				AppResources.INSTANCE.delete_small()));
		btnClearImage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				model.applyImage("");
			}

		});
		btnOpenFile = new Button();
		btnOpenFile.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				new MyImageFileInputDialog(app, null);
			}
		});

		btnPanel.add(btnImage);
		btnPanel.add(btnClearImage);
		btnPanel.add(btnOpenFile);
		btnPanel.setStyleName("optionsPanelIndent");

		imagePanel.add(btnPanel);

	}

	@Override
	public void setStandardFillType() {
		fillTypePanel.setVisible(true);
		opacityPanel.setVisible(false);
		hatchFillPanel.setVisible(false);
		imagePanel.setVisible(false);
		setSymbolsVisible(false);
	}

	@Override
	public void setHatchFillType() {
		fillTypePanel.setVisible(true);
		distanceSlider.setMinimum(5);
		opacityPanel.setVisible(false);
		hatchFillPanel.setVisible(true);
		imagePanel.setVisible(false);
		anglePanel.setVisible(true);
		angleSlider.setMaximum(180);
		angleSlider.setMinorTickSpacing(5);
		setSymbolsVisible(false);
	}

	@Override
	public void setCrossHatchedFillType() {
		fillTypePanel.setVisible(true);
		distanceSlider.setMinimum(5);
		opacityPanel.setVisible(false);
		hatchFillPanel.setVisible(true);
		imagePanel.setVisible(false);
		anglePanel.setVisible(true);
		// Only at 0, 45 and 90 degrees texturepaint not have mismatches
		angleSlider.setMaximum(45);
		angleSlider.setMinorTickSpacing(45);
		setSymbolsVisible(false);

	}

	@Override
	public void setBrickFillType() {
		fillTypePanel.setVisible(true);
		distanceSlider.setMinimum(5);
		opacityPanel.setVisible(false);
		hatchFillPanel.setVisible(true);
		imagePanel.setVisible(false);
		anglePanel.setVisible(true);
		angleSlider.setMaximum(180);
		angleSlider.setMinorTickSpacing(45);
		setSymbolsVisible(false);
	}

	@Override
	public void setSymbolFillType() {
		fillTypePanel.setVisible(true);
		distanceSlider.setMinimum(10);
		opacityPanel.setVisible(false);
		hatchFillPanel.setVisible(true);
		imagePanel.setVisible(false);
		// for dotted angle is useless
		anglePanel.setVisible(false);
		setSymbolsVisible(true);
	}

	@Override
	public void setDottedFillType() {
		distanceSlider.setMinimum(5);
		opacityPanel.setVisible(false);
		hatchFillPanel.setVisible(true);
		imagePanel.setVisible(false);
		// for dotted angle is useless
		anglePanel.setVisible(false);
		setSymbolsVisible(false);
	}

	@Override
	public void setImageFillType() {
		fillTypePanel.setVisible(true);
		opacityPanel.setVisible(true);
		hatchFillPanel.setVisible(false);
		imagePanel.setVisible(true);
		this.btnImage.setVisible(true);
		this.btnClearImage.setVisible(true);

		// for GeoButtons only show the image file button
		if (model.hasGeoButton() || model.hasGeoTurtle()) {
			fillTypePanel.setVisible(false);
			opacityPanel.setVisible(false);
			if (model.hasGeoTurtle()) {
				this.btnImage.setVisible(false);
				this.btnClearImage.setVisible(true);
			}
		}
		setSymbolsVisible(false);


	}

	@Override
	public void setSelectedIndex(int index) {
		lbFillType.setSelectedIndex(index);
	}

	@Override
	public void addItem(String item) {
		lbFillType.addItem(item);
	}

	public void updateFillTypePanel(FillType fillType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFillInverseVisible(boolean isVisible) {
		cbFillInverse.setVisible(isVisible);
	}

	@Override
	public void setFillTypeVisible(boolean isVisible) {
		lbFillType.setVisible(isVisible);
	}

	@Override
	public void setLabels() {
		Localization loc = app.getLocalization();
		fillTypeTitle.setText(loc.getMenu("Filling") + ":");
		cbFillInverse.setText(loc.getPlain("InverseFilling"));
		int idx = lbFillType.getSelectedIndex();
		lbFillType.clear();
		model.fillModes(loc);
		lbFillType.setSelectedIndex(idx);
		fillingSliderTitle.setText(loc.getMenu("Opacity"));
		angleSliderTitle.setText(loc.getMenu("Angle"));
		distanceSliderTitle.setText(loc.getMenu("Spacing"));
		btnOpenFile.setText(loc.getMenu("ChooseFromFile") + "...");
	}

	@Override
	public void setSelectedItem(String item) {
		int idx = 0;
		lbFillType.setSelectedIndex(idx);
	}

	@Override
	public void setSymbolsVisible(boolean isVisible) {

		if (isVisible) {
			unicodePanel.setVisible(true);
			lblSymbols.setVisible(true);
			lblSelectedSymbol.setVisible(true);
			lblMsgSelected.setVisible(true);
		} else {
			lblSymbols.setVisible(false);
			unicodePanel.setVisible(false);
			lblMsgSelected.setVisible(false);
			lblSelectedSymbol.setVisible(false);
			lblSelectedSymbol.setText("");
		}
	}

	@Override
	public void setFillingImage(String imageFileName) {

		int itemIndex = -1;
		if (imageFileName != null) {
			String fileName = imageFileName.substring(imageFileName
					.indexOf('/') + 1);
			Log.debug("Filling with " + fileName);

			int idx = iconNameList.lastIndexOf(fileName);
			itemIndex = idx > 0 ? idx : 0;
		}

		btnImage.setSelectedIndex(itemIndex);

	}

	@Override
	public void setFillValue(int value) {
		opacitySlider.setValue(value);
	}

	@Override
	public void setAngleValue(int value) {
		angleSlider.setValue(value);
	}

	@Override
	public void setDistanceValue(int value) {
		distanceSlider.setValue(value);
	}

	@Override
	public int getSelectedBarIndex() {
		return lbBars.getSelectedIndex();
	}

	@Override
	public void selectSymbol(String symbol) {
		lblSelectedSymbol.setText(symbol);
	}

	@Override
	public String getSelectedSymbolText() {
		return lblSelectedSymbol.getText();
	}

	@Override
	public float getFillingValue() {
		return opacitySlider.getValue();
	}

	@Override
	public FillType getSelectedFillType() {
		return model.getFillTypeAt(lbFillType.getSelectedIndex());
	}

	@Override
	public int getDistanceValue() {
		return distanceSlider.getValue();
	}

	@Override
	public int getAngleValue() {
		return angleSlider.getValue();
	}

	@Override
	public void setFillInverseSelected(boolean value) {
		cbFillInverse.setValue(value);
	}

	public void clearItems() {
		// TODO Auto-generated method stub

	}

	public void setBarChart(AlgoBarChart algo) {
		if (algo != null) {
			lbBars.setBarCount(algo.getIntervals());
		}

		lbBars.update(algo != null);
	}
}