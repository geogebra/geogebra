#import "java/io/ByteArrayInputStream.h"
#import "java/io/InputStream.h"
#import "java/io/File.h"
#import "ResourceLoaderI.h"
#import "J2ObjC_source.h"

@implementation ResourceLoaderI

- (instancetype)init {
    self = [super init];
    if (self) {
        NSString* bundlePath = [[NSBundle mainBundle] pathForResource:@"GeoGebraResources" ofType:@"bundle"];
        NSString* rendererBundlePath = [bundlePath stringByAppendingPathComponent:@"ReTeX/Renderer/Assets"];
        _bundle = [NSBundle bundleWithPath:rendererBundlePath];
    }
    return self;
}

- (JavaIoInputStream *)loadResourceWithNSString:(NSString *)path {
    NSString* fullPath = [_bundle pathForResource:path ofType:nil];

    JavaIoFile *file = [[JavaIoFile alloc] initWithNSString:fullPath];

    NSData *data = [NSData dataWithContentsOfFile:[file getAbsolutePath]];
    IOSByteArray *byteArray = [IOSByteArray arrayWithNSData:data];

    return [[JavaIoByteArrayInputStream alloc] initWithByteArray:byteArray];
}

@end
