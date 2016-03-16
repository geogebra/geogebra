#import "RoundRectangle2DI.h"

@implementation RoundRectangle2DI

- (instancetype)init {
    self = [super init];
    if (self) {
        [self setRoundRectangleWithDouble:0 withDouble:0 withDouble:0 withDouble:0 withDouble:0 withDouble:0];
    }
    return self;
}

- (instancetype)initWithX:(double)x withY:(double)y withWidth:(double)w withHeight:(double)h withArcWidth:(double)arcWidth withArcHeight:(double)arcHeight {
    self = [super init];
    if (self) {
        [self setRoundRectangleWithDouble:x withDouble:y withDouble:w withDouble:h withDouble:arcWidth
                               withDouble:arcHeight];
    }
    return self;
}

- (jdouble)getArcW {
    return _arcWidth;
}

- (jdouble)getArcH {
    return _arcHeight;
}

- (jdouble)getX {
    return _rectangle.origin.x;
}

- (jdouble)getY {
    return _rectangle.origin.y;
}

- (jdouble)getWidth {
    return _rectangle.size.width;
}

- (jdouble)getHeight {
    return _rectangle.size.height;
}

- (void)setRoundRectangleWithDouble:(jdouble)x withDouble:(jdouble)y withDouble:(jdouble)w withDouble:(jdouble)h withDouble:(jdouble)arcw withDouble:(jdouble)arch {
    _rectangle = CGRectMake((CGFloat) x, (CGFloat) y, (CGFloat) w, (CGFloat) h);
    _arcHeight = arch;
    _arcWidth = arcw;
}

@end
