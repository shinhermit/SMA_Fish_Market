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
public class InitialOrAfterFailedBidBidderBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(InitialOrAfterFailedBidBidderBehaviour.class.getName());

    private RunningAuctionBidderFSMBehaviour myFSM;

    private boolean firstAnnounce = true;

    private static String AUCTION_START =
            "Auction start";

    private static String ENTERED_AUCTION =
            "Entered auction";

    /** Allows filtering incoming messages. */
    private static final MessageTemplate MESSAGE_FILTER =
            MessageTemplate.and(
                    MessageTemplate.MatchTopic(
                    		RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC
                    ),
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

    private int transition;

    public InitialOrAfterFailedBidBidderBehaviour(Agent a,
                                            RunningAuctionBidderFSMBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;

        //Display enter message
        ((BidderAgent)a).displayBidInformation(ENTERED_AUCTION);

    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        BidderAgent bidderAgent =
                (BidderAgent) super.myAgent;

        bidderAgent.updateAuctionStatus(getBehaviourName());

        ACLMessage mess = bidderAgent.receive(MESSAGE_FILTER);

        if (mess != null)
        {
            this.myFSM.setRequest(mess);

            if (mess.getPerformative() == FishMarket.Performatives.TO_ANNOUNCE)
            {
                this.transition = RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_FIRST_ANNOUNCE;
                if (this.firstAnnounce)
                {
                    this.firstAnnounce = false;
                    bidderAgent.displayBidInformation(AUCTION_START);
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
            this.transition = RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_FIRST_ANNOUNCE;
        }

        // transition to next step

    }

    @Override
    public int onEnd()
    {
        return this.transition;
    }
}
