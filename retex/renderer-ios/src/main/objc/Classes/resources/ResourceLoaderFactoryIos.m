#import "ResourceLoaderFactoryIos.h"
#import "ResourceLoaderI.h"

@implementation ResourceLoaderFactoryIos

- (id <RXResourceLoader>)createResourceLoader {
    return [[ResourceLoaderI alloc] init];
}

@end
