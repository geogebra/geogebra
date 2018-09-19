#import "UIColor+RGBA.h"

@implementation UIColor (RGBA)

- (unsigned int)rgba {
    CGFloat r, g, b, a;
    bool success = [self getRed: &r green:&g blue:&b alpha:&a];
    if (success) {
        int red = r * 255;
        int green = g * 255;
        int blue = b * 255;
        int alpha = a * 255;
        return (((alpha * 256) + red) * 256 + green) * 256 + blue;
    } else {
        [[NSException exceptionWithName:@"Error" reason:@"Cannot convert UIColor to RGBA value" userInfo:nil] raise];
    }
    return 0;
}

@end
