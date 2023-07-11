package com.buenaflor.kflogger

public actual interface LoggingScopeProvider {
  public actual val currentScope: KLoggingScope?
}
