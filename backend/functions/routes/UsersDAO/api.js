const express = require("express");
const router = express.Router();

router.get("/getUser/:userID", (req,res) => {
    let database = req.app.get("database")
    var targetUser = database.ref('User/User' + req.param('userID'));
    targetUser.once("value", function(snapshot){
        res.end(JSON.stringify(snapshot.val()));
    })
})

router.delete("/deleteUser/:userID", (req, res) => {

    let database = req.app.get("database")
    let userToDel = database.ref('User/User' + req.param('userID'))
    userToDel.set({})

    res.end("deleted from the DB!")

})

router.delete("/deleteUser/:userID", (req, res) => {

    let database = req.app.get("database")
    let userToDel = database.ref('User/User-' + req.param('userID'))
    userToDel.set({})

    res.end("deleted from the DB")

})

router.put("/updateUser/:userID", (req, res) => {

    let database = req.app.get("database")
    let userToUpdate = database.ref("User/User" + req.params.userID)

	userToUpdate.once("value", function(snapshot){
            newData = snapshot.val()
			userToUpdate.update(req.body)
        })
	
	res.json(req.body)
})

router.post("/addUser/:newID", (req, res) => { 
    let database = req.app.get("database")
    let userRef = database.ref("User")

	const newReq = userRef.child("User" + req.params.newID)
	req.body.UserID = req.params.newID; 
	newReq.set(req.body);
	res.json(req.body);
	})
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

module.exports = router;
