#import "com/himamis/retex/renderer/share/platform/graphics/Transform.h"
#import <CoreGraphics/CoreGraphics.h>

@interface TransformI : NSObject <RXTransform>

@property CGAffineTransform transform;

- (instancetype)initWithCGAffineTransform:(CGAffineTransform)transform;
- (instancetype)initWithCGAffineTransform:(CGAffineTransform)transform withScale:(CGFloat)scale;

@end
