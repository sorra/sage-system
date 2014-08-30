package sage.web.context;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

public class JsTemplate {
  private File file;
  private String source;
  private long loadedModifiedTime;

  public JsTemplate(File file) {
    this.file = file;
    load();
  }

  public String getSource() {
    if (ComponentResourceManager.instance().isDevelopmentMode()) {
      if (file.lastModified() != loadedModifiedTime) {
        load();
      }
    }
    return source;
  }

  private void load() {
    try {
      source = new String(Files.readAllBytes(file.toPath()), "UTF-8");
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    loadedModifiedTime = file.lastModified();
  }
}
