package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.statistics.CmdZMean2Estimate;
import geogebra.common.kernel.statistics.CmdZMean2Test;
import geogebra.common.kernel.statistics.CmdZMeanEstimate;
import geogebra.common.kernel.statistics.CmdZMeanTest;
import geogebra.common.kernel.statistics.CmdZProportion2Estimate;
import geogebra.common.kernel.statistics.CmdZProportion2Test;
import geogebra.common.kernel.statistics.CmdZProportionEstimate;
import geogebra.common.kernel.statistics.CmdZProportionTest;

public class CmdDispatcherStats {
	public CommandProcessor dispatch(Commands c, Kernel kernel){
		switch(c){
		case ZProportionTest:
			return new CmdZProportionTest(kernel);
		case ZProportion2Test:
			return new CmdZProportion2Test(kernel);
		case ZProportionEstimate:
			return new CmdZProportionEstimate(kernel);
		case ZProportion2Estimate:
			return new CmdZProportion2Estimate(kernel);
		case ZMeanEstimate:
			return new CmdZMeanEstimate(kernel);
		case ZMean2Estimate:
			return new CmdZMean2Estimate(kernel);
		case ZMeanTest:
			return new CmdZMeanTest(kernel);
		case ZMean2Test:
			return new CmdZMean2Test(kernel);
		}
		return null;
	}
}
