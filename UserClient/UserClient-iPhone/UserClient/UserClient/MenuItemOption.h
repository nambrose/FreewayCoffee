//
//  MenuItemOption.h
//  UserClient
//
//  Created by Nick Ambrose on 9/3/12.
//  Copyright (c) 2012 Immerse Images. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface MenuItemOption : NSObject
{
}

// ALl OptionID's are NSNumber because they may need to be used as a Key in NSDictionary
@property (nonatomic, strong) NSNumber *OptionID;
@property (nonatomic, strong) NSNumber *OptionGroupID;
@property (nonatomic, strong) NSString *OptionName;
@property (nonatomic, assign) NSInteger SortOrder;
@end
