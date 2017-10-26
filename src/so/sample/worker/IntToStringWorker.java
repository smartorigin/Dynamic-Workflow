package so.sample.worker;


import java.util.concurrent.CompletableFuture;
import so.engine.IWorker;
import so.engine.ProcessResult;
import so.engine.ResultStatusEnum;

/**
 * A worker that prepend a string to an int
 * @author marc_alx
 */
public class IntToStringWorker implements IWorker<Integer,String>{

    @Override
    public CompletableFuture<ProcessResult<String>> process(Integer input) {
        CompletableFuture<ProcessResult<String>> res = CompletableFuture.supplyAsync(()->{
            return new ProcessResult<>("Your value is : "+input.toString(),ResultStatusEnum.SUCCESS,null);
		});
		return res;
    }

}
