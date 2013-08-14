package geogebra.touch.model;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.elements.stylebar.OptionsPanel;
import geogebra.touch.gui.elements.stylebar.StyleBar;
import geogebra.touch.gui.elements.stylebar.StyleBarStatic;
import geogebra.touch.gui.elements.toolbar.SubToolBar;
import geogebra.touch.gui.elements.toolbar.ToolBarButton;
import geogebra.touch.utils.OptionType;
import geogebra.touch.utils.ToolBarCommand;

import java.util.ArrayList;

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
	private GColor color;
	private float alpha = -1f;
	private int lineStyle = -1;
	private int lineSize = -1;
	private int captionMode = -1;
	private PopupPanel activeDialog;

	/**
	 * @param model
	 *            if it is not intended to use a TouchModel, model can be null
	 */
	public GuiModel(final TouchModel model) {
		this.touchModel = model;
	}

	public void appendStyle(final ArrayList<GeoElement> elements) {
		if (this.color != null) {
			StyleBarStatic.applyColor(elements, this.color);
		}
		if (this.alpha >= 0) // != -1f
		{
			StyleBarStatic.applyAlpha(elements, this.alpha);
		}
		if (this.lineStyle != -1) {
			StyleBarStatic.applyLineStyle(elements, this.lineStyle);
		}
		if (this.lineSize != -1) {
			StyleBarStatic.applyLineSize(elements, this.lineSize);
		}
		if (this.captionMode != -1) {
			EuclidianStyleBarStatic.applyCaptionStyle(elements, -1,
					this.captionMode);
			// second argument (-1): anything other than 0
		}
	}

	public void buttonClicked(final ToolBarButton tbb) {
		this.closeOptions();
		this.setActive(tbb);

		if (this.touchModel != null) {
			this.touchModel.resetSelection();
			this.touchModel.repaint();
		}
	}

	public void closeActiveDialog() {
		if (this.activeDialog != null) {
			this.activeDialog.hide();
		}
		this.setActiveDialog(null);
	}

	public void closeOnlyOptions() {
		if (this.optionsPanel != null) {
			this.optionsPanel.hide();
			this.styleBarOptionShown = OptionType.None;

			if (this.touchModel != null) {
				this.touchModel.optionsClosed();
			}
		}
	}

	/**
	 * closes options and ToolBar
	 */
	public void closeOptions() {
		this.closeOnlyOptions();
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

	public void resetStyle() {
		this.color = null;
		this.alpha = -1f;
		this.lineStyle = -1;
		this.lineSize = -1;
		this.captionMode = -1;
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
		this.alpha = a;
	}

	public void setCaptionMode(final int i) {
		this.captionMode = i;
	}

	public void setColor(final GColor c) {
		this.color = c;

		// Update Pen color if necessary
		if (this.touchModel.getCommand().equals(ToolBarCommand.Pen)
				|| this.touchModel.getCommand().equals(
						ToolBarCommand.FreehandShape)) {
			this.touchModel.getKernel().getApplication().getEuclidianView1()
					.getEuclidianController().getPen().setPenColor(this.color);
		}

	}

	public void setLineSize(final int i) {
		this.lineSize = i;
	}

	public void setLineStyle(final int i) {
		this.lineStyle = i;
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
	 * @param parent
	 *            the button that was clicked, null in case of a Dialog
	 *            (OptionsType.Dialog)
	 */
	public void showOption(final OptionsPanel panel,
			final StandardImageButton parent) {
		this.closeOnlyOptions();
		this.optionsPanel = panel;
		this.optionsPanel.showRelativeTo(parent);
		this.styleBarOptionShown = panel.getType();
	}

	public void updateStyleBar() {
		if (this.styleBar != null) {
			this.styleBar.rebuild();
		}
	}
}