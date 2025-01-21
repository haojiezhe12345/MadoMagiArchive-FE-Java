package love.madohomu.madomagiarchive_fe_java.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import love.madohomu.madomagiarchive_fe_java.Utils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.function.Consumer;

public class Http {
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
        return new Request.Builder().url(ApiClient.BaseUrl + path);
    }

    private static void AsyncRequest(Request request, Consumer<Response> callback) {
        HttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> Utils.showAlert("Request failed due to a network error:\n%s".formatted(e), Alert.AlertType.ERROR));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                callback.accept(response);
            }
        });
    }

    private static <T> void RequestJson(Request request, Consumer<T> callback, Type type) {
        AsyncRequest(request, response -> {
            String responseBody;
            try {
                responseBody = response.body() != null ? response.body().string() : null;
            } catch (IOException e) {
                Platform.runLater(() -> Utils.showAlert("Failed to read response body:\n%s".formatted(e), Alert.AlertType.ERROR));
                return;
            }
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
        });
    }

    public static <T> void Get(String path, Consumer<T> callback, Type type) {
        Request request = newRequestBuilderWithBaseUrl(path).build();
        RequestJson(request, callback, type);
    }

    public static void GetFile(String path, Consumer<InputStream> callback) {
        Request request = newRequestBuilderWithBaseUrl(path).build();
        AsyncRequest(request, response -> {
            callback.accept(response.body() != null ? response.body().byteStream() : null);
        });
    }

    public static <T> void PostJson(String path, Object obj, Type typeObj, Consumer<T> callback, Type type) {
        RequestBody body = RequestBody.create(gson.toJson(obj, typeObj), MediaType.parse("application/json; charset=utf-8"));
        Request request = newRequestBuilderWithBaseUrl(path)
                .post(body)
                .build();
        RequestJson(request, callback, type);
    }

    public static <T> void PutJson(String path, Object obj, Type typeObj, Consumer<T> callback, Type type) {
        RequestBody body = RequestBody.create(gson.toJson(obj, typeObj), MediaType.parse("application/json; charset=utf-8"));
        Request request = newRequestBuilderWithBaseUrl(path)
                .put(body)
                .build();
        RequestJson(request, callback, type);
    }

    public static <T> void PostFile(String path, File file, Consumer<T> callback, Type type) {
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("files", file.getName(), RequestBody.create(file, null))
                .build();
        Request request = newRequestBuilderWithBaseUrl(path)
                .post(formBody)
                .build();
        RequestJson(request, callback, type);
    }

    public static <T> void Delete(String path, Consumer<T> callback, Type type) {
        Request request = newRequestBuilderWithBaseUrl(path).delete().build();
        RequestJson(request, callback, type);
    }
}
