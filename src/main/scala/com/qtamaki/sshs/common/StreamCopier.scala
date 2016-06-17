package com.qtamaki.sshs.common

import net.schmizz.concurrent.Event;
import net.schmizz.concurrent.ExceptionChainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

object StreamCopier {
  trait Listener {
    def reportProgress(transferred: Long): Unit
  }
  private val NULL_LISTENER: Listener = new Listener() {
    override def reportProgress(transferred: Long) {}
  };

}

class StreamCopier(val in: InputStream, val out: OutputStream) {
  import StreamCopier._

  private val log: Logger = LoggerFactory.getLogger(classOf[StreamCopier]);

  private var listener: Listener = NULL_LISTENER;

  private var bufSize: Int = 1;
  private var keepFlushing: Boolean = true;
  private var length: Long = -1;

  def bufSize(bufSize: Int): StreamCopier = {
    this.bufSize = bufSize;
    return this;
  }

  def keepFlushing(keepFlushing: Boolean): StreamCopier = {
    this.keepFlushing = keepFlushing;
    return this;
  }

  def listener(listener: Listener): StreamCopier = {
    if (listener == null) this.listener = NULL_LISTENER
    else this.listener = listener
    return this;
  }

  def length(length: Long): StreamCopier = {
    this.length = length;
    return this;
  }

  def spawn(name: String): Event[IOException] = {
    return spawn(name, false);
  }

  def spawnDaemon(name: String): Event[IOException] = {
    return spawn(name, true);
  }

  private def spawn(name: String, daemon: Boolean): Event[IOException] = {
    val doneEvent: Event[IOException] =
      new Event("copyDone", new ExceptionChainer[IOException]() {
        override def chain(t: Throwable): IOException = {
          t match {
            case t: IOException => t
            case _ => new IOException(t)
          }
        }
      });

    new Thread() {
      {
        setName(name);
        setDaemon(daemon);
      }

      override def run() {
        try {
          log.debug(s"Will copy from ${in} to ${out}");
          copy();
          log.debug(s"Done copying from ${in}");
          doneEvent.set();
        } catch {
          case ioe: IOException =>
            log.error(s"In pipe from ${in} to ${out}: ${ioe}");
            doneEvent.deliverError(ioe);
        }
      }
    }.start();
    return doneEvent;
  }

  def copy(): Long = {
    val buf = new Array[Byte](bufSize)
    var count: Long = 0;
    var read: Int = 0;

    val startTime = System.currentTimeMillis();

    if (length == -1) {
      while ((read = in.read(buf)) != -1)
        count = write(buf, count, read);
    } else {
      def _read = { read = in.read(buf, 0, Math.min(bufSize, (length - count).toInt)); read != -1 }
      while (count < length && _read) {
        count = write(buf, count, read);
      }
    }

    if (!keepFlushing)
      out.flush();

    val timeSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
    val sizeKiB = count / 1024.0;
    log.debug(s"${sizeKiB} KiB transferred in ${timeSeconds} seconds (${sizeKiB / timeSeconds} KiB/s)");

    if (length != -1 && read == -1)
      throw new IOException("Encountered EOF, could not transfer " + length + " bytes");

    return count;
  }

  def write(buf: Array[Byte], count: Long, read: Int): Long = {
    out.write(buf, 0, read);
    val x = count + read;
    if (keepFlushing)
      out.flush();
    listener.reportProgress(x);
    return x;
  }

}
