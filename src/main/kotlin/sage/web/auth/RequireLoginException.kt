package sage.web.auth

class RequireLoginException : RuntimeException {

  constructor() : super() {
  }

  constructor(message: String, cause: Throwable, enableSuppression: Boolean,
              writableStackTrace: Boolean) : super(message, cause, enableSuppression, writableStackTrace) {
  }

  constructor(message: String, cause: Throwable) : super(message, cause) {
  }

  constructor(message: String) : super(message) {
  }

  constructor(cause: Throwable) : super(cause) {
  }

  companion object {
    private val serialVersionUID = -7612510838405643774L
  }

}
