//
//  fcCarDataValuePickerViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcCarDataValuePickerViewController.h"
#import "fcAppDelegate.h"
#import "Constants.h"
#import "CarDataIndexItem.h"
#import "CarDataValuePickerIndexItem.h"
#import "CarMakeModelColorData.h"
#import "CarColor.h"
#import "CarModel.h"
#import "CarModelList.h"

@interface fcCarDataValuePickerViewController ()

@end

@implementation fcCarDataValuePickerViewController
//@synthesize WelcomeUserLabel;
@synthesize ListIndex;
@synthesize delegate=_delegate;

@synthesize ItemsList=_itemsList;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        // Custom initialization
    }
    return self;
}

- (id)initWithNibName:(NSString *)nibNameOrNil andPickType:(int)inPickType andMakeID:(int)inMakeID andPopTwo:(BOOL)inPopTwo
{
    self = [super initWithNibName:nibNameOrNil bundle:nil];
    if (self)
    {
        if (self)
        {
            PickType = inPickType;
            switch(PickType)
            {
                case CAR_DATA_ITEM_TYPE_MAKE:
                    PickTypeStr=CAR_MAKE_TEXT;
                    break;
                case CAR_DATA_ITEM_TYPE_MODEL:
                    PickTypeStr=CAR_MODEL_TEXT;
                    break;
                case CAR_DATA_ITEM_TYPE_COLOR:
                    PickTypeStr = CAR_COLOR_TEXT;
                    break;
                default:
                    PickTypeStr=NONE_TEXT;
            }
            MakeID = inMakeID;
            PopTwo=inPopTwo;
        }
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    NSString *WelcomeString = [NSString stringWithFormat:@"%@",PickTypeStr];
    
    UIColor *background = [[UIColor alloc] initWithPatternImage:[UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
        
    self.title=WelcomeString;
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:WelcomeString style:UIBarButtonItemStyleBordered target:self action:nil];
    [self.navigationItem setBackBarButtonItem: backButton];

    ListIndex = [[NSMutableArray alloc] init];
    
    [self CreateListIndex];
}
    - (void) CreateListIndex
    {
        
        CarDataValuePickerIndexItem *ListItem  = nil;
        [ListIndex removeAllObjects];
        
        if(PickType==CAR_DATA_ITEM_TYPE_MAKE)
        {
            NSMutableDictionary *CarMakesAndModels =
            [[myCommonAppDelegate CarData] CarMakesAndModels];
            
            for( id aKey in [CarMakesAndModels allKeys] )
            {
                CarMake *make = [CarMakesAndModels objectForKey: aKey];
                ListItem = [[CarDataValuePickerIndexItem alloc] init];
                
                [ListItem setItemText: [make MakeLongDescr]];
                [ListItem setSortOrder:[make SortOrder]];
                [ListItem setM_ItemID: [make MakeID]];
                
                if([make CanHaveModels] && ([make GetNumberOfModels]>0))
                {
                    [ListItem setM_HasModels:TRUE];
                }
                else
                {
                    [ListItem setM_HasModels:FALSE];
                }
                [ListIndex addObject:ListItem];
                                
            }
            
        }
        else if(PickType==CAR_DATA_ITEM_TYPE_MODEL)
        {
            if(MakeID==0)
            {
                return;
            }
            CarMake *make = [[myCommonAppDelegate CarData ] GetCarMake:MakeID] ;
            if( [make CanHaveModels]!=TRUE)
            {
                return; // Should never get here !
            }
            for(id Key in [[make Models] Models] )
            {
                CarModel *model = [[[make Models ] Models] objectForKey: Key];
                if(model!=NULL)
                {
                    ListItem = [[CarDataValuePickerIndexItem alloc] init];
                    
                    [ListItem setItemText: [model ModelLongDescr]];
                    [ListItem setSortOrder:[model SortOrder]];
                    [ListItem setM_ItemID: [model ModelID]];
                    [ListIndex addObject:ListItem];
                    
                }
            }
            
            
            
        }
        else if(PickType==CAR_DATA_ITEM_TYPE_COLOR)
        {
            // I cannot believe i just did that. Hire iPhone expert to shoot me (asap)
            NSMutableDictionary *CarColors =
            [[[myCommonAppDelegate CarData] CarColors] CarColors];
            
            for( id aKey in [CarColors allKeys] )
            {
                CarColor *color = [CarColors objectForKey: aKey];
                ListItem = [[CarDataValuePickerIndexItem alloc] init];
                
                [ListItem setItemText: [color CarColorLongDescr]];
                [ListItem setSortOrder:[color SortOrder]];
                [ListItem setM_ItemID: [color CarColorID]];
                [ListIndex addObject:ListItem];
                
                
            }
            
        }
        
        self.ListIndex = [[ListIndex sortedArrayUsingSelector:@selector(compare:)] mutableCopy];
        
        [self.ItemsList reloadData];
    }
    
    - (NSInteger) numberOfSectionsInTableView:(UITableView*)tableView
    {
        return 1;
    }
    
    - (NSInteger)tableView:(UITableView*)tableView numberOfRowsInSection:(NSInteger)section
    {
        return [ListIndex count];
    }
    
    - (UITableViewCell*)tableView:(UITableView*)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
    {
        static NSString *CarCellID = @"CarTableCell";
        
        UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CarCellID];
        if(cell==nil)
        {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CarCellID] ;
        }
        
        CarDataValuePickerIndexItem *listItem = [ListIndex objectAtIndex:indexPath.row];
        
        cell.textLabel.text=[listItem ItemText];
        if(PickType == CAR_DATA_ITEM_TYPE_MAKE)
        {
            if( [listItem m_HasModels] ==TRUE)
            {
                cell.accessoryType=UITableViewCellAccessoryDisclosureIndicator;
            }
            else
            {
                cell.accessoryType=UITableViewCellAccessoryNone;
            }
        }
        
        return cell;
        
    }
    
    - (void) tableView: (UITableView*)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
    {
        CarDataValuePickerIndexItem *Item = [ListIndex objectAtIndex:indexPath.row];
        if(nil==Item)
        {
            NSString *Log = [NSString stringWithFormat:@"CarValuePicker: didSelectRowAtIndexPath Item: %i, Not found In Index (bad)",indexPath.row];
            FC_ALog(@"%@",Log);
            return ;
        }
        
        switch(PickType)
        {
            case CAR_DATA_ITEM_TYPE_MAKE:
                if([self.delegate respondsToSelector:@selector(setMakeID:)])
                {
                    [self.delegate setMakeID: [Item m_ItemID]];
                    if([Item m_HasModels]!=TRUE)
                    {
                        [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
                    }
                    else
                    {
                        fcCarDataValuePickerViewController *CarView =
                        
                        
                        [[ fcCarDataValuePickerViewController alloc] initWithNibName:@"fcCarDataValuePickerViewController"
                                                                       andPickType:CAR_DATA_ITEM_TYPE_MODEL andMakeID:Item.m_ItemID andPopTwo:PopTwo];
                        CarView.delegate=self.delegate;
                        
                        [ [myCommonAppDelegate navController] pushViewController:CarView animated:NO];
                    }
                }
                return;
                break;
            case CAR_DATA_ITEM_TYPE_MODEL:
                if([self.delegate respondsToSelector:@selector(setModelID:)])
                {
                    [self.delegate setModelID: [Item m_ItemID]];
                    if(PopTwo==TRUE)
                    {
                        
                        [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
                        [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
                        // NSArray *allViewControllers = self.navigationController.viewControllers;
                        //NSInteger n = [allViewControllers count];
                        //[self.navigationController popToViewController: [allViewControllers objectAtIndex: (n-2)] animated: NO];
                    }
                    else
                    {
                        [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
                    }
                }
                return;
                break;
            case CAR_DATA_ITEM_TYPE_COLOR:
                if([self.delegate respondsToSelector:@selector(setColorID:)])
                {
                    [self.delegate setColorID: [Item m_ItemID]];
                    [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
                }
                return;
                
            default:
                // TODO ALert ?
                FC_Log(@"Invald Pick TYpe in CarDataValuePicker:didSelectRowAtIndexPath %d",PickType);
                return;
        }
    }
    
   
- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.ItemsList deselectRowAtIndexPath:[self.ItemsList indexPathForSelectedRow] animated:NO];
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
