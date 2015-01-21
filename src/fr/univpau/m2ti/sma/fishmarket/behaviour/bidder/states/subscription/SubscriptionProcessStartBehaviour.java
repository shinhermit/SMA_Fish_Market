package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.subscription;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.SubsribeToAuctionBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.messaging.TopicUtility;
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
        AID marketAID = bidderAgent.getMarketAgentAID();

        ACLMessage requestAuctionListMessage =
                new ACLMessage(FishMarket.Performatives.TO_PROVIDE);
        requestAuctionListMessage.addReceiver(marketAID);

        final AID topic =
                TopicUtility.createTopic(
                        FishMarket.Topics.TOPIC_BIDDERS_SUBSCRIPTION);

        requestAuctionListMessage.addReceiver(topic);


        bidderAgent.send(requestAuctionListMessage);

        // transition to next step

    }

    @Override
    public int onEnd() {
        // Implemented because of possible early return to end state.
        return SubsribeToAuctionBehaviour.TRANSITION_REQUEST_AUCTION_LIST;
    }
}
