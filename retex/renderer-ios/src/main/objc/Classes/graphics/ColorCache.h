#import <Foundation/Foundation.h>

@class ColorI;

@interface ColorCache : NSObject

+ (instancetype)instance;

- (ColorI *)getColorWithRGBA:(unsigned int) rgba;

- (ColorI *)getColorWithR:(int)red withG:(int)green withB:(int)blue withA:(int)alpha;

@end
