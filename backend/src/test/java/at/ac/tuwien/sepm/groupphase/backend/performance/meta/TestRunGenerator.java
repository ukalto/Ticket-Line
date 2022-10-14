package at.ac.tuwien.sepm.groupphase.backend.performance.meta;

public class TestRunGenerator {
  private final EndpointLoader loader;

  public TestRunGenerator(EndpointLoader loader) {
    this.loader = loader;
  }

  public String generateTestClass() {
    var file = new StringBuilder();

    file.append("import java.util.List;\n\n");
    file.append("public class EndpointCaller {\n");

    var endpoints = this.loader.endpoints();

    endpoints.forEach(
        endpoint ->
            file.append(
                String.format(
                    """

        \t// Java Method: %s
        \t// Path: %s %s
        \t// Returns: %s
        \tpublic void %s() {
        \t\t// TODO: implement
        \t}
        """,
                    endpoint.javaMethodName(),
                    endpoint.httpMethod(),
                    endpoint.path(),
                    endpoint.returns(),
                    endpoint.testMethodName())));

    var calls = new StringBuilder();

    for (int i = 0; i < endpoints.size(); i++) {
      String delimiter = ",\n";
      if ((i + 1) == endpoints.size()) {
        delimiter = "";
      }
      calls.append("\t\t\t() -> this." + endpoints.get(i).testMethodName() + "()" + delimiter);
    }

    file.append(
        String.format(
            """

      \tpublic List<Runnable> asRunnables() {
      \t\treturn List.of(
      %s
      \t\t);
      \t}
      """,
            calls.toString()));

    file.append("}\n");

    return file.toString();
  }

  public static void main(String[] args) {
    var loader = new EndpointLoader("at.ac.tuwien.sepm.groupphase.backend.endpoint");
    var generator = new TestRunGenerator(loader);

    var file = generator.generateTestClass();

    System.out.println(file);
  }
}
