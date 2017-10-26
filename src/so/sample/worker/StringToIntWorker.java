package so.sample.worker;


import java.util.concurrent.CompletableFuture;
import so.engine.IWorker;
import so.engine.ProcessResult;
import so.engine.ResultStatusEnum;

/**
 * A worker that convert a string to an int
 * 
 *  Basically 'Integer.parseInt'
 * 
 * @author marc_alx
 */
public class StringToIntWorker implements IWorker<String,Integer>{

    @Override
    public CompletableFuture<ProcessResult<Integer>> process(String input) {
        CompletableFuture<ProcessResult<Integer>> res = CompletableFuture.supplyAsync(()->{
            try{
                return new ProcessResult<>(Integer.parseInt(input),ResultStatusEnum.SUCCESS,null);
            }
            catch(NumberFormatException e){
                return new ProcessResult<>(null,ResultStatusEnum.FAIL,e);
            }
		});
		return res;
    }
    

}
