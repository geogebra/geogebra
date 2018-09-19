#import "com/himamis/retex/renderer/share/platform/parser/NamedNodeMap.h"
#import "org/w3c/dom/NamedNodeMap.h"

@interface NamedNodeMapI : NSObject <RXNamedNodeMap>

@property id <OrgW3cDomNamedNodeMap> namedNodeMap;

- (instancetype)initWithNamedNodeMap:(id <OrgW3cDomNamedNodeMap>)namedNodeMap;

@end
