#import "TextLayoutI.h"
#import "Graphics2DI.h"
#import "Rectangle2DI.h"
#import "NSString+Util.h"
#import <UIKit/UIKit.h>

@implementation TextLayoutI

- (instancetype)initWithString:(NSString *)string withFont:(FontI *)font {
    self = [super init];
    if (self) {
        _font = font;
        _string = string;
    }
    return self;
}

- (void)drawWithRXGraphics2DInterface:(id <RXGraphics2DInterface>)graphics withInt:(jint)x withInt:(jint)y {
    if ([graphics isKindOfClass:[Graphics2DI class]]) {
        Graphics2DI *g2d = (Graphics2DI *) graphics;
        id<RXFont> oldFont = [graphics getFont];
        
        [g2d setFontWithRXFont:_font];
        [g2d drawStringWithString:_string withX:x withY:y];
        [g2d setFontWithRXFont:oldFont];
    }
}

- (id <RXRectangle2D>)getBounds {
    CTFontRef font = [_font font];
    
    CGRect rectangle = [_string getBoundingBoxForFont:font];
    CGRect transform = CGRectMake(0, -rectangle.size.height, rectangle.size.width, rectangle.size.height);
    return [[Rectangle2DI alloc] initWithCGRect:transform];
}

@end
