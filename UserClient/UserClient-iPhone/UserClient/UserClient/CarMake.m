//
//  CarMake.m
//  UserClient
//
//  Created by Nick Ambrose on 1/22/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//

#import "CarMake.h"
#import "CarModelList.h"

@implementation CarMake

@synthesize MakeID;
@synthesize MakeLongDescr;
@synthesize MakeShortDescr;
@synthesize CanHaveModels;
@synthesize Models;
@synthesize SortOrder;


- (id)init
{
    self = [super init];
    if (self)
    {
        // Initialization code here.
        MakeLongDescr = [[NSString alloc] init];
        MakeShortDescr = [[NSString alloc] init];
        Models = [[CarModelList alloc] init];
    }
    
    
    
    return self;
}


- (BOOL) DoesMakeHaveModels
{
    if( (CanHaveModels) && ( [Models Size] >0))
    {
        return TRUE;
    }
    return FALSE;
}

-(void) AddModel:(CarModel*)CarModel
{
    [Models AddModel:CarModel];
}

- (CarModel*) GetModel:(NSInteger) ModelID
{
    return [Models GetModel:ModelID];
}


-(NSInteger) GetNumberOfModels
{
    return [Models Size];
}

- (BOOL) IsNone
{
    // TODO, find a way to share this logic with server code common files
    if(MakeID==CAR_MAKE_NONE_ID)
    {
        return TRUE;
    }
    return TRUE;
}


- (void)dealloc 
{
    self.MakeLongDescr=nil;
    self.MakeShortDescr=nil;
    self.Models=nil;
    //[super dealloc];
}
@end
