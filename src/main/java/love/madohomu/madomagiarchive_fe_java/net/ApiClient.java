package love.madohomu.madomagiarchive_fe_java.net;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import love.madohomu.madomagiarchive_fe_java.models.*;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class ApiClient {
    public static String BaseUrl = "https://haojiezhe12345.top:82/madohomu/archive/api/";

    private static final OkHttpClient HttpClient = new OkHttpClient.Builder()
            .addInterceptor(new HttpInterceptor())
            .build();

    private static final Gson gson = new Gson();

    private static class HttpInterceptor implements Interceptor {

        @NotNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request origRequest = chain.request();
            return chain.proceed(origRequest.newBuilder()
                    .url(BaseUrl + origRequest.url())
                    .addHeader("Token", "")
                    .build());
        }
    }

    private static Request.Builder newRequestBuilder(String path) {
        return new Request.Builder().url(BaseUrl + path);
    }

    private static <T> void HttpGet(String path, Consumer<T> callback) {
        Request request = new Request.Builder().url(path).build();
        HttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                T parsedObject = gson.fromJson(response.body().string(), new TypeToken<T>() {
                }.getType());
                callback.accept(parsedObject);
            }
        });
    }

    public static void getFileList(Consumer<List<FileItem>> callback) {
        HttpGet("files", callback);
    }
}
