package love.madohomu.madomagiarchive_fe_java.net;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import love.madohomu.madomagiarchive_fe_java.models.*;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

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

    private static <T> void HttpGet(String path, Consumer<T> callback, Type type) {
        Request request = newRequestBuilderWithBaseUrl(path).build();
        HttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() == null) return;
                T result = gson.fromJson(response.body().string(), type);
                callback.accept(result);
            }
        });
    }

    public static void getFileList(Consumer<List<FileItem>> callback) {
        HttpGet("files", callback, new TypeToken<List<FileItem>>() {
        }.getType());
    }
}
