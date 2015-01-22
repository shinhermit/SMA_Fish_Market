package fr.univpau.m2ti.sma.fishmarket.behaviour.market;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.sellers.ConfirmCreationBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.sellers.EvaluateResquestBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.sellers.TerminateSellerManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.sellers.WaitCreationRequestBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.AID;
import jade.core.behaviours.FSMBehaviour;
import jade.core.messaging.TopicUtility;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
/**
 * The FSM behaviour of the market agent which is responsible of handling seller's auction creation requests.
 * 
 * @author Josuah Aron
 *
 */
public class AuctionCreationManagementBehaviour extends FSMBehaviour
{
	/** Holds the last auction creation request. */
	private ACLMessage request;
	
	/** The topic of the messages of conversations accepted by the behaviour. */
	public static final AID MESSAGE_TOPIC = 
			TopicUtility.createTopic(
					FishMarket.Topics.TOPIC_AUCTION_CREATION);

	/** Allows filtering incoming messages. */
	public static final MessageTemplate MESSAGE_FILTER =
				MessageTemplate.MatchTopic(
							AuctionCreationManagementBehaviour.MESSAGE_TOPIC);
	
	/** The state in which the agent waits for auction creation requests. */
	private static final String STATE_WAIT_REQUEST =
			"STATE_WAIT_REQUEST";
	
	/** The state in which the agent evaluates auction creation requests. */
	private static final String STATE_EVALUATE_REQUEST =
			"STATE_EVALUATE_REQUEST";
	
	/** The state in which the agent evaluates auction creation requests. */
	private static final String STATE_ACCEPT_CREATION =
			"STATE_ACCEPT_CREATION";
	
	/** The ending state for the behaviour (user properly stopped the market). */
	private static final String STATE_TERMINATE =
			"STATE_TERMINATE";
	
	/** Return code which activates the transition to evaluate an auction registration request. */
	public static final int TRANSITION_TO_EVALUATE_REQUEST;
	
	/** Return code which activates the transition to terminate this finite state machine. */
	public static final int TRANSITION_TO_TERMINATE;
	
	/** Return code which activates the transition to the wait new requests after
	 * the last evaluation resulted in a reject. */
	public static final int TRANSITION_TO_WAIT_REQUEST;
	
	/** Return code which activates the transition to confirm the registration of a new auction
	 * after the last evaluation resulted in an approval. */
	public static final int TRANSITION_TO_CONFIRM_CREATION;
	
	static
	{
		int start = -1;
		
		TRANSITION_TO_EVALUATE_REQUEST = ++start;
		TRANSITION_TO_TERMINATE = ++start;
		TRANSITION_TO_WAIT_REQUEST = ++start;
		TRANSITION_TO_CONFIRM_CREATION = ++start;
	}
	
	/**
	 * Create the behaviour of a market agent which is responsible for creating new auction and registering them.
	 * 
	 * @param myMarketAgent
	 * 			the agent to which this behaviour is added.
	 */
	public AuctionCreationManagementBehaviour(MarketAgent myMarketAgent)
	{
		super(myMarketAgent);
		
		// Register states
		// The last state must call myMarketAgent.setIsDone(true);	
		this.registerFirstState(new WaitCreationRequestBehaviour(myMarketAgent, this),
				STATE_WAIT_REQUEST);
		
		this.registerState(new EvaluateResquestBehaviour(myMarketAgent, this),
				STATE_EVALUATE_REQUEST);
		
		this.registerState(new ConfirmCreationBehaviour(myMarketAgent, this),
				STATE_ACCEPT_CREATION);
		
		this.registerLastState(new TerminateSellerManagementBehaviour(myMarketAgent),
				STATE_TERMINATE);
		
		// Register transitions
		this.registerTransition(STATE_WAIT_REQUEST,
				STATE_EVALUATE_REQUEST,
				TRANSITION_TO_EVALUATE_REQUEST);
		
		this.registerTransition(STATE_WAIT_REQUEST,
				STATE_WAIT_REQUEST,
				TRANSITION_TO_WAIT_REQUEST);
		
		this.registerTransition(STATE_WAIT_REQUEST,
				STATE_TERMINATE,
				TRANSITION_TO_TERMINATE);
		
		this.registerTransition(STATE_EVALUATE_REQUEST,
				STATE_WAIT_REQUEST,
				TRANSITION_TO_WAIT_REQUEST);
		
		this.registerTransition(STATE_EVALUATE_REQUEST,
				STATE_ACCEPT_CREATION,
				TRANSITION_TO_CONFIRM_CREATION);
		
		this.registerDefaultTransition(STATE_ACCEPT_CREATION,
				STATE_WAIT_REQUEST);
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
}
