#import <UIKit/UIKit.h>
#import "ImageI.h"
#import "Graphics2DI.h"

@interface ImageI ()

@property int width;
@property int height;

@end

@implementation ImageI

- (void)dealloc {
    CGContextRelease(_context);
}

- (instancetype)initWithWidth:(int)width withHeight:(int)height {
    self = [super init];
    if (self) {
        _scale = [UIScreen mainScreen].scale;
        _width = width;
        _height = height;
        size_t pixelsWide = (size_t) (width * _scale + 0.5);
        size_t pixelsHigh = (size_t) (height * _scale + 0.5);

        CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();

        _context = CGBitmapContextCreate(NULL, pixelsWide, pixelsHigh, 8, 0, colorSpace, kCGImageAlphaPremultipliedLast);
        
        // This transform is necessary because Core Graphics LLO default coordinates
        CGAffineTransform transform = CGAffineTransformMakeTranslation(0.0, pixelsHigh);
        transform = CGAffineTransformScale(transform, 1.0, -1.0);
        CGContextSetTextMatrix(_context, transform);

        CGColorSpaceRelease(colorSpace);
        CGContextScaleCTM(_context, _scale, _scale);
    }
    return self;
}

- (id <RXGraphics2DInterface>)createGraphics2D {
    return [[Graphics2DI alloc] initWithContext:_context withScale:_scale];
}

- (CGImageRef)createCGImage {
    return CGBitmapContextCreateImage(_context);;
}

- (jint)getWidth {
    return _width;
}

- (jint)getHeight {
    return _height;
}

@end
