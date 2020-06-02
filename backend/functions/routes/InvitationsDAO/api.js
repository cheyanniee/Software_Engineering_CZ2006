const express = require("express");
const router = express.Router();

router.get("/getInvitation/:invitationID", (req,res) => {
    let database = req.app.get("database")
    var ref = database.ref('Invitations/Invitation' + req.param('invitationID'));
    ref.once("value", function(snapshot){
        res.end(JSON.stringify(snapshot.val()));
    })
})

router.get("/getInvitations/:category/:user", (req,res)=>{ 
  
    let database = req.app.get("database")
    var ref = database.ref('Invitations');
    var category = req.params.category;
    var user = req.params.user;

    function compare(a, b) {
        if (a.InvitationID > b.InvitationID) return 1;
        if (b.InvitationID > a.InvitationID) return -1;
      
        return 0;
      }
    
    ref.once("value", function(snapshot){
        var invitations = snapshot.val();
        var invitationsArray = [];
        if (category=="All"){
            for (var i in invitations){
                if ((invitations[i].Host!=user)&invitations[i].Category!="test"){
                    invitationsArray.push(invitations[i]);
                }
            }
        }
        else{
            for (var i in invitations){
                if ((invitations[i].Category==(category))&(invitations[i].Host!=user)){
                    invitationsArray.push(invitations[i]);
                }
            }
        }
        invitationsArray.sort(compare);
        res.json(invitationsArray); 
    })
})

router.get("/getUserInvitations/:user", (req,res)=>{ 
   
    let database = req.app.get("database")
    var ref = database.ref('Invitations');
    var user = req.params.user;

    function compare(a, b) {
        if (a.InvitationID > b.InvitationID) return 1;
        if (b.InvitationID > a.InvitationID) return -1;
      
        return 0;
      }
    
    ref.once("value", function(snapshot){
        var invitations = snapshot.val();
        var invitationsArray = [];
        for (var i in invitations){
            if ((invitations[i].Host==(user))){
                invitationsArray.push(invitations[i]);
            }
        }
        invitationsArray.sort(compare);
        res.json(invitationsArray); 
    })
})

router.post("/addInvitation", (req, res) => { 
   
    let database = req.app.get("database")
    let ref = database.ref("Invitations")

    function compare(a, b) {
        if (a.InvitationID > b.InvitationID) return 1;
        if (b.InvitationID > a.InvitationID) return -1;
      
        return 0;
      }

    ref.once("value", function(snapshot){
        var invitations = snapshot.val();
        var newID = 0;
        var invitationsArray = [];
        for (var i in invitations){
            invitationsArray.push(invitations[i]);
        }
        invitationsArray.sort(compare);
        newID = invitationsArray[invitationsArray.length-1].InvitationID + 1;
        const newInvitation = ref.child("Invitation" + newID) 
        req.body.InvitationID = newID; 
        newInvitation.set(req.body)

        res.json(req.body); 
    })

})

router.delete("/deleteInvitation/:InvitationID", (req, res) => {

    let database = req.app.get("database")
    let invToDel = database.ref('Invitations/Invitation' + req.param('InvitationID'))
    invToDel.set({})

    res.end("Invitation deleted.")

})

router.delete("/deleteUserInvitations/:user", (req, res) => {

    let database = req.app.get("database")
    let ref = database.ref("Invitations")
    var user = req.params.user

    ref.once("value", function(snapshot){
        var invitations = snapshot.val()
        var invitationsArray = []
        for (var i in invitations){
            if (invitations[i].Host==user){
                invitationsArray.push(invitations[i].InvitationID);
            }
            
        }
        for (var j in invitationsArray){
            let invToDel = database.ref('Invitations/Invitation' + invitationsArray[j])
            invToDel.set({})
        }
    })

    res.end("Invitations deleted.")

})

router.put("/updateInvitation/:invitationID", (req, res) => {

    let database = req.app.get("database")
    var invitationToUpdate = database.ref('Invitations/Invitation' + req.param('invitationID'));

	invitationToUpdate.once("value", function(snapshot){
            newData = snapshot.val()
			invitationToUpdate.update(req.body)
        })
	res.json(req.body)
})

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

module.exports = router;