package fr.univpau.m2ti.sma.fishmarket.behaviour.market;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders.RespondAuctionCreationResquestState;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders.WaitAuctionCreationRequestBehaviour;
import jade.core.behaviours.FSMBehaviour;

@SuppressWarnings("serial")
public class SellerManagementBehaviour extends FSMBehaviour
{
	private static final String STATE_WAIT_AUCTION_CREATION_REQUEST =
			"STATE_WAIT_AUCTION_CREATION_REQUEST";
	private static final String STATE_RESPOND_AUCTION_CREATION_REQUEST =
			"STATE_RESPOND_AUCTION_CREATION_REQUEST";
	
//	private static final int TRANSITION_REFUSE_AUCTION_CREATION_REQUEST = 0;
//	private static final int TRANSITION_REFUSE_AUCTION_CREATION_REQUEST = 0;
	
	public SellerManagementBehaviour(MarketAgent myMarketAgent)
	{
		super(myMarketAgent);
		
		// Register states
		this.registerFirstState(new WaitAuctionCreationRequestBehaviour(),
				STATE_WAIT_AUCTION_CREATION_REQUEST);
		this.registerState(new RespondAuctionCreationResquestState(),
				STATE_RESPOND_AUCTION_CREATION_REQUEST);
		
		// Register transitions
		this.registerDefaultTransition(STATE_WAIT_AUCTION_CREATION_REQUEST,
				STATE_RESPOND_AUCTION_CREATION_REQUEST);
		
//		this.registerDefaultTransition(STATE_WAIT_AUCTION_CREATION_REQUEST,
//				STATE_RESPOND_AUCTION_CREATION_REQUEST);
//		this.registerDefaultTransition(STATE_WAIT_AUCTION_CREATION_REQUEST,
//				STATE_RESPOND_AUCTION_CREATION_REQUEST);
	}
}
