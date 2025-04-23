package com.himamis.retex.editor.share.meta;

import java.util.Collection;

/**
 * Iterable MetaGroup.
 * TODO replace with Iterable
 */
public interface MetaGroupCollection extends MetaGroup {

    /**
     * @return the components in this group
     */
	Collection<? extends MetaComponent> getComponents();

}
