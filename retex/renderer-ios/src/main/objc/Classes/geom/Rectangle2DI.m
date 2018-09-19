#import "Rectangle2DI.h"

@implementation Rectangle2DI

- (instancetype)init {
    self = [super init];
    if (self) {
        _rectangle = CGRectMake(0, 0, 0, 0);
    }
    return self;
}

- (instancetype)initWithCGRect:(CGRect)rectangle {
    self = [super init];
    if (self) {
        _rectangle = rectangle;
    }
    return self;
}

- (id)initWithX:(double)x withY:(double)y withWidth:(double)w withHeight:(double)h {
    self = [super init];
    if (self) {
        _rectangle = CGRectMake((CGFloat) x, (CGFloat) y, (CGFloat) w, (CGFloat) h);
    }
    return self;
}

- (void)setRectangleWithDouble:(jdouble)x withDouble:(jdouble)y withDouble:(jdouble)width withDouble:(jdouble)height {
    _rectangle = CGRectMake((CGFloat) x, (CGFloat) y, (CGFloat) width, (CGFloat) height);
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

- (id<RXRectangle2D>)getBounds2DX {
    return self;
}

@end
