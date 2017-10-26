package so.engine;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Represent a process done by several IWoker
 * 
 * The Workflow could be sequential the results of the first will be passed to the next specified
 * 
 *      WorkFlow w1 = WorkFlow.startSequential(PUT_YOUR_WORKER_HERE).appendSequential(PUT_YOUR_WORKER_HERE).appendSequential(PUT_YOUR_WORKER_HERE);
 *      w1.process(12);
 * 
 * Workflow could also be parallel specify like that :
 *        WorkFlow.startParallel(YOUR_PARAM,YOUR_WORKER)
 *                .appendParrallel(YOUR_PARAM,YOUR_WORKER) 
 *                .endWithJoiner(YOUR_WORKER);
 *        WorkFlow.process(null);
 * 
 * As WorkFlow is IWorker a WorkFlow can include a WorkFlow
 * 
 * @author marc_alx
 * @param <IN> Input type
 * @param <OUT> Output type
 */
public class WorkFlow<IN, OUT> implements IWorker<IN,OUT>{
    /**
     * Stores workers that will be launch sequentially
     */
    private List<IWorker<?, ?>> workers;
    
    /**
     * Stores a list of executions that will be launch in parrallel
     */
    private final List<ExecutionContext> parallelExecutions;
    
    /**
     * The joiner that will join results of parallel execution
     */
    private IWorker<List<?>,OUT> joiner;

    /**
     * Create a new WorFlow
     */
    public WorkFlow() {
        workers=new ArrayList<>();
        parallelExecutions=new ArrayList<>();
    }

    /**
     * Specify the worker that start a sequential workflow
     * @param <IN> Input type
     * @param <TMP> Temporary type
     * @param worker
     * @return A workflow to chain with 'appendSequential'
     */
    public static <IN, TMP> WorkFlow<IN, TMP> startSequential(IWorker<IN, TMP> worker) {
        WorkFlow<IN, TMP> chain = new WorkFlow<>();
        chain.workers = Collections.<IWorker<?, ?>>singletonList(worker);
        return chain;
    }

    /**
     * Append a worker to a sequential workflow
     * 
     * @param <NEXT> Next type (ie new OUT)
     * @param pipe
     * @return A workflow to chain with 'appendSequential'
     */
    public <NEXT> WorkFlow<IN, NEXT> appendSequential(IWorker<OUT, NEXT> pipe) {
        WorkFlow<IN, NEXT> chain = new WorkFlow<>();
        chain.workers = new ArrayList<>(workers);
        chain.workers.add(pipe);
        return chain;
    }
    
    /**
     * Specify the start of a parallel workflow 
     * 
     * as it will be launch in parallel later specify parameter also
     * 
     * (one line syntax)
     * 
     * @param <IN> Input type
     * @param <TMP> Temporary type
     * @param param Value that will be passed to the given worker when workflow launch
     * @param worker Worker that will be executed in parallel
     * @return A workflow to chain with 'appendParrallel'
     */
    public static <IN, TMP> WorkFlow<IN, TMP> startParallel(IN param,IWorker<IN,TMP> worker) {
        WorkFlow<IN, TMP> chain = new WorkFlow<>();
        chain.parallelExecutions.add(new ExecutionContext<>(worker,param));
        return chain;
    }
    
    /**
     * Specify a new parallel worker that will be launch in parallel of existing ones
     * 
     * (one line syntax)
     * 
     * @param <IN> Input type
     * @param <TMP> Temporary type
     * @param param Value that will be passed to the given worker when workflow launch
     * @param worker Worker that will be executed in parallel
     * @return A workflow to chain with 'appendParrallel'
     */
    public <IN, TMP> WorkFlow<IN, TMP> appendParallel(IN param,IWorker<IN,TMP> worker){
        parallelExecutions.add(new ExecutionContext<>(worker,param));
        return (WorkFlow<IN, TMP>)this;
    }
    
    /**
     * Specify the worker that will join results of parrallel process
     * @param joiner the joiner
     * @return Worklow that could be launch via 'process(null)'
     * 
     * (one line syntax)
     */
    public WorkFlow<IN,OUT> endWithJoiner(IWorker<List<?>,OUT> joiner){
        this.joiner=(IWorker<List<?>,OUT>)joiner;
        return this;
    }
    
    /**
     * Same as 'appendParrallel' but not mean't to be called in chain
     * @param <TMP> Temporary type
     * @param param Value that will be passed to the given worker when workflow launch
     * @param worker Worker that will be executed in parallel
     * 
     * (multiple line syntax)
     */
    public <TMP> void addParallelProcess(IN param,IWorker<IN,TMP> worker){
        parallelExecutions.add(new ExecutionContext<>(worker,param));
    }
    
    /**
     * Same as 'endWithJoiner' but not mean't to be called in chain
     * @param joiner the joiner
     * 
     * (multiple line syntax)
     */
    public void setJoiner(IWorker<List<?>,OUT> joiner){
        this.joiner=(IWorker<List<?>,OUT>)joiner;
    }

    /**
     * Launch workflow 
     * 
     * NB : if a Joiner and at least a parallelExecution is provided Workers are launch in parallel and joined
     *
     * @param input
     * @return 
     */
    @Override
    public CompletableFuture<ProcessResult<OUT>> process(IN input) {
        //if parralel workers are provided -> launch parrallel worker then join them
        if(this.parallelExecutions.size()>0 && this.joiner!=null){
            //launch all process
            ArrayList<CompletableFuture<ProcessResult<Object>>> toWait = new ArrayList<>();
            for (ExecutionContext exe : this.parallelExecutions) {
                toWait.add(exe.getWorker().process(exe.getParameter()));
            }

            //wait for all to complete
            CompletableFuture<Void> step1 = CompletableFuture.allOf(toWait.toArray(new CompletableFuture[toWait.size()]));
            //when all completed group results in list
            CompletableFuture<ArrayList<Object>> step2 = step1.thenApplyAsync(v -> {
                ArrayList<Object> l = new ArrayList<>();
                for(CompletableFuture<ProcessResult<Object>> tmp : toWait){
                    try {
                        l.add(tmp.get().getResult());
                    } catch (InterruptedException | ExecutionException ex) {
                    }
                }
                return l;
            });
            //Join results
            CompletableFuture<ProcessResult<OUT>> step3 = step2.thenApplyAsync(v -> {
                try {
                    return this.joiner.process(v).get();
                } 
                catch (InterruptedException | ExecutionException ex) {
                    return null;
                }
            });
            return step3;
        }
        //no parrallel worker given -> chain workers
        else{
            Object source = input;
            CompletableFuture<ProcessResult<OUT>> target = null;
            for (IWorker p : workers) {
                target = p.process(source);
                try {
                    source = target.get().getResult();
                } catch (InterruptedException | ExecutionException ex) {
                    source=null;
                }
            }
            return (CompletableFuture<ProcessResult<OUT>>)target;
        }
    }
}