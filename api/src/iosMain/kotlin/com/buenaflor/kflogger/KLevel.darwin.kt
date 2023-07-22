package com.buenaflor.kflogger

public actual class KLevel {
  public actual companion object {
    /**
     * OFF is a special LogKLevel that can be used to turn off logging. This LogKLevel is
     * initialized to <CODE>Integer.MAX_VALUE</CODE>.
     */
    public actual val OFF: KLevel
      get() = TODO("Not yet implemented")

    /**
     * SEVERE is a message LogKLevel indicating a serious failure.
     *
     * In general SEVERE messages should describe events that are of considerable importance and
     * which will prevent normal program execution. They should be reasonably intelligible to end
     * users and to system administrators. This LogKLevel is initialized to <CODE>1000</CODE>.
     */
    public actual val SEVERE: KLevel
      get() = TODO("Not yet implemented")

    /**
     * WARNING is a message LogKLevel indicating a potential problem.
     *
     * In general WARNING messages should describe events that will be of interest to end users or
     * system managers, or which indicate potential problems. This LogKLevel is initialized to
     * <CODE>900</CODE>.
     */
    public actual val WARNING: KLevel
      get() = TODO("Not yet implemented")

    /**
     * INFO is a message LogKLevel for informational messages.
     *
     * Typically INFO messages will be written to the console or its equivalent. So the INFO
     * LogKLevel should only be used for reasonably significant messages that will make sense to end
     * users and system administrators. This LogKLevel is initialized to <CODE>800</CODE>.
     */
    public actual val INFO: KLevel
      get() = TODO("Not yet implemented")

    /**
     * CONFIG is a message LogKLevel for static configuration messages.
     *
     * CONFIG messages are intended to provide a variety of static configuration information, to
     * assist in debugging problems that may be associated with particular configurations. For
     * example, CONFIG message might include the CPU type, the graphics depth, the GUI
     * look-and-feel, etc. This LogKLevel is initialized to <CODE>700</CODE>.
     */
    public actual val CONFIG: KLevel
      get() = TODO("Not yet implemented")

    /**
     * FINE is a message LogKLevel providing tracing information.
     *
     * All of FINE, FINER, and FINEST are intended for relatively detailed tracing. The exact
     * meaning of the three LogKLevels will vary between subsystems, but in general, FINEST should
     * be used for the most voluminous detailed output, FINER for somewhat less detailed output, and
     * FINE for the lowest volume (and most important) messages.
     *
     * In general the FINE LogKLevel should be used for information that will be broadly interesting
     * to developers who do not have a specialized interest in the specific subsystem.
     *
     * FINE messages might include things like minor (recoverable) failures. Issues indicating
     * potential performance problems are also worth logging as FINE. This LogKLevel is initialized
     * to <CODE>500</CODE>.
     */
    public actual val FINE: KLevel
      get() = TODO("Not yet implemented")

    /**
     * FINER indicates a fairly detailed tracing message. By default logging calls for entering,
     * returning, or throwing an exception are traced at this LogKLevel. This LogKLevel is
     * initialized to <CODE>400</CODE>.
     */
    public actual val FINER: KLevel
      get() = TODO("Not yet implemented")

    /**
     * FINEST indicates a highly detailed tracing message. This LogKLevel is initialized to
     * <CODE>300</CODE>.
     */
    public actual val FINEST: KLevel
      get() = TODO("Not yet implemented")

    /**
     * ALL indicates that all messages should be logged. This LogKLevel is initialized to
     * <CODE>Integer.MIN_VALUE</CODE>.
     */
    public actual val ALL: KLevel
      get() = TODO("Not yet implemented")
  }
}
