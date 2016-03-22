#import <UIKit/UIKit.h>


@interface LaTexView : UIView

@property(nonatomic) IBInspectable NSString *latexText;
@property(nonatomic) IBInspectable float size;
@property(nonatomic) IBInspectable int style;
@property(nonatomic) IBInspectable UIColor *latexForegroundColor;
@property(nonatomic) IBInspectable UIColor *latexBackgroundColor;
@property(nonatomic) IBInspectable int type;

@end
