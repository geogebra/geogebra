/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.geos;

import java.util.Collections;

import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.hamcrest.Matcher;
import org.junit.Before;

public class BaseSymbolicTest {
    protected AppCommon app;
    protected AlgebraProcessor ap;
    protected Kernel kernel;
    private UndoManager undoManager;

    /**
     * Create the app
     */
    @Before
    public void setup() {
        app = AlgebraTest.createApp(new AppConfigCas());
        kernel = app.getKernel();
        ap = kernel.getAlgebraProcessor();

        kernel.setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
        app.setRounding("10");
        kernel.getGeoGebraCAS().evaluateGeoGebraCAS("1+1", null,
                StringTemplate.defaultTemplate, app.getKernel());
        undoManager = kernel.getConstruction().getUndoManager();
    }

    protected void t(String input, String... expected) {
        AlgebraTestHelper.checkSyntaxSingle(input, expected, ap,
                StringTemplate.testTemplate);
    }

    protected void t(String input, Matcher<String> matcher) {
        AlgebraTestHelper.checkSyntaxSingle(input, Collections.singletonList(matcher), ap,
                StringTemplate.testTemplate);
    }

    protected void tn(String input, String... expected) {
        AlgebraTestHelper.checkSyntaxSingle(input, expected, ap,
                StringTemplate.testNumeric);
    }

    protected void t(String input, EvalInfo info, String... expected) {
        GeoElementND result = ap.processAlgebraCommandNoExceptionHandling(input,
                false, TestErrorHandler.INSTANCE, info, null)[0];
        AlgebraTestHelper.assertOneOf(result, expected,
                StringTemplate.testTemplate);
    }

    protected<T extends GeoElement> T add(String text) {
        GeoElementND[] geos = ap.processAlgebraCommandNoExceptionHandling(text, false,
                ErrorHelper.silent(), false, null);
        return geos != null && geos.length > 0 ? (T) geos[0] : null;
    }

    protected void undoRedo() {
        undoManager.undo();
        undoManager.redo();
    }

    protected GeoElement lookup(String label) {
        return app.getKernel().lookupLabel(label);
    }
}
