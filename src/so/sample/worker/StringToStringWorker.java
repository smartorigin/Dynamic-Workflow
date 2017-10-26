package so.sample.worker;

import java.util.concurrent.CompletableFuture;
import so.engine.IWorker;
import so.engine.ProcessResult;
import so.engine.ResultStatusEnum;

/**
 * A simple worker that prepend a string to another
 * @author marc_alx
 */
public class StringToStringWorker implements IWorker<String, String>{
    public CompletableFuture<ProcessResult<String>> process(String input) {
        CompletableFuture<ProcessResult<String>> res = CompletableFuture.supplyAsync(()->{
            return new ProcessResult<>("OK : "+input,ResultStatusEnum.SUCCESS,null);
		});
		return res;
    }
    
}
