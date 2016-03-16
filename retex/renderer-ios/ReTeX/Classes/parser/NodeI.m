#import "NodeI.h"
#import "AttrI.h"
#import "ElementI.h"

@implementation NodeI

- (instancetype)initWithNode:(id <OrgW3cDomNode>)node {
    self = [super init];
    if (self) {
        _node = node;
    }
    return self;
}

- (jshort)getNodeType {
    return [_node getNodeType];
}

- (id <RXAttr>)castToAttr {
    return [[AttrI alloc] initWithAttr:(id <OrgW3cDomAttr>) _node];

}

- (id <RXElement>)castToElement {
    return [[ElementI alloc] initWithElement:(id <OrgW3cDomElement>) _node];

}

@end
