//
//  fcOrderPriceBreakdownViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 3/30/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import "fcOrderPriceBreakdownViewController.h"
#import "fcAppDelegate.h"
#import "WEPopoverController.h"

@interface fcOrderPriceBreakdownViewController ()

@end

@implementation fcOrderPriceBreakdownViewController
@synthesize orderTotal=_orderTotal;
@synthesize orderItemsTotal=_orderItemsTotal;
@synthesize orderDiscount=_orderDiscount;
@synthesize orderTip=_orderTip;
@synthesize orderTax=_orderTax;
@synthesize orderTaxableAmount=_orderTaxableAmount;
@synthesize orderTaxable=_orderTaxable;
@synthesize orderConvenienceFee = _orderConvenienceFee;
@synthesize WEcontroller=_WEcontroller;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    [myCommonAppDelegate prepareButtonForGradient:self.doneBut];
        
    CAGradientLayer *feedbackGrad =  [myCommonAppDelegate makeGreyGradient];
    feedbackGrad.frame = self.doneBut.bounds;
    [self.doneBut.layer insertSublayer:feedbackGrad atIndex:0];

    
    NSNumberFormatter *currencyFormatter  = [myCommonAppDelegate getCurrencyFormatter];
    
    NSMutableString *text = [NSMutableString stringWithFormat:@"      Order Breakdown\n===================\n\nItems Total:\t\t%@\n",
                             [currencyFormatter stringFromNumber:self.orderItemsTotal]];
                             
    [text appendFormat:@"Discount:\t\t\t%@\n",[currencyFormatter stringFromNumber:self.orderDiscount] ];
                                        
        if([self.orderConvenienceFee compare:[NSDecimalNumber zero]]!=NSOrderedSame)
    {
       [ text appendFormat:@"Additional Fee:\t\t%@\n",[currencyFormatter stringFromNumber:self.orderConvenienceFee ]];
    }
    
    if(self.orderTaxable==TRUE)
    {
        [text appendFormat:@"Total Taxable:\t\t%@\n",[currencyFormatter stringFromNumber:self.orderTaxableAmount]];
    }
    
    [text appendFormat:@"Tax:\t\t\t\t%@\n",[currencyFormatter stringFromNumber:self.orderTax]];
    
    [text appendFormat:@"\nTip:\t\t\t\t%@\n",[currencyFormatter stringFromNumber:self.orderTip]];
    
    
    [text appendFormat:@"===================\nOrder Total:\t\t%@\n",[currencyFormatter stringFromNumber:self.orderTotal]];

     
    
    self.mainText.text= text;

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(IBAction) doDone:(id)sender
{
    if(self.WEcontroller)
    {
        [self.WEcontroller dismissPopoverAnimated:NO];
    }

}
@end
