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
package com.qtamaki.sshs.connection

import com.qtamaki.concurrent.Promise;
import com.qtamaki.keepalive.KeepAlive;
import com.qtamaki.sshs.common.SSHPacket
import com.qtamaki.sshs.connection.channel.Channel;
import com.qtamaki.sshs.connection.channel.OpenFailException;
import com.qtamaki.sshs.connection.channel.forwarded.ForwardedChannelOpener;
import com.qtamaki.sshs.transport.Transport;
import com.qtamaki.sshs.transport.TransportException;

/** Connection layer of the SSH protocol. Refer to RFC 254. */
trait Connection {

    /**
     * Attach a {@link Channel} to this connection. A channel must be attached to the connection if it is to receive any
     * channel-specific data that is received.
     *
     * @param chan the channel
     */
    def attach(chan:Channel)

    /**
     * Attach a {@link ForwardedChannelOpener} to this connection, which will be delegated opening of any {@code
     * CHANNEL_OPEN} packets {@link ForwardedChannelOpener#getChannelType() for which it is responsible}.
     *
     * @param opener an opener for forwarded channels
     */
    def attach(opener:ForwardedChannelOpener)

    /**
     * Forget an attached {@link Channel}.
     *
     * @param chan the channel
     */
    def forget(chan:Channel)

    /**
     * Forget an attached {@link ForwardedChannelOpener}.
     *
     * @param opener the opener to forget
     */
    def forget(opener:ForwardedChannelOpener)

    /**
     * @param id number of the channel to retrieve
     *
     * @return an attached {@link Channel} of specified channel number, or {@code null} if no such channel was attached
     */
    def get(id:Int):Channel;

    /**
     * Wait for the situation that no channels are attached (e.g., got closed).
     *
     * @throws InterruptedException if the thread is interrupted
     */
    def join()

    /**
     * @param chanType channel type
     *
     * @return an attached {@link ForwardedChannelOpener} of specified channel-type, or {@code null} if no such channel
     *         was attached
     */
    def get(chanType:String):ForwardedChannelOpener

    /** @return an available ID a {@link Channel} can rightfully claim. */
    def nextID():Int

    /**
     * Send an SSH global request.
     *
     * @param name      request name
     * @param wantReply whether a reply is requested
     * @param specifics {@link SSHPacket} containing fields specific to the request
     *
     * @return a {@link com.qtamaki.concurrent.Promise} for the reply data (in case {@code wantReply} is true) which
     *         allows waiting on the reply, or {@code null} if a reply is not requested.
     *
     * @throws TransportException if there is an error sending the request
     */
    def sendGlobalRequest(name:String, wantReply:Boolean, specifics:Array[Byte]):Promise[SSHPacket, ConnectionException]

    /**
     * Send a {@code SSH_MSG_OPEN_FAILURE} for specified {@code Reason} and {@code message}.
     *
     * @param recipient number of the recipient channel
     * @param reason    a reason for the failure
     * @param message   an explanatory message
     *
     * @throws TransportException if there is a transport-layer error
     */
    def sendOpenFailure(recipient:Int, reason:OpenFailException.Reason, message:String)

    /**
     * @return the maximum packet size for the local window this connection recommends to any {@link Channel}'s that ask
     *         for it.
     */
    def getMaxPacketSize():Int

    /**
     * Set the maximum packet size for the local window this connection recommends to any {@link Channel}'s that ask for
     * it.
     *
     * @param maxPacketSize maximum packet size in bytes
     */
    def setMaxPacketSize(maxPacketSize:Int)

    /** @return the size for the local window this connection recommends to any {@link Channel}'s that ask for it. */
    def getWindowSize():Long

    /**
     * Set the size for the local window this connection recommends to any {@link Channel}'s that ask for it.
     *
     * @param windowSize window size in bytes
     */
    def setWindowSize(windowSize:Long)

    /** @return the associated {@link Transport}. */
    def getTransport():Transport

    /**
     * @return the {@code timeout} in milliseconds that this connection uses for blocking operations and recommends to
     *         any {@link Channel other} {@link ForwardedChannelOpener classes} that ask for it.
     */
    def getTimeoutMs():Int

    /**
     * Set the {@code timeout} this connection uses for blocking operations and recommends to any {@link Channel other}
     * {@link ForwardedChannelOpener classes} that ask for it.
     *
     * @param timeout timeout in milliseconds
     */
    def setTimeoutMs(timeout:Int)

    /**
     * @return The configured {@link com.qtamaki.keepalive.KeepAlive} mechanism.
     */
    def getKeepAlive():KeepAlive
}