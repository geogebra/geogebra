#import "BasicStrokeI.h"

@implementation BasicStrokeI

- (instancetype)initWithWidth:(double)width withDashes:(IOSDoubleArray *)dashes {
    return [self initWithWidth:width withMiterLimit:10 withCap:RXBasicStroke_get_CAP_BUTT() withJoin:RXBasicStroke_get_JOIN_MITER() withDashes:dashes];
}

-(instancetype)initWithWidth:(double)width withMiterLimit:(double)miterLimit withCap:(int)cap withJoin:(int)join {
    return [self initWithWidth:width withMiterLimit:miterLimit withCap:cap withJoin:join withDashes:nil];
}

- (instancetype)initWithWidth:(double)width withMiterLimit:(double)miterLimit withCap:(int)cap withJoin:(int)join withDashes:(IOSDoubleArray*)dashes {
    self = [super init];
    if (self) {
        _width = width;
        _miterLimit = miterLimit;
        _cap = cap;
        _join = join;
        _dashes = dashes;
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
