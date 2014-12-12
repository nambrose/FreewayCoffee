//
//  CarDataValuePickerIndexItem.h
//  UserClient
//
//  Created by Nick Ambrose on 9/2/12.
//  Copyright (c) 2012 Immerse Images. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CarDataValuePickerIndexItem : NSObject
{
@private
    NSString *ItemText;
    int m_ItemID;
    BOOL m_HasModels;
    int SortOrder;
}

@property (nonatomic, retain) NSString *ItemText;
@property (nonatomic, assign) int SortOrder;
@property (nonatomic, assign) BOOL m_HasModels;
@property (nonatomic, assign) int m_ItemID;
- (NSComparisonResult)compare:(CarDataValuePickerIndexItem *)otherObject;
@end
