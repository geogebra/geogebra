package geogebra.touch.gui.elements.toolbar;

import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.utils.ToolBarCommand;

/**
 * Buttons of the options-menu.
 * 
 * @author Thomas Krismayer
 * @see geogebra.touch.gui.elements.toolbar.ToolButton ToolButton
 */
class SubToolBarButton extends ToolButton {

	OptionsClickedListener ancestor;

	/**
	 * Initializes the button of the options-menu and adds a {@link TapHandler}.
	 * 
	 * @param cmd
	 *            ToolBarCommand
	 * @param ancestor
	 *            OptionsClickedListener
	 */
	SubToolBarButton(ToolBarCommand cmd, OptionsClickedListener ancestor) {
		super(cmd);

		this.ancestor = ancestor;

		this.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				SubToolBarButton.this.ancestor
						.optionClicked(SubToolBarButton.this.getCmd());
			}
		});
	}
}
