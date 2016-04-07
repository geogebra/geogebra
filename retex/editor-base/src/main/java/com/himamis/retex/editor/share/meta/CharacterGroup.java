package com.himamis.retex.editor.share.meta;

import java.util.HashMap;
import java.util.Map;

public class CharacterGroup implements MetaGroup {

    private Map<String, MetaCharacter> characters = new HashMap<String, MetaCharacter>();

    @Override
    public String getName() {
        return MetaModel.CHARACTERS;
    }

    @Override
    public String getGroup() {
        return getName();
    }

    @Override
    public MetaComponent getComponent(String name) {
        if (name == null || name.length() != 1) {
            return null;
        }

        MetaCharacter character = characters.get(name);
        if (character == null) {
            char code = name.length() > 0 ? name.charAt(0) : 0;
            character = new MetaCharacter(name, name, name, code, code,
                    MetaCharacter.CHARACTER);
            characters.put(name, character);
        }

        return character;
    }
}
