package com.himamis.retex.editor.share.meta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CharacterGroup implements MetaGroup {

    private static final Set<Character> extraChars = new HashSet<Character>();

    static {
        extraChars.add('.');
        extraChars.add(' ');
        extraChars.add(';');
        extraChars.add(',');
    }

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

        char ch = name.charAt(0);
        /*if (!Character.isLetter(ch) && !Character.isDigit(ch) && !extraChars.contains(ch)) {
            return null;
        }*/

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
