package org.geogebra.web.full.gui.dialog.options;

import java.util.ArrayList;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.dialog.options.model.FillingModel;
import org.geogebra.common.gui.dialog.options.model.FillingModel.IFillingListener;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.FileInputDialog;
import org.geogebra.web.full.gui.properties.OptionPanel;
import org.geogebra.web.full.gui.util.BarList;
import org.geogebra.web.full.gui.util.GeoGebraIconW;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.GPushButton;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.SliderPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ImageManagerW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

import elemental2.dom.File;
import elemental2.dom.FileReader;
import jsinterop.base.Js;

public class FillingPanel extends OptionPanel implements IFillingListener {
	FillingModel model;
	SliderPanel opacitySlider;
	SliderPanel angleSlider;
	SliderPanel distanceSlider;
	private Label fillingSliderTitle;
	private Label angleSliderTitle;
	private Label distanceSliderTitle;

	private FlowPanel opacityPanel;
	private FlowPanel hatchFillPanel;
	private FlowPanel imagePanel;
	private FlowPanel anglePanel;
	private Label lblSelectedSymbol;
	private Label lblMsgSelected;
	private Button btnOpenFile;

	private PopupMenuButtonW btnImage;
	// button for removing turtle's image
	private GPushButton btnClearImage;
	private Label lblSymbols;
	ArrayList<SVGResource> iconList;
	private ArrayList<String> iconNameList;

	ListBox lbFillType;
	CheckBox cbFillInverse;
	private FlowPanel fillTypePanel;
	private Label fillTypeTitle;
	private FlowPanel btnPanel;
	AutoCompleteTextFieldW tfInsertUnicode;
	private InputPanelW unicodePanel;
	private AppW app;
	private BarList lbBars;

	private class MyImageFileInputDialog extends FileInputDialog {

		public MyImageFileInputDialog(AppW app) {
			super(app);
			createGUI();
		}

		@Override
		protected void createGUI() {
			super.createGUI();
			addGgbChangeHandler(Js.uncheckedCast(getInputWidget().getElement()));
		}

		public void addGgbChangeHandler(elemental2.dom.HTMLInputElement el) {
			el.setAttribute("accept", "image/*");
			el.onchange = (event) -> {
				for (int i = 0; i < el.files.length; ++i) {
					if (el.files.getAt(i).type.matches("^image.*$")) {
						openFileAsImage(el.files.getAt(i));
						break;
					}
				}

				return null;
			};
		}

		public void openFileAsImage(File fileToHandle) {
			String imageRegEx = ".*\\.(png|jpg|jpeg|gif|bmp|svg)$";
			if (!fileToHandle.name.toLowerCase().matches(imageRegEx)) {
				return;
			}

			FileReader reader = new FileReader();

			reader.addEventListener("load", (ev) -> {
				if (reader.readyState == FileReader.DONE) {
					applyFillImage(fileToHandle.name, reader.result.asString());
					hideAndFocus();
				}
			});

			reader.readAsDataURL(fileToHandle);
		}

		public void applyFillImage(String name, String url) {
			applyImage(name, url);
		}
	}

	/**
	 * @param model0
	 *            model
	 * @param app
	 *            application
	 */
	public FillingPanel(FillingModel model0, AppW app) {
		this.app = app;
		model = model0;
		model.setListener(this);
		setModel(model);

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
		lblSymbols = new Label(
				app.getLocalization().getMenu("Filling.Symbol") + ":");
		lblSymbols.setVisible(false);
		lblSelectedSymbol = new Label();

		opacitySlider = new SliderPanel(0, 100);
		opacitySlider.setTickSpacing(5);

		angleSlider = new SliderPanel(0, 180);
		angleSlider.setTickSpacing(5);

		distanceSlider = new SliderPanel(5, 50);
		// distanceSlider.setPreferredSize(new Dimension(150,50));
		distanceSlider.setTickSpacing(5);

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
		FlowPanel distancePanel = new FlowPanel();
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
		FlowPanel mainWidget = new FlowPanel();
		mainWidget.add(panel);
		mainWidget.add(opacityPanel);
		mainWidget.add(hatchFillPanel);
		mainWidget.add(imagePanel);

		mainWidget.add(symbolPanel);

		lbBars = new BarList(app);

		lbBars.setVisible(false);

		fillTypePanel.add(lbBars);

		lbBars.addChangeHandler(new ChangeHandler() {

			@Override
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

		tfInsertUnicode.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
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

	/**
	 * Apply image uploaded by user.
	 * 
	 * @param fileName0
	 *            image filename
	 * @param fileData
	 *            file content
	 */
	public void applyImage(String fileName0, String fileData) {
		String fileName = ImageManagerW.getMD5FileName(fileName0, fileData);

		Construction cons = app.getKernel().getConstruction();
		app.getImageManager().addExternalImage(fileName, fileData);
		GeoImage geoImage = new GeoImage(cons);
		app.getImageManager().triggerSingleImageLoading(fileName, geoImage);
		model.applyImage(fileName);
	}

	private void createImagePanel() {
		imagePanel = new FlowPanel();
		btnPanel = new FlowPanel();
		iconList = new ArrayList<>();
		iconList.add(null); // for delete
		GuiResourcesSimple res = GuiResourcesSimple.INSTANCE;
		iconList.add(res.pause());
		iconList.add(res.play());
		iconList.add(res.stop());
		iconList.add(res.replay());
		iconList.add(res.skip_next());
		iconList.add(res.skip_previous());
		iconList.add(res.loop());
		iconList.add(res.zoom_in());
		iconList.add(res.zoom_out());
		iconList.add(res.close());
		iconList.add(res.arrow_up());
		iconList.add(res.arrow_down());
		iconList.add(res.arrow_forward());
		iconList.add(res.arrow_back());
		iconList.add(res.fast_forward());
		iconList.add(res.fast_rewind());

		iconNameList = new ArrayList<>();
		for (SVGResource ir : iconList) {
			iconNameList.add(ir != null ? ir.getName() : "");
		}

		final ImageOrText[] iconArray = new ImageOrText[iconList.size()];
		iconArray[0] = GeoGebraIconW.createNullSymbolIcon();
		for (int i = 1; i < iconArray.length; i++) {
			iconArray[i] = new ImageOrText(iconList.get(i), 24);
		}
		// // ============================================
		//
		// // panel for button to open external file
		//
		btnImage = new PopupMenuButtonW(app, iconArray, -1, 4,
				SelectionTable.MODE_ICON) {
			@Override
			public void handlePopupActionEvent() {
				super.handlePopupActionEvent();
				int idx = getSelectedIndex();
				SVGResource resource = iconList.get(idx);
				if (resource != null) {
					applyImage(resource + ".svg", resource.getSafeUri()
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
		btnClearImage = new GPushButton(
				new NoDragImage(MaterialDesignResources.INSTANCE.delete_black(), 24));
		btnClearImage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				model.applyImage("");
			}

		});
		btnOpenFile = new Button();
		btnOpenFile.addStyleName("openFileBtn");
		btnClearImage.addStyleName("clearImgBtn");
		btnOpenFile.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				new MyImageFileInputDialog(app).center();
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
		angleSlider.setTickSpacing(5);
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
		angleSlider.setTickSpacing(45);
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
		angleSlider.setTickSpacing(45);
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
		btnImage.setVisible(model.hasGeoButton());
		btnClearImage.setVisible(true);

		// for GeoButtons only show the image file button
		if (model.hasGeoButton() || model.hasGeoTurtle()) {
			fillTypePanel.setVisible(false);
			opacityPanel.setVisible(false);
			if (model.hasGeoTurtle()) {
				btnClearImage.setVisible(true);
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
		cbFillInverse.setText(loc.getMenu("InverseFilling"));
		int idx = lbFillType.getSelectedIndex();
		lbFillType.clear();
		model.fillModes(loc);
		lbFillType.setSelectedIndex(idx);
		fillingSliderTitle.setText(loc.getMenu("Opacity"));
		angleSliderTitle.setText(loc.getMenu("Angle"));
		distanceSliderTitle.setText(loc.getMenu("Spacing"));
		btnOpenFile.setText(loc.getMenu("ChooseFromFile"));
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
			String fileName = imageFileName.substring(imageFileName.indexOf('/') + 1);

			int idx = iconNameList.lastIndexOf(fileName);
			itemIndex = Math.max(idx, 0);
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
	public double getFillingValue() {
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

	@Override
	public void clearItems() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBarChart(int cols) {
		lbBars.updateTranslationKeys(model.getGeos());
		lbBars.setBarCount(cols);
	}
}