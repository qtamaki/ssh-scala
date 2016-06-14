/*
 * Copyright (C)2016 - SSH-SCALA Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qtamaki.sshs.signature

import net.i2p.crypto.eddsa.EdDSAPublicKey
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec
import net.schmizz.sshj.common.SSHRuntimeException

import java.util.Arrays

case class Ed25519PublicKey(spec:EdDSAPublicKeySpec) extends EdDSAPublicKey(spec) {
    val ed25519 = EdDSANamedCurveTable.getByName("ed25519-sha-512");
    if (!spec.getParams().getCurve().equals(ed25519.getCurve())) {
        throw new SSHRuntimeException("Cannot create Ed25519 Public Key from wrong spec");
    }
    
    override def equals(other:Any):Boolean = {
      other match {
        case otherKey:Ed25519PublicKey =>
          Arrays.equals(getAbyte(), otherKey.getAbyte());
        case _ => false 
      }
    }

    override def hashCode() = getA().hashCode()

}