package at.ac.tuwien.sepm.groupphase.backend.performance.meta;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

public class EndpointLoader {
  private final String packageName;

  public EndpointLoader(String packageName) {
    this.packageName = packageName;
  }

  private Class getClass(String className, String packageName) {
    try {
      return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private Set<Class> findAllClassesUsingClassLoader() {
    var stream =
        ClassLoader.getSystemClassLoader()
            .getResourceAsStream(this.packageName.replaceAll("[.]", "/"));
    var reader = new BufferedReader(new InputStreamReader(stream));

    return reader
        .lines()
        .filter(line -> line.endsWith(".class"))
        .map(line -> getClass(line, this.packageName))
        .collect(Collectors.toSet());
  }

  private EndpointDescription methodToEndpointDescription(Method method) {
    var annotations = method.getDeclaredAnnotations();
    var javaMethodName = method.getName();
    var httpAnnotation =
        Arrays.stream(annotations)
            .filter(
                annotation ->
                    annotation instanceof GetMapping
                        || annotation instanceof PostMapping
                        || annotation instanceof DeleteMapping
                        || annotation instanceof PutMapping)
            .findFirst()
            .orElse(null);

    if (httpAnnotation == null) {
      return null;
    }

    String httpMethod = "";
    String path = "";

    if (httpAnnotation instanceof GetMapping) {
      var getAnnotation = (GetMapping) httpAnnotation;
      var paths = getAnnotation.value();
      if (paths.length == 0) {
        return null;
      }

      httpMethod = "GET";
      path = getAnnotation.value()[0];
    }

    if (httpAnnotation instanceof PostMapping) {
      var postAnnotation = (PostMapping) httpAnnotation;
      var paths = postAnnotation.value();
      if (paths.length == 0) {
        return null;
      }

      httpMethod = "POST";
      path = postAnnotation.value()[0];
    }

    if (httpAnnotation instanceof DeleteMapping) {
      var deleteAnnotation = (DeleteMapping) httpAnnotation;
      var paths = deleteAnnotation.value();
      if (paths.length == 0) {
        return null;
      }

      httpMethod = "DELETE";
      path = deleteAnnotation.value()[0];
    }

    if (httpAnnotation instanceof PutMapping) {
      var putAnnotation = (PutMapping) httpAnnotation;
      var paths = putAnnotation.value();
      if (paths.length == 0) {
        return null;
      }

      httpMethod = "PUT";
      path = putAnnotation.value()[0];
    }

    return new EndpointDescription(
        javaMethodName, httpMethod, path, method.getGenericReturnType().toString());
  }

  public List<EndpointDescription> endpoints() {
    final var endpoints = new ArrayList<EndpointDescription>();
    final var allClasses = this.findAllClassesUsingClassLoader();

    for (var clazz : allClasses) {
      for (var method : clazz.getDeclaredMethods()) {
        var mapped = this.methodToEndpointDescription(method);
        if (mapped != null) {
          endpoints.add(mapped);
        }
      }
    }

    return endpoints;
  }
}
