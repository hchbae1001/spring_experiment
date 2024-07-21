package bae.springexperiment;

public record CommonResponse<T>(
        int status,
        boolean success,
        T data
) {
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(200, true, data);
    }
}
