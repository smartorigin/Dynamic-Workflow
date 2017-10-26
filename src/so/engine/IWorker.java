package so.engine;

import java.util.concurrent.CompletableFuture;

/**
 * Define the concept of a worker, takes something IN return OUT
 * 
 * @author marc-alexandre.blanchard
 *
 * @param <IN> Input type
 * @param <OUT> Output type
 */
public interface IWorker<IN,OUT> {
    /**
     * Process in put async to produce OUT stored in a ProcessResult
     * @param input
     * @return 
     */
	public abstract CompletableFuture<ProcessResult<OUT>> process(IN input);
}
