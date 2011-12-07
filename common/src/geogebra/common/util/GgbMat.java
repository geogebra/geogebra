package geogebra.common.util;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.ExpressionValue;

public interface GgbMat {
	public void inverseImmediate();
	//TODO: change ExpressionValue to MyList once ported
	public void getMyList(ExpressionValue outputListEV,AbstractKernel kernel);
}
