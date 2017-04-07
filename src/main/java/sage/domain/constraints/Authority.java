package sage.domain.constraints;

public enum Authority {
  USER, TAG_ADMIN, SITE_ADMIN;

  public boolean isTagAdminOrHigher() {
    return this == TAG_ADMIN || this == SITE_ADMIN;
  }

  public boolean isSiteAdmin() {
    return this == SITE_ADMIN;
  }
}
