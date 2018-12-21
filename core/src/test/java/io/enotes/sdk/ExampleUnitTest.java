package io.enotes.sdk;

import android.util.Log;

import com.ripple.core.coretypes.AccountID;
import com.ripple.core.coretypes.Amount;
import com.ripple.core.coretypes.uint.UInt32;
import com.ripple.core.types.known.tx.signed.SignedTransaction;
import com.ripple.core.types.known.tx.txns.Payment;
import com.ripple.crypto.ecdsa.IKeyPair;
import com.ripple.crypto.ecdsa.K256KeyPair;
import com.ripple.crypto.ecdsa.Seed;
import com.ripple.encodings.base58.B58;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.ethereum.util.ByteUtil;
import org.junit.Test;

import io.enotes.sdk.repository.card.CommandException;
import io.enotes.sdk.repository.card.TLVBox;
import io.enotes.sdk.utils.CardUtils;
import io.enotes.sdk.utils.LogUtils;

import static io.enotes.sdk.repository.card.Commands.TLVTag.Device_Certificate;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void createXrpRawTransaction(){
        String secret = "shwtqohe1FBZ9ErN8aDt7PxviRnVw";
        Payment payment = new Payment();

        // Put `as` AccountID field Account, `Object` o
        payment.as(AccountID.Account,     "rP7JNBVPS31t1vroS3u9pPpywi5MYjyvkr");
        payment.as(AccountID.Destination, "r3fVM5zkC4TVmLnD5PeSYSNZQkDLT6bTeQ");
        payment.as(Amount.Amount,         "20000000");
        payment.as(UInt32.Sequence,       10);
        payment.as(Amount.Fee,            "10");

        // Try commenting out the Fee, you'll get STObject.FormatException
        SignedTransaction signed = payment.sign(secret);
        String toHexString = ByteUtil.toHexString(signed.signingData);
        toHexString.toUpperCase();
    }

    @Test
    public void testRippleAddress(){
        String secret = "shwtqohe1FBZ9ErN8aDt7PxviRnVw";
        IKeyPair keyPair = Seed.getKeyPair(secret);
        AccountID accountID = AccountID.fromKeyPair(keyPair);
        String address = accountID.address;
        String pubHex = keyPair.canonicalPubHex();


        Payment payment = new Payment();

        // Put `as` AccountID field Account, `Object` o
        payment.as(AccountID.Account,     "rP7JNBVPS31t1vroS3u9pPpywi5MYjyvkr");
        payment.as(AccountID.Destination, "r3fVM5zkC4TVmLnD5PeSYSNZQkDLT6bTeQ");
        payment.as(Amount.Amount,         "20000000");
        payment.as(UInt32.Sequence,       4);
        payment.as(Amount.Fee,            "10");

        // Try commenting out the Fee, you'll get STObject.FormatException
        SignedTransaction signed = payment.sign(secret);
        String tx_blob = signed.tx_blob;
        tx_blob.toUpperCase();
    }


}