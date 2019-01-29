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
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.signers.ECDSASigner;

import java.math.BigInteger;

import io.enotes.sdk.constant.ErrorCode;
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
    public void createXrpRawTransaction() {
        String secret = "shwtqohe1FBZ9ErN8aDt7PxviRnVw";
        Payment payment = new Payment();

        // Put `as` AccountID field Account, `Object` o
        payment.as(AccountID.Account, "rP7JNBVPS31t1vroS3u9pPpywi5MYjyvkr");
        payment.as(AccountID.Destination, "r3fVM5zkC4TVmLnD5PeSYSNZQkDLT6bTeQ");
        payment.as(Amount.Amount, "20000000");
        payment.as(UInt32.Sequence, 10);
        payment.as(Amount.Fee, "10");

        // Try commenting out the Fee, you'll get STObject.FormatException
        SignedTransaction signed = payment.sign(secret);
        String toHexString = ByteUtil.toHexString(signed.signingData);
        toHexString.toUpperCase();
    }

    @Test
    public void testRippleAddress() {
        String secret = "shwtqohe1FBZ9ErN8aDt7PxviRnVw";
        IKeyPair keyPair = Seed.getKeyPair(secret);
        AccountID accountID = AccountID.fromKeyPair(keyPair);
        String address = accountID.address;
        String pubHex = keyPair.canonicalPubHex();


        Payment payment = new Payment();

        // Put `as` AccountID field Account, `Object` o
        payment.as(AccountID.Account, "rP7JNBVPS31t1vroS3u9pPpywi5MYjyvkr");
        payment.as(AccountID.Destination, "r3fVM5zkC4TVmLnD5PeSYSNZQkDLT6bTeQ");
        payment.as(Amount.Amount, "30000000");
        payment.as(UInt32.Sequence, 5);
        payment.as(Amount.Fee, "10");

        // Try commenting out the Fee, you'll get STObject.FormatException
        SignedTransaction signed = payment.sign(secret);
        String tx_blob = signed.tx_blob;
        tx_blob.toUpperCase();
    }

    private static final X9ECParameters curve = SECNamedCurves.getByName("secp256k1");
    private static final ECDomainParameters domain = new ECDomainParameters(curve.getCurve(), curve.getG(), curve.getN(), curve.getH());

    @Test
    public void verifySignatureTwiceHash() throws CommandException {
        String s1 = org.ethereum.crypto.ECKey.HALF_CURVE_ORDER.toString();
        String s2 = org.ethereum.crypto.ECKey.CURVE.getN().toString(16);


        byte[] publicKey = ByteUtil.hexStringToBytes("0444ae7f5391d52515b746191d114e2bf612408bd2abd9ee95bbd2b4f9a67566707193c394d07511bd110c21c640053ca8ca1ad948cdac9281c62e87444fbb85e6");
        byte[] data = ByteUtil.hexStringToBytes("30818d0201021309654e6f7465732e696f180f32303138313130313038353930305a302302080de0b6b3a7640000040480000000020100300e0404000000028106313233343536042103966b952d0c1e874e02769d619a8e20dfafd457c4ba433b6afd53973d0ba651183024161041414130303030393030303030313236161041414130303030303030303030303030");
        if (publicKey == null || publicKey.length == 0 || data == null || data.length == 0) {
            throw new CommandException(ErrorCode.INVALID_CARD, "Invalid cert _ verify manufacture cert fail");
        }
        BigInteger r = new BigInteger("79504e0c8abb36b49ea8ecdf55bfb1ffe4341435f59170243a64a3b509d7cc3e", 16);
        BigInteger s = new BigInteger("35ad80aab2e2ef2260af20fae370d02c4bc84a09e020b56d008fb9c09278db31", 16);
        SHA256Digest s_SHA256Digest = new SHA256Digest();
        s_SHA256Digest.update(data, 0, data.length);
        byte hash[] = new byte[32];
        s_SHA256Digest.doFinal(hash, 0);

        s_SHA256Digest.reset();

        s_SHA256Digest.update(hash, 0, hash.length);
        byte db_hash[] = new byte[32];
        s_SHA256Digest.doFinal(db_hash, 0);

        ECDSASigner signer = new ECDSASigner();
        signer.init(false, new ECPublicKeyParameters(curve.getCurve().decodePoint(publicKey), domain));
        boolean verifySignature = signer.verifySignature(db_hash, r, s);
    }


}