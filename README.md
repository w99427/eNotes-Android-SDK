# eNotes-Android-SDK

[ ![Download](https://api.bintray.com/packages/enotes/sdk/core/images/download.svg) ](https://bintray.com/enotes/sdk/core/_latestVersion)[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

##### Project website:[https://github.com/w99427/eNotes-Android-SDK](https://github.com/w99427/eNotes-Android-SDK)

### Description

The eNotes library is a Java implementation of the verify and manage your eNotes through this app with NFC supported smartphones,which use android 5.1 or above smartphones with NFC support,  or use these smarphones with bluetooth NFC reader which is sold separately.

### Technologies

* Java 8 for the core modules
* [Gradle 3.4+](https://gradle.org/) - for building the project
* Android SDK Version 27+

### Features:
* Support BTC (mainnet and testnet)
* Support ETH (mainnet , ropsten , rinkeby and kovan)
* Support ERC20 token
* Verify physical form digital asserts with NFC and bluetooth

### Getting started
#### 1.Using Gradle:
```
repositories {
        google()
        jcenter()
        maven {
            url'https://dl.bintray.com/enotes/sdk'
        }
    }
```

```
implementation 'io.enotes.sdk:core:0.1.0'
```

#### 2.Building from the command line:
To get started, it is best to have the latest JDK and Gradle installed. The HEAD of the `master` branch contains the latest development code.

To perform a full build use
```
./gradlew clean build
```

The outputs are under the `build` directory.

#### 3.Usage:
##### NFC
* init CardManager
```
CardManager cardManager = new CardManager(activity);
```
*  you must set enableNfcReader onResume and set disableNfcReader onPause
```
@Override
    protected void onResume() {
        super.onResume();
        cardManager.enableNfcReader(this);
    }

protected void onPause() {
        super.onPause();
        cardManager.disableNfcReader(this);
    }
```
* you can get read card callback
```
cardManager.setReadCardCallback(resource -> {
            Log.i("tag", resource.status + "");
            if (resource.status == Status.SUCCESS) {
                Card card = resource.data;
            }
       });
```
#### Bluetooth
* init CardManager
```
CardManager cardManager = new CardManager(activity);
```
* scan bluetooth devices and connect
```
cardManager.startBluetoothScan((resource -> {
                if (resource.status == Status.SUCCESS) {
                    Reader data = resource.data;
                    
                }
            }));
            
cardManager.connectBluetooth(bluetoothDevice);
```
* you can get read card callback
```
cardManager.setReadCardCallback(resource -> {
            Log.i("tag", resource.status + "");
            if (resource.status == Status.SUCCESS) {
                Card card = resource.data;
            }
       });
```

#### uses-permission:
```
    <uses-feature
        android:name="android.hardware.bluetooth"
        android:required="false" />
    <uses-feature
        android:name="android.hardware.bluetooth_le"
        android:required="false" />
    <uses-feature
        android:name="android.hardware.nfc"
        android:required="false" />

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.NFC" />
```


For more details, you can read the apidocs and the examples of the project.


## License

``` 
Copyright 2018 eNotes

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


