package com.himamis.retex.editor.share.meta;

import java.util.HashMap;
import java.util.Map;

class CharacterGroup implements MetaGroup {

    private Map<String, MetaCharacter> characters = new HashMap<>();

	MetaCharacter getComponent(String name) {
		if (name == null || name.length() != 1) {
			return null;
		}

		MetaCharacter character = characters.get(name);
		if (character == null) {
			char code = name.charAt(0);
			character = new MetaCharacter(name, name, code, code, MetaCharacter.CHARACTER);
			characters.put(name, character);
		}
		return character;
	}

    @Override
    public MetaComponent getComponent(Tag tag) {
        return getComponent(tag.toString());
    }
}
