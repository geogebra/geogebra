package org.geogebra.web.phone.gui.view.algebra;

import org.geogebra.web.phone.gui.view.AbstractStyleBar;
import org.geogebra.web.web.gui.layout.panels.AlgebraStyleBarW;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * StyleBar for AlgebraView
 * 
 */
public class AlgebraStyleBar extends AbstractStyleBar {

	private AlgebraStyleBarW algebraStyleBar;

	/**
	 * @param algebraStyleBar
	 *            {@link AlgebraStyleBarW}
	 */
	public AlgebraStyleBar(AlgebraStyleBarW algebraStyleBar) {
		this.algebraStyleBar = algebraStyleBar;
	}

	@Override
	public void setOpen(boolean showStyleBar) {
		algebraStyleBar.setOpen(showStyleBar);
	}

	@Override
	protected IsWidget createStyleBar() {
		return this.algebraStyleBar;
	}

	@Override
	protected ImageResource createStyleBarIcon() {
		return (ImageResource) resources.styleBar_algebraView();
	}

}
