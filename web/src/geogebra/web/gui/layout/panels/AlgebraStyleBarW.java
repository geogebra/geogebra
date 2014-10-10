package geogebra.web.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.AlgebraSettings;
import geogebra.common.main.settings.SettingListener;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.main.AppW;
import geogebra.web.gui.images.StyleBarResources;
import geogebra.web.gui.util.ImageOrText;
import geogebra.web.gui.util.MyToggleButton2;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.gui.util.PopupMenuHandler;
import geogebra.web.gui.util.StyleBarW;
import geogebra.web.gui.view.algebra.AlgebraViewWeb;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class AlgebraStyleBarW extends StyleBarW implements ValueChangeHandler<Boolean>, SettingListener {
	MyToggleButton2 auxiliary;

	public AlgebraStyleBarW(AppW app){
		super(app, App.VIEW_ALGEBRA);
		auxiliary = new MyToggleButton2(StyleBarResources.INSTANCE.auxiliary());
		auxiliary.setDown(app.showAuxiliaryObjects());
		auxiliary.addValueChangeHandler(this);
		add(auxiliary);
		app.getSettings().getAlgebra().addListener(this);
		setToolTips();

		addTreeModeButton();
		addViewButton();
	}
	
	private void setToolTips() {

	    Localization loc = app.getLocalization();
	    auxiliary.setToolTipText(loc.getMenu("auxiliary"));
	}

	@Override
    public void onValueChange(ValueChangeEvent<Boolean> event) {
	    if(event.getSource() == auxiliary){
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

	public void addTreeModeButton() {
		final int[] supportedModes = new int[] {0,1,3};

		ImageOrText[] data = new ImageOrText[supportedModes.length];
		for (int i = 0; i < supportedModes.length; i++) {
			data[i] = new ImageOrText(app.getLocalization().getPlain(
			        AlgebraViewWeb.intToMode(supportedModes[i]).toString()));
		}

		final PopupMenuButton treeModeButton = new PopupMenuButton(app, data,
		        data.length, 1, new GDimensionW(-1, -1),
		        geogebra.common.gui.util.SelectionTable.MODE_TEXT);
		ImageOrText icon = new ImageOrText();
		icon.url = StyleBarResources.INSTANCE.sortObjects().getSafeUri()
		        .asString();
		treeModeButton.setFixedIcon(icon);

		treeModeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int selectedIndex = app.getAlgebraView().getTreeModeValue();
				for(int i = 0; i < supportedModes.length; i++){
					if(supportedModes[i] == selectedIndex){
						treeModeButton.setSelectedIndex(i);
					}
				}
			}
		});

		treeModeButton.addPopupHandler(new PopupMenuHandler() {
			@Override
			public void fireActionPerformed(PopupMenuButton actionButton) {
				int i = treeModeButton.getSelectedIndex();
				app.getAlgebraView().setTreeMode(supportedModes[i]);
			}
		});
		add(treeModeButton);
	}
}
