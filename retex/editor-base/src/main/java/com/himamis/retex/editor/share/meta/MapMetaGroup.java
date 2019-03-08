package com.himamis.retex.editor.share.meta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Group of Meta Components stored in maps.
 */
public class MapMetaGroup implements MetaGroupCollection {

	private Map<String, MetaSymbol> components;

    MapMetaGroup() {
        components = new HashMap<>();
    }

    /**
     * Adds a component to the map, with the name and unicode String as keys.
     * @param component the component to be added
     */
	void addComponent(MetaSymbol component) {
        components.put(component.getName().toString(), component);
        components.put(component.getUnicodeString(), component);
    }

	/**
	 * Prefer using {@link MetaGroup#getComponent(Tag)} over this method.
	 *
	 * @see #getComponent(Tag)
	 * @param componentName
	 *            the name of the component
	 * @return the component with name, otherwise null
	 */
	public MetaSymbol getComponent(String componentName) {
        return components.get(componentName);
    }

    @Override
	public MetaSymbol getComponent(Tag tag) {
        return getComponent(tag.toString());
    }

    @Override
	public Collection<MetaSymbol> getComponents() {
        return components.values();
    }
}
