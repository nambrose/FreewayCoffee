//
//  fcLastOrder.m
//  UserClient
//
//  Created by Nick Ambrose on 9/16/12.
//  Copyright (c) 2012,2013 Freeway Coffee. All rights reserved.
//

#import "fcLastOrder.h"
#import "fcLastOrderItem.h"
#import "fcAppDelegate.h"
#import "fcLocationAllowedPaymentMethod.h"

@implementation fcLastOrder

@synthesize orderStatus=_orderStatus;
@synthesize lastOrder=_lastOrder;
@synthesize lastOrderLocation=_lastOrderLocation;

@synthesize orderID=_orderID; // This one comes in the Order Submitted. We also get an OrderID in m_LastOrder later. Irksome.
@synthesize orderItems=_orderItems;
@synthesize orderCreditCard=_orderCreditCard;

- (id)init
{
    self = [super init];
    if (self)
    {
        _orderItems = [[NSMutableArray alloc]init];
    }
    
    
    return self;
}
-(void) addLastOrderItem:(fcLastOrderItem*)item
{
    [self.orderItems addObject:item];
    
}

-(BOOL)doesOrderExist
{
    if(nil!=self.lastOrder)
    {
        return TRUE;
    }
    return FALSE;
}

+ (NSString*) makeErrorText
{
    NSString *errorText = [myCommonAppDelegate makeLastErrorText];
    
    NSString *Result = [NSString stringWithFormat:@"Sorry %@\n Your Order Failed\n\nReason\n%@",
                        [myCommonAppDelegate getName],
                        errorText];
    return Result;
     
}
- (NSString*)makeOrderSubmittedText
{
    /*
    @"<html>"
    "<body>"
    "<p>%@</p>"
    "</body></html>"
    */
    
    NSMutableString *result = [NSMutableString stringWithFormat:@"<html><style type=\"text/css\"> \n"
                               "body {font-family: \"helvetica\";}\n"
                               "</style> \n<body><p>"];
    
    [result appendFormat:@"Congratulations %@,<br><br>Your order was submitted successfully<br>",
     [myCommonAppDelegate getName]];
    
    
    
    [result appendFormat:@"Order #: %@<br>Cost:$%@<br>",
                self.orderID,
                [self.lastOrder valueForKey:ORDER_TOTAL_COST_ATTR]];
    
    if([fcLocationAllowedPaymentMethod isPayInStore:self.getPayMethod])
    {
        [result appendFormat:@"<b>Order must be paid for upon arrival</b><br><br>"];
    }
    else
    {
        [result appendFormat:@"Congratulations, %@<br>Your card has been charged<br>",
         [myCommonAppDelegate getName]];
    }
    
    if(nil!=self.lastOrderLocation)
    {
        [result appendFormat:@"Location: %@<br>%@",
            [self.lastOrderLocation valueForKey:ORDER_LOCATION_DESCRIPTION_ATTR],
            [self.lastOrderLocation valueForKey:ORDER_LOCATION_INSTRUCTIONS_ATTR]];
        
    }
    
    [result appendFormat:@"</p></body</html>"];
    
    return result;
        

}

- (void) updateImHereStatus
{
    if(nil!=self.lastOrder)
    {
        NSString *userTimeHere = [self.lastOrder valueForKey:ORDER_USER_TIME_HERE_ATTR];
        
        if( (nil!=userTimeHere) && ([userTimeHere length] !=0))
        {
            self.orderStatus=ORDER_HERE_OK;
        }
        
    }
}

- (void) updateOrderIDStatus
{
    if(nil!=self.lastOrder)
    {
        NSString *orderIDStr = [self.lastOrder valueForKey:ID_ATTR];
        self.orderID = [NSNumber numberWithInteger:[orderIDStr integerValue]];
    }
}

- (NSNumber*)getPayMethod
{
    NSNumber *result = [NSNumber numberWithInteger:LOCATION_PAY_METHOD_UNKNOWN];
    
    NSString *payMethStr = [self.lastOrder valueForKey:ORDER_PAY_METHOD_ATTR];
    if(payMethStr==nil)
    {
        return result;
    }
    result = [NSNumber numberWithInteger:[payMethStr integerValue]];
    return result;
}

- (NSString*)makeOrderResponseText
{
    
    NSMutableString *result = [NSMutableString stringWithFormat:@"<html><style type=\"text/css\"> \n"
                               "body {font-family: \"helvetica\";}\n"
                               "</style> \n<body><p>"];
    
    [result appendFormat:@"Congratulations, %@<br><br>Your order was successful<br>Order #: %@<br><br>",
            [myCommonAppDelegate getName],self.orderID];
    
    
    if([fcLocationAllowedPaymentMethod isPayInStore:self.getPayMethod])
    {
        [result appendFormat:@"Order must be paid for upon arrival<br><br>"];
    }
 
    else
    {
        [result appendFormat:@"Total Cost:$%@<br>Your credit card:%@ (...%@) was charged<br><br>",
                    [self.lastOrder valueForKey:ORDER_TOTAL_COST_ATTR],
                    [self.orderCreditCard valueForKey:ORDER_CREDIT_CARD_DESCR],
                    [self.orderCreditCard valueForKey:ORDER_CREDIT_CARD_CARD_LAST4]];
    }
    
    
   // NSNumberFormatter *currencyFormatter  = [myCommonAppDelegate getCurrencyFormatter];
    
    // Sadly shared with OrderPriceBreakdown VC
    
    [result appendFormat:@"<br>Order Breakdown<br>==============<br><br>Items Total: $%@<br>",
                             [self.lastOrder valueForKey:ORDER_TOTAL_COST_ATTR]];
    
    [result appendFormat:@"Discount: $%@<br>",[self.lastOrder valueForKey:ORDER_DISCOUNT_ATTR]];
    
    [result appendFormat:@"Additional Fee: $%@<br>",[self.lastOrder valueForKey:ORDER_CONV_FEE_ATTR ] ];
    
    [result appendFormat:@"<br>Subtotal: $%@<br>",[self.lastOrder valueForKey:ORDER_TAXABALE_AMOUNT_ATTR ] ];
    
    [result appendFormat:@"Tax: $%@<br>",[self.lastOrder valueForKey:ORDER_TAX_ATTR]];
    
    [result appendFormat:@"<br>Tip: $%@<br>",[self.lastOrder valueForKey:ORDER_TIP_ATTR]];
    
    
    [result appendFormat:@"==============<br>Order Total: $%@<br><br>",[self.lastOrder valueForKey:ORDER_TOTAL_COST_ATTR]];
    

    
    [result appendFormat:@"Order Summary:<br><br>"];
    
    for (int index=0;index <[self.orderItems count];index++)
    {
        
        fcLastOrderItem *item = [self.orderItems objectAtIndex:index];
        if(nil!=item)
        {
            [ result appendFormat:@"%@ ($%@)<br><br>",item.orderItemDescription, item.orderItemCost ];
        }
        else
        {
            [result appendFormat:@"Item Not Found! (Please send feedback)<br><br>"];
        }
    }
    
    
    /* Removed Ontime: D-155
     Result += appState.getString(R.string.fc_order_ready_at) + " ";
     if(m_LastOrder.get(FreewayCoffeeXMLHelper.ORDER_TIME_NEEDED_ATTR) !=null)
     {
     Result += GetTimeDateAsHoursAndMins(m_LastOrder.get(FreewayCoffeeXMLHelper.ORDER_TIME_NEEDED_ATTR) );
     }
     else
     {
     Result += "N/A";
     }
     
     Result += "<br>";
     
     */
    /*
    Result += "Order Summary:<br><br>";
    
    
     */
    [result appendFormat:@"</p></body</html>"];
    
    return [result copy];
    
}
@end
