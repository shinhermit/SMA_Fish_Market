package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.subscription;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.SubsribeToAuctionBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
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

    private SubsribeToAuctionBehaviour myFSM;

    private HashSet<Auction> auctionList;

    private Random randomGenerator;

    private int transition;

    public PickAuctionBehaviour(Agent a, SubsribeToAuctionBehaviour fsm)
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
            // pick a random auction
            AID selectedAuction = this.pickRandomAuctionSeller(
                    (HashSet<Auction>) content
            );

            if (selectedAuction != null)
            {
                //an auction has been found
                ACLMessage reply = mess.createReply();
                reply.setPerformative(FishMarket.Performatives.REQUEST_AUCTION_REGISTRATION);

                this.transition = SubsribeToAuctionBehaviour.TRANSITION_REQUEST_SUBSCRIPTION;

                try
                {
                    reply.setContentObject(selectedAuction);
                }
                catch (IOException e)
                {
                    PickAuctionBehaviour.LOGGER.log(Level.SEVERE, null, e);
                }

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

        if (requestNewList)
        {
            // could not select item
            this.transition =
                    SubsribeToAuctionBehaviour.TRANSITION_RETURN_TO_SUBSCRIPTION_PROCESS_START;
        }


        // transition to next step

    }

    @Override
    public int onEnd()
    {
        return this.transition;
    }


    private AID pickRandomAuctionSeller(HashSet<Auction> auctions)
    {
        List<Auction> auctionList = new ArrayList<Auction>(auctions);
        int randomAuction;
        boolean lookingForAuction = true;
        AID seller;


        int numAuctions = auctionList.size();;

        do
        {
            randomAuction = this.randomGenerator.nextInt(numAuctions);
            seller = auctionList.get(randomAuction).getSellerID();

            if (this.myFSM.hasSubscribedToAuction(seller))
            {
                // known seller.
                auctionList.remove(seller);
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
            seller = null;
        }

        return seller;

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
            PickAuctionBehaviour.LOGGER.log(Level.SEVERE, null, e);
        }

        return filter;
    }
}
