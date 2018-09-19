#import "com/himamis/retex/renderer/share/platform/graphics/BasicStroke.h"
#import <CoreGraphics/CoreGraphics.h>

@interface BasicStrokeI : NSObject <RXBasicStroke>

@property float width;
@property float miterLimit;
@property int cap;
@property int join;

- (instancetype)initWithWidth:(float)width withMiterLimit:(float)miterLimit withCap:(int)cap withJoin:(int)join;

- (CGLineCap)getNativeCap;

- (CGLineJoin)getNativeJoin;

@end
