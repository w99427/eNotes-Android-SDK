# eNotes Sdk Android

## CardManager

- [selectAid](#selectAid)
- [startBluetoothScan](#startBluetoothScan)
- [stopBluetoothScan](#stopBluetoothScan)
- [connectBluetooth](#connectBluetooth)
- [disconnectBluetooth](#disconnectBluetooth)
- [enableNfcReader](#enableNfcReader)
- [disableNfcReader](#disableNfcReader)
- [setReadCardCallback](#setReadCardCallback)
- [isConnected](#isConnected)
- [isPresent](#isPresent)
- [getBtcRawTransaction](#getBtcRawTransaction)
- [getEthRawTransaction](#getEthRawTransaction)

## RPCApiManager

- Universal
  - [setNetworkConfig](#setNetworkConfig)
  - [getBalance](#getBalance)
  - [getTransactionReceipt](#getTransactionReceipt)
  - [sendRawTransaction](#sendRawTransaction)

- Bitcoin
  - [estimateFee](#estimateFee)
  - [getUnSpend](#getUnSpend)

- Ethereum
  - [estimateGas](#estimateGas)
  - [getGasPrice](#getGasPrice)
  - [getNonce](#getNonce)
  - [call](#call)

## Callback
##### All asynchronous requests are returned through this interface
```
public Interface Callback {
    void onBack(Resource resource);
}
```

```
public class Resource <T> {
    public T data;
    public int status;
    public int errorCode;
    public String msg;
}
```

## NetworkConfig
```
public class NetworkConfig{
    private String[] etherscanKeys;
    private String[] infuraKeys;
    private String[] blockchypherKeys;
}
```

## Constant

### blockchain
| blockchain    | value                               |
| :-            | :-                                    |
| bitcoin       | 80000000     |
| ethereum      | 8000003c     |

### network
- Bitcoin
| network    | value                               |
| :-            | :-                                    |
| mainnet      | 0    |
| testnet     | 1    |

- Ethereum
| network    | value                               |
| :-            | :-                                    |
| mainnet      | 1    |
| ropsten     | 3    |
| rinkeby     | 4    |
| kovan     | 42    |

###  Status

| status    | value             | remark          |
| :-        | :-              | :-              |
| SUCCESS       | 0             | callback is success        |
| ERROR         | 1               | callback is error, you can get error code by status.getCode() |
| NFC_CONEECTED   | 2		| nfc is connected , will begin parse card  |
| BLUETOOTH_CONNECTING | 3     | connecting bluetooth device |
| BLUETOOTH_CONNECTED | 4      | bluetooth device is connected |
| BLUETOOTH_PARSING | 5      | parsing card|
| BLUETOOTH_SCAN_FINISH | 6      | bluetooth scan finish|
| CARD_PARSE_FINISH | 7      | card parse finish|


### Error code

| error     | code             | remark          |
| :-            | :-                  | :-              |
| NOT_ERROR       | 0               | not error         |
| BLUETOOTH_DISCONNECT       | 100               | bluetooth disconnect         |
| NFC_DISCONNECT       | 101               | nfc disconnect       |
| INVALID_CARD       | 102               |  read cert or verify sign error      |
| NOT_FIND_CARD       | 103           | card not on the phone or on the card reader         |
| NET_ERROR       | 104               |  include 404 、 500 、 timeout...       |
| NET_UNAVAILABLE       | 105               |  net unavailable      |
| BLUETOOTH_UNABLE       | 106               |  bluetooth unable      |
| CALL_CERT_PUB_KEY_ERROR       | 107               |  Call cert public key error      |
| SDK_ERROR       | 108               |  sdk self error      |
| NOT_FIND_RIGHT_CARD       | 109               |  not find right card when withdraw      |



## CardManager Reference

### selectAid
- select cardlet application id
- interface definition：
```
public void selectAid(String aid)
```
- code example:
```
CardManager cardManager = new CardManager(activity);
cardManager.selectAid(aid)
```

### startBluetoothScan
- start scan bluetooth devices
- interface definition： 
```
public void startBluetoothScan(Callback callback)
```
- code example: 
```
CardManager cardManager = new CardManager(activity);
cardManager.startScanDevice((resource)-> {
	if(resource.status == Status.SUCCESS) {
		List<BluetoothDevice> deviceList = resource.data;
		//TODO
	} else if(resource.status == Status.ERROR) {
		String errorMsg = resource.msg;
		int errorCode = resource.code;
		//TODO
	}
})
```

### stopBluetoothScan
- stop scan bluetooth devices
- interface definition：
```
public void stopBluetoothScan()
```

### connectBluetooth
- connect bluetooth device,you can get connect statu from [setReadCardCallback](#setReadCardCallback)
- interface definition：
```
public void connectBluetooth(BluetoothDevice device)
```

### disconnectBluetooth
- disconnect bluetooth device,you can get connect statu from [setReadCardCallback](#setReadCardCallback)
- interface definition：
```
public void disconnectBluetooth()
```

### enableNfcReader
- Limit the NFC controller to reader mode while this Activity is in the foreground , you can get read status from [setReadCardCallback](#setReadCardCallback),support you set on BaseActivity onResume().
- interface definition：
```
public void enableNfcReader(Activity activity)
```

### disableNfcReader
- Restore the NFC adapter to normal mode of operation,support you set on BaseActivity onPause().
- interface definition：
```
public void disableNfcReader(Activity activity)
```

### setReadCardCallback
- after read cert and verify sign by bluetooth or nfc , will get card object.
- interface definition：
```
public void setReadCardCallback(Callback callback)
```
- params:
  Callback:  read card callback , you can get success or fail statu , @ [Callback](#Callback)
- code example:
```
CardManager cardManager = new CardManager(activity);
cardManager.setReadCardManager((resource)-> {
	if(resource.status == Status.SUCCESS) {
		Card card = resource.data;
		//TODO
	} else if(resource.status == Status.ERROR) {
		String errorMsg = resource.msg;
		int errorCode = resource.code;
		//TODO
	}
})
```

### isConnected
- judge whether bluetooth is connected. 
- interface definition：
```
public boolean isConnected()
```

### isPresent
- judge whether card is clinging to phone or bluetooth.
- interface definition：
```
public boolean isPresend()
```

### getBtcRawTransaction
- get btc raw transaction.
- interface definition：
```
public void getBtcRawTransaction(Card card, string fee, String toAddress, List<EntUtxoEntity> utxos, Callback callback)
```
- code exapmle:
```
CardManager cardManager = new CardManager(activity);
cardManager.getBtcRawTransaction(Card card, string fee, String toAddress, List<EntUtxoEntity> utxos, (resource)-> {
	if(resource.status == Status.SUCCESS) {
		String hex = resource.data;
		//TODO
	} else if(resource.status == Status.ERROR) {
		String errorMsg = resource.msg;
		int errorCode = resource.code;
		//TODO
	}
})
```

### getEthRawTransaction
- get eth raw transaction.
- interface definition：
```
public void getEthRawTransaction(Card card, string nonce, String estimateGas, String gasPrice, String value, Callback callback)
```
- code exapmle:
```
CardManager cardManager = new CardManager(activity);
cardManager.getEthRawTransaction(Card card, string nonce, String estimateGas, String gasPrice, String value, (resource)-> {
	if(resource.status == Status.SUCCESS) {
		String hex = resource.data;
		//TODO
	} else if(resource.status == Status.ERROR) {
		String errorMsg = resource.msg;
		int errorCode = resource.code;
		//TODO
	}
})
```

## RPCApiManager Reference

### Universal

#### setNetworkConfig
- define different blockchain api keys setting, we provide default value, you can provide it by yourself, @[NetworkConfig](#NetworkConfig).
- interface definition:
```
public static void setNetworkConfig(NetworkConfig networkConfig)
```

#### getBalance
- Returns the balance of the account of given address.
- interface definition:
```
public void getBalance(String blockchain, int network, String address, Callback callback)
```
- code example:
```
RPCApiManager manager = new RPCApiManager(activity);
manager.getBalance("8000003c", 42, (resource)-> {
    if(resource.status == Status.SUCCESS) {
		EntBalanceEntity entity= resource.data;
		String balance = entity.getBalance();
		//TODO
	} else if(resource.status == Status.ERROR) {
		String errorMsg = resource.msg;
		//TODO
	}
})
```

#### getTransactionReceipt
- Returns the receipt of a transaction by transaction hash.
- interface definition:
```
public void getTransactionReceipt(String blockchain, int network, String txId, Callback callback)
```

- code example:
```
RPCApiManager manager = new RPCApiManager(activity);
manager.getTransactionReceipt("8000003c", 42, "0x21h43kj345h353h3k32b43j3434wwa", (resource)-> {
    if(resource.status == Status.SUCCESS) {
		EntConfirmedEntity entity= (EntConfirmedEntity)resource.data;
		int confirmations = entity.getConfirmations();
		//TODO
	} else if(resource.status == Status.ERROR) {
		String errorMsg = resource.msg;
		int errorCode = resource.code;
		//TODO
	}
})
```

#### sendRawTransaction
- Creates new message call transaction or a contract creation for signed transactions.
- interface definition:
```
public void sendRawTransaction(String blockchain, int network, String hexString, Callback callback)
```
- code example:
```
RPCApiManager manager = new RPCApiManager(activity);
manager.sendRawTransaction("8000003c", 42, "0x21h43dsfetgdfgrsfse32423tryfsntrtrerttr8iolkj345h353h3k32b43j3434wwa", (resource)-> {
    if(resource.status == Status.SUCCESS) {
		EntSendTxEntity entity= resource.data;
		String txId = entity.getTxId();
		//TODO
	} else if(resource.status == Status.ERROR) {
		String errorMsg = resource.msg;
		int errorCode = resource.code;
		//TODO
	}
})
```

### Bitcoin

#### estimateFee
- Return current Bitcoin transaction fee predictions.
- interface definition:
```
public void estimateFee(int network, Callback callback)
```
- code example:
```
RPCApiManager manager = new RPCApiManager(activity);
manager.estimateFee(42, (resource)-> {
    if(resource.status == Status.SUCCESS) {
		EntFeeEntity entity= resource.data;
		String highFee = entity.getHighFee();
		String mediumFee = entity.getMediumFee();
		String lowFee = entity.getLowFee();
		//TODO
	} else if(resource.status == Status.ERROR) {
		String errorMsg = resource.msg;
		int errorCode = resource.code;
		//TODO
	}
})
```

#### getUnSpend
- Returns data contained in all unspend outputs.
- interface definition:
```
public void getUnSpend(int network, String address, Callback callback)
```
- code example:
```
RPCApiManager manager = new RPCApiManager(activity);
manager.getUnspend(42, "mjsdsah12hads3234h", (resource)-> {
    if(resource.status == Status.SUCCESS) {
		List<EntUtxoEntity> list= resource.data;
		for(EntUnspendEntity entity:list) {
            String balance = entity.getBalance();
            String script = entity.getScipt();
            String txId = entity.getTxId();
		}
		//TODO
	} else if(resource.status == Status.ERROR) {
		String errorMsg = resource.msg;
		int errorCode = resource.code;
		//TODO
	}
})
```

### Ethereum

#### estimateGas
- Generates and returns an estimate of how much gas is necessary to allow the transaction to complete. 
- interface definition:
```
public void estimateGas(int network, String from, String to, String value, String gasPrice, String data, Callback callback)
```
- code example:
```
RPCApiManager manager = new RPCApiManager(activity);
manager.estimateGas(42, "0xewkwjshsgsg333433",""0xewej2o1j3j2jljvnej34ew", "3000000000000000", "9000000000", "0xdasdader343434", (resource)-> {
    if(resource.status == Status.SUCCESS) {
		EntGasEntity entity= resource.data;
		String gas = entity.getResult();
		//TODO
	} else if(resource.status == Status.ERROR) {
		String errorMsg = resource.msg;
		int errorCode = resource.code;
		//TODO
	}
})
```

#### getGasPrice
- Returns the number of hashes per second that the node is mining with.
- interface definition:
```
public void getGasPrice(int network, Callback callback)
```
- code example:
```
RPCApiManager manager = new RPCApiManager(activity);
manager.getGasPrice(42, (resource)-> {
    if(resource.status == Status.SUCCESS) {
		EntGasPriceEntity entity= resource.data;
		String low = entity.getLow();
		String standard = entity.getStandard();
		String fast = entity.getFast();
		String fastest = entity.getFastest();
		//TODO
	} else if(resource.status == Status.ERROR) {
		String errorMsg = resource.msg;
		int errorCode = resource.code;
		//TODO
	}
})
```

#### getNonce
- Returns the number of transactions sent from an address.
- interface definition:
```
public void getNonce(int network, String address, Callback callback)
```
- code example:
```
RPCApiManager manager = new RPCApiManager(activity);
manager.getNonce(42, "0xsugjauwr23sjd3shdsj1", (resource)-> {
    if(resource.status == Status.SUCCESS) {
		EntNonceEntity entity= resource.data;
		String nonce = entity.getResult();
		//TODO
	} else if(resource.status == Status.ERROR) {
		String errorMsg = resource.msg;
		int errorCode = resource.code;
		//TODO
	}
})
```

#### call
- Executes a new message call immediately without creating a transaction on the block chain.
- interface definition:
```
public void call(int network, String toAddress, String data, Callback callback)
```
- code example:
```
RPCApiManager manager = new RPCApiManager(activity);
manager.callCertPublickKey(42, "0xdsaew3332we4tteesdafdf", "0xhasidjrrit7373737sjsjsnvnv", (resource)-> {
    if(resource.status == Status.SUCCESS) {
		String hex= resource.data;
		//TODO
	} else if(resource.status == Status.ERROR) {
		String errorMsg = resource.msg;
		int errorCode = resource.code;
		//TODO
	}
})
```
