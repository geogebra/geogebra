#import "ResourceLoaderI.h"
#import "java/io/File.h"
#import "java/io/InputStream.h"
#import "java/io/ByteArrayInputStream.h"
#import "J2ObjC_source.h"

@implementation ResourceLoaderI

@synthesize mNSBundle = _mNSBundle;

- (instancetype)init {
    _mNSBundle = [NSBundle mainBundle];
    return self;
}

- (JavaIoInputStream *)loadResourceWithId:(id)base withNSString:(NSString *)path {
    NSURL *url = [NSURL URLWithString:path];
    NSArray *arry = [url pathComponents];

    path = [_mNSBundle pathForResource:(NSString *) arry[[arry count] - 1] ofType:nil];

    JavaIoFile *f = [[JavaIoFile alloc] initWithNSString:path];

    NSData *mNSData = [NSData dataWithContentsOfFile:[f getAbsolutePath]];
    NSUInteger length = [mNSData length];
    uint8_t *bytes = malloc(sizeof(*bytes) * length);
    [mNSData getBytes:bytes length:length];
    IOSByteArray *iosByteArray = [IOSByteArray arrayWithLength:length];

    for (int i = 0; i < length; i++) {
        [iosByteArray replaceByteAtIndex:i withByte:bytes[i]];
    }
    return [[JavaIoByteArrayInputStream alloc] initWithByteArray:iosByteArray];
}

@end
