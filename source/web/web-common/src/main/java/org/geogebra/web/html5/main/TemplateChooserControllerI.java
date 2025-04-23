package org.geogebra.web.html5.main;

import java.util.List;

import org.geogebra.common.move.ggtapi.models.Material;

/**
 * Controller for template chooser.
 * TODO not needed if we move the caller from web-common to web
 */
public interface TemplateChooserControllerI {

    /**
     * fill templates list given a material list
     * @param templates list of materials having type ggs-templates
     */
    void fillTemplates(List<Material> templates);
}
