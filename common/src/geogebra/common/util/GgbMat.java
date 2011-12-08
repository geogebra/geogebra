package geogebra.common.util;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyList;

public interface GgbMat {
	public void inverseImmediate();
	
	public void getMyList(MyList outputListEV,AbstractKernel kernel);
}
