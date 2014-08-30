package sage.web.context;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public abstract class Json {
  private static final ObjectMapper om = new ObjectMapper();

  public static String json(Object object) {
    try {
      return om.writeValueAsString(object);
    }
    catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public static <T> T object(String json, Class<T> type) {
    try {
      return om.readValue(json, type);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static <T> T object(String json, JavaType jt) {
    try {
      return om.readValue(json, jt);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static TypeFactory typeFactory() {
    return om.getTypeFactory();
  }
}
