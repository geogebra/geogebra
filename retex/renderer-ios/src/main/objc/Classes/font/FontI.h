#import "com/himamis/retex/renderer/share/platform/font/Font.h"
#import <CoreText/CoreText.h>

@interface FontI : NSObject <RXFont>

@property CTFontRef font;
@property NSString* name;
@property int size;

- (instancetype)initWithName:(NSString *)name withSize:(int)s withPath:(NSString*) path;

- (instancetype)initWithName:(NSString *)name withStyle:(int) style withSize:(int)s;

@end
