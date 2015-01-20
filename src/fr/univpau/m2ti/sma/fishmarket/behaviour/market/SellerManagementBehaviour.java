package fr.univpau.m2ti.sma.fishmarket.behaviour.market;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.sellers.ConfirmRegistrationBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.sellers.EvaluateRegistrationResquestBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.sellers.TerminateSellerManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.sellers.WaitRegistrationRequestBehaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
/**
 * The FSM behaviour of the market agent which is responsible of handling seller's auction creation requests.
 * 
 * @author Josuah Aron
 *
 */
public class SellerManagementBehaviour extends FSMBehaviour
{
	/** Holds the last auction creation request. */
	private ACLMessage request;
	
	/** The state in which the agent waits for auction creation requests. */
	private static final String STATE_WAIT_AUCTION_REGISTRATION_REQUEST =
			"STATE_WAIT_AUCTION_CREATION_REQUEST";
	
	/** The state in which the agent evaluates auction creation requests. */
	private static final String STATE_EVALUATE_AUCTION_REGISTRATION_REQUEST =
			"STATE_EVALUATE_AUCTION_CREATION_REQUEST";
	
	/** The state in which the agent evaluates auction creation requests. */
	private static final String STATE_CONFIRM_AUCTION_REGISTRATION_REQUEST =
			"STATE_CONFIRM_AUCTION_CREATION_REQUEST";
	
	/** The ending state for the behaviour (user properly stopped the market). */
	private static final String STATE_TERMINATE =
			"STATE_TERMINATE";
	
	/** Return code which activates the transition to evaluate an auction registration request. */
	public static final int TRANSITION_AUCTION_REQUEST_RECEIVED = 0;
	
	/** Return code which activates the transition to terminate this finite state machine. */
	public static final int TRANSITION_USER_TERMINATE = 1;
	
	/** Return code which activates the transition to the wait new requests after
	 * the last evaluation resulted in a reject. */
	public static final int TRANSITION_REFUSE_AUCTION_REGISTRATION_REQUEST = 2;
	
	/** Return code which activates the transition to confirm the registration of a new auction
	 * after the last evaluation resulted in an approval. */
	public static final int TRANSITION_CONFIRM_AUCTION_REGISTRATION = 3;
	
	/**
	 * Create the behaviour of a market agent which is responsible for creating new auction and registering them.
	 * 
	 * @param myMarketAgent
	 * 			the agent to which this behaviour is added.
	 */
	public SellerManagementBehaviour(MarketAgent myMarketAgent)
	{
		super(myMarketAgent);
		
		// Register states
		// TODO: The last state must call myMarketAgent.setIsDone(true);	
		this.registerFirstState(new WaitRegistrationRequestBehaviour(myMarketAgent, this),
				STATE_WAIT_AUCTION_REGISTRATION_REQUEST);
		
		this.registerState(new EvaluateRegistrationResquestBehaviour(myMarketAgent, this),
				STATE_EVALUATE_AUCTION_REGISTRATION_REQUEST);
		
		this.registerState(new ConfirmRegistrationBehaviour(myMarketAgent, this),
				STATE_CONFIRM_AUCTION_REGISTRATION_REQUEST);
		
		this.registerLastState(new TerminateSellerManagementBehaviour(myMarketAgent),
				STATE_TERMINATE);
		
		// Register transitions
		this.registerTransition(STATE_WAIT_AUCTION_REGISTRATION_REQUEST,
				STATE_EVALUATE_AUCTION_REGISTRATION_REQUEST,
				TRANSITION_AUCTION_REQUEST_RECEIVED);
		
		this.registerTransition(STATE_WAIT_AUCTION_REGISTRATION_REQUEST,
				STATE_TERMINATE,
				TRANSITION_USER_TERMINATE);
		
		this.registerTransition(STATE_EVALUATE_AUCTION_REGISTRATION_REQUEST,
				STATE_WAIT_AUCTION_REGISTRATION_REQUEST,
				TRANSITION_REFUSE_AUCTION_REGISTRATION_REQUEST);
		
		this.registerTransition(STATE_EVALUATE_AUCTION_REGISTRATION_REQUEST,
				STATE_CONFIRM_AUCTION_REGISTRATION_REQUEST,
				TRANSITION_CONFIRM_AUCTION_REGISTRATION);
		
		this.registerDefaultTransition(STATE_CONFIRM_AUCTION_REGISTRATION_REQUEST,
				STATE_WAIT_AUCTION_REGISTRATION_REQUEST);
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
