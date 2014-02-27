//
//  User.h
//  CampusFeed
//
//  Created by Anubhaw Arya on 2/26/14.
//  Copyright (c) 2014 CS 307. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface User : NSObject
@property (nonatomic) int UserId;
@property (strong,nonatomic) NSString *FirstName;
@property (strong,nonatomic) NSString *LastName;
@property (strong,nonatomic) NSString *FBOAuth;
@end
