package fr.univpau.m2ti.sma.fishmarket.behaviour.market;

import java.util.HashSet;
import java.util.Set;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.auctions.*;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.AID;
import jade.core.behaviours.FSMBehaviour;
import jade.core.messaging.TopicUtility;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

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
	
	/** The ID of the conversation associated with the auction that this behaviour manages. */
	private final String myAuctionId;
	
	/** Allows filtering incoming messages. */
	private final MessageTemplate messageFilter;
	
	/** The first bidder for the current price. */
	private AID firstBidder = null;
	
	/** The list of bidders for the current price. */
	private Set<AID> bidders = new HashSet<AID>();
	
	/** Holds the last auction creation request. */
	private ACLMessage request;

	/** The topic of the conversations managed by this behaviour. */
	public static final AID MESSAGE_TOPIC = TopicUtility.createTopic(
			FishMarket.Topics.TOPIC_RUNNING_AUCTION);
	
	/** The amount of time to wait for incoming messages before relaying a <i>to_bid</i>.*/
	public static final long BID_WAIT_DELAY = 10000; // 10 sec
	
	/** The amount of time to wait for incoming messages before relaying a <i>to_bid</i>.*/
	public static final long TO_CANCEL_WAIT_DELAY = 500; // 0.5 sec
	
	/** The state in which the agent waits for a first announcement from the seller. */
	private static final String STATE_WAIT_TO_ANNOUNCE =
			"STATE_WAIT_TO_ANNOUNCE";
	
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
			"STATE_RELAY_REP_BID_OK";
	
	/** The state in which the agent relays the negative response to the bid from the seller to non-chosen bidders. */
	private static final String STATE_RELAY_REP_BID_NOK =
			"STATE_RELAY_REP_BID_NOK";
	
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
	private static final String STATE_TERMINATE_CANCEL =
			"STATE_TERMINATE_CANCEL";
	
	/** Return code to activates the appropriate transitions to keep waiting for incoming <code>to_announce</code>. */
	public static final int TRANSITION_TO_WAIT_TO_ANNOUNCE;
	
	/** Return code to activates the appropriate transitions when <code>to_announce</code> is received. */
	public static final int TRANSITION_TO_RELAY_ANNOUNCE;
	
	/** Return code to activates the appropriate transitions when <code>to_bid</code> is received. */
	public static final int TRANSITION_TO_RELAY_BID;
	
	/** Return code to activates the appropriate transitions when <code>auction_cancelled</code> is received. */
	public static final int TRANSITION_TO_CANCEL;
	
	/** Return code to activates the appropriate transitions when <code>auction_cancelled</code> is received. */
	public static final int TRANSITION_TO_WAIT_REP_BID;
	
	/** Return code to activates the appropriate transitions when <code>rep_bid_ok</code> is received. */
	public static final int TRANSITION_TO_RELAY_REP_BID_OK;
	
	/** Return code to activates the appropriate transitions when <code>rep_bid_nok</code> is received. */
	public static final int TRANSITION_TO_RELAY_REP_BID_NOK;
	
	static
	{
		int start = -1;
		
		TRANSITION_TO_WAIT_TO_ANNOUNCE = ++start;
		TRANSITION_TO_RELAY_ANNOUNCE = ++start;
		TRANSITION_TO_RELAY_BID = ++start;
		TRANSITION_TO_CANCEL = ++start;
		TRANSITION_TO_WAIT_REP_BID = ++start;
		TRANSITION_TO_RELAY_REP_BID_OK = ++start;
		TRANSITION_TO_RELAY_REP_BID_NOK = ++start;
	}
	
	/**
	 * Create the behaviour of a market agent which is responsible  for enforcing the auction protocol.
	 * 
	 * @param myMarketAgent
	 * 			the agent to which this behaviour is added.
	 */
	public AuctionManagementBehaviour(
			MarketAgent myMarketAgent,
			AID mySeller,
			String myAuctionId)
	{
		super(myMarketAgent);
		
		this.mySeller = mySeller;
		
		this.myAuctionId = myAuctionId;
		
		this.messageFilter = this.createMessageFilter();
		
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
		
		this.registerState(new RelayToCancelBehaviour(myMarketAgent, this),
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
		
		this.registerLastState(new TerminateCancelBehaviour(myMarketAgent, this),
				STATE_TERMINATE_CANCEL);
		
		// Register transitions
		this.registerTransition(STATE_WAIT_TO_ANNOUNCE,
				STATE_WAIT_TO_ANNOUNCE,
				TRANSITION_TO_WAIT_TO_ANNOUNCE);
		
		this.registerTransition(STATE_WAIT_TO_ANNOUNCE,
				STATE_RELAY_TO_ANNOUNCE,
				TRANSITION_TO_RELAY_ANNOUNCE);
		
		this.registerTransition(STATE_WAIT_TO_ANNOUNCE,
				STATE_TERMINATE_CANCEL,
				TRANSITION_TO_CANCEL);
		
		this.registerDefaultTransition(STATE_RELAY_TO_ANNOUNCE,
				STATE_WAIT_TO_BID);
		
		this.registerDefaultTransition(STATE_RELAY_REP_BID_NOK,
				STATE_WAIT_TO_ANNOUNCE);
		
		this.registerDefaultTransition(STATE_RELAY_AUCTION_CANCELLED,
				STATE_TERMINATE_CANCEL);
		
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
				TRANSITION_TO_RELAY_BID);
		
		this.registerTransition(STATE_WAIT_TO_BID,
				STATE_RELAY_TO_ANNOUNCE,
				TRANSITION_TO_RELAY_ANNOUNCE);
		
		this.registerTransition(STATE_WAIT_TO_BID,
				STATE_RELAY_AUCTION_CANCELLED,
				TRANSITION_TO_CANCEL);
		
		this.registerTransition(STATE_RELAY_TO_BID,
				STATE_WAIT_REP_BID,
				TRANSITION_TO_WAIT_REP_BID);
		
		this.registerTransition(STATE_RELAY_TO_BID,
				STATE_RELAY_AUCTION_CANCELLED,
				TRANSITION_TO_CANCEL);
		
		this.registerTransition(STATE_WAIT_REP_BID,
				STATE_RELAY_REP_BID_OK,
				TRANSITION_TO_RELAY_REP_BID_OK);
		
		this.registerTransition(STATE_WAIT_REP_BID,
				STATE_RELAY_REP_BID_NOK,
				TRANSITION_TO_RELAY_REP_BID_NOK);
		
		this.registerTransition(STATE_WAIT_REP_BID,
				STATE_RELAY_TO_BID,
				TRANSITION_TO_RELAY_BID);
		
		this.registerTransition(STATE_WAIT_REP_BID,
				STATE_RELAY_AUCTION_CANCELLED,
				TRANSITION_TO_CANCEL);
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
		return this.firstBidder;
	}

	/**
	 * 
	 * @param selectedBidder the AID of the bidder to which the fish supply has been attributed, null meaning no attribution.
	 */
	public void setFirstBidder(AID bidder)
	{
		this.firstBidder = bidder;
	}

	/**
	 * 
	 * @return the <code>AID</code> of the first bidder for the current price if there has been, <code>null</code> otherwise.
	 */
	public AID getFirstBidder()
	{
		return firstBidder;
	}
	
	/**
	 * 
	 * @param bidder the <code>AID</code> of a bidder agent.
	 */
	public void addBidder(AID bidder)
	{
		if(this.bidders.isEmpty())
		{
			this.firstBidder = bidder;
		}
		
		this.bidders.add(bidder);
	}
	
	/**
	 * Remove all bidders.
	 */
	public void clearBidderList()
	{
		this.bidders.clear();
		
		this.firstBidder = null;
	}

	/**
	 * 
	 * @return the ID of the conversation associated with the auction that this behaviour manages.
	 */
	public String getAuctionId()
	{
		return myAuctionId;
	}
	
	/**
	 * Creates a unique conversation id based on the AID of the seller agent which initiated and auction.
	 * 
	 * @param sellerAID AID of the seller agent which initiated and auction.
	 * 
	 * @return a conversation if for the auction.
	 */
	public static String createAuctionId(AID sellerAID)
	{
		return sellerAID.getName().toString();
	}
	
	/**
	 * 
	 * @return base message filter for conversations for the auction managed by this behaviour.
	 */
	public MessageTemplate getMessageFilter() 
	{
		return messageFilter;
	}

	/**
	 * Creates a filter for incoming message.
	 * 
	 * @return the filter for incoming messages.
	 */
	private MessageTemplate createMessageFilter()
	{
		return MessageTemplate.and(
						MessageTemplate.MatchTopic(
								AuctionManagementBehaviour.MESSAGE_TOPIC),
						MessageTemplate.MatchConversationId(
										this.myAuctionId));
	}
}