package fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.bidder.states;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

import java.util.logging.Logger;

/**
 *
 */
public class TerminateSuccessBidderBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(TerminateSuccessBidderBehaviour.class.getName());

    public TerminateSuccessBidderBehaviour(Agent a)
    {
        super(a);
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());
        System.out.println("Subscribed successfully to auction.");

        BidderAgent bidderAgent = (BidderAgent) super.myAgent;

        bidderAgent.createBidderFSM();
    }
}
