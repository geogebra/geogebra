#import "FontLoaderI.h"
#import "FontI.h"

@implementation FontLoaderI

- (id <RXFont>)loadFontWithId:(id)fontInt withNSString:(NSString *)name {
    NSURL *url = [NSURL URLWithString:name];
    NSArray *arry = [url pathComponents];
    NSString *fontName = (NSString *) arry[[arry count] - 1];

    fontName = [fontName substringToIndex:[fontName length] - 4];

    return [[FontI alloc] initWithName:fontName withStyle:0 withSize:1];
}

@end
