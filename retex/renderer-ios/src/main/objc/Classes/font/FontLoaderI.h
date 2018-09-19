#import "com/himamis/retex/renderer/share/platform/font/FontLoader.h"
#import <Foundation/Foundation.h>

@interface FontLoaderI : NSObject <RXFontLoader>

@property NSBundle* bundle;

-(instancetype) init;

@end
