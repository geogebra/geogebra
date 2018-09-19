#import "com/himamis/retex/renderer/share/exception/ResourceParseException.h"
#import "javax/xml/parsers/DocumentBuilder.h"
#import "org/w3c/dom/Document.h"
#import "java/io/InputStream.h"
#import "DocumentI.h"
#import "ParserI.h"

@implementation ParserI

- (instancetype)init {
    self = [super init];
    if (self) {
        _factory = JavaxXmlParsersDocumentBuilderFactory_newInstance();
    }
    return self;
}

- (id <RXDocument>)parseWithId:(id)input {
    JavaIoInputStream *is = (JavaIoInputStream *) input;

    id <OrgW3cDomDocument> document = [self parseWithInputStream:is];

    return [[DocumentI alloc] initWithDocument:document];
}

- (id <OrgW3cDomDocument>)parseWithInputStream:(JavaIoInputStream *)inputStream {
    @try {
        JavaxXmlParsersDocumentBuilder *documentBuilder = [_factory newDocumentBuilder];
        return [documentBuilder parseWithJavaIoInputStream:inputStream];
    }
    @catch (JavaLangException *exception) {
        @throw new_RXResourceParseException_initWithNSString_withJavaLangThrowable_(@"Could not parse resource.", exception);
    }
}

- (void)setIgnoringCommentsWithBoolean:(jboolean)ignoreComments {
    [_factory setIgnoringCommentsWithBoolean:ignoreComments];
}

- (void)setIgnoringElementContentWhitespaceWithBoolean:(jboolean)whitespace {
    [_factory setIgnoringElementContentWhitespaceWithBoolean:whitespace];
}

@end
