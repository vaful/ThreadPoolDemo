import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: WuYifei
 * Date: 2017/7/21
 * Time: 17:16
 */
public class JsonResponse<T> implements Serializable {
    public static final int RETURN_SUCCESS = 1;
    public static final int RETURN_FAILURE = 0;
    public static final String error = "{\"returnCode\":0,\"returnMsg\":\"error\"}";
    public static final String success = "{\"returnCode\":1,\"returnMsg\":\"success\"}";
    private static final long serialVersionUID = 3617154470380165248L;
    private int returnCode;
    private String returnMsg;
    private T content;

    public JsonResponse() {
    }

    public JsonResponse(int returnCode, String returnMsg, T content) {
        super();
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
        this.content = content;
    }

    public JsonResponse(int returnCode, String returnMsg) {
        super();
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String toString() {
        String json = JsonMapper.getDefault().toJson(this);
        return json == null ? JsonResponse.error : json;

    }
}
