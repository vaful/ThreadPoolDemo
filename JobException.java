package Concurrencey;

/**
 * Created with IntelliJ IDEA.
 * User: WuYifei
 * Date: 2017/2/8
 * Time: 10:05
 */
public class JobException extends Exception{
    private Integer errorCode;

    public JobException() {
    }

    public JobException(String message) {
        super(message);
    }

    public JobException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobException(Integer errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public JobException(Throwable cause) {
        super(cause);
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

}
