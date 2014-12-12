//
//  fcOrder.h
//  UserClient
//
//  Created by Nick Ambrose on 9/16/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

#define ORDER_STATUS_ORDER_RECEIVED 1
#define ORDER_STATUS_ORDER_INPROGRESS 2
#define ORDER_STATUS_ORDER_DELIVERED 3
#define ORDER_STATUS_ORDER_REFUNDED 4
#define ORDER_STATUS_ORDER_NOSHOW 5

@class fcOrderItem;

@interface fcOrder : NSObject
 
/* LATER
@property (nonatomic,strong) NSNumber *orderID;
// LATER private Date m_TimeReceivedAsDate;
@property (nonatomic,copy) NSString *timeReceived;
@property (nonatomic,copy) NSString *timeReceivedDisplayTime;
@property (nonatomic,copy) NSString *timeNeeded;
@property (nonatomic,copy) NSString *timeNeededDisplayTime; // Just the Time, excluding date for display
@property (nonatomic,copy) NSString *timeToLocation; // How long the user said they would take to get there
@property (nonatomic,copy) NSString *timeOrderEnd; // Time we went into a final state (Refund, DELIVERED, NOSHOW)
@property (nonatomic,copy) NSString *timeOrderEndDisplayTime;

@property (nonatomic,copy) NSString *userName;
@property (nonatomic,copy) NSString *userEmail;
@property (nonatomic,copy) NSString *totalCost;
@property (nonatomic,copy) NSString *disposition;
private Integer m_DispositionInt;
private Integer m_LocationID;
@property (nonatomic,copy) NSString *timeUserHere;
@property (nonatomic,copy) NSString *timeUserHereDisplayTime;
//private Date m_TimeHereAsDate;
//private Date m_TimeNeededAsDate;

@property (nonatomic,copy) NSString *userCarMakeModelInfo;
@property (nonatomic,copy) NSString *userTag;

private Integer m_ArriveMode; // 0 - In Car, 1 - Walkup
private Integer m_UserID;

// To be added
private String m_CreditCardType;
private String m_CreditCardLast4;
// Need credit card Auth data too ?
private Integer m_RecordIncarnation; // Eventually used to detect if someone else made an edit in the meantime
private FCOrderItemList m_OrderItems;
private boolean m_IsDemoOrder;
*/

@end
