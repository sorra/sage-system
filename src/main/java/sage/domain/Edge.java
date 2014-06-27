package sage.domain;

public class Edge {
  public final EdgeType type;
  public final Long edgeId;

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

  public static enum EdgeType {
    NONE,
    BEFORE,
    AFTER
  }
}
