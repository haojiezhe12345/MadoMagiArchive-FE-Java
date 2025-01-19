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

    private static <T> void Request(Request request, Consumer<T> callback, Type type) {
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

    public static <T> void Get(String path, Consumer<T> callback, Type type) {
        Request request = newRequestBuilderWithBaseUrl(path).build();
        Request(request, callback, type);
    }

    public static <T> void PostFile(String path, File file, Consumer<T> callback, Type type) {
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("files", file.getName(), RequestBody.create(file, null))
                .build();
        Request request = newRequestBuilderWithBaseUrl(path)
                .post(formBody)
                .build();
        Request(request, callback, type);
    }

    public static <T> void Delete(String path, Consumer<T> callback, Type type) {
        Request request = newRequestBuilderWithBaseUrl(path).delete().build();
        Request(request, callback, type);
    }
}
