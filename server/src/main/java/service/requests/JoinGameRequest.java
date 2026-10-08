package service.requests;

public record JoinGameRequest(String authToken, String gameID, String playerColor) {
}
