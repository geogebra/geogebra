package org.geogebra.web.html5.main;

import java.util.List;

import org.geogebra.common.move.ggtapi.models.Material;

public interface TemplateChooserControllerI {

    /**
     * fill templates list given a material list
     * @param appW see {@link AppW}
     * @param templates list of materials having type ggs-templates
     */
    void fillTemplates(AppW appW, List<Material> templates);
}
