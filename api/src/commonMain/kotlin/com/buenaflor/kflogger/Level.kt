package com.buenaflor.kflogger

public expect class Level {
  public companion object {
    /**
     * OFF is a special LogLevel that can be used to turn off logging. This LogLevel is initialized
     * to <CODE>Integer.MAX_public valUE</CODE>.
     */
    public val OFF: Level

    /**
     * SEVERE is a message LogLevel indicating a serious failure.
     *
     * In general SEVERE messages should describe events that are of considerable importance and
     * which will prevent normal program execution. They should be reasonably intelligible to end
     * users and to system administrators. This LogLevel is initialized to <CODE>1000</CODE>.
     */
    public val SEVERE: Level

    /**
     * WARNING is a message LogLevel indicating a potential problem.
     *
     * In general WARNING messages should describe events that will be of interest to end users or
     * system managers, or which indicate potential problems. This LogLevel is initialized to
     * <CODE>900</CODE>.
     */
    public val WARNING: Level

    /**
     * INFO is a message LogLevel for informational messages.
     *
     * Typically INFO messages will be written to the console or its equipublic valent. So the INFO
     * LogLevel should only be used for reasonably significant messages that will make sense to end
     * users and system administrators. This LogLevel is initialized to <CODE>800</CODE>.
     */
    public val INFO: Level

    /**
     * CONFIG is a message LogLevel for static configuration messages.
     *
     * CONFIG messages are intended to provide a variety of static configuration information, to
     * assist in debugging problems that may be associated with particular configurations. For
     * example, CONFIG message might include the CPU type, the graphics depth, the GUI
     * look-and-feel, etc. This LogLevel is initialized to <CODE>700</CODE>.
     */
    public val CONFIG: Level

    /**
     * FINE is a message LogLevel providing tracing information.
     *
     * All of FINE, FINER, and FINEST are intended for relatively detailed tracing. The exact
     * meaning of the three LogLevels will vary between subsystems, but in general, FINEST should be
     * used for the most voluminous detailed output, FINER for somewhat less detailed output, and
     * FINE for the lowest volume (and most important) messages.
     *
     * In general the FINE LogLevel should be used for information that will be broadly interesting
     * to developers who do not have a specialized interest in the specific subsystem.
     *
     * FINE messages might include things like minor (recoverable) failures. Issues indicating
     * potential performance problems are also worth logging as FINE. This LogLevel is initialized
     * to <CODE>500</CODE>.
     */
    public val FINE: Level

    /**
     * FINER indicates a fairly detailed tracing message. By default logging calls for entering,
     * returning, or throwing an exception are traced at this LogLevel. This LogLevel is initialized
     * to <CODE>400</CODE>.
     */
    public val FINER: Level

    /**
     * FINEST indicates a highly detailed tracing message. This LogLevel is initialized to
     * <CODE>300</CODE>.
     */
    public val FINEST: Level

    /**
     * ALL indicates that all messages should be logged. This LogLevel is initialized to
     * <CODE>Integer.MIN_public valUE</CODE>.
     */
    public val ALL: Level
  }
}
