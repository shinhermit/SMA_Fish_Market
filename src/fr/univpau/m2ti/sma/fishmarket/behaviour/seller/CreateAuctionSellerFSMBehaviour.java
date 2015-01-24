package fr.univpau.m2ti.sma.fishmarket.behaviour.seller;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.CreateAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.creation.*;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
/**
 * The behaviour of the seller agent before his creates an auction.
 * 
 * <p>Conversation with the market agent in order to create an auction.</p>
 * 
 * @author Josuah Aron
 *
 */
public class CreateAuctionSellerFSMBehaviour extends FSMBehaviour
{
	/** Holds the response from the market. */
	private ACLMessage response;
	
	/** The number of request already sent. */
	private int requestCount = 0;
	
	/** Base filter for filtering messages. */
	public static final MessageTemplate MESSAGE_FILTER =
			MessageTemplate.MatchTopic(CreateAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
	
	/** The initial state, in which the seller creates an auction and request it's registration to the market agent. */
	private static final String STATE_REQUEST_CREATION =
			"STATE_REQUEST_CREATION";
	
	/** The state in which the seller waits that the market agent confirms the registration of his auction. */
	private static final String STATE_WAIT_RESPONSE =
			"STATE_WAIT_RESPONSE";
	
	/** The state in which the seller waits that at least one bidder subscribes to his auction. */
	private static final String STATE_WAIT_SUBSCRIBERS =
			"STATE_WAIT_SUBSCRIBERS";
	
	/** The state in which the seller end this behaviour and creates the behaviour to participate to his auction. */
	private static final String STATE_TERMINATE_SUCCESS =
			"STATE_TERMINATE_SUCCESS";
	
	/** The state in which the seller terminate because his auction requests have been rejected too many times. */
	private static final String STATE_TERMINATE_FAILURE =
			"STATE_TERMINATE_FAILURE";
	
	/** The state in which the seller cancels the auction because no bidder subscribed. */
	private static final String STATE_TERMINATE_CANCEL =
			"STATE_TERMINATE_CANCEL";
	
	/** The code which activates the transition to wait for an answer from the market after a registration request. */
	public static final int TRANSITION_TO_WAIT_RESPONSE;
	
	/** The code which activates the transition to terminate because auction requests have been rejected too many times. */
	public static final int TRANSITION_TO_TERMINATE_FAILURE;
	
	/** The code which activates the transition to terminate and cancel the auction because no bidder subscribed. */
	public static final int TRANSITION_TO_TERMINATE_CANCEL;
	
	/** The code which activates the transition to wait for bidders subscriptions. */
	public static final int TRANSITION_TO_WAIT_SUBSCRIBERS;
	
	/** The code which activates the transition to try another registration request. */
	public static final int TRANSITION_TO_REQUEST_CREATION;
	
	/** The code which activates the transition to terminate and actually begin the auction. */
	public static final int TRANSITION_TO_TERMINATE_SUCCESS;
	
	static
	{
		int start = -1;
		
		TRANSITION_TO_WAIT_RESPONSE = ++start;
		TRANSITION_TO_TERMINATE_FAILURE = ++start;
		TRANSITION_TO_TERMINATE_CANCEL = ++start;
		TRANSITION_TO_WAIT_SUBSCRIBERS = ++start;
		TRANSITION_TO_REQUEST_CREATION = ++start;
		TRANSITION_TO_TERMINATE_SUCCESS = ++start;
	}
	
	/**
	 * Create a behaviour which allows to interact with the market agent in order to create an auction.
	 * 
	 * @param mySellerAgent the seller agent to which the behaviour is to be added.
	 */
	public CreateAuctionSellerFSMBehaviour(
			SellerAgent mySellerAgent)
	{
		super(mySellerAgent);
		
		// Add states
		// Final state must create AuctionSellerBehaviour
		this.registerFirstState(
				new RequestCreationBehaviour(mySellerAgent, this),
				STATE_REQUEST_CREATION);
		
		this.registerState(
				new WaitResponseBehaviour(mySellerAgent, this),
				STATE_WAIT_RESPONSE);
		
		this.registerState(
				new WaitSubscribersBehaviour(mySellerAgent, this),
				STATE_WAIT_SUBSCRIBERS);
		
		this.registerLastState(
				new TerminateSuccessBehaviour(mySellerAgent, this),
				STATE_TERMINATE_SUCCESS);
		
		this.registerLastState(
				new TerminateFailureBehaviour(mySellerAgent, this),
				STATE_TERMINATE_FAILURE);
		
		this.registerLastState(
				new TerminateCancelBehaviour(mySellerAgent, this),
				STATE_TERMINATE_CANCEL);
		
		// Add transitions
		this.registerTransition(
				STATE_REQUEST_CREATION, STATE_WAIT_RESPONSE,
				TRANSITION_TO_WAIT_RESPONSE);
		
		this.registerTransition(
				STATE_REQUEST_CREATION, STATE_TERMINATE_FAILURE,
				TRANSITION_TO_TERMINATE_FAILURE);
		
		this.registerTransition(
				STATE_WAIT_RESPONSE, STATE_WAIT_RESPONSE,
				TRANSITION_TO_WAIT_RESPONSE);
		
		this.registerTransition(
				STATE_WAIT_RESPONSE, STATE_REQUEST_CREATION,
				TRANSITION_TO_REQUEST_CREATION);
		
		this.registerTransition(
				STATE_WAIT_RESPONSE, STATE_WAIT_SUBSCRIBERS,
				TRANSITION_TO_WAIT_SUBSCRIBERS);
		
		this.registerTransition(
				STATE_WAIT_SUBSCRIBERS, STATE_WAIT_SUBSCRIBERS,
				TRANSITION_TO_WAIT_SUBSCRIBERS);
		
		this.registerTransition(
				STATE_WAIT_SUBSCRIBERS, STATE_TERMINATE_CANCEL,
				TRANSITION_TO_TERMINATE_CANCEL);
		
		this.registerTransition(
				STATE_WAIT_SUBSCRIBERS, STATE_TERMINATE_SUCCESS,
				TRANSITION_TO_TERMINATE_SUCCESS);
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
