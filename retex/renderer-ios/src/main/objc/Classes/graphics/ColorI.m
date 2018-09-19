#import "ColorI.h"

@implementation ColorI

- (void)dealloc {
    CFRelease(_color);
}

- (instancetype)initWithR:(int)r withG:(int)g withB:(int)b {
    return [self initWithFloatR:r / 255.0f withG:g / 255.0f withB:b / 255.0f withA:1.0f];
}

- (instancetype)initWithRGBA:(unsigned int)rgba {
    float b = (rgba & 0xFF) / 255.0f;
    float g = ((rgba >> 8) & 0xFF) / 255.0f;
    float r = ((rgba >> 16) & 0xFF) / 255.0f;
    float a = ((rgba >> 24) & 0xFF) / 255.0f;
    return [self initWithFloatR:r withG:g withB:b withA:a];
}

- (instancetype)initWithFloatR:(float)r withG:(float)g withB:(float)b withA:(float)a {
    self = [super init];
    if (self) {
        CGFloat component[] = {r, g, b, a};
        CGColorSpaceRef rgb = CGColorSpaceCreateDeviceRGB();
        _color = CGColorCreate(rgb, component);
        CFRelease(rgb);
    }
    return self;
}

- (instancetype)initWithCGColor:(CGColorRef)color {
    self = [super init];
    if (self) {
        _color = color;
        CFRetain(_color);
    }
    return self;
}

- (jint)getColor {
    const CGFloat *components = CGColorGetComponents(_color);
    jint color = 0;

    for (int i = 0; i < CGColorGetNumberOfComponents(_color); i++) {
        int component = (int) (components[i] * 255 + 0.5);
        color = (color << 8) | component;
    }
    return color;
}

@end
