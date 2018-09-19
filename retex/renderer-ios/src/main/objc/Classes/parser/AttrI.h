#import "com/himamis/retex/renderer/share/platform/parser/Attr.h"
#import "org/w3c/dom/Attr.h"
#import "NodeI.h"

@interface AttrI : NodeI <RXAttr>

@property id <OrgW3cDomAttr> attr;

- (instancetype)initWithAttr:(id <OrgW3cDomAttr>)attr;

@end
