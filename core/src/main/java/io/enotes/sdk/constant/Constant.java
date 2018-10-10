package io.enotes.sdk.constant;

public class Constant {
    public static class BlockChain{
        public static final String BITCOIN = "80000000";
        public static final String ETHEREUM = "8000003c";
    }

    public static class Network{
        public static final int BTC_MAINNET = 0;
        public static final int BTC_TESTNET = 1;
        public static final int ETH_MAINNET = 1;
        public static final int ETH_ROPSTEN = 3;
        public static final int ETH_RINKEBY = 4;
        public static final int ETH_KOVAN = 42;
    }

    public static class ContractAddress {
        public static final String ABI_ADDRESS="0x9A21e2c918026D9420DdDb2357C8205216AdD269";
        public static final String ABI_KOVAN_ADDRESS="0x5C036d8490127ED26E3A142024082eaEE482BbA2";
    }
}
