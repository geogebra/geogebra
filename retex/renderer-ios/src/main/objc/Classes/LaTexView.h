#import <UIKit/UIKit.h>

@interface LaTexView : UIView

@property(nonatomic) IBInspectable NSString* latexText;
@property(nonatomic) IBInspectable CGFloat size;
@property(nonatomic) IBInspectable int style;
@property(nonatomic) IBInspectable UIColor* foregroundColor;
@property(nonatomic) IBInspectable UIColor* backgroundColor;
@property(nonatomic) IBInspectable int type;

@end
