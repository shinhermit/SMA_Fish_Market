package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.auction;

import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.BidderBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.Agent;
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

    private static final MessageTemplate MESSAGE_FILTER;

    static
    {
        MESSAGE_FILTER =
                MessageTemplate.or(
                        MessageTemplate.MatchPerformative(
                                FishMarket.Performatives.TO_ANNOUNCE
                        ),
                        MessageTemplate.or(
                                MessageTemplate.MatchPerformative(
                                        FishMarket.Performatives.AUCTION_CANCELLED
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

        long price = Long.parseLong(mess.getContent());

        // Is price low enough ?
        if (this.myFSM.getPriceLimit() > price)
        {
            //bidding is possible
            ACLMessage bid = mess.createReply();
            bid.setPerformative(FishMarket.Performatives.TO_BID);
            super.myAgent.send(bid);

            // Store bidding price
            this.myFSM.setBiddingPrice(price);

            this.transition = BidderBehaviour.TRANSITION_BID;
        }
        else
        {
            //Price is too high, waiting for price decrease
            this.block();

            ACLMessage newMessage = myAgent.receive(MESSAGE_FILTER);

            this.myFSM.setRequest(newMessage);

            if (newMessage != null)
            {
                this.myFSM.setRequest(mess);

                if (newMessage.getPerformative() == FishMarket.Performatives.TO_ANNOUNCE)
                {
                    this.transition = BidderBehaviour.TRANSITION_RECEIVED_SUBSEQUENT_ANNOUNCE;
                }
                else if (newMessage.getPerformative() == FishMarket.Performatives.AUCTION_CANCELLED)
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
                //Should not happen
                AboutToBidBehaviour.LOGGER.log(Level.SEVERE, null, "Received null message");
            }
        }
    }

    @Override
    public int onEnd()
    {
        return this.transition;
    }
}
