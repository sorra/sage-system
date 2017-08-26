package sage.domain.constraints;

public enum Authority {
  USER, TAG_ADMIN, SITE_ADMIN;

  public boolean canManageTags() {
    return this == TAG_ADMIN || this == SITE_ADMIN;
  }

  public boolean cannotManageTags() {
    return !canManageTags();
  }

  public boolean isSiteAdmin() {
    return this == SITE_ADMIN;
  }
}
