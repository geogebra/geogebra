#import "com/himamis/retex/renderer/share/platform/parser/Node.h"
#import "org/w3c/dom/Node.h"

@interface NodeI : NSObject <RXNode>

@property id <OrgW3cDomNode> node;

- (instancetype)initWithNode:(id <OrgW3cDomNode>)node;

@end
