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

	protected SubToolBarButton[] menuEntry;
	protected GuiModel model;

	private static int BUTTON_WIDTH = 56;
	private static int BUTTONPANEL_BORDER = 7;

	public ToolBarButton(SVGResource svgResource, GuiModel guiModel) {
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
	public ToolBarButton(ToolBarMenu menu, GuiModel guiModel) {
		super(menu.getCommand());

		this.menuEntry = new SubToolBarButton[menu.getEntries().length];
		for (int i = 0; i < menu.getEntries().length; i++) {
			this.menuEntry[i] = new SubToolBarButton(menu.getEntries()[i], this);
		}

		this.model = guiModel;

		this.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.preventDefault();
				if (ToolBarButton.this.model.getCommand() == ToolBarButton.this
						.getCmd()
						&& ToolBarButton.this.model.getOptionTypeShown() == OptionType.ToolBar) {
					ToolBarButton.this.model.closeOptions();
				} else {
					ToolBarButton.this.showOptions();
				}
			}
		}, ClickEvent.getType());
	}

	@Override
	public void optionClicked(ToolBarCommand cmd) {
		super.setCmd(cmd);
		this.model.buttonClicked(this);
	}

	protected void showOptions() {
		if (this.menuEntry.length != 0) {
			final SubToolBar options = new SubToolBar(this.menuEntry, this);

			final int optionsWidth = this.menuEntry.length
					* ToolBarButton.BUTTON_WIDTH
					+ ToolBarButton.BUTTONPANEL_BORDER;

			// if the width of the subtoolbar ist too big, the position should
			// be
			// different
			// leftpos of button + width of subtoolbar must not be bigger than
			// Window-width!!
			if (this.getAbsoluteLeft() + optionsWidth > Window.getClientWidth()
					&& options.isHorizontal()) {
				this.model.closeOnlyOptions();
				this.model.setOption(options);

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

				options.show();
				this.model.setStyleBarOptionShown(OptionType.ToolBar);

			} else {
				this.model.showOption(options, OptionType.ToolBar, this);
			}

		}
		this.model.setActive(this);
	}
}
