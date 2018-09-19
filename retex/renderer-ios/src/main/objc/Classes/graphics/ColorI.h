#import "com/himamis/retex/renderer/share/platform/graphics/Color.h"
#import <CoreGraphics/CoreGraphics.h>

@interface ColorI : NSObject <RXColor>

@property CGColorRef color;

- (instancetype)initWithRGBA:(unsigned int)rgba;

- (instancetype)initWithR:(int)r withG:(int)g withB:(int)b;

- (instancetype)initWithFloatR:(float)r withG:(float)g withB:(float)b withA:(float)a;

- (instancetype)initWithCGColor:(CGColorRef)color;

@end
