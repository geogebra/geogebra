package org.geogebra.common.main.exam.output;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OutputFilter {

    private List<Commands> fitCommands =
            Arrays.asList(
                    Commands.FitExp,
                    Commands.Fit,
                    Commands.FitGrowth,
                    Commands.FitImplicit,
                    Commands.FitLine,
                    Commands.FitLineX,
                    Commands.FitLineY,
                    Commands.FitLog,
                    Commands.FitLogistic,
                    Commands.FitPoly,
                    Commands.FitPow,
                    Commands.FitSin);

    private List<GetCommand> allowedCommands;

    public boolean isAllowed(GeoElement geoElement) {
        App app = geoElement.getKernel().getApplication();
        boolean isCasEnabled = app.getSettings().getCasSettings().isEnabled();
        AlgoElement parentAlgorithm = geoElement.getParentAlgorithm();
        if (app.isExamStarted() && !isCasEnabled && parentAlgorithm != null) {
            return getAllowedCommands().contains(parentAlgorithm.getClassName());
        } else {
            return true;
        }
    }

    private List<GetCommand> getAllowedCommands() {
        if (allowedCommands == null) {
            allowedCommands = new ArrayList<>();
            allowedCommands.add(Algos.Expression);
            allowedCommands.addAll(fitCommands);
        }
        return allowedCommands;
    }
}
