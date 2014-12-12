//
//  fcUserTip.h
//  UserClient
//
//  Created by Nick Ambrose on 9/29/12.
//  Copyright (c) 2012 Freeway Coffee. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface fcUserTip : NSObject

@property (nonatomic,strong) NSNumber *userID;
@property (nonatomic,strong) NSNumber *locationID;
@property (nonatomic,assign) NSInteger tipType;
@property (nonatomic,copy) NSString *tipAmount;
@property (nonatomic,assign) BOOL roundUp;

- (NSDecimalNumber*) calculateTipDollarAmount:(NSDecimalNumber*)amountToTipOn;
@end
