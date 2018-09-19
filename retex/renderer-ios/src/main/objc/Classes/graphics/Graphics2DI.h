#import "com/himamis/retex/renderer/share/platform/graphics/Graphics2DInterface.h"
#import <CoreGraphics/CoreGraphics.h>
#import "FontI.h"
#import "ColorI.h"
#import "BasicStrokeI.h"

@interface Graphics2DI : NSObject <RXGraphics2DInterface>

@property CGContextRef context;
@property(retain) FontI *font;
@property(retain) ColorI *color;
@property(retain) BasicStrokeI *stroke;

/* Use this, when the context is scaled when passed to this object */
@property CGFloat scale;

- (instancetype)initWithContext:(CGContextRef)context;
- (instancetype)initWithContext:(CGContextRef)context withScale:(CGFloat)scale;

- (void)drawStringWithString:(NSString *)string withX:(int)x withY:(int)y;

@end
