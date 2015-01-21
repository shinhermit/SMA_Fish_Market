package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.subscription;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.SubscribeToAuctionBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class WaitAuctionListBehaviour extends Behaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(WaitAuctionListBehaviour.class.getName());

    private SubscribeToAuctionBehaviour myFSM;

    public WaitAuctionListBehaviour(Agent a, SubscribeToAuctionBehaviour myFSM)
    {
        super(a);
        this.myFSM = myFSM;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        BidderAgent bidderAgent =
                (BidderAgent) super.myAgent;

        // wait for incoming message
        this.block();

        // Filter to extract incoming message
        MessageTemplate template = this.createFilter();

        // Receive message
        ACLMessage mess = bidderAgent.receive(template);

        myFSM.setRequest(mess);

        // transition to next step

    }

    @Override
    public boolean done()
    {
        // Stays alive in case we need to ask for new auction list
        return false;
    }


    /**
     * Creates a filter for incoming messages.
     *
     * @return a message template which can be used to filter incoming messages.
     */
    private MessageTemplate createFilter()
    {
        MessageTemplate filter = null;

        // Create message filter
        TopicManagementHelper topicHelper;
        try
        {
            topicHelper = (TopicManagementHelper) myAgent.getHelper(
                    TopicManagementHelper.SERVICE_NAME);

            final AID topic =
                    topicHelper.createTopic(
                            FishMarket.Topics.TOPIC_BIDDERS_SUBSCRIPTION);

            topicHelper.register(topic);

            filter = MessageTemplate.and(
                    MessageTemplate.MatchTopic(topic),
                    MessageTemplate.MatchPerformative(
                            FishMarket.Performatives.REPLY_AUCTION_LIST));
        }
        catch (ServiceException e)
        {
            WaitAuctionListBehaviour.LOGGER.log(Level.SEVERE, null, e);
        }

        return filter;
    }

    @Override
    public int onEnd()
    {
        // Implemented because of possible early return to end state.
        return SubscribeToAuctionBehaviour.TRANSITION_AUCTION_LIST_RECEIVED;
    }
}
