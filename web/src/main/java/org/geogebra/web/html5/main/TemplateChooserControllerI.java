package org.geogebra.web.html5.main;

import org.geogebra.common.move.ggtapi.models.Material;

import java.util.List;

public interface TemplateChooserControllerI {

    void fillTemplates(AppW appW, List<Material> templates);
}
