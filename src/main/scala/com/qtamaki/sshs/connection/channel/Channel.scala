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
package com.qtamaki.sshs.connection.channel

import com.qtamaki.sshs.common.ErrorNotifiable;
import com.qtamaki.sshs.common.SSHPacketHandler;
import com.qtamaki.sshs.connection.ConnectionException;
import com.qtamaki.sshs.transport.TransportException;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/** A channel is the basic medium for application-layer data on top of an SSH transport. */
trait Channel
        extends Closeable with SSHPacketHandler with ErrorNotifiable {

    /** Direct channels are those that are initiated by us. */
    trait Direct
            extends Channel {

        /**
         * Request opening this channel from remote end.
         *
         * @throws OpenFailException   in case the channel open request was rejected
         * @throws ConnectionException other connection-layer error
         * @throws TransportException  error writing packets etc.
         */
        def open()

    }

    /** Forwarded channels are those that are initiated by the server. */
    trait Forwarded
            extends Channel {

        /**
         * Confirm {@code CHANNEL_OPEN} request.
         *
         * @throws TransportException error sending confirmation packet
         */
        def confirm()

        /** @return the IP of where the forwarded connection originates. */
        def getOriginatorIP():String

        /** @return port from which the forwarded connection originates. */
        def getOriginatorPort():Int

        /**
         * Indicate rejection to remote end.
         *
         * @param reason  indicate {@link OpenFailException.Reason reason} for rejection of the request
         * @param message indicate a message for why the request is rejected
         *
         * @throws TransportException error sending rejection packet
         */
        def reject(reason:OpenFailException.Reason, message:String)

    }


    /** Close this channel. */
    override def close()

    /**
     * @return whether auto-expansion of local window is set.
     *
     * @see #setAutoExpand(boolean)
     */
    def getAutoExpand():Boolean

    /** @return the channel ID */
    def getID():Int

    /** @return the {@code InputStream} for this channel. */
    def getInputStream():InputStream

    /** @return the maximum packet size that we have specified. */
    def getLocalMaxPacketSize():Int

    /** @return the current local window size. */
    def getLocalWinSize():Long

    /** @return an {@code OutputStream} for this channel. */
    def getOutputStream():OutputStream

    /** @return the channel ID at the remote end. */
    def getRecipient():Int

    /** @return the maximum packet size as specified by the remote end. */
    def getRemoteMaxPacketSize():Int

    /** @return the current remote window size. */
    def getRemoteWinSize():Long

    /** @return the channel type identifier. */
    def getType():String

    /** @return whether the channel is open. */
    def isOpen():Boolean

    /**
     * Set whether local window should automatically expand when data is received, irrespective of whether data has been
     * read from that stream. This is useful e.g. when a remote command produces a lot of output that would fill the
     * local window but you are not interested in reading from its {@code InputStream}.
     *
     * @param autoExpand whether local windows should automatically expand
     */
    def setAutoExpand(autoExpand:Boolean);

    def join()

    def join(timeout:Long, unit:TimeUnit)

}

