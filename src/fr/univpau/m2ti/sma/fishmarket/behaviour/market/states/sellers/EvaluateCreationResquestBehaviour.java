package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.sellers;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.SellerManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import jade.core.behaviours.OneShotBehaviour;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the marker agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class EvaluateCreationResquestBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private SellerManagementBehaviour myFSM;
	
	/** Tells whether the creation of the auction is confirmed or not. */
	private boolean isCreationAccepted;
	
	/**
	 * Creates a behaviour which is to be associated with a state of the 
	 * market agent's FSM behaviour.
	 * 
	 * @param myMarketAgent
	 * 			the market agent of which FSM behavior's state this behaviour is to be associated.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public EvaluateCreationResquestBehaviour(
			MarketAgent myMarketAgent,
			SellerManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		MarketAgent myMarketAgent = (MarketAgent)myAgent;
		
		Auction auction = this.myFSM.getAuctionCreationRequest();
		
		if(! myMarketAgent.isRegisteredAuction(auction))
		{
			myMarketAgent.registerAuction(auction);
			
			this.isCreationAccepted = true;
			
			// Do not de-register creation request: used later for confirmation message.
		}
		else
		{
			this.isCreationAccepted = false;
			
			// De-register auction creation request
			this.myFSM.setAuctionCreationRequest(null);
		}
	}
	
	@Override
	public int onEnd()
	{
		return this.isCreationAccepted ?
				SellerManagementBehaviour.TRANSITION_CONFIRM_AUCTION_REGISTRATION :
					SellerManagementBehaviour.TRANSITION_REFUSE_AUCTION_REGISTRATION_REQUEST;
	}
}