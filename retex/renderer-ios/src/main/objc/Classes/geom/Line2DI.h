#import "com/himamis/retex/renderer/share/platform/geom/Line2D.h"
#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>

@interface Line2DI : NSObject <RXLine2D>

@property CGPoint startPoint;
@property CGPoint endPoint;

- (instancetype)initWithX1:(double)x1 withY1:(double)y1 withX2:(double)x2 withY2:(double)y2;

@end
