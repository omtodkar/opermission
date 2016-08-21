#oPermission

**How to Use**

This library is to help developers to save time writing code for android permission run-time permissions.
It is very simple to implement, first create a `PermBean` for required android run-time permission(s).

    // Just a permission
    PermBean bean = new PermBean(Manifest.permission.READ_PHONE_STATE);

OR

    // Multiple permission example.
    PermBean permBean = new PermBean();

    // wrapper of permissions.
    permBean.put(Permission.READ_PHONE_STATE, "Your rationale message to be shown for READ_PHONE_STATE");
    
    // usually keep both message same as android ask for Location group permission.
    String locMessage = "Your rationale message to be shown for LOCATION";
    permBean.put(Permission.ACCESS_FINE_LOCATION, locMessage);     
    permBean.put(Permission.ACCESS_COARSE_LOCATION, locMessage);
    
    permBean.put(Permission.WRITE_EXTERNAL_STORAGE, "Your rationale message to be shown for WRITE_EXTERNAL_STORAGE");

Then build a request using `RequestPermission`.

    // build oPermission request.
    if (RequestPermission.isPermissionRequired(MainActivity.this, permBean)) {
        RequestPermission
                .on(MainActivity.this)
                .with(permBean)
                .request();
    } else {
        // call your granted methods.
    }
    
You can get results in two ways,
    1. Create two methods and annotate it with `@GrantedPermission` and `@DeniedPermission`.
    2. Create a local broadcast receiver to get result arrays.

*Annotation base results*

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

    @DeniedPermission(permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
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
    
In case you are using some architectural patterns such as MVP, MVVM, etc.

    // prepare permission request.
    if (RequestPermission.isPermissionRequired(this, permBean)) {
        RequestPermission
                .on(this)
                .with(permBean)
                .debug(false)       // optional
                // set your class instance where annotated methods are declared.
                .setResultTarget(mPresenter)
                .request();
    } else {
        // call your granted methods.
    }
    
*Results via Local Broadcast Receiver*

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
    
Do not forget to register local receiver.
    
    @Override
    protected void onStart() {
        super.onStart();
           
        LocalBroadcastManager.getInstance(this)
                    .registerReceiver(mReceiver, new IntentFilter(RequestPermission.PERMISSION_RESULT_BROADCAST));
    }

    @Override
    protected void onStop() {
        super.onStop();
        
        LocalBroadcastManager.getInstance(this)
                    .unregisterReceiver(mReceiver);
    }
    
**How to import to your project**

Just add below gradle dependency.

    dependencies {
        compile 'co.omkar.utility:opermission:0.0.2'        
    }
    
Example Activity to implement request.

    public class Example extends AppCompatActivity {
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_example);
            
            /* Multiple permission example. */
            PermBean permBean = new PermBean();
            
            permBean.put(Permission.READ_PHONE_STATE, "Your rationale message to be shown for READ_PHONE_STATE");
            
            // usually keep both message same as android ask for Location group permission.
            permBean.put(Permission.ACCESS_FINE_LOCATION, "Your rationale message to be shown for LOCATION");     
            permBean.put(Permission.ACCESS_COARSE_LOCATION, "Your rationale message to be shown for LOCATION");
            
            permBean.put(Permission.WRITE_EXTERNAL_STORAGE, "Your rationale message to be shown for WRITE_EXTERNAL_STORAGE");
    
            // prepare permission request.
            if (RequestPermission.isPermissionRequired(this, permBean)) {
                RequestPermission
                        .on(this)
                        .with(permBean)
                        .debug(false)       // optional
                        .request();
            } else {
                // call your granted methods.
            }
        }
    }