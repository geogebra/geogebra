package geogebra.common.kernel.arithmetic;

import geogebra.common.plugin.Operation;

public interface IneqTreeInterface {

	IneqTreeInterface getLeft();
	IneqTreeInterface getRight();
	Operation getOperation();

}
