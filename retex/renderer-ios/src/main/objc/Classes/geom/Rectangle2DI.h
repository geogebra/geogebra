#import "com/himamis/retex/renderer/share/platform/geom/Rectangle2D.h"
#import <CoreGraphics/CoreGraphics.h>

@interface Rectangle2DI : NSObject <RXRectangle2D>

@property CGRect rectangle;

- (instancetype)initWithCGRect:(CGRect)rectangle;

- (instancetype)initWithX:(double)x withY:(double)y withWidth:(double)w withHeight:(double)h;

@end
