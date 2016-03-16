#import <Foundation/Foundation.h>
#import "com/himamis/retex/renderer/share/platform/parser/Attr.h"
#import "NodeI.h"
#import "org/w3c/dom/Attr.h"

@interface AttrI : NodeI <RXAttr>

@property id <OrgW3cDomAttr> attr;

- (instancetype)initWithAttr:(id <OrgW3cDomAttr>)attr;

@end
