package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.subscription;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.SubscribeToAuctionBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.messaging.TopicUtility;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 *
 */
public class SubscriptionProcessStartBehaviour extends Behaviour
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
        AID marketAID = bidderAgent.getMarketAgentAID();

        ACLMessage requestAuctionListMessage =
                new ACLMessage(
                        FishMarket.Performatives.REQUEST_AUCTION_LIST
                );
        requestAuctionListMessage.addReceiver(marketAID);

        final AID topic =
                TopicUtility.createTopic(
                        FishMarket.Topics.TOPIC_BIDDERS_SUBSCRIPTION
                );

        requestAuctionListMessage.addReceiver(topic);


        bidderAgent.send(requestAuctionListMessage);

        // transition to next step

    }

    @Override
    public boolean done()
    {
        // Stays alive in case we need to ask for new auction list
        return false;
    }

    @Override
    public int onEnd() {
        // Implemented because of possible early return to end state.
        return SubscribeToAuctionBehaviour.TRANSITION_REQUEST_AUCTION_LIST;
    }
}
