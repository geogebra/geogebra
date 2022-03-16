#import "com/himamis/retex/renderer/share/platform/graphics/BasicStroke.h"
#import "IOSPrimitiveArray.h"
#import <CoreGraphics/CoreGraphics.h>

@interface BasicStrokeI : NSObject <RXBasicStroke>

@property double width;
@property double miterLimit;
@property int cap;
@property int join;
@property IOSDoubleArray* dashes;

- (instancetype)initWithWidth:(double)width withDashes:(IOSDoubleArray*)dashes;

- (instancetype)initWithWidth:(double)width withMiterLimit:(double)miterLimit withCap:(int)cap withJoin:(int)join;

- (instancetype)initWithWidth:(double)width withMiterLimit:(double)miterLimit withCap:(int)cap withJoin:(int)join withDashes:(IOSDoubleArray*)dashes;

- (CGLineCap)getNativeCap;

- (CGLineJoin)getNativeJoin;

@end
