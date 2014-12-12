//
//  fcMenuItemMandatoryOption.h
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface fcMenuItemMandatoryOption : NSObject

//@property (nonatomic,strong) NSNumber *mandatoryOptionID;
@property (nonatomic,strong) NSNumber *itemTypeID;
@property (nonatomic,strong) NSNumber *itemGroupID;

-(id)init;

@end
