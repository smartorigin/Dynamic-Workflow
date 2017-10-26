package so.engine;

/**
 * Represent an execution context
 * 
 * This means an IWorker + its input parameter
 * 
 * @author marc_alx
 * @param <S> The type of execution context
 */
public class ExecutionContext<S> {
    /**
     * The worker that will be execute
     */
    private IWorker worker;
    
    /**
     * The input parameter of the executed worker
     */
    private S parameter;
    
    /**
     * Creates a new execution context
     * @param worker
     * @param parameter 
     */
    public ExecutionContext(IWorker worker, S parameter){
        this.worker=worker;
        this.parameter=parameter;
    }

    /**
     * @return the worker
     */
    public IWorker getWorker() {
        return worker;
    }

    /**
     * @return the parameter
     */
    public S getParameter() {
        return parameter;
    }
}
