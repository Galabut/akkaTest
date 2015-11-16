import akka.actor.UntypedActor;

/**
 * Created by Galabut on 15.11.2015.
 */
public class SumActor extends UntypedActor {
    private Long sum = 0L;
    private Long id = 0L;

    public static enum Msg {KILL;}

    public SumActor(Long id) {
        this.id = id;
    }

    public SumActor() {
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Long) {
            sum += (Long) message;
            return;
        }
        if (message == Msg.KILL) {
            getContext().parent().tell(this, getSelf());
            return;
        }
        unhandled(message);
    }

    public Long getSum() {
        return sum;
    }

    public Long getId() {
        return id;
    }
}

