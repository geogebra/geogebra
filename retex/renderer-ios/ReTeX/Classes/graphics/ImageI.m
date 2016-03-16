#import "ImageI.h"
#import "Graphics2DI.h"


@implementation ImageI

- (instancetype)initWithWidth:(int)width withHeight:(int)height {
    self = [super init];
    if (self) {
        float scale = [UIScreen mainScreen].scale;

        CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();

        _context = CGBitmapContextCreate(nil, (size_t) (width * scale + 0.5), (size_t) (height * scale + 0.5), 8,
                (size_t) (width * 4 * scale + 0.5), colorSpace, kCGImageAlphaPremultipliedFirst);

        CGColorSpaceRelease(colorSpace);
        CGContextScaleCTM(_context, 1 / scale, 1 / scale);
    }
    return self;
}

- (id <RXGraphics2DInterface>)createGraphics2D {
    return [[Graphics2DI alloc] initWithContext:_context];
}

- (CGImageRef)createCGImage {
    return CGBitmapContextCreateImage(_context);;
}

- (jint)getWidth {
    return (int) (CGBitmapContextGetWidth(_context) + 0.5);
}

- (jint)getHeight {
    return (int) (CGBitmapContextGetHeight(_context) + 0.5);
}

@end
