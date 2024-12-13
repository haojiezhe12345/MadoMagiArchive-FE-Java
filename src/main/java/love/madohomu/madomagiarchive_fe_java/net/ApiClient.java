package love.madohomu.madomagiarchive_fe_java.net;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import love.madohomu.madomagiarchive_fe_java.Utils;
import love.madohomu.madomagiarchive_fe_java.models.*;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;

public class ApiClient {
    public static String BaseUrl = "https://haojiezhe12345.top:82/madohomu/archive/api/";

    private static final OkHttpClient HttpClient = new OkHttpClient.Builder()
            .addInterceptor(new HttpInterceptor())
            .build();

    private static class HttpInterceptor implements Interceptor {
        @NotNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request origRequest = chain.request();
            return chain.proceed(origRequest.newBuilder()
                    .addHeader("Token", "")
                    .build());
        }
    }

    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
            .create();

    private static Request.Builder newRequestBuilderWithBaseUrl(String path) {
        return new Request.Builder().url(BaseUrl + path);
    }

    private static <T> void HttpRequest(Request request, Consumer<T> callback, Type type) {
        HttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> Utils.showAlert("Request failed due to a network error:\n%s".formatted(e), Alert.AlertType.ERROR));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : null;
                T result;

                if (response.isSuccessful()) {
                    try {
                        result = gson.fromJson(responseBody, type);
                    } catch (Exception e) {
                        Platform.runLater(() -> Utils.showAlert("Could not parse the response text:\n%s".formatted(e), Alert.AlertType.ERROR));
                        return;
                    }

                } else {
                    Platform.runLater(() -> Utils.showAlert("Request failed with code %d:\n%s".formatted(response.code(), responseBody), Alert.AlertType.ERROR));
                    try {
                        result = gson.fromJson(responseBody, type);
                    } catch (Exception ignored) {
                        return;
                    }
                }

                callback.accept(result);
            }
        });
    }

    private static <T> void HttpGet(String path, Consumer<T> callback, Type type) {
        Request request = newRequestBuilderWithBaseUrl(path).build();
        HttpRequest(request, callback, type);
    }

    private static <T> void HttpPostFile(String path, File file, Consumer<T> callback, Type type) {
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("files", file.getName(), RequestBody.create(file, null))
                .build();
        Request request = newRequestBuilderWithBaseUrl(path)
                .post(formBody)
                .build();
        HttpRequest(request, callback, type);
    }

    private static <T> void HttpDelete(String path, Consumer<T> callback, Type type) {
        Request request = newRequestBuilderWithBaseUrl(path).delete().build();
        HttpRequest(request, callback, type);
    }

    public static void getFileList(Consumer<List<FileItem>> callback) {
        HttpGet("files", callback, new TypeToken<List<FileItem>>() { }.getType());
    }

    public static void uploadFile(File file, Consumer<ApiResponse<List<Integer>>> callback) {
        HttpPostFile("files", file, callback, new TypeToken<ApiResponse<List<Integer>>>() { }.getType());
    }

    public static void deleteFile(int id, Consumer<ApiResponse<Object>> callback) {
        HttpDelete("files/%d".formatted(id), callback, new TypeToken<ApiResponse<Object>>() { }.getType());
    }
}
