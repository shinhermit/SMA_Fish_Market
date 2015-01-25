package fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.bidder.states;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.SubscribeToAuctionBidderFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.SubscribeToAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.protocol.FishMarket;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 */
public class SubscriptionProcessStartBidderBehaviour extends OneShotBehaviour
{
    private SubscribeToAuctionBidderFSMBehaviour myFsm;

    private int transition;

    public SubscriptionProcessStartBidderBehaviour(Agent a, SubscribeToAuctionBidderFSMBehaviour fsm)
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
        AID marketAID = bidderAgent.getMarketAgentAID();

        if (marketAID != null)
        {
            ACLMessage requestAuctionListMessage =
                    new ACLMessage(FishMarket.Performatives.TO_REQUEST);

            requestAuctionListMessage.addReceiver(marketAID);

            requestAuctionListMessage.addReceiver(
                    SubscribeToAuctionMarketFSMBehaviour.MESSAGE_TOPIC
            );

            bidderAgent.send(requestAuctionListMessage);

            this.transition =
                    SubscribeToAuctionBidderFSMBehaviour.TRANSITION_REQUEST_AUCTION_LIST;;
        }
        else
        {
            ((BidderAgent)myAgent).alertNoMarket();

            this.transition =
                    SubscribeToAuctionBidderFSMBehaviour.TRANSITION_EXIT_SUBSCRIPTION_PROCESS;
        }
        // transition to next step

    }

    @Override
    public int onEnd() {
        // Implemented because of possible early return to end state.

        return this.transition;
    }
}
