import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import scala.Option;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Galabut on 15.11.2015.
 */
public class SumKernel extends UntypedActor {
    public static final String OUTPUT_FILE = "amountResult.csv";
    private ActorRef actorRef;

    public static enum Msg {FINISH}
    private static int sumActorsAlive = 0;

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            String s = (String) message;
            String[] numbers = s.split(";");
            Long id = Long.valueOf(numbers[0]);
            Long amount = Long.valueOf(numbers[1]);
            String actorName = id.toString();
            Option<ActorRef> opt_ref = getContext().child(actorName);
            if (opt_ref.nonEmpty()) {
                actorRef = opt_ref.get();
            } else {
                actorRef = getContext().actorOf(Props.create(SumActor.class, id), actorName);
                sumActorsAlive++;
            }
            actorRef.tell(amount, getSelf());
            return;
        }
        if (message == Msg.FINISH) {
            for (ActorRef actor : getContext().getChildren()) {
                actor.tell(SumActor.Msg.KILL, getSelf());
            }
            return;
        }
        if (message instanceof SumActor) {
            SumActor sa = (SumActor) message;
            FileWriter writer = new FileWriter(OUTPUT_FILE, true);
            String text = sa.getId() + ";" + sa.getSum() + '\n';
            writer.write(text);
            writer.close();
            getSender().tell(PoisonPill.getInstance(), getSelf());
            sumActorsAlive--;
            if (sumActorsAlive==0) {
                getContext().system().terminate();
            }
        } else
            unhandled(message);
    }
}