//
//  fcUserDrinkOptionTable.m
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcUserItemOptionTable.h"
#import "fcUserItemOption.h"
#import "Constants.h"

@implementation fcUserItemOptionTable

@synthesize userOptions=_userOptions;

- (id)init
{
    self = [super init];
    if (self)
    {
        _userOptions = [[NSMutableArray alloc] init];
    }
    return self;
}
-(void)clear
{
    [self.userOptions removeAllObjects];
}
-(void)addUserItemOption:(fcUserItemOption*)option
{
    [self.userOptions addObject:option];
}
- (void)sort
{
    self.userOptions = [[self.userOptions sortedArrayUsingSelector:@selector(compare:)] mutableCopy];
}
- (NSString*)makeOptionValueStringForOptionGroup:(NSNumber*)optionGroupID andItemType: (NSNumber*)itemType andMenu:(fcMenu*)menu
{
    // NOTE: We have to obey sort order here on the options. This is handeled rather inefficently right now as
    // We re-sort the options whenever we add one in.
    NSMutableString *result=[[NSMutableString alloc]initWithString:@""];
    
    BOOL firstTime=TRUE;
    
    int index=0;
    for(index=0;index < [self.userOptions count];index++)
    {
        fcUserItemOption *option = [self.userOptions objectAtIndex:index];
        NSMutableString *subResult =[[NSMutableString alloc]initWithString:@""];
        if(firstTime!=TRUE)
        {
            [subResult appendString:@", "];
            
        }
        
        if([optionGroupID isEqualToNumber:option.itemOptionGroupID])
        {
            NSString *optionText =[option makeOptionValueString:itemType withMenu:menu];
            if(optionText!=nil)
            {
                firstTime=FALSE;
                [subResult appendString: optionText];
                [result appendString:subResult];
            }
        }
    }
    
    return result;
}
 
- (NSString*)makeOptionCostForOptionGroup:(NSNumber*)optionGroupID andItemType: (NSNumber*)itemType andMenu:(fcMenu*)menu
{
    // NOTE: We have to obey sort order here on the options, that's going to be a thorn as it's not included. LATER FIXME

    /*
    NSDecimalNumberHandler *handler = [NSDecimalNumberHandler decimalNumberHandlerWithRoundingMode:NSRoundPlain
                                                                                             scale:-2
                                                                                  raiseOnExactness:NO
                                                                                   raiseOnOverflow:NO
                                                                                  raiseOnUnderflow:NO
                                                                               raiseOnDivideByZero:NO];
    */
    NSDecimalNumber *CurrentCost = [NSDecimalNumber decimalNumberWithString:@"0.00" ];
    int index=0;
    for(index=0;index < [self.userOptions count];index++)
    {
        fcUserItemOption *option = [self.userOptions objectAtIndex:index];
        
        if([optionGroupID isEqualToNumber:option.itemOptionGroupID])
        {
            NSDecimalNumber *subCost = [option getTotalCost:itemType withMenu:menu];
            if(nil!=subCost)
            {
                CurrentCost = [CurrentCost decimalNumberByAdding:subCost];
            }
        }
    }
   // CurrentCost = [CurrentCost decimalNumberByRoundingAccordingToBehavior:handler];
   // NSNumberFormatter * formatter = [[NSNumberFormatter alloc] init];
    //[formatter setNumberStyle: NSNumberFormatterCurrencyStyle];
    
    //return [formatter stringFromNumber:CurrentCost];
    

    return [NSString stringWithFormat:@"%.2f",[CurrentCost doubleValue]];
    
  //  return [CurrentCost stringValue];
    
}

- (fcUserItemOption*) findUserItemOptionByItemOptionID:(NSNumber*) optionID
{
    
    //fcUserItemOptionTable *newTable = [[fcUserItemOptionTable alloc]init];
    int index=0;
    for(index=0;index<[self.userOptions count];index++)
    {
        fcUserItemOption *option = [self.userOptions objectAtIndex:index];
        if([[option itemOptionID] isEqualToNumber:optionID])
        {
            return option;
        }
        
    }
    return nil;
}

- (fcUserItemOptionTable*) cloneOptions
{
    fcUserItemOptionTable *newTable = [[fcUserItemOptionTable alloc]init];
    int index=0;
    for(index=0;index<[self.userOptions count];index++)
    {
        fcUserItemOption *option=[self.userOptions objectAtIndex:index];
        [newTable addUserItemOption: [option clone]];
    }
    return newTable;
}


- (void) removeAllOptionsForOptionGroup: (NSNumber*)groupID
{
    if([self.userOptions count]==0)
    {
        return;
    }
    
    for(int index=[self.userOptions count];index>0;index--)
    {
        fcUserItemOption *option = [self.userOptions objectAtIndex:index-1];
        if(nil==option)
        {
            continue; // This is bad day
        }
        if( [[option itemOptionGroupID] isEqualToNumber:groupID])
        {
            [self.userOptions removeObjectAtIndex:index-1];
        }
    }
}
- (void) removeOptionByOptionID:(NSNumber*)optionID
{
    for(int index=0;index<[self.userOptions count];index++)
    {
        fcUserItemOption *option = [self.userOptions objectAtIndex:index];
        if(nil==option)
        {
            continue; // This is bad day
        }
        
        if([option.itemOptionID isEqualToNumber:optionID])
        {
            [self.userOptions removeObjectAtIndex:index];
            return; // Can only be one
        }
    }
}

- (void) removeAllOptionsForAnyOtherOptionInGroup:(NSNumber*) groupID withOptionID:(NSNumber*) optionID
{
    if([self.userOptions count]==0)
    {
        return;
    }
    
    for(int index=[self.userOptions count];index>0;index--)
    {
        fcUserItemOption *option = [self.userOptions objectAtIndex:index-1];
        if(nil==option)
        {
            continue; // This is bad day
        }
        if([option.itemOptionGroupID isEqualToNumber:groupID]) 
        {
            if (! ([option.itemOptionID isEqualToNumber:optionID]))
            {
                 [self.userOptions removeObjectAtIndex:index-1];
            }
        }
    }
}

- (void) addOptionsListToPost:(NSMutableString*)postString
{
    for(int index=0;index<[self.userOptions count];index++)
    {
        fcUserItemOption *option = [self.userOptions objectAtIndex:index];
        if(nil==option)
        {
            continue; // This is bad day
        }
        
        // Looks like the protocol is fairly broken. Same in android.
        
        [postString appendFormat:@"&%@%d=%@*%@*%@*%@*%d",S_USER_ITEM_OPTIONS,index,option.itemOptionGroupID,option.itemOptionID,
         option.itemTypesOptionID,option.itemTypesOptionID, option.itemOptionCount];
    }
}

@end
