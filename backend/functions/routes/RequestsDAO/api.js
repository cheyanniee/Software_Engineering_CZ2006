const express = require("express");
const router = express.Router();

router.get("/getReceivedRequests/:user", (req,res)=>{ 
    let database = req.app.get("database")
    var ref = database.ref('Requests');
    var user = req.params.user;
    
    function compare(a, b) {
        if (a.RequestID > b.RequestID) return 1;
        if (b.RequestID > a.RequestID) return -1;
      
        return 0;
      }
    
    ref.once("value", function(snapshot){
        var requests = snapshot.val();  
        var requestsArray = [];
        for (var i in requests){
			if (requests[i].Host==user){
				requestsArray.push(requests[i]);
			}
        }
        requestsArray.sort(compare);
        res.json(requestsArray); 
    })
})

router.get("/getPendingRequests/:user", (req,res)=>{ 
    let database = req.app.get("database")
    var ref = database.ref('Requests');
    var user = req.params.user;
    
    function compare(a, b) {
        if (a.RequestID > b.RequestID) return 1;
        if (b.RequestID > a.RequestID) return -1;
      
        return 0;
      }
    
    ref.once("value", function(snapshot){
        var requests = snapshot.val();  
        var requestsArray = [];
        for (var i in requests){
			if (requests[i].Participant==user){
				requestsArray.push(requests[i]);
			}
        }
        requestsArray.sort(compare);
        res.json(requestsArray);
    })
})

router.post("/sendRequest", (req, res) => {
    let database = req.app.get("database")
	let ref = database.ref("Requests")
	
	function compare(a, b) {
        if (a.RequestID > b.RequestID) return 1;
        if (b.RequestID > a.RequestID) return -1;
      
        return 0;
      }

    ref.once("value", function(snapshot){
        var requests = snapshot.val(); 
        var newID = 0;
        var requestsArray = [];
        for (var i in requests){
            requestsArray.push(requests[i]);
		}
		requestsArray.sort(compare);
        newID = requestsArray[requestsArray.length-1].RequestID + 1;
        const newReq = ref.child("Request" + newID) 
        req.body.RequestID = newID; 
        newReq.set(req.body);
		res.json(req.body)
    })
    
})

router.delete("/deleteRequest/:RequestID", (req, res) => {

    let database = req.app.get("database")
    let reqToDel = database.ref('Requests/Request' + req.param('RequestID'))
    reqToDel.set({})

    res.end("Request deleted.")

})

router.delete("/deleteUserRequests/:user", (req, res) => {

    let database = req.app.get("database")
    let ref = database.ref("Requests")
    var user = req.params.user

    ref.once("value", function(snapshot){
        var requests = snapshot.val()
        var requestsArray = []
        for (var i in requests){
            if (requests[i].Host==user|requests[i].Participant==user){
                requestsArray.push(requests[i].RequestID);
            }
		}
        for (var j in requestsArray){
            let reqToDel = database.ref('Requests/Request' + requestsArray[j])
    		reqToDel.set({})
		}
    })

	res.end("Requests deleted.")

})

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

module.exports = router;
