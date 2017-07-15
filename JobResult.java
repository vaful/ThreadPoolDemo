

/**
 * Created with IntelliJ IDEA.
 * User: WuYifei
 * Date: 2017/2/8
 * Time: 10:06
 */
public class JobResult {
    public static final int RETURN_SUCCESS = 1;
    public static final int RETURN_FAIL = 0;

    private int returnCode;
    private String returnMsg;

    public JobResult() {
    }

    public JobResult(int returnCode, String returnMsg) {
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
    }

    public boolean isSuccess() {
        return getReturnCode() == RETURN_SUCCESS;
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
}
