#import "com/himamis/retex/renderer/share/platform/parser/NodeList.h"
#import "org/w3c/dom/NodeList.h"

@interface NodeListI : NSObject <RXNodeList>

@property id <OrgW3cDomNodeList> nodeList;

- (instancetype)initWithNodeList:(id <OrgW3cDomNodeList>)nodeList;

@end
