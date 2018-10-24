# eNotes-Android-SDK

[ ![Download](https://api.bintray.com/packages/enotes/eNotes/core/images/download.svg) ]

##### Project website:[https://github.com/w99427/eNotes-Android-SDK](https://github.com/w99427/eNotes-Android-SDK)

### Description

The eNotes library is a Java implementation of the verify and manage your eNotes through this app with NFC supported smartphones,which use android 5.1 or above smartphones with NFC support,  or use these smarphones with bluetooth NFC reader which is sold separately.

### Technologies

* Java 8 for the core modules
* [Gradle 3.4+](https://gradle.org/) - for building the project
* Android SDK Version 28+ 

### Features:
* Support BTC (mainnet and testnet)
* Support ETH (mainnet , ropsten , rinkeby and kovan)
* Support ERC20 token
* Verify physical form of digital asserts with NFC and bluetooth

### Getting started
#### 1.Using Gradle:
```
repositories {
        google()
        jcenter()
        maven {
            url'https://dl.bintray.com/enotes/eNotes'
        }
    }
```

```
implementation 'io.enotes.sdk:core:1.0.7'
```

#### 2.Building from the command line:
To get started, it is best to have the latest JDK and Gradle installed. The HEAD of the `develop` branch contains the latest development code.

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


For more details, you can read the apiDoc and the Example of the project.


