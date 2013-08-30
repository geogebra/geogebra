package geogebra.touch.model;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.touch.gui.elements.FastButton;
import geogebra.touch.gui.elements.stylebar.OptionsPanel;
import geogebra.touch.gui.elements.stylebar.StyleBar;
import geogebra.touch.gui.elements.stylebar.StyleBarStatic;
import geogebra.touch.gui.elements.toolbar.SubToolBar;
import geogebra.touch.gui.elements.toolbar.ToolBarButton;
import geogebra.touch.utils.OptionType;
import geogebra.touch.utils.ToolBarCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Organizes the visibility of the additional {@link OptionsBar toolBar}
 * according to the {@link ToolBarButton active button}.
 * 
 * @author Thomas Krismayer
 * 
 */
public class GuiModel {
	private final TouchModel touchModel;
	private ToolBarButton activeButton;
	private ToolBarButton defaultButton;
	private StyleBar styleBar;
	private PopupPanel optionsPanel;
	private OptionType styleBarOptionShown = OptionType.None;
	private PopupPanel activeDialog;
	private HashMap<Integer, Integer> defaultGeoMap = EuclidianStyleBarStatic
			.createDefaultMap();

	/**
	 * @param model
	 *            if it is not intended to use a TouchModel, model can be null
	 */
	GuiModel(final TouchModel model) {
		this.touchModel = model;
	}

	public int getDefaultType() {
		if (!this.defaultGeoMap.containsKey(Integer.valueOf(this.touchModel
				.getCommand().getMode()))) {
			// Move, Pen, ...
			return -1;
		}

		return this.defaultGeoMap.get(
				Integer.valueOf(this.touchModel.getCommand().getMode()))
				.intValue();
	}

	public GeoElement getDefaultGeo(int mode) {
		if (!this.defaultGeoMap.containsKey(Integer.valueOf(mode))) {
			// Move, Pen, ...
			return null;
		}

		return this.touchModel
				.getKernel()
				.getConstruction()
				.getConstructionDefaults()
				.getDefaultGeo(
						this.defaultGeoMap.get(Integer.valueOf(mode))
								.intValue());
	}

	public GeoElement getDefaultGeo() {
		return getDefaultGeo(this.touchModel.getCommand().getMode());
	}

	public void buttonClicked(final ToolBarButton tbb) {
		closeOptions();
		setActive(tbb);

		if (this.touchModel != null) {
			this.touchModel.resetSelection();
		}
	}

	public void closeActiveDialog() {
		if (this.activeDialog != null) {
			this.activeDialog.hide();
		}
		setActiveDialog(null);
	}

	public void closeOptions() {
		if (this.optionsPanel != null) {
			this.optionsPanel.hide();

			this.styleBarOptionShown = OptionType.None;

			if (this.touchModel != null) {
				this.touchModel.optionsClosed();
			}
		}
	}

	public ToolBarCommand getCommand() {
		return this.activeButton == null ? null : this.activeButton.getCmd();
	}

	public OptionType getOptionTypeShown() {
		return this.styleBarOptionShown;
	}

	public boolean isDialogShown() {
		return this.activeDialog != null;
	}

	public void setActive(final ToolBarButton toolBarButton) {
		if (this.activeButton != null && this.activeButton != toolBarButton) {
			// transparent
			this.activeButton.setActive(false);
			this.activeButton.removeStyleName("active");
		}
		this.activeButton = toolBarButton;
		this.activeButton.setActive(true);
		this.activeButton.addStyleName("active");

		if (this.touchModel != null) {
			this.touchModel.setCommand(toolBarButton.getCmd());
		}
		if (this.styleBar != null) {
			this.styleBar.rebuild();
		}
	}

	public void setActiveDialog(final PopupPanel dialog) {
		this.activeDialog = dialog;
	}

	public void setAlpha(final float a) {
		if (this.defaultGeoMap.containsKey(Integer.valueOf(this.touchModel
				.getCommand().getMode()))) {
			// Commands that have a default GeoElement
			getDefaultGeo(this.touchModel.getCommand().getMode())
					.setAlphaValue(a);
		} else if (this.touchModel.getCommand().getMode() == 0) {
			// Move
			ConstructionDefaults cd = this.touchModel.getKernel()
					.getConstruction().getConstructionDefaults();
			for (GeoElement geo : this.touchModel.getSelectedGeos()) {
				cd.getDefaultGeo(cd.getDefaultType(geo)).setAlphaValue(a);
			}
		}

		final List<GeoElement> fillable = new ArrayList<GeoElement>();
		for (final GeoElement geo : this.touchModel.getSelectedGeos()) {
			if (geo.isFillable()) {
				fillable.add(geo);
			}
		}

		if (fillable.size() > 0 && StyleBarStatic.applyAlpha(fillable, a)) {
			fillable.get(0).updateRepaint();
			this.touchModel.storeOnClose();
		}
	}

	public void setCaptionMode(final int i) {
		if (this.defaultGeoMap.containsKey(Integer.valueOf(this.touchModel
				.getCommand().getMode()))) {
			// Commands that have a default GeoElement
			getDefaultGeo(this.touchModel.getCommand().getMode()).setLabelMode(
					i - 1);
		} else if (this.touchModel.getCommand().getMode() == 0) {
			// Move
			ConstructionDefaults cd = this.touchModel.getKernel()
					.getConstruction().getConstructionDefaults();
			for (GeoElement geo : this.touchModel.getSelectedGeos()) {
				cd.getDefaultGeo(cd.getDefaultType(geo)).setLabelMode(i - 1);
			}
		}

		if (this.touchModel.getTotalNumber() > 0) {
			EuclidianStyleBarStatic.applyCaptionStyle(this.touchModel
					.getSelectedGeos(), this.touchModel.getCommand().getMode(),
					i);
		}
	}

	public void setColor(final GColor c) {
		if (this.defaultGeoMap.containsKey(Integer.valueOf(this.touchModel
				.getCommand().getMode()))) {
			// Commands that have a default GeoElement
			getDefaultGeo(this.touchModel.getCommand().getMode())
					.setObjColor(c);
		} else if (this.touchModel.getCommand().getMode() == 0) {
			// Move
			ConstructionDefaults cd = this.touchModel.getKernel()
					.getConstruction().getConstructionDefaults();
			for (GeoElement geo : this.touchModel.getSelectedGeos()) {
				cd.getDefaultGeo(cd.getDefaultType(geo)).setObjColor(c);
			}
		}

		// Update Pen color if necessary
		if (this.touchModel.getCommand().equals(ToolBarCommand.Pen)
				|| this.touchModel.getCommand().equals(
						ToolBarCommand.FreehandShape)) {
			this.touchModel.getKernel().getApplication().getEuclidianView1()
					.getEuclidianController().getPen().setPenColor(c);
		}
	}

	public void setLineSize(final int i) {
		if (this.defaultGeoMap.containsKey(Integer.valueOf(this.touchModel
				.getCommand().getMode()))) {
			// Commands that have a default GeoElement
			getDefaultGeo(this.touchModel.getCommand().getMode())
					.setLineThickness(i);
		} else if (this.touchModel.getCommand().getMode() == 0) {
			// Move
			ConstructionDefaults cd = this.touchModel.getKernel()
					.getConstruction().getConstructionDefaults();
			for (GeoElement geo : this.touchModel.getSelectedGeos()) {
				cd.getDefaultGeo(cd.getDefaultType(geo)).setLineThickness(i);
			}
		}

		if (this.touchModel.getCommand().equals(ToolBarCommand.Pen)
				|| this.touchModel.getCommand().equals(
						ToolBarCommand.FreehandShape)) {
			this.touchModel.getKernel().getApplication().getEuclidianView1()
					.getEuclidianController().getPen().setPenSize(i);
			this.touchModel.getKernel().getApplication().getEuclidianView1()
					.getEuclidianController().getPen().setPenSize(i);
		}
	}

	public void setLineStyle(final int index) {
		final int lineStyle = EuclidianStyleBarStatic.lineStyleArray[index]
				.intValue();

		if (this.defaultGeoMap.containsKey(Integer.valueOf(this.touchModel
				.getCommand().getMode()))) {
			// Commands that have a default GeoElement
			getDefaultGeo(this.touchModel.getCommand().getMode()).setLineType(
					lineStyle);
		} else if (this.touchModel.getCommand().getMode() == 0) {
			// Move
			ConstructionDefaults cd = this.touchModel.getKernel()
					.getConstruction().getConstructionDefaults();
			for (GeoElement geo : this.touchModel.getSelectedGeos()) {
				cd.getDefaultGeo(cd.getDefaultType(geo)).setLineType(lineStyle);
			}
		}

		if (this.touchModel.getCommand().equals(ToolBarCommand.Pen)
				|| this.touchModel.getCommand().equals(
						ToolBarCommand.FreehandShape)) {
			this.touchModel.getKernel().getApplication().getEuclidianView1()
					.getEuclidianController().getPen()
					.setPenLineStyle(lineStyle);
			if (this.touchModel.getCommand().equals(ToolBarCommand.Pen)
					|| this.touchModel.getCommand().equals(
							ToolBarCommand.FreehandShape)) {
				this.touchModel.getKernel().getApplication()
						.getEuclidianView1().getEuclidianController().getPen()
						.setPenLineStyle(lineStyle);
			}
		}
	}

	public void setOption(final SubToolBar options) {
		this.optionsPanel = options;
	}

	public void setStyleBarOptionShown(final OptionType type) {
		this.styleBarOptionShown = type;
	}

	public void setStyleBar(final StyleBar bar) {
		this.styleBar = bar;
	}

	public void setDefaultButton(final ToolBarButton manipulateObjects) {
		this.defaultButton = manipulateObjects;
	}

	public ToolBarButton getDefaultButton() {
		return this.defaultButton;
	}

	/**
	 * 
	 * @param panel
	 *            the OptionsPanel to be shown
	 * @param button
	 *            the button that was clicked, null in case of a Dialog
	 *            (OptionsType.Dialog)
	 */
	public void showOption(final OptionsPanel panel, final FastButton button) {
		closeOptions();
		this.optionsPanel = panel;
		this.optionsPanel.showRelativeTo(button);
		this.styleBarOptionShown = panel.getType();
	}

	void updateStyleBar() {
		if (this.styleBar != null) {
			this.styleBar.rebuild();
		}
	}

}