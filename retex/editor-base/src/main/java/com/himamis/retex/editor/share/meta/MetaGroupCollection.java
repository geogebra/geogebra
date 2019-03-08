package com.himamis.retex.editor.share.meta;

import java.util.Collection;

public interface MetaGroupCollection extends MetaGroup {

    /**
     * @return the components in this collection
     */
	Collection<? extends MetaComponent> getComponents();

}
