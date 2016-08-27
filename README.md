#oPermission [![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

**oPermission** is library to reduce the developer's efforts implementing android run-time permissions boilerplate. It 
is very simple to implement. With **oPermissions** developer do not need to check for API levels before requesting permissions. 
oPermissions have an **AutoExecute** mode (by default turned `ON`), if application is running on API < 23 **oPermission**
will automatically execute respective `@GrantedPermission` annotated method(s). Library also supports MVP, MVVM, MVC, etc. 
kind of architectural patterns too.

---

##How to Use

First create a `PermBean`, it is just a wrapper for permission(s) and their rationale message(s) to be displayed.

```java
// Just a permission with no message.
PermBean bean = new PermBean(Manifest.permission.READ_PHONE_STATE);
```
OR
```java
// Multiple permission example.
PermBean permBean = new PermBean();

// wrapper of permissions.
permBean.put(Permission.READ_PHONE_STATE, "Your rationale message to be shown for READ_PHONE_STATE");

// usually keep both message same as android ask for Location group permission.
String locMessage = "Your rationale message to be shown for LOCATION";
permBean.put(Permission.ACCESS_FINE_LOCATION, locMessage);     
permBean.put(Permission.ACCESS_COARSE_LOCATION, locMessage);

permBean.put(Permission.WRITE_EXTERNAL_STORAGE, "Your rationale message to be shown for WRITE_EXTERNAL_STORAGE");
```

Then build a request using `RequestPermission`.

```java
// build oPermission request.
RequestPermission
        .on(MainActivity.this)
        .with(permBean)
        .request();
```

In case you are using some architectural patterns such as MVP, MVVM, etc. you can use something like this.

```java
final int PERMISSION_REQUEST_CODE = 1001

// prepare permission request.
RequestPermission
        .on(this)
        .with(permBean)
        .code(PERMISSION_REQUEST_CODE) // optional
        .debug(false)                  // optional
        // set your class instance where annotated methods are declared.
        .setResultTarget(mPresenter)
        .request();
```
    
---
    
You can get results in two ways:
* Create respective methods for expected results with annotations `@GrantedPermission` and `@DeniedPermission`.
* Create a local `BroadcastReceiver` to get all asked permissions result arrays.

*Annotation based results*

```java
@GrantedPermission(permission = Manifest.permission.READ_PHONE_STATE)
void onReadPhonePermissionGranted() {
    // Read Phone status Granted do something related...
}

@DeniedPermission(permission = Manifest.permission.READ_PHONE_STATE)
void onReadPhonePermissionDenied() {
    // Read Phone status Denied do something to prevent... 
}

@GrantedPermission(values = {Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION})
void onLocationPermissionGranted() {
    // Location status Granted do something related...
}

@DeniedPermission(permissions = {Manifest.permission.ACCESS_FINE_LOCATION, 
                                Manifest.permission.ACCESS_COARSE_LOCATION})
void onLocationPermissionDenied() {
    // Location status denied do something to prevent...
}

@GrantedPermission(value = Permission.WRITE_EXTERNAL_STORAGE)
void onWriteStorageGranted() {
    // Write Storage Granted do something to related... 
}

@DeniedPermission(value = Permission.WRITE_EXTERNAL_STORAGE)
void onWriteStorageDenied() {
    // Write Storage Denied do something to prevent...
}
```
    
*Results via Local Broadcast Receiver*

```java
private BroadcastReceiver mReceiver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
    
        Bundle extras = intent.getExtras();
        String[] grantedPermissions = extras.getStringArray(RequestPermission.GRANTED);
        String[] deniedPermissions = extras.getStringArray(RequestPermission.DENIED);

        if (grantedPermissions != null) {
            // onReceive: Granted Permission array
        }
        if (deniedPermissions != null && deniedPermissions.length > 0) {
            // onReceive: Denied Permission array
        }
    }
};
```
    
Do not forget to register local receiver.
   
```java
@Override
protected void onStart() {
    super.onStart();
       
    LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiver, 
                new IntentFilter(RequestPermission.PERMISSION_RESULT_BROADCAST));
}

@Override
protected void onStop() {
    super.onStop();
    
    LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mReceiver);
}
```
    
---
    
##How to import library to your project

Just add below in app module's gradle dependency.

```groovy
dependencies {
    //...
    compile 'co.omkar.utility:opermission:0.0.4'        
}
```
    
Example Activity to implement request.

```java
public class Example extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        
        /* Multiple permission example. */
        PermBean permBean = new PermBean();
        
        permBean.put(Permission.READ_PHONE_STATE, 
                        "Your rationale message to be shown for READ_PHONE_STATE");
        
        // usually keep both message same as android ask for Location group permission.
        permBean.put(Permission.ACCESS_FINE_LOCATION, 
                        "Your rationale message to be shown for LOCATION");     
        permBean.put(Permission.ACCESS_COARSE_LOCATION, 
                        "Your rationale message to be shown for LOCATION");
        
        permBean.put(Permission.WRITE_EXTERNAL_STORAGE, 
                        "Your rationale message to be shown for WRITE_EXTERNAL_STORAGE");

        // prepare permission request.
        RequestPermission
                    .on(this)
                    .with(permBean)
                    .debug(false)       // optional
                    .request();
    }
    
    @GrantedPermission(permissions = {Manifest.permission.ACCESS_FINE_LOCATION, 
                        Manifest.permission.ACCESS_COARSE_LOCATION})
    void onLocationPermissionGranted(){
        getLocation();
    }
    
    @DeniedPermission(permissions = {Manifest.permission.ACCESS_FINE_LOCATION, 
                        Manifest.permission.ACCESS_COARSE_LOCATION})
    void onLocationPermissionDenied(){
        turnOffLocationFeatures();
    }
    
    @GrantedPermission(value = Permission.WRITE_EXTERNAL_STORAGE)
    void onStoragePermissionGranted(){
        writeDataToStorage();
    }
    
    @DeniedPermission(value = Permission.WRITE_EXTERNAL_STORAGE)
    void onStoragePermissionDenied(){
        skipWriteDataToStorage();
    }
}
```

>####NOTE
 Some permissions from `Manifest.permission` are introduced after API > 16, so it is strongly recommended use oPermission's `Permission` value(s) to ask permission(s) in case you are using **AutoExecute** mode.
 
##License
    Copyright (C) 2016 Omkar Todkar
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.