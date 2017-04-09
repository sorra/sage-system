package sage.util

object PaginationLogic {
  fun pagesCount(size: Int, totalRecords: Int): Int {
    val division = totalRecords / size
    return if (totalRecords % size == 0) division else division + 1
  }
}