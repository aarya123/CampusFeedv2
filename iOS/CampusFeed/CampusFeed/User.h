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
@property (nonatomic) NSString *FirstName;
@property (nonatomic) NSString *LastName;
@property (nonatomic) NSString *FBOAuth;
@property (nonatomic) int id;
- (instancetype) initUserFirstName:(NSString *) fName
                          LastName: (NSString *) lName
                             AndId: (int) id;
- (instancetype) initUserFirstName:(NSString *) fName LastName: (NSString *) lName AndId: (int) id WithCredentials:(NSString *) fbOAuth;
@end
