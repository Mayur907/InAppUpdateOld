# InAppUpdateLibrary
[![](https://jitpack.io/v/Mayur907/InAppUpdateOld.svg)](https://jitpack.io/#Mayur907/InAppUpdateOld)

>The simplest InAppUpdate library, set IMMEDIATE or FLEXIBLE in a few steps!
 

## How To Use
### Install
from JitPack:

Add it in your root build.gradle at the end of repositories
```
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}

```
Then, add the library to your module `build.gradle`
Add the dependency
```
dependencies {
	       implementation 'com.github.Mayur907:InAppUpdateOld:1.1.1'
	}
```

### In Your Code
```
/* start InAppUpadateActivity to check if an update is available or not in your splash screen on create() Method*/
        createTimer();
	
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
get a result like this
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
```

### In Your Manifest.xml
```
<activity android:name="inappupdate.updateimmediate.updateflexible.InAppUpdate"
            android:theme="@style/Theme.AppCompat.Translucent" />
```	

### In Your theme.xml
```
<!--add below code in themes.xml file-->
    <!--below is the style for transparent activity and here we are using no action bar.-->
    <style name="Theme.AppCompat.Translucent" parent="Theme.AppCompat.NoActionBar">
        <!--on below line we are setting background as transparent color-->
        <item name="android:background">@android:color/transparent</item>
        <!--on below line we are displaying the windowNotitle as true as we are not displaying our status bar-->
        <item name="android:windowNoTitle">true</item>
        <!--on below line we are setting our window background as transparent color-->
        <item name="android:windowBackground">@android:color/transparent</item>
        <!--on below line we are setting color background cache hint as null-->
        <item name="android:colorBackgroundCacheHint">@null</item>
        <!--on below line we are adding a window translucent as true-->
        <item name="android:windowIsTranslucent">true</item>
        <!--on below line we are adding a window animationstyle-->
        <item name="android:windowAnimationStyle">@null</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@android:color/transparent</item>
    </style>


<!-- If your app minimum API version is below 26 so add theme in value-v26 and update this line: "The problem causing crashes is that API 26 doesn't support windowIsTranslucent."-->
	<item name="android:windowIsTranslucent">false</item>

```

## About Me
Follow me at [Mayur907](https://github.com/Mayur907).
