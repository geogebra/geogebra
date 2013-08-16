package geogebra.touch.gui.elements.toolbar;

import geogebra.touch.model.GuiModel;
import geogebra.touch.utils.OptionType;
import geogebra.touch.utils.ToolBarCommand;
import geogebra.touch.utils.ToolBarMenu;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;

/**
 * 
 * A button for the main-toolBar.
 * 
 * @author Thomas Krismayer
 * 
 */
public class ToolBarButton extends ToolButton implements OptionsClickedListener {

	private SubToolBarButton[] menuEntry;
	private final GuiModel model;

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
	 */
	public ToolBarButton(final ToolBarMenu menu, final GuiModel guiModel) {
		super(menu.getCommand());
		this.model = guiModel;

		this.menuEntry = new SubToolBarButton[menu.getEntries().length];
		for (int i = 0; i < menu.getEntries().length; i++) {
			this.menuEntry[i] = new SubToolBarButton(menu.getEntries()[i], this);
		}

		this.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				event.preventDefault();
				onToolBarButton();
			}
		}, ClickEvent.getType());
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
		if (this.menuEntry.length != 0) {
			final SubToolBar options = new SubToolBar(this.menuEntry, this);

			final int optionsWidth = this.menuEntry.length
					* ToolBarButton.BUTTON_WIDTH
					+ ToolBarButton.BUTTONPANEL_BORDER;

			this.model.closeOnlyOptions();
			this.model.setOption(options);

			// if the width of the subtoolbar ist too big, the position should
			// be different leftpos of button + width of subtoolbar must not be
			// bigger than Window-width!!
			if (this.getAbsoluteLeft() + optionsWidth > Window.getClientWidth()
					&& options.isHorizontal()) {

				// special case for cirlces (is still too long)
				if (this.getAbsoluteLeft() + ToolBarButton.BUTTON_WIDTH
						- optionsWidth < 0) {
					final int buttonsLeft = this.menuEntry.length / 2;
					options.setPopupPosition(this.getAbsoluteLeft()
							- buttonsLeft * ToolBarButton.BUTTON_WIDTH,
							this.getAbsoluteTop() - ToolBarButton.BUTTON_WIDTH
									- 16);
					options.setSubToolBarArrowPaddingLeft(buttonsLeft
							* ToolBarButton.BUTTON_WIDTH + 23);
				} else {
					options.setPopupPosition(this.getAbsoluteLeft()
							+ ToolBarButton.BUTTON_WIDTH - optionsWidth
							+ ToolBarButton.BUTTONPANEL_BORDER,
							this.getAbsoluteTop() - ToolBarButton.BUTTON_WIDTH
									- 16);
					options.setSubToolBarArrowPaddingLeft(optionsWidth - 37);
				}

			} else {
				// this.model.showOption(options, this);
				// (showRelativeToParent doesn't work correctly)
				options.setPopupPosition(this.getAbsoluteLeft(),
						this.getAbsoluteTop() - ToolBarButton.BUTTON_WIDTH - 16);
			}

			options.show();
			this.model.setStyleBarOptionShown(OptionType.ToolBar);

		}
		this.model.setActive(this);
	}
}
