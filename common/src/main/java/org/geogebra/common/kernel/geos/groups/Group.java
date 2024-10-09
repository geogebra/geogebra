package org.geogebra.common.kernel.geos.groups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Stream;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 *  model for group of selected geos
 */
public class Group {

    public static final Comparator<GeoElement> orderComparator =
			Comparator.comparingDouble(GeoElement::getOrdering);

    private GeoElement lead;
    private ArrayList<GeoElement> geosGroup = new ArrayList<>();
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

        updateLead();
    }

    public GeoElement getMinByOrder() {
        return Collections.min(geosGroup, orderComparator);
    }

    public GeoElement getMaxByOrder() {
        return Collections.max(geosGroup, orderComparator);
    }

    private void updateLead() {
        lead = geosGroup.get(0);
        for (GeoElement geo : geosGroup) {
            if (geo.getConstructionIndex() < lead.getConstructionIndex()) {
                lead = geo;
            }
        }
    }

    /**
     * @return list of geos in this group
     */
    public ArrayList<GeoElement> getGroupedGeos() {
        return geosGroup;
    }

    public Stream<GeoElement> stream() {
        return geosGroup.stream();
    }

    /**
     * set as group the geos given
     * @param geos list of selected geos
     */
    public void setGroupedGeos(ArrayList<GeoElement> geos) {
        geosGroup = geos;
        updateLead();
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

    /**
     * The lead element of the group.
     * Used to skip the others when tabbing through geos.
     *
     * @return lead element of the group
     */
    public GeoElement getLead() {
        return lead;
    }

    /**
     *
     * @param geo to query
     * @return if geo is the lead element of the group.
     */
    public boolean isLead(GeoElement geo) {
        return geo == lead;
    }

    /**
     *
     * @param geos to check
     * @return if all geos belongs to the same group
     */
    public static boolean isInSameGroup(ArrayList<GeoElement> geos) {
        if (geos.size() == 0 || !geos.get(0).hasGroup()) {
            return false;
        }

        Group group = geos.get(0).getParentGroup();
        for (int i = 1; i < geos.size(); i++) {
            if (geos.get(i).getParentGroup() != group) {
                return false;
            }
        }
        return true;
    }
}
