package sage.domain.commons;

public class Edge {
  public static final int FETCH_SIZE = 20;

  public final EdgeType type;
  public final Long edgeId;
  public int limitStart = 0;
  public int limitCount = 20;

  private Edge(EdgeType type, Long edgeId) {
    this.type = type;
    this.edgeId = edgeId;
  }

  public static Edge none() {
    return new Edge(EdgeType.NONE, 0L);
  }

  public static Edge before(Long edgeId) {
    return new Edge(EdgeType.BEFORE, edgeId);
  }

  public static Edge after(Long edgeId) {
    return new Edge(EdgeType.AFTER, edgeId);
  }

  public enum EdgeType {
    NONE,
    BEFORE,
    AFTER
  }
}
