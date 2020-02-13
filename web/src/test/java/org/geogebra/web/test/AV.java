package org.geogebra.web.test;

import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

/**
 * Helper class for processing commands like these were entered into the AV.
 */
public class AV {

    private App app;

    public void setApp(App app) {
        this.app = app;
    }

    /**
     * Use this method when you want to test the commands as if those were inserted in AV.
     *
     * @param command
     *            algebra input to be processed
     * @return resulting element
     */
    public  <T extends GeoElement> T enter(String command) {
        EvalInfo info = EvalInfoFactory.getEvalInfoForAV(app, false);
        T[] geoElements =
                (T[]) getAlgebraProcessor()
                        .processAlgebraCommandNoExceptionHandling(
                                command,
                                false,
                                app.getErrorHandler(),
                                info,
                                null);
        return getFirstElement(geoElements);
    }

    private AlgebraProcessor getAlgebraProcessor() {
        return app.getKernel().getAlgebraProcessor();
    }

    private <T extends GeoElement> T getFirstElement(T[] geoElements) {
        return geoElements.length == 0 ? null : (T) geoElements[0].toGeoElement();
    }
}
