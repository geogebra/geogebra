#import "NamedNodeMapI.h"
#import "NodeI.h"

@implementation NamedNodeMapI

- (instancetype)initWithNamedNodeMap:(id <OrgW3cDomNamedNodeMap>)namedNodeMap {
    self = [super init];
    if (self) {
        _namedNodeMap = namedNodeMap;
    }
    return self;
}

- (jint)getLength {
    return [_namedNodeMap getLength];
}

- (id <RXNode>)itemWithInt:(jint)index {
    return [[NodeI alloc] initWithNode:[_namedNodeMap itemWithInt:index]];
}

@end
