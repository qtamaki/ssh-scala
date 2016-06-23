/*
 * Copyright (C)2009 - SSHJ Contributors
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
package com.qtamaki.concurrent

import java.util.Collection;

object ErrorDeliveryUtil {

    def alertPromises[V, T <: Throwable](x:Throwable, promises:Promise[V,T]*) {
        promises.foreach { p =>
            p.deliverError(x);
        }
    }

//    def alertPromises[V, T <: Throwable](x:Throwable, promises:Seq[_ <: Promise[V,T]]) {
//        promises.foreach { p =>
//            p.deliverError(x);
//        }
//    }
//
    def alertEvents[T <: Throwable](x:Throwable, events:Event[T]*) {
      events.foreach { e =>
        e.deliverError(x);
      }
    }

//    def alertEvents[T <: Throwable](x:Throwable, events:Seq[_ <: Event[T]]) {
//      events.foreach { e =>
//        e.deliverError(x);
//      }
//    }
//
}
