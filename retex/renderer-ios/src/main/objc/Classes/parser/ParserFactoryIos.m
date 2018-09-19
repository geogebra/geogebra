#import "ParserFactoryIos.h"
#import "ParserI.h"

@implementation ParserFactoryIos

- (id <RXParser>)createParser {
    return [[ParserI alloc] init];
}

@end
