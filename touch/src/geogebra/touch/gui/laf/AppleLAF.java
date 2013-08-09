package geogebra.touch.gui.laf;

import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.elements.stylebar.StyleBar;
import geogebra.touch.utils.OptionType;

import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

/**
 * On 2013-08-06 iOS had TouchStartEvents
 * 
 * @author Matthias Meisinger
 * 
 */
public class AppleLAF extends DefaultLAF {

	public AppleLAF(TouchApp app) {
		super(app);
	}

	@Override
	public DefaultResources getIcons() {
		return AppleResources.INSTANCE;
	}

	@Override
	public int getAppBarHeight() {
		return 53;
	}

	@Override
	public boolean isMouseDownIgnored() {
		return false;
	}

	@Override
	public StandardImageButton setStyleBarButtonHandler(
			final StandardImageButton button, final StyleBar styleBar,
			final String process) {
		button.addDomHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				event.preventDefault();
				event.stopPropagation();
				styleBar.onStyleBarButtonEvent(button, process);
			}
		}, TouchStartEvent.getType());

		return button;
	}

	@Override
	public StandardImageButton setOptionalButtonHandler(
			final StandardImageButton button, final StyleBar styleBar,
			final OptionType type) {
		button.addDomHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				event.preventDefault();
				event.stopPropagation();
				styleBar.onOptionalButtonEvent(button, type);
			}
		}, TouchStartEvent.getType());

		return button;
	}

	@Override
	public StandardImageButton setStyleBarShowHideHandler(
			final StandardImageButton button, final StyleBar styleBar) {
		button.addDomHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				event.preventDefault();
				event.stopPropagation();
				styleBar.showHide();
			}
		}, TouchStartEvent.getType());

		return button;
	}

	@Override
	public StandardImageButton setAlgebraButtonHandler(
			StandardImageButton button, final TabletGUI gui) {
		button.addDomHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				event.preventDefault();
				event.stopPropagation();
				gui.toggleAlgebraView();
				if (TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel() != null) {
					TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel()
							.enableDisableButtons();
				}
			}
		}, TouchStartEvent.getType());

		return button;
	}
}
