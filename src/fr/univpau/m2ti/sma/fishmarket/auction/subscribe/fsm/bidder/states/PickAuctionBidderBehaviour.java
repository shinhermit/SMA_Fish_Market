package fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.bidder.states;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.SubscribeToAuctionBidderFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.SubscribeToAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import fr.univpau.m2ti.sma.fishmarket.protocol.FishMarket;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class PickAuctionBidderBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(PickAuctionBidderBehaviour.class.getName());

    private SubscribeToAuctionBidderFSMBehaviour myFSM;

    private HashSet<Auction> auctionList;

    private Random randomGenerator;

    private int transition;

    private ACLMessage lastAuctionListMessage = null;

    private static final MessageTemplate MESSAGE_FILTER =
            MessageTemplate.and(
                    SubscribeToAuctionMarketFSMBehaviour.MESSAGE_FILTER,
                    MessageTemplate.MatchPerformative(
                            FishMarket.Performatives.TO_PROVIDE
                    )
            );

    private static final MessageTemplate UNBLOCK_FILTER =
            MessageTemplate.and(
                    SubscribeToAuctionMarketFSMBehaviour.MESSAGE_FILTER,
                    MessageTemplate.MatchPerformative(
                            FishMarket.Performatives.TO_ANNOUNCE
                    )
            );

    public PickAuctionBidderBehaviour(Agent a, SubscribeToAuctionBidderFSMBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
        this.randomGenerator = new Random();
    }

    @Override
    public void action ()
    {
        System.out.println("action => " + getBehaviourName());

        BidderAgent bidderAgent = (BidderAgent)  myAgent;

        ACLMessage mess = this.myFSM.getRequest();

        if (mess != null)
        {
            //Handling new list
            this.myFSM.setRequest(null);
            this.lastAuctionListMessage = mess;

            //Extract auction list
            HashSet<Auction> auctionList =
                    this.extractAuctionsFromMessage(mess);

            bidderAgent.displayAuctionList(auctionList);
        }

        if (bidderAgent.hasAuctionSelected())
        {
            //Auction has been picked by user
            Auction selectedAuction = bidderAgent.getSubscribedAuction();

            ACLMessage subscription = this.lastAuctionListMessage.createReply();
            subscription.setPerformative(FishMarket.Performatives.TO_SUBSCRIBE);


            subscription.addReceiver(
                    SubscribeToAuctionMarketFSMBehaviour.MESSAGE_TOPIC
            );

            subscription.setContent(selectedAuction.getID());

            bidderAgent.send(subscription);

            //Reset state
            bidderAgent.setAuctionSelected(false);

            this.transition =
                    SubscribeToAuctionBidderFSMBehaviour.TRANSITION_REQUEST_SUBSCRIPTION;
        }
        else
        {
            //wait some more
            this.myFSM.block(500);

            this.transition =
                    SubscribeToAuctionBidderFSMBehaviour.TRANSITION_WAIT_USER_CHOICE;
        }
    }
    // Old method.
// @Override
//    public void action ()
//    {
//        System.out.println("action => " + getBehaviourName());
//
//        //Handling new list
//
//        ACLMessage mess = this.myFSM.getRequest();
//        boolean requestNewList = false;
//
//        HashSet<Auction> auctionList =
//                this.extractAuctionsFromMessage(mess);
//
//        ((BidderAgent)myAgent).displayAuctionList(auctionList);
//
//        //Waiting unblock message
//        ACLMessage unblockMessage = myAgent.receive(
//                PickAuctionBidderBehaviour.UNBLOCK_FILTER
//        );
//
//        if (unblockMessage != null)
//        {
//            this.transition =
//                    SubscribeToAuctionBidderFSMBehaviour.TRANSITION_REQUEST_SUBSCRIPTION;
//
//            Auction selectedAuction = ((BidderAgent) myAgent).getSubscribedAuction();
//
//            ACLMessage subscription = mess.createReply();
//            subscription.setPerformative(
//                    FishMarket.Performatives.TO_SUBSCRIBE
//            );
//
//            subscription.addReceiver(
//                    SubscribeToAuctionMarketFSMBehaviour.MESSAGE_TOPIC
//            );
//
//            subscription.setContent(selectedAuction.getID());
//
//            myAgent.send(subscription);
//        }
//        else
//        {
//            this.myFSM.block();
//            this.transition =
//                    SubscribeToAuctionBidderFSMBehaviour.TRANSITION_WAIT_USER_CHOICE;
//        }
//    }


    private HashSet<Auction> extractAuctionsFromMessage(ACLMessage message)
    {
        Object content = null;
        boolean requestNewList = false;
        HashSet<Auction> auctionList = null;

        if (message != null)
        {
            try
            {
                content = message.getContentObject();
            }
            catch (UnreadableException e)
            {
                PickAuctionBidderBehaviour.LOGGER.log(Level.SEVERE, null, e);
            }

            if (content != null)
            {
                auctionList = (HashSet<Auction>) content;
            }
        }

        if (auctionList == null)
        {
            auctionList = new HashSet<Auction>();
        }

        return auctionList;
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
            PickAuctionBidderBehaviour.LOGGER.log(Level.SEVERE, null, e);
        }

        return filter;
    }
}
