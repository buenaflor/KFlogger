package com.giancarlobuenaflor.kflogger

public actual interface KLoggingScopeProvider {
  /**
   * Returns the current scope (most likely via global or thread local state) or `null` if there is
   * no current scope.
   */
  public actual fun getCurrentScope(): KLoggingScope?
}
