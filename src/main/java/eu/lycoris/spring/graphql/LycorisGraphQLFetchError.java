package eu.lycoris.spring.graphql;

import static graphql.Assert.assertNotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.execution.ExecutionPath;
import graphql.language.SourceLocation;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@SuppressWarnings("serial")
public class LycorisGraphQLFetchError implements GraphQLError {

  private final String message;
  private final transient List<Object> path;
  private final Throwable exception;
  private final List<SourceLocation> locations;
  private final transient Map<String, Object> extensions;

  public LycorisGraphQLFetchError(
      MessageSource messageSource,
      ExecutionPath path,
      Throwable exception,
      SourceLocation sourceLocation) {
    this.path = assertNotNull(path).toList();
    this.exception = assertNotNull(exception);
    this.locations = Collections.singletonList(sourceLocation);
    this.extensions = mkExtensions(exception);
    this.message = mkMessage(exception, messageSource);
  }

  private String mkMessage(Throwable exception, MessageSource messageSource) {
    return messageSource.getMessage(
        exception.getMessage(),
        new Object[] {},
        exception.getMessage(),
        LocaleContextHolder.getLocale());
  }

  private Map<String, Object> mkExtensions(Throwable exception) {
    Map<String, Object> ext = null;
    if (exception instanceof GraphQLError) {
      Map<String, Object> map = ((GraphQLError) exception).getExtensions();
      if (map != null) {
        ext = new LinkedHashMap<>();
        ext.putAll(map);
      }
    }
    return ext;
  }

  public Throwable getException() {
    return exception;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public List<SourceLocation> getLocations() {
    return locations;
  }

  @Override
  public List<Object> getPath() {
    return path;
  }

  @Override
  public Map<String, Object> getExtensions() {
    return extensions;
  }

  @Override
  public ErrorType getErrorType() {
    return ErrorType.DataFetchingException;
  }
}
