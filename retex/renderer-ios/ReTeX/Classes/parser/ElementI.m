#import "ElementI.h"
#import "NodeListI.h"
#import "NamedNodeMapI.h"

@implementation ElementI

- (instancetype)initWithElement:(id <OrgW3cDomElement>)element {
    self = [super initWithNode:element];
    if (self) {
        _element = element;
    }
    return self;
}

- (id <RXNodeList>)getElementsByTagNameWithNSString:(NSString *)name {
    return [[NodeListI alloc] initWithNodeList:[_element getElementsByTagNameWithNSString:name]];
}

- (NSString *)getAttributeWithNSString:(NSString *)name {
    return [_element getAttributeWithNSString:name];
}

- (NSString *)getTagName {
    return [_element getTagName];
}

- (id <RXNodeList>)getChildNodes {
    return [[NodeListI alloc] initWithNodeList:[_element getChildNodes]];
}

- (id <RXNamedNodeMap>)getAttributes {
    return [[NamedNodeMapI alloc] initWithNamedNodeMap:[_element getAttributes]];
}

- (jboolean)isNull {
    return _element == nil;
}

@end
