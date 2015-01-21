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
import jade.lang.acl.UnreadableException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class WaitSubscriptionReplyBehaviour extends Behaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(WaitSubscriptionReplyBehaviour.class.getName());

    private SubscribeToAuctionBehaviour myFSM;

    private int transition;

    public WaitSubscriptionReplyBehaviour(Agent a, SubscribeToAuctionBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
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

        AID seller = null;

        if(mess != null)
        {
            if(mess.getPerformative() ==
                    FishMarket.Performatives.TO_ACCEPT)
            {
                // subscription succeeded
                this.transition =
                        SubscribeToAuctionBehaviour
                                .TRANSITION_SUBSCRIPTION_ACCEPTED;

                try
                {
                    seller = (AID) mess.getContentObject();

                }
                catch (UnreadableException e)
                {
                    WaitSubscriptionReplyBehaviour.LOGGER.log(Level.SEVERE, null, e);
                }

                this.myFSM.subscribeToAuction(seller);
            }
            else
            {
                this.transition =
                        SubscribeToAuctionBehaviour
                                .TRANSITION_SUBSCRIPTION_REFUSED;
            }
        }

        // transition to next step

    }

    @Override
    public boolean done()
    {
        // Dies with fsm
        return false;
    }

    @Override
    public int onEnd()
    {
        return this.transition;
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
                MessageTemplate.or(
                    MessageTemplate.MatchPerformative(
                            FishMarket.Performatives.TO_ACCEPT),
                    MessageTemplate.MatchPerformative(
                            FishMarket.Performatives.TO_REFUSE
                    )
                )
            );

        }
        catch (ServiceException e)
        {
            WaitSubscriptionReplyBehaviour.LOGGER.log(Level.SEVERE, null, e);
        }

        return filter;
    }
}
