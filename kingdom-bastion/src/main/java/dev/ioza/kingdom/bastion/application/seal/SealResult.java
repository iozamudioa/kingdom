package dev.ioza.kingdom.bastion.application.seal;

public record SealResult(

        String accessToken,

        String tokenType,

        long expiresIn

) {

    public SealResult {

        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("SealResult accessToken cannot be null or blank");
        }

        if (tokenType == null || tokenType.isBlank()) {
            throw new IllegalArgumentException("SealResult tokenType cannot be null or blank");
        }

        if (expiresIn <= 0) {
            throw new IllegalArgumentException("SealResult expiresIn must be greater than 0");
        }

    }

    public static SealResult bearer(String accessToken, long expiresIn) {
        return new SealResult(accessToken, "Bearer", expiresIn);
    }

}
