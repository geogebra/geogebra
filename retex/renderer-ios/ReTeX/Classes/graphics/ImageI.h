#import <Foundation/Foundation.h>
#import "Image.h"
#import <CoreGraphics/CoreGraphics.h>
#import <UIKit/UIKit.h>


@interface ImageI : NSObject <RXImage>

@property CGContextRef context;

- (instancetype)initWithWidth:(int)width withHeight:(int)height;

- (CGImageRef)createCGImage;

@end
