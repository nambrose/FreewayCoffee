//
//  fcTableCellButton.h
//  UserClient
//
//  Created by Nick Ambrose on 9/15/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface fcTableCellButton : UIButton

// NSIndexPath provides a convenient way to store an integer pair
// Note we are using cellIndex.section to store the column (or button #)
@property (strong, nonatomic) NSIndexPath *cellIndex;

@end


