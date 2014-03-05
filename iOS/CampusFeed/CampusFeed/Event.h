//
//  Event.h
//  CampusFeed
//
//  Created by Anubhaw Arya on 2/27/14.
//  Copyright (c) 2014 CS 307. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Event : NSObject
@property (nonatomic) NSString *name;
@property (nonatomic) NSString *location;
@property (nonatomic) NSDate *date;
@property (nonatomic) NSString *description;
@property (nonatomic) int *status;
@property (nonatomic) int *visibility;
@property (nonatomic) NSDate *creationDate;
@property (nonatomic) NSDate *modificationDate;
@property (nonatomic) int id;
@end
