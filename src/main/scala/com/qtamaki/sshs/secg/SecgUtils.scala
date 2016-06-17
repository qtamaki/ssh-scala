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
package com.qtamaki.sshs.secg

import java.math.BigInteger;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Arrays;

import com.qtamaki.sshs.common

object SecgUtils {
  /**
   * SECG 2.3.4 Octet String to ECPoint
   */
  def getDecoded(M: Array[Byte], curve: EllipticCurve): ECPoint = {
    val elementSize = getElementSize(curve);
    if (M.length != 2 * elementSize + 1 || M(0) != 0x04) {
      throw new common.SSHRuntimeException("Invalid 'f' for Elliptic Curve " + curve.toString());
    }
    val xBytes = new Array[Byte](elementSize);
    val yBytes = new Array[Byte](elementSize);
    System.arraycopy(M, 1, xBytes, 0, elementSize);
    System.arraycopy(M, 1 + elementSize, yBytes, 0, elementSize);
    return new ECPoint(new BigInteger(1, xBytes), new BigInteger(1, yBytes));
  }

  /**
   * SECG 2.3.3 ECPoint to Octet String
   */
  def getEncoded(point: ECPoint, curve: EllipticCurve): Array[Byte] = {
    val elementSize = getElementSize(curve);
    val M = new Array[Byte](2 * elementSize + 1);
    M(0) = 0x04;

    val xBytes = stripLeadingZeroes(point.getAffineX().toByteArray());
    val yBytes = stripLeadingZeroes(point.getAffineY().toByteArray());
    System.arraycopy(xBytes, 0, M, 1 + elementSize - xBytes.length, xBytes.length);
    System.arraycopy(yBytes, 0, M, 1 + 2 * elementSize - yBytes.length, yBytes.length);
    return M;
  }

  private def stripLeadingZeroes(bytes: Array[Byte]): Array[Byte] = {
    var start = 0;
    while (bytes(start) == 0x0) {
      start += 1
    }

    return Arrays.copyOfRange(bytes, start, bytes.length);
  }

  private def getElementSize(curve: EllipticCurve): Int = {
    val fieldSize = curve.getField().getFieldSize();
    return (fieldSize + 7) / 8;
  }

}
