//
//  fcMenuItemMandatoryOptionTable.m
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcMenuItemMandatoryOptionTable.h"
#import "fcMenuItemMandatoryOption.h"


@implementation fcMenuItemMandatoryOptionTable
@synthesize mandatoryOptions=_mandatoryOptions;

-(id)init
{
    self = [super init];
    if (self)
    {
        _mandatoryOptions = [[NSMutableArray alloc] init];
    }
    return self;
}
-(NSUInteger) size
{
    return [self.mandatoryOptions count];
}
-(void)clear
{
    [self.mandatoryOptions removeAllObjects];
}

-(void)addMandatoryOption:(fcMenuItemMandatoryOption*)mandatoryOption;
{
    [self.mandatoryOptions addObject:mandatoryOption ]; 
}

- (BOOL) isOptionGroupMandatoryForItemType:(NSNumber*) itemTypeID andOptionGroup:(NSNumber*)optionGroupID
{
    fcMenuItemMandatoryOption *mandOption=nil;

    int index=0;
    for(index=0;index < [self.mandatoryOptions count];index++)
    {
        mandOption = [self.mandatoryOptions objectAtIndex:index];
        
        if( ([mandOption.itemTypeID isEqualToNumber:itemTypeID]) &&
            ([mandOption.itemGroupID isEqualToNumber:optionGroupID]))
        {
            return TRUE;
        }
    }
    return FALSE;
}
@end
