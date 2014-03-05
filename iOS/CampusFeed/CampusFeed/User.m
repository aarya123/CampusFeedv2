//
//  User.m
//  CampusFeed
//
//  Created by Anubhaw Arya on 2/26/14.
//  Copyright (c) 2014 CS 307. All rights reserved.
//

#import "User.h"

@implementation User
- (instancetype) initUserFirstName:(NSString *)fName LastName:(NSString *)lName AndId:(int)id{
    self=[super init];
    if(self)
    {
        self.FirstName=fName;
        self.LastName=lName;
        self.id=id;
    }
    return self;
}
-(instancetype) initUserFirstName:(NSString *)fName LastName:(NSString *)lName AndId:(int)id WithCredentials:(NSString *)fbOAuth{
    self=[super init];
    if(self)
    {
        self.FirstName=fName;
        self.LastName=lName;
        self.id=id;
        self.FBOAuth=fbOAuth;
    }
    return self;
}
@end
