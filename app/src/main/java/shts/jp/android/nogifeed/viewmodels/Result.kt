package shts.jp.android.nogifeed.viewmodels

sealed class Result<T>(val inProgress: Boolean) {
    class InProgress<T> : Result<T>(true)
    data class Success<T>(var data: T) : Result<T>(false)
    data class Failure<T>(val errorMessage: String?, val e: Throwable) : Result<T>(false)
}
