import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.type.JavaType;
import test.HttpException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: WuYifei
 * Date: 2017/7/21
 * Time: 17:02
 */
public abstract class AbstractHttpOperate {
    public static final int BODY_SECURE_MODE = 0;
    public static final int PARAMS_SECURE_MODE = 1;
    public static final String SECURITY = "security";
    public static final String CERT_ID = "certId";
    public static final String SECURE_MODE = "SECURE_MODE";

    /**
     * 执行HTTP请求任务
     *
     * @param httpJob
     * @param <X>
     * @return
     * @throws HttpException
     */
    protected static <X> X execute(HttpJob<X> httpJob) throws HttpException {
        try {
            JobExecutor.executeInCurrentThread(httpJob);
            return httpJob.getResult();
        } catch (JobException e) {
            throw new HttpException(e.getErrorCode(), e.getMessage(), e.getCause());
        }
    }

    protected void setHttpPostParams(HttpPost httpPost, List<NameValuePair> nvps) throws UnsupportedEncodingException {
        if (CollectionUtils.isEmpty(nvps)) {
            nvps = new ArrayList<NameValuePair>();
        }
        List<String> paramList = new ArrayList<String>();
        nvps.add(new BasicNameValuePair(CERT_ID, getCertId()));
        for (NameValuePair nameValuePair : nvps) {
            paramList.add(nameValuePair.getName() + "=" + nameValuePair.getValue());
        }
        Collections.sort(paramList);
        StringBuilder stringBuilder = new StringBuilder();
        for (String param : paramList) {
            stringBuilder.append(param);
        }
        String security = generateSecurityContent(stringBuilder.toString());
        nvps.add(new BasicNameValuePair(SECURITY, security));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, "UTF-8");
        httpPost.setEntity(entity);
    }


    protected void setHttpPostParam(HttpPost httpPost, String name, String value) throws UnsupportedEncodingException {
//        Assert.notNull(name, "参数名不能为空");
//        Assert.notNull(value, "参数值不能为空");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(name, value));
        setHttpPostParams(httpPost, nvps);
    }

    protected String generateSecurityContent(String content) {
        byte[] base64 = Base64.encodeBase64(DigestUtils.sha(content + getCertKey()));
        return new String(base64, Charset.forName("utf8"));
    }

    protected void handleResponse(HttpResponse response) throws HttpException, IOException {
        JsonResponse jsonResponse = handleJsonResponse(response);
        if (JsonResponse.RETURN_SUCCESS != jsonResponse.getReturnCode()) {
            throw new HttpException(jsonResponse.getReturnCode(), jsonResponse.getReturnMsg());
        }
    }

    protected JsonResponse handleJsonResponse(HttpResponse response) throws HttpException, IOException {
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() == 200) {
            HttpEntity httpEntity = response.getEntity();
            JsonMapper mapper = JsonMapper.getDefault();
            return mapper.fromJson(httpEntity.getContent(), JsonResponse.class);
        } else {
            throw new HttpException(statusLine.getStatusCode(), String.format("HTTP请求发生错误,状态码:%d,错误原因:%s", statusLine.getStatusCode(), statusLine.getReasonPhrase()));
        }
    }


    protected <T> T handleResponse(HttpResponse response, Class<T> clazz) throws HttpException, IOException {
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() == 200) {
            HttpEntity httpEntity = response.getEntity();
            JsonMapper mapper = JsonMapper.getDefault();
            JsonResponse<T> jsonResponse = mapper.fromJson(httpEntity.getContent(), mapper.constructParametricType(JsonResponse.class, clazz));
            if (jsonResponse == null) {
                jsonResponse = mapper.fromJson(httpEntity.getContent(), mapper.constructParametricType(String.class));
            }
            if (jsonResponse.getReturnCode() == JsonResponse.RETURN_SUCCESS) {
                return jsonResponse.getContent();
            } else {
                throw new HttpException(jsonResponse.getReturnCode(), jsonResponse.getReturnMsg());
            }
        } else {
            throw new HttpException(statusLine.getStatusCode(), String.format("HTTP请求发生错误,状态码:%d,错误原因:%s", statusLine.getStatusCode(), statusLine.getReasonPhrase()));
        }
    }

    protected <T> T handleResponse(HttpResponse response, JavaType javaType) throws HttpException, IOException {
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() == 200) {
            HttpEntity httpEntity = response.getEntity();
            JsonMapper mapper = JsonMapper.getDefault();
            JsonResponse<T> jsonResponse = mapper.fromJson(httpEntity.getContent(), mapper.constructParametricType(JsonResponse.class, javaType));
            if (jsonResponse == null) {
                jsonResponse = mapper.fromJson(httpEntity.getContent(), mapper.constructParametricType(String.class));
            }
            if (jsonResponse.getReturnCode() == JsonResponse.RETURN_SUCCESS) {
                return jsonResponse.getContent();
            } else {
                throw new HttpException(jsonResponse.getReturnCode(), jsonResponse.getReturnMsg());
            }
        } else {
            throw new HttpException(statusLine.getStatusCode(), String.format("HTTP请求发生错误,状态码:%d,错误原因:%s", statusLine.getStatusCode(), statusLine.getReasonPhrase()));
        }
    }

    protected <X> List<X> handleListResponse(HttpResponse response, Class clazz) throws HttpException, IOException {
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() == 200) {
            HttpEntity httpEntity = response.getEntity();
            JsonMapper mapper = JsonMapper.getDefault();
            JavaType pageType = mapper.constructParametricType(List.class, clazz);
            JavaType javaType = mapper.constructParametricType(JsonResponse.class, pageType);
            JsonResponse<List<X>> jsonResponse = mapper.fromJson(httpEntity.getContent(), javaType);
            if (jsonResponse == null) {
                pageType = mapper.constructParametricType(List.class, String.class);
                javaType = mapper.constructParametricType(JsonResponse.class, pageType);
                jsonResponse = mapper.fromJson(httpEntity.getContent(), javaType);
            }
            if (jsonResponse.getReturnCode() == JsonResponse.RETURN_SUCCESS) {
                return jsonResponse.getContent();
            } else {
                throw new HttpException(jsonResponse.getReturnCode(), jsonResponse.getReturnMsg());
            }
        } else {
            throw new HttpException(statusLine.getStatusCode(), String.format("HTTP请求发生错误,状态码:%d,错误原因:%s", statusLine.getStatusCode(), statusLine.getReasonPhrase()));
        }
    }

    protected Map handleMapResponse(HttpResponse response, Class key, Class content) throws HttpException, IOException {
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() == 200) {
            HttpEntity httpEntity = response.getEntity();
            JsonMapper mapper = JsonMapper.getDefault();
            JavaType sub = mapper.constructParametricType(content);
            JavaType keyType = mapper.constructParametricType(key);
            JavaType mapType = mapper.constructParametricType(Map.class, new JavaType[]{keyType, sub});
            JavaType javaType = mapper.constructParametricType(JsonResponse.class, mapType);
            JsonResponse<Map> jsonResponse = mapper.fromJson(httpEntity.getContent(), javaType);
            if (jsonResponse.getReturnCode() == JsonResponse.RETURN_SUCCESS) {
                return jsonResponse.getContent();
            } else {
                throw new HttpException(jsonResponse.getReturnCode(), jsonResponse.getReturnMsg());
            }
        } else {
            throw new HttpException(statusLine.getStatusCode(), String.format("HTTP请求发生错误,状态码:%d,错误原因:%s", statusLine.getStatusCode(), statusLine.getReasonPhrase()));
        }
    }

    protected Map handleMapListResponse(HttpResponse response, Class key, Class content) throws HttpException, IOException {
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() == 200) {
            HttpEntity httpEntity = response.getEntity();
            JsonMapper mapper = JsonMapper.getDefault();
            JavaType sub = mapper.constructParametricType(List.class, content);
            JavaType keyType = mapper.constructParametricType(key);
            JavaType mapType = mapper.constructParametricType(Map.class, new JavaType[]{keyType, sub});
            JavaType javaType = mapper.constructParametricType(JsonResponse.class, mapType);
            JsonResponse<Map> jsonResponse = mapper.fromJson(httpEntity.getContent(), javaType);
            if (jsonResponse.getReturnCode() == JsonResponse.RETURN_SUCCESS) {
                return jsonResponse.getContent();
            } else {
                throw new HttpException(jsonResponse.getReturnCode(), jsonResponse.getReturnMsg());
            }
        } else {
            throw new HttpException(statusLine.getStatusCode(), String.format("HTTP请求发生错误,状态码:%d,错误原因:%s", statusLine.getStatusCode(), statusLine.getReasonPhrase()));
        }
    }

    public abstract String getCertId();

    public abstract String getCertKey();
}
