const functions = require('firebase-functions')
const admin = require('firebase-admin')
const express = require('express')

const apirouteInvitations = require('./routes/InvitationsDAO/api')
const apirouteRequests = require('./routes/RequestsDAO/api')
const apirouteMessages = require('./routes/MessagesDAO/api')
const apirouteUsers = require('./routes/UsersDAO/api')
const apirouteNotifications = require('./routes/NotificationsAPI/api')

const app = express();
app.use(require('cors')({ origin: true, credentials: true }))
app.use(express.json())
app.use(express.urlencoded({ extended: false }))

// var serviceAccount = require("../ignore/lonely-4a186-firebase-adminsdk-xiwbf-eb81a9c382.json")
admin.initializeApp({
    // credential: admin.credential.cert(serviceAccount),
    credential: admin.credential.applicationDefault(),
    databaseURL: "https://lonely-4a186.firebaseio.com/"
});

var db = admin.database();
app.set('database',db)
app.use('/InvitationsDAO', apirouteInvitations) 
app.use('/RequestsDAO', apirouteRequests) 
app.use('/MessagesDAO', apirouteMessages)
app.use('/UsersDAO', apirouteUsers)
app.use('/NotificationsAPI', apirouteNotifications)
exports.app = functions.https.onRequest(app)


// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });