#import "Line2DI.h"

@implementation Line2DI

- (instancetype)init {
    self = [super init];
    if (self) {
        _startPoint = CGPointMake(0, 0);
        _endPoint = CGPointMake(0, 0);
    }
    return self;
}

- (instancetype)initWithX1:(double)x1 withY1:(double)y1 withX2:(double)x2 withY2:(double)y2 {
    self = [super init];
    if (self) {
        _startPoint = CGPointMake((CGFloat) x1, (CGFloat) y1);
        _endPoint = CGPointMake((CGFloat) x2, (CGFloat) y2);
    }
    return self;
}

- (void)setLineWithDouble:(jdouble)x1 withDouble:(jdouble)y1 withDouble:(jdouble)x2 withDouble:(jdouble)y2 {
    _startPoint.x = (CGFloat) x1;
    _startPoint.y = (CGFloat) y1;
    _endPoint.x = (CGFloat) x2;
    _endPoint.y = (CGFloat)y2;
}

- (jdouble)getX1 {
    return _startPoint.x;
}

- (jdouble)getY1 {
    return _startPoint.y;
}

- (jdouble)getX2 {
    return _endPoint.x;
}

- (jdouble)getY2 {
    return _endPoint.y;
}

@end
