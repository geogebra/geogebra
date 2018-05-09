package org.geogebra.web.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import org.geogebra.web.resources.SassResource;

public interface SharedResources extends ClientBundle {

    SharedResources INSTANCE = GWT.create(SharedResources.class);

    @Source("org/geogebra/common/icons/png/web/algebra-view-tree-open.png")
    ImageResource algebra_tree_open();

    @Source("org/geogebra/common/icons/png/web/algebra-view-tree-closed.png")
    ImageResource algebra_tree_closed();

    @Source("org/geogebra/web/resources/scss/solver.scss")
    SassResource solverStyleScss();
}
