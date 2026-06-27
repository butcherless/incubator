package shared

case class Pagination(page: Int, pageSize: Int) {
  def offset: Int = (page - 1) * pageSize
}

object Pagination {
  val default: Pagination                         = Pagination(page = 1, pageSize = 20)
  def apply(page: Int, pageSize: Int): Pagination =
    new Pagination(math.max(1, page), math.min(100, math.max(1, pageSize)))
}
