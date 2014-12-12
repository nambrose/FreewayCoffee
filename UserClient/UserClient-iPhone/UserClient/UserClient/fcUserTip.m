//
//  fcUserTip.m
//  UserClient
//
//  Created by Nick Ambrose on 9/29/12.
//  Copyright (c) 2012 Freeway Coffee. All rights reserved.
//

#import "fcUserTip.h"
#import "Constants.h"

@implementation fcUserTip

@synthesize userID=_userID;
@synthesize locationID=_locationID;
@synthesize tipType=_tipType;
@synthesize tipAmount=_tipAmount;
@synthesize roundUp=_roundUp;

- (NSDecimalNumber*) calculateTipDollarAmount:(NSDecimalNumber*)amountToTipOn
{
    if(self.roundUp==TRUE)
    {
        NSDecimalNumber *result = [NSDecimalNumber decimalNumberWithString:self.tipAmount];
        
        result = [result decimalNumberByAdding:amountToTipOn];
        
        NSDecimalNumberHandler *handler = [NSDecimalNumberHandler decimalNumberHandlerWithRoundingMode:NSRoundUp
                                                                                                 scale:0 raiseOnExactness:TRUE raiseOnOverflow:TRUE raiseOnUnderflow:TRUE raiseOnDivideByZero:TRUE];
        result = [result decimalNumberByRoundingAccordingToBehavior:handler];
        return [result decimalNumberBySubtracting:amountToTipOn];
        
    }
    else
    {
        return [NSDecimalNumber decimalNumberWithString:self.tipAmount];
    }
}

@end
