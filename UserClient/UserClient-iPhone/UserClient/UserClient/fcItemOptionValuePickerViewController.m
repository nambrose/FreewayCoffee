//
//  fcItemOptionValuePickerViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 9/9/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>
#import "fcItemOptionValuePickerViewController.h"
#import "fcAppDelegate.h"
#import "fcUserItem.h"
#import "MenuItemOption.h"
#import "MenuItemOptionGroup.h"
#import "fcItemsListViewController.h"
#import "fcAddEditItemListIndexDataItem.h"
#import "fcMenu.h"
#import "fcItemType.h"
#import "fcOptionValuePickerDataItem.h"
#import "fcUserItemOptionTable.h"
#import "fcUserItemOption.h"
#import "fcItemTypeOption.h"
#import "fcTableCellButton.h"

@interface OptionValuePickerCellHolder : NSObject
{
    fcTableCellButton *minusButton;
    fcTableCellButton *plusButton;
    UILabel *mainText;
    UILabel *priceText;
    UILabel *totalCostText;
}
@property (nonatomic, strong) fcTableCellButton *minusButton;
@property (nonatomic, strong) fcTableCellButton *plusButton;
@property (nonatomic, strong) UILabel *mainText;
@property (nonatomic, strong) UILabel *priceText;
@property (nonatomic, strong) UILabel *totalCostText;

@end

@implementation OptionValuePickerCellHolder
@synthesize minusButton=_minusButton;
@synthesize plusButton=_plusButton;
@synthesize mainText=_mainText;
@synthesize priceText=_priceText;
@synthesize totalCostText=_totalCostText;

@end



@interface fcItemOptionValuePickerViewController ()

@end

@implementation fcItemOptionValuePickerViewController

@synthesize TableFooter=_TableFooter;
@synthesize tableCell=_tableCell;
@synthesize itemsList=_itemsList;

@synthesize itemBeingEdited=_itemBeingEdited;
@synthesize itemOptionsBeingEdited=_itemOptionsBeingEdited;
@synthesize itemOptionGroup=_itemOptionGroup;
@synthesize doesAnyOptionHaveMaxCountMoreThanOne=_doesAnyOptionHaveMaxCountMoreThanOne;
@synthesize itemType=_itemType;
@synthesize listIndex=_listIndex;
@synthesize delegate=_delegate;
@synthesize menu=_menu;

- (id) initWithNibName:(NSString *)nibNameOrNil userItem:(fcUserItem*)item andOptionGroup:(MenuItemOptionGroup*)group andItemType:(fcItemType*)itemType
{
    self = [super initWithNibName:nibNameOrNil bundle:nil];
    if (self)
    {
        _itemBeingEdited = item; // This is strong ref so we are actually editing the object stored in the parent controller. Thats going to find a way to bite me
        _itemOptionGroup = group;
        _itemType=itemType;
        _doesAnyOptionHaveMaxCountMoreThanOne=FALSE;
        _menu = [myCommonAppDelegate getMenuForCurrentLocation];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    NSString *WelcomeString = [NSString stringWithFormat:@"%@",
                               @"Select options"];
    
    self.title = WelcomeString;
    
    UIColor *background = [[UIColor alloc] initWithPatternImage:
                           [UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
    /*
    if (!self.itemsList.tableFooterView)
    {
        [self AddFooter ];
    }
     */
    self.itemOptionsBeingEdited = [self.itemBeingEdited cloneOptions];
    
    
    self.itemsList.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    UIBarButtonItem *rightButton = [[UIBarButtonItem alloc] initWithTitle:@"Done" style:UIBarButtonItemStylePlain
                                                                   target:self action:@selector(doneCommit)];
    self.navigationItem.rightBarButtonItem = rightButton;
    
    
     self.listIndex = [[NSMutableArray alloc]init];
    [self createListIndex];

}
-(CGFloat) tableView:(UITableView*)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 50.0;
    /*
    ItemListIndexData *Item = [ListIndex objectAtIndex:indexPath.row];
    if(nil==Item)
    {
        NSString *Log = [NSString stringWithFormat:@"ItemsList: heightForRowAtIndexPath Item: %i, Not found In Index (bad)",indexPath.row];
        FC_Log(@"%@",Log);
        return ITEMS_LIST_DEFAULT_ROW_HEIGHT;
    }
    
    if(Item.ItemType==ITEM_LIST_USER_DRINK_TYPE)
    {
        return ITEMS_LIST_DRINK_ROW_HEIGHT;
    }
    return ITEMS_LIST_DEFAULT_ROW_HEIGHT;
    */
}

- (void) doneCommit
{
    /// Set the options in here to the drink !!!!
    // I *think* this actually edits the option in the parent. In a way I hope it does.
    // In a way that scares me to death. The polite way to do it is to extend the childFinished Protocol
    // To pass the edited drink back up. We will see if it "works"
    [self.itemBeingEdited replaceUserItemOptions:self.itemOptionsBeingEdited]; 
    
    if([self.delegate respondsToSelector:@selector(childFinished)])
    {
        [self.delegate childFinished];
    }
    
    [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];

}
- (void) createListIndex
{
    [self.listIndex removeAllObjects];
    
    // Check to see if we have to add a NONE entry
    fcOptionValuePickerDataItem *indexEntry=nil;
    
    // If OptionGroup is not mandatory, add a "None" at the top so the user has a way to "deselect"
    // Right now, None is present for MultiSelect(as a shortcut to de-selecting all) and Pick one(needed). We will see if it looks odd on multiselect
    
    if ( ! [self.menu isOptionGroupMandatoryForItemType: [self.itemBeingEdited ItemTypeID]
                                                              andOptionGroup: [self.itemOptionGroup GroupID] ])
          
    {
        indexEntry = [[fcOptionValuePickerDataItem alloc]init];
        indexEntry.optionText = NONE_TEXT;
        indexEntry.isNone=TRUE;
        indexEntry.sortOrder=0; // Make sure NONE is always at the top
        [self.listIndex addObject:indexEntry];
        
    }
    
    int index=0;
    for(index=0;index< [[self.itemOptionGroup ItemGroupItemOptions] count];index++)
    {
        MenuItemOption *option = [[self.itemOptionGroup ItemGroupItemOptions] objectAtIndex:index];
        if(nil==option)
        {
            continue; // This would not be good.
        }
        
        if( [self.itemType isOptionValidForItemType: [option OptionID] andGroup: [self.itemOptionGroup GroupID]])
        {
            indexEntry = [[fcOptionValuePickerDataItem alloc]init];
            indexEntry.isNone=FALSE;
            indexEntry.optionID = [option OptionID];
            indexEntry.optionCount=0;
            indexEntry.userOption = [self.itemOptionsBeingEdited findUserItemOptionByItemOptionID:[option OptionID]]; // OK if its null
            
            indexEntry.option=option;
            if(nil!=indexEntry.userOption)
            {
                indexEntry.optionText = [indexEntry.userOption makeOptionValueString:[self.itemType itemTypeID] withMenu:self.menu];
                indexEntry.optionCount = indexEntry.userOption.itemOptionCount;
            }
            else
            {
                indexEntry.optionText = [option OptionName];
            }
            
            indexEntry.itemTypeOption = [self.itemType findItemTypeOptionByItemOptionID: [option OptionID] andOptionGroupID:[option OptionGroupID]];
            indexEntry.optionGroup=self.itemOptionGroup;
            indexEntry.sortOrder=[option SortOrder];
            
            if([indexEntry.itemTypeOption itemTypeRangeMax]>1)
            {
                self.doesAnyOptionHaveMaxCountMoreThanOne=true;
            }
            
            [self.listIndex addObject:indexEntry];
            
        }
        
    }
    self.listIndex = [[self.listIndex sortedArrayUsingSelector:@selector(compare:)] mutableCopy];
    [self.itemsList reloadData];
  
    
}

- (UITableViewCell*)tableView:(UITableView*)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    // DO NOT CHANGE CellIdentifier without chaging Reuse ID in the NIB !!!!!
    static NSString *CellIdentifier = @"ItemOptionValuePickerTableCell"; // MUST Match the NIB !!!
    UITableViewCell *Cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if(nil==Cell)
    {
        // Got to load one
        UINib *theCellNib = [UINib nibWithNibName:@"fcItemOptionValuePickerTableCell" bundle:nil];
        [theCellNib instantiateWithOwner:self options:nil];
        Cell = [self tableCell];
        
    }
    else
    {
        // Test to make sure reuse happens sometimes at leaste.
    }
    
    OptionValuePickerCellHolder *holder = [[OptionValuePickerCellHolder alloc]init];
    
    // Minus Button
    holder.minusButton = (fcTableCellButton*)[Cell viewWithTag:1];
    holder.minusButton.cellIndex = indexPath;
    [holder.minusButton addTarget:self action:@selector(doMinus:)
      forControlEvents:UIControlEventTouchUpInside];

    
    holder.mainText = (UILabel*)[Cell viewWithTag:2];
    holder.priceText =(UILabel*)[Cell viewWithTag:3];
    
    // Plus Button
    holder.plusButton = (fcTableCellButton*)[Cell viewWithTag:4];
    holder.plusButton.cellIndex = indexPath;
    [holder.plusButton addTarget:self action:@selector(doPlus:)
                 forControlEvents:UIControlEventTouchUpInside];
    
    UIImage *buttonImage = [UIImage imageNamed:@"fc_remove.png"];
    [holder.minusButton setBackgroundImage:buttonImage forState:UIControlStateNormal];

    buttonImage = [UIImage imageNamed:@"fc_add.png"];
    [holder.plusButton setBackgroundImage:buttonImage forState:UIControlStateNormal];
    
    // Small text
    holder.totalCostText = (UILabel*)[Cell viewWithTag:5];
    [self updateTableRow:holder withIndex:indexPath];
    
    return Cell;
}

- (void) updateTableRow:(OptionValuePickerCellHolder*)holder withIndex:(NSIndexPath*)indexPath
{
    
    fcOptionValuePickerDataItem *item = [self.listIndex objectAtIndex:indexPath.row];
    
    if(nil==item)
    {
        return; // Not good
    }
     
    [holder.mainText setTextColor: [UIColor blackColor]];
    [holder.priceText setText: @""];
    [holder.mainText setText: item.optionText];
        
    [holder.minusButton setHidden:YES];
    [holder.plusButton setHidden:YES];
    [holder.totalCostText setText: @""];
        
    if(item.isNone==TRUE)
    {
return;
    }
    
    NSString *costPer = [item.itemTypeOption itemTypeCost];
    
    [holder.mainText setText:@""];
    
    if( (nil!=costPer) &&
        (![costPer isEqualToString:@""]) &&
        (![costPer isEqualToString:@"0.00"]))
    {
        [holder.priceText setText: [NSString stringWithFormat:@"$%@",costPer]];
        
        // Only set total cost where it can possibly be something other than cost per
        // That is when we have a User Option (for safety) and if the max count allowed >1 AND there is a cost per
        if( (item.userOption) && ([item.itemTypeOption itemTypeRangeMax]>1))
        {
            NSDecimalNumber *totalCost = [item.userOption getTotalCost:self.itemType.itemTypeID withMenu:self.menu];
            NSString *totalCostString = [NSString stringWithFormat:@"Total Cost: $%.2f",[totalCost doubleValue]];
        
            [holder.totalCostText setText:totalCostString];
        }
        
    }
    else
    {
        [holder.priceText setText:@""];
    }
    
    [holder.mainText setText: item.optionText];
    
    // == OK as Both NSInteger
    if(item.optionCount == [item.itemTypeOption itemTypeRangeMin])
    {
        [holder.minusButton setHidden:YES];
    }
    else
    {
        [holder.minusButton setHidden:NO];
    }
     
    // == OK as Both NSInteger
    if(item.optionCount == [item.itemTypeOption itemTypeRangeMax])
    {
        [holder.plusButton setHidden:YES];
    }
    else
    {
        [holder.plusButton setHidden:NO];
    }
    
    if ( ([item.optionGroup SelectionType]==ItemOptionGroupSelectOne) &&
         ([item.itemTypeOption itemTypeRangeMax]==1) &&
         ([item.itemTypeOption itemTypeRangeMin]==0))
    {
        [holder.minusButton setHidden:YES];
        [holder.plusButton setHidden:YES];
    }
    
    
    if(item.userOption!=nil)
    {
        [holder.mainText setTextColor: [UIColor greenColor]];
        
    }
    else
    {
        [holder.mainText setTextColor: [UIColor blackColor]];
        
    }
    //holder.item_icon.setImageResource(R.drawable.fc_drink);
}

- (NSInteger) numberOfSectionsInTableView:(UITableView*)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView*)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.listIndex count];
}
- (void) AddFooter
{
    [[NSBundle mainBundle] loadNibNamed:@"fcItemOptionValuePickerTableFooter" owner:self options:nil];
    UIButton *But = (UIButton*)[[self TableFooter] viewWithTag:1];
    [But addTarget:self action:@selector(doBack:) forControlEvents:UIControlEventTouchUpInside];
    
    But = (UIButton*)[[self TableFooter] viewWithTag:2];
    [But addTarget:self action:@selector(doGone:) forControlEvents:UIControlEventTouchUpInside];
    
    
    
    //footerView.userInteractionEnabled = YES;
    self.itemsList.tableFooterView = self.TableFooter;
    self.itemsList.tableFooterView.userInteractionEnabled = YES;
    //[ItemsList.tableFooterView setFrame:ViewSize];
    
    
}

- (void) doMinus:(id)sender
{
    if(nil==sender)
    {
        return;
    }
    
    fcTableCellButton *but = (fcTableCellButton*)(sender);
    
    fcOptionValuePickerDataItem *item =[self.listIndex objectAtIndex:but.cellIndex.row];
    
    if(nil==item)
    {
        return;
    }
    
    
    // OK to compare NSIntegers
    if(item.optionCount > [item.itemTypeOption itemTypeRangeMin])
    {
        // Count is smaller than Max
        item.optionCount=item.optionCount-1;
        if(nil!=item.userOption)
        {
            [item.userOption decrementCount];
            if(item.optionCount==0)
            {
                [self.itemOptionsBeingEdited removeOptionByOptionID: item.optionID];
            }
        }
        
    }
    [self createListIndex];
    
}
- (void) doPlus:(id) sender
{
    if(nil==sender)
    {
        return;
    }
    
    fcTableCellButton *but = (fcTableCellButton*)(sender);
    
    fcOptionValuePickerDataItem *item =[self.listIndex objectAtIndex:but.cellIndex.row];
    
    if(nil==item)
    {
        return;
    }

    if([item.optionGroup SelectionType]==ItemOptionGroupSelectOne)
    {
        // Clear out any other Options from this group *except* this one because we can only have one (SelectOne)
        [self.itemOptionsBeingEdited removeAllOptionsForAnyOtherOptionInGroup:[item.optionGroup GroupID]
                                                                 withOptionID:item.optionID];
        
    }
    
    // OK to compare NSIntegers directly
    if(item.optionCount < [item.itemTypeOption itemTypeRangeMax])
    {
        // Count is smaller than Max
        [item incrementOptionCount];
        
    }
    if(nil==item.userOption)
    {
        fcUserItemOption *userItem = [item createUserItemOption];
        [self.itemOptionsBeingEdited addUserItemOption:userItem];
    }
    else
    {
        [item.userOption setItemOptionCount:item.optionCount];
    }
    
    [self createListIndex];
    
}
- (void) tableView: (UITableView*)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    fcOptionValuePickerDataItem *itemData = [self.listIndex objectAtIndex:[indexPath row]];
    if(nil==itemData)
    {
        NSString *Log = [NSString stringWithFormat:@"Option Value Picker: didSelectRowAtIndexPath Item: %i, Not found In Index (bad)",indexPath.row];
        FC_ALog(@"%@",Log);
        return ;
    }
   
    
    if(itemData.isNone==TRUE)
    {
        [self.itemOptionsBeingEdited removeAllOptionsForOptionGroup: self.itemOptionGroup.GroupID];
        [self doneCommit];
        
        return;
    }
    // Must do this bit before create the UserItem !
    if(itemData.optionCount==0)
    {
        itemData.optionCount=1; // This way, we dont add it with a count of zero and selecting it, will add with a count of 1
    }
    fcUserItemOption *userOption = [itemData createUserItemOption];
    
    
    if([itemData.optionGroup SelectionType]==ItemOptionGroupSelectOne)
    {
        [self.itemOptionsBeingEdited removeAllOptionsForOptionGroup: self.itemOptionGroup.GroupID];
        [self.itemOptionsBeingEdited addUserItemOption:userOption];
        [self doneCommit];
        
        return;
    }
    else if(itemData.userOption==nil)
    {
        // Dont add again and again just by clicking it.
        [self.itemOptionsBeingEdited addUserItemOption:userOption];
        [self createListIndex];
        
    }
}


- (void) doBack:(id) sender;
{
    if([self.delegate respondsToSelector:@selector(childFinished)])
    {
        [self.delegate childFinished];
    }
    
    [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
    
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

@end
