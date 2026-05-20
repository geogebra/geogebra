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

package org.geogebra.common.jre.plugin;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.JsReference;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.util.debug.Log;

public abstract class ScriptManagerJre extends ScriptManager {

    /**
     * @param app application
     */
    public ScriptManagerJre(App app) {
        super(app);
    }

    @Override
    public void ggbOnInit() {
        if (app.getEventDispatcher().isDisabled(ScriptType.JAVASCRIPT)) {
            return;
        }

        try {
            // call only if libraryJavaScript is not the default (ie do nothing)
            if (!app.getKernel().getLibraryJavaScript()
                    .equals(Kernel.defaultLibraryJavaScript)) {
                evalJavaScript("ggbOnInit(\"ggbApplet\", ggbApplet);");
            }
        } catch (Exception e) {
            Log.debug("Error calling ggbOnInit(): " + e.getMessage());
        }
    }

    @Override
    protected void callClientListeners(List<JsReference> listeners, Event evt) {
        ArrayList<String> args = new ArrayList<>();
        args.add(evt.type.getName());
        if (evt.targets != null) {
            for (GeoElement geo : evt.targets) {
                args.add(geo.getLabelSimple());
            }
        } else if (evt.target != null) {
            args.add(evt.target.getLabelSimple());
        } else {
            args.add("");
        }
        if (evt.argument != null) {
            args.add(evt.argument);
        }
        Object event = toNativeArray(args);
        for (JsReference listener : listeners) {
             callListener(listener, event);
        }
    }

    /**
     * Converts list into a NativeArray (which can be only referenced from platform code).
     * @param args arguments
     * @return arguments as array
     */
    protected abstract Object toNativeArray(ArrayList<String> args);

    protected abstract void evalJavaScript(String jsFunction);

}
