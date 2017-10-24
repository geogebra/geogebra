package org.geogebra.common.kernel;

public class GTemplate {
	private StringTemplate tpl;
	private Kernel kernel;

	public GTemplate(StringTemplate tpl, Kernel kernel) {
		this.tpl = tpl;
		this.kernel = kernel;
	}

	public StringTemplate getTemplate() {
		return tpl;
	}
}
