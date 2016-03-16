#import <UIKit/UIKit.h>
#import "Color.h"

@interface ColorI : NSObject <RXColor>

@property CGColorRef color;

- (instancetype)initWithRGB:(unsigned int)rgb;

- (instancetype)initWithR:(int)r withG:(int)g withB:(int)b;

- (instancetype)initWithFloatR:(float)r withG:(float)g withB:(float)b;

- (instancetype)initWithCGColor:(CGColorRef)color;

@end
