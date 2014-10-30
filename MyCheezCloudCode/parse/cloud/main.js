Parse.Cloud.define("onCheeseTheft", function(request, response)
{
	console.log(request);
	var thiefFacebookId = request.params.thiefFacebookId;
	var victimFacebookId = request.params.victimFacebookId;
	
	console.log(thiefFacebookId + " is stealing cheese from " + victimFacebookId);
	var query = new Parse.Query("cheese");
	var facebookIds = [thiefFacebookId, victimFacebookId];
	query.containedIn("facebookId", facebookIds);
	query.find(
	{
		success: function(cheeseRows)
		{
			console.log("cheese query response is: " + cheeseRows);
			
			var thiefUserCheese;
			var victimUserCheese;
			
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
			
			thiefUserCheese.increment("cheeseCount");	
			thiefUserCheese.save();
			victimUserCheese.increment("cheeseCount", -1);			
			victimUserCheese.save();
			updateCheeseSuccess();
			
			
			
			/* insert row into theft history table */

			/*var TheftHistoryClass = Parse.Object.extend("thefthistory");
			var theftHistory = new TheftHistoryClass();
			theftHistory.set("thiefFBId", thiefFacebookId);
			theftHistory.set("victimFBId", victimFacebookId);
			
			console.log("adding theft history for thief " + thiefFacebookId + " and victim " + victimFacebookId);
			
			theftHistory.save(null,{
			  success:function(theftResponse) { 
			    console.log(theftResponse);
				response.success(theftResponse);
			  },
			  error:function(error) {
				response.error(error);
			  }
			});*/
			
		},
		error: function(error)
		{
			console.log(error);
		}
	});
	
	var updateCheeseSuccess = function()
	{
		console.log("I'm in updateCheeseSuccess!");
		insertTheftHistory(response);
	}
	
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
			getLatestCheeseCounts(theftResponse);
			//response.success(theftResponse);
		  },
		  error:function(error) {
			response.error(error);
		  }
		});
	}
	
	var getLatestCheeseCounts = function(response)
	{
		
		var query = new Parse.Query(Parse.User);
		query.equalTo("facebookId", thiefFacebookId);
		query.find({
			success: function(user)
			{
				console.log("I'm in getlatest");
				console.log(user);
				var friendFacebookIds = user[0].get("friends");
				console.log("User has friends: " + friendFacebookIds);
				friendFacebookIds.push(thiefFacebookId);
				getFriendsCheeseCounts(friendFacebookIds);
			},
			error: function()
			{
				response.error("Reservation could not be found");
			}	
		});
	};
	
	var getFriendsCheeseCounts = function(friendFacebookIds)
	{
		var query = new Parse.Query("cheese");
		console.log(friendFacebookIds);
		query.containedIn("facebookId", friendFacebookIds);
		query.find({
			success: function(usersFriends)
			{
				console.log(usersFriends);
				response.success(usersFriends);
			},
			error: function()
			{
				response.error("Cannot get user friends cheese count");
			}	
		});
	}
	
});
