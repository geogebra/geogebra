package org.geogebra.common.kernel;

public class GTemplate {
	private StringTemplate tpl;
	private Kernel kernel;

	/**
	 * @param tpl
	 *            string template
	 * @param kernel
	 *            kernel
	 */
	public GTemplate(StringTemplate tpl, Kernel kernel) {
		this.tpl = tpl;
		this.kernel = kernel;
	}

	public StringTemplate getTemplate() {
		return tpl;
	}

	public Kernel getKernel() {
		return kernel;
	}

}
