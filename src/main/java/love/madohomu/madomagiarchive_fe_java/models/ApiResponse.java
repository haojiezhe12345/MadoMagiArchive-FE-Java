package love.madohomu.madomagiarchive_fe_java.models;

public class ApiResponse<T> {
    public int code;
    public String message;
    public T data;
}
