//
//  CarModel.h
//  UserClient
//
//  Created by Nick Ambrose on 1/22/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Constants.h"

#define CAR_MODEL_NONE_ID 0

@interface CarModel : NSObject
{
    
	NSInteger ModelID;
    NSInteger MakeID;
	NSString *ModelLongDescr;
	NSString *ModelShortDescr;
	NSInteger SortOrder;
	
}

@property (nonatomic, assign) NSInteger ModelID;
@property (nonatomic, assign) NSInteger MakeID;
@property (nonatomic, copy) NSString *ModelLongDescr;
@property (nonatomic, copy) NSString *ModelShortDescr;
@property (nonatomic, assign) NSInteger SortOrder;

- (id) init;
-(BOOL) IsNone;


@end
