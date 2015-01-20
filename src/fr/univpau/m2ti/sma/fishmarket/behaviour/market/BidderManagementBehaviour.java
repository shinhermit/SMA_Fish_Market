package fr.univpau.m2ti.sma.fishmarket.behaviour.market;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders.ConfirmSubscriptionBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders.EvaluateSubscriptionRequestBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders.ReplyAuctionListBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders.TerminateBidderManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders.WaitBidderRequestBehaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
/**
 * The FSM behaviour of the market agent which is responsible of handling bidder's request of the available auction or to subscribe to an auction.
 * 
 * @author Josuah Aron
 *
 */
public class BidderManagementBehaviour extends FSMBehaviour
{
	/** Subscription management: the bidder agent who requested a subscription to an auction. */
	private ACLMessage request;
	
	/** The state in which the agent waits for bidders messages requests. */
	private static final String STATE_WAIT_BIDDER_REQUEST =
			"STATE_WAIT_BIDDER_REQUEST";
	
	/** The state in which the agent replies to a request for the list of available auction. */
	private static final String STATE_REPLY_AUCTION_LIST =
			"STATE_REPLY_AUCTION_LIST";
	
	/** The state in which the agent evaluates a request for a subscription to an auction. */
	private static final String STATE_EVALUATE_SUBSCRIPTION_REQUEST =
			"STATE_EVALUATE_SUBSCRIPTION_REQUEST";
	
	/** The state in which the agent confirms the subscription of a bidder to an auction. */
	private static final String STATE_CONFIRM_SUBSCRIPTION =
			"STATE_CONFIRM_SUBSCRIPTION";
	
	/** The terminating state of the bidder management behaviour. */
	private static final String STATE_TERMINATE_BIDDER_MANAGEMENT =
			"STATE_TERMINATE_BIDDER_MANAGEMENT";
	
	/** Return code which activates the transition to reply to an available auction list request. */
	public static final int TRANSITION_AUCTION_LIST_REQUEST_RECEIVED;
	
	/** Return code which activates the transition to evaluate a request for a subscription to an auction. */
	public static final int TRANSITION_BIDDER_SUBSCRIPTION_REQUEST_RECEIVED;
	
	/** Return code which activates the transition to terminate this FSM. */
	public static final int TRANSITION_USER_TERMINATE;
	
	/** Return code which activates the transition to reject a request for a subscription to an auction. */
	public static final int TRANSITION_REFUSE_SUBSCRIPTION;
	
	/** Return code which activates the transition to register the subscription of a bidder to an auction. */
	public static final int TRANSITION_CONFIRM_SUBSCRIPTION;
	
	/** Status code for a subscription request has not been refused. */
	public static final int STATUS_REFUSE_NOT_REFUSED;
	
	/** Status code for a subscription request which is refused because the auction is over. */
	public static final int STATUS_REFUSE_AUCTION_OVER;
	
	/** Status code for a subscription request which is refused because the auction has been cancelled. */
	public static final int STATUS_REFUSE_AUCTION_CANCELLED;
	
	/** Status code for a subscription request which is refused because the auction could not be found. */
	public static final int STATUS_REFUSE_AUCTION_NOT_FOUND;
	
	/** Status code for a subscription request which is refused because the bidder is already registered for the auction. */
	public static final int STATUS_REFUSE_ALREADY_REGISTERED;
	
	/** Status code for a subscription request which is refused because the AID of seller could not be retrieved in the request. */
	public static final int STATUS_REFUSE_SELLER_AID_NOT_UNDERSTOOD;
	
	static
	{
		int start = -1;
		TRANSITION_AUCTION_LIST_REQUEST_RECEIVED = ++start;
		TRANSITION_BIDDER_SUBSCRIPTION_REQUEST_RECEIVED = ++start;
		TRANSITION_USER_TERMINATE = ++start;
		TRANSITION_REFUSE_SUBSCRIPTION = ++start;
		TRANSITION_CONFIRM_SUBSCRIPTION = ++start;
		
		start = -1;
		STATUS_REFUSE_NOT_REFUSED = ++start;
		STATUS_REFUSE_AUCTION_OVER = ++start;
		STATUS_REFUSE_AUCTION_CANCELLED = ++start;
		STATUS_REFUSE_AUCTION_NOT_FOUND = ++start;
		STATUS_REFUSE_ALREADY_REGISTERED = ++start;
		STATUS_REFUSE_SELLER_AID_NOT_UNDERSTOOD = ++start;
	}
	
	/**
	 * Create the behaviour of a market agent which is responsible for handling bidder request when they are not yet in an auction.
	 * 
	 * @param myMarketAgent
	 * 			the agent to which this behaviour is added.
	 */
	public BidderManagementBehaviour(MarketAgent myMarketAgent)
	{
		super(myMarketAgent);
		
		// Register states
		this.registerFirstState(new WaitBidderRequestBehaviour(myMarketAgent, this),
				STATE_WAIT_BIDDER_REQUEST);
		
		this.registerState(new ReplyAuctionListBehaviour(myMarketAgent, this),
				STATE_REPLY_AUCTION_LIST);
		
		this.registerState(new EvaluateSubscriptionRequestBehaviour(myMarketAgent, this),
				STATE_EVALUATE_SUBSCRIPTION_REQUEST);
		
		this.registerState(new ConfirmSubscriptionBehaviour(myMarketAgent, this),
				STATE_CONFIRM_SUBSCRIPTION);
		
		this.registerLastState(new TerminateBidderManagementBehaviour(myMarketAgent),
				STATE_TERMINATE_BIDDER_MANAGEMENT);
		
		// Register transitions
		this.registerTransition(STATE_WAIT_BIDDER_REQUEST,
				STATE_REPLY_AUCTION_LIST,
				TRANSITION_AUCTION_LIST_REQUEST_RECEIVED);
		
		this.registerTransition(STATE_WAIT_BIDDER_REQUEST,
				STATE_EVALUATE_SUBSCRIPTION_REQUEST,
				TRANSITION_BIDDER_SUBSCRIPTION_REQUEST_RECEIVED);
		
		this.registerTransition(STATE_WAIT_BIDDER_REQUEST,
				STATE_TERMINATE_BIDDER_MANAGEMENT,
				TRANSITION_USER_TERMINATE);
		
		this.registerDefaultTransition(STATE_REPLY_AUCTION_LIST,
				STATE_WAIT_BIDDER_REQUEST);
		
		this.registerTransition(STATE_EVALUATE_SUBSCRIPTION_REQUEST,
				STATE_WAIT_BIDDER_REQUEST,
				TRANSITION_REFUSE_SUBSCRIPTION);
		
		this.registerTransition(STATE_EVALUATE_SUBSCRIPTION_REQUEST,
				STATE_CONFIRM_SUBSCRIPTION,
				TRANSITION_CONFIRM_SUBSCRIPTION);
		
		this.registerDefaultTransition(STATE_CONFIRM_SUBSCRIPTION,
				STATE_WAIT_BIDDER_REQUEST);
	}

	/**
	 * 
	 * @return the last received request (either auction list or subscription request).
	 */
	public ACLMessage getRequest()
	{
		return this.request;
	}

	/**
	 * 
	 * @param requestSender last received request (either auction list or subscription request).
	 */
	public void setRequest(ACLMessage request)
	{
		this.request = request;
	}
}
