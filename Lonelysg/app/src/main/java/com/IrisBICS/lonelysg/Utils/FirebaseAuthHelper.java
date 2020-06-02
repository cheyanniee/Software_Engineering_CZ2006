package com.IrisBICS.lonelysg.Utils;


import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthHelper {

//    public static String getCurrentUser() {
//        return currentUser.getEmail();
//    }

    public static String getCurrentUserID() {
        FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        return currentUser.getUid();
    }

//    public interface DataStatus{
//        void DataIsLoaded(List<Message> messages, List<String> keys);
//        void DataIsInserted();
//        public void readMessage(final DataStatus dataStatus){
//            mReferenceMessages.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    messages.clear();
//                    List<String> keys = new ArrayList<>();
//                    for (DataSnapshot keyNode : dataSnapshot.getChildren()){
//                        keys.add(keyNode.getKey());
//                        Message message = keyNode.getValue(Message.class);
//                        messages.add(message);
//                    }
//                    dataStatus.DataIsLoaded(messages, keys);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                }
//            });
//        }
//
//        public void sendMessage(Message message, final DataStatus dataStatus){
//            String key = mReferenceMessages.push().getKey();
//            mReferenceMessages.child(key).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    dataStatus.DataIsInserted();
//
//                }
//            });
//        }   void DataIsUpdated();
//        void DataIsDeleted();
//    }



}


