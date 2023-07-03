package com.buenaflor.kflogger

actual interface KLoggingScopeProvider

actual val KLoggingScopeProvider.currentScope: KLoggingScope?
  get() = TODO()
