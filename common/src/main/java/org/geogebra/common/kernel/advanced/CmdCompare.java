package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.prover.AlgoCompare;
import org.geogebra.common.main.MyError;

public class CmdCompare extends CommandProcessor {
    /**
     * Create new command processor
     *
     * @param kernel kernel
     */
    public CmdCompare(Kernel kernel) {
        super(kernel);
    }

    @Override
    public GeoElement[] process(Command c)
            throws MyError, CircularDefinitionException {
        int n = c.getArgumentNumber();
        GeoElement[] arg;
        arg = resArgs(c);
        if (n == 2) {

            AlgoCompare algo = new AlgoCompare(cons, c.getLabel(), arg[0],
                    arg[1]);

            GeoElement[] ret = {algo.getResult()};
            return ret;
        }
        throw argNumErr(c);

    }

}