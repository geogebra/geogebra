package com.himamis.retex.editor.share.meta;

/**
 * Describes a group of meta objects.
 */
public interface MetaGroup {

    /**
     * @param tag the component tag
     * @return the respective meta component if exists, null otherwise
     */
	 MetaComponent getComponent(Tag tag);
}
