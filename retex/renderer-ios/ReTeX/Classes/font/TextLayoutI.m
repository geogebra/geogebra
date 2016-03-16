#import "TextLayoutI.h"
#import "Graphics2DI.h"
#import "Rectangle2DI.h"

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
    Graphics2DI *g2d = (Graphics2DI *) graphics;


    [g2d drawStringWithString:_string withX:x withY:y];
}

- (id <RXRectangle2D>)getBounds {
    NSDictionary *attrsDictionary = @{NSFontAttributeName : _font};
    NSAttributedString *attributedString = [[NSAttributedString alloc] initWithString:_string
                                                                           attributes:attrsDictionary];
    NSTextStorage *textStorage = [[NSTextStorage alloc] initWithAttributedString:attributedString];
    NSLayoutManager *layoutManager = [[NSLayoutManager alloc] init];

    [textStorage addLayoutManager:layoutManager];

    NSTextContainer *textContainer = [[NSTextContainer alloc] init];
    [layoutManager addTextContainer:textContainer];
    CGRect rectangle = [layoutManager boundingRectForGlyphRange:NSMakeRange(0, [_string length])
                                                inTextContainer:textContainer];

    return [[Rectangle2DI alloc] initWithCGRect:rectangle];
}

@end
