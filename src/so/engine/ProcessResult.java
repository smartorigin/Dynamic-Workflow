package so.engine;

/**
 * Represent the result of a process
 * @author marc_alx
 * @param <T> Type of the result
 */
public class ProcessResult<T> {
    private T result;
    private ResultStatusEnum status;
    private Exception exception;

    /**
     * Create a new process result
     * @param result Result of the process (null if status is failed)
     * @param status Status of the execution
     * @param exception null or Exception if status is failed
     */
    public ProcessResult(T result, ResultStatusEnum status, Exception exception) {
        this.result = result;
        this.status = status;
        this.exception=exception;
    }

    /**
     * return the result of the process, null if status == failed
     * @return
     */
    public T getResult() {
        return result;
    }

    /**
     * return status of the process
     * @return
     */
    public ResultStatusEnum getStatus() {
        return status;
    }

    /**
     * return the exception associated with status failed, null if success
     * @return
     */
    public Exception getException() {
        return exception;
    }
}
