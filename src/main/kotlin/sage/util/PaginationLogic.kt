package sage.util

object PaginationLogic {
  fun pagesCount(size: Int, totalRecords: Long): Int {
    val division = (totalRecords / size).toInt()
    return if (totalRecords % size == 0L) division else division + 1
  }
}