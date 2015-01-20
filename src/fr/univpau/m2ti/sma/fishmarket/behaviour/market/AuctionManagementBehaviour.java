package fr.univpau.m2ti.sma.fishmarket.behaviour.market;

import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.auctions.*;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.FSMBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
/**
 * The FSM behaviour of the market agent which is responsible for enforcing the auction protocol.
 * 
 * <p>One AuctionManagementBehaviour must be added for each running auction.</p>
 * 
 * @author Josuah Aron
 *
 */
public class AuctionManagementBehaviour extends FSMBehaviour
{
	/** The AID of the seller agent who created the auction which is managed by this behaviour. */
	private final AID mySeller;
	
	/** The AID of the bidder to which the fish supply has been attributed. */
	private AID selectedBidder = null;
	
	/** Holds the last auction creation request. */
	private ACLMessage request;

	/** The topic of the conversations managed by this behaviour. */
	private final AID topic;
	
	/** The amount of time to wait for incoming messages before relaying a <i>to_bid</i>.*/
	public static final long BID_WAIT_DELAY = 10000; // 10 sec
	
	/** The amount of time to wait for incoming messages before relaying a <i>to_bid</i>.*/
	public static final long CANCELLETION_WAIT_DELAY = 500; // 0.5 sec
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(AuctionManagementBehaviour.class.getName());
	
	/** The state in which the agent waits for a first announcement from the seller. */
	private static final String STATE_WAIT_TO_ANNOUNCE =
			"STATE_WAIT_FIRST_TO_ANNOUNCE";
	
	/** The state in which the agent relays the first announcement from the seller to the bidders. */
	private static final String STATE_RELAY_TO_ANNOUNCE =
			"STATE_RELAY_TO_ANNOUNCE";
	
	/** The state in which the agent waits for bids from the bidders. */
	private static final String STATE_WAIT_TO_BID =
			"STATE_WAIT_TO_BID";
	
	/** The state in which the agent relays the bids from the bidders to the seller. */
	private static final String STATE_RELAY_TO_BID =
			"STATE_RELAY_TO_BID";
	
	/** The state in which the agent relays a cancellation of the auction by the seller to the bidders. */
	private static final String STATE_RELAY_AUCTION_CANCELLED =
			"STATE_RELAY_AUCTION_CANCELLED";
	
	/** The state in which the agent waits for the response to the bids from the seller. */
	private static final String STATE_WAIT_REP_BID =
			"STATE_WAIT_REP_BID";
	
	/** The state in which the agent relays the positive response to the bid from the seller to one bidder. */
	private static final String STATE_RELAY_REP_BID_OK =
			"STATE_STATE_RELAY_REP_BID_OK";
	
	/** The state in which the agent relays the negative response to the bid from the seller to non-chosen bidders. */
	private static final String STATE_RELAY_REP_BID_NOK =
			"STATE_STATE_RELAY_REP_BID_NOK";
	
	/** The state in which the agent waits for the attribution message from the seller. */
	private static final String STATE_WAIT_TO_ATTRIBUTE =
			"STATE_WAIT_TO_ATTRIBUTE";
	
	/** The state in which the agent relays the the attribution message from the seller to one bidder. */
	private static final String STATE_RELAY_TO_ATTRIBUTE =
			"STATE_RELAY_TO_ATTRIBUTE";
	
	/** The state in which the agent waits for the fish supply from the seller. */
	private static final String STATE_WAIT_TO_GIVE =
			"STATE_WAIT_TO_GIVE";
	
	/** The state in which the agent relays the fish supply from the seller to one bidder. */
	private static final String STATE_RELAY_TO_GIVE =
			"STATE_RELAY_TO_GIVE";
	
	/** The state in which the agent waits the payment from bidder. */
	private static final String STATE_WAIT_TO_PAY =
			"STATE_WAIT_TO_PAY";
	
	/** The state in which the agent relays the payment from the bidder to the seller. */
	private static final String STATE_RELAY_TO_PAY =
			"STATE_RELAY_TO_PAY";
	
	/** The state in which the agent waits for the notification of the successful end of the auction. */
	private static final String STATE_WAIT_AUCTION_OVER =
			"STATE_WAIT_AUCTION_OVER";
	
	/** The state in which the agent relays the notification of a successful end of the auction from the seller to the bidders. */
	private static final String STATE_RELAY_AUCTION_OVER =
			"STATE_RELAY_AUCTION_OVER";
	
	/** The successful ending state of this FSM. */
	private static final String STATE_TERMINATE_SUCCESS =
			"STATE_TERMINATE_SUCCESS";
	
	/** The ending state of this FSM after a cancellation of the auction. */
	private static final String STATE_TERMINATE_CANCELLED =
			"STATE_TERMINATE_CANCELLED";
	
	/** Return code to activates the appropriate transitions when <i>to_announce</i> is received. */
	public static final int TRANSITION_TO_ANNOUNCE_RECEIVED;
	
	/** Return code to activates the appropriate transitions when <i>to_bid</i> is received. */
	public static final int TRANSITION_TO_BID_RECEIVED;
	
	/** Return code to activates the appropriate transitions when <i>auction_cancelled</i> is received. */
	public static final int TRANSITION_AUCTION_CANCELLED_RECEIVED;
	
	/** Return code to activates the appropriate transitions when <i>auction_cancelled</i> is received. */
	public static final int TRANSITION_TO_BID_RELAYED;
	
	/** Return code to activates the appropriate transitions when <i>rep_bid_ok</i> is received. */
	public static final int TRANSITION_REP_BID_OK_RECEIVED;
	
	/** Return code to activates the appropriate transitions when <i>rep_bid_nok</i> is received. */
	public static final int TRANSITION_REP_BID_NOK_RECEIVED;
	
	static
	{
		int start = -1;
		
		TRANSITION_TO_ANNOUNCE_RECEIVED = ++start;
		TRANSITION_TO_BID_RECEIVED = ++start;
		TRANSITION_AUCTION_CANCELLED_RECEIVED = ++start;
		TRANSITION_TO_BID_RELAYED = ++start;
		TRANSITION_REP_BID_OK_RECEIVED = ++start;
		TRANSITION_REP_BID_NOK_RECEIVED = ++start;
	}
	
	/**
	 * Create the behaviour of a market agent which is responsible  for enforcing the auction protocol.
	 * 
	 * @param myMarketAgent
	 * 			the agent to which this behaviour is added.
	 */
	public AuctionManagementBehaviour(
			MarketAgent myMarketAgent,
			AID mySeller)
	{
		super(myMarketAgent);
		
		this.mySeller = mySeller;
		
		this.topic = this.createTopic();
		
		// Register states
		this.registerFirstState(new WaitToAnnounceBehaviour(myMarketAgent, this),
				STATE_WAIT_TO_ANNOUNCE);
		
		this.registerState(new WaitToBidBehaviour(myMarketAgent, this),
				STATE_WAIT_TO_BID);
		
		this.registerState(new WaitRepBidBehaviour(myMarketAgent, this),
				STATE_WAIT_REP_BID);
		
		this.registerState(new WaitToAttributeBehaviour(myMarketAgent, this),
				STATE_WAIT_TO_ATTRIBUTE);
		
		this.registerState(new WaitToGiveBehaviour(myMarketAgent, this),
				STATE_WAIT_TO_GIVE);
		
		this.registerState(new WaitToPayBehaviour(myMarketAgent, this),
				STATE_WAIT_TO_PAY);
		
		this.registerState(new WaitAuctionOverBehaviour(myMarketAgent, this),
				STATE_WAIT_AUCTION_OVER);
		
		this.registerState(new RelayToAnnounceBehaviour(myMarketAgent, this),
				STATE_RELAY_TO_ANNOUNCE);
		
		this.registerState(new RelayToBidBehaviour(myMarketAgent, this),
				STATE_RELAY_TO_BID);
		
		this.registerState(new RelayAuctionCancelledBehaviour(myMarketAgent, this),
				STATE_RELAY_AUCTION_CANCELLED);
		
		this.registerState(new RelayRepBidOkBehaviour(myMarketAgent, this),
				STATE_RELAY_REP_BID_OK);
		
		this.registerState(new RelayRepBidNokBehaviour(myMarketAgent, this),
				STATE_RELAY_REP_BID_NOK);
		
		this.registerState(new RelayToAttributeBehaviour(myMarketAgent, this),
				STATE_RELAY_TO_ATTRIBUTE);
		
		this.registerState(new RelayToGiveBehaviour(myMarketAgent, this),
				STATE_RELAY_TO_GIVE);
		
		this.registerState(new RelayToPayBehaviour(myMarketAgent, this),
				STATE_RELAY_TO_PAY);
		
		this.registerState(new RelayAuctionOverBehaviour(myMarketAgent, this),
				STATE_RELAY_AUCTION_OVER);
		
		this.registerLastState(new TerminateSuccessBehaviour(myMarketAgent, this),
				STATE_TERMINATE_SUCCESS);
		
		this.registerLastState(new TerminateCancelledBehaviour(myMarketAgent, this),
				STATE_TERMINATE_CANCELLED);
		
		// Register transitions
		this.registerDefaultTransition(STATE_WAIT_TO_ANNOUNCE,
				STATE_RELAY_TO_ANNOUNCE);
		
		this.registerDefaultTransition(STATE_RELAY_TO_ANNOUNCE,
				STATE_WAIT_TO_BID);
		
		this.registerDefaultTransition(STATE_RELAY_REP_BID_NOK,
				STATE_WAIT_TO_ANNOUNCE);
		
		this.registerDefaultTransition(STATE_RELAY_AUCTION_CANCELLED,
				STATE_TERMINATE_CANCELLED);
		
		this.registerDefaultTransition(STATE_RELAY_REP_BID_OK,
				STATE_WAIT_TO_ATTRIBUTE);
		
		this.registerDefaultTransition(STATE_WAIT_TO_ATTRIBUTE,
				STATE_RELAY_TO_ATTRIBUTE);
		
		this.registerDefaultTransition(STATE_RELAY_TO_ATTRIBUTE,
				STATE_WAIT_TO_GIVE);
		
		this.registerDefaultTransition(STATE_WAIT_TO_GIVE,
				STATE_RELAY_TO_GIVE);
		
		this.registerDefaultTransition(STATE_RELAY_TO_GIVE,
				STATE_WAIT_TO_PAY);
		
		this.registerDefaultTransition(STATE_WAIT_TO_PAY,
				STATE_RELAY_TO_PAY);
		
		this.registerDefaultTransition(STATE_RELAY_TO_PAY,
				STATE_WAIT_AUCTION_OVER);
		
		this.registerDefaultTransition(STATE_WAIT_AUCTION_OVER,
				STATE_RELAY_AUCTION_OVER);
		
		this.registerDefaultTransition(STATE_RELAY_AUCTION_OVER,
				STATE_TERMINATE_SUCCESS);
		
		this.registerTransition(STATE_WAIT_TO_BID,
				STATE_RELAY_TO_BID,
				TRANSITION_TO_BID_RECEIVED);
		
		this.registerTransition(STATE_WAIT_TO_BID,
				STATE_RELAY_TO_ANNOUNCE,
				TRANSITION_TO_ANNOUNCE_RECEIVED);
		
		this.registerTransition(STATE_WAIT_TO_BID,
				STATE_RELAY_AUCTION_CANCELLED,
				TRANSITION_AUCTION_CANCELLED_RECEIVED);
		
		this.registerTransition(STATE_RELAY_TO_BID,
				STATE_WAIT_REP_BID,
				TRANSITION_TO_BID_RELAYED);
		
		this.registerTransition(STATE_RELAY_TO_BID,
				STATE_RELAY_AUCTION_CANCELLED,
				TRANSITION_AUCTION_CANCELLED_RECEIVED);
		
		this.registerTransition(STATE_WAIT_REP_BID,
				STATE_RELAY_REP_BID_OK,
				TRANSITION_REP_BID_OK_RECEIVED);
		
		this.registerTransition(STATE_WAIT_REP_BID,
				STATE_RELAY_REP_BID_NOK,
				TRANSITION_REP_BID_NOK_RECEIVED);
		
		this.registerTransition(STATE_WAIT_REP_BID,
				STATE_RELAY_TO_BID,
				TRANSITION_TO_BID_RECEIVED);
		
		this.registerTransition(STATE_WAIT_REP_BID,
				STATE_RELAY_AUCTION_CANCELLED,
				TRANSITION_AUCTION_CANCELLED_RECEIVED);
	}
	
	/**
	 * Maintains the provided request (considered as the last received).
	 * 
	 * @param auction the request.
	 */
	public void setRequest(ACLMessage request)
	{
		this.request = request;
	}
	
	/**
	 * Provides the last received request.
	 * 
	 * @return the last request.
	 */
	public ACLMessage getRequest()
	{
		return this.request;
	}

	/**
	 * 
	 * @return the AID of the seller agent who created the auction which is managed by this behaviour. 
	 */
	public AID getSeller()
	{
		return mySeller;
	}

	/**
	 * 
	 * @return the AID of the bidder to which the fish supply has been attributed, null if not attribution.
	 */
	public AID getSelectedBidder()
	{
		return selectedBidder;
	}

	/**
	 * 
	 * @param selectedBidder the AID of the bidder to which the fish supply has been attributed, null meaning no attribution.
	 */
	public void setSelectedBidder(AID selectedBidder)
	{
		this.selectedBidder = selectedBidder;
	}
	
	/**
	 * 
	 * @return the topic of the conversation which are managed by this FSM behaviour.
	 */
	public AID getTopic()
	{
		return this.topic;
	}
	
	/**
	 * Creates the topic of the conversation which are managed by this FSM behaviour.
	 * 
	 * @return the topic of the conversation which are managed by this FSM behaviour.
	 */
	private final AID createTopic()
	{
		TopicManagementHelper topicHelper = null;
		
		try
		{
			topicHelper =
					(TopicManagementHelper) super.myAgent.getHelper(
							TopicManagementHelper.SERVICE_NAME);
		}
		catch (ServiceException e)
		{
			AuctionManagementBehaviour.LOGGER.log(Level.SEVERE, null, e);
		}
		
		return topicHelper.createTopic(
				FishMarket.Topics.TOPIC_AUCTION_MANAGEMENT);
	}
}