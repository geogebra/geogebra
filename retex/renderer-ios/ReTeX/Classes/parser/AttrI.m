#import "AttrI.h"

@implementation AttrI

- (instancetype)initWithAttr:(id <OrgW3cDomAttr>)attr {
    self = [super initWithNode:attr];
    if (self) {
        _attr = attr;
    }
    return self;
}

- (NSString *)getName {
    return [_attr getName];
}

- (jboolean)isSpecified {
    return [_attr getSpecified];
}

- (NSString *)getValue {
    return [_attr getValue];
}

@end
