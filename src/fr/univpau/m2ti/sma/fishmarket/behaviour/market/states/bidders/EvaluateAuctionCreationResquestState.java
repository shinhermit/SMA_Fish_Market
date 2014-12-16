package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.SellerManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import jade.core.behaviours.OneShotBehaviour;

@SuppressWarnings("serial")
/**
 * A behavior which is to be associated with a state of the marker agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class EvaluateAuctionCreationResquestState extends OneShotBehaviour
{
	/** Tells whether the creation of the auction is confirmed or not. */
	private boolean isAuctionConfirmed;
	
	/**
	 * Creates a behavior which is to be associated with a state of the 
	 * market agent's FSM behavior.
	 * 
	 * @param myMarketAgent
	 * 			the market agent of which FSM behavior's state this behavior is to be associated.
	 */
	public EvaluateAuctionCreationResquestState(MarketAgent myMarketAgent)
	{
	}
	
	@Override
	public void action()
	{
		MarketAgent myMarketAgent = (MarketAgent)myAgent;
		
		Auction auction = myMarketAgent.getAuctionCreationRequest();
		
		if(! myMarketAgent.isRegisteredAuction(auction))
		{
			myMarketAgent.registerAuction(auction);
			
			this.isAuctionConfirmed = true;
		}
		else
		{
			this.isAuctionConfirmed = false;
			
			// De-register auction creation request
			myMarketAgent.registerAuctionCreationRequest(null);
		}
	}
	
	@Override
	public int onEnd()
	{
		return this.isAuctionConfirmed ?
				SellerManagementBehaviour.TRANSITION_CONFIRM_AUCTION_CREATION :
					SellerManagementBehaviour.TRANSITION_REFUSE_AUCTION_CREATION_REQUEST;
	}
}