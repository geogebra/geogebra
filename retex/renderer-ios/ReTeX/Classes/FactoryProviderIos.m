#import "FactoryProviderIos.h"
#import "GeomFactoryIos.h"
#import "FontFactoryIos.h"
#import "GraphicsFactoryIos.h"
#import "ParserFactoryIos.h"
#import "ResourceLoaderFactoryIos.h"

@implementation FactoryProviderIos

- (RXFontFactory *)createFontFactory {
    return [[FontFactoryIos alloc] init];
}

- (RXGeomFactory *)createGeomFactory {
    return [[GeomFactoryIos alloc] init];
}

- (RXGraphicsFactory *)createGraphicsFactory {
    return [[GraphicsFactoryIos alloc] init];
}

- (RXParserFactory *)createParserFactory {
    return [[ParserFactoryIos alloc] init];
}

- (id <RXResourceLoaderFactory>)createResourceLoaderFactory {
    return [[ResourceLoaderFactoryIos alloc] init];
}

@end
