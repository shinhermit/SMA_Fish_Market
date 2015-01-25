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

    private int transition;

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
        AID marketAID = bidderAgent.getMarketAgentAID();

        if (marketAID != null)
        {
            ACLMessage requestAuctionListMessage =
                    new ACLMessage(FishMarket.Performatives.TO_REQUEST);

            requestAuctionListMessage.addReceiver(marketAID);

            requestAuctionListMessage.addReceiver(
                    BidderSubscriptionMarketFSMBehaviour.MESSAGE_TOPIC
            );

            bidderAgent.send(requestAuctionListMessage);

            this.transition =
                    SubscribeToAuctionBehaviour.TRANSITION_REQUEST_AUCTION_LIST;;
        }
        else
        {
            ((BidderAgent)myAgent).alertNoMarket();

            this.transition =
                    SubscribeToAuctionBehaviour.TRANSITION_EXIT_SUBSCRIPTION_PROCESS;
        }
        // transition to next step

    }

    @Override
    public int onEnd() {
        // Implemented because of possible early return to end state.

        return this.transition;
    }
}
