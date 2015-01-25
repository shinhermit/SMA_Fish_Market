package fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.market.states.EvaluateSubscriptionRequestMarketBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.market.states.NotifySellerMarketBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.market.states.ProvideAuctionListMarketBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.market.states.TerminateSubscriptionsMarketBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.market.states.WaitBidderRequestMarketBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.AID;
import jade.core.behaviours.FSMBehaviour;
import jade.core.messaging.TopicUtility;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
/**
 * The FSM behaviour of the market agent which is responsible of handling bidder's request of the available auction or to subscribe to an auction.
 * 
 * @author Josuah Aron
 *
 */
public class SubscribeToAuctionMarketFSMBehaviour extends FSMBehaviour
{
	/** Subscription management: the bidder agent who requested a subscription to an auction. */
	private ACLMessage request;
	
	/** The topic of the messages of conversations accepted by the behaviour. */
	public static final AID MESSAGE_TOPIC =
			TopicUtility.createTopic(
					FishMarket.Topics.TOPIC_BIDDERS_SUBSCRIPTION);

	/** Allows filtering incoming messages. */
	public static final MessageTemplate MESSAGE_FILTER =
				MessageTemplate.MatchTopic(
						SubscribeToAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
	
	/** The state in which the agent waits for bidders messages requests. */
	private static final String STATE_WAIT_BIDDER_REQUEST =
			"STATE_WAIT_BIDDER_REQUEST";
	
	/** The state in which the agent replies to a request for the list of available auction. */
	private static final String STATE_PROVIDE_AUCTION_LIST =
			"STATE_PRIVIDE_AUCTION_LIST";
	
	/** The state in which the agent evaluates a request for a subscription to an auction. */
	private static final String STATE_EVALUATE_SUBSCRIPTION_REQUEST =
			"STATE_EVALUATE_SUBSCRIPTION_REQUEST";
	
	/** The state in which the agent confirms the subscription of a bidder to an auction. */
	private static final String STATE_NOTIFY_SELLER =
			"STATE_NOTIFY_SELLER";
	
	/** The terminating state of the bidder management behaviour. */
	private static final String STATE_TERMINATE_BIDDER_MANAGEMENT =
			"STATE_TERMINATE_BIDDER_MANAGEMENT";
	
	/** Return code which activates the transition to reply to an available auction list request. */
	public static final int TRANSITION_TO_PROVIDE_AUCTION_LIST;
	
	/** Return code which activates the transition to evaluate a request for a subscription to an auction. */
	public static final int TRANSITION_TO_VALUATE_REQUEST;
	
	/** Return code which activates the transition to terminate this FSM. */
	public static final int TRANSITION_TO_TERMINATE;
	
	/** Return code which activates the transition to reject a request for a subscription to an auction. */
	public static final int TRANSITION_TO_WAIT_REQUEST;
	
	/** Return code which activates the transition to register the subscription of a bidder to an auction. */
	public static final int TRANSITION_TO_NOTIFY_SELLER;
	
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
		TRANSITION_TO_PROVIDE_AUCTION_LIST = ++start;
		TRANSITION_TO_VALUATE_REQUEST = ++start;
		TRANSITION_TO_TERMINATE = ++start;
		TRANSITION_TO_WAIT_REQUEST = ++start;
		TRANSITION_TO_NOTIFY_SELLER = ++start;
		
		start = -1;
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
	public SubscribeToAuctionMarketFSMBehaviour(MarketAgent myMarketAgent)
	{
		super(myMarketAgent);
		
		// Register states
		// The last state should call myMarketAgent.setIsDone(true);	
		this.registerFirstState(new WaitBidderRequestMarketBehaviour(myMarketAgent, this),
				STATE_WAIT_BIDDER_REQUEST);
		
		this.registerState(new ProvideAuctionListMarketBehaviour(myMarketAgent, this),
				STATE_PROVIDE_AUCTION_LIST);
		
		this.registerState(new EvaluateSubscriptionRequestMarketBehaviour(myMarketAgent, this),
				STATE_EVALUATE_SUBSCRIPTION_REQUEST);
		
		this.registerState(new NotifySellerMarketBehaviour(myMarketAgent, this),
				STATE_NOTIFY_SELLER);
		
		this.registerLastState(new TerminateSubscriptionsMarketBehaviour(myMarketAgent),
				STATE_TERMINATE_BIDDER_MANAGEMENT);
		
		// Register transitions
		this.registerTransition(STATE_WAIT_BIDDER_REQUEST,
				STATE_PROVIDE_AUCTION_LIST,
				TRANSITION_TO_PROVIDE_AUCTION_LIST);
		
		this.registerTransition(STATE_WAIT_BIDDER_REQUEST,
				STATE_EVALUATE_SUBSCRIPTION_REQUEST,
				TRANSITION_TO_VALUATE_REQUEST);
		
		this.registerTransition(STATE_WAIT_BIDDER_REQUEST,
				STATE_WAIT_BIDDER_REQUEST,
				TRANSITION_TO_WAIT_REQUEST);
		
		this.registerTransition(STATE_WAIT_BIDDER_REQUEST,
				STATE_TERMINATE_BIDDER_MANAGEMENT,
				TRANSITION_TO_TERMINATE);
		
		this.registerDefaultTransition(STATE_PROVIDE_AUCTION_LIST,
				STATE_WAIT_BIDDER_REQUEST);
		
		this.registerTransition(STATE_EVALUATE_SUBSCRIPTION_REQUEST,
				STATE_WAIT_BIDDER_REQUEST,
				TRANSITION_TO_WAIT_REQUEST);
		
		this.registerTransition(STATE_EVALUATE_SUBSCRIPTION_REQUEST,
				STATE_NOTIFY_SELLER,
				TRANSITION_TO_NOTIFY_SELLER);
		
		this.registerDefaultTransition(STATE_NOTIFY_SELLER,
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
