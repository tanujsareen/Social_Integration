package in.srntech.social_integration;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/** Created By Tanuj Sareen */
public class BaseActivity extends AppCompatActivity implements OnClickListener {

    AppCompatButton txtFacebook, txtGooglePlus, txt_Logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GenerateFaceBookAPI();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        (txtFacebook = (AppCompatButton) findViewById(R.id.txtFacebook)).setOnClickListener(this);
        (txtGooglePlus = (AppCompatButton) findViewById(R.id.txtGooglePlus)).setOnClickListener(this);
        (txt_Logout = (AppCompatButton) findViewById(R.id.txt_Logout)).setOnClickListener(this);
        generateHashKey();
    }

    public void generateHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("in.srntech.social_integration", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    CallbackManager mCallbackManager;

    /**
     * Initializing Facebook SDK :)
     */
    private void GenerateFaceBookAPI() {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.i("Success", "Login" + "::" + loginResult.getAccessToken());
                        Toast.makeText(BaseActivity.this, "AccessToken: " + loginResult.getAccessToken().getToken(), Toast.LENGTH_SHORT).show();
                        GetFacebookLogin(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(BaseActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(BaseActivity.this, "Error:" + exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }


    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    /** Get User Details**/
    private void GetFacebookLogin(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        Log.v("BaseActivity", response.toString());
                        if (object != null) {
                            Log.d("JSONObject", "::" + object.toString());
                            Toast.makeText(BaseActivity.this, "" + object.toString(), Toast.LENGTH_SHORT).show();
                            /**Chaaaakkkkkk De Fattttttttttey  ...... Burraaaaa !!!!!!!!!!!! **/
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender, birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }


    /** Generate FaceBook Sign In */
    public void generateFacebookSignIn() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email", "user_birthday", "user_photos"));
    }


    /**
     * Bind the results to the facebook callManager
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtFacebook:
                generateFacebookSignIn();
                break;

            case R.id.txt_Logout:
                /** Simply logout the session**/
                LoginManager.getInstance().logOut();
                Toast.makeText(BaseActivity.this, "SuccessFully Logout !!", Toast.LENGTH_LONG).show();
                break;

        }

    }


}

