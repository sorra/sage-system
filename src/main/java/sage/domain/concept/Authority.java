package sage.domain.concept;

public enum Authority {
  USER, TAG_ADMIN, SITE_ADMIN;

  public static boolean isTagAdminOrHigher(Authority authority) {
    return authority == TAG_ADMIN || authority == SITE_ADMIN;
  }
}
