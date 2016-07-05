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
package com.qtamaki.sshs.common

object Factory {

  object Named {

    object Util {
      def create[T](factories: List[Factory.Named[T]], name: String): T = {
        return factories.find(_.getName == name).map(_.create).getOrElse(null).asInstanceOf[T]
      }

      def get[T](factories: List[Factory.Named[T]], name: String): Factory.Named[T] = {
        return factories.find(_.getName == name).getOrElse(null)
      }

      def getNames[T](factories: List[Factory.Named[T]]): List[String] = {
        return factories.map(_.getName)
      }
    }

  }

  trait Named[T] extends Factory[T] {
    def getName: String
  }

}

trait Factory[T] {
  def create: T
}