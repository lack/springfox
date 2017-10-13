package springfox.documentation.schema;

import static springfox.documentation.schema.ResolvedTypes.resolvedTypeSignature;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ViewProviderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class JacksonJsonViewProvider implements ViewProviderPlugin {
  
  private static final Logger LOG = LoggerFactory.getLogger(JacksonJsonViewProvider.class);

  private final TypeResolver typeResolver;

  @Autowired
  public JacksonJsonViewProvider(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  @Override
  public Optional<ResolvedType> viewFor(ResolvedType type, ResolvedMethodParameter parameter) {
    return viewFor(type, parameter.findAnnotation(JsonView.class));
  }
  
  @Override
  public Optional<ResolvedType> viewFor(ResolvedType type, RequestMappingContext context) {
    return viewFor(type, context.findAnnotation(JsonView.class));
  }
  
  @Override
  public Optional<ResolvedType> viewFor(ResolvedType type, OperationContext context) {
    return viewFor(type, context.findAnnotation(JsonView.class));
  }
  
  private Optional<ResolvedType> viewFor(ResolvedType type, Optional<JsonView> annotation) {
    Optional<ResolvedType> view = Optional.absent();
    if (annotation.isPresent()) {
      Class<?>[] views = ((JsonView)(annotation.get())).value();
      view = Optional.of(typeResolver.resolve(views[0]));
      LOG.debug("Found view {} for type {}", resolvedTypeSignature(view.get()).or("<null>"), resolvedTypeSignature(type).or("<null>"));
    }
    return view;
  }

  @Override
  public boolean applyView(ResolvedType activeview, ResolvedField field) {
    final Class<?> activeView = activeview.getErasedType();
    if (activeView != null) {
      Optional<? extends Annotation> annotation = FluentIterable.from(field.getAnnotations())
          .filter(JsonView.class).first();
      if (!annotation.isPresent()) {
        return true;
      }
      final Class<?>[] typeviews =  ((JsonView)(annotation.get())).value();
      int i = 0, len = typeviews.length;
      for (; i < len; ++i) {
        if (typeviews[i].isAssignableFrom(activeView)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean applyView(ResolvedType activeView, Class<?>[] typeViews) {
    // TODO Auto-generated method stub
    return false;
  }
}