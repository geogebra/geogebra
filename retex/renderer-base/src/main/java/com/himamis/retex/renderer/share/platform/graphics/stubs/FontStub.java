package com.himamis.retex.renderer.share.platform.graphics.stubs;

import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.TextAttribute;

import java.util.Map;

public class FontStub implements Font {

    @Override
    public Font deriveFont(Map<TextAttribute, Object> map) {
        return this;
    }

    @Override
    public Font deriveFont(int type) {
        return this;
    }

    @Override
    public boolean isEqual(Font f) {
        return false;
    }
}
