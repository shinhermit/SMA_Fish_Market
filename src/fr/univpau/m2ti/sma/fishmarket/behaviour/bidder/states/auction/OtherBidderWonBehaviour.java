package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.auction;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.BidderBehaviour;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

import java.util.logging.Logger;

/**
 *
 */
public class OtherBidderWonBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(OtherBidderWonBehaviour.class.getName());

    private BidderBehaviour myFSM;

    private static String OTHER_BIDDER_WON =
            "Another bidder won the auction.";

    public OtherBidderWonBehaviour(Agent a, BidderBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        System.out.println("Other bidder won.");

        ((BidderAgent)myAgent).displayBidInformation(OTHER_BIDDER_WON);
    }
}
