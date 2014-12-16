package fr.univpau.m2ti.sma.fishmarket.behaviour.market;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders.ConfirmAuctionCreationBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders.EvaluateAuctionCreationResquestState;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders.WaitAuctionCreationRequestBehaviour;
import jade.core.behaviours.FSMBehaviour;

@SuppressWarnings("serial")
/**
 * The FSM behavior of the market agent.
 * 
 * @author Josuah Aron
 *
 */
public class SellerManagementBehaviour extends FSMBehaviour
{
	/** The state in which the agent waits for auction creation requests. */
	private static final String STATE_WAIT_AUCTION_CREATION_REQUEST =
			"STATE_WAIT_AUCTION_CREATION_REQUEST";
	
	/** The state in which the agent evaluates auction creation requests. */
	private static final String STATE_EVALUATE_AUCTION_CREATION_REQUEST =
			"STATE_EVALUATE_AUCTION_CREATION_REQUEST";
	
	/** The state in which the agent evaluates auction creation requests. */
	private static final String STATE_CONFIRM_AUCTION_CREATION_REQUEST =
			"STATE_CONFIRM_AUCTION_CREATION_REQUEST";
	
	/** Return code which activates the transition to the wait new requests after
	 * the last evaluation resulted in a reject. */
	public static final int TRANSITION_REFUSE_AUCTION_CREATION_REQUEST = 0;
	
	/** Return code which activates the transition to confirm the creation of a new auction
	 * after the last evaluation resulted in an approval. */
	public static final int TRANSITION_CONFIRM_AUCTION_CREATION = 1;
	
	/**
	 * Create the behavior of a market agent which is responsible for creating new auction and registering them.
	 * 
	 * @param myMarketAgent
	 * 			the agent to which this behavior is added.
	 */
	public SellerManagementBehaviour(MarketAgent myMarketAgent)
	{
		super(myMarketAgent);
		
		// Register states
		this.registerFirstState(new WaitAuctionCreationRequestBehaviour(myMarketAgent),
				STATE_WAIT_AUCTION_CREATION_REQUEST);
		this.registerState(new EvaluateAuctionCreationResquestState(myMarketAgent),
				STATE_EVALUATE_AUCTION_CREATION_REQUEST);
		this.registerState(new ConfirmAuctionCreationBehaviour(myMarketAgent),
				STATE_CONFIRM_AUCTION_CREATION_REQUEST);
		
		// Register transitions
		this.registerDefaultTransition(STATE_WAIT_AUCTION_CREATION_REQUEST,
				STATE_EVALUATE_AUCTION_CREATION_REQUEST);
		
		this.registerTransition(STATE_EVALUATE_AUCTION_CREATION_REQUEST,
				STATE_WAIT_AUCTION_CREATION_REQUEST,
				TRANSITION_REFUSE_AUCTION_CREATION_REQUEST);
		
		this.registerTransition(STATE_EVALUATE_AUCTION_CREATION_REQUEST,
				STATE_CONFIRM_AUCTION_CREATION_REQUEST,
				TRANSITION_CONFIRM_AUCTION_CREATION);
		
		this.registerDefaultTransition(STATE_CONFIRM_AUCTION_CREATION_REQUEST,
				STATE_WAIT_AUCTION_CREATION_REQUEST);
	}
}
