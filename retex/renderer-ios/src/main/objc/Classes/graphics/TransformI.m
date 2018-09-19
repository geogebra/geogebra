#import "TransformI.h"

@implementation TransformI

- (instancetype)init {
    self = [super init];
    if (self) {
        _transform = CGAffineTransformIdentity;
    }
    return self;
}

- (instancetype)initWithCGAffineTransform:(CGAffineTransform)transform {
    self = [super init];
    if (self) {
        _transform = CGAffineTransformMake(transform.a, transform.b, transform.c, transform.d, transform.tx, transform.ty);
    }
    return self;
}

- (instancetype)initWithCGAffineTransform:(CGAffineTransform)transform withScale:(CGFloat)scale {
    self = [self initWithCGAffineTransform:transform];
    if (self) {
        [self scale__WithDouble:scale withDouble:scale];
    }
    return self;
}

- (jdouble)getTranslateX {
    return _transform.tx;
}

- (jdouble)getTranslateY {
    return _transform.ty;
}

- (jdouble)getScaleX {
    return _transform.a;
}

- (jdouble)getScaleY {
    return _transform.d;
}

- (jdouble)getShearX {
    return _transform.c;
}

- (jdouble)getShearY {
    return _transform.b;
}

- (id <RXTransform>)createClone {
    return [[TransformI alloc] initWithCGAffineTransform:_transform];
}

- (void)scale__WithDouble:(jdouble)sx withDouble:(jdouble)sy {
    _transform = CGAffineTransformConcat(_transform, CGAffineTransformMakeScale((CGFloat) sx, (CGFloat) sy));
}

- (void)translateWithDouble:(jdouble)tx withDouble:(jdouble)ty {
    _transform = CGAffineTransformConcat(_transform, CGAffineTransformMakeTranslation((CGFloat) tx, (CGFloat) ty));
}

- (void)shearWithDouble:(jdouble)sx withDouble:(jdouble)sy {
    CGAffineTransform operation = CGAffineTransformMake(1.0f, (CGFloat) sx, (CGFloat) sy, 1.0f, 0.0f, 0.0f);

    _transform = CGAffineTransformConcat(_transform, operation);
}

@end
