package org.geogebra.web.full.html5;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.AriaMenuItemMock;
import org.geogebra.web.full.javax.swing.InlineTextToolbar;

import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class InlineTextToolbarMock extends InlineTextToolbar {

	public InlineTextToolbarMock(List<HasTextFormat> inlines, App app) {
		super(Collections.emptyList(), new AriaMenuItemMock(), app);
	}

	@Override
	protected void createGui() {
		setContent("TEXTTOOLBAR");
	}

	@Override
	protected void setTooltips() {
		// Nothing to do.
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {

	}

	@Override
	protected String getScriptFormat() {
		return "";
	}

	@Override
	protected String getListStyle() {
		return "";
	}

}
