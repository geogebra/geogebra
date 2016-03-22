#import "com/himamis/retex/renderer/share/exception/ParseException.h"
#import "LaTexView.h"
#import "TeXIcon.h"
#import "Color.h"
#import "ColorI.h"
#import "Graphics2DI.h"
#import "TeXFormula.h"
#import "TeXConstants.h"

@interface LaTexView () {
    RXTeXFormula *formula_;
    RXTeXIcon *texIcon_;
    RXTeXFormula_TeXIconBuilder *texIconBuilder_;
    Graphics2DI *graphics_;

    float sizeScale_;
}

- (void)ensureTexIconExists;

@end

@implementation LaTexView

@synthesize latexText = _latexText;
@synthesize size = _size;
@synthesize style = _style;
@synthesize latexForegroundColor = _latexForegroundColor;
@synthesize latexBackgroundColor = _backgroundColor;

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        _latexText = @"";
        _size = 20;
        _style = RXTeXConstants_STYLE_DISPLAY;
        _latexForegroundColor = [UIColor blackColor];
        _backgroundColor = [UIColor colorWithRed:0.0 green:0.0 blue:0.0
                                           alpha:0.0];
        _type = RXTeXFormula_SERIF;

        sizeScale_ = [UIScreen mainScreen].scale;
    }
    return self;
}

- (void)ensureTexIconExists {
    if (formula_ == nil) {
        @try {
            formula_ = [[RXTeXFormula alloc] initWithNSString:_latexText];
        }
        @catch (RXParseException *exception) {
            formula_ = [RXTeXFormula getPartialTeXFormulaWithNSString:_latexText];
        }
    }
    if (texIconBuilder_ == nil) {
        texIconBuilder_ = new_RXTeXFormula_TeXIconBuilder_initWithRXTeXFormula_(formula_);
    }
    if (texIcon_ == nil) {
        [texIconBuilder_ setSizeWithFloat:_size * sizeScale_];
        [texIconBuilder_ setStyleWithInt:_style];
        [texIconBuilder_ setTypeWithInt:_type];
        texIcon_ = [texIconBuilder_ build];
    }
}

- (void)setSize:(float)size {
    if (fabsf(size - _size) > 0.01) {
        _size = size;
        texIcon_ = nil;
        [self ensureTexIconExists];
        [self setNeedsDisplay];
    }
}

- (void)setLatexText:(NSString *)latexText {
    _latexText = latexText;
    formula_ = nil;
    texIconBuilder_ = nil;
    texIcon_ = nil;
    [self ensureTexIconExists];
    [self setNeedsDisplay];
}

- (void)setStyle:(int)style {
    if (_style != style) {
        _style = style;
        texIcon_ = nil;
        [self ensureTexIconExists];
        [self setNeedsDisplay];

    }
}

- (void)setForegroundColor:(UIColor *)foregroundColor {
    _latexForegroundColor = foregroundColor;
    [self setNeedsDisplay];
}

- (void)setBackgroundColor:(UIColor *)backgroundColor {
    _backgroundColor = backgroundColor;
    [self setNeedsDisplay];
}

- (void)setType:(int)type {
    if (_type != type) {
        _type = type;
        texIcon_ = nil;
        [self ensureTexIconExists];
        [self setNeedsDisplay];
    }
}


- (void)drawRect:(CGRect)rect {
    if (texIcon_ == nil) {
        return;
    }
    if (graphics_ == nil) {
        graphics_ = [[Graphics2DI alloc] init];
    }

    ColorI *foregroundColor = [[ColorI alloc] initWithCGColor:[_latexForegroundColor CGColor]];

    // fill background
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, [_backgroundColor CGColor]);
    CGContextFillRect(context, rect);

    // draw latex
    [graphics_ setContext:context];
    [texIcon_ setForegroundWithRXColor:foregroundColor];
    @try {
    [texIcon_ paintIconWithRXHasForegroundColor:nil
                      withRXGraphics2DInterface:graphics_ withInt:0
                                        withInt:0];
    }
    @catch(NSException* ex) {
        NSLog(@"%@", [ex reason]);
    }
        [graphics_ setContext:nil];
}

- (CGSize)sizeThatFits:(CGSize)size {
    return CGSizeMake([texIcon_ getIconWidth], [texIcon_ getIconHeight]);
}

@end



























