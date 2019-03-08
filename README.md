# eNotes Android SDK

[ ![Download](https://api.bintray.com/packages/cryptoenotes/eNotes/library/images/download.svg) ](https://bintray.com/enoteschain/sdk/core/_latestVersion)[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## Description

eNotes Android SDK is the official library for verifing and managing eNotes which is the first physical form of digital assets in the world. With this SDK, developers could integrate the functions in their application easily and fast.

**eNotes Home Page**: [https://enotes.io](https://enotes.io)

## Technologies

* Java 8 for the core modules
* [Gradle 3.4+](https://gradle.org/) - for building the project
* Android SDK Version 27+

## Features

* Communicate with eNotes through NFC if the smart phone supports NFC
* Communicate with eNotes through bluetooth NFC reader if the smart phone doesn't support NFC
* Verify eNotes, including counterfeit detection, balance querying, cryptocurrency withdraw and etc.
* Query exchange rate from well-known exchanges.

## Getting started

### Get SDK

#### Using Gradle

```
repositories {
    google()
    jcenter()
    maven {
        url 'https://dl.bintray.com/cryptoenotes/eNotes'
    }
}
```

```
implementation 'io.enotes.sdk:library:1.0.8'
```

#### Building from source code

It is best to have the latest JDK and Gradle installed. The HEAD of the `master` branch contains the latest code.

To perform a full build, use:

```
./gradlew clean build
```

The outputs are under the `build` directory.

### Debug through emulator service

If you don't have eNotes around, you can use a service to simulate eNotes for your debugging. Please follow the steps below to use these services.

1. download and run emulator service

    Download the jar file from [eNotes Emulator](https://github.com/w99427/eNotes-Emulator/tree/master/out), and run it.

    ```
    java -jar eNotesSdkServer-0.1.0.jar
    ```

2. config sdk in your code

    ```
    ENotesSDK.config.debugForEmulatorCard = true;
    ENotesSDK.config.emulatorCardIp = "your server ip";
    ```
  
    For more details, see [eNotes Emulator Project](https://github.com/w99427/eNotes-Emulator) project.

### Use NFC

1. init CardManager

    ```
    CardManager cardManager = new CardManager(activity);
    ```

2. set enableNfcReader onResume and set disableNfcReader onPause

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
3. set callback to get data

    ```
    cardManager.setReadCardCallback(resource -> {
        Log.i("tag", resource.status + "");
        if (resource.status == Status.SUCCESS) {
            Card card = resource.data;
            Cert cert = card.getCert();
            String publicKey = card.getCurrencyPubKey();
            String address = card.getAddress();
        }
    });
    ```
4. signs the given hash and returns the R and S
	```
	Entsignature sign = cardManager.doSign(hash);
	String r = sign.getR();
	String s = sign.getS();
	```

### Use Bluetooth NFC Reader

1. init CardManager

    ```
    CardManager cardManager = new CardManager(activity);
    ```

2. scan bluetooth devices and connect

    ```
    cardManager.startBluetoothScan((resource -> {
        if (resource.status == Status.SUCCESS) {
            Reader data = resource.data;
            
        }
    }));
    
    cardManager.connectBluetooth(bluetoothDevice);
    ```

3. set callback to get data

    ```
    cardManager.setReadCardCallback(resource -> {
        Log.i("tag", resource.status + "");
        if (resource.status == Status.SUCCESS) {
            Card card = resource.data;
        }
    });
    ```

### Permissions

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

For more details, please read the apidocs and the examples of this project.

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
