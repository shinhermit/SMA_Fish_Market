package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.auction;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.BidderBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.RunningAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class AboutToBidBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(AboutToBidBehaviour.class.getName());

    private BidderBehaviour myFSM;

    private int transition;

    /** Message filtering */
    private static final MessageTemplate MESSAGE_FILTER;

    //set to true when block is called
    private boolean blocked = false;

    static
    {
        MESSAGE_FILTER =
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
                );
    }

    public AboutToBidBehaviour(Agent a, BidderBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action()
    {
        System.out.println("action => " + getBehaviourName());

        // read price from last announce
        ACLMessage mess = this.myFSM.getRequest();

        float price = Float.parseFloat(mess.getContent());

        // Is price low enough ?
        if (this.myFSM.getPriceLimit() > price && !blocked)
        {
            //bidding is possible
            ACLMessage bid = mess.createReply();
            bid.setPerformative(FishMarket.Performatives.TO_BID);
            bid.setContent(String.valueOf(price));
            bid.clearAllReceiver();
            bid.addReceiver(((BidderAgent)super.myAgent).getMarketAgentAID());
            bid.addReceiver(RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
            bid.setConversationId(this.myFSM.getRequest().getConversationId());
            bid.setSender(super.myAgent.getAID());
            super.myAgent.send(bid);

            // Store bidding price
            this.myFSM.setBiddingPrice(price);

            this.transition = BidderBehaviour.TRANSITION_BID;
        }
        else
        {
            ACLMessage newMessage = myAgent.receive(MESSAGE_FILTER);

            if (newMessage != null)
            {
                this.myFSM.setRequest(newMessage);

                if (newMessage.getPerformative() == FishMarket.Performatives.TO_ANNOUNCE)
                {
                    this.transition = BidderBehaviour.TRANSITION_RECEIVED_SUBSEQUENT_ANNOUNCE;
                }
                else if (newMessage.getPerformative() == FishMarket.Performatives.TO_CANCEL)
                {
                    this.transition = BidderBehaviour.TRANSITION_RECEIVED_AUCTION_CANCELLED;
                }
                else
                {
                    //mess.getPerformative() == FishMarket.Performatives.AUCTION_OVER
                    this.transition = BidderBehaviour.TRANSITION_RECEIVED_AUCTION_OVER;
                }
            }
            else
            {
                //Price is too high, waiting for price decrease
                this.myFSM.block();
                blocked = true;
            }
        }
    }

    @Override
    public int onEnd()
    {
        return this.transition;
    }
}
