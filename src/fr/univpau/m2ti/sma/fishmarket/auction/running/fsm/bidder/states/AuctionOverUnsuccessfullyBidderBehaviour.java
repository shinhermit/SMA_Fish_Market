package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.bidder.states;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionBidderFSMBehaviour;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

import java.util.logging.Logger;

/**
 *
 */
public class AuctionOverUnsuccessfullyBidderBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(AuctionOverUnsuccessfullyBidderBehaviour.class.getName());

    private RunningAuctionBidderFSMBehaviour myFSM;

    private static String AUCTION_FAILURE =
            "Auction canceled.";

    public AuctionOverUnsuccessfullyBidderBehaviour(Agent a, RunningAuctionBidderFSMBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        System.out.println("Auction cancelled.");

        ((BidderAgent)myAgent).displayBidInformation(AUCTION_FAILURE);
        ((BidderAgent)myAgent).restoreInitialViewState();
    }
}
