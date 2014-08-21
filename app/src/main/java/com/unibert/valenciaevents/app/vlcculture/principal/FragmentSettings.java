package com.unibert.valenciaevents.app.vlcculture.principal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.unibert.valenciaevents.app.adaptadores.ProfilePictureView;
import com.unibert.valenciaevents.app.vlcculture.R;

import java.util.Arrays;
import java.util.List;

public class FragmentSettings extends Fragment {

    private static final String TAG = FragmentSettings.class.getSimpleName();

    private UiLifecycleHelper uiHelper;
    private GraphUser user;
    private ProfilePictureView profilePictureView;
    private TextView userName;
    private LoginButton authButton;

    private final List<String> permissions;

    public FragmentSettings() {
        permissions = Arrays.asList("user_status");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this.getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        userName = (TextView) view.findViewById(R.id.nombreFB);

        profilePictureView = (ProfilePictureView) view.findViewById(R.id.profilePicture);

        authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                FragmentSettings.this.user = user;
                updateUI();
                // It's possible that we were waiting for this.user to be populated in order to post a
                // status update.
            }
        });

        authButton.setFragment(this);
//        authButton.setReadPermissions(permissions);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();

        updateUI();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        updateUI();
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
        @Override
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
        }

        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            Log.d("HelloFacebook", "Success!");
        }
    };

    private void updateUI() {
        Session session = Session.getActiveSession();
        boolean enableButtons = (session != null && session.isOpened());

        if (enableButtons && user != null) {
            profilePictureView.setProfileId(user.getId());
            userName.setText(user.getName());
        } else {
            profilePictureView.setProfileId(null);
            userName.setText(null);
        }
    }
}