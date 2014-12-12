//
//  fcLastOrder.h
//  UserClient
//
//  Created by Nick Ambrose on 9/16/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcLastOrderItem;

typedef enum
{
    ORDER_FAILED,
    ORDER_SUBMITTED,
    ORDER_HERE_SENT,
    ORDER_HERE_OK
    
}OrderSubmittedStatus;

@interface fcLastOrder : NSObject


@property (nonatomic,assign) OrderSubmittedStatus orderStatus;


@property (nonatomic,strong) NSMutableDictionary *lastOrder;
@property (nonatomic,strong) NSMutableDictionary *lastOrderLocation;
//boolean LastOrderSentImHere; // NOTE TODO FIXME. Getting nervous here.
//Date LastOrderTime;
@property (nonatomic,strong) NSNumber *orderID; // This one comes in the Order Submitted. We also get an OrderID in m_LastOrder later. Irksome.
@property (nonatomic,strong) NSMutableArray *orderItems;
@property (nonatomic,strong) NSMutableDictionary *orderCreditCard;

- (id)init;
- (void) addLastOrderItem:(fcLastOrderItem*)item;
+ (NSString*) makeErrorText;
- (NSString*)makeOrderSubmittedText;
- (NSString*)makeOrderResponseText;
- (void) updateImHereStatus;
- (void) updateOrderIDStatus;
- (BOOL) doesOrderExist;
- (NSNumber*)getPayMethod;
@end
