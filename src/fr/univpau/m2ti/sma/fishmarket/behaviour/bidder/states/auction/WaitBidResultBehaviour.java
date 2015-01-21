package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.auction;


import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.BidderBehaviour;

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
public class WaitBidResultBehaviour extends Behaviour
{

    /**
     * Logging.
     */
    private static final Logger LOGGER =
            Logger.getLogger(WaitBidResultBehaviour.class.getName());

    private BidderBehaviour myFSM;

    private int transition;

    /** Message filtering */
    private static final MessageTemplate MESSAGE_FILTER;

    static
    {
        MESSAGE_FILTER =
                MessageTemplate.or(
                        MessageTemplate.MatchPerformative(
                                FishMarket.Performatives.REP_BID
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

    public WaitBidResultBehaviour(Agent a, BidderBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

       // waiting for bid results.

        this.block();

        ACLMessage mess = myAgent.receive(MESSAGE_FILTER);

        if (mess != null)
        {
            this.myFSM.setRequest(mess);

            if (mess.getPerformative() == FishMarket.Performatives.REP_BID)
            {
                // Bid results
                String bidResult = mess.getContent();
                if (bidResult.equals("OK")) // Constant ?
                {
                    this.transition = BidderBehaviour.TRANSITION_RECEIVED_REP_BID_OK;
                    System.out.println("I won !!!");
                }
                else
                {
                    //bidResult.equals("NOK")
                    this.transition = BidderBehaviour.TRANSITION_RECEIVED_REP_BID_NOK;
                    System.out.println("Several bidders");
                }
            }
            else if (mess.getPerformative() == FishMarket.Performatives.AUCTION_CANCELLED)
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
            WaitBidResultBehaviour.LOGGER.log(Level.SEVERE, null, "Received null message");
        }

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
}
