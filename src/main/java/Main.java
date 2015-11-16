import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

/**
 * Created by Galabut on 15.11.2015.
 */
public class Main {
    public static final String INPUT_FILE = "amount.csv";

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("summary");
        final ActorRef kernel = system.actorOf(Props.create(SumKernel.class), "sumKernel");

        try {
            FileWriter writer = new FileWriter(SumKernel.OUTPUT_FILE, false);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(INPUT_FILE)));
            String s = "";
            while ((s = reader.readLine()) != null) {
                kernel.tell(s, ActorRef.noSender());
            }
            kernel.tell(SumKernel.Msg.FINISH, ActorRef.noSender());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
