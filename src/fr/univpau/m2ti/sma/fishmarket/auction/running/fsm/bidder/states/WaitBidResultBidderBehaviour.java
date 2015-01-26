package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.bidder.states;


import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionBidderFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.protocol.FishMarket;
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
public class WaitBidResultBidderBehaviour extends OneShotBehaviour
{

    /**
     * Logging.
     */
    private static final Logger LOGGER =
            Logger.getLogger(WaitBidResultBidderBehaviour.class.getName());

    private RunningAuctionBidderFSMBehaviour myFSM;

    private int transition;

    /** Message filtering */
    private static final MessageTemplate MESSAGE_FILTER=
            MessageTemplate.and(
                    MessageTemplate.MatchTopic(
                    		RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC
                    ),
                    MessageTemplate.or(
                            MessageTemplate.MatchPerformative(
                                    FishMarket.Performatives.REP_BID
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

    public WaitBidResultBidderBehaviour(Agent a, RunningAuctionBidderFSMBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        BidderAgent bidderAgent =
                (BidderAgent) super.myAgent;

        bidderAgent.updateAuctionStatus(getBehaviourName());

       // waiting for bid results.

        ACLMessage mess = myAgent.receive(MESSAGE_FILTER);

        if (mess != null)
        {
            this.myFSM.setRequest(mess);

            if (mess.getPerformative() == FishMarket.Performatives.REP_BID)
            {
                // Bid results
                boolean bidResult = Boolean.valueOf(mess.getContent());
                if (bidResult == true)
                {
                    this.transition = RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_REP_BID_OK;
                    System.out.println("I won !!!");
                }
                else
                {
                    //bid not won
                    this.transition = RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_REP_BID_NOK;
                    System.out.println("Several bidders");
                }
            }
            else if (mess.getPerformative() == FishMarket.Performatives.TO_CANCEL)
            {
                this.transition = RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_AUCTION_CANCELLED;
            }
            else
            {
                //mess.getPerformative() == FishMarket.Performatives.AUCTION_OVER
                this.transition = RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_AUCTION_OVER;
            }
        }
        else
        {
            this.myFSM.block();
            this.transition = RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_BID_RESULT;
        }

    }

    @Override
    public int onEnd()
    {
        return this.transition;
    }
}
