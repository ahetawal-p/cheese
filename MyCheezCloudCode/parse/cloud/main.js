// CheeseBot FB Id
// NOTE: DO NOT CHANGE. UNLESS NEW BOT IS CREATED... 
var CHEESE_BOT_FB_ID = "369050949920159";

/** TEST - this version takes in user facebookId 
Retrieves ranking and player info for user and top ranked players
**/
Parse.Cloud.define("onTestGetRankings", function(request, response) {
    console.log("onTestGetRankings called");

    var passedInUser = request.user;
    var userName =  'fakeFirstName';
    var userFacebookId = request.params.facebookId;
    var userCheeseRow;
    var collectedPlayerInfos = [];
    console.log("user name is: " + userName + "user facebookId is: " + userFacebookId);

    var query = new Parse.Query("cheese");
    query.descending("cheeseCount");
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
            var query = new Parse.Query("cheese");
            query.greaterThan("cheeseCount", userCheeseCount);

            /* excluding bots from count of higher ranking players */
            query.notEqualTo("facebookId", CHEESE_BOT_FB_ID);
            query.count()
                 .then(function(userRank)
                    {
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
            var ranking = 1;
            for(var ii=0; ii< cheeseRows.length; ii++) {
                var playerFacebookId = cheeseRows[ii].get("facebookId");

                /* exclude bot from ranking */
                if (playerFacebookId === CHEESE_BOT_FB_ID)
                {
                    console.log("cheese bot identified! "+ playerFacebookId);
                    continue;
                }

                var cheeseCount = cheeseRows[ii].get("cheeseCount");
                console.log("player " + playerFacebookId + " has cheese: " + cheeseCount);
                playerCheeseMap[playerFacebookId] = 
                                                    { cheeseCount: cheeseCount,
                                                      ranking: ranking
                                                    };
                topFacebookIds.push(playerFacebookId);

                ranking++;
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

/** End of onTestGetRankings **/


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
    query.descending("cheeseCount");
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
            var query = new Parse.Query("cheese");
            query.greaterThan("cheeseCount", userCheeseCount);

            /* excluding bots from count of higher ranking players */
            query.notEqualTo("facebookId", CHEESE_BOT_FB_ID);
            query.count()
                 .then(function(userRank)
                    {
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
            var ranking = 1;
            for(var ii=0; ii< cheeseRows.length; ii++) {
                var playerFacebookId = cheeseRows[ii].get("facebookId");

                /* exclude bot from ranking */
                if (playerFacebookId === CHEESE_BOT_FB_ID)
                {
                    console.log("cheese bot identified! "+ playerFacebookId);
                    continue;
                }

                var cheeseCount = cheeseRows[ii].get("cheeseCount");
                console.log("player " + playerFacebookId + " has cheese: " + cheeseCount);
                playerCheeseMap[playerFacebookId] = 
                                                    { cheeseCount: cheeseCount,
                                                      ranking: ranking
                                                    };
                topFacebookIds.push(playerFacebookId);

                ranking++;
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
        var query = new Parse.Query("cheese");
        var finalCheesUpdates = {};
        var promise = new Parse.Promise();
        console.log("getFriendsCheeseCounts received friendsFacebookIds: " + friendFacebookIds);
        query.containedIn("facebookId", friendFacebookIds);
        query.find().then(function(usersFriends){   
                for(var i = 0; i < usersFriends.length; i++){
                    var currFBId = usersFriends[i].get("facebookId");
                    finalCheesUpdates[currFBId] = {
                        facebookId: currFBId,
                         showMe: null,
                         cheeseCount: usersFriends[i].get("cheeseCount")
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
               
                var d = new Date(); // gets today
                var dMinus = new Date(d - 1000 * 60 * 60 * 24 *.08); // around 2 hrs ago
                console.log("Around Two hour time..." + dMinus);
                   
                //var dMinus1 = new Date("November 8, 2014 10:10:00");
                 
                var findWhereThiefIsThief = new Parse.Query("theftdirection");
                findWhereThiefIsThief.equalTo("thiefFBId", thiefFacebookId);
                findWhereThiefIsThief.containedIn("victimFBId", friendFacebookIds);
                findWhereThiefIsThief.greaterThanOrEqualTo("updatedAt", dMinus);
                return findWhereThiefIsThief.find();
               
            }).then(function(currThiefs){
                for(var i = 0; i < currThiefs.length; i++){
                    var fbId = currThiefs[i].get("victimFBId");
                    console.log("WhereThiefIsThief for id " + fbId);
                    finalCheesUpdates[fbId].showMe = false;
            }
               
            console.log("FINAL WRAPPERR IS ...");
            console.log(finalCheesUpdates);
               
            var finalCheesUpdatesList = [];
            for(var key in finalCheesUpdates){
                   
                // check for 0 cheese count, and disabling the image
                var localCheeseCount = finalCheesUpdates[key].cheeseCount;
                if(localCheeseCount < 1){
                    finalCheesUpdates[key].showMe = false;
                }
                if(finalCheesUpdates[key].showMe == null){
                    finalCheesUpdates[key].showMe = true;
                }
                   
               finalCheesUpdatesList.push(finalCheesUpdates[key]);
            }
            console.log(finalCheesUpdatesList);
            promise.resolve(finalCheesUpdatesList);
           
        });
       
     return promise;
}
   

/**
Method used for sending push notification
to the friend from whom the cheese was stolen
**/
var performNotification = function(thiefName, victimFacebookId){ 
        console.log("Running notification...");
        var sampleQuery = new Parse.Query(Parse.Installation); 
        sampleQuery.equalTo('facebookId', victimFacebookId);
        //query.notEqualTo('facebookId', CHEESE_BOT_FB_ID);
        var message =  thiefName  +  ' just snatched your cheese!'
        var promise = new Parse.Promise();
         
        if(victimFacebookId == CHEESE_BOT_FB_ID){
            promise.resolve("Notification sent");
             return promise;    
        }
         
        Parse.Push.send({
            where:sampleQuery,
            data: {
                alert: message
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
       
    query.containedIn("facebookId", facebookIds);
    query.find().then(function(cheeseRows){
                        console.log("Ran cheese query...");
                        findVictimThiefCheeseRows(cheeseRows);
                        victimUserCheese.increment("cheeseCount", -1);
                        return victimUserCheese.save();
                    }).then(function() {
                        console.log("I'm in success");
                        thiefUserCheese.increment("cheeseCount");   
                        return thiefUserCheese.save();
                        },
                        function(error){
                        response.error(error);
                 })
                .then(function(){return performNotification(thiefName,victimFacebookId);})
                .then(function(){return insertTheftHistory(thiefFacebookId,victimFacebookId);})
                .then(function(){return updateTheftDirection(thiefFacebookId,victimFacebookId);})
                .then(function(){return getUserFriendsFacebookIds();})
                .then(function(friendFacebookIds){
                        return getFriendsCheeseCounts(friendFacebookIds, thiefFacebookId);
                  
                }).then(function(allCounts){
                    response.success(allCounts);
                      
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
    console.log("START Passed In USER ...");
    console.log(passedInUser);
    console.log("END Passed In USER ...");
    var fbaccessToken = passedInUser.get('authData').facebook.access_token;
    console.log("fbaccessToken " + fbaccessToken);
    var currentFBUserId = passedInUser.get("facebookId");
       
    var existingUserSteps = function(request, response, isBot) {
        console.log("Inside performExistingUser Steps...");
        var updatedFriendsList = [];
        Parse.Cloud.httpRequest({
               url: 'https://graph.facebook.com/me/friends?access_token=' + fbaccessToken
        }).then(function(httpResponse){
                    console.log("Fb Reponse " + httpResponse.text);
                    var fbResponse = httpResponse['data'].data;
                    console.log(fbResponse);
                    var friendsList = [CHEESE_BOT_FB_ID];
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
                    passedInUser.set("friends", allFriendslist);
                    updatedFriendsList = allFriendslist;
                    return passedInUser.save();
                    
                }).then(function(savedUser){
                        console.log("In final stage");
                        updatedFriendsList.push(passedInUser.get("facebookId"));
                        console.log(updatedFriendsList);
                        var query = new Parse.Query(Parse.User);
                        query.containedIn("facebookId", updatedFriendsList);
                        return query.find();
                        
                }).then(function(allCheeseCountObjects){
                            //for(var i = 0; i<allCheeseCountObjects.length; i++){
                             //   console.log(allCheeseCountObjects[i]);
                            //}
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
    query.equalTo("facebookId", CHEESE_BOT_FB_ID);
    query.find().then(function(botUser){
        allUsersList = botUser[0].get("friends");
        return allUsersList;
    }).then(function(fulllist){
        if(currentFBUserId == CHEESE_BOT_FB_ID){
            console.log("I am BOT..");
            doCommonSteps(true);
        }else {
            var query = new Parse.Query(Parse.User);
            query.equalTo("facebookId", CHEESE_BOT_FB_ID);
            query.find().then(function(botUser){
                console.log("Bot USER IS ...");
                console.log(botUser[0]);
                botUser[0].addUnique("friends", passedInUser.get("facebookId"));
                botUser[0].save(null,{
                    success:function(theftResponse) { 
                        doCommonSteps(false);
                    },
                    error:function(error) {
                        response.error(error);
                    }
                });
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
        .then(function(allCounts){
                response.success(allCounts);
      
            });
});
   
   
   
   
/* beforeSave function for the cheese table to check victim cheese count */
Parse.Cloud.beforeSave("cheese", function(re, response){
    var myCount = re.object.get("cheeseCount");
    console.log("Player cheese count is: " + myCount);
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
  var query = new Parse.Query("cheese");
  query.equalTo("facebookId", CHEESE_BOT_FB_ID);
  query.find().then(function(user){
    mainBotUser = user[0];
    return user[0];
  }).then(function(temp){
        var query = new Parse.Query(Parse.User);
        query.equalTo("facebookId", CHEESE_BOT_FB_ID);
        return query.find();
        }).then(function(botUser){
            var allUsersList = botUser[0].get("friends");
            return getFriendsCheeseCounts(allUsersList, CHEESE_BOT_FB_ID);
        }).then(function(allCounts){
            console.log("Se mee here");
            console.log(allCounts);
            var filteredList = [];
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
                var query = new Parse.Query("cheese");
                    query.equalTo("facebookId", currentObj["facebookId"]);
                    query.find().then(function(friend){
                        friend[0].increment("cheeseCount", -1);
                        return friend[0].save();
                    }).then(function(returnFriend){
                        mainBotUser.increment("cheeseCount", 1);
                        return mainBotUser.save();
                    }).then(function(){
                        return performNotification("CheeseBot", currentObj["facebookId"]);
                    }).then(function(){
                        return insertTheftHistory(CHEESE_BOT_FB_ID,currentObj["facebookId"]);
                    }).then(function(){
                        return updateTheftDirection(CHEESE_BOT_FB_ID,currentObj["facebookId"]);
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