#import "com/himamis/retex/renderer/share/platform/geom/RoundRectangle2D.h"
#import <CoreGraphics/CoreGraphics.h>

@interface RoundRectangle2DI : NSObject <RXRoundRectangle2D>

@property CGRect rectangle;
@property double arcWidth;
@property double arcHeight;

- (instancetype)initWithX:(double)x withY:(double)y withWidth:(double)w withHeight:(double)h withArcWidth:(double)arcWidth withArcHeight:(double)arcHeight;

@end
