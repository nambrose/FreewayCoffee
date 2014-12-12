//
//  fcXMLParserItemsList.m
//  UserClient
//
//  Created by Nick Ambrose on 12/24/12.
//  Copyright (c) 2012,2013 Freeway Coffee. All rights reserved.
//

#import "fcXMLParserItemsList.h"
#import "Constants.h"
#import "XMLConstants.h"
#import "XMLHelper.h"
#import "fcAppDelegate.h"
#import "fcXMLParserUserItems.h"
#import "fcUserItem.h"
#import "fcAppSettingsTable.h"
#import "fcUserTipTable.h"
#import "fcUserItemTable.h"
#import "fcLastOrder.h"
#import "fcLastOrderItem.h"
#import "fcLocation.h"
#import "fcLocationTable.h"
#import "fcUserInfo.h"
#import "fcLocationAllowedArrivalMethod.h"
#import "fcLocationAllowedPaymentMethod.h"
#import "fcServerObjectResponse.h"


@implementation fcXMLParserItemsList

@synthesize currentItem=_currentItem;
@synthesize signonError=_signonError;
@synthesize lastOrder=_lastOrder;
@synthesize parseSuccessful=_parseSuccessful;
@synthesize wasServerResponseGood=_wasServerResponseGood;
@synthesize wasOrderHereResponse=_wasOrderHereResponse;
@synthesize updatedFreeDrinks=_updatedFreeDrinks;
@synthesize locations=_locations;
@synthesize userInfo=_userInfo;
@synthesize currentLocation=_currentLocation;
@synthesize orderObjResponse=_orderObjResponse;

- (id) init
{
    self = [super init];
    if (self)
    {
        _currentItem=nil;
        _signonError=FALSE;
        _parseSuccessful=FALSE;
        _wasServerResponseGood=FALSE;
        _parseSuccessful=FALSE;
        _wasOrderHereResponse=FALSE;
        _lastOrder = [[fcLastOrder alloc]init];
        _locations = [[fcLocationTable alloc]init];
        _userInfo = [[fcUserInfo alloc]init];
        _currentLocation=nil;
        _orderObjResponse = [[fcServerObjectResponse alloc ] init];
    }
    
    return self;
}


- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    if( [elementName isEqualToString:USER_ITEMS_TAG])
    {
    }
    else if([elementName isEqualToString:SIGNON_RESPONSE_TAG])
    {
        NSString *attrValue = [NSString stringWithString:[attributeDict valueForKey:RESULT_ATTR] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        if([attrValue isEqualToString:OK_ATTR_VALUE]!=TRUE)
        {
            self.signonError=TRUE;
        }
    }
    else if([elementName isEqualToString:USER_INFO_TAG])
    {
        self.userInfo = [fcUserInfo parseFromXML:attributeDict] ;
    }
    else if([elementName isEqualToString:USER_CAR_DATA_TAG])
    {
        [myCommonAppDelegate setUserCarInfo:[XMLHelper convertStringsFromWeb:attributeDict]  ];
    }
    
    else if([elementName isEqualToString:USER_LOCATION_TAG])
    {
        self.currentLocation = [fcLocation parseFromXML:attributeDict];
        
    }
    else if([elementName isEqualToString:LOCATION_ARRIVE_MODE_TAG])
    {
        fcLocationAllowedArrivalMethod *method = [fcLocationAllowedArrivalMethod parseFromXMLAttributes:attributeDict];
        if(method!=nil && self.currentLocation!=nil)
        {
            [self.currentLocation addArrivalMethod:method];
        }
    }
    else if([elementName isEqualToString:LOCATION_PAY_METHOD_TAG])
    {
        fcLocationAllowedPaymentMethod *method = [fcLocationAllowedPaymentMethod parseFromXMLAttributes:attributeDict];
        if(method!=nil && self.currentLocation!=nil)
        {
            [self.currentLocation addPaymentMethod:method];
        }
    }
    else if([elementName isEqualToString: USER_CREDIT_CARDS_TAG])
    {
        [myCommonAppDelegate setUserCreditCardInfo:[XMLHelper convertStringsFromWeb:attributeDict]  ];
    }
    else if([elementName isEqualToString:USER_ITEM_TAG])
    {
        self.currentItem = [fcXMLParserUserItems parseUserItem:attributeDict];
        
    }
    else if([elementName isEqualToString:USER_ITEM_OPTION_TAG])
    {
        if(nil!=self.currentItem)
        {
            
            fcUserItemOption *option = [fcXMLParserUserItems parseUserItemOption:attributeDict];
            [self.currentItem addUserItemOption:option];
        }
        
    }
    else if ([elementName isEqualToString:ERROR_TAG])
    {
        [myCommonAppDelegate setLastError:[XMLHelper convertStringsFromWeb:attributeDict]];
    }
    
    else if ([elementName isEqualToString:APP_SETTING_TAG])
    {
        fcAppSetting *setting = [XMLHelper parseAppSetting:attributeDict];
        if(nil!=setting)
        {
            [[myCommonAppDelegate appSettings] addAppSetting:setting];
            
        }
    }
    else if ([elementName isEqualToString:USER_TIP_TAG])
    {
        fcUserTip *tip = [XMLHelper parseUserTip:attributeDict];
        if(nil!=tip)
        {
            [[myCommonAppDelegate userTips] addTip:tip];
        }
    }
    else  if ([elementName isEqualToString:USER_ORDER_RESPONSE_TAG])
    {
        // The Overall Order Response with the ID
        
        
        self.orderObjResponse= [fcServerObjectResponse parseFromXMLAttributes:attributeDict];
        
        NSString *attr = [attributeDict valueForKey:ORDER_ID_ATTR];
        self.parseSuccessful=TRUE;
        
        NSString *response = [attributeDict valueForKey:RESULT_ATTR];
        response = [XMLHelper stringByDecodingURLFormat:response];
        if([response isEqualToString:OK_ATTR_VALUE])
        {
            
            self.wasServerResponseGood=TRUE;
        }
        attr = [XMLHelper stringByDecodingURLFormat:attr];
        self.lastOrder.orderID =[NSNumber numberWithInteger:[attr integerValue]];
        
        attr = [attributeDict valueForKey:USER_INFO_USER_FREE_DRINKS];
        if(nil!=attr)
        {
            attr = [XMLHelper stringByDecodingURLFormat:attr];
            [[myCommonAppDelegate UserInfo] setUserFreeDrinksCount:[NSNumber numberWithInteger:[attr integerValue]]];
             
        }
        
    }
    else if ([elementName isEqualToString:USER_ORDER_HERE_RESPONSE_TAG])
    {
        self.wasOrderHereResponse=TRUE;
        self.parseSuccessful=TRUE;
        NSString *response = [attributeDict valueForKey:RESULT_ATTR];
        if(nil==response)
        {
            return;
        }
        
        
        response = [XMLHelper stringByDecodingURLFormat:response];
        if([response isEqualToString:OK_ATTR_VALUE])
        {
            self.wasServerResponseGood=TRUE;
            
        }
        
    }
    
    /////// WARNING, LAST ORDER STUFF IS ALSO PASTED INTO LOCATIONS .... Ucky I KNOW BUT OH WELL. MAKE SURE TO CHANGE BOTH .....
    
    else if ([elementName isEqualToString:OM_ORDER_ITEM_TAG])
    {
        fcLastOrderItem *item = [fcXMLParserItemsList parseLastOrderItem:attributeDict];
        if(nil!=item)
        {
            [self.lastOrder addLastOrderItem:item];
        }
    }
    else if ([elementName isEqualToString:ORDER_CREDIT_CARD_TAG])
    {
        self.lastOrder.orderCreditCard = [XMLHelper convertStringsFromWeb:attributeDict];
    }
    else if ([elementName isEqualToString:ORDER_LOCATION_TAG])
    {
        self.lastOrder.lastOrderLocation = [XMLHelper convertStringsFromWeb:attributeDict];
    }
    else if ([elementName isEqualToString:ORDER_TAG])
    {
        self.lastOrder.lastOrder = [XMLHelper convertStringsFromWeb:attributeDict];
    }

}


- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI
    qualifiedName:(NSString *)qName
{
    if([elementName isEqualToString:USER_ITEM_TAG])
    {
        [[myCommonAppDelegate userItems] addUserItem:self.currentItem];
    }
    else  if([elementName isEqualToString:USER_LOCATION_TAG])
    {
        if(self.currentLocation!=nil)
        {
            [self.locations setLocation:self.currentLocation];
        }
    }
        
}

- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
        
}

+ (fcLastOrderItem*) parseLastOrderItem:(NSDictionary *)attributeDict
{
    fcLastOrderItem *item = [[fcLastOrderItem alloc]init];
    
    for(NSString *aKey in [ attributeDict allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attributeDict valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        /*
         orderItemID;
         @property (nonatomic,copy) NSString *orderItemDescription;
         @property (nonatomic,copy) NSString *orderItemCost;
         */
        //FC_Log(@"parseUserItem: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:ID_ATTR])
        {
            //[item setOrderItemID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
            [item setOrderItemID:attrValue];
        }
        else if([aKey isEqualToString:OM_ORDER_ITEM_DESCR_ATTR])
        {
            [item setOrderItemDescription:attrValue];
        }
        else if([aKey isEqualToString:OM_ORDER_ITEM_COST_ATTR])
        {
            [item setOrderItemCost:attrValue];
        }
    }
    
    return item;
    
}

@end
