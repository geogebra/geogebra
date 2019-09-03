package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.algos.GetCommand;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;

public class SuggestionDiscover extends Suggestion {

    static final Suggestion DISCOVER = new SuggestionDiscover();
    private String[] labels;

    public SuggestionDiscover(String... labels) {
        this.labels = labels;
    }

    @Override
    public String getCommand(Localization loc) {
        return loc.getMenu("Discover");
    }

    /**
     * @param geo last element
     * @return list of labels {previous equations, last element}
     */
    public String getLabels(GeoElementND geo) {
        if (labels == null || labels.length < 1) {
            return geo.getLabelSimple();
        }
        return "{" + StringUtil.join(", ", labels) + "," + geo.getLabelSimple()
                + "}";
    }

    @Override
    protected void runCommands(GeoElementND geo) {
        geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
                "Discover[" + getLabels(geo) + "]", false);
    }

    /**
     * Check if Discover is available for the geo and return suitable suggestion
     *
     * @param geo construction element
     * @return suggestion if applicable
     */
    public static Suggestion get(GeoElement geo) {
        if (checkDependentAlgo(geo, DISCOVER, null)) {
            return null;
        }

        if (geo instanceof GeoPoint) {
            return DISCOVER;
        }
        return null;
    }


    @Override
    protected boolean allAlgosExist(GetCommand className, GeoElement[] input,
                                    boolean[] algosMissing) {
        return className == Commands.Discover;
    }
}
