package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.registration;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.SubsribeToAuctionBehaviour;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import java.util.logging.Logger;

/**
 *
 */
public class CreateBidderFSMBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(CreateBidderFSMBehaviour.class.getName());

    private SubsribeToAuctionBehaviour myFSM;

    public CreateBidderFSMBehaviour(Agent a, SubsribeToAuctionBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        BidderAgent bidderAgent =
                (BidderAgent) super.myAgent;

        bidderAgent.createBidderFSM(this.myFSM.getLastSubscribedAuction());
    }

}
