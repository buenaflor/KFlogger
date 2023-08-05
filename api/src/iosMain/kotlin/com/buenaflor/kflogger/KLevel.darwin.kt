package com.buenaflor.kflogger

public actual open class KLevel
protected constructor(public val name: String, public val value: Int) {
  public actual companion object {
    /**
     * OFF is a special KLevel that can be used to turn off logging. This KLevel is initialized to
     * <CODE>Integer.MAX_VALUE</CODE>.
     */
    public actual val OFF: KLevel
      get() = KLevel("OFF", Int.MAX_VALUE)

    /**
     * SEVERE is a message KLevel indicating a serious failure.
     *
     * In general SEVERE messages should describe events that are of considerable importance and
     * which will prevent normal program execution. They should be reasonably intelligible to end
     * users and to system administrators. This KLevel is initialized to <CODE>1000</CODE>.
     */
    public actual val SEVERE: KLevel
      get() = KLevel("SEVERE", 1000)

    /**
     * WARNING is a message KLevel indicating a potential problem.
     *
     * In general WARNING messages should describe events that will be of interest to end users or
     * system managers, or which indicate potential problems. This KLevel is initialized to
     * <CODE>900</CODE>.
     */
    public actual val WARNING: KLevel
      get() = KLevel("WARNING", 900)

    /**
     * INFO is a message KLevel for informational messages.
     *
     * Typically INFO messages will be written to the console or its equivalent. So the INFO KLevel
     * should only be used for reasonably significant messages that will make sense to end users and
     * system administrators. This KLevel is initialized to <CODE>800</CODE>.
     */
    public actual val INFO: KLevel
      get() = KLevel("INFO", 800)

    /**
     * CONFIG is a message KLevel for static configuration messages.
     *
     * CONFIG messages are intended to provide a variety of static configuration information, to
     * assist in debugging problems that may be associated with particular configurations. For
     * example, CONFIG message might include the CPU type, the graphics depth, the GUI
     * look-and-feel, etc. This KLevel is initialized to <CODE>700</CODE>.
     */
    public actual val CONFIG: KLevel
      get() = KLevel("CONFIG", 700)

    /**
     * FINE is a message KLevel providing tracing information.
     *
     * All of FINE, FINER, and FINEST are intended for relatively detailed tracing. The exact
     * meaning of the three KLevels will vary between subsystems, but in general, FINEST should be
     * used for the most voluminous detailed output, FINER for somewhat less detailed output, and
     * FINE for the lowest volume (and most important) messages.
     *
     * In general the FINE KLevel should be used for information that will be broadly interesting to
     * developers who do not have a specialized interest in the specific subsystem.
     *
     * FINE messages might include things like minor (recoverable) failures. Issues indicating
     * potential performance problems are also worth logging as FINE. This KLevel is initialized to
     * <CODE>500</CODE>.
     */
    public actual val FINE: KLevel
      get() = KLevel("FINE", 500)

    /**
     * FINER indicates a fairly detailed tracing message. By default logging calls for entering,
     * returning, or throwing an exception are traced at this KLevel. This KLevel is initialized to
     * <CODE>400</CODE>.
     */
    public actual val FINER: KLevel
      get() = KLevel("FINER", 400)

    /**
     * FINEST indicates a highly detailed tracing message. This KLevel is initialized to
     * <CODE>300</CODE>.
     */
    public actual val FINEST: KLevel
      get() = KLevel("FINEST", 300)

    /**
     * ALL indicates that all messages should be logged. This KLevel is initialized to
     * <CODE>Integer.MIN_VALUE</CODE>.
     */
    public actual val ALL: KLevel
      get() = KLevel("ALL", Int.MIN_VALUE)
  }
}
