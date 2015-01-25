package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.bidder.states;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionBidderFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.protocol.FishMarket;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.logging.Logger;

/**
 *
 */
public class AboutToBidBidderBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(AboutToBidBidderBehaviour.class.getName());

    private RunningAuctionBidderFSMBehaviour myFSM;

    private static String AUCTION_BID_SENT = "Bid sent.";


    private ACLMessage lastMessage = null;

    private int transition;

    /** Message filtering */
    private static final MessageTemplate MESSAGE_FILTER =
            MessageTemplate.and(
                    MessageTemplate.MatchTopic(RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC),
                    MessageTemplate.or(
                            MessageTemplate.MatchPerformative(
                                    FishMarket.Performatives.TO_ANNOUNCE
                            ),
                            MessageTemplate.or(
                                    MessageTemplate.MatchPerformative(
                                            FishMarket.Performatives.TO_CANCEL
                                    ),
                                    MessageTemplate.MatchPerformative(
                                            FishMarket.Performatives.AUCTION_OVER
                                    )
                            )
                    )
            );


    public AboutToBidBidderBehaviour(Agent a, RunningAuctionBidderFSMBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action ()
    {
        System.out.println("action => " + getBehaviourName());

        BidderAgent bidderAgent = (BidderAgent)myAgent;

        // read price from last announce
        ACLMessage mess = this.myFSM.getRequest();
        float price;

        if (mess != null)
        {
            this.lastMessage = mess;
            price = Float.parseFloat(mess.getContent());
            //Display price to user and store it
            bidderAgent.handleAnnounce(price);
            //remove last announce
            this.myFSM.setRequest(null);

            //needs user interaction
            bidderAgent.setCastBid(false);
            bidderAgent.setWaitingForUserBid(true);

            this.transition =
                    RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_USER_CHOICE;
        }

        ACLMessage newMessage = myAgent.receive(MESSAGE_FILTER);

        //Did we receive a new message ?
        if (newMessage != null)
        {
            //new price, cancel, or auction end
            if (newMessage.getPerformative()
                    == FishMarket.Performatives.TO_ANNOUNCE)
            {
                this.myFSM.setRequest(newMessage);

                this.transition =
                        RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_SUBSEQUENT_ANNOUNCE;

                bidderAgent.setWaitingForUserBid(true);
                bidderAgent.setCastBid(false);
            }
            else if (newMessage.getPerformative()
                    == FishMarket.Performatives.TO_CANCEL)
            {
                //to final state
                this.myFSM.setRequest(null);
                this.transition =
                        RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_AUCTION_CANCELLED;

                bidderAgent.setWaitingForUserBid(false);
                bidderAgent.setCastBid(false);
            }
            else
            {
                //to final state : other bidder won
                this.myFSM.setRequest(null);
                //mess.getPerformative() == FishMarket.Performatives.AUCTION_OVER
                this.transition =
                        RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_AUCTION_OVER;

                bidderAgent.setWaitingForUserBid(false);
                bidderAgent.setCastBid(false);
            }
        }

        //user interaction
        if (bidderAgent.isWaitingForUserBid())
        {
            if (bidderAgent.castBid())
            {
                //Send bid
                ACLMessage bid = this.lastMessage.createReply();
                bid.setPerformative(FishMarket.Performatives.TO_BID);
                bid.setContent(String.valueOf(bidderAgent.getBiddingPrice()));
                bid.clearAllReceiver();
                bid.addReceiver(bidderAgent.getMarketAgentAID());
                bid.addReceiver(RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
                bid.setConversationId(this.lastMessage.getConversationId());
                bid.setSender(bidderAgent.getAID());
                bidderAgent.send(bid);

                //user interacted
                bidderAgent.setWaitingForUserBid(false);
                bidderAgent.setCastBid(false);

                this.transition =
                        RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_BID_RESULT;
            }
            else
            {
                //wait some more
                System.out.println("Waiting for user bid");
                this.myFSM.block(500);
                this.transition =
                        RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_USER_CHOICE;
            }
        }
    }

    @Override
    public int onEnd()
    {
        return this.transition;
    }
}