package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.auction;

import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.BidderBehaviour;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

import java.util.logging.Logger;

/**
 *
 */
public class AuctionOverSuccessfullyBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(AuctionOverSuccessfullyBehaviour.class.getName());

    private BidderBehaviour myFSM;

    public AuctionOverSuccessfullyBehaviour(Agent a, BidderBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());
    }
}