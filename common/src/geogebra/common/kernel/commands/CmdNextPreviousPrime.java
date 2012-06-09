package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

public class CmdNextPreviousPrime extends CmdOneNumber {

	private boolean next;
	public CmdNextPreviousPrime(Kernel kernel,boolean next) {
		super(kernel);
		this.next = next;
	}

	@Override
	protected GeoElement getResult(NumberValue num,String label){
		return kernelA.NextPreviousPrime(label, num,next);
	}

}
