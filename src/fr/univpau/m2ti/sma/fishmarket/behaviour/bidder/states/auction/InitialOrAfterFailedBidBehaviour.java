package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.auction;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.BidderBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.SubsribeToAuctionBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.core.messaging.TopicUtility;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class InitialOrAfterFailedBidBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(InitialOrAfterFailedBidBehaviour.class.getName());

    private BidderBehaviour myFSM;

    /** Allows filtering incoming messages. */
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
                                    FishMarket.Performatives.TO_CANCEL
                            ),
                            MessageTemplate.MatchPerformative(
                                    FishMarket.Performatives.AUCTION_OVER
                            )
                    )
            );
    }

    private int transition;

    public InitialOrAfterFailedBidBehaviour(Agent a,
                                            BidderBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        BidderAgent bidderAgent =
                (BidderAgent) super.myAgent;

        // waiting for an announce

        this.block();

        ACLMessage mess = bidderAgent.receive(MESSAGE_FILTER);

        if (mess != null)
        {
            this.myFSM.setRequest(mess);

            if (mess.getPerformative() == FishMarket.Performatives.TO_ANNOUNCE)
            {
                this.transition = BidderBehaviour.TRANSITION_RECEIVED_FIRST_ANNOUNCE;
            }
            else if (mess.getPerformative() == FishMarket.Performatives.TO_CANCEL)
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
            InitialOrAfterFailedBidBehaviour.LOGGER
                    .log(Level.SEVERE, null, "Received null message");
        }

        // transition to next step

    }

    @Override
    public int onEnd()
    {
        return this.transition;
    }
}
