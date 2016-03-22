#import "ColorI.h"

@implementation ColorI

- (void)dealloc {
    CFRelease(_color);
}

- (instancetype)initWithR:(int)r withG:(int)g withB:(int)b {
    return [self initWithFloatR:r / 255.0f withG:g / 255.0f withB:b / 255.0f];
}

- (instancetype)initWithRGB:(unsigned int)rgb {
    float b = rgb & 0xFF;
    float g = (rgb >> 8) & 0xFF;
    float r = (rgb >> 16) & 0xFF;
    return [self initWithFloatR:r withG:g withB:b];
}

- (instancetype)initWithFloatR:(float)r withG:(float)g withB:(float)b {
    self = [super init];
    if (self) {
        CGFloat component[] = {r, g, b, 1.0f};
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
