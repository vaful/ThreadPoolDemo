package test;

/**
 * Created with IntelliJ IDEA.
 * User: WuYifei
 * Date: 2017/7/21
 * Time: 16:14
 */
public class HttpException extends Exception {
    private Integer errorCode;

    public HttpException(Integer errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public HttpException(Integer errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public HttpException(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
