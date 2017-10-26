package so;

import so.engine.IWorker;
import so.engine.WorkFlow;
import java.util.List;
import so.sample.worker.StringToStringWorker;
import so.sample.worker.IntToStringWorker;
import java.util.concurrent.ExecutionException;
import so.sample.worker.IntToIntJoiner;
import so.sample.worker.StringToIntWorker;

/**
 *  Illustrate how to use Workflow
 * @author marc_alx
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //1. Chain IWorker and launch by supplying an init value (one line syntax)
        System.out.println("\nExample 1");
        try {
            WorkFlow<Integer,String> w1 = WorkFlow.startSequential(new IntToStringWorker()).appendSequential(new StringToStringWorker());
            System.out.println(w1.process(12).get().getResult());
        }catch (ExecutionException | InterruptedException ex) {
        }  
        
        
        //2. Initialised a parallel execution (multiple line syntax)
        System.out.println("\nExample 2");
        WorkFlow<String,Integer> w2 = new WorkFlow<>();
        try {
            w2.addParallelProcess("1",new StringToIntWorker());
            w2.addParallelProcess("2",new StringToIntWorker());
            //cast explain here : https://stackoverflow.com/questions/933447/how-do-you-cast-a-list-of-supertypes-to-a-list-of-subtypes
            w2.setJoiner((IWorker<List<?>,Integer>)(IWorker<?,?>)new IntToIntJoiner());
            System.out.println(w2.process(null).get().getResult());
        } catch (InterruptedException | ExecutionException ex) {
        }
        
        //3. Initialised a sequential execution that takes a workflow as first worker (one line syntax)
        System.out.println("\nExample 3");
        try {
            WorkFlow<String,String> w3 = WorkFlow.startSequential(w2).appendSequential(new IntToStringWorker());
            System.out.println(w3.process(null).get().getResult());
        } catch (InterruptedException | ExecutionException ex) {
        }
           
        //4. Initialised a parallel execution (one line syntax)
        System.out.println("\nExample 4");
        try {
            WorkFlow<String,Integer> w4 = WorkFlow.startParallel("1",new StringToIntWorker())
            .appendParallel("2",new StringToIntWorker())
            //cast explain here : https://stackoverflow.com/questions/933447/how-do-you-cast-a-list-of-supertypes-to-a-list-of-subtypes
            .endWithJoiner((IWorker<List<?>,Integer>)(IWorker<?,?>)new IntToIntJoiner());
            System.out.println(w4.process(null).get().getResult());
        } catch (InterruptedException | ExecutionException ex) {
        }
        
        //5. Take multiple string process in parallel then sum
        System.out.println("\nExample 5");
        try {
        WorkFlow<String,String> w5 = WorkFlow.startSequential(
        WorkFlow.startParallel("12",WorkFlow.startSequential(new StringToIntWorker()))
                .appendParallel("24",WorkFlow.startSequential(new StringToIntWorker()))
                //cast explain here : https://stackoverflow.com/questions/933447/how-do-you-cast-a-list-of-supertypes-to-a-list-of-subtypes
                .endWithJoiner((IWorker<List<?>,Integer>)(IWorker<?,?>)new IntToIntJoiner()))
                .appendSequential(new IntToStringWorker());
        System.out.println(w5.process(null).get().getResult());
        } catch (InterruptedException | ExecutionException ex) {
        }
    }
}
