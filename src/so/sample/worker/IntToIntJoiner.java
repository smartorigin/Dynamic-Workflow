package so.sample.worker;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import so.engine.IWorker;
import so.engine.ProcessResult;
import so.engine.ResultStatusEnum;

/**
 * A woker that join a list of int into a single int
 * 
 *  Basically it sums values of the given list
 * 
 * @author marc_alx
 */
public class IntToIntJoiner implements IWorker<List<Integer>,Integer>{

    @Override
    public CompletableFuture<ProcessResult<Integer>> process(List<Integer> input) {
        CompletableFuture<ProcessResult<Integer>> res = CompletableFuture.supplyAsync(()->{
            int tmp = 0;
            for(Integer i : (List<Integer>)input){
                tmp+=i;
            }
            return new ProcessResult<>(tmp,ResultStatusEnum.SUCCESS,null);
		});
		return res;
    }

}
