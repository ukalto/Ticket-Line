package at.ac.tuwien.sepm.groupphase.backend.performance.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

public class TicketLineClient {
  private final HttpClient httpClient;
  private final Credentials credentials;
  private final String host;
  private final String basePath;
  private final int port;
  private final ObjectMapper mapper;

  private String authHeader;

  public TicketLineClient(
      final HttpClient httpClient,
      final Credentials credentials,
      final String host,
      final int port,
      final String basePath) {
    this.httpClient = httpClient;
    this.credentials = credentials;
    this.host = host;
    this.port = port;
    this.basePath = basePath;
    this.mapper = new ObjectMapper();
  }

  private void authenticate() {
    final var uri =
        UriComponentsBuilder.newInstance()
            .scheme("http")
            .host(this.host)
            .port(this.port)
            .path(this.basePath + "authentication")
            .build()
            .toUri();

    try {
      final var encoded = this.mapper.writeValueAsString(this.credentials);
      final var request =
          HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(encoded)).build();

      final var response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() < 400) {
        this.authHeader = response.body();
        return;
      }

      throw new IllegalStateException(
          String.format(
              "Authentication request returned illegal status %d with message %s",
              response.statusCode(), response.body()));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private HttpRequest attachAuthorization(HttpRequest.Builder builder) {
    if (this.authHeader != null) {
      return builder.header("Authorization", this.authHeader).build();
    }

    return builder.build();
  }

  private HttpResponse<String> safeSend(HttpRequest.Builder builder) {
    try {
      final var response =
          this.httpClient.send(
              this.attachAuthorization(builder), HttpResponse.BodyHandlers.ofString());

      final var triggerAuthenticationCodes =
          List.of(HttpStatus.UNAUTHORIZED.value(), HttpStatus.FORBIDDEN.value());
      if (triggerAuthenticationCodes.contains(response.statusCode())) {
        this.authenticate();

        return this.httpClient.send(
            this.attachAuthorization(builder), HttpResponse.BodyHandlers.ofString());
      }

      System.out.println(
          String.format("Status: %d\nBody: %s\n", response.statusCode(), response.body()));

      return response;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private UriComponentsBuilder expandQueryParams(
      final UriComponentsBuilder builder, final Map<String, Object> queryParams) {
    for (final var key : queryParams.keySet()) {
      final var value = queryParams.get(key);

      builder.queryParam(key, value);
    }

    return builder;
  }

  public <O> O doGet(final String path, final Class<O> responseClass) {
    return this.doGet(path, responseClass, Map.of());
  }

  public <O> O doGet(
      final String path, final Class<O> responseClass, final Map<String, Object> queryParams) {
    final var uri =
        UriComponentsBuilder.newInstance()
            .scheme("http")
            .host(this.host)
            .port(this.port)
            .path(this.basePath + path);

    this.expandQueryParams(uri, queryParams);

    final var request = HttpRequest.newBuilder(uri.build().toUri()).GET();

    final var response = this.safeSend(request);

    try {
      if (response.body().length() == 0) {
        return null;
      }

      return this.mapper.readValue(response.body(), responseClass);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public <I, O> O doPost(final String path, I body, Class<O> responseType) {
    final var uri =
        UriComponentsBuilder.newInstance()
            .scheme("http")
            .host(this.host)
            .port(this.port)
            .path(this.basePath + path)
            .build()
            .toUri();
    try {
      final var encoded = this.mapper.writeValueAsString(body);
      final var request =
          HttpRequest.newBuilder(uri)
              .header("content-type", "application/json")
              .POST(HttpRequest.BodyPublishers.ofString(encoded));

      final var response = this.safeSend(request);

      return this.mapper.readValue(response.body(), responseType);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public <I, O> O doPut(final String path, I body, Class<O> responseType) {
    final var uri =
        UriComponentsBuilder.newInstance()
            .scheme("http")
            .host(this.host)
            .port(this.port)
            .path(this.basePath + path)
            .build()
            .toUri();
    try {
      final var encoded = this.mapper.writeValueAsString(body);
      final var request =
          HttpRequest.newBuilder(uri)
              .header("content-type", "application/json")
              .PUT(HttpRequest.BodyPublishers.ofString(encoded));

      final var response = this.safeSend(request);

      return this.mapper.readValue(response.body(), responseType);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
