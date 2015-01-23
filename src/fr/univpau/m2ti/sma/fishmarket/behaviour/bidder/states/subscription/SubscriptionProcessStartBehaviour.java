package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.subscription;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.SubscribeToAuctionBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.BidderSubscriptionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 */
public class SubscriptionProcessStartBehaviour extends OneShotBehaviour
{
    private SubscribeToAuctionBehaviour myFsm;

    public SubscriptionProcessStartBehaviour(Agent a, SubscribeToAuctionBehaviour fsm)
    {
        super(a);
        this.myFsm = fsm;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        BidderAgent bidderAgent =
                (BidderAgent) super.myAgent;

        // Prepare and send message
        // TODO : faire le lookup une fois pour toutes
        AID marketAID = bidderAgent.getMarketAgentAID();

        ACLMessage requestAuctionListMessage =
                new ACLMessage(FishMarket.Performatives.TO_REQUEST);

        requestAuctionListMessage.addReceiver(marketAID);

        requestAuctionListMessage.addReceiver(
        		BidderSubscriptionMarketFSMBehaviour.MESSAGE_TOPIC);


        bidderAgent.send(requestAuctionListMessage);

        // transition to next step

    }

    @Override
    public int onEnd() {
        // Implemented because of possible early return to end state.
        System.out.println("SubscriptionProcessStartBehaviour : in onEnd");
        return SubscribeToAuctionBehaviour.TRANSITION_REQUEST_AUCTION_LIST;
    }
}
