package love.madohomu.madomagiarchive_fe_java.net;

import com.google.gson.reflect.TypeToken;
import love.madohomu.madomagiarchive_fe_java.models.*;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public class ApiClient {
    public static String BaseUrl = "https://haojiezhe12345.top:82/madohomu/archive/api/";

    public static void getFileList(Consumer<List<FileItem>> callback) {
        Http.Get("files", callback, new TypeToken<List<FileItem>>() { }.getType());
    }

    public static void uploadFile(File file, Consumer<ApiResponse<List<Integer>>> callback) {
        Http.PostFile("files", file, callback, new TypeToken<ApiResponse<List<Integer>>>() { }.getType());
    }

    public static void deleteFile(int id, Consumer<ApiResponse<Object>> callback) {
        Http.Delete("files/%d".formatted(id), callback, new TypeToken<ApiResponse<Object>>() { }.getType());
    }
}
