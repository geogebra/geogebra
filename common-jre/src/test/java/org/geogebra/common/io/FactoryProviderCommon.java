package org.geogebra.common.io;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.ArgumentMatchers;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.font.FontFactory;
import com.himamis.retex.renderer.share.platform.font.TextAttributeProvider;
import com.himamis.retex.renderer.share.platform.geom.GeomFactory;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.GraphicsFactory;
import com.himamis.retex.renderer.share.platform.graphics.Image;

public class FactoryProviderCommon extends FactoryProvider {

    @Override
    protected GeomFactory createGeomFactory() {
        return mock(GeomFactory.class);
    }

    @Override
    protected FontFactory createFontFactory() {
        FontFactory fontFactory = mock(FontFactory.class);
        when(fontFactory.createTextAttributeProvider())
                .thenReturn(mock(TextAttributeProvider.class));
        return fontFactory;
    }

    @Override
    protected GraphicsFactory createGraphicsFactory() {
        GraphicsFactory factory = mock(GraphicsFactory.class);
        when(factory.createImage(ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt()))
                .thenReturn(mock(Image.class));
        when(factory.createColor(ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt()))
                .thenReturn(mock(Color.class));
        return factory;
    }

}
