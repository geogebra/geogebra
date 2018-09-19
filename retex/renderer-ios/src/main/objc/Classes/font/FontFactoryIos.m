#import "FontFactoryIos.h"
#import "FontLoaderI.h"
#import "TextLayoutI.h"
#import "TextAttributeProviderI.h"

@implementation FontFactoryIos

- (id <RXFont>)createFontWithNSString:(NSString *)name withInt:(jint)style withInt:(jint)size {
    return [[FontI alloc] initWithName:name withStyle:style withSize:size];
}

- (id <RXFontLoader>)createFontLoader {
    return [[FontLoaderI alloc] init];
}

- (id <RXTextAttributeProvider>)createTextAttributeProvider {
    return [[TextAttributeProviderI alloc] init];
}

- (id <RXTextLayout>)createTextLayoutWithNSString:(NSString *)string withRXFont:(id <RXFont>)font withRXFontRenderContext:(id <RXFontRenderContext>)fontRenderContext {
    return [[TextLayoutI alloc] initWithString:string withFont:font];
}

@end
