
/* cloud code to steal cheese */
Parse.Cloud.define("onCheeseTheft", function(request, response)
{
	console.log(request);
	var thiefFacebookId = request.params.thiefFacebookId;
	var victimFacebookId = request.params.victimFacebookId;
	var thiefUserCheese;
	var victimUserCheese;
	console.log(thiefFacebookId + " is stealing cheese from " + victimFacebookId);
	var query = new Parse.Query("cheese");
	var facebookIds = [thiefFacebookId, victimFacebookId];
	query.containedIn("facebookId", facebookIds);
	query.find().then(
					function(cheeseRows)
					{
						console.log("cheese query response is: " + cheeseRows);
						findVictimThiefCheeseRows(cheeseRows);
						victimUserCheese.increment("cheeseCount", -1);
						return victimUserCheese.save();
					})
				.then(
					function()
					{
						console.log("I'm in success");
						thiefUserCheese.increment("cheeseCount");	
						return thiefUserCheese.save();
					},
					function(error)
					{
						response.error(error);
					})
				.then(function(){insertTheftHistory();})
				.then(function(){return getUserFriendsFacebookIds();})
				.then(function(friendFacebookIds){getFriendsCheeseCounts(friendFacebookIds);});				
	
	/* find and set victim and thief cheese rows */
	var findVictimThiefCheeseRows = function(cheeseRows)
	{					
		for(var ii=0; ii< cheeseRows.length; ii++)
		{
			var userFacebookId = cheeseRows[ii].get("facebookId");
			var cheeseCount = cheeseRows[ii].get("cheeseCount");
			
			console.log("user " + userFacebookId + " has cheese: " + cheeseCount);
			
			if (userFacebookId === thiefFacebookId)
			{
				thiefUserCheese = cheeseRows[ii];
			}
			else
			{
				victimUserCheese = cheeseRows[ii];
			}
		}
	}	
	
	/* add theft record to thefthistory table */
	var insertTheftHistory = function(response)
	{
		var TheftHistoryClass = Parse.Object.extend("thefthistory");
		var theftHistory = new TheftHistoryClass();
		theftHistory.set("thiefFBId", thiefFacebookId);
		theftHistory.set("victimFBId", victimFacebookId);
		
		console.log("adding theft history for thief " + thiefFacebookId + " and victim " + victimFacebookId);
		
		theftHistory.save(null,{
		  success:function(theftResponse) { 
			console.log(theftResponse);
		  },
		  error:function(error) {
			response.error(error);
		  }
		});
	}
	
	/* return all facebook ids of user's friends */
	var getUserFriendsFacebookIds = function()
	{
		var query = new Parse.Query(Parse.User);
		query.equalTo("facebookId", thiefFacebookId);
		var results = 
			query.find().then(
				function(user)
				{
					console.log(user);
					var friendFacebookIds = user[0].get("friends");
					console.log("User has friends: " + friendFacebookIds);
					friendFacebookIds.push(thiefFacebookId);
					return friendFacebookIds;
				},
				function(error)
				{
					console.log("Cannot find user friends facebookIds");
					response.error("Cannot find user friends facebookIds");
				}	
			
			);
		console.log("getUserFriendsFacebookIds results:  " + results);
		return results;
	};
	
	/* retrieve cheese counts of user's friends */
	var getFriendsCheeseCounts = function(friendFacebookIds)
	{
		var query = new Parse.Query("cheese");
		console.log("getFriendsCheeseCounts received friendsFacebookIds: " + friendFacebookIds);
		query.containedIn("facebookId", friendFacebookIds);
		query.find({
			success: function(usersFriends)
			{
				console.log("user friends are: " + usersFriends);
				response.success(usersFriends);
			},
			error: function()
			{
				response.error("Cannot get user friends cheese count");
			}	
		});
	}
	
});

 



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
    
    var existingUserSteps = function(request, response) {
    	console.log("Inside performExistingUser Steps...");
    	console.log(fbaccessToken);
    	var updatedFriendsList = [];
    	Parse.Cloud.httpRequest({
               url: 'https://graph.facebook.com/me/friends?access_token=' + fbaccessToken
    	}).then(function(httpResponse){
                    console.log("Fb Reponse " + httpResponse.text);
                    var fbResponse = httpResponse['data'].data;
                    console.log(fbResponse);
                    var friendsList = [];
                    for(var i = 0; i < fbResponse.length; i++) {
                        var fbId = fbResponse[i].id;
                        friendsList.push(fbId);
                    }
                     return friendsList;
                         
                    }, function(errorResponse){
                        // Throw error
                        console.error('Request failed with response code ' + errorResponse.status);
                 
                }).then(function(allFriendslist){
                    console.log("in updating user friends list");
                    console.log(passedInUser);
                    passedInUser.set("friends", allFriendslist);
                    updatedFriendsList = allFriendslist;
                    return passedInUser.save();
                 
                }).then(function(savedUser){
                        console.log("In final stage");
                        updatedFriendsList.push(passedInUser.get("facebookId"));
                        console.log(updatedFriendsList);
                       // var query = new Parse.Query("cheese");
                        var query = new Parse.Query(Parse.User);
                        query.containedIn("facebookId", updatedFriendsList);
                        return query.find();
                     
                }).then(function(allCheeseCountObjects){
                            for(var i = 0; i<allCheeseCountObjects.length; i++){
                                console.log(allCheeseCountObjects[i]);
                            }
                            response.success(allCheeseCountObjects);
                        },function(errorHandler){
                            response.error("Not able to complete this operation");
                        }
                );
	}   
     
 
	if(isNewUser) {
       console.log("Inside NEW USER BLOCK...");
       var CheeseCountClass = Parse.Object.extend("cheese");
       var cheeseCount = new CheeseCountClass();
       cheeseCount.set("facebookId", passedInUser.get("facebookId"));
       cheeseCount.set("cheeseCount", 20);
       cheeseCount.save().then(function(cheeseCount){
                        return existingUserSteps(request, response);
                });
    } else {
    
        console.log("Inside else part...");
        return existingUserSteps(request, response);
    }
 
});


Parse.Cloud.define("getAllCheeseCounts", function(request, response) {
	console.log("In getAllCheeseCounts...");
	console.log(request);
	var friendsList = request.user.get("friends");
	var query = new Parse.Query("cheese");
	friendsList.push(request.user.get("facebookId"));
	console.log("getFriendsCheeseCounts received friendsFacebookIds: " + friendsList);
	query.containedIn("facebookId", friendsList);
	query.find({
			success: function(cheeseCounts)
			{
				console.log(cheeseCounts);
				response.success(cheeseCounts);
			},
			error: function()
			{
				response.error("Cannot get user friends cheese count");
			}	
		}); 
	


});


Parse.Cloud.define("testCount", function(request, response) {
	Parse.Cloud.useMasterKey();
	var query = new Parse.Query("cheese");
	query.equalTo("facebookId", "1517055471875244");
	
	
	var query = new Parse.Query("cheese");
	query.equalTo("facebookId", "1517055471875244");
		query.find({
			success: function(cheeses)
			{
				console.log(cheeses[0]);
				var currCheese = cheeses[0];
				currCheese.increment("cheeseCount", -1);
				currCheese.save().then(function(result){
								console.log("I am in success...");
				
						}, function(error){
								console.log("I am in error...");
						});
				
			},
			error: function()
			{
				response.error("Cannot get user friends cheese count");
			}	
		});
	
	
	

});

/* beforeSave function for the cheese table to check victim cheese count */
Parse.Cloud.beforeSave("cheese", function(re, response){
	var myCount = re.object.get("cheeseCount");
	console.log("Player cheese count is: " + myCount);
	if(myCount >= 0)
	{
		console.log("cheese table beforeSave success");
		response.success();
	}
	else 
	{
		console.log("cheese table beforeSave error");
		response.error("Victim has no cheese! Cannot steal from victim");
	}
});


