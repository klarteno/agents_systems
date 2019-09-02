package mas;

import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Logger;

import env.WorldEnv;
import env.model.CellModel;
import env.planner.Preprocessor;
import level.cell.Goal;
import logging.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class.getName());
    private static DecimalFormat df = new DecimalFormat("0.0000");

    public static void main(String[] args)
    {
        try
        {
            long startTime = System.nanoTime();

            WorldEnv env = new WorldEnv();

            long endTime = System.nanoTime();

            String duration = df.format( (endTime - startTime) / 1000000000.0 );


            System.err.println("Result: " + duration + " " + env.getSolutionLength());

            if (args.length == 0)
            {
                env.executePlanner();
            }
            else{
                System.err.println("Unknown results: ");
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
