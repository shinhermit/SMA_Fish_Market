package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.bidder.states;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionBidderFSMBehaviour;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

import java.util.logging.Logger;

/**
 *
 */
public class OtherBidderWonBidderBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(OtherBidderWonBidderBehaviour.class.getName());

    private RunningAuctionBidderFSMBehaviour myFSM;

    private static String OTHER_BIDDER_WON =
            "Another bidder won the auction.";

    public OtherBidderWonBidderBehaviour(
            Agent a, RunningAuctionBidderFSMBehaviour fsm
    )
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        System.out.println("Other bidder won.");

        ((BidderAgent)myAgent).displayBidInformation(OTHER_BIDDER_WON);
        ((BidderAgent)myAgent).auctionOver();
    }
}
