package org.geogebra.common.kernel;

import org.geogebra.common.main.Localization;

public class GTemplate {
	private Localization loc;
	private StringTemplate tpl;

	public GTemplate(Localization loc, StringTemplate tpl) {
		this.loc = loc;
		this.tpl = tpl;
	}

	public StringTemplate getTemplate() {
		return tpl;
	}
}
