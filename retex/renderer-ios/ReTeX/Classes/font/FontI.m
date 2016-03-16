#import "FontI.h"

@implementation FontI

- (instancetype)initWithName:(NSString *)name withStyle:(int)style withSize:(int)s {
    self = [super init];
    if (self) {
        _size = s;
        _font = [UIFont fontWithName:name size:s];
    }

    return self;
}

- (id <RXFont>)deriveFontWithInt:(jint)type {
    return nil;
}

- (id <RXFont>)deriveFontWithJavaUtilMap:(id <JavaUtilMap>)map {
    return nil;
}

@end
