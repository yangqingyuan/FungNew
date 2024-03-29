package org.fungo.common_network;

import org.fungo.common_core.utils.Logger;
import org.fungo.common_core.utils.Utils;
import org.fungo.common_network.api.A8ApiService;
import org.fungo.common_network.api.ActivityApiService;
import org.fungo.common_network.api.AdApiService;
import org.fungo.common_network.api.NowTvApiService;
import org.fungo.common_network.api.UserApiService;
import org.fungo.common_network.host.ServiceHostManager;
import org.fungo.common_network.interceptor.BasicParamsInterceptor;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author yqy
 * @create 19-7-16
 * @Describe
 */
public class HttpUtils {

    private static volatile HttpUtils httpUtils;
    private static final String TAG = "HttpUtils";

    /**
     * 单利模式
     *
     * @return
     */
    public static HttpUtils getInstance() {
        if (httpUtils == null) {
            synchronized (HttpUtils.class) {
                if (httpUtils == null) {
                    httpUtils = new HttpUtils();
                }
            }
        }
        return httpUtils;
    }

    private HttpUtils() {
    }

    private OkHttpClient okHttpClient;

    /**
     * 获取httpclick
     *
     * @return
     */
    private OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            //定制OkHttp
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient
                    .Builder();
            /**
             * OkHttp进行添加拦截器
             */
            httpClientBuilder.addInterceptor(getLoggerInterceptor());
            httpClientBuilder.addInterceptor(getBasicParamsInterceptor());
            okHttpClient = httpClientBuilder.build();
        }
        return okHttpClient;
    }

    /**
     * long
     *
     * @return
     */
    private Interceptor getLoggerInterceptor() {
        //日志显示级别
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BASIC;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Logger.e(TAG + " message=" + message);
            }
        });
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
    }

    private String apk_package_name = "test";
    private String enterPrise = "0";
    private String appMark = "android";

    /**
     * 公共参数
     *
     * @return
     */
    private Interceptor getBasicParamsInterceptor() {
        BasicParamsInterceptor basicParamsInterceptor = new BasicParamsInterceptor.Builder()
                .addQueryParam(NetWorkConstants.REQ_PARAMS_KEY_VERSION, Utils.getVersionNameTiny())
                .addQueryParam(NetWorkConstants.REQ_PARAMS_KEY_PLATFORM, NetWorkConstants.REQ_PARAMS_VALUE_PLATFROM)
                .addQueryParam(NetWorkConstants.REQ_PARAMS_KEY_APPX, NetWorkConstants.REQ_PARAMS_VALUE_APPX)
                .addQueryParam(NetWorkConstants.REQ_PARAMS_KEY_APPPN, apk_package_name)
                .addQueryParam(NetWorkConstants.REQ_PARAMS_KEY_ENTERPRISE, enterPrise)
                .addQueryParam(NetWorkConstants.REQ_PARAMS_KEY_CHANNEL, Utils.getChannel())
                .addQueryParam(NetWorkConstants.REQ_PARAMS_KEY_MARKET, String.valueOf(Utils.getMarketInfo()))
                .addQueryParam(NetWorkConstants.REQ_PARAMS_KEY_OS_VERSION, String.valueOf(Utils.getReleaseVersionNumber()))
                .addQueryParam(NetWorkConstants.REQ_PARAMS_KEY_DEVICE_MODEL, String.valueOf(Utils.getDeviceModel()))
                .addQueryParam(NetWorkConstants.REQ_PARAMS_KEY_DEVICE_CODE, Utils.getDeviceId())
                .addQueryParam(NetWorkConstants.REQ_PARAMS_VALUE_UDID, Utils.getDeviceInfoWithoutMD5())
                .addQueryParam(NetWorkConstants.REQ_PARAMS_VALUE_ANDROID_ID, Utils.getAndroidId())
                .addQueryParam(NetWorkConstants.REQ_PARAMS_VALUE_SOURCE, appMark).build();
        return basicParamsInterceptor;
    }

    /**
     * user接口
     *
     * @return
     */
    private static Retrofit UserRetrofit;

    private Retrofit getUserApiRetrofit() {
        if (UserRetrofit == null) {
            UserRetrofit = new Retrofit.Builder()
                    .baseUrl(ServiceHostManager.getInstall().getUser_service() + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getOkHttpClient())//使用自己创建的OkHttp
                    .build();
        }
        return UserRetrofit;
    }


    /**
     * user接口
     *
     * @return
     */
    private static Retrofit NowTvRetrofit;

    private Retrofit getNowTvApiRetrofit() {
        if (NowTvRetrofit == null) {
            NowTvRetrofit = new Retrofit.Builder()
                    .baseUrl(ServiceHostManager.getInstall().getNowtv_service() + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(getOkHttpClient())//使用自己创建的OkHttp
                    .build();
        }
        return NowTvRetrofit;
    }


    /**
     * ad接口
     *
     * @return
     */
    private static Retrofit AdRetrofit;

    private Retrofit getAdApiRetrofit() {
        if (AdRetrofit == null) {
            AdRetrofit = new Retrofit.Builder()
                    .baseUrl(ServiceHostManager.getInstall().getAd_service() + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getOkHttpClient())//使用自己创建的OkHttp
                    .build();
        }
        return AdRetrofit;
    }

    /**
     * ad接口
     *
     * @return
     */
    private static Retrofit ActivityRetrofit;

    private Retrofit getActivityApiRetrofit() {
        if (ActivityRetrofit == null) {
            ActivityRetrofit = new Retrofit.Builder()
                    .baseUrl(ServiceHostManager.getInstall().getActivity_service() + "/")
                    .addConverterFactory(GsonConverterFactory.create())//json转换成JavaBean
                    .client(getOkHttpClient())//使用自己创建的OkHttp
                    .build();
        }
        return ActivityRetrofit;
    }

    /**
     * ad接口
     *
     * @return
     */
    private static Retrofit A8Retrofit;

    private Retrofit getA8ApiRetrofit() {
        if (A8Retrofit == null) {
            A8Retrofit = new Retrofit.Builder()
                    .baseUrl(ServiceHostManager.getInstall().getA8_service() + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getOkHttpClient())//使用自己创建的OkHttp
                    .build();
        }
        return A8Retrofit;
    }


    /**
     * 获取接口
     * User接口
     *
     * @return
     */

    public UserApiService getUserApiService() {
        return getUserApiRetrofit().create(UserApiService.class);
    }

    /**
     * 获取接口
     * nowTv接口
     *
     * @return
     */
    public NowTvApiService getNowTvApiService() {
        return getNowTvApiRetrofit().create(NowTvApiService.class);
    }


    /**
     * 获取接口
     * ad接口
     *
     * @return
     */
    public AdApiService getAdApiService() {
        return getAdApiRetrofit().create(AdApiService.class);
    }


    /**
     * 获取接口
     * activity接口
     *
     * @return
     */
    public ActivityApiService getActivityApiService() {
        return getActivityApiRetrofit().create(ActivityApiService.class);
    }


    /**
     * 获取接口
     * A8接口
     *
     * @return
     */
    public A8ApiService getA8ApiService() {
        return getA8ApiRetrofit().create(A8ApiService.class);
    }


}
