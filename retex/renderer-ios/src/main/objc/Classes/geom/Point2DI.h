#import "com/himamis/retex/renderer/share/platform/geom/Point2D.h"
#import <CoreGraphics/CoreGraphics.h>

@interface Point2DI : NSObject <RXPoint2D>

@property CGPoint point;

- (instancetype)initWithX:(double)x withY:(double)y;

@end
