#import <Foundation/Foundation.h>
#import "Graphics2DInterface.h"
#import "FontI.h"
#import "ColorI.h"
#import "BasicStrokeI.h"
#import <CoreGraphics/CoreGraphics.h>

@interface Graphics2DI : NSObject <RXGraphics2DInterface>

@property CGContextRef context;
@property CGRect frame;
@property(retain) FontI *font;
@property(retain) ColorI *color;
@property(retain) BasicStrokeI *stroke;

- (instancetype)initWithContext:(CGContextRef)context;

- (void)drawStringWithString:(NSString *)string withX:(int)x withY:(int)y;

@end
