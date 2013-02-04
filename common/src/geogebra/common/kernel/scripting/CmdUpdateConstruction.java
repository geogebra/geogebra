package geogebra.common.kernel.scripting;



import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.main.MyError;

/**
 *UpdateConstruction
 */
public class CmdUpdateConstruction extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUpdateConstruction(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:
			app.getKernel().updateConstruction();
			app.setUnsaved();
			
			return;

		case 1:
			arg = resArgs(c);
			if (arg[0].isNumberValue()) {
				double val = ((NumberValue) arg[0]).getDouble();
				if (Kernel.isInteger(val)){
					app.getKernel().updateConstruction((int) val);
					app.setUnsaved();
					return;
				}
			}
			
			throw argErr(app, c.getName(), arg[0]);
			
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
