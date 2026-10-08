package service.requests;

// carries request info which requires a single auth token
public record AuthTokenRequest(String authToken) {
}
