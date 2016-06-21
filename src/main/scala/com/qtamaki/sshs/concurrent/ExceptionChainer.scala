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
package com.qtamaki.sshs.concurrent

/**
 * Chains an exception to desired type. For example: </p>
 * <p/>
 * <pre>
 * val chainer:ExceptionChainer[SomeException] = new ExceptionChainer[SomeException]()
 * {
 *     def chain(t: Throwable): SomeException = {
 *         t match {
 *         case ex:SomeException => ex
 *         case _ => new  SomeExcepion(t)
 *         }
 *     }
 * };
 * </pre>
 *
 * @param <Z> Throwable type
 */
trait ExceptionChainer[Z <: Throwable] {

  def chain(t: Throwable): Z

}