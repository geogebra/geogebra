package geogebra.euclidian;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.commands.AlgebraProcessor;

import org.scilab.forge.jlatexmath.dynamic.ExternalConverter;
import org.scilab.forge.jlatexmath.dynamic.ExternalConverterFactory;

public class LatexConvertorFactory implements ExternalConverterFactory {

	private AbstractKernel kernel;

	public LatexConvertorFactory(AbstractKernel kernel) {
		this.kernel = kernel;
	}

	public ExternalConverter getExternalConverter() {
		// you can associated an Geogebra env. with a DynamicAtom
		return new LatexConvertor(getCurrentGeoGebraEnv(),
				kernel.getConstruction());
	}

	private AlgebraProcessor getCurrentGeoGebraEnv() {
		return kernel.getAlgebraProcessor();
	}
}
