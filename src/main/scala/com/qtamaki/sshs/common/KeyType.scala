package com.qtamaki.sshs.common

import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.math.GroupElement;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces._;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

import com.qtamaki.sshs.secg.SecgUtils
import com.qtamaki.sshs.signature.Ed25519PublicKey

sealed abstract class KeyType(var sType:String) {

    def readPubKeyFromBuffer(stype:String, buf:Buffer):PublicKey

    def putPubKeyIntoBuffer(pk:PublicKey, buf:Buffer)

    protected def isMyType(key:Key):Boolean

    override def toString():String = {
        return sType;
    }

 
}

object KeyType {
    /** SSH identifier for RSA keys */
    val RSA = new KeyType("ssh-rsa") {
        override def readPubKeyFromBuffer(stype:String, buf:Buffer):PublicKey = {
            val (e, n) = try {
                (buf.readMPInt(), buf.readMPInt())
            } catch {
              case be:Buffer.BufferException =>
                throw new GeneralSecurityException(be);
            }
            val keyFactory = SecurityUtils.getKeyFactory("RSA");
            return keyFactory.generatePublic(new RSAPublicKeySpec(n, e));
        }

        override def putPubKeyIntoBuffer(pk:PublicKey, buf:Buffer) {
            val rsaKey = pk.asInstanceOf[RSAPublicKey];
            buf.putString(sType)
            .putMPInt(rsaKey.getPublicExponent()) // e
            .putMPInt(rsaKey.getModulus()); // n
        }

        override protected def isMyType(key:Key):Boolean = {
            return (key.isInstanceOf[RSAPublicKey] || key.isInstanceOf[RSAPrivateKey]);
        }

    }

    /** SSH identifier for DSA keys */
    val DSA = new KeyType("ssh-dss") {
        override def readPubKeyFromBuffer(stype:String, buf:Buffer):PublicKey = {
            val (p,q,g,y) = try {
                (buf.readMPInt(),
                buf.readMPInt(),
                buf.readMPInt(),
                buf.readMPInt())
            } catch {
              case be:Buffer.BufferException =>
                throw new GeneralSecurityException(be);
            }
            val keyFactory = SecurityUtils.getKeyFactory("DSA");
            return keyFactory.generatePublic(new DSAPublicKeySpec(y, p, q, g));
        }

        override def putPubKeyIntoBuffer(pk:PublicKey, buf:Buffer) {
            pk match {
              case dsaKey:DSAPublicKey =>
                buf.putString(sType)
                        .putMPInt(dsaKey.getParams().getP()) // p
                        .putMPInt(dsaKey.getParams().getQ()) // q
                        .putMPInt(dsaKey.getParams().getG()) // g
                        .putMPInt(dsaKey.getY()); // y
            }
        }

        override protected def isMyType(key:Key):Boolean = {
            return (key.isInstanceOf[DSAPublicKey] || key.isInstanceOf[DSAPrivateKey]);
        }

    }

    /** SSH identifier for ECDSA keys */
    val ECDSA = new KeyType("ecdsa-sha2-nistp256") {
        private val log:Logger = LoggerFactory.getLogger(getClass());

        override def readPubKeyFromBuffer(stype:String, buf:Buffer):PublicKey = {
            try {
                // final String algo = buf.readString();  it has been already read
                val curveName = buf.readString();
                val keyLen = buf.readUInt32AsInt();
                val x04 = buf.readByte();  // it must be 0x04, but don't think we need that check
                val x = new Array[Byte]((keyLen - 1) / 2);
                val y = new Array[Byte]((keyLen - 1) / 2);
                buf.readRawBytes(x);
                buf.readRawBytes(y);
                if(log.isDebugEnabled()) {
                    log.debug("Key algo: %s, Key curve: %s, Key Len: %s, 0x04: %s\nx: %s\ny: %s".format(
                            stype,
                            curveName,
                            keyLen,
                            x04,
                            Arrays.toString(x),
                            Arrays.toString(y))
                    );
                }

                if (!NISTP_CURVE.equals(curveName)) {
                    throw new GeneralSecurityException(String.format("Unknown curve %s", curveName));
                }

                val bigX = new BigInteger(1, x);
                val bigY = new BigInteger(1, y);

                val ecParams = NISTNamedCurves.getByName("p-256");
                val pPublicPoint = ecParams.getCurve().createPoint(bigX, bigY);
                val spec = new ECParameterSpec(ecParams.getCurve(),
                        ecParams.getG(), ecParams.getN());
                val publicSpec = new ECPublicKeySpec(pPublicPoint, spec);

                val keyFactory = KeyFactory.getInstance("ECDSA");
                return keyFactory.generatePublic(publicSpec);
            } catch {
              case ex:Exception => 
                throw new GeneralSecurityException(ex);
            }
        }


        override def putPubKeyIntoBuffer(pk:PublicKey, buf:Buffer) {
            pk match {
              case ecdsa:ECPublicKey =>
            val encoded = SecgUtils.getEncoded(ecdsa.getW(), ecdsa.getParams().getCurve());

            buf.putString(sType)
                .putString(NISTP_CURVE)
                .putBytes(encoded);
            }
        }

        override protected def isMyType(key:Key):Boolean = {
            return ("ECDSA".equals(key.getAlgorithm()));
        }
    }

    val ED25519 = new KeyType("ssh-ed25519") {
        private val logger:Logger = LoggerFactory.getLogger(KeyType.getClass);

        override def readPubKeyFromBuffer(stype:String, buf:Buffer):PublicKey = {
            try {
                val keyLen = buf.readUInt32AsInt();
                val p = new Array[Byte](keyLen);
                buf.readRawBytes(p);
                if (logger.isDebugEnabled()) {
                    logger.debug("Key algo: %s, Key curve: 25519, Key Len: %s\np: %s".format(
                            stype,
                            keyLen,
                            Arrays.toString(p))
                    );
                }

                val ed25519 = EdDSANamedCurveTable.getByName("ed25519-sha-512");
                val point = ed25519.getCurve().createPoint(p, true);
                val publicSpec = new EdDSAPublicKeySpec(point, ed25519);
                return new Ed25519PublicKey(publicSpec);

            } catch {
              case be:Buffer.BufferException =>
                throw new SSHRuntimeException(be);
            }
        }

        override def putPubKeyIntoBuffer(pk:PublicKey, buf:Buffer) {
            pk match {
              case key:EdDSAPublicKey =>
                buf.putString(sType).putBytes(key.getAbyte());
            }
        }

        override protected def isMyType(key:Key):Boolean = {
            return "EdDSA".equals(key.getAlgorithm());
        }
    }

    /** Unrecognized */
    val UNKNOWN = new KeyType("unknown") {
        override def readPubKeyFromBuffer(stype:String, buf:Buffer):PublicKey = {
            throw new UnsupportedOperationException("Don't know how to decode key:" + stype);
        }

        override def putPubKeyIntoBuffer(pk:PublicKey, buf:Buffer) {
            throw new UnsupportedOperationException("Don't know how to encode key: " + pk);
        }

        override protected def isMyType(key:Key):Boolean = {
            return false;
        }
    };

    private val NISTP_CURVE = "nistp256";
    
    val values = Array(
       RSA,
       DSA,
       ECDSA,
       ED25519,
       UNKNOWN
   )

    def  fromKey(key:Key):KeyType = {
        for (kt <- values)
            if (kt.isMyType((key)))
                return kt;
        return UNKNOWN;
    }
    
    def fromString(sType:String):KeyType = {
        for (kt <- values)
            if (kt.sType.equals(sType))
                return kt;
        return UNKNOWN;
    }
 
}