package fr.univpau.m2ti.sma.fishmarket.behaviour.seller;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.*;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
/**
 * The behaviour of the seller agent before his creates an auction.
 * 
 * <p>Conversation with the market agent in order to create an auction.</p>
 * 
 * @author Josuah Aron
 *
 */
public class CreateAuctionBehaviour extends FSMBehaviour
{
	/** Holds the response from the market. */
	private ACLMessage response;
	
	/** The number of request already sent. */
	private int requestCount = 0;
	
	/** The initial state, in which the seller creates an auction and request it's registration to the market agent. */
	private static final String STATE_REQUEST_REGISTRATION =
			"STATE_REQUEST_REGISTRATION";
	
	/** The state in which the seller waits that the market agent confirms the registration of his auction. */
	private static final String STATE_WAIT_RESPONSE =
			"STATE_WAIT_RESPONSE";
	
	/** The state in which the seller waits that at least one bidder subscribes to his auction. */
	private static final String STATE_WAIT_BIDDER =
			"STATE_WAIT_BIDDERS";
	
	/** The state in which the seller can wait for more bidder subscription. */
	private static final String STATE_WAIT_MORE_BIDDERS =
			"STATE_WAIT_MORE_BIDDERS";
	
	/** The state in which the seller end this behaviour and creates the behaviour to participate to his auction. */
	private static final String STATE_TERMINATE_SUCCESS =
			"STATE_TERMINATE_SUCCESS";
	
	/** The state in which the seller end this behaviour and creates the behaviour to participate to his auction. */
	private static final String STATE_TERMINATE_FAILURE =
			"STATE_TERMINATE_FAILURE";
	
	/** The code which activates the transition to wait for an answer from the market after a registration request. */
	public static final int TRANSITION_REQUEST_SENT;
	
	/** The code which activates the transition to terminate without any completing the whole registration process. */
	public static final int TRANSITION_TERMINATE_FAILURE;
	
	/** The code which activates the transition to wait for bidders subscriptions. */
	public static final int TRANSITION_REGISTRATION_CONFIRMED;
	
	/** The code which activates the transition to try another registration request. */
	public static final int TRANSITION_REGISTRATION_REFUSED;
	
	/** The code which activates the transition to wait for more bidder subscriptions. */
	public static final int TRANSITION_WAIT_MORE_BIDDERS;
	
	static
	{
		int start = -1;
		
		TRANSITION_REQUEST_SENT = ++start;
		TRANSITION_TERMINATE_FAILURE = ++start;
		TRANSITION_REGISTRATION_CONFIRMED = ++start;
		TRANSITION_REGISTRATION_REFUSED = ++start;
		TRANSITION_WAIT_MORE_BIDDERS = ++start;
	}
	
	/**
	 * Create a behaviour which allows to interact with the market agent in order to create an auction.
	 * 
	 * @param mySellerAgent the seller agent to which the behaviour is to be added.
	 */
	public CreateAuctionBehaviour(
			SellerAgent mySellerAgent)
	{
		super(mySellerAgent);
		
		// Add states
		// Final state must create AuctionSellerBehaviour
		this.registerFirstState(
				new RequestRegistrationBehaviour(mySellerAgent, this),
				STATE_REQUEST_REGISTRATION);
		
		this.registerState(
				new WaitResponseBehaviour(mySellerAgent, this),
				STATE_WAIT_RESPONSE);
		
		this.registerState(
				new WaitBidderBehaviour(mySellerAgent),
				STATE_WAIT_BIDDER);
		
		this.registerState(
				new WaitMoreBiddersBehaviour(mySellerAgent),
				STATE_WAIT_MORE_BIDDERS);
		
		this.registerLastState(
				new TerminateSuccessBehaviour(mySellerAgent, this),
				STATE_TERMINATE_SUCCESS);
		
		this.registerLastState(
				new TerminateFailureBehaviour(mySellerAgent, this),
				STATE_TERMINATE_FAILURE);
		
		// Add transitions
		this.registerTransition(STATE_REQUEST_REGISTRATION, STATE_WAIT_RESPONSE,
				TRANSITION_REQUEST_SENT);
		
		this.registerTransition(STATE_REQUEST_REGISTRATION, STATE_TERMINATE_FAILURE,
				TRANSITION_TERMINATE_FAILURE);
		
		this.registerTransition(STATE_WAIT_RESPONSE, STATE_REQUEST_REGISTRATION,
				TRANSITION_REGISTRATION_REFUSED);
		
		this.registerTransition(STATE_WAIT_RESPONSE, STATE_WAIT_BIDDER,
				TRANSITION_REGISTRATION_CONFIRMED);
		
		this.registerDefaultTransition(
				STATE_WAIT_BIDDER, STATE_WAIT_MORE_BIDDERS);
		
		this.registerDefaultTransition(STATE_WAIT_MORE_BIDDERS, STATE_TERMINATE_FAILURE);
	}

	/**
	 * 
	 * @return the response from the market
	 */
	public ACLMessage getResponse()
	{
		return response;
	}

	/**
	 * 
	 * @param response the response from the market
	 */
	public void setResponse(ACLMessage response)
	{
		this.response = response;
	}

	/**
	 * 
	 * @return the number of request sent until now.
	 */
	public int getRequestCount()
	{
		return requestCount;
	}

	/**
	 * 
	 * @param count the number of request sent until now.
	 */
	public void setRequestCount(int count)
	{
		this.requestCount = count;
	}
	
	/**
	 * Notifies that a new registration request has been sent.
	 * 
	 * @return the update number of sent requests.
	 */
	public int notifyNewRequest()
	{
		return ++this.requestCount;
	}
}
