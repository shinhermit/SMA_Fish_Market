package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.subscription;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import java.util.logging.Logger;

/**
 *
 */
public class SubscriptionProcessEndBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(SubscriptionProcessEndBehaviour.class.getName());

    public SubscriptionProcessEndBehaviour(Agent a)
    {
        super(a);
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());
    }
}
