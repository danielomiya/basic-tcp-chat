package com.github.gwyddie.chat.shared
package logging

import java.util.logging.Logger

trait LoggingMixin {
  lazy val logger: Logger = Logger.getLogger(this.getClass.getName)
}
