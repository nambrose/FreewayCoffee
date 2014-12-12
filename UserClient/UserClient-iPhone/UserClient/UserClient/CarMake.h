//
//  CarMake.h
//  UserClient
//
//  Created by Nick Ambrose on 1/22/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Constants.h"

#define CAR_MAKE_NONE_ID 0

@class CarModelList;
@class CarModel;

@interface CarMake : NSObject
{
    
	NSInteger MakeID;
	NSString *MakeLongDescr;
	NSString *MakeShortDescr;
	BOOL CanHaveModels;
	CarModelList *Models;
	NSInteger SortOrder;
}

@property (nonatomic, assign) NSInteger MakeID; 
@property (nonatomic, copy) NSString *MakeLongDescr;
@property (nonatomic, copy) NSString *MakeShortDescr;
@property (nonatomic, assign) BOOL CanHaveModels;
@property (nonatomic, strong) CarModelList *Models;
@property (nonatomic, assign) NSInteger SortOrder;

- (id) init;
- (BOOL) DoesMakeHaveModels;
-(void) AddModel:(CarModel*)CarModel;
- (CarModel*) GetModel:(NSInteger) ModelID;
-(NSInteger) GetNumberOfModels;
- (BOOL) IsNone;



@end
