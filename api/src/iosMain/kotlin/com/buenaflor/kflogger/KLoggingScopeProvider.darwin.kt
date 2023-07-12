package com.buenaflor.kflogger

public actual interface KLoggingScopeProvider {
  public actual val currentScope: KLoggingScope?
}
