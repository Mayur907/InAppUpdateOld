//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package inappupdate.updateimmediate.updateflexible;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

public class InAppUpdate extends AppCompatActivity {
    private static final int RC_APP_UPDATE_IMMEDIATE = 123456;
    private static final int RC_APP_UPDATE_FLEXIBLE = 654321;
    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;
    private Type type;

    public InAppUpdate() {
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = this.getIntent().getExtras();
        if (extras != null && extras.containsKey("key")) {
            this.type = (Type)extras.getSerializable("key");
        }

        this.appUpdateManager = AppUpdateManagerFactory.create(this);
        this.checkType(this.type);
    }

    private void checkType(Type type) {
        if (type == Type.IMMEDIATE) {
            this.immediateUpdate();
        } else {
            this.flexibleUpdate();
        }

    }

    public void flexibleUpdate() {
        this.installStateUpdatedListener = (state) -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                this.popupSnackBarForCompleteUpdate();
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                this.removeInstallStateUpdateListener();
            } else {
                Toast.makeText(this.getApplicationContext(), "InstallStateUpdatedListener: state: " + state.installStatus(), Toast.LENGTH_LONG).show();
                this.onBackPressed();
            }

        };
        this.appUpdateManager.registerListener(this.installStateUpdatedListener);
        Task<AppUpdateInfo> appUpdateInfoTask = this.appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener((appUpdateInfo) -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                this.startUpdateFlowFlexible(appUpdateInfo);
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                this.popupSnackBarForCompleteUpdate();
            } else {
                Intent intent = new Intent();
                this.setResult(0, intent);
                this.finish();
            }

        });
        appUpdateInfoTask.addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Intent intent = new Intent();
                InAppUpdate.this.setResult(0, intent);
                InAppUpdate.this.finish();
            }
        });
    }

    private void startUpdateFlowFlexible(AppUpdateInfo appUpdateInfo) {
        try {
            this.appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, 654321);
        } catch (IntentSender.SendIntentException var3) {
            var3.printStackTrace();
        }

    }

    private void popupSnackBarForCompleteUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert!");
        builder.setMessage("New app is ready, Do you want to install?");
        builder.setCancelable(false);
        builder.setPositiveButton("Install", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (InAppUpdate.this.appUpdateManager != null) {
                    InAppUpdate.this.appUpdateManager.completeUpdate();
                }

            }
        });
        builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                InAppUpdate.this.finish();
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeInstallStateUpdateListener() {
        if (this.appUpdateManager != null) {
            this.appUpdateManager.unregisterListener(this.installStateUpdatedListener);
        }

    }

    protected void onStop() {
        super.onStop();
        if (this.type == Type.FLEXIBLE) {
            this.removeInstallStateUpdateListener();
        }

    }

    public void onBackPressed() {
        Intent intent;
        if (this.type == Type.FLEXIBLE) {
            intent = new Intent();
            this.setResult(0, intent);
            this.finish();
        } else {
            intent = new Intent();
            intent.putExtra("from", 123456);
            this.setResult(0, intent);
            this.finish();
        }

    }

    public void immediateUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = this.appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener((appUpdateInfo) -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                this.startUpdateFlowImmediate(appUpdateInfo);
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                this.startUpdateFlowImmediate(appUpdateInfo);
            } else {
                Intent intent = new Intent();
                this.setResult(0, intent);
                this.finish();
            }

        });
        appUpdateInfoTask.addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Intent intent = new Intent();
                InAppUpdate.this.setResult(0, intent);
                InAppUpdate.this.finish();
            }
        });
    }

    private void startUpdateFlowImmediate(AppUpdateInfo appUpdateInfo) {
        try {
            this.appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, 123456);
        } catch (IntentSender.SendIntentException var3) {
            var3.printStackTrace();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123456 || requestCode == 654321) {
            Intent intent;
            if (resultCode == 0) {
                Toast.makeText(this.getApplicationContext(), "Update canceled by user! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
                intent = new Intent();
                intent.putExtra("from", requestCode);
                this.setResult(0, intent);
                this.finish();
            } else if (resultCode == -1) {
                Toast.makeText(this.getApplicationContext(), "Update success! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
                intent = new Intent();
                this.setResult(-1, intent);
                this.finish();
            } else {
                Toast.makeText(this.getApplicationContext(), "Update Failed! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
                this.checkType(this.type);
            }
        }

    }
}
