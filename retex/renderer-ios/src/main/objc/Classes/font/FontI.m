#import "FontI.h"
#import <CoreText/CoreText.h>
#import <UIKit/UIKit.h>

@interface FontI () {
    NSString *_baseName;
    int _style;
}

- (CTFontRef) loadFontWithPath:(NSString*) path withSize:(int) size;

- (NSString*) appendStyleName:(NSString*) name withStyle:(int) style;

@end

@implementation FontI

- (void) dealloc {
    CFRelease(_font);
}

- (instancetype)initWithName:(NSString *)name withSize:(int)s withPath:(NSString *)path {
    self = [super init];
    if (self) {
        _size = s;
        _font = [self loadFontWithPath:path withSize:s];
        _name = name;
        _baseName = nil;
        _style = -1;
    }
    return self;
}

- (instancetype)initWithName:(NSString *)name withStyle:(int)style withSize:(int)s {
    self = [super init];
    if (self) {
        _baseName = name;
        _style = style;
        if ([name isEqualToString:@"Serif"]) {
            name = [self appendStyleName:@"Georgia" withStyle:style];
        } else if ([name isEqualToString:@"SansSerif"]) {
            name = [self appendStyleName:@"ArialMT" withStyle:style];
        } else {
            [NSException raise:@"Invalid font name" format:@"Font name of %@ is invalid", name];
        }
        _font = CTFontCreateWithName((CFStringRef) name, s, nil);
        _name = name;
        _size = s;
    }
    return self;
}

- (NSString *)appendStyleName:(NSString *)name withStyle:(int)style {
    NSString* nameWithStyle;
    if (style == RXFont_get_BOLD()) {
        nameWithStyle = [name stringByAppendingString:@"-Bold"];
    } else if (style == RXFont_get_ITALIC()) {
        nameWithStyle = [name stringByAppendingString:@"-Italic"];
    } else if (style == (RXFont_get_BOLD() | RXFont_get_ITALIC())) {
        nameWithStyle = [name stringByAppendingString:@"-BoldItalic"];
    } else {
        nameWithStyle = name;
    }
    return nameWithStyle;
}

- (CTFontRef) loadFontWithPath:(NSString *)path withSize:(int)size {
    NSData *data = [[NSData alloc] initWithContentsOfFile:path];
    CGDataProviderRef fontProvider = CGDataProviderCreateWithCFData((CFDataRef)data);
    CGFontRef fontRef = CGFontCreateWithDataProvider(fontProvider);
    
    CGDataProviderRelease(fontProvider);
    CTFontRef ctFontRef = CTFontCreateWithGraphicsFont(fontRef, size, nil, nil);
    CFRelease(fontRef);
    return ctFontRef;
}

- (id <RXFont>)deriveFontWithInt:(jint)type {
    if (type != _style) {
        return [[FontI alloc] initWithName:_baseName withStyle:type withSize:_size];
    } else {
        return self;
    }
}

- (id <RXFont>)deriveFontWithJavaUtilMap:(id <JavaUtilMap>)map {
    [NSException raise:@"Not implemented" format:@"Function %s is not implemented!", __FUNCTION__];
    return nil;
}

- (jboolean)isEqualWithRXFont:(id<RXFont>)f {
    FontI* font = (FontI*) f;
    return _font == font.font;
}

- (jint)getScale {
    return 1;
}

- (id<RXShape>)getGlyphOutlineWithRXFontRenderContext:(id<RXFontRenderContext>)frc withNSString:(NSString *)valueOf {
    return nil;
}

- (jboolean)canDisplayWithChar:(jchar)ch {
    return true;
}

- (id<RXShape>)getGlyphOutlineWithRXFontRenderContext:(id<RXFontRenderContext>)frc
                                       withRXCharFont:(RXCharFont *)cf {
    return nil;
}

- (jboolean)canDisplayWithInt:(jint)c {
    return true;
}



@end
