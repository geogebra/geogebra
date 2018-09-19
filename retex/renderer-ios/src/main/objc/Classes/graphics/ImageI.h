#import "com/himamis/retex/renderer/share/platform/graphics/Image.h"
#import <CoreGraphics/CoreGraphics.h>

@interface ImageI : NSObject <RXImage>

@property (nonatomic) CGContextRef context;
@property (readonly) float scale;

- (instancetype)initWithWidth:(int)width withHeight:(int)height;

- (CGImageRef)createCGImage;

@end
