#oPermission [![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

**oPermission** is android library to reduce the developer's efforts implementing android run-time permissions boilerplate. It is very simple to implement. With **oPermissions** it is not necessary to check for API or Build version requesting permissions. oPermissions have an **AutoExecute** mode, if application is running on API < 23 **oPermission** will automatically execute respective `@GrantedPermission` or `OPermission` with `true` annotated method(s). Library also supports MVP, MVVM, MVC, etc. architectural patterns.

---

##Usage

Single line code for request.

```java
RequestPermission.on(this).with(PermBean.create(Permission.READ_PHONE_STATE)).request();
```

Single method to get results.

```java
@OPermission(value = Permission.READ_PHONE_STATE)
void onReadPhoneStatePermission(@Result boolean isGranted) {
    if (isGranted) {
        textView.setText("Write Storage Granted");
    } else {
        textView.setText("Write Storage Denied");
    }
}
```
    
If your application is running on API lower than Marshmallows, result method works as permission is granted and value assigned to `isGranted` is true.  
    
---
    
##How to get it? [![Download](https://api.bintray.com/packages/omtodkar/maven/opermission/images/download.svg) ](https://bintray.com/omtodkar/maven/opermission/_latestVersion)

Just append following code in your `app` module's gradle file.

```groovy
dependencies {
    //...
    compile 'co.omkar.utility:opermission:0.0.5'
}
```
 
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