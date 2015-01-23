package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.subscription;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.SubscribeToAuctionBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.BidderSubscriptionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
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

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class PickAuctionBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(PickAuctionBehaviour.class.getName());

    private SubscribeToAuctionBehaviour myFSM;

    private HashSet<Auction> auctionList;

    private Random randomGenerator;

    private int transition;

    private static final MessageTemplate MESSAGE_FILTER =
            MessageTemplate.and(
                    BidderSubscriptionManagementBehaviour.MESSAGE_FILTER,
                    MessageTemplate.MatchPerformative(
                            FishMarket.Performatives.TO_PROVIDE
                    )
            );


    public PickAuctionBehaviour(Agent a, SubscribeToAuctionBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
        this.randomGenerator = new Random();
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        BidderAgent bidderAgent =
                (BidderAgent) super.myAgent;

        ACLMessage mess = this.myFSM.getRequest();
        Object content = null;
        boolean requestNewList = false;

        if (mess != null)
        {
            try
            {
                content = mess.getContentObject();
            }
            catch (UnreadableException e)
            {
                PickAuctionBehaviour.LOGGER.log(Level.SEVERE, null, e);
            }

            if (content != null)
            {
                HashSet<Auction> auctionList = (HashSet<Auction>) content;

                if (auctionList.size() > 0)
                {
                    // pick a random auction
                    String selectedAuction =
                            this.pickRandomAuctionSeller(auctionList);


                    if (selectedAuction != null)
                    {
                        //an auction has been found
                        ACLMessage reply = mess.createReply();
                        reply.setPerformative(
                                FishMarket.Performatives.TO_SUBSCRIBE
                        );

                        reply.addReceiver(
                                BidderSubscriptionManagementBehaviour.MESSAGE_TOPIC
                        );

                        this.transition =
                                SubscribeToAuctionBehaviour.TRANSITION_REQUEST_SUBSCRIPTION;

                        reply.setContent(selectedAuction);

                        bidderAgent.send(reply);
                    }
                    else
                    {
                        requestNewList = true;
                    }
                }
                else
                {
                    requestNewList = true;
                }
            }
            else
            {
                requestNewList = true;
            }

            if (requestNewList)
            {
                // could not select item
                this.transition =
                        SubscribeToAuctionBehaviour.TRANSITION_RETURN_TO_SUBSCRIPTION_PROCESS_START;
            }

        }
        else
        {

        }
        // transition to next step

    }

    @Override
    public int onEnd()
    {
        return this.transition;
    }


    private String pickRandomAuctionSeller(HashSet<Auction> auctions)
    {
        List<Auction> auctionList = new ArrayList<Auction>(auctions);
        int randomAuction;
        boolean lookingForAuction = true;
        String auctionId;


        int numAuctions = auctionList.size();;

        do
        {
            randomAuction = this.randomGenerator.nextInt(numAuctions);
            auctionId = auctionList.get(randomAuction).getID();

            if (this.myFSM.hasSubscribedToAuction(auctionId))
            {
                // known seller.
                auctionList.remove(auctionId);
            }
            else
            {
                //new auction
                lookingForAuction = false;
            }

            numAuctions = auctionList.size();

        } while (lookingForAuction && numAuctions > 0);

        if (lookingForAuction)
        {
        	auctionId = null;
        }

        return auctionId;

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
                            FishMarket.Performatives.TO_PROVIDE));
        }
        catch (ServiceException e)
        {
            PickAuctionBehaviour.LOGGER.log(Level.SEVERE, null, e);
        }

        return filter;
    }
}
