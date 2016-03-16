#import <Foundation/Foundation.h>
#import "Font.h"
#import <CoreText/CoreText.h>
#import <UIKit/UIKit.h>

@interface FontI : NSObject <RXFont>

@property UIFont *font;
@property int size;

- (instancetype)initWithName:(NSString *)name withStyle:(int)style withSize:(int)s;

@end
