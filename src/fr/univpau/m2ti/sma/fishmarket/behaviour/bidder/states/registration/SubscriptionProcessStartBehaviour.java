package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.registration;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.SubsribeToAuctionBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 */
public class SubscriptionProcessStartBehaviour extends OneShotBehaviour
{
    public SubscriptionProcessStartBehaviour(Agent a)
    {
        super(a);
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        BidderAgent bidderAgent =
                (BidderAgent) super.myAgent;


        // Prepare and send message
        ACLMessage requestActionListMessage =
                new ACLMessage(FishMarket.Performatives.REQUEST_AUCTION_LIST);

        bidderAgent.sendMessage(requestActionListMessage);

        // transition to next step

    }

    @Override
    public int onEnd() {
        // Implemented because of possible early return to end state.
        return SubsribeToAuctionBehaviour.TRANSITION_REQUEST_AUCTION_LIST;
    }
}
