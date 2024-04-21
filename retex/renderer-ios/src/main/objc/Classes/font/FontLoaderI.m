#import "FontLoaderI.h"

#import "FontI.h"
#import "GeoGebraLibrary-Swift.h"

@implementation FontLoaderI

- (instancetype)init {
    self = [super init];
    if (self) {
        NSString* bundlePath = [NSBundle geoGebraResourcesPath];
        NSString* rendererBundlePath = [bundlePath stringByAppendingPathComponent:@"Assets"];
        _bundle = [NSBundle bundleWithPath:rendererBundlePath];
    }
    return self;
}

- (id<RXFont>)loadFontWithNSString:(NSString *)name {
    NSString* fontPath = [_bundle pathForResource:name ofType:nil];
    
    NSURL *url = [NSURL URLWithString:name];
    NSArray *arry = [url pathComponents];
    NSString *fontName = (NSString *) arry[[arry count] - 1];

    fontName = [fontName substringToIndex:[fontName length] - 4];

    return [[FontI alloc] initWithName:fontName withSize:1 withPath:fontPath];
}

@end
