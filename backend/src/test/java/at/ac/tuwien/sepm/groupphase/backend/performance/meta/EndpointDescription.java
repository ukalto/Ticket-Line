package at.ac.tuwien.sepm.groupphase.backend.performance.meta;

public record EndpointDescription(
    String javaMethodName, String httpMethod, String path, String returns) {
  private String normalizePath() {
    String normalized = String.copyValueOf(this.path.toCharArray());
    normalized = normalized.replace("{", "");
    normalized = normalized.replace("}", "");

    while (normalized.contains("-")) {
      var index = normalized.indexOf("-");
      var followingCharacter = Character.toString(normalized.charAt(index + 1)).toUpperCase();
      var chars = normalized.toCharArray();
      chars[index + 1] = followingCharacter.toCharArray()[0];
      normalized = new String(chars);
      normalized = normalized.replaceFirst("-", "");
    }

    return normalized;
  }

  public String testMethodName() {
    String sep = "_";
    if (path.startsWith("/")) {
      sep = "";
    }

    return this.httpMethod.toLowerCase() + sep + normalizePath().replace("/", "_");
  }
}
