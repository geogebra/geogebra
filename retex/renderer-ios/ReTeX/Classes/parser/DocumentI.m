#import "DocumentI.h"
#import "ElementI.h"

@implementation DocumentI

- (instancetype)initWithDocument:(id <OrgW3cDomDocument>)document {
    self = [super init];
    if (self) {
        _document = document;
    }
    return self;
}

- (id <RXElement>)getDocumentElement {
    return [[ElementI alloc] initWithElement:[_document getDocumentElement]];
}

@end
