#import "NodeListI.h"
#import "NodeI.h"

@implementation NodeListI

- (instancetype)initWithNodeList:(id <OrgW3cDomNodeList>)nodeList {
    self = [super init];
    if (self) {
        _nodeList = nodeList;
    }
    return self;
}

- (jint)getLength {
    return [_nodeList getLength];
}

- (id <RXNode>)itemWithInt:(jint)index {
    return [[NodeI alloc] initWithNode:[_nodeList itemWithInt:index]];
}

@end
