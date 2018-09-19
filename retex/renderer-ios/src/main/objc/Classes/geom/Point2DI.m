#import "Point2DI.h"

@implementation Point2DI

- (instancetype)init {
    self = [super init];
    if (self) {
        _point = CGPointMake(0, 0);
    }
    return self;
}

- (instancetype)initWithX:(double)x withY:(double)y {
    self = [super init];
    if (self) {
        _point = CGPointMake((CGFloat) x, (CGFloat) y);
    }
    return self;
}

- (jdouble)getX {
    return _point.x;
}

- (jdouble)getY {
    return _point.y;
}

- (void)setXWithDouble:(jdouble)x {
    _point.x = (CGFloat) x;
}

- (void)setYWithDouble:(jdouble)y {
    _point.y = (CGFloat) y;
}

@end
