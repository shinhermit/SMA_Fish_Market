package fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.bidder.states;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import java.util.logging.Logger;

/**
 *
 */
public class TerminateFailureBidderBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(TerminateFailureBidderBehaviour.class.getName());

    public TerminateFailureBidderBehaviour(Agent a)
    {
        super(a);
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());
        System.out.println("Subscription process interrupted.");
    }
}
