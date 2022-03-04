#import "com/himamis/retex/renderer/share/ColorUtil.h"
#import <CoreText/CoreText.h>
#import "RoundRectangle2DI.h"
#import "Rectangle2DI.h"
#import "Graphics2DI.h"
#import "TransformI.h"
#import "Line2DI.h"
#import "ImageI.h"
#import "J2ObjC_source.h"

@implementation Graphics2DI

- (instancetype)init {
    self = [super init];
    if (self) {
        _font = [[FontI alloc] initWithName:@"Serif" withStyle:1 withSize:12];
        _color = (ColorI *) RXColorUtil_get_BLACK();
        _scale = 1.0;
    }
    return self;
}

- (instancetype)initWithContext:(CGContextRef)context {
    self = [self init];
    if (self) {
        _context = context;
    }
    return self;
}

- (instancetype)initWithContext:(CGContextRef)context withScale:(CGFloat)scale {
    self = [self initWithContext:context];
    if (self) {
        _scale = scale;
    }
    return self;
}

- (void)setStrokeWithRXStroke:(id <RXStroke>)stroke {
    _stroke = (BasicStrokeI *) stroke;

    CGContextSetLineCap(_context, [_stroke getNativeCap]);
    CGContextSetLineJoin(_context, [_stroke getNativeJoin]);
    CGContextSetMiterLimit(_context, [_stroke miterLimit]);
    CGContextSetLineWidth(_context, [_stroke width]);
    CGFloat* lengths = NULL;
    int count = 0;
    if (_stroke.dashes != nil) {
        lengths = [self convertDashes:_stroke.dashes];
        count = _stroke.dashes.length;
    }
    CGContextSetLineDash(_context, 0.0, lengths, count);
}

-(CGFloat*)convertDashes:(IOSDoubleArray*)dashes {
    CGFloat* floats = malloc(sizeof(CGFloat) * dashes.length);
    for (int i = 0; i < dashes.length; i++) {
        floats[i] = [dashes doubleAtIndex:i];
    }
    return floats;
}

- (id <RXStroke>)getStroke {
    return _stroke;
}

- (void)setColorWithRXColor:(id <RXColor>)color {
    _color = (ColorI *) color;

    CGContextSetStrokeColorWithColor(_context, [_color color]);
    CGContextSetFillColorWithColor(_context, [_color color]);
}

- (id <RXColor>)getColor {
    return _color;
}

- (id <RXFont>)getFont {
    return _font;
}

- (id <RXTransform>)getTransform {
    return [[TransformI alloc] initWithCGAffineTransform:CGContextGetCTM(_context) withScale:_scale];
}

- (void)setFontWithRXFont:(id <RXFont>)font {
    _font = (FontI *) font;
}

- (void)fillRectWithDouble:(jdouble)x withDouble:(jdouble)y withDouble:(jdouble)width withDouble:(jdouble)height {
    CGContextFillRect(_context, CGRectMake(x, y, width, height));
}

- (void)drawWithRXRectangle2D:(id <RXRectangle2D>)rectangle {
    CGContextStrokeRect(_context, [((Rectangle2DI *) rectangle) rectangle]);
}

- (void)drawWithRXRoundRectangle2D:(id <RXRoundRectangle2D>)rectangle {
    RoundRectangle2DI *roundRectangle = (RoundRectangle2DI *) rectangle;

    CGPathRef path = CGPathCreateWithRoundedRect([roundRectangle rectangle], (float) [roundRectangle arcWidth], (float) [roundRectangle arcHeight], NULL);

    CGContextAddPath(_context, path);
    CGContextStrokePath(_context);
    CFRelease(path);
}

- (void)drawWithRXLine2D:(id <RXLine2D>)line {
    Line2DI *local = (Line2DI *) line;
    CGPoint points[] = {[local startPoint], [local endPoint]};

    CGContextStrokeLineSegments(_context, points, 2);
}

- (void)drawCharsWithCharArray:(IOSCharArray *)data withInt:(jint)offset withInt:(jint)length withInt:(jint)x withInt:(jint)y {
    NSString *string = [NSString java_stringWithCharacters:data offset:offset length:length];

    [self drawStringWithString:string withX:x withY:y];
}

- (void)drawStringWithString:(NSString *)string withX:(int)x withY:(int)y {
    CGContextSaveGState(_context);
    
    CTFontRef font = [_font font];
    CGColorRef color = [_color color];
    NSDictionary *attributesDict = @{ (id) kCTFontAttributeName : (__bridge id) font,
                                      (id) kCTForegroundColorAttributeName : (__bridge id) color };
    
    // make the attributed string
    NSAttributedString *stringToDraw = [[NSAttributedString alloc] initWithString:string
                                                                       attributes:attributesDict];
    
    CTLineRef line = CTLineCreateWithAttributedString((__bridge CFAttributedStringRef) stringToDraw);
    
    CGPoint point = CGPointMake(x, y);
    
    CGContextSetTextPosition(_context, point.x, point.y);
    CTLineDraw(line, _context);
    
    CFRelease(line);
    
    CGContextRestoreGState(_context);
}

- (CGMutablePathRef)createArcPathWithWithInt:(jint)x withInt:(jint)y withInt:(jint)width withInt:(jint)height withInt:(jint)startAngle withInt:(jint)arcAngle {
    CGFloat cx = x + width * 0.5f;
    CGFloat cy = y + height * 0.5f;
    CGFloat radius = width * 0.5f;
    CGFloat startAngleRadian = startAngle * (float)M_PI / 180.0f;
    CGFloat sweepAngleRadian = arcAngle * (float)M_PI / 180.0f;

    CGMutablePathRef path = CGPathCreateMutable();
    CGAffineTransform transform = CGAffineTransformMakeTranslation(cx, cy);
    CGAffineTransform scale = CGAffineTransformMakeScale(1.0, height / width);

    transform = CGAffineTransformConcat(scale, transform);

    CGPathAddArc(path, &transform, 0, 0, radius, startAngleRadian, sweepAngleRadian, true);

    return path;
}

- (void)drawArcWithInt:(jint)x withInt:(jint)y withInt:(jint)width withInt:(jint)height withInt:(jint)startAngle withInt:(jint)arcAngle {
    CGMutablePathRef path = [self createArcPathWithWithInt:x withInt:y withInt:width withInt:height withInt:startAngle
                                                   withInt:arcAngle];

    CGContextAddPath(_context, path);
    CGContextStrokePath(_context);

    CFRelease(path);
}

- (void)fillArcWithInt:(jint)x withInt:(jint)y withInt:(jint)width withInt:(jint)height withInt:(jint)startAngle withInt:(jint)arcAngle {
    CGMutablePathRef path = [self createArcPathWithWithInt:x withInt:y withInt:width withInt:height withInt:startAngle
                                                   withInt:arcAngle];

    CGContextAddPath(_context, path);
    CGContextFillPath(_context);

    CFRelease(path);
}

- (void)translateWithDouble:(jdouble)x withDouble:(jdouble)y {
    CGContextTranslateCTM(_context, (float) x, (float) y);
}

- (void)scale__WithDouble:(jdouble)x withDouble:(jdouble)y {
    CGContextScaleCTM(_context, (float) x, (float) y);
}

- (void)rotateWithDouble:(jdouble)theta withDouble:(jdouble)x withDouble:(jdouble)y {
    [self translateWithDouble:x withDouble:y];
    [self rotateWithDouble:theta];
    [self translateWithDouble:-x withDouble:-y];
}

- (void)rotateWithDouble:(jdouble)theta {
    CGContextRotateCTM(_context, (float) theta);
}

- (void)drawImageWithRXImage:(id <RXImage>)image withInt:(jint)x withInt:(jint)y {
    ImageI *imageI = (ImageI *) image;
    CGImageRef imageRef = [imageI createCGImage];

    CGFloat width = CGImageGetWidth(imageRef);
    CGFloat height = CGImageGetHeight(imageRef);

    CGContextDrawImage(_context, CGRectMake(x, y, width, height), imageRef);
    
    //CFRelease(imageRef)
}

- (void)drawImageWithRXImage:(id <RXImage>)image withRXTransform:(id <RXTransform>)transform {
    [NSException raise:@"(void)drawImageWithRXImage:(id<RXImage>)image withRXTransform:(id<RXTransform>)transform not implemented!"
                format:@""];

}

- (jint)getRenderingHintWithInt:(jint)key {
    return -1;
}

- (void)dispose {
    ;
}

- (void)setRenderingHintWithInt:(jint)key withInt:(jint)value {
    ;
}

- (void)saveTransformation {
    CGContextSaveGState(_context);
}

- (void)restoreTransformation {
    CGContextRestoreGState(_context);
}

- (id <RXFontRenderContext>)getFontRenderContext {
    return nil;
}

- (void)fillWithRXShape:(id<RXShape>)rectangle {
    if ([rectangle isKindOfClass:[Rectangle2DI class]]) {
        CGContextFillRect(_context, [((Rectangle2DI *) rectangle) rectangle]);
    }
}

- (void)startDrawing {
    
}

- (void)moveToWithDouble:(jdouble)x
              withDouble:(jdouble)y {
    
}

- (void)lineToWithDouble:(jdouble)x
              withDouble:(jdouble)y {
    
}

- (void)quadraticCurveToWithDouble:(jdouble)x
                        withDouble:(jdouble)y
                        withDouble:(jdouble)x1
                        withDouble:(jdouble)y1 {
    
}

- (void)bezierCurveToWithDouble:(jdouble)x
                     withDouble:(jdouble)y
                     withDouble:(jdouble)x1
                     withDouble:(jdouble)y1
                     withDouble:(jdouble)x2
                     withDouble:(jdouble)y2 {
    
}

- (void)finishDrawing {
    
}

@end
