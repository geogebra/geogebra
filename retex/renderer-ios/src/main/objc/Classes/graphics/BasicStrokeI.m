#import "BasicStrokeI.h"

@implementation BasicStrokeI

- (instancetype)initWithWidth:(double)width withMiterLimit:(double)miterLimit withCap:(int)cap withJoin:(int)join {
    self = [super init];
    if (self) {
        _width = width;
        _miterLimit = miterLimit;
        _cap = cap;
        _join = join;
    }
    return self;
}

- (CGLineCap)getNativeCap {
    switch (_cap) {
        case RXBasicStroke_CAP_BUTT:
            return kCGLineCapButt;
        case RXBasicStroke_CAP_ROUND:
            return kCGLineCapRound;
        case RXBasicStroke_CAP_SQUARE:
            return kCGLineCapSquare;
        default:
            return kCGLineCapButt;
    }
}

- (CGLineJoin)getNativeJoin {
    switch (_join) {
        case RXBasicStroke_JOIN_BEVEL:
            return kCGLineJoinBevel;
        case RXBasicStroke_JOIN_MITER:
            return kCGLineJoinMiter;
        case RXBasicStroke_JOIN_ROUND:
            return kCGLineJoinRound;
        default:
            return kCGLineJoinBevel;
    }
}

@end
