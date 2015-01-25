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
                                    MessageTemplate.or(
                                            MessageTemplate.MatchPerformative(
                                                    FishMarket.Performatives.TO_BID
                                            ),
                                            MessageTemplate.MatchPerformative(
                                                    FishMarket.Performatives.AUCTION_OVER
                                            )
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
            price = Float.parseFloat(mess.getContent());
            bidderAgent.handleAnnounce(price);
            //remove last announce
            //this.myFSM.setRequest(null);
        }

        ACLMessage newMessage = myAgent.receive(MESSAGE_FILTER);

        if (newMessage != null)
        {
            if (newMessage.getPerformative()
                    == FishMarket.Performatives.TO_ANNOUNCE)
            {
                this.myFSM.setRequest(newMessage);

                this.transition =
                        RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_SUBSEQUENT_ANNOUNCE;
            }
            else if (newMessage.getPerformative()
                    == FishMarket.Performatives.TO_BID)
            {
                // User wants to bid
                ACLMessage bid = new ACLMessage(
                        FishMarket.Performatives.TO_BID
                );
                bid.setContent(String.valueOf(bidderAgent.getBiddingPrice()));
                bid.clearAllReceiver();
                bid.addReceiver(bidderAgent.getMarketAgentAID());
                bid.addReceiver(RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
                bid.setConversationId(this.myFSM.getRequest().getConversationId());
                bid.setSender(bidderAgent.getAID());
                bidderAgent.send(bid);

                //Display message to user
                bidderAgent.displayBidInformation(AUCTION_BID_SENT);

                this.transition =
                        RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_BID_RESULT;
            }
            else if (newMessage.getPerformative()
                    == FishMarket.Performatives.TO_CANCEL)
            {
                //to final state
                this.myFSM.setRequest(null);
                this.transition =
                        RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_AUCTION_CANCELLED;
            }
            else
            {
                //to final state : other bidder won
                this.myFSM.setRequest(null);
                //mess.getPerformative() == FishMarket.Performatives.AUCTION_OVER
                this.transition =
                        RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_AUCTION_OVER;
            }
        }
        else
        {
            this.myFSM.block();
            this.transition =
                    RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_USER_CHOICE;
        }
    }

//    @Override
//    public void action()
//    {
//        System.out.println("action => " + getBehaviourName());
//
//        // read price from last announce
//        ACLMessage mess = this.myFSM.getRequest();
//
//        float price = Float.parseFloat(mess.getContent());
//
//        // Is price low enough ?
//        if (this.myFSM.getPriceLimit() > price && !blocked)
//        {
//            //bidding is possible
//            ACLMessage bid = mess.createReply();
//            bid.setPerformative(FishMarket.Performatives.TO_BID);
//            bid.setContent(String.valueOf(price));
//            bid.clearAllReceiver();
//            bid.addReceiver(((BidderAgent)super.myAgent).getMarketAgentAID());
//            bid.addReceiver(RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
//            bid.setConversationId(this.myFSM.getRequest().getConversationId());
//            bid.setSender(super.myAgent.getAID());
//            super.myAgent.send(bid);
//
//            // Store bidding price
//            this.myFSM.setBiddingPrice(price);
//
//            this.transition = BidderBehaviour.TRANSITION_BID;
//        }
//        else
//        {
//            ACLMessage newMessage = myAgent.receive(MESSAGE_FILTER);
//
//            if (newMessage != null)
//            {
//                this.myFSM.setRequest(newMessage);
//
//                if (newMessage.getPerformative() == FishMarket.Performatives.TO_ANNOUNCE)
//                {
//                    this.transition = BidderBehaviour.TRANSITION_RECEIVED_SUBSEQUENT_ANNOUNCE;
//                }
//                else if (newMessage.getPerformative() == FishMarket.Performatives.TO_CANCEL)
//                {
//                    this.transition = BidderBehaviour.TRANSITION_RECEIVED_AUCTION_CANCELLED;
//                }
//                else
//                {
//                    //mess.getPerformative() == FishMarket.Performatives.AUCTION_OVER
//                    this.transition = BidderBehaviour.TRANSITION_RECEIVED_AUCTION_OVER;
//                }
//            }
//            else
//            {
//                //Price is too high, waiting for price decrease
//                this.myFSM.block();
//                blocked = true;
//            }
//        }
//    }

    @Override
    public int onEnd()
    {
        return this.transition;
    }
}
