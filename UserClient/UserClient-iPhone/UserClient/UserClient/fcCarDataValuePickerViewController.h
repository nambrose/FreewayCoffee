//
//  fcCarDataValuePickerViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol CarDataValuePickerDelegate;

@interface fcCarDataValuePickerViewController : UIViewController
{
@private
    NSMutableArray *ListIndex;
    int PickType; // Make, Model or Color
    NSString *PickTypeStr; // Make, Model or Color Just for convenience
    int MakeID;
    BOOL PopTwo;
}
@property (nonatomic, weak) id<CarDataValuePickerDelegate> delegate;
//@property (nonatomic, weak) IBOutlet UILabel *WelcomeUserLabel;
@property (nonatomic, weak) IBOutlet UITableView *ItemsList;
@property (nonatomic, strong) IBOutlet UIView *tableHeader;


@property (nonatomic, strong) NSMutableArray *ListIndex;


// Uck. Make is only used if we are selecting a model (as I need to get the make first ...
// PopTwo is set if we initially select a Make as it then creates a second data picker (leaving itself alive)
// We then need to pop both views on selecting a Model. There is definitely a better way (more than one) to do this
// Probably adding a "ViewDidExit" to the Protocol and popping self ...
- (id)initWithNibName:(NSString *)nibNameOrNil andPickType:(int)inPickType andMakeID:(int)inMakeID andPopTwo:(BOOL)inPopTwo;

- (void) CreateListIndex;

@end

@protocol CarDataValuePickerDelegate <NSObject>
-(void) setMakeID:(int)MakeID;
-(void) setModelID:(int)ModelID;
-(void) setColorID:(int)ColorID;
@end
