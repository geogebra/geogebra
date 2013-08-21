package geogebra.touch.gui.elements.toolbar;

import geogebra.touch.TouchApp;
import geogebra.touch.model.GuiModel;
import geogebra.touch.utils.OptionType;
import geogebra.touch.utils.ToolBarCommand;
import geogebra.touch.utils.ToolBarMenu;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.user.client.Window;

/**
 * 
 * A button for the main-toolBar.
 * 
 * @author Thomas Krismayer
 * 
 */
public class ToolBarButton extends ToolButton implements OptionsClickedListener {

	private SubToolBarButton[] menuEntries;
	private final GuiModel model;
	private SubToolBar options;
	private static int BUTTON_WIDTH = 56;
	private static int BUTTONPANEL_BORDER = 7;

	public ToolBarButton(final SVGResource svgResource, final GuiModel guiModel) {
		super(guiModel.getCommand());
		super.setIcon(svgResource);
		this.model = guiModel;
	}

	/**
	 * Each ToolBarButton belongs to a {@link ToolBarMenu}.
	 * 
	 * @param menu
	 *            : the button to be placed
	 * @param guiModel
	 *            : the ToolBar it is placed on
	 * @param app
	 * @param toolBar
	 */
	public ToolBarButton(final ToolBarMenu menu, final GuiModel guiModel,
			TouchApp app) {
		super(menu.getCommand());
		this.model = guiModel;

		this.menuEntries = new SubToolBarButton[menu.getEntries().length];
		for (int i = 0; i < menu.getEntries().length; i++) {
			this.menuEntries[i] = new SubToolBarButton(menu.getEntries()[i],
					this);
		}
		this.options = new SubToolBar(this.menuEntries, app);
	}

	protected void onToolBarButton() {
		if (this.model.getCommand() == this.getCmd()
				&& this.model.getOptionTypeShown() == OptionType.ToolBar) {
			this.model.closeOptions();
		} else {
			this.showOptions();
		}
	}

	@Override
	public void optionClicked(final ToolBarCommand cmd) {
		super.setCmd(cmd);
		this.model.buttonClicked(this);
	}

	private void showOptions() {
		this.model.setActive(this);
		if (this.menuEntries.length != 0) {

			final int optionsWidth;
			if (this.options.isHorizontal()) {
				optionsWidth = this.menuEntries.length
						* ToolBarButton.BUTTON_WIDTH
						+ ToolBarButton.BUTTONPANEL_BORDER;
			} else {
				optionsWidth = ToolBarButton.BUTTON_WIDTH
						+ ToolBarButton.BUTTONPANEL_BORDER;
			}

			final int optionsHeight;
			if (this.options.isHorizontal()) {
				optionsHeight = ToolBarButton.BUTTON_WIDTH + 16;
			} else {
				optionsHeight = this.menuEntries.length
						* ToolBarButton.BUTTON_WIDTH + 16;
			}

			this.model.closeOnlyOptions();
			this.model.setOption(this.options);

			// if the width of the subtoolbar is too big, the position should
			// be different leftpos of button + width of subtoolbar must not be
			// bigger than Window-width!!
			if (this.getAbsoluteLeft() + optionsWidth > Window.getClientWidth()
					&& this.options.isHorizontal()) {

				// special case for cirlces (is still too long)
				if (this.getAbsoluteLeft() + ToolBarButton.BUTTON_WIDTH
						- optionsWidth < 0) {

					final int buttonsLeft = this.menuEntries.length / 2;

					this.options.setPopupPosition(this.getAbsoluteLeft()
							- buttonsLeft * ToolBarButton.BUTTON_WIDTH,
							this.getAbsoluteTop() - optionsHeight);

					this.options.setSubToolBarArrowPaddingLeft(buttonsLeft
							* ToolBarButton.BUTTON_WIDTH + 23);

				} else {

					this.options.setPopupPosition(this.getAbsoluteLeft()
							+ ToolBarButton.BUTTON_WIDTH - optionsWidth
							+ ToolBarButton.BUTTONPANEL_BORDER,
							this.getAbsoluteTop() - optionsHeight);

					this.options
							.setSubToolBarArrowPaddingLeft(optionsWidth - 37);
				}

			} else {
				// this.model.showOption(options, this);
				// (showRelativeToParent doesn't work correctly)
				this.options.setPopupPosition(this.getAbsoluteLeft(),
						this.getAbsoluteTop() - optionsHeight);

				this.options.setSubToolBarArrowPaddingLeft(23);
			}

			this.options.show();
			this.model.setStyleBarOptionShown(OptionType.ToolBar);
		}
	}
}