package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.auction;


import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.BidderBehaviour;

import jade.core.Agent;

import jade.core.behaviours.OneShotBehaviour;

import java.util.logging.Logger;

/**
 *
 */
public class WaitBidResultBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(WaitBidResultBehaviour.class.getName());

    private BidderBehaviour myFSM;

    private int transition;

    public WaitBidResultBehaviour(Agent a, BidderBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

    }
}
