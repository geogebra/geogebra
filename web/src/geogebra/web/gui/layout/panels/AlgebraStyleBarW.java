package geogebra.web.gui.layout.panels;

import geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import geogebra.common.main.App;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.AlgebraSettings;
import geogebra.common.main.settings.SettingListener;
import geogebra.html5.main.AppW;
import geogebra.web.gui.images.StyleBarResources;
import geogebra.web.gui.util.ImageOrText;
import geogebra.web.gui.util.MyToggleButton2;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.gui.util.PopupMenuHandler;
import geogebra.web.gui.util.StyleBarW;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

/**
 * StyleBar for AlgebraView
 */
public class AlgebraStyleBarW extends StyleBarW implements ValueChangeHandler<Boolean>, SettingListener {

	/** button to show hide auxiliary objects */
	private MyToggleButton2 auxiliary;
	/** button to open the popup with the supported tree-modes */
	PopupMenuButton treeModeButton;
	/** list of all supported {@link SortMode modes} */
	ArrayList<SortMode> supportedModes = new ArrayList<SortMode>();

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public AlgebraStyleBarW(AppW app){
		super(app, App.VIEW_ALGEBRA);
		app.getSettings().getAlgebra().addListener(this);

		addAuxiliaryButton();
		addTreeModeButton();
		addViewButton();

		setLabels();
	}

	/**
	 * @param app
	 */
	private void addAuxiliaryButton() {
		auxiliary = new MyToggleButton2(StyleBarResources.INSTANCE.auxiliary());
		auxiliary.setDown(app.showAuxiliaryObjects());
		auxiliary.addValueChangeHandler(this);
		add(auxiliary);
	}

	private void addTreeModeButton() {
		supportedModes.add(SortMode.DEPENDENCY);
		supportedModes.add(SortMode.TYPE);
		supportedModes.add(SortMode.ORDER);

		ImageOrText[] strTreeMode = getTreeModeStr();
		treeModeButton = new PopupMenuButton(app, strTreeMode,
		        strTreeMode.length, 1,
		        geogebra.common.gui.util.SelectionTable.MODE_TEXT);

		ImageOrText icon = new ImageOrText();
		icon.setUrl(StyleBarResources.INSTANCE.sortObjects().getSafeUri()
		        .asString());
		treeModeButton.setFixedIcon(icon);

		treeModeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				SortMode selectedMode = app.getAlgebraView().getTreeMode();
				treeModeButton.setSelectedIndex(supportedModes
				        .indexOf(selectedMode));
			}
		});

		treeModeButton.addPopupHandler(new PopupMenuHandler() {
			@Override
			public void fireActionPerformed(PopupMenuButton actionButton) {
				// called if a object of the popup is clicked
				int i = treeModeButton.getSelectedIndex();
				app.getAlgebraView().setTreeMode(supportedModes.get(i));
			}
		});
		add(treeModeButton);
	}

	/**
	 * creates an array from all available supported modes and converts it to an
	 * array of {@link ImageOrText} elements
	 * 
	 * @return {@link ImageOrText ImageOrText[]}
	 */
    private ImageOrText[] getTreeModeStr() {
		String[] modes = new String[supportedModes.size()];
		for (int i = 0; i < supportedModes.size(); i++) {
			modes[i] = app.getPlain(supportedModes.get(i).toString());
		}
		return ImageOrText.convert(modes);
    }

	private void setToolTips() {
		auxiliary.setToolTipText(app.getLocalization().getPlain(
		        "AuxiliaryObjects"));
		treeModeButton.setToolTipText(app.getLocalization().getPlainTooltip(
		        "SortBy"));
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		if (event.getSource() == auxiliary) {
			app.setShowAuxiliaryObjects(auxiliary.isDown());
		}
	}

	@Override
	public void setOpen(boolean showStyleBar) {
		// TODO Auto-generated method stub
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		AlgebraSettings as = (AlgebraSettings) settings;
		auxiliary.setDown(as.getShowAuxiliaryObjects());
	}

	@Override
	public void setLabels() {
		super.setLabels();
		this.treeModeButton.getMyTable().updateText(getTreeModeStr());
		setToolTips();
	}
}
