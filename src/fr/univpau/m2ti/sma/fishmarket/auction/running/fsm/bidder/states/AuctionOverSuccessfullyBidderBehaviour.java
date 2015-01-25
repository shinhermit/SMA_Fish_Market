package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.bidder.states;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionBidderFSMBehaviour;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

import java.util.logging.Logger;

/**
 *
 */
public class AuctionOverSuccessfullyBidderBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(AuctionOverSuccessfullyBidderBehaviour.class.getName());

    private RunningAuctionBidderFSMBehaviour myFSM;

    private static String AUCTION_SUCCESS =
            "You won the auction.";

    public AuctionOverSuccessfullyBidderBehaviour(Agent a, RunningAuctionBidderFSMBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        System.out.println("Thanks for the fish");

        ((BidderAgent)myAgent).displayBidInformation(AUCTION_SUCCESS);
    }
}
