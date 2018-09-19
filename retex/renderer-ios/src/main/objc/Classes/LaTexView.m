#import "com/himamis/retex/renderer/share/exception/ParseException.h"
#import "com/himamis/retex/renderer/share/TeXIcon.h"
#import "com/himamis/retex/renderer/share/TeXFormula.h"
#import "com/himamis/retex/renderer/share/TeXConstants.h"
#import "ColorCache.h"
#import "UIColor+RGBA.h"
#import "LaTexView.h"
#import "ColorI.h"
#import "Graphics2DI.h"
@import UILibrary;

@interface LaTexView () {
    RXTeXFormula *formula_;
    RXTeXIcon *texIcon_;
    RXTeXFormula_TeXIconBuilder *texIconBuilder_;
    Graphics2DI *graphics_;

    float sizeScale_;
}

- (void)ensureTexIconExists;

- (void)setup;

@end

@implementation LaTexView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self setup];
    }
    return self;
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self setup];
    }
    return self;
}

- (void) setup {
    _latexText = @"";
    _size = 20;
    _style = RXTeXConstants_get_STYLE_DISPLAY();
    _foregroundColor = [UIColor blackColor];
    _backgroundColor = [UIColor whiteColor];
    
    _type = RXTeXFormula_get_SERIF();
    
    sizeScale_ = [UIFont getScale:20];
    
    self.contentMode = UIViewContentModeRedraw;
}

- (void)ensureTexIconExists {
    if (formula_ == nil) {
        @try {
            formula_ = [[RXTeXFormula alloc] initWithNSString:_latexText];
        }
        @catch (NSException *exception) {
            formula_ = [RXTeXFormula getPartialTeXFormulaWithNSString:_latexText];
        }
    }
    if (texIconBuilder_ == nil) {
        texIconBuilder_ = new_RXTeXFormula_TeXIconBuilder_initWithRXTeXFormula_(formula_);
    }
    if (texIcon_ == nil) {
        [texIconBuilder_ setSizeWithDouble:_size * sizeScale_];
        [texIconBuilder_ setStyleWithInt:_style];
        [texIconBuilder_ setTypeWithInt:_type];
        @try {
            texIcon_ = [texIconBuilder_ build];
        }
        @catch (NSException* ex) {
            NSLog(@"%@", [ex callStackSymbols]);
        }
    }
}

- (void)setSize:(CGFloat)size {
    if (fabs(size - _size) > 0.01 || fabs(sizeScale_ - [UIFont getScale:size]) > 0.01) {
        _size = size;
        sizeScale_ = [UIFont getScale:size];
        texIcon_ = nil;
        [self invalidateIntrinsicContentSize];
        [self ensureTexIconExists];
        [self setNeedsDisplay];
    }
}

- (void)setLatexText:(NSString *)latexText {
    _latexText = latexText;
    formula_ = nil;
    texIconBuilder_ = nil;
    texIcon_ = nil;
    [self invalidateIntrinsicContentSize];
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
    _foregroundColor = foregroundColor;
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

    ColorI *foregroundColor = [[ColorCache instance] getColorWithRGBA:[_foregroundColor rgba]];

    // fill background
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    CGAffineTransform transform = CGAffineTransformIdentity;
    transform = CGAffineTransformTranslate(transform, 0.0f, self.bounds.size.height);
    transform = CGAffineTransformScale(transform, 1.0f, -1.0f);
    CGContextSetTextMatrix(context, transform);
    
    CGContextSetFillColorWithColor(context, [_backgroundColor CGColor]);
    CGContextFillRect(context, rect);

    // draw latex
    [graphics_ setContext:context];
    [texIcon_ setForegroundWithRXColor:foregroundColor];
    int dy = (self.bounds.size.height- texIcon_.getIconHeight)/2;
    @try {
        [texIcon_ paintIconWithRXHasForegroundColor:nil withRXGraphics2DInterface:graphics_ withInt:0 withInt:dy];
    }
    @catch(NSException* ex) {
        NSLog(@"%@", [ex reason]);
    }
        [graphics_ setContext:nil];
}

- (CGSize)sizeThatFits:(CGSize)size {
    return [self intrinsicContentSize];
}

- (CGSize)intrinsicContentSize {
    return CGSizeMake([texIcon_ getIconWidth], [texIcon_ getIconHeight]);
}

@end



























