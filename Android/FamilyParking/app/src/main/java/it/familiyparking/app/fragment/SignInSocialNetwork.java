package it.familiyparking.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.SocialPerson;
import com.androidsocialnetworks.lib.impl.FacebookSocialNetwork;
import com.androidsocialnetworks.lib.impl.GooglePlusSocialNetwork;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;

import it.familiyparking.app.R;
import it.familiyparking.app.utility.Code;

/**
 * Created by francesco on 12/04/15.
 */
public class SignInSocialNetwork extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener, View.OnClickListener{

    private SocialNetworkManager socialManager;

    protected boolean socialManagerInitialized = false;

    protected Button facebookButton;
    protected Button googlePlusButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_socialnetworks, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        socialManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(Code.SOCIAL);

        if (socialManager == null) {
            socialManager = SocialNetworkManager.Builder.from(getActivity())
                    .facebook()
                    .googlePlus()
                    .build();
            getFragmentManager().beginTransaction().add(socialManager,Code.SOCIAL).commit();

            socialManager.setOnInitializationCompleteListener(this);
        }
        else {
            socialManagerInitialized = true;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        facebookButton = (Button) view.findViewById(R.id.facebook_button);
        googlePlusButton = (Button) view.findViewById(R.id.google_plus_button);

        facebookButton.setOnClickListener(this);
        googlePlusButton.setOnClickListener(this);

        if (socialManagerInitialized) {
            onSocialNetworkManagerInitialized();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        onRequestCancel();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.facebook_button:
                onFacebookAction();
                break;
            case R.id.google_plus_button:
                onGooglePlusAction();
                break;
            default:
                throw new IllegalArgumentException("Can't find click handler for: " + v);
        }
    }

    @Override
    public void onSocialNetworkManagerInitialized() {}

    protected boolean checkIsLoginned(int socialNetworkID) {
        if (socialManager.getSocialNetwork(socialNetworkID).isConnected()) {
            return true;
        }

        return false;
    }

    public void onRequestCancel() {
        for (SocialNetwork socialNetwork : socialManager.getInitializedSocialNetworks()) {
            socialNetwork.cancelAll();
        }
    }

    private void onFacebookAction(){
        if (!checkIsLoginned(FacebookSocialNetwork.ID)){
            socialManager.getFacebookSocialNetwork().requestLogin(new OnLoginCompleteListener() {
                @Override
                public void onLoginSuccess(int i) {
                    socialManager.getFacebookSocialNetwork().requestCurrentPerson(new requestSocialPersonCompleteListener());
                }

                @Override
                public void onError(int i, String s, String s1, Object o) {

                }
            });
        }

        socialManager.getFacebookSocialNetwork().requestCurrentPerson(new requestSocialPersonCompleteListener());
    }

    private void onGooglePlusAction(){
        if (!checkIsLoginned(GooglePlusSocialNetwork.ID)){
            socialManager.getGooglePlusSocialNetwork().requestLogin(new OnLoginCompleteListener() {
                @Override
                public void onLoginSuccess(int i) {
                    socialManager.getGooglePlusSocialNetwork().requestCurrentPerson(new requestSocialPersonCompleteListener());
                }

                @Override
                public void onError(int i, String s, String s1, Object o) {

                }
            });
        }

        socialManager.getGooglePlusSocialNetwork().requestCurrentPerson(new requestSocialPersonCompleteListener());
    }

    private class requestSocialPersonCompleteListener implements OnRequestSocialPersonCompleteListener {
        @Override
        public void onRequestSocialPersonSuccess(int socialNetworkID, SocialPerson socialPerson) {
            Log.e("Profile",socialPerson.toString());
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            Log.e("Error",errorMessage);
        }
    }

}