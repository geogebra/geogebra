#import "TextLayout.h"
#import "FontI.h"

@interface TextLayoutI : NSObject <RXTextLayout>

@property(retain) NSString *string;
@property(retain) FontI *font;

- (instancetype)initWithString:(NSString *)string withFont:(FontI *)font;

@end
