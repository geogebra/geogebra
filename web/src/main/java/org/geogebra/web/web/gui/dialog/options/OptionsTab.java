package org.geogebra.web.web.gui.dialog.options;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.options.model.ColorObjectModel;
import org.geogebra.common.gui.dialog.options.model.ColorObjectModel.IColorObjectListener;
import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.web.gui.properties.IOptionPanel;
import org.geogebra.web.web.gui.properties.OptionPanel;
import org.geogebra.web.web.gui.util.ColorChooserW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabPanel;

class OptionsTab extends FlowPanel {
	/**
	 * 
	 */
	// private final OptionsObjectW optionsObjectW;
	private String titleId;
	private int index;
	private List<OptionsModel> models;
	private TabPanel tabPanel;
	private Localization loc;
	private boolean inited = false;
	
	public OptionsTab(Localization loc, TabPanel tabPanel,
			final String title) {
		super();
		// this.optionsObjectW = optionsObjectW;
		this.titleId = title;
		this.loc = loc;
		this.tabPanel = tabPanel;
		models = new ArrayList<OptionsModel>();
		setStyleName("propertiesTab");
	}

	public void add(IOptionPanel panel) {
		add(panel.getWidget());
		models.add(panel.getModel());
	}

	public void addModel(OptionsModel model) {
		models.add(model);
	}

	public void addPanelList(List<OptionPanel> list) {
		for (OptionPanel panel: list) {
			add(panel);
		}
	}

	public boolean update(Object[] geos) {
		boolean enabled = false;
		for (OptionsModel panel : models) {
			enabled = panel.updateMPanel(geos) || enabled;
		}

		TabBar tabBar = this.tabPanel.getTabBar();
		tabBar.setTabText(index, getTabText());
		tabBar.setTabEnabled(index, enabled);	
		if (!enabled && tabBar.getSelectedTab() == index) {
			tabBar.selectTab(0);
		}
		return enabled;
	}

	private String getTabText() {
		return loc.getMenu(titleId);
	}

	public void addToTabPanel() {
		this.tabPanel.add(this, getTabText());
		index = this.tabPanel.getWidgetIndex(this);
	}

	public void onResize(int height, int width) {
         this.setHeight(height + "px");
    }

	public void initGUI(App app) {
		if (inited) {
			return;
		}
		inited = true;
		for (OptionsModel m : models) {
			IOptionPanel panel = buildPanel(m, app);
			if (panel != null) {
				add(panel.getWidget());
			}
		}

	}

	private IOptionPanel buildPanel(OptionsModel m, App app) {
		if (m instanceof ColorObjectModel) {
			return new ColorPanel((ColorObjectModel) m, app);
		}
		return null;
	}

	String localize(final String id) {
		// TODO Auto-generated method stub
		String txt = loc.getPlain(id);
		if (txt.equals(id)) {
			txt = loc.getMenu(id);
		}
		return txt;
	}
	public class ColorPanel extends OptionPanel implements IColorObjectListener {
		ColorObjectModel model;
		private FlowPanel mainPanel;
		private ColorChooserW colorChooserW;
		private GColor selectedColor;
		CheckBox sequential;

		public ColorPanel(ColorObjectModel model0, App app) {
			this.model = model0;
			model.setListener(this);
			setModel(model);

			final GDimensionW colorIconSizeW = new GDimensionW(20, 20);

			colorChooserW = new ColorChooserW(app, 350, 210, colorIconSizeW, 4);
			colorChooserW.addChangeHandler(new ColorChangeHandler() {

				@Override
				public void onColorChange(GColor color) {
					applyChanges(false);
				}

				@Override
				public void onAlphaChange() {
					applyChanges(true);

				}

				@Override
				public void onClearBackground() {
					model.clearBackgroundColor();
				}

				@Override
				public void onBackgroundSelected() {
					updatePreview(model.getGeoAt(0).getBackgroundColor(), 1.0f);
				}

				@Override
				public void onForegroundSelected() {
					GeoElement geo0 = model.getGeoAt(0);
					float alpha = 1.0f;
					GColor color = null;
					if (geo0.isFillable()) {
						color = geo0.getFillColor();
						alpha = geo0.getAlphaValue();
					} else {
						color = geo0.getObjectColor();
					}

					updatePreview(color, alpha);
				}
			});
			colorChooserW.setColorPreviewClickable();

			sequential = new CheckBox("Sequential");
			mainPanel = new FlowPanel();
			mainPanel.add(colorChooserW);
			mainPanel.add(sequential);
			sequential.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					// TODO we may need to update the GUI here
					model.setSequential(sequential.getValue());

				}
			});
			setWidget(mainPanel);

		}

		public void applyChanges(boolean alphaOnly) {
			float alpha = colorChooserW.getAlphaValue();
			GColor color = colorChooserW.getSelectedColor();
			model.applyChanges(color, alpha, alphaOnly);
		}

		@Override
		public void updateChooser(boolean equalObjColor,
				boolean equalObjColorBackground, boolean allFillable,
				boolean hasBackground, boolean hasOpacity) {
			GColor selectedBGColor = null;
			float alpha = 1;
			GeoElement geo0 = model.getGeoAt(0);
			selectedColor = null;

			if (equalObjColorBackground) {
				selectedBGColor = geo0.getBackgroundColor();
			}

			if (isBackgroundColorSelected()) {
				selectedColor = selectedBGColor;
			} else {
				// set selectedColor if all selected geos have the same color
				if (equalObjColor) {
					if (allFillable) {
						selectedColor = geo0.getFillColor();
						alpha = geo0.getAlphaValue();
					} else {
						selectedColor = geo0.getObjectColor();
					}
				}
			}

			if (allFillable && hasOpacity) { // show opacity slider and set to
				// first geo's
				// alpha value

				colorChooserW.enableOpacity(true);
				alpha = geo0.getAlphaValue();
				colorChooserW.setAlphaValue(Math.round(alpha * 100));

			} else { // hide opacity slider and set alpha = 1
				colorChooserW.enableOpacity(false);
				alpha = 1;
				colorChooserW.setAlphaValue(Math.round(alpha * 100));
			}

			colorChooserW.enableBackgroundColorPanel(hasBackground);
			updatePreview(selectedColor, alpha);
		}

		@Override
		public void updatePreview(GColor color, float alpha) {
			colorChooserW.setSelectedColor(color);
			colorChooserW.setAlphaValue(alpha);
			colorChooserW.update();
		}

		@Override
		public boolean isBackgroundColorSelected() {
			return colorChooserW.isBackgroundColorSelected();
		}

		@Override
		public void updateNoBackground(GeoElement geo, GColor col, float alpha,
				boolean updateAlphaOnly, boolean allFillable) {
			if (!updateAlphaOnly) {
				geo.setObjColor(col);
			}
			if (allFillable) {
				geo.setAlphaValue(alpha);
			}

		}

		@Override
		public void setLabels() {
			colorChooserW.setPaletteTitles(localize("RecentColor"),
					localize("Other"));
			colorChooserW.setPreviewTitle(localize("Preview"));
			colorChooserW.setBgFgTitles(localize("BackgroundColor"),
					localize("ForegroundColor"));
			colorChooserW.setOpacityTitle(localize("Opacity"));
		}

	}

}