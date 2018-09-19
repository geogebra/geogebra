#import <Foundation/Foundation.h>
#import "com/himamis/retex/renderer/share/platform/parser/Document.h"
#import "org/w3c/dom/Document.h"

@interface DocumentI : NSObject <RXDocument>

@property id <OrgW3cDomDocument> document;

- (instancetype)initWithDocument:(id <OrgW3cDomDocument>)document;

@end
