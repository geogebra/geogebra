#import "ColorCache.h"
#import "ColorI.h"

@interface ColorCache ()

@property NSMutableDictionary* cache;

@end

@implementation ColorCache

+ (instancetype)instance {
    static ColorCache *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[ColorCache alloc] init];
    });
    return instance;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _cache = [[NSMutableDictionary alloc] init];
    }
    return self;
}

- (ColorI *)getColorWithRGBA:(unsigned int)rgba {
    NSNumber *key = [NSNumber numberWithInt:rgba];
    ColorI *color = [_cache objectForKey:key];
    if (color == nil) {
        color = [[ColorI alloc] initWithRGBA:rgba];
        [_cache setObject:color forKey:key];
    }
    return color;
}
   
- (ColorI *)getColorWithR:(int)red withG:(int)green withB:(int)blue withA:(int)alpha {
    unsigned int rgba = ((255 * 256 + red) * 256 + green) * 256 + blue;
    return [self getColorWithRGBA:rgba];
}

@end
