package com.regan.demo;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import okhttp3.*;
import okio.BufferedSink;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OkHttpDemo {

    private final OkHttpClient client = new OkHttpClient();
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final String IMGUR_CLIENT_ID = "...";

    public String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String post(String url, String json) throws IOException {

        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    String bowlingJson(String player1, String player2) {
        return "{'winCondition':'HIGH_SCORE',"
                + "'name':'Bowling',"
                + "'round':4,"
                + "'lastSaved':1367702411696,"
                + "'dateStarted':1367702378785,"
                + "'players':["
                + "{'name':'" + player1 + "','history':[10,8,6,7,8],'color':-13388315,'total':39},"
                + "{'name':'" + player2 + "','history':[6,10,5,10,10],'color':-48060,'total':41}"
                + "]}";
    }

    /**
     * 下载一个文件，打印他的响应头，以string形式打印响应体。
     * 响应体的 string() 方法对于小文档来说十分方便、高效。但是如果响应体太大（超过1MB），
     * 应避免适应 string()方法 ，因为他会将把整个文档加载到内存中。
     * 对于超过1MB的响应body，应使用流的方式来处理body。
     * @throws Exception
     */
    public void syncGet() throws Exception {
        Request request = new Request.Builder()
                .url("https://publicobject.com/helloworld.txt")
                .build();

//        Response response = client.newCall(request).execute();//同步
//        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }

            System.out.println(response.body().string());
        }

    }

    /**
     * 在一个工作线程中下载文件，当响应可读时回调Callback接口。读取响应时会阻塞当前线程。
     * OkHttp现阶段不提供异步api来接收响应体。
     * @throws Exception
     */
    public void AsyncGet() throws Exception {
        Request request = new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .build();

        //异步，需要设置一个回调接口
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    System.out.println(responseBody.string());
                }
            }
        });
    }

    /**
     * 使用header(name,value)来设置HTTP头的唯一值，如果请求中已经存在响应的信息那么直接替换掉。
     * 使用addHeader(name,value)来补充新值，如果请求头中已经存在name的name-value，
     *                          那么还会继续添加，请求头中便会存在多个name相同而value不同的“键值对”。
     * 使用header(name)读取唯一值或多个值的最后一个值
     * 使用headers(name)获取所有值
     * @throws Exception
     */
    public void accessHeaders() throws Exception {
        Request request = new Request.Builder()
                .url("https://api.github.com/repos/square/okhttp/issues")
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println("Server: " + response.header("Server"));
            System.out.println("Date: " + response.header("Date"));
            System.out.println("Vary: " + response.headers("Vary"));
        }
    }

    /**
     * 由于整个请求体(request body)同时位于内存中，因此请避免使用此API发布较大（大于1 MiB）的文档
     * @throws Exception
     */
    public void postString() throws Exception {

        String postBody = ""
                + "Releases\n"
                + "--------\n"
                + "\n"
                + " * _1.0_ May 6, 2013\n"
                + " * _1.1_ June 15, 2013\n"
                + " * _1.2_ August 11, 2013\n";

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(response.body().string());
        }
    }


    /**
     * 以流的方式POST提交请求体。请求体的内容由流写入产生。这个例子是流直接写入Okio的BufferedSink。
     * 你的程序可能会使用OutputStream，你可以使用BufferedSink.outputStream()来获取。
     * @throws Exception
     */
    public void postStreaming() throws Exception {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new RequestBody() {
            @Override public MediaType contentType() {
                return MEDIA_TYPE_MARKDOWN;
            }

            @Override public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("Numbers\n");
                sink.writeUtf8("-------\n");
                for (int i = 2; i <= 997; i++) {
                    sink.writeUtf8(String.format(" * %s = %s\n", i, factor(i)));
                }
            }

            private String factor(int n) {
                for (int i = 2; i < n; i++) {
                    int x = n / i;
                    if (x * i == n) return factor(x) + " × " + i;
                }
                return Integer.toString(n);
            }
        };

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(response.body().string());
        }
    }


    public void postFile() throws Exception {
        OkHttpClient client = new OkHttpClient();
        File file = new File("README.md");

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(response.body().string());
        }
    }

    /**
     * 使用FormBody.Builder来构建和HTML标签相同效果的请求体。键值对将使用一种HTML兼容形式的URL编码来进行编码
     * @throws Exception
     */
    public void postForm() throws Exception {
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("search", "Jurassic Park")
                .build();
        Request request = new Request.Builder()
                .url("https://en.wikipedia.org/w/index.php")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(response.body().string());
        }
    }

    /**
     * MultipartBuilder可以构建复杂的请求体，与HTML文件上传形式兼容。
     * 多块请求体中每块请求都是一个请求体，可以定义自己的请求头。
     * 这些请求头可以用来描述这块请求，例如他的Content-Disposition。
     * 如果Content-Length和Content-Type可用的话，他们会被自动添加到请求头中
     * @throws Exception
     */
    public void postMultipart() throws Exception {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "Square Logo")
                .addFormDataPart("image", "logo-square.png",
                        RequestBody.create(MEDIA_TYPE_PNG, new File("website/static/logo-square.png")))
                .build();

        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .url("https://api.imgur.com/3/image")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(response.body().string());
        }
    }

    /**
     * Moshi是一个便捷的API，用于在JSON和Java对象之间进行转换。这里我们使用它来解码来自GitHub API的JSON响应。
     * 请注意，ResponseBody.charStream()使用Content-Type响应头来决定在解码响应主体时使用哪个字符集。
     * 如果没有指定字符集，它默认为UTF-8
     */
    private final Moshi moshi = new Moshi.Builder().build();
    private final JsonAdapter<Gist> gistJsonAdapter = moshi.adapter(Gist.class);
    public void parseResponseWithMoshi() throws Exception {
        Request request = new Request.Builder()
                .url("https://api.github.com/gists/c2a7c39532239ff261be")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            Gist gist = gistJsonAdapter.fromJson(response.body().source());

            for (Map.Entry<String, GistFile> entry : gist.files.entrySet()) {
                System.out.println(entry.getKey());
                System.out.println(entry.getValue().content);
            }
        }
    }
    static class Gist { Map<String, GistFile> files; }
    static class GistFile { String content; }

    /**
     * 响应缓存使用HTTP headers作为配置。
     * web服务器配置的响应时间来配置缓存的缓存，如Cache-Control: max-age=9600
     * 有缓存头可强制缓存响应，强制网络响应，或强制网络响应通过条件GET进行验证
     * 要防止使用缓存的响应，请使用CacheControl.FORCE_NETWORK。
     * 要阻止它使用网络，请使用CacheControl.FORCE_CACHE。
     * 如果您使用FORCE_CACHE并且响应需要网络，OkHttp将返回504 Unsatisfiable Request响应。
     * @throws Exception
     */
    public void cacheResponse(File cacheDirectory) throws Exception {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(cacheDirectory, cacheSize);
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();
        Request request = new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .build();

        String response1Body;
        try (Response response1 = client.newCall(request).execute()) {
            if (!response1.isSuccessful()) throw new IOException("Unexpected code " + response1);

            response1Body = response1.body().string();
            System.out.println("Response 1 response:          " + response1);
            System.out.println("Response 1 cache response:    " + response1.cacheResponse());
            System.out.println("Response 1 network response:  " + response1.networkResponse());
        }

        String response2Body;
        try (Response response2 = client.newCall(request).execute()) {
            if (!response2.isSuccessful()) throw new IOException("Unexpected code " + response2);

            response2Body = response2.body().string();
            System.out.println("Response 2 response:          " + response2);
            System.out.println("Response 2 cache response:    " + response2.cacheResponse());
            System.out.println("Response 2 network response:  " + response2.networkResponse());
        }

        System.out.println("Response 2 equals Response 1? " + response1Body.equals(response2Body));
    }

    /**
     * 使用Call.cancel（）立即停止正在进行的呼叫。 如果线程当前正在写请求或读取响应，它将接收到IOException。
     * 使用此选项可在不再需要呼叫时节省网络; 例如当您的用户导航离开应用程序时。 同步和异步调用都可以取消
     */
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    public void cancelCall () throws Exception {
        Request request = new Request.Builder()
                .url("http://httpbin.org/delay/2") // This URL is served with a 2 second delay.
                .build();

        final long startNanos = System.nanoTime();
        final Call call = client.newCall(request);

        // Schedule a job to cancel the call in 1 second.
        executor.schedule(new Runnable() {
            @Override public void run() {
                System.out.printf("%.2f Canceling call.%n", (System.nanoTime() - startNanos) / 1e9f);
                call.cancel();
                System.out.printf("%.2f Canceled call.%n", (System.nanoTime() - startNanos) / 1e9f);
            }
        }, 1, TimeUnit.SECONDS);

        System.out.printf("%.2f Executing call.%n", (System.nanoTime() - startNanos) / 1e9f);
        try (Response response = call.execute()) {
            System.out.printf("%.2f Call was expected to fail, but completed: %s%n",
                    (System.nanoTime() - startNanos) / 1e9f, response);
        } catch (IOException e) {
            System.out.printf("%.2f Call failed as expected: %s%n",
                    (System.nanoTime() - startNanos) / 1e9f, e);
        }
    }

    public void configureTimeouts() throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url("http://httpbin.org/delay/2") // This URL is served with a 2 second delay.
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response completed: " + response);
        }
    }

    /**
     * 所有HTTP客户端配置都存在于OkHttpClient中，包括代理设置，超时和高速缓存。
     * 当您需要更改单个调用的配置时，请调用OkHttpClient.newBuilder（）。
     * 这将返回与原始客户端共享相同连接池，调度程序和配置的构建器。
     * 在下面的示例中，我们使用500毫秒超时创建一个请求，另一个请求使用3000毫秒超时
     * @throws Exception
     */
    public void perCallSettings() throws Exception {
        Request request = new Request.Builder()
                .url("http://httpbin.org/delay/1") // This URL is served with a 1 second delay.
                .build();

        // Copy to customize OkHttp for this request.
        OkHttpClient client1 = client.newBuilder()
                .readTimeout(500, TimeUnit.MILLISECONDS)
                .build();
        try (Response response = client1.newCall(request).execute()) {
            System.out.println("Response 1 succeeded: " + response);
        } catch (IOException e) {
            System.out.println("Response 1 failed: " + e);
        }

        // Copy to customize OkHttp for this request.
        OkHttpClient client2 = client.newBuilder()
                .readTimeout(3000, TimeUnit.MILLISECONDS)
                .build();
        try (Response response = client2.newCall(request).execute()) {
            System.out.println("Response 2 succeeded: " + response);
        } catch (IOException e) {
            System.out.println("Response 2 failed: " + e);
        }
    }

    /**
     * OkHttp可以自动重试未经身份验证的请求。
     * 当响应为401未授权时，将要求Authenticator提供凭据。 实现应构建包含缺少凭据的新请求。
     * 如果没有可用的凭据，则返回null以跳过重试。
     * 使用Response.challenges（）获取任何身份验证挑战的方案和领域。
     * 当完成基本挑战时，使用Credentials.basic（username，password）对请求标头进行编码。
     * @throws Exception
     */
    public void authenticate() throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Override public Request authenticate(Route route, Response response) throws IOException {
                        if (response.request().header("Authorization") != null) {
                            return null; // Give up, we've already attempted to authenticate.
                        }

                        System.out.println("Authenticating for response: " + response);
                        System.out.println("Challenges: " + response.challenges());
                        String credential = Credentials.basic("jesse", "password1");
                        return response.request().newBuilder()
                                .header("Authorization", credential)
                                .build();
                    }
                })
                .build();

        Request request = new Request.Builder()
                .url("http://publicobject.com/secrets/hellosecret.txt")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(response.body().string());
        }

/*
        为了避免在身份验证不起作用时进行许多重试，可以返回null放弃。
        例如，您可能希望在尝试这些确切的凭据时跳过重试：
        if (credential.equals(response.request().header("Authorization"))) {
            return null; // If we already failed with these credentials, don't retry.
        }

        当您达到应用程式定义的尝试上限时，您也可以略过重试：
        if (responseCount(response) >= 3) {
            return null; // If we've failed 3 times, give up.
        }

        以上代码依赖于此responseCount（）方法：
        private int responseCount(Response response) {
            int result = 1;
            while ((response = response.priorResponse()) != null) {
                result++;
            }
            return result;
        }*/
    }

    public static void main(String[] args) throws Exception {
        OkHttpDemo example = new OkHttpDemo();
        String response = example.get("https://raw.github.com/square/okhttp/master/README.md");
        System.out.println(response);

        String json = example.bowlingJson("Jesse", "Jake");
        response = example.post("http://www.roundsapp.com/post", json);
        System.out.println(response);
    }
}
