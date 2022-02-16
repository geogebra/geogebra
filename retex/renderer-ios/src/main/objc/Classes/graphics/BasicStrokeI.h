#import "com/himamis/retex/renderer/share/platform/graphics/BasicStroke.h"
#import <CoreGraphics/CoreGraphics.h>

@interface BasicStrokeI : NSObject <RXBasicStroke>

@property double width;
@property double miterLimit;
@property int cap;
@property int join;

- (instancetype)initWithWidth:(double)width withMiterLimit:(double)miterLimit withCap:(int)cap withJoin:(int)join;

- (CGLineCap)getNativeCap;

- (CGLineJoin)getNativeJoin;

@end
