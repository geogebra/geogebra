package geogebra.mobile.gui.elements.toolbar;

import org.vectomatic.dom.svg.ui.SVGResource;

import geogebra.mobile.gui.elements.GuiModel;
import geogebra.mobile.utils.ToolBarCommand;
import geogebra.mobile.utils.ToolBarMenu;
import geogebra.web.euclidian.event.TouchEvent;

import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;

/**
 * 
 * A button for the main-toolBar.
 * 
 * @author Thomas Krismayer
 * 
 */
public class ToolBarButton extends ToolButton implements OptionsClickedListener
{

	protected ToolBarCommand[] menuEntries;
	protected GuiModel model;

	/**
	 * Each ToolBarButton belongs to a {@link ToolBarMenu}.
	 * 
	 * @param menu
	 *            : the button to be placed
	 * @param guiModel
	 *            : the ToolBar it is placed on
	 */
	public ToolBarButton(ToolBarMenu menu, GuiModel guiModel)
	{
		super(menu.getCommand());

		this.menuEntries = menu.getEntries();
		this.model = guiModel;

		/*
		 * this.addTapHandler(new TapHandler() {
		 * 
		 * @Override public void onTap(TapEvent event) {
		 * System.out.println("onTap"); if
		 * (ToolBarButton.this.model.getCommand() == ToolBarButton.this
		 * .getCmd() && ToolBarButton.this.model.optionsShown()) {
		 * ToolBarButton.this.model.closeOptions(); } else { showOptions(); } }
		 * });
		 */

		// test another EventHandler
		this.addTouchHandler(new TouchHandler()
		{

			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				event.preventDefault();
				// ToolBarButton.this.addStyleName("button-active");
				if (ToolBarButton.this.model.getCommand() == ToolBarButton.this
						.getCmd() && ToolBarButton.this.model.optionsShown())
				{
					ToolBarButton.this.model.closeOptions();
				} else
				{
					showOptions();
				}
			}

			@Override
			public void onTouchMove(TouchMoveEvent event)
			{
				event.preventDefault();

			}

			@Override
			public void onTouchEnd(TouchEndEvent event)
			{
				event.preventDefault();

			}

			@Override
			public void onTouchCanceled(TouchCancelEvent event)
			{
				event.preventDefault();

			}
		});
	}

	// end test another EventHandler

	public ToolBarButton(SVGResource svg, GuiModel guiModel)
	{
		super(svg);
		this.model = guiModel;
	}

	@Override
	public void optionClicked(ToolBarCommand cmd)
	{
		super.setCmd(cmd);
		this.model.buttonClicked(this);
	}

	protected void showOptions()
	{
		this.model.closeOptions();

		
			OptionsBarBackground options = new OptionsBarBackground(
					this.menuEntries, this);
			this.model.setOptions(options);
			
			RootPanel.get().add(options);
			if (this.menuEntries.length != 0)
			{
			options.show();
			}
			this.model.setOptionsShown(true);
		

		this.model.setActive(this);
	}

}
