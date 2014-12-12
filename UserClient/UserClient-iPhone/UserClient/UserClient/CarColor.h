//
//  CarColor.h
//  UserClient
//
//  Created by Nick Ambrose on 1/22/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Constants.h"


@interface CarColor : NSObject
{
    NSInteger CarColorID;
    NSString *CarColorLongDescr;
    NSString *CarColorShortDescr;
    NSInteger SortOrder;
}

@property (nonatomic, assign) NSInteger CarColorID; 
@property (nonatomic, copy) NSString *CarColorLongDescr;
@property (nonatomic, copy) NSString *CarColorShortDescr;
@property (nonatomic, assign) NSInteger SortOrder;

@end
