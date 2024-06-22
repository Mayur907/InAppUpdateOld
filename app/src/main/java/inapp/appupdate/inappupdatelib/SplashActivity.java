package inapp.appupdate.inappupdatelib;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;

import inappupdate.updateimmediate.updateflexible.InAppUpdate;
import inappupdate.updateimmediate.updateflexible.Type;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int EARTH_RC_APP_UPDATE = 123;
    private static final long COUNTER_TIME = 4;
    private AppUpdateManager appUpdateManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createTimer();
    }

    private void createTimer() {

        CountDownTimer countDownTimer = new CountDownTimer(COUNTER_TIME * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                startActivity();
            }
        };
        countDownTimer.start();
    }

    public void startActivity() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        checkUpdate();
    }

    private void checkUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    startInAppUpdateActivity();
                } else {
                    startMainActivity();
                }
            }
        });

        appUpdateInfoTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                startMainActivity();
            }
        });
    }

    private void startInAppUpdateActivity() {
        Intent intent = new Intent(this, InAppUpdate.class);
        intent.putExtra("key", Type.FLEXIBLE); /* Type.IMMEDIATE, Type.FLEXIBLE */
        startActivityForResult(intent, EARTH_RC_APP_UPDATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EARTH_RC_APP_UPDATE) {
            if (resultCode == RESULT_OK) {
                startActivity();
            } else /*resultCode == RESULT_CANCELED*/ {
                if (data != null) {
                    /*check return from immediate or flexible
                     * if return from immediate so app close */
                    if(data.getIntExtra("from", 0) == 123456){
                        finish();
                    } else {
                        startMainActivity();
                    }
                } else {
                    startMainActivity();
                }
            }
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
