// CheeseBot FB Id
// NOTE: DO NOT CHANGE. UNLESS NEW BOT IS CREATED... 
var CHEESE_BOT_FB_ID = "369050949920159";
var CHEESE_BOT_FB_ID_2 = "1560541364176557";
var CHEESE_BOT_FB_ID_3 = "1555376431342538";

/** Retrieves ranking and player info for user and top ranked players **/
Parse.Cloud.define("onGetRankings", function(request, response) {
    console.log("onGetRankings called");
  
    var passedInUser = request.user;
    var userName = passedInUser.get("firstName");
    var userFacebookId = passedInUser.get("facebookId");
    var userCheeseRow;
    var collectedPlayerInfos = [];
    console.log("user name is: " + userName + "user facebookId is: " + userFacebookId);
  
    var query = new Parse.Query("cheese");
    query.descending("cheeseCount,facebookId");
    query.notEqualTo("facebookId", CHEESE_BOT_FB_ID);
    query.notEqualTo("facebookId", CHEESE_BOT_FB_ID_2);
    query.notEqualTo("facebookId", CHEESE_BOT_FB_ID_3);
    query.limit(10);
    query.find()
        .then(function(cheeseRows){return getTopPlayersInfo(cheeseRows); })
        .then(function(){return getUserCheeseRow();})
        .then(function(userCheeseRow){return getUserInfo(userCheeseRow);})
        .then(
            function(userInfo)
            {
                collectedPlayerInfos.push(userInfo);
                console.log("collectedPlayerInfos is: " + collectedPlayerInfos);
                response.success(collectedPlayerInfos);
            },
            function(error)
            {
                console.log("error occured: " + error);
                resopnse.error(error);
            }
        );
  
        /* get cheese row of current user */
        var getUserCheeseRow = function()
        {
            console.log("getUserCheeseRow called");
            var query = new Parse.Query("cheese");
            query.equalTo("facebookId", userFacebookId);
            return query.first();
        };
  
        /* get user info with facebookId from ParseUser table */
        var getUserInfo = function(userCheeseRow)
        {
            console.log("getUserInfo called");
            var promise = new Parse.Promise();
            var userCheeseCount = userCheeseRow.get("cheeseCount");
            var queryGreater = new Parse.Query("cheese");
            queryGreater.greaterThan("cheeseCount", userCheeseCount);
  
            var queryEqualTo = new Parse.Query("cheese");
            queryEqualTo.equalTo("cheeseCount", userCheeseCount);
            queryEqualTo.greaterThan("facebookId", userFacebookId);
  
            var combinedQuery = Parse.Query.or(queryGreater, queryEqualTo);
            /* excluding bots from count of higher ranking players */
            combinedQuery.notEqualTo("facebookId", CHEESE_BOT_FB_ID);
            combinedQuery.notEqualTo("facebookId", CHEESE_BOT_FB_ID_2);
            combinedQuery.notEqualTo("facebookId", CHEESE_BOT_FB_ID_3);
            combinedQuery.count()
                 .then(function(userRank)
                    {
                        console.log("user rank is " + userRank + 1);
                        var userPlayerInfo = 
                        {
                            facebookId: userFacebookId,
                            cheeseCount: userCheeseCount,
                            firstName: userName,
                            ranking: userRank + 1
                        };
  
                        console.log("User info is: " + userPlayerInfo);
                        promise.resolve(userPlayerInfo);
                    });
  
            return promise;
        };
  
        /* get player info from top 10 players, like firstName, and add to array */
        var getTopPlayersInfo = function(cheeseRows)
        {
            var topFacebookIds = [];
            var playerCheeseMap = {};
            for(var ii=0; ii< cheeseRows.length; ii++) {
                var playerFacebookId = cheeseRows[ii].get("facebookId");
  
                var cheeseCount = cheeseRows[ii].get("cheeseCount");
                console.log("player " + playerFacebookId + " has cheese: " + cheeseCount);
                playerCheeseMap[playerFacebookId] = 
                                                    { cheeseCount: cheeseCount,
                                                      ranking: ii + 1
                                                    };
                topFacebookIds.push(playerFacebookId);
            }
  
            var query = new Parse.Query(Parse.User);
            query.containedIn("facebookId", topFacebookIds);
            var promise = new Parse.Promise();
            query.find().then(function(parseUsers)
                {
                    for(var ii=0;ii<parseUsers.length;ii++)
                    {
                        /* null protect in case player is in cheese table but not ParseUser table */
                        var player = parseUsers[ii];
                        if (player === null)
                            return false;
  
                        var playerFacebookId = player.get("facebookId");
                        var playerInfo = 
                        {
                            facebookId: playerFacebookId,
                            cheeseCount: playerCheeseMap[playerFacebookId]["cheeseCount"],
                            firstName: player.get("firstName"),
                            ranking: playerCheeseMap[playerFacebookId]["ranking"]
                        };
  
                        collectedPlayerInfos.push(playerInfo);
                    }
                }).then(
                    function()
                    {
                        console.log("getTopPlayersInfo success! player info is: " + collectedPlayerInfos);
                        promise.resolve();
                    },
                    function(error)
                    {
                        response.error("getTopPlayersInfofailed with: " + error);
                    }
                );
  
            return promise;
        };
    });
/** End of onGetRankings **/
  
  
  
/**
Method used for returning the final state of the friends
cheese count, along with the flag which states whether to 
enable or disable the friend i.e. is the friend eligible for
stealing.
**/
var getFriendsCheeseCounts = function(friendFacebookIds, thiefFacebookId) {
		
		var moment = require('moment');
		Parse.Cloud.useMasterKey();
        var query = new Parse.Query("cheese");
        var finalCheesUpdates = {};
        var promise = new Parse.Promise();
        var d = new Date(); // gets today
        var dMinus = new Date(d - 1000 * 60 * 60 * 24 *.08); // around 2 hrs ago
        console.log("Around Two hour time..." + dMinus);
        var dMinusMoment = moment(dMinus);
        var responsePayload = {};
        var isCounterNeeded = true;
        
        console.log("getFriendsCheeseCounts received friendsFacebookIds: " + friendFacebookIds);
        query.containedIn("facebookId", friendFacebookIds);
        query.find().then(function(usersFriends){   
                for(var i = 0; i < usersFriends.length; i++){
                    var currFBId = usersFriends[i].get("facebookId");
                    finalCheesUpdates[currFBId] = {
                        facebookId: currFBId,
                         showMe: null,
                         cheeseCount: usersFriends[i].get("cheeseCount"),
                         animateMe: false
                    };
                }
                console.log("INITIAL WRAPPERR IS ...");
                console.log(finalCheesUpdates);
                     
                var findWhereThiefIsVictimQuery = new Parse.Query("theftdirection");
                findWhereThiefIsVictimQuery.equalTo("victimFBId", thiefFacebookId);
                return findWhereThiefIsVictimQuery.find();
             
            }).then(function(thiefs){
                for(var i = 0; i < thiefs.length; i++){
                    var fbId = thiefs[i].get("thiefFBId");
                    console.log("WhereThiefIsVictim of id " + fbId);
                    finalCheesUpdates[fbId].showMe = true;
                }
                     
                //var dMinus1 = new Date("November 8, 2014 10:10:00");
                var findWhereThiefIsThief = new Parse.Query("theftdirection");
                findWhereThiefIsThief.equalTo("thiefFBId", thiefFacebookId);
                findWhereThiefIsThief.containedIn("victimFBId", friendFacebookIds);
                findWhereThiefIsThief.greaterThanOrEqualTo("updatedAt", dMinus);
                return findWhereThiefIsThief.find();
                 
            }).then(function(currThiefs){
            	var timeLeftArray = [];
                for(var i = 0; i < currThiefs.length; i++){
                	var fbId = currThiefs[i].get("victimFBId");
                    console.log("WhereThiefIsThief for id " + fbId);
                    finalCheesUpdates[fbId].showMe = false;
                    var updatedAtMoment = moment(currThiefs[i].updatedAt);
                    var timeLeftForNextSteal = moment.duration(updatedAtMoment.diff(dMinusMoment)).asSeconds();
                    //console.log("Sec left: " + timeLeftForNextSteal);
                    timeLeftArray.push(timeLeftForNextSteal);
                    //console.log(moment().startOf('day').seconds(timeLeftForNextSteal).format('H:mm:ss'));
            	}
            	if(timeLeftArray.length > 0){
                	timeLeftArray.sort(function(a, b){return a-b});
                	console.log(timeLeftArray[0]);
                	responsePayload["countDown"] = timeLeftArray[0];
                	console.log(moment().startOf('day').seconds(timeLeftArray[0]).format('H:mm:ss'));
                }
             	 
            	var finalCheesUpdatesList = [];
            	for(var key in finalCheesUpdates){
                     
                	// check for 0 cheese count, and disabling the image
                	var localCheeseCount = finalCheesUpdates[key].cheeseCount;
                	var showMeFlag = finalCheesUpdates[key].showMe;
                	if(showMeFlag && thiefFacebookId != finalCheesUpdates[key].facebookId){
                		isCounterNeeded = false;
                	}
                	if(localCheeseCount < 1){
                    	finalCheesUpdates[key].showMe = false;
                	}
                	if(finalCheesUpdates[key].showMe == null){
                    	finalCheesUpdates[key].showMe = true;
                    	if(thiefFacebookId != finalCheesUpdates[key].facebookId){
                    		isCounterNeeded = false;
                    	}
                	}
                	finalCheesUpdatesList.push(finalCheesUpdates[key]);
            	}
            	// remove countdown if even one steal is available
                if(!isCounterNeeded){
                	delete responsePayload["countDown"];
                }
                	
            	responsePayload["cheeseCountList"] = finalCheesUpdatesList;
           		console.log("FINAL WRAPPERR IS ...");
           		console.log(responsePayload); 
            	promise.resolve(responsePayload);
             
        });
         
     return promise;
}
     
  
/**
Method used for sending push notification
to the friend from whom the cheese was stolen
**/
var performNotification = function(thiefName, victimFacebookId, thiefUser, victimUser){ 
        console.log("Running notification...");
        var sampleQuery = new Parse.Query(Parse.Installation); 
        sampleQuery.equalTo('facebookId', victimFacebookId);
        //query.notEqualTo('facebookId', CHEESE_BOT_FB_ID);
        var message =  thiefName  +  ' just snatched your cheese!'
        var promise = new Parse.Promise();
           
        if(victimFacebookId == CHEESE_BOT_FB_ID || victimFacebookId == CHEESE_BOT_FB_ID_2
                                                || victimFacebookId == CHEESE_BOT_FB_ID_3){
            promise.resolve("Notification sent");
             return promise;    
        }
           
        Parse.Push.send({
            where:sampleQuery,
            data: {
                alert: message,
                thiefId: thiefUser.get("facebookId"),
                thiefCheeseCount: thiefUser.get("cheeseCount"),
                victimCheeseCount: victimUser.get("cheeseCount"),
                animateMe: true
            },
            expiration_interval : 300
            }, 
            {   
                success: function() {
                    promise.resolve("Notification sent");
                },
                error: function(error) {
                    console.log("Notification error..");
                    console.log(error);
                    promise.reject("Notification error");
                }
            });
            
        return promise;
}
        
  
/**
  Method used for inserting a new record for the current
  theft, to maintain theft history. 
*/
var insertTheftHistory = function(thiefFacebookId,victimFacebookId) {
        var TheftHistoryClass = Parse.Object.extend("thefthistory");
        var theftHistory = new TheftHistoryClass();
        theftHistory.set("thiefFBId", thiefFacebookId);
        theftHistory.set("victimFBId", victimFacebookId);
            
        var promise = new Parse.Promise();
              
        console.log("adding theft history for thief " + thiefFacebookId + " and victim " + victimFacebookId);
             
        theftHistory.save(null,{
          success:function(theftResponse) { 
            console.log(theftResponse);
            promise.resolve("All saved...");
          },
          error:function(error) {
            console.log(error);
            promise.resolve("Error saving");
          }
        });
            
        return promise;
}
        
/**
    Method used for maintaining theft direction between any two given friends.
    This data is used for deriving whether a friend in friends-list on UI, 
    is enabled vs disabled.
**/
var updateTheftDirection = function(thiefFacebookId,victimFacebookId){
        console.log("Inside theft direction...");
        Parse.Cloud.useMasterKey();
        var fwdDirection = new Parse.Query("theftdirection");
        fwdDirection.equalTo("thiefFBId", thiefFacebookId);
        fwdDirection.equalTo("victimFBId", victimFacebookId);
             
        var reverseDirection = new Parse.Query("theftdirection");
        reverseDirection.equalTo("thiefFBId", victimFacebookId);
        reverseDirection.equalTo("victimFBId", thiefFacebookId);
            
        var promise = new Parse.Promise();
             
        var directionQuery = Parse.Query.or(fwdDirection, reverseDirection);
        directionQuery.find({
            success: function(theftVictimCombination)
            {
               // console.log("Combination size : " + theftVictimCombination.length);
                if(theftVictimCombination.length < 1){
                    var TheftDirectionClass = Parse.Object.extend("theftdirection");
                    var theftDir = new TheftDirectionClass();
                    theftDir.set("thiefFBId", thiefFacebookId);
                    theftDir.set("victimFBId", victimFacebookId);
                    console.log("Inserted new combination");
                    theftDir.save().then(function(){
                         promise.resolve("This is a test");
                    });
                }else {
                    console.log("updating existing combination...");
                    theftVictimCombination[0].set("thiefFBId", thiefFacebookId);
                    theftVictimCombination[0].set("victimFBId", victimFacebookId);
                    theftVictimCombination[0].save().then(function(){
                         promise.resolve("This is a test");
                    });
                }
                     
            },
            error: function(error)
            {   
                console.log("Not able to find any row..." + error);
                promise.reject("Test again");
                     
            }   
        });
            
    return promise; 
}
        
     
/** 
  Cloud code used being executed when a theft action
  takes place on the device.
**/
Parse.Cloud.define("onCheeseTheft", function(request, response) {
    console.log(request);
    var thiefFacebookId = request.params.thiefFacebookId;
    var victimFacebookId = request.params.victimFacebookId;
    var passedInUser = request.user;
    var thiefName =  passedInUser.get('firstName') ;
    var thiefUserCheese;
    var victimUserCheese;
    console.log(thiefFacebookId + " is stealing cheese from " + victimFacebookId);
    var query = new Parse.Query("cheese");
    var facebookIds = [thiefFacebookId, victimFacebookId];
    var victimUser;
      
    query.containedIn("facebookId", facebookIds);
    query.find().then(function(cheeseRows){
                        console.log("Ran cheese query...");
                        findVictimThiefCheeseRows(cheeseRows);
                        victimUserCheese.increment("cheeseCount", -1);
                        return victimUserCheese.save();
                    }).then(function(victim) {
                        victimUser = victim;
                        console.log("I'm in success");
                        thiefUserCheese.increment("cheeseCount");   
                        return thiefUserCheese.save();
                        },
                        function(error){
                        response.error(error);
                 })
                .then(function(thiefUser){return performNotification(thiefName,victimFacebookId,thiefUser,victimUser);})
                .then(function(){return insertTheftHistory(thiefFacebookId,victimFacebookId);})
                .then(function(){return updateTheftDirection(thiefFacebookId,victimFacebookId);})
                .then(function(){return getUserFriendsFacebookIds();})
                .then(function(friendFacebookIds){
                        return getFriendsCheeseCounts(friendFacebookIds, thiefFacebookId);
                    
                }).then(function(responsePayload){
                    response.success(responsePayload);
                        
                });
                                 
         
       
         
    /* find and set victim and thief cheese rows */
    var findVictimThiefCheeseRows = function(cheeseRows) {                   
        for(var ii=0; ii< cheeseRows.length; ii++) {
            var userFacebookId = cheeseRows[ii].get("facebookId");
            var cheeseCount = cheeseRows[ii].get("cheeseCount");
            console.log("user " + userFacebookId + " has cheese: " + cheeseCount);
            if (userFacebookId === thiefFacebookId){
                thiefUserCheese = cheeseRows[ii];
            }else {
                victimUserCheese = cheeseRows[ii];
            }
        }
    }   
         
         
    /* return all facebook ids of user's friends */
    var getUserFriendsFacebookIds = function() {
        var query = new Parse.Query(Parse.User);
        query.equalTo("facebookId", thiefFacebookId);
        var promise = new Parse.Promise();
        query.find().then(
                function(user)
                {
                    console.log(user);
                    var friendFacebookIds = user[0].get("friends");
                    console.log("User has friends: " + friendFacebookIds);
                    friendFacebookIds.push(thiefFacebookId);
                    //return friendFacebookIds;
                    promise.resolve(friendFacebookIds);
                },
                function(error)
                {
                    console.log("Cannot find user friends facebookIds" + error);
                    response.error("Cannot find user friends facebookIds");
                }   
                 
            );
        return promise;
    }
         
});
     
/**
    Cloud code which executed when during a fresh launch of MyCheez
**/ 
Parse.Cloud.define("onLoginActivity", function(request, response) {
    console.log(request);
    Parse.Cloud.useMasterKey();
              
    var isNewUser = request.params.isNewUserFlag;
    var passedInUser = request.user;
      
    // defensive check when user is not being sent from client
    // after a long time of inactivity
    if(passedInUser == null) {
        response.error("No current user present");
    }
    console.log("START Passed In USER ...");
    console.log(passedInUser);
    console.log("END Passed In USER ...");
    var fbaccessToken = passedInUser.get('authData').facebook.access_token;
    console.log("fbaccessToken " + fbaccessToken);
    var currentFBUserId = passedInUser.get("facebookId");
         
    var existingUserSteps = function(request, response, isBot) {
        console.log("Inside performExistingUser Steps...");
        Parse.Cloud.httpRequest({
               url: 'https://graph.facebook.com/me/friends?access_token=' + fbaccessToken
        }).then(function(httpResponse){
                    console.log("Fb Reponse " + httpResponse.text);
                    var fbResponse = httpResponse['data'].data;
                    console.log(fbResponse);
                    var friendsList = [CHEESE_BOT_FB_ID, CHEESE_BOT_FB_ID_2, CHEESE_BOT_FB_ID_3];
                    if(isBot){
                        console.log("I am bot..");
                        return allUsersList;
                    }
                    for(var i = 0; i < fbResponse.length; i++) {
                        var fbId = fbResponse[i].id;
                        friendsList.push(fbId);
                    }
                     return friendsList;
                              
                    }, function(errorResponse){
                        // Throw error
                        console.error('Request failed with response code ' + errorResponse.status);
                      
                }).then(function(allFriendslist){
                    //backdoor to add more friends for testing
                    for(var i = 0; i<allFriendslist.length; i++){
                        passedInUser.addUnique("friends", allFriendslist[i]);
                    }
                    return passedInUser.save();
                      
                }).then(function(savedUser){
                        console.log("In final stage");
                        var newFriendList = savedUser.get("friends");
                        newFriendList.push(passedInUser.get("facebookId"));
                        console.log(newFriendList);
                        var query = new Parse.Query(Parse.User);
                        query.containedIn("facebookId", newFriendList);
                        return query.find();
                          
                }).then(function(allCheeseCountObjects){
                            response.success(allCheeseCountObjects);
                        },function(errorHandler){
                            response.error("Not able to complete this operation");
                        }
                );
    }   
         
    var doCommonSteps = function(isBot){
        if(isNewUser) {
            console.log("Inside NEW USER BLOCK...");
            var CheeseCountClass = Parse.Object.extend("cheese");
            var cheeseCount = new CheeseCountClass();
            cheeseCount.set("facebookId", passedInUser.get("facebookId"));
            cheeseCount.set("cheeseCount", 20);
            cheeseCount.save().then(function(cheeseCount){
                        return existingUserSteps(request, response, isBot);
                });
        } else {
            console.log("Inside else part...");
            return existingUserSteps(request, response, isBot);
        }
    }
         
    var allUsersList = [];
    var query = new Parse.Query(Parse.User);
    //query.equalTo("facebookId", CHEESE_BOT_FB_ID);
    query.containedIn("facebookId", [CHEESE_BOT_FB_ID, CHEESE_BOT_FB_ID_2, CHEESE_BOT_FB_ID_3]);
    var origBotUser = null;
    var botUser2 = null;
    var botUser3 = null;
    query.find().then(function(botUsers){
        console.log("bot users are: " + botUsers);
        origBotUser = botUsers[0];
        botUser2 = botUsers[1];
        botUser3 = botUsers[2];
        allUsersList = origBotUser.get("friends");
        return allUsersList;
    }).then(function(fulllist){
        if(currentFBUserId == CHEESE_BOT_FB_ID || currentFBUserId == CHEESE_BOT_FB_ID_2
                                                || currentFBUserId == CHEESE_BOT_FB_ID_3){
            console.log("I am BOT..");
            doCommonSteps(true);
        }else {
                origBotUser.addUnique("friends", passedInUser.get("facebookId"));
                botUser2.addUnique("friends", passedInUser.get("facebookId"));
                botUser3.addUnique("friends", passedInUser.get("facebookId"));
                origBotUser.save();
                botUser2.save();

                botUser3.save(null,{
                    success:function(theftResponse) { 
                        doCommonSteps(false);
                    },
                    error:function(error) {
                        response.error(error);
                    }
                });
            }
     });
         
         
});
     


/**
Cloud code to just return the latest cheese counts for the friends
along with the enable-disable flag
**/ 
Parse.Cloud.define("getAllCheeseCounts", function(request, response) {
    console.log("In getAllCheeseCounts...");
    console.log(request);
    var friendsList = request.user.get("friends");
    var query = new Parse.Query("cheese");
    friendsList.push(request.user.get("facebookId"));
    getFriendsCheeseCounts(friendsList, request.user.get("facebookId"))
        .then(function(responsePayload){
                response.success(responsePayload);
        
            });
});
     
     
     
     
/* beforeSave function for the cheese table to check victim cheese count */
Parse.Cloud.beforeSave("cheese", function(re, response){
    var myCount = re.object.get("cheeseCount");
    //console.log("Player cheese count is: " + myCount);
    if(myCount >= 0) {
        response.success();
    } else {
        response.error("Victim has no cheese! Cannot steal from victim");
    }
});
   
   
   
/**
    Background job : Used for triggering the Cheese Bot action
    for stealing cheese from all users every certain time
**/
Parse.Cloud.job("botAction", function(request, status) {
  Parse.Cloud.useMasterKey();
  var _ = require('underscore.js');
  var size = 0;
  var mainBotUser;
    
  var cleanupQuery = new Parse.Query("thefthistory");
  var today = new Date();
  console.error(today);
  today.setDate(today.getDate() - 1); // 1 days old history
  console.error(today);
  cleanupQuery.lessThan("createdAt", today);
  cleanupQuery.find().then(function(histResult){
        console.log("Hist size is >> ", histResult.length);
        return Parse.Object.destroyAll(histResult);
    
  }).then(function(){

        //select random cheese bot to steal cheese from user
        var cheeseBotsArray = [CHEESE_BOT_FB_ID, CHEESE_BOT_FB_ID_2, CHEESE_BOT_FB_ID_3];
        var selectedBot = cheeseBotsArray[Math.floor(Math.random() * cheeseBotsArray.length)];
        var query = new Parse.Query("cheese");
        query.equalTo("facebookId", selectedBot);
        query.find().then(function(user){
            mainBotUser = user[0];
            return user[0];
    }).then(function(temp){
            var query = new Parse.Query(Parse.User);
            query.equalTo("facebookId", selectedBot);
            return query.find();
        }).then(function(botUser){
            var allUsersList = botUser[0].get("friends");
            return getFriendsCheeseCounts(allUsersList, selectedBot);
        }).then(function(responsePayload){
            console.log("Se mee here");
            console.log(responsePayload);
            var filteredList = [];
            var allCounts = responsePayload["cheeseCountList"];
            for(var i = 0; i < allCounts.length; i++){
                if(allCounts[i].showMe){
                    filteredList.push(allCounts[i]);
                }
            }
            size = filteredList.length;
            if(size == 0){
                status.success("Nothing to steal...");
            }
            console.log("Running filter loop...")
            _.each(filteredList, function(filter) {
                var currentObj = filter;
                var victimUser;
                var query = new Parse.Query("cheese");
                    query.equalTo("facebookId", currentObj["facebookId"]);
                    query.find().then(function(friend){
                        friend[0].increment("cheeseCount", -1);
                        return friend[0].save();
                    }).then(function(returnFriend){
                        victimUser = returnFriend;
                        mainBotUser.increment("cheeseCount", 1);
                        return mainBotUser.save();
                    }).then(function(botUser){
                        return performNotification("Cheesy", currentObj["facebookId"], botUser, victimUser);
                    }).then(function(){
                        return insertTheftHistory(selectedBot,currentObj["facebookId"]);
                    }).then(function(){
                        return updateTheftDirection(selectedBot,currentObj["facebookId"]);
                    }).then(function(){
                        size--;
                        if(size == 0){
                            console.log("I am done finally...");
                            status.success("I am all done");
                        }
                    });
                    
                });
        });
    });
                
});