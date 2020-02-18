package org.geogebra.common.kernel.geos.groups;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 *  model for group of selected geos
 */
public class Group {
    private ArrayList<GeoElement> geosGroup = new ArrayList();
    private boolean isFixed;

    /**
     * Constructor for group
     * @param selectedGeos - geos selected for group
     */
    public Group(ArrayList<GeoElement> selectedGeos) {
        setFixed(selectedGeos.get(0).isLocked());
        for (GeoElement geo : selectedGeos) {
            geosGroup.add(geo);
            geo.setParentGroup(this);
        }
    }

    /**
     * @return list of geos in this group
     */
    public ArrayList<GeoElement> getGroupedGeos() {
        return geosGroup;
    }

    /**
     * set as group the geos given
     * @param geos list of selected geos
     */
    public void setGroupedGeos(ArrayList<GeoElement> geos) {
        geosGroup = geos;
    }

    public void setFixed(boolean fixed) {
        isFixed = fixed;
    }

    public boolean isGroupFixed() {
        return isFixed;
    }

    /**
     * xml representation of group for saving/loading
     * @param sb - xml string builder
     */
    public void getXML(StringBuilder sb) {
        sb.append("<group ");
        for (int i = 0; i < getGroupedGeos().size(); i++) {
            sb.append("l");
            sb.append(i);
            sb.append("=\"");
            sb.append(getGroupedGeos().get(i).getLabelSimple());
            sb.append("\" ");
        }
        sb.append("/>\n");
    }
}
