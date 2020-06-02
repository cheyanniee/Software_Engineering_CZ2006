package com.IrisBICS.lonelysg.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.IrisBICS.lonelysg.Fragments.FragmentAccount;
import com.IrisBICS.lonelysg.Fragments.FragmentActivities;
import com.IrisBICS.lonelysg.Fragments.FragmentDiscoveryPage;
import com.IrisBICS.lonelysg.Fragments.FragmentInvitations;
import com.IrisBICS.lonelysg.R;
import com.IrisBICS.lonelysg.Utils.FirebaseAuthHelper;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.messaging.RemoteMessage;
import com.pusher.pushnotifications.PushNotificationReceivedListener;
import com.pusher.pushnotifications.PushNotifications;

public class ActivityNavigationBar extends AppCompatActivity implements MeowBottomNavigation.ShowListener, MeowBottomNavigation.ClickListener {
    MeowBottomNavigation meo;
    private final static int ID_DISCOVERY = 1;
    private final static int ID_INVITATION = 2;
    private final static int ID_CHAT = 3;
    private final static int ID_ACCOUNT = 4;
    String currentUser = FirebaseAuthHelper.getCurrentUserID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_bar);

        meo = findViewById(R.id.bottom_nav);
        meo.add(new MeowBottomNavigation.Model(1, R.drawable.search_black));
        meo.add(new MeowBottomNavigation.Model(2, R.drawable.invitation_black));
        meo.add(new MeowBottomNavigation.Model(3, R.drawable.chat_black));
        meo.add(new MeowBottomNavigation.Model(4, R.drawable.account_circle));

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentDiscoveryPage()).commit();

        meo.setOnClickMenuListener(this);
        meo.setOnShowListener(this);

        PushNotifications.start(getApplicationContext(), "211e38a9-4bc8-40c5-958a-4a7f9aa91547");
        PushNotifications.addDeviceInterest(currentUser);
        PushNotifications.setOnMessageReceivedListenerForVisibleActivity(this, new PushNotificationReceivedListener() {
            @Override
            public void onMessageReceived(RemoteMessage remoteMessage) {
                String messagePayload = remoteMessage.getData().get("inAppNotificationMessage");
                if (messagePayload == null) {
                    // Message payload was not set for this notification
                    Log.i("MyActivity", "Payload was missing");
                } else {
                    Log.i("MyActivity", messagePayload);
                    Toast.makeText(ActivityNavigationBar.this, "You received a request", Toast.LENGTH_SHORT).show();
                    // Now update the UI based on your message payload!
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onShowItem(MeowBottomNavigation.Model item) {
        Fragment select_fragment = null;
        switch(item.getId()){
            case ID_ACCOUNT:
                select_fragment = new FragmentAccount();
                break;
            case ID_CHAT:
                select_fragment = new FragmentActivities();
                break;
            case ID_DISCOVERY:
                select_fragment = new FragmentDiscoveryPage();
                break;
            case ID_INVITATION:
                select_fragment = new FragmentInvitations();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, select_fragment).commit();
    }

    @Override
    public void onClickItem(MeowBottomNavigation.Model item) {
    }



}
